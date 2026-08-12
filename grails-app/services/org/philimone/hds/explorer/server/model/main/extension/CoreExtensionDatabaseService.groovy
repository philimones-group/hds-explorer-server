package org.philimone.hds.explorer.server.model.main.extension

import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import net.betainteractive.utilities.StringUtil
import org.hibernate.Session
import org.hibernate.jdbc.Work
import org.philimone.hds.explorer.server.model.enums.CoreForm
import org.philimone.hds.explorer.server.model.enums.extensions.DatabaseColumnType
import org.philimone.hds.explorer.server.model.enums.extensions.FormColumnType
import org.philimone.hds.explorer.server.model.json.JActionResult
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.CoreFormExtensionModel
import org.philimone.hds.forms.model.Column
import org.philimone.hds.forms.model.ColumnGroup
import org.philimone.hds.forms.model.ColumnRepeatGroup
import org.philimone.hds.forms.model.enums.ColumnType
import org.philimone.hds.forms.model.enums.RepeatCountType
import org.philimone.hds.forms.model.parsers.ExcelFormParser

import java.sql.Connection
import java.sql.SQLException

@Transactional
class CoreExtensionDatabaseService {

    def sessionFactory
    def generalUtilitiesService
    def errorMessageService

    static final PREGNANCY_CHILD_EXT_TABLE = "pregnancy_child_ext"
    static final PREGNANCY_VISIT_CHILD_EXT_TABLE = "pregnancy_visit_child_ext"

    def generateDatabaseModel(CoreFormExtension coreFormExtension) {
        if (coreFormExtension.extFormPath != null) {
            def inputStream = new FileInputStream(new File(coreFormExtension.extFormPath))
            def hForm = new ExcelFormParser(inputStream).getForm()

            if (hForm != null) {
                def columnIndex = 0
                def tableName = coreFormExtension.extFormId //main table - already exists

                hForm.columns.each { columnGroup ->
                    columnIndex = processColumnGroup(coreFormExtension, tableName, columnGroup, columnIndex, null)
                }
            }
        }
    }

    def int processColumnGroup(CoreFormExtension coreFormExtension, String tableName, ColumnGroup columnGroup, int columnIndex, CoreFormExtensionModel parentRepeatModel) {

        if (columnGroup instanceof ColumnRepeatGroup) {
            def repeatGroup = (ColumnRepeatGroup) columnGroup
            def repeatName = repeatGroup.getName()
            def dbRepeatName = StringUtil.toSnakeCase(repeatName)
            def nextTableName = "${tableName}_${dbRepeatName}"

            // Check if it's a special repeat - basically means that they have a unique table name
            if (coreFormExtension.coreForm == CoreForm.PREGNANCY_OUTCOME_FORM && repeatName == "childs") {
                nextTableName = PREGNANCY_CHILD_EXT_TABLE
            }

            if (coreFormExtension.coreForm == CoreForm.PREGNANCY_VISIT_FORM && repeatName == "childs") {
                nextTableName = PREGNANCY_VISIT_CHILD_EXT_TABLE
            }

            //all repeat groups must create an extra database table
            // Create repeat datamodel
            def repeatModel = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
            repeatModel.dbColumnIndex = columnIndex++
            repeatModel.dbColumnTable = nextTableName
            repeatModel.dbColumnName = nextTableName
            repeatModel.dbColumnType = DatabaseColumnType.NOT_APPLICABLE
            repeatModel.dbColumnSize = -1
            repeatModel.formColumnName = repeatName
            repeatModel.formColumnType = FormColumnType.REPEAT_GROUP
            repeatModel.formRepeatGroup = (parentRepeatModel == null) ? null : parentRepeatModel.dbColumnName
            repeatModel.formRepeatLength = -1
            repeatModel.formChoiceList = false
            repeatModel.formChoiceValue = null
            repeatModel.repeatPerTable = true
            repeatModel.parentGroup = parentRepeatModel
            repeatModel.save(flush: true)

            //add default system columns
            columnIndex = addSystemColumns(coreFormExtension, nextTableName, columnIndex, tableName, repeatModel)

            // Process inner groups
            repeatGroup.columnsGroups.each { innerGroup ->
                columnIndex = processColumnGroup(coreFormExtension, nextTableName, innerGroup, columnIndex, repeatModel)
            }

        } else {
            // Regular ColumnGroup
            columnGroup.columns.each { column ->
                columnIndex = processColumn(coreFormExtension, tableName, column, columnIndex, parentRepeatModel)
            }
        }

        return columnIndex
    }

