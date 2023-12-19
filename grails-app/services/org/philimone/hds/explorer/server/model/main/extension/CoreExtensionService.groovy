package org.philimone.hds.explorer.server.model.main.extension

import grails.gorm.transactions.Transactional
import net.betainteractive.io.odk.util.XFormReader
import net.betainteractive.utilities.StringUtil
import org.javarosa.core.model.DataType
import org.javarosa.core.model.FormDef
import org.javarosa.core.model.data.GeoPointData
import org.javarosa.core.model.data.MultipleItemsData
import org.javarosa.core.model.instance.TreeElement
import org.javarosa.xform.util.XFormAnswerDataParser
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.enums.CoreForm
import org.philimone.hds.explorer.server.model.enums.extensions.DatabaseColumnType
import org.philimone.hds.explorer.server.model.enums.extensions.FormColumnType
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.CoreFormExtensionModel
import org.philimone.hds.explorer.server.model.main.Household

import java.time.format.DateTimeFormatter

@Transactional
class CoreExtensionService {

    def coreExtensionDatabaseService

    CoreExtensionDatabaseService.SqlExecutionResult insertHouseholdExtension(RawHousehold rawObj, Household finalObj) {

        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.HOUSEHOLD_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)

        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)


        println "Inserting extension for (${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    LinkedHashMap<String, Object> getInstanceMappedValues(CoreFormExtension coreFormExt, byte[] formDefBytes, byte[] instanceBytes) {
        def formDef = XFormReader.getFormDefinition(formDefBytes)
        def instanceXml = XFormReader.getFormInstanceFrom(instanceBytes)

        //open HouseholdExt
        //get mapping models, navigate xml (attention to Repeat, CHOICE_LIST, GEOPOINT)

        def mapValues = new LinkedHashMap<String, Object>()
        def repeatIndexes = new LinkedHashMap<String, Integer>()
        readElementChildren(coreFormExt, formDef, instanceXml.getRoot(), mapValues, null, 0, repeatIndexes, new String[1])

        return mapValues
    }

    def readElementChildren(CoreFormExtension coreFormExtension, FormDef formDef, TreeElement instanceElement, Map<String, Object> mapValues, String repeatGroup, int repeatLength, LinkedHashMap<String, Integer> repeatIndexes, String[] lastReadedRepeatGroup) {
        for (int i=0; i < instanceElement.numChildren; i++) {
            def insChildElement = instanceElement.getChildAt(i)
            def insChildRef = insChildElement.getRef()
            def questDef = formDef.findQuestionByRef(insChildRef, formDef)
            def defChildElement = formDef.getMainInstance()?.getTemplatePath(insChildRef)

            def formColName = defChildElement.getName()
            def dataType = defChildElement?.dataType
            def answerData = insChildElement?.value
            def textValue = answerData?.displayText
            def repeatgroup = defChildElement.repeatable


            /* ignore these variables */
            if (["instanceID", "instanceName"].contains(insChildElement.getName())) continue

            //Handling Groups
            if (dataType == DataType.NULL.value && !repeatgroup && insChildElement.numChildren > 0) {
                readElementChildren(coreFormExtension, formDef, insChildElement, mapValues, repeatGroup, repeatLength, repeatIndexes)
                continue
            }

            //Handling Repeat Groups
            if (dataType == DataType.NULL.value && repeatgroup) {
                //process the repeat childs, get repeat model
                def repeatModel = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnType(coreFormExtension, formColName, FormColumnType.REPEAT_GROUP)

                if (lastReadedRepeatGroup[0] != null) {
                    if (!lastReadedRepeatGroup[0].equals(repeatModel.dbColumnName)) {
                        //root repeats should not be removed
                        if (!lastReadedRepeatGroup[0].equals(repeatModel.formRepeatGroup)) {
                            //if the last repeated is the parent of this repeat do not remove the last repeat
                            repeatIndexes.remove(lastReadedRepeatGroup[0])  //remove the counting of that repeatgroup to reset counters of inner repeat groups
                        }
                    }
                }

                def repeatIndex = !repeatIndexes.containsKey(repeatModel.dbColumnName) ? 1 : (repeatIndexes.get(repeatModel.dbColumnName)+1)
                repeatIndexes.put(repeatModel.dbColumnName, repeatIndex)

                lastReadedRepeatGroup[0] = repeatModel.dbColumnName

                readElementChildren(coreFormExtension, formDef, insChildElement, mapValues, repeatModel.dbColumnName, repeatModel.formRepeatLength, repeatIndexes, lastReadedRepeatGroup)

                continue
            }

            if (dataType == DataType.MULTIPLE_ITEMS.value && answerData != null) {
                //Read the answers Create multiple choice data model answers
                if (answerData instanceof MultipleItemsData) {
                    answerData.value.each {
                        //get the model
                        //    db_col           odk_col   ock_choice
                        //1. custom_quest_00,custom_quest,LBR
                        def model = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnTypeAndFormChoiceValue(coreFormExtension, formColName, FormColumnType.MULTIPLE_ITEMS, it?.value as String)

                        if (model != null) {
                            if (model.formRepeatGroup == null) { //not under repeat
                                mapValues.put(model.dbColumnName, it?.value)
                            } else { //under a repeat group
                                def finalColumnName = getFinalColumnName(model, repeatIndexes)
                                mapValues.put(finalColumnName, it?.value)
                            }
                        } else {
                            throw new Exception("Error model is null")
                        }

                    }
                }

                continue
            }

            if (dataType == DataType.GEOPOINT.value) {
                //create gps variables
                if (!StringUtil.isBlank(textValue)) {
                    answerData = XFormAnswerDataParser.getAnswerData(textValue, dataType, questDef)
                    def geoData = (GeoPointData) answerData

                    ["lat", "lng", "alt", "acc"].eachWithIndex { gpsSuffix, geoIndex ->
                        def model = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnTypeAndFormChoiceValue(coreFormExtension, formColName, FormColumnType.GEOPOINT, gpsSuffix)
                        def finalColumnName = getFinalColumnName(model, repeatIndexes)
                        mapValues.put(finalColumnName, new Double(geoData.getPart(geoIndex)))
                    }
                }

                continue
            }


            //any other type
            def model = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnType(coreFormExtension, formColName, FormColumnType.getFrom(dataType))

            Object objValue = getObjectValueByType(model.dbColumnType, textValue)

            if (model != null) {

                if (model.formRepeatGroup == null) { //not under repeat
                    mapValues.put(model.dbColumnName, textValue)
                } else {
                    //under a repeat group
                    //habitation_#_rooms_#_type, habitation_#_rooms, habitation
                    //substitution of cardinals
                    def finalColumnName = getFinalColumnName(model, repeatIndexes)

                    mapValues.put(finalColumnName, textValue)
                }

            } else {
                throw new Exception("Error")
            }

        }
    }

    Object getObjectValueByType(DatabaseColumnType dbColumnType, String textValue) {
        Object objValue = textValue

        switch (dbColumnType) {
            case DatabaseColumnType.BLOB:    objValue = new ByteArrayInputStream(textValue.getBytes()); break;
            case DatabaseColumnType.BOOLEAN: objValue = Boolean.parseBoolean(textValue); break;
            case DatabaseColumnType.DECIMAL: objValue = BigDecimal.valueOf(Double.parseDouble(textValue)); break;
            case DatabaseColumnType.DOUBLE: objValue = Double.parseDouble(textValue); break;
            case DatabaseColumnType.INTEGER: objValue = Integer.parseInt(textValue); break;
            case DatabaseColumnType.DATETIME: objValue = StringUtil.toLocalDateTime(textValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME); break;
            case DatabaseColumnType.STRING: break;
            case DatabaseColumnType.NOT_APPLICABLE: break;
        }

        return objValue;
    }

    String getFinalColumnName(CoreFormExtensionModel modelLoop, LinkedHashMap<String, Integer> repeatIndexes) {
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
}
