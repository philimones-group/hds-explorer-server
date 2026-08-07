package org.philimone.hds.explorer.server.model.main.extension

import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import net.betainteractive.io.odk.util.XFormReader
import net.betainteractive.utilities.StringUtil
import org.hibernate.Session
import org.hibernate.jdbc.Work
import org.javarosa.core.model.DataType
import org.javarosa.core.model.FormDef
import org.javarosa.core.model.GroupDef
import org.javarosa.core.model.IFormElement
import org.javarosa.core.model.SelectChoice
import org.javarosa.core.model.instance.TreeElement
import org.javarosa.core.model.instance.TreeReference
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
import org.philimone.hds.forms.model.parsers.ExcelFormParser

import java.sql.Connection
import java.sql.SQLException

@Transactional
class CoreExtensionDatabaseService {

    def sessionFactory
    def generalUtilitiesService
    def errorMessageService

    def generateDatabaseModel(CoreFormExtension coreFormExtension) {
        if (coreFormExtension.extFormPath != null) {
            def inputStream = new FileInputStream(new File(coreFormExtension.extFormPath))
            def hForm = new ExcelFormParser(inputStream).getForm()

            if (hForm != null) {
                def columnIndex = 0
                def tableName = coreFormExtension.extFormId

                hForm.columns.each { columnGroup ->
                    columnIndex = processColumnGroup(coreFormExtension, tableName, columnGroup, columnIndex, null)
                }
            }
        }
    }

    def int processColumnGroup(CoreFormExtension coreFormExtension, String tableName, ColumnGroup columnGroup, int columnIndex, CoreFormExtensionModel parentGroupModel) {

        if (columnGroup instanceof ColumnRepeatGroup) {
            def repeatGroup = (ColumnRepeatGroup) columnGroup
            def forname = repeatGroup.getName()
            def colname = (parentGroupModel == null) ? forname : "${parentGroupModel.dbColumnName}_#_${forname}"

            // Check if it's a special pregnancy repeat
            if (coreFormExtension.coreForm == CoreForm.PREGNANCY_OUTCOME_FORM && forname == "childs") {
                repeatGroup.columnsGroups.each { innerGroup ->
                    columnIndex = processColumnGroup(coreFormExtension, CoreExtensionService.PREGNANCY_CHILD_EXT_TABLE, innerGroup, columnIndex, null)
                }
                return columnIndex
            }

            if (coreFormExtension.coreForm == CoreForm.PREGNANCY_VISIT_FORM && forname == "childs") {
                repeatGroup.columnsGroups.each { innerGroup ->
                    columnIndex = processColumnGroup(coreFormExtension, CoreExtensionService.PREGNANCY_VISIT_CHILD_EXT_TABLE, innerGroup, columnIndex, null)
                }
                return columnIndex
            }

            // Create repeat datamodel
            def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
            model.dbColumnIndex = columnIndex++
            model.dbColumnTable = tableName
            model.dbColumnName = colname
            model.dbColumnType = DatabaseColumnType.NOT_APPLICABLE
            model.dbColumnSize = -1
            model.formColumnName = forname
            model.formColumnType = FormColumnType.REPEAT_GROUP
            model.formRepeatGroup = (parentGroupModel == null) ? null : parentGroupModel.dbColumnName
            model.formRepeatLength = 2 // default min - get the constant number - but also can be variable/dynamic in such cases we must create a separated table
            model.formChoiceList = false
            model.formChoiceValue = null
            model.parentGroup = parentGroupModel
            model.save(flush: true)

            // Process inner groups
            repeatGroup.columnsGroups.each { innerGroup ->
                columnIndex = processColumnGroup(coreFormExtension, tableName, innerGroup, columnIndex, model)
            }

        } else {
            // Regular ColumnGroup
            columnGroup.columns.each { column ->
                columnIndex = processColumn(coreFormExtension, tableName, column, columnIndex, parentGroupModel)
            }
        }

        return columnIndex
    }