    def int processColumn(CoreFormExtension coreFormExtension, String tableName, Column column, int columnIndex, CoreFormExtensionModel parentRepeatModel) {
        def formColName = column.getName()
        def formColType = column.getType()

        // Ignore internal HForm variables if present in the list
        if (["id", "start", "end", "device_id", "postExecution", "media_collected"].contains(formColName)) return columnIndex

        def dbColName = StringUtil.toSnakeCase(formColName)

        if (formColType == ColumnType.MULTI_SELECT) {
            // Create multiple choice data model answers
            def options = column.getTypeOptions()
            if (options) {
                def maxChoiceLength = options.keySet().collect { it.length() }.max() ?: 0
                def maxLength = (int) ((Math.floor(maxChoiceLength / 10) * 10) + 10)

                options.eachWithIndex { entry, index ->
                    def choiceValue = entry.key
                    def strIndex = String.format("%02d", index)

                    def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
                    model.dbColumnIndex = columnIndex++
                    model.dbColumnTable = tableName
                    model.dbColumnName = "${dbColName}_${strIndex}"
                    model.dbColumnType = DatabaseColumnType.STRING
                    model.dbColumnSize = maxLength
                    model.formColumnName = formColName
                    model.formColumnType = FormColumnType.MULTIPLE_ITEMS
                    model.formRepeatGroup = (parentRepeatModel == null) ? null : parentRepeatModel.dbColumnName
                    model.formRepeatLength = 0
                    model.formChoiceList = true
                    model.formChoiceValue = choiceValue
                    model.parentGroup = parentRepeatModel
                    model.save(flush: true)
                }
            }
            return columnIndex
        }

        if (formColType == ColumnType.GPS) {
            ["Lat", "Lon", "Alt", "Acc"].each { gpsSuffix ->
                def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
                model.dbColumnIndex = columnIndex++
                model.dbColumnTable = tableName
                model.dbColumnName = "${dbColName}_${gpsSuffix.toLowerCase()}"
                model.dbColumnType = DatabaseColumnType.DOUBLE
                model.dbColumnSize = -1
                model.formColumnName = "${formColName}${gpsSuffix}"
                model.formColumnType = FormColumnType.GEOPOINT
                model.formRepeatGroup = (parentRepeatModel == null) ? null : parentRepeatModel.dbColumnName
                model.formRepeatLength = 0
                model.formChoiceList = false
                model.formChoiceValue = gpsSuffix
                model.parentGroup = parentRepeatModel
                model.save(flush: true)
            }
            return columnIndex
        }

        // Handle other types
        DatabaseColumnType dbColumnType = null
        int dbColumnSize = -1
        FormColumnType fColumnType = null

        switch (formColType) {
            case ColumnType.STRING:
                dbColumnType = DatabaseColumnType.STRING; dbColumnSize = 255; fColumnType = FormColumnType.TEXT; break
            case ColumnType.INTEGER:
                dbColumnType = DatabaseColumnType.INTEGER; fColumnType = FormColumnType.INTEGER; break
            case ColumnType.DECIMAL:
                dbColumnType = DatabaseColumnType.DECIMAL; fColumnType = FormColumnType.DECIMAL; break
            case ColumnType.DATE:
                dbColumnType = DatabaseColumnType.DATETIME; fColumnType = FormColumnType.DATE; break
            case ColumnType.TIME:
                dbColumnType = DatabaseColumnType.DATETIME; fColumnType = FormColumnType.TIME; break
            case ColumnType.DATETIME:
                dbColumnType = DatabaseColumnType.DATETIME; fColumnType = FormColumnType.DATE_TIME; break
            case ColumnType.SELECT:
                dbColumnType = DatabaseColumnType.STRING; fColumnType = FormColumnType.CHOICE
                def options = column.getTypeOptions()
                if (options) {
                    def maxChoiceLength = options.keySet().collect { it.length() }.max() ?: 0
                    dbColumnSize = (int) ((Math.floor(maxChoiceLength / 10) * 10) + 10)
                } else {
                    dbColumnSize = 40
                }
                break
            case ColumnType.BARCODE:
                dbColumnType = DatabaseColumnType.STRING; dbColumnSize = 100; fColumnType = FormColumnType.BARCODE; break
            case ColumnType.START_TIMESTAMP:
            case ColumnType.END_TIMESTAMP:
            case ColumnType.TIMESTAMP:
                dbColumnType = DatabaseColumnType.TIMESTAMP; fColumnType = FormColumnType.TIMESTAMP; break
            case ColumnType.IMAGE:
            case ColumnType.AUDIO:
            case ColumnType.VIDEO:
                //dbColumnType = DatabaseColumnType.BLOB; fColumnType = FormColumnType.BINARY; break
                dbColumnType = DatabaseColumnType.STRING; dbColumnSize = 255; fColumnType = FormColumnType.TEXT; break
            case ColumnType.NOTE:
                return columnIndex
            default:
                dbColumnType = DatabaseColumnType.STRING; dbColumnSize = 255; fColumnType = FormColumnType.UNSUPPORTED; break
        }

        def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
        model.dbColumnIndex = columnIndex++
        model.dbColumnTable = tableName
        model.dbColumnName = dbColName
        model.dbColumnType = dbColumnType
        model.dbColumnSize = dbColumnSize
        model.formColumnName = formColName
        model.formColumnType = fColumnType
        model.formRepeatGroup = (parentRepeatModel == null) ? null : parentRepeatModel.dbColumnName
        model.formRepeatLength = 0
        model.formChoiceList = false
        model.formChoiceValue = null
        model.parentGroup = parentRepeatModel
        model.save(flush: true)

        return columnIndex
    }

