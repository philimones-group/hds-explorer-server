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
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.CoreFormExtensionModel

import java.sql.Connection
import java.sql.SQLException

@Transactional
class CoreExtensionDatabaseService {

    def sessionFactory
    def generalUtilitiesService
    def errorMessageService

    def generateDatabaseModel(CoreFormExtension coreFormExtension) {
        if (coreFormExtension.extFormDefinition != null) {
            def formDef = XFormReader.getFormDefinition(coreFormExtension.extFormDefinition)
            def formInstance = formDef.getMainInstance()
            def rootElement = formInstance.getRoot()
            def columnIndex = 0

            //iterate treeElement - match the types and create the model
            def tableName = coreFormExtension.extFormId
            def repeatCountCols = findRepeatCountColumns(formDef)
            generateDatabaseModelProcessChildren(coreFormExtension, tableName, formDef, rootElement, columnIndex, null, repeatCountCols)
        }
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
                        result.keys.addAll(queryResult.first())

                        //println "result id=" + result.keys?.first() + ", type="+result.keys?.first()?.getClass()

                    } catch (SQLException ex){
                        ex.printStackTrace()

                        result = new SqlExecutionResult(success: false, errorMessage: ex.getMessage(), command: sqlinsert)
                    }

                    //sql.close()
                }
            }
        }

        result.mappedValues = mapValues

        return result
    }

    private int generateDatabaseModelProcessChildren(CoreFormExtension coreFormExtension, String tableName, FormDef formDef, TreeElement rootElement, int columnIndex, CoreFormExtensionModel repeatGroupModel, List<TreeReference> repeatCountColumns) {
        for (int i=0; i < rootElement.numChildren; i++) {
            def element = rootElement.getChildAt(i)
            def questDef = FormDef.findQuestionByRef(element.getRef(), formDef)

            def forname = element.getName()
            def colname = (repeatGroupModel==null) ? element.getName() : "${repeatGroupModel.dbColumnName}_#_${element.getName()}"  //ANALYSE SUFFIXES
            def fortype = element.getDataType()
            def repeatgroup = element.repeatable
            def hasChoices = questDef?.getNumChoices() > 0
            def choices = hasChoices ? questDef?.choices : null

            if (["instanceID", "instanceName"].contains(element.getName())) continue /* ignore these variables */

            if (fortype == DataType.NULL.value && !repeatgroup && element.numChildren > 0) { //Its a group
                generateDatabaseModelProcessChildren(coreFormExtension, tableName, formDef, element, columnIndex, repeatGroupModel, repeatCountColumns)
                continue
            }

            if (forname.endsWith("_count") && fortype == DataType.TEXT.value){ //check if is a jr:count variable
                def ref = element.getRef().genericize()

                //println("ref found: "+ref+" "+repeatCountColumns.contains(ref))

                if (repeatCountColumns.contains(ref)) {
                    continue
                }
            }

            if (fortype == DataType.NULL.value && repeatgroup) { //its a repeat group initiation

                if (coreFormExtension.coreForm == CoreForm.PREGNANCY_OUTCOME_FORM && forname.equals("childs")) {

                    columnIndex = generateDatabaseModelProcessChildren(coreFormExtension, CoreExtensionService.PREGNANCY_CHILD_EXT_TABLE, formDef, element, columnIndex, null, repeatCountColumns)
                    continue
                }

                //create repeat datamodel
                def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
                model.dbColumnIndex = columnIndex++
                model.dbColumnTable = tableName
                model.dbColumnName = colname
                model.dbColumnType = DatabaseColumnType.NOT_APPLICABLE
                model.dbColumnSize = -1
                model.formColumnName = forname
                model.formColumnType = FormColumnType.REPEAT_GROUP
                model.formRepeatGroup = (repeatGroupModel==null) ? null : repeatGroupModel.dbColumnName
                model.formRepeatLength = 2 //min 2
                model.formChoiceList = false
                model.formChoiceValue = null
                model.parentGroup = repeatGroupModel
                model.save(flush:true)

                //process the repeat childs
                columnIndex = generateDatabaseModelProcessChildren(coreFormExtension, tableName, formDef, element, columnIndex, model, repeatCountColumns)
                continue
            }

            if (fortype == DataType.MULTIPLE_ITEMS.value) {
                //Create multiple choice data model answers
                if (hasChoices) {

                    def maxChoice = choices.max { it.value.length() }
                    def maxLength = (int) ((Math.floor(maxChoice?.value?.length()/10)*10) + 10) //round to next 10th

                    choices.eachWithIndex{ SelectChoice choice, int index ->
                        def strIndex = String.format("%02d", index)

                        def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
                        model.dbColumnIndex = columnIndex++
                        model.dbColumnTable = tableName
                        model.dbColumnName = "${colname}_${strIndex}" //rgroup_1_habitations
                        model.dbColumnType = DatabaseColumnType.STRING
                        model.dbColumnSize = maxLength
                        model.formColumnName = forname
                        model.formColumnType = FormColumnType.getFrom(fortype)
                        model.formRepeatGroup = (repeatGroupModel==null) ? null : repeatGroupModel.dbColumnName
                        model.formRepeatLength = 0
                        model.formChoiceList = true
                        model.formChoiceValue = choice.value
                        model.parentGroup = repeatGroupModel
                        model.save(flush:true)
                    }
                }

                continue
            }

            if (fortype == DataType.GEOPOINT.value) {
                //create gps variables
                ["lat", "lng", "alt", "acc"].each { gpsSuffix ->

                    def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
                    model.dbColumnIndex = columnIndex++
                    model.dbColumnTable = tableName
                    model.dbColumnName = "${colname}_${gpsSuffix}"
                    model.dbColumnType = DatabaseColumnType.DOUBLE
                    model.dbColumnSize = -1
                    model.formColumnName = forname
                    model.formColumnType = FormColumnType.GEOPOINT
                    model.formRepeatGroup = (repeatGroupModel==null) ? null : repeatGroupModel.dbColumnName
                    model.formRepeatLength = 0
                    model.formChoiceList = false
                    model.formChoiceValue = gpsSuffix
                    model.parentGroup = repeatGroupModel
                    model.save(flush:true)

                }

                continue
            }

            //handle other dataTypes
            DatabaseColumnType dbColumnType = null
            int dbColumnSize = -1

            /* retrieve database column type */
            switch (fortype) {
                case DataType.TEXT.value:    dbColumnType = DatabaseColumnType.STRING; dbColumnSize = 255; break;
                case DataType.INTEGER.value: dbColumnType = DatabaseColumnType.INTEGER; break;
                case DataType.DECIMAL.value: dbColumnType = DatabaseColumnType.DECIMAL; break;
                case DataType.DATE.value:    dbColumnType = DatabaseColumnType.DATETIME; break;
                case DataType.TIME.value:    dbColumnType = DatabaseColumnType.DATETIME; break;
                case DataType.DATE_TIME.value: dbColumnType = DatabaseColumnType.DATETIME; break;
                case DataType.CHOICE.value: dbColumnType = DatabaseColumnType.STRING; break;
                case DataType.MULTIPLE_ITEMS.value: dbColumnType = DatabaseColumnType.STRING; break;
                case DataType.BOOLEAN.value: dbColumnType = DatabaseColumnType.BOOLEAN; break;
                case DataType.GEOPOINT.value: /*dbColumnType = DatabaseColumnType.GEOPOINT;*/ break
                case DataType.GEOTRACE.value: /* Question with location trace. */ break;
                case DataType.GEOSHAPE.value: /* Question with location trace. */ break;
                case DataType.BARCODE.value: dbColumnType = DatabaseColumnType.STRING; dbColumnSize = 100; /* guessing */ break;
                case DataType.BINARY.value:  dbColumnType = DatabaseColumnType.BLOB; break;
                case DataType.NULL.value: break;
                case DataType.UNSUPPORTED.value: dbColumnType = DatabaseColumnType.STRING; dbColumnSize = 255; break;
                default: break
            }

            //others types
            def model = new CoreFormExtensionModel(coreForm: coreFormExtension, extFormId: coreFormExtension.extFormId)
            model.dbColumnIndex = columnIndex++
            model.dbColumnTable = tableName
            model.dbColumnName = colname
            model.dbColumnType = dbColumnType
            model.dbColumnSize = dbColumnSize
            model.formColumnName = forname
            model.formColumnType = FormColumnType.getFrom(fortype)
            model.formRepeatGroup = (repeatGroupModel==null) ? null : repeatGroupModel.dbColumnName
            model.formRepeatLength = 0
            model.formChoiceList = false
            model.formChoiceValue = null
            model.parentGroup = repeatGroupModel
            model.save(flush:true)
        }

        return columnIndex
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

    UpdateResult updateDataModel(String editedColumnName, String id, String newValue) {

        def model = CoreFormExtensionModel.findById(id)

        //check if newValue is blank
        if (StringUtil.isBlank(newValue)) {
            return new UpdateResult(result: UpdateResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.notblank.label"))
        }

        //check if it is renaming dbColumnName
        if (editedColumnName.equals("dbColumnName") && !model.dbColumnName.equals(newValue) && CoreFormExtensionModel.countByDbColumnName(newValue)>0) {
            return new UpdateResult(result: UpdateResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.option.unique.label"))
        }

        Object objNewValue = newValue
        if (editedColumnName.equals("dbColumnSize") || editedColumnName.equals("formRepeatLength")) {
            //convert to int
            objNewValue = Integer.parseInt(newValue)
        }

        //update the record
        CoreFormExtensionModel.executeUpdate("update CoreFormExtensionModel set " + editedColumnName + " = ?0 where id = ?1", [objNewValue, id])

        return new UpdateResult(result: UpdateResult.Result.SUCCESS.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.updated.label"))
    }

    UpdateResult deleteDataModel(String id) {
        def model = CoreFormExtensionModel.get(id)
        //checks
        model.delete(flush: true)

        if (!model.hasErrors()) {
            return new UpdateResult(result: UpdateResult.Result.SUCCESS, message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.deleted.label"))
        } else {
            return new UpdateResult(result: UpdateResult.Result.ERROR.name(), message: "" + errorMessageService.formatErrors(model))
        }
    }

    private List<TreeReference> findRepeatCountColumns(FormDef formDef) {
        def list = new ArrayList<TreeReference>()

        if (formDef.children)
            for (int i = 0; i < formDef.children.size(); i++) {
                findRepeatCountColumns(formDef.children.get(i), list);
            }

        return list
    }

    private def findRepeatCountColumns(IFormElement fe, List<TreeReference> list) {
        if (fe instanceof GroupDef) {
            def groupDef = (GroupDef) fe;
            def countRef = groupDef?.countReference

            if (groupDef.repeat && countRef != null) {
                def ref = countRef.reference as TreeReference
                list.add(ref)
            }
        }

        if (fe.children != null) {
            for (int i = 0; i < fe.children.size(); i++) {
                findRepeatCountColumns(fe.getChild(i), list);
            }
        }
    }

    class SqlExecutionResult {
        boolean success
        String errorMessage
        String command
        Map<String, Object> mappedValues
        List<Object> keys
    }

    class UpdateResult {
        enum Result {
            ERROR, SUCCESS
        }

        String result = Result.ERROR.name()
        String message = ""
    }
}
