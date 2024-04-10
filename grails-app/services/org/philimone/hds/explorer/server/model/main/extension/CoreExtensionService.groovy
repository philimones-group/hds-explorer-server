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
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.enums.CoreForm
import org.philimone.hds.explorer.server.model.enums.extensions.DatabaseColumnType
import org.philimone.hds.explorer.server.model.enums.extensions.FormColumnType
import org.philimone.hds.explorer.server.model.main.*

import java.time.format.DateTimeFormatter

@Transactional
class CoreExtensionService {

    static final PREGNANCY_CHILD_EXT_TABLE = "pregnancy_child_ext"

    def coreExtensionDatabaseService

    CoreExtensionDatabaseService.SqlExecutionResult insertRegionExtension(RawRegion rawObj, Region finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.REGION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.regionCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }
    
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

    CoreExtensionDatabaseService.SqlExecutionResult insertVisitExtension(RawVisit rawObj, Visit finalObj) {
        if (rawObj.extensionForm == null) return null

        if (rawObj.extensionForm.length == 0) { println("empty extensionForm"); return null }

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.VISIT_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertEnumerationExtension(RawMemberEnu rawObj, Enumeration finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.MEMBER_ENU_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.code}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertMaritalRelationshipExtension(RawMaritalRelationship rawObj, MaritalRelationship finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.MARITAL_RELATIONSHIP_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberA}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertExternalInMigrationExtension(RawExternalInMigration rawObj, InMigration finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.INMIGRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertInMigrationExtension(RawInMigration rawObj, InMigration finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.INMIGRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }
    
    CoreExtensionDatabaseService.SqlExecutionResult insertOutMigrationExtension(RawOutMigration rawObj, OutMigration finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.OUTMIGRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyRegistrationExtension(RawPregnancyRegistration rawObj, PregnancyRegistration finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.PREGNANCY_REGISTRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.motherCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyOutcomeExtension(RawPregnancyOutcome rawObj, PregnancyOutcome finalObj) {

        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.PREGNANCY_OUTCOME_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map - in this form we have a special repeat (childs) -> that will be sent to a separated table
        def instanceMappedValues = getExtraInstanceMappedValues(coreFormExt, ["childs"], coreFormExt.extFormDefinition, rawObj.extensionForm)

        //insert into pregnancy_outcome_ext
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, instanceMappedValues.mainFormValues)

        //insert into pregnancy_child_ext
        if (result != null && result.success) {
            def id = result.keys?.first()

            instanceMappedValues.childFormValues.get("childs").each {map ->
                //insert secondary key and others
                map.put("collected_id", finalObj.collectedId)
                map.put("pregnancy_outcome_ext_id", id)

                def cresult = coreExtensionDatabaseService.executeSqlInsert(PREGNANCY_CHILD_EXT_TABLE, map)

                println "Inserting child extension for (${rawObj.motherCode}) - result=${cresult.success}, msg: ${cresult.keys}"
            }
        }

        println "Inserting extension for (${rawObj.motherCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertDeathExtension(RawDeath rawObj, Death finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.DEATH_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertChangeHeadExtension(RawChangeHead rawObj, HeadRelationship finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.CHANGE_HEAD_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormDefinition == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, coreFormExt.extFormDefinition, rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertIncompleteVisitExtension(RawIncompleteVisit rawObj, IncompleteVisit finalObj) {
        if (rawObj.extensionForm == null) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.INCOMPLETE_VISIT_FORM)
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

    InstanceMappedValues getExtraInstanceMappedValues(CoreFormExtension coreFormExt, List<String> innerChilds, byte[] formDefBytes, byte[] instanceBytes) {
        def formDef = XFormReader.getFormDefinition(formDefBytes)
        def instanceXml = XFormReader.getFormInstanceFrom(instanceBytes)

        //open HouseholdExt
        //get mapping models, navigate xml (attention to Repeat, CHOICE_LIST, GEOPOINT)

        //def mapValues = new LinkedHashMap<String, Object>()
        def repeatIndexes = new LinkedHashMap<String, Integer>()
        def instanceMapValues = new InstanceMappedValues()
        readExtraElementChildren(coreFormExt, innerChilds, formDef, instanceXml.getRoot(), instanceMapValues, instanceMapValues.mainFormValues, null, 0, repeatIndexes, new String[1])

        return instanceMapValues
    }

    def readElementChildren(CoreFormExtension coreFormExtension, FormDef formDef, TreeElement instanceElement, Map<String, Object> mapValues, String repeatGroup, int repeatLength, LinkedHashMap<String, Integer> repeatIndexes, String[] lastReadedRepeatGroup) {
        for (int i=0; i < instanceElement.numChildren; i++) {
            def insChildElement = instanceElement.getChildAt(i)
            def insChildRef = insChildElement.getRef()
            def questDef = formDef.findQuestionByRef(insChildRef, formDef)
            def defChildElement = formDef.getMainInstance()?.getTemplatePath(insChildRef)

            def formColName = defChildElement?.getName()
            def dataType = defChildElement?.dataType
            def answerData = insChildElement?.value
            def textValue = answerData?.displayText
            def repeatgroup = defChildElement?.repeatable


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

            if (model != null) {
                //println("var: ${formColName}")

                if (textValue != null) { //if the value is null, we dont need to map it
                    Object objValue = getObjectValueByType(model.dbColumnType, textValue)

                    if (model.formRepeatGroup == null) { //not under repeat
                        mapValues.put(model.dbColumnName, objValue)
                    } else {
                        //under a repeat group
                        //habitation_#_rooms_#_type, habitation_#_rooms, habitation
                        //substitution of cardinals
                        def finalColumnName = getFinalColumnName(model, repeatIndexes)

                        mapValues.put(finalColumnName, objValue)
                    }
                }

            } else {
                //throw new Exception("Error")
            }

        }
    }

    def readExtraElementChildren(CoreFormExtension coreFormExtension, List<String> innerChilds, FormDef formDef, TreeElement instanceElement, InstanceMappedValues instanceMappedValues, Map<String, Object> mapValues, String repeatGroup, int repeatLength, LinkedHashMap<String, Integer> repeatIndexes, String[] lastReadedRepeatGroup) {

        for (int i=0; i < instanceElement.numChildren; i++) {
            def insChildElement = instanceElement.getChildAt(i)
            def insChildRef = insChildElement.getRef()
            def questDef = formDef.findQuestionByRef(insChildRef, formDef)
            def defChildElement = formDef.getMainInstance()?.getTemplatePath(insChildRef)

            def formColName = defChildElement?.getName()
            def dataType = defChildElement?.dataType
            def answerData = insChildElement?.value
            def textValue = answerData?.displayText
            def repeatgroup = defChildElement?.repeatable


            /* ignore these variables */
            if (["instanceID", "instanceName"].contains(insChildElement.getName())) continue

            //Handling Groups
            if (dataType == DataType.NULL.value && !repeatgroup && insChildElement.numChildren > 0) {
                readExtraElementChildren(coreFormExtension, innerChilds, formDef, insChildElement, instanceMappedValues, mapValues, repeatGroup, repeatLength, repeatIndexes)
                continue
            }

            //Handling Repeat Groups
            if (dataType == DataType.NULL.value && repeatgroup) {

                if (innerChilds.contains(formColName)) { //a inner repeat - that maps to a separated table

                    def list = instanceMappedValues.childFormValues.containsKey(formColName) ? instanceMappedValues.childFormValues.get(formColName) : new ArrayList<LinkedHashMap<String, Object>>()
                    instanceMappedValues.childFormValues.put(formColName, list)

                    def newMappedValues = new LinkedHashMap<String, Object>()
                    list.add(newMappedValues)

                    readExtraElementChildren(coreFormExtension, innerChilds, formDef, insChildElement, instanceMappedValues, newMappedValues, null, repeatLength, repeatIndexes)

                    continue
                }

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

                readExtraElementChildren(coreFormExtension, innerChilds, formDef, insChildElement, instanceMappedValues, mapValues, repeatModel.dbColumnName, repeatModel.formRepeatLength, repeatIndexes, lastReadedRepeatGroup)

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

            if (model != null) {

                if (textValue != null) { //if the value is null, we dont need to map it
                    Object objValue = getObjectValueByType(model.dbColumnType, textValue)

                    if (model.formRepeatGroup == null) { //not under repeat
                        mapValues.put(model.dbColumnName, objValue)
                    } else {
                        //under a repeat group
                        //habitation_#_rooms_#_type, habitation_#_rooms, habitation
                        //substitution of cardinals
                        def finalColumnName = getFinalColumnName(model, repeatIndexes)

                        mapValues.put(finalColumnName, objValue)
                    }
                }

            } else {
                //throw new Exception("Error")
            }

        }
    }

    Object getObjectValueByType(DatabaseColumnType dbColumnType, String textValue) {
        Object objValue = textValue
        //println("type: ${dbColumnType?.name()}, value: ${textValue}")
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

    /*
     * Used to store readed values of pregnancy_outcome_ext XML instance (contails childs that will be stored in a separated table)
     */
    class InstanceMappedValues {
        LinkedHashMap<String, Object> mainFormValues
        LinkedHashMap<String, List<LinkedHashMap<String, Object>>> childFormValues

        public InstanceMappedValues() {
            this.mainFormValues = new LinkedHashMap<>()
            this.childFormValues = new ArrayList<>()
        }
    }
}