    def int addSystemColumns(CoreFormExtension coreFormExtension, String tableName, int columnIndex, String parentTableName, CoreFormExtensionModel parentRepeatModel) {
        // id BIGINT
        def idModel = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
        idModel.dbColumnIndex = columnIndex++
        idModel.dbColumnTable = tableName
        idModel.dbColumnName = ExtensionDatabaseColumns.ID
        idModel.dbColumnType = DatabaseColumnType.LONG
        idModel.dbColumnSize = -1
        idModel.formColumnName = ExtensionDatabaseColumns.ID
        idModel.formColumnType = FormColumnType.SYSTEM
        idModel.parentGroup = parentRepeatModel
        idModel.save(flush: true)

        // collected_id STRING
        def collIdModel = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
        collIdModel.dbColumnIndex = columnIndex++
        collIdModel.dbColumnTable = tableName
        collIdModel.dbColumnName = ExtensionDatabaseColumns.COLLECTED_ID
        collIdModel.dbColumnType = DatabaseColumnType.STRING
        collIdModel.dbColumnSize = 32
        collIdModel.formColumnName = ExtensionDatabaseColumns.COLLECTED_ID
        collIdModel.formColumnType = FormColumnType.SYSTEM
        collIdModel.parentGroup = parentRepeatModel
        collIdModel.save(flush: true)

        // parent_id BIGINT
        def parentIdModel = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
        parentIdModel.dbColumnIndex = columnIndex++
        parentIdModel.dbColumnTable = tableName
        parentIdModel.dbColumnName = "${parentRepeatModel?.parentGroup?.dbColumnTable ?: parentTableName}_id"
        parentIdModel.dbColumnType = DatabaseColumnType.LONG
        parentIdModel.dbColumnSize = -1
        parentIdModel.formColumnName = ExtensionDatabaseColumns.FORM_PARENT_ID
        parentIdModel.formColumnType = FormColumnType.SYSTEM
        parentIdModel.parentGroup = parentRepeatModel
        parentIdModel.save(flush: true)

        // ordinal_number INTEGER
        def ordinalModel = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
        ordinalModel.dbColumnIndex = columnIndex++
        ordinalModel.dbColumnTable = tableName
        ordinalModel.dbColumnName = ExtensionDatabaseColumns.ORDINAL_NUMBER
        ordinalModel.dbColumnType = DatabaseColumnType.INTEGER
        ordinalModel.dbColumnSize = -1
        ordinalModel.formColumnName = ExtensionDatabaseColumns.ORDINAL_NUMBER
        ordinalModel.formColumnType = FormColumnType.SYSTEM
        ordinalModel.parentGroup = parentRepeatModel
        ordinalModel.save(flush: true)

        return columnIndex
    }

