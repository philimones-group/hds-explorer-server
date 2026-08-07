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
import org.philimone.hds.forms.model.HForm
import org.philimone.hds.forms.model.RepeatObject
import org.philimone.hds.forms.model.parsers.ExcelFormParser
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.time.format.DateTimeFormatter

@Transactional
class CoreExtensionService {

    static final PREGNANCY_CHILD_EXT_TABLE = "pregnancy_child_ext"
    static final PREGNANCY_VISIT_CHILD_EXT_TABLE = "pregnancy_visit_child_ext"

    def coreExtensionDatabaseService

    CoreExtensionDatabaseService.SqlExecutionResult insertRegionExtension(RawRegion rawObj, Region finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.REGION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.regionCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }
    
    CoreExtensionDatabaseService.SqlExecutionResult insertHouseholdExtension(RawHousehold rawObj, Household finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.HOUSEHOLD_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertVisitExtension(RawVisit rawObj, Visit finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        if (rawObj.extensionForm.length == 0) { println("empty extensionForm"); return null }

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.VISIT_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertEnumerationExtension(RawMemberEnu rawObj, Enumeration finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.MEMBER_ENU_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.code}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertMaritalRelationshipExtension(RawMaritalRelationship rawObj, MaritalRelationship finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.MARITAL_RELATIONSHIP_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberA}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertExternalInMigrationExtension(RawExternalInMigration rawObj, InMigration finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.INMIGRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertInMigrationExtension(RawInMigration rawObj, InMigration finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.INMIGRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }
    
    CoreExtensionDatabaseService.SqlExecutionResult insertOutMigrationExtension(RawOutMigration rawObj, OutMigration finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.OUTMIGRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyRegistrationExtension(RawPregnancyRegistration rawObj, PregnancyRegistration finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.PREGNANCY_REGISTRATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.motherCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyOutcomeExtension(RawPregnancyOutcome rawObj, PregnancyOutcome finalObj) {

        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.PREGNANCY_OUTCOME_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map - in this form we have a special repeat (childs) -> that will be sent to a separated table
        def instanceMappedValues = getExtraInstanceMappedValues(coreFormExt, ["childs"], new File(coreFormExt.extFormPath), rawObj.extensionForm)

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

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyVisitExtension(RawPregnancyVisit rawObj, PregnancyVisit finalObj) {

        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.PREGNANCY_VISIT_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map - in this form we have a special repeat (childs) -> that will be sent to a separated table
        def instanceMappedValues = getExtraInstanceMappedValues(coreFormExt, ["childs"], new File(coreFormExt.extFormPath), rawObj.extensionForm)

        //insert into pregnancy_outcome_ext
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, instanceMappedValues.mainFormValues)

        //insert into pregnancy_child_ext
        if (result != null && result.success) {
            def id = result.keys?.first()

            instanceMappedValues.childFormValues.get("childs").each {map ->
                //insert secondary key and others
                map.put("collected_id", finalObj.collectedId)
                map.put("pregnancy_visit_ext_id", id)

                def cresult = coreExtensionDatabaseService.executeSqlInsert(PREGNANCY_VISIT_CHILD_EXT_TABLE, map)

                println "Inserting child extension for (${rawObj.motherCode}) - result=${cresult.success}, msg: ${cresult.keys}"
            }
        }

        println "Inserting extension for (${rawObj.motherCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertDeathExtension(RawDeath rawObj, Death finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.DEATH_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.memberCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertChangeHeadExtension(RawChangeHead rawObj, HeadRelationship finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.CHANGE_HEAD_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertIncompleteVisitExtension(RawIncompleteVisit rawObj, IncompleteVisit finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.INCOMPLETE_VISIT_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertChangeRegionHeadExtension(RawChangeRegionHead rawObj, RegionHeadRelationship finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.CHANGE_REGION_HEAD_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for (${rawObj.regionCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertHouseholdRelocationExtension(RawHouseholdRelocation rawObj, HouseholdRelocation finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.HOUSEHOLD_RELOCATION_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for hhr(${rawObj.originCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertHouseholdProxyHeadExtension(RawHouseholdProxyHead rawObj, HouseholdProxyHead finalObj) {
        if (rawObj.extensionForm == null || rawObj?.extensionForm?.size()==0) return null

        //get form extensions
        def coreFormExt = CoreFormExtension.findByCoreForm(CoreForm.CHANGE_PROXY_HEAD_FORM)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        //read xml data to map
        def mapInstanceValues = getInstanceMappedValues(coreFormExt, new File(coreFormExt.extFormPath), rawObj.extensionForm)
        //insert into
        def result = coreExtensionDatabaseService.executeSqlInsert(coreFormExt.extFormId, mapInstanceValues)

        println "Inserting extension for hhr(${rawObj.householdCode}) - result=${result.success}, msg: ${result.errorMessage}"

        return result
    }

    LinkedHashMap<String, Object> getInstanceMappedValues(CoreFormExtension coreFormExt, File formDefFile , byte[] instanceBytes) {
        def mapValues = new LinkedHashMap<String, Object>()
        byte[] formDefBytes = formDefFile.bytes
        def hForm = new ExcelFormParser(new ByteArrayInputStream(formDefBytes)).getForm()
        def xmlData = getXmlMappedData(instanceBytes, hForm)

        if (xmlData) {
            def repeatIndexes = new LinkedHashMap<String, Integer>()
            readElementChildren(coreFormExt, xmlData, mapValues, repeatIndexes, new String[1])
        }

        return mapValues
    }

    InstanceMappedValues getExtraInstanceMappedValues(CoreFormExtension coreFormExt, List<String> innerChilds, File formDefFile, byte[] instanceBytes) {
        def instanceMapValues = new InstanceMappedValues()
        byte[] formDefBytes = formDefFile.bytes
        def hForm = new ExcelFormParser(new ByteArrayInputStream(formDefBytes)).getForm()
        def xmlData = getXmlMappedData(instanceBytes, hForm)

        if (xmlData) {
            def repeatIndexes = new LinkedHashMap<String, Integer>()
            readExtraElementChildren(coreFormExt, innerChilds, xmlData, instanceMapValues, instanceMapValues.mainFormValues, repeatIndexes, new String[1])
        }

        return instanceMapValues
    }

    private Map<String, Object> getXmlMappedData(byte[] instanceBytes, HForm form) {
        Map<String, Object> map = new LinkedHashMap<>()
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
            DocumentBuilder builder = factory.newDocumentBuilder()
            Document doc = builder.parse(new ByteArrayInputStream(instanceBytes))
            Node node = doc.getElementsByTagName(form.getFormId()).item(0)
            if (node != null) {
                readMainNodes(node, map, form)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return map
    }

    private void readMainNodes(Node node, Map<String, Object> map, HForm form) {
        NodeList nodes = node.getChildNodes()
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i)
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (n.hasChildNodes() && form.isRepeatColumnName(n.getNodeName())) {
                    String repeatNodeName = n.getNodeName()
                    NodeList repeatChilds = n.getChildNodes()
                    RepeatObject newRepObjList = new RepeatObject()
                    for (int ri = 0; ri < repeatChilds.getLength(); ri++) {
                        Node nodeRepObj = repeatChilds.item(ri)
                        if (nodeRepObj.getNodeType() == Node.ELEMENT_NODE) {
                            NodeList childElements = nodeRepObj.getChildNodes()
                            Map<String, String> obj = newRepObjList.createNewObject()
                            for (int j = 0; j < childElements.getLength(); j++) {
                                Node elementNode = childElements.item(j)
                                if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                                    obj.put(elementNode.getNodeName(), elementNode.getTextContent() ?: "")
                                }
                            }
                        }
                    }
                    map.put(repeatNodeName, newRepObjList)
                } else {
                    map.put(n.getNodeName(), n.getTextContent() ?: "")
                }
            }
        }
    }

    private void readElementChildren(CoreFormExtension coreFormExtension, Map<String, Object> xmlData, Map<String, Object> mapValues, LinkedHashMap<String, Integer> repeatIndexes, String[] lastReadedRepeatGroup) {
        xmlData.each { key, value ->
            if (["instanceID", "instanceName"].contains(key)) return

            if (value instanceof RepeatObject) {
                def repeatModel = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnType(coreFormExtension, key, FormColumnType.REPEAT_GROUP)
                if (repeatModel) {
                    if (lastReadedRepeatGroup[0] != null) {
                        if (!lastReadedRepeatGroup[0].equals(repeatModel.dbColumnName)) {
                            if (!lastReadedRepeatGroup[0].equals(repeatModel.formRepeatGroup)) {
                                repeatIndexes.remove(lastReadedRepeatGroup[0])
                            }
                        }
                    }
                    lastReadedRepeatGroup[0] = repeatModel.dbColumnName

                    value.getList().eachWithIndex { Map<String, String> itemMap, int index ->
                        def currentRepeatIndexes = new LinkedHashMap(repeatIndexes)
                        currentRepeatIndexes.put(repeatModel.dbColumnName, index + 1)
                        readElementChildren(coreFormExtension, itemMap, mapValues, currentRepeatIndexes, lastReadedRepeatGroup)
                    }
                }
            } else {
                processNodeValue(coreFormExtension, key, value as String, mapValues, repeatIndexes)
            }
        }
    }

    private void readExtraElementChildren(CoreFormExtension coreFormExtension, List<String> innerChilds, Map<String, Object> xmlData, InstanceMappedValues instanceMappedValues, Map<String, Object> mapValues, LinkedHashMap<String, Integer> repeatIndexes, String[] lastReadedRepeatGroup) {
        xmlData.each { key, value ->
            if (["instanceID", "instanceName"].contains(key)) return

            if (value instanceof RepeatObject) {
                if (innerChilds.contains(key)) {
                    def list = instanceMappedValues.childFormValues.computeIfAbsent(key, { k -> new ArrayList<LinkedHashMap<String, Object>>() })
                    value.getList().each { Map<String, String> itemMap ->
                        def newMappedValues = new LinkedHashMap<String, Object>()
                        list.add(newMappedValues)
                        readExtraElementChildren(coreFormExtension, innerChilds, itemMap, instanceMappedValues, newMappedValues, repeatIndexes, lastReadedRepeatGroup)
                    }
                    return
                }

                def repeatModel = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnType(coreFormExtension, key, FormColumnType.REPEAT_GROUP)
                if (repeatModel) {
                    if (lastReadedRepeatGroup[0] != null) {
                        if (!lastReadedRepeatGroup[0].equals(repeatModel.dbColumnName)) {
                            if (!lastReadedRepeatGroup[0].equals(repeatModel.formRepeatGroup)) {
                                repeatIndexes.remove(lastReadedRepeatGroup[0])
                            }
                        }
                    }
                    lastReadedRepeatGroup[0] = repeatModel.dbColumnName

                    value.getList().eachWithIndex { Map<String, String> itemMap, int index ->
                        def currentRepeatIndexes = new LinkedHashMap(repeatIndexes)
                        currentRepeatIndexes.put(repeatModel.dbColumnName, index + 1)
                        readExtraElementChildren(coreFormExtension, innerChilds, itemMap, instanceMappedValues, mapValues, currentRepeatIndexes, lastReadedRepeatGroup)
                    }
                }
            } else {
                processNodeValue(coreFormExtension, key, value as String, mapValues, repeatIndexes)
            }
        }
    }

    private void processNodeValue(CoreFormExtension coreFormExtension, String key, String textValue, Map<String, Object> mapValues, LinkedHashMap<String, Integer> repeatIndexes) {

        def multiModels = CoreFormExtensionModel.findAllByCoreFormAndFormColumnNameAndFormColumnType(coreFormExtension, key, FormColumnType.MULTIPLE_ITEMS)
        if (multiModels) {
            def choices = textValue.split(",")
            multiModels.each { model ->
                if (choices.contains(model.formChoiceValue)) {
                    def finalColName = getFinalColumnName(model, repeatIndexes)
                    mapValues.put(finalColName, model.formChoiceValue)
                }
            }
            return
        }

        if (key.contains("_")) {
            def suffix = key.substring(key.lastIndexOf("_") + 1)
            if (["lat", "lng", "alt", "acc"].contains(suffix)) {
                def baseName = key.substring(0, key.lastIndexOf("_"))
                def gpsModel = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnTypeAndFormChoiceValue(coreFormExtension, baseName, FormColumnType.GEOPOINT, suffix)
                if (gpsModel) {
                    def finalColName = getFinalColumnName(gpsModel, repeatIndexes)
                    mapValues.put(finalColName, Double.parseDouble(textValue))
                    return
                }
            }
        }

        def model = CoreFormExtensionModel.findByCoreFormAndFormColumnName(coreFormExtension, key)
        if (model && model.formColumnType != FormColumnType.REPEAT_GROUP) {
            def finalColName = getFinalColumnName(model, repeatIndexes)
            mapValues.put(finalColName, getObjectValueByType(model.dbColumnType, textValue))
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
            this.childFormValues = new LinkedHashMap<>()
        }
    }
}