    def int processColumn(CoreFormExtension coreFormExtension, String tableName, Column column, int columnIndex, CoreFormExtensionModel parentGroupModel) {
        def forname = column.getName()

        // Ignore internal ODK/HForm variables if present in the list
        if (["instanceID", "instanceName", "id", "start", "end", "device_id", "postExecution", "media_collected"].contains(forname)) return columnIndex

        def colname = (parentGroupModel == null) ? forname : "${parentGroupModel.dbColumnName}_#_${forname}"
        def fortype = column.getType()

        if (fortype == ColumnType.MULTI_SELECT) {
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
                    model.dbColumnName = "${colname}_${strIndex}"
                    model.dbColumnType = DatabaseColumnType.STRING
                    model.dbColumnSize = maxLength
                    model.formColumnName = forname
                    model.formColumnType = FormColumnType.MULTIPLE_ITEMS
                    model.formRepeatGroup = (parentGroupModel == null) ? null : parentGroupModel.dbColumnName
                    model.formRepeatLength = 0
                    model.formChoiceList = true
                    model.formChoiceValue = choiceValue
                    model.parentGroup = parentGroupModel
                    model.save(flush: true)
                }
            }
            return columnIndex
        }

        if (fortype == ColumnType.GPS) {
            ["lat", "lng", "alt", "acc"].each { gpsSuffix ->
                def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
                model.dbColumnIndex = columnIndex++
                model.dbColumnTable = tableName
                model.dbColumnName = "${colname}_${gpsSuffix}"
                model.dbColumnType = DatabaseColumnType.DOUBLE
                model.dbColumnSize = -1
                model.formColumnName = forname
                model.formColumnType = FormColumnType.GEOPOINT
                model.formRepeatGroup = (parentGroupModel == null) ? null : parentGroupModel.dbColumnName
                model.formRepeatLength = 0
                model.formChoiceList = false
                model.formChoiceValue = gpsSuffix
                model.parentGroup = parentGroupModel
                model.save(flush: true)
            }
            return columnIndex
        }

        // Handle other types
        DatabaseColumnType dbColumnType = null
        int dbColumnSize = -1
        FormColumnType fColumnType = null

        switch (fortype) {
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
        model.dbColumnName = colname
        model.dbColumnType = dbColumnType
        model.dbColumnSize = dbColumnSize
        model.formColumnName = forname
        model.formColumnType = fColumnType
        model.formRepeatGroup = (parentGroupModel == null) ? null : parentGroupModel.dbColumnName
        model.formRepeatLength = 0
        model.formChoiceList = false
        model.formChoiceValue = null
        model.parentGroup = parentGroupModel
        model.save(flush: true)

        return columnIndex
    }

    List<String[]> executeSqlCommands(String commandsText) {

        def resultMessages = new ArrayList<String[]>()

        commandsText = commandsText.replace("\n", "")
        List<String> listCommands = commandsText.split(";").collect {"${it};"}

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
                    def sql = new Sql(connection)
                    sql.rows("select * from "+tableName, 0, 1, { metadata ->
                        int cols = metadata.getColumnCount()
                        for (int i=1; i <= cols; i++) {
                            list.add(new JDatabaseColumn(table: tableName, name: metadata.getColumnName(i), type: metadata.getColumnTypeName(i), size: metadata.getColumnDisplaySize(i)+""))
                            //println "name: ${metadata.getColumnName(i)}, type: ${metadata.getColumnTypeName(i)}, dsize: ${metadata.getColumnDisplaySize(i)}"
                        }
                    })
                }
            }
        }

        return list
    }

    List<String> generateSqlCommandsFrom(List<CoreFormExtensionModel> models) {
        def sqlcommands = new ArrayList<String>()

        //def modelsList = models.findAll { it.dbColumnType != DatabaseColumnType.NOT_APPLICABLE}
        //alter table TABLE_NAME ADD new_column TYPE(SIZE);

        def repeatIndexes = new LinkedHashMap<String, Integer>()

        models.each {model ->
            def sql = ""

            if (model.formColumnType == FormColumnType.REPEAT_GROUP && model.formRepeatGroup == null) { //get root repeat groups
                //deal with repeats
                //get all inner groups of these repeat

                repeatIndexes.put(model.dbColumnName, 1)

                def resultList = generateSqlComandsFromRepeat(model, models, repeatIndexes)
                sqlcommands.addAll(resultList)

                return
            } else if (model.formRepeatGroup != null) { //cols belongs to inner groups
                return
            } else {
                //regular columns

                def databaseTypeSize = getDatabaseTypeSize(model)
                sql = "alter table ${model.dbColumnTable} add ${model.dbColumnName} ${databaseTypeSize};"
                sqlcommands.add(sql)
            }
        }
        return sqlcommands
    }

    List<String> generateSqlComandsFromRepeat(CoreFormExtensionModel repeatModel, List<CoreFormExtensionModel> models, HashMap<String, Integer> repeatIndexes) {
        def sqlcommands = new ArrayList<String>()
        def innermodels = models.findAll { it.parentGroup==repeatModel}

        for (int i=1; i <= repeatModel.formRepeatLength; i++) {

            repeatIndexes.put(repeatModel.dbColumnName, i)

            innermodels.each {model ->
                if (model.formColumnType == FormColumnType.REPEAT_GROUP) {
                    def resultList = generateSqlComandsFromRepeat(model, models, repeatIndexes)
                    sqlcommands.addAll(resultList)
                } else {
                    //regular variable
                    def columnName = getFinalColumnName(model, repeatIndexes)
                    def databaseTypeSize = getDatabaseTypeSize(model)
                    def sql = "alter table ${model.dbColumnTable} add ${columnName} ${databaseTypeSize};"
                    sqlcommands.add(sql)
                }
            }
        }

        return sqlcommands
    }

    String getFinalColumnName(CoreFormExtensionModel modelLoop, HashMap<String, Integer> repeatIndexes) {
        def finalColumnName = ""

        while (modelLoop != null) {
            def origName = modelLoop.dbColumnName
            def remParName = (modelLoop.formRepeatGroup != null) ? origName.replace(modelLoop.formRepeatGroup, "") : origName

            //remove the parent repeat group from the naming
            def parIndex = repeatIndexes.get((modelLoop.formRepeatGroup != null) ? modelLoop.formRepeatGroup : origName)
            remParName = remParName.replace("#", "${String.format('%02d', parIndex)}")

            finalColumnName = remParName + finalColumnName

            modelLoop = modelLoop.parentGroup
        }

        return finalColumnName
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

    String getDatabaseTypeSize(CoreFormExtensionModel model) {
        def result = ""

        switch (model.dbColumnType) {
            case DatabaseColumnType.BLOB:    result = "MEDIUMBLOB"; break;
            case DatabaseColumnType.BOOLEAN: result = "BIT(1)"; break;
            case DatabaseColumnType.DECIMAL: result = "DECIMAL(38,10)"; break;
            case DatabaseColumnType.DOUBLE: result = "DOUBLE"; break;
            case DatabaseColumnType.INTEGER: result = "INT"; break;
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