    List<String[]> executeSqlCommands(List<String> listCommands) {

        def resultMessages = new ArrayList<String[]>()

        CoreFormExtension.withSession { Session session ->
            session.doWork new Work() {
                void execute(Connection connection) throws SQLException {
                    def sql = new Sql(connection)

                    listCommands.each { sqlcommand ->
                        try {
                            def result = sql.execute(sqlcommand, new ArrayList<Object>())
                            resultMessages.add(new String[] { sqlcommand, generalUtilitiesService.getMessage("coreFormExtension.columns.successfully.executed.label"), "true"})
                        } catch (SQLException ex){
                            resultMessages.add(new String[] { sqlcommand, ex.getMessage(), "false"})
                        }
                    }

                    //sql.close()
                }
            }
        }

        return resultMessages
    }

    SqlExecutionResult executeSqlInsert(String tableName, LinkedHashMap<String, Object> mapValues) {

        SqlExecutionResult result = null

        removeNonExistentColumns(tableName, mapValues)

        if (mapValues.size()==0) { //core_form xml is empty
            def msg = generalUtilitiesService.getMessage("coreFormExtension.database.insert.xml.error", new String[] {tableName}, "")
            println(msg)

            return new SqlExecutionResult(success: false, errorMessage: msg, command: "empty sql insert")
        }

        def index = 0
        def columns = mapValues.keySet().join(", ")
        def params = mapValues.keySet().collect { "?" }.join(', ') //${index++}
        def values= mapValues.values().collect { it}
        def sqlinsert = "insert into ${tableName}(${columns}) values (${params});" as String

        //println("columns: ${columns}")
        //println("params: ${params}")
        //println("values: ${values}")

        CoreFormExtension.withSession { Session session ->
            session.doWork new Work() {
                void execute(Connection connection) throws SQLException {
                    def sql = new Sql(connection)
                    try {

                        def queryResult = sql.executeInsert(sqlinsert, values)

                        result = new SqlExecutionResult(success: true, errorMessage: null, command: sqlinsert)
                        result.keys = new ArrayList<>()

                        if (queryResult.size()>0) {
                            result.keys.addAll(queryResult.first())
                        }

                        //println "sql = " + sqlinsert
                        //println "query result = ${queryResult}"
                        //println "result id=" + result.keys + ", type="+result.keys
                        //println "result id=" + result.keys?.first() + ", type="+result.keys?.first()?.getClass()

                    } catch (SQLException ex){
                        ex.printStackTrace()
                        def prefix = generalUtilitiesService.getMessage("coreFormExtension.database.insert.xml.prefix.error", new String[]{tableName}, "")
                        result = new SqlExecutionResult(success: false, errorMessage: prefix + " - " + ex.getMessage(), command: sqlinsert)
                    }

                    //sql.close()
                }
            }
        }

        result.mappedValues = mapValues

        return result
    }

    def executeSqlDeleteByCollectedId(String tableName, String collectedId) {
        String sqlDelete = "DELETE FROM ${tableName} WHERE ${ExtensionDatabaseColumns.COLLECTED_ID} = ?;"
        CoreFormExtension.withSession { Session session ->
            session.doWork new Work() {
                void execute(Connection connection) throws SQLException {
                    def sql = new Sql(connection)
                    sql.execute(sqlDelete, [collectedId])
                }
            }
        }
    }

    def removeNonExistentColumns(String tableName, LinkedHashMap<String, Object> mapValues) {
        def existentColumns = getDatabaseColumns(tableName).collect { it.name }
        def cols = new ArrayList<>(mapValues.keySet())

        cols.each { col ->
            if (!existentColumns.contains(col)) {
                mapValues.remove(col)
            }
        }
    }

    List<JDatabaseColumn> getDatabaseColumns(String tableName) {
        def list = new ArrayList<JDatabaseColumn>()

        CoreFormExtension.withSession { Session session ->
            session.doWork new Work() {
                void execute(Connection connection) throws SQLException {
                    try {
                        def sql = new Sql(connection)
                        sql.rows("select * from " + tableName, 0, 1, { metadata ->
                            int cols = metadata.getColumnCount()
                            for (int i = 1; i <= cols; i++) {
                                list.add(new JDatabaseColumn(table: tableName, name: metadata.getColumnName(i), type: metadata.getColumnTypeName(i), size: metadata.getColumnDisplaySize(i) + ""))
                                //println "name: ${metadata.getColumnName(i)}, type: ${metadata.getColumnTypeName(i)}, dsize: ${metadata.getColumnDisplaySize(i)}"
                            }
                        })
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        return list
    }

    List<String> generateSqlCommandsFrom(CoreFormExtension coreFormExtension, List<CoreFormExtensionModel> models) {
        def sqlcommands = new ArrayList<String>()

        models.each {model ->
            def sql = ""

            if (model.formColumnType == FormColumnType.REPEAT_GROUP) {
                def repeatModel = model

                //all repeat groups are separated tables - must create a table and then iterate the inner columns

                def parentIdModel = models.find { it.parentGroup == repeatModel && it.formColumnName == ExtensionDatabaseColumns.FORM_PARENT_ID}
                def createSql = "CREATE TABLE IF NOT EXISTS ${repeatModel.dbColumnTable} (" +
                                "${ExtensionDatabaseColumns.ID} BIGINT NOT NULL AUTO_INCREMENT, " +
                                "${ExtensionDatabaseColumns.COLLECTED_ID} VARCHAR(32), " +
                                "${parentIdModel.dbColumnName} BIGINT, " +
                                "${ExtensionDatabaseColumns.ORDINAL_NUMBER} INT, " +
                                "PRIMARY KEY (${ExtensionDatabaseColumns.ID})" +
                                ");"
                sqlcommands.add(createSql)

            } else {
                //regular columns
                if (model.formColumnType == FormColumnType.SYSTEM) return //ignore system columns - they are already created

                //alter table TABLE_NAME ADD new_column TYPE(SIZE);
                def databaseTypeAndSize = getDatabaseTypeAndSize(model)
                sql = "alter table ${model.dbColumnTable} add ${model.dbColumnName} ${databaseTypeAndSize};"
                sqlcommands.add(sql)
            }
        }

        return sqlcommands
    }

    String getDatabaseSystemName() {

        def result = ""

        CoreFormExtension.withSession { Session session ->
            session.doWork new Work() {
                void execute(Connection connection) throws SQLException {
                    //def sql = new Sql(connection)
                    def metadata = connection.metaData
                    result = "${metadata.getDatabaseProductName()} v${metadata.getDatabaseProductVersion()}"
                }
            }
        }

        return result
    }

    String getDatabaseTypeAndSize(CoreFormExtensionModel model) {
        def result = ""

        switch (model.dbColumnType) {
            case DatabaseColumnType.BLOB:    result = "MEDIUMBLOB"; break;
            case DatabaseColumnType.BOOLEAN: result = "BIT(1)"; break;
            case DatabaseColumnType.DECIMAL: result = "DECIMAL(38,10)"; break;
            case DatabaseColumnType.DOUBLE: result = "DOUBLE"; break;
            case DatabaseColumnType.INTEGER: result = "INT"; break;
            case DatabaseColumnType.LONG: result = "BIGINT"; break;
            case DatabaseColumnType.DATETIME: result = "DATETIME"; break;
            case DatabaseColumnType.STRING: result = "VARCHAR(${model.dbColumnSize})"; break;
            case DatabaseColumnType.NOT_APPLICABLE: break;
        }

        return result
    }

    JActionResult updateDataModel(String editedColumnName, String id, String newValue) {

        def model = CoreFormExtensionModel.findById(id)

        //check if newValue is blank
        if (StringUtil.isBlank(newValue)) {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.notblank.label"))
        }

        //check if it is renaming dbColumnName
        if (editedColumnName.equals("dbColumnName") && !model.dbColumnName.equals(newValue) && CoreFormExtensionModel.countByDbColumnName(newValue)>0) {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.option.unique.label"))
        }

        Object objNewValue = newValue
        if (editedColumnName.equals("dbColumnSize") || editedColumnName.equals("formRepeatLength")) {
            //convert to int
            objNewValue = Integer.parseInt(newValue)
        }

        //update the record
        CoreFormExtensionModel.executeUpdate("update CoreFormExtensionModel set " + editedColumnName + " = ?0 where id = ?1", [objNewValue, id])

        return new JActionResult(result: JActionResult.Result.SUCCESS.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.updated.label"))
    }

    JActionResult deleteDataModel(String id) {
        def model = CoreFormExtensionModel.get(id)
        //checks
        model.delete(flush: true)

        if (!model.hasErrors()) {
            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.deleted.label"))
        } else {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: "" + errorMessageService.formatErrors(model))
        }
    }

    class SqlExecutionResult {
        boolean success
        String errorMessage
        String command
        Map<String, Object> mappedValues
        List<Object> keys
    }

}
