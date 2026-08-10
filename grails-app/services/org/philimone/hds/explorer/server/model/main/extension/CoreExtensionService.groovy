package org.philimone.hds.explorer.server.model.main.extension

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.enums.CoreForm
import org.philimone.hds.explorer.server.model.enums.extensions.DatabaseColumnType
import org.philimone.hds.explorer.server.model.enums.extensions.FormColumnType
import org.philimone.hds.explorer.server.model.main.*
import org.philimone.hds.forms.model.HForm
import org.philimone.hds.forms.model.parsers.ExcelFormParser
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.time.format.DateTimeFormatter

@Transactional
class CoreExtensionService {

    def coreExtensionDatabaseService

    CoreExtensionDatabaseService.SqlExecutionResult insertRegionExtension(RawRegion rawObj, Region finalObj) {
        return insertExtension(CoreForm.REGION_FORM, finalObj.collectedId, rawObj.extensionForm)
    }
    
    CoreExtensionDatabaseService.SqlExecutionResult insertHouseholdExtension(RawHousehold rawObj, Household finalObj) {
        return insertExtension(CoreForm.HOUSEHOLD_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertVisitExtension(RawVisit rawObj, Visit finalObj) {
        return insertExtension(CoreForm.VISIT_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertEnumerationExtension(RawMemberEnu rawObj, Enumeration finalObj) {
        return insertExtension(CoreForm.MEMBER_ENU_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertMaritalRelationshipExtension(RawMaritalRelationship rawObj, MaritalRelationship finalObj) {
        return insertExtension(CoreForm.MARITAL_RELATIONSHIP_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertExternalInMigrationExtension(RawExternalInMigration rawObj, InMigration finalObj) {
        return insertExtension(CoreForm.INMIGRATION_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertInMigrationExtension(RawInMigration rawObj, InMigration finalObj) {
        return insertExtension(CoreForm.INMIGRATION_FORM, finalObj.collectedId, rawObj.extensionForm)
    }
    
    CoreExtensionDatabaseService.SqlExecutionResult insertOutMigrationExtension(RawOutMigration rawObj, OutMigration finalObj) {
        return insertExtension(CoreForm.OUTMIGRATION_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyRegistrationExtension(RawPregnancyRegistration rawObj, PregnancyRegistration finalObj) {
        return insertExtension(CoreForm.PREGNANCY_REGISTRATION_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyOutcomeExtension(RawPregnancyOutcome rawObj, PregnancyOutcome finalObj) {
        return insertExtension(CoreForm.PREGNANCY_OUTCOME_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertPregnancyVisitExtension(RawPregnancyVisit rawObj, PregnancyVisit finalObj) {
        return insertExtension(CoreForm.PREGNANCY_VISIT_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertDeathExtension(RawDeath rawObj, Death finalObj) {
        return insertExtension(CoreForm.DEATH_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertChangeHeadExtension(RawChangeHead rawObj, HeadRelationship finalObj) {
        return insertExtension(CoreForm.CHANGE_HEAD_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertIncompleteVisitExtension(RawIncompleteVisit rawObj, IncompleteVisit finalObj) {
        return insertExtension(CoreForm.INCOMPLETE_VISIT_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertChangeRegionHeadExtension(RawChangeRegionHead rawObj, RegionHeadRelationship finalObj) {
        return insertExtension(CoreForm.CHANGE_REGION_HEAD_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertHouseholdRelocationExtension(RawHouseholdRelocation rawObj, HouseholdRelocation finalObj) {
        return insertExtension(CoreForm.HOUSEHOLD_RELOCATION_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertHouseholdProxyHeadExtension(RawHouseholdProxyHead rawObj, HouseholdProxyHead finalObj) {
        return insertExtension(CoreForm.CHANGE_PROXY_HEAD_FORM, finalObj.collectedId, rawObj.extensionForm)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertExtension(CoreForm coreForm, String collectedId, byte[] extensionFormXml) {
        if (extensionFormXml == null || extensionFormXml.size() == 0) return null

        def coreFormExt = CoreFormExtension.findByCoreForm(coreForm)
        if (!coreFormExt?.enabled || coreFormExt?.extFormPath == null) return null

        return insertExtensionData(coreFormExt, collectedId, extensionFormXml)
    }

    CoreExtensionDatabaseService.SqlExecutionResult insertExtensionData(CoreFormExtension coreFormExt, String collectedId, byte[] instanceBytes) {
        byte[] formDefBytes = new File(coreFormExt.extFormPath).bytes
        def hForm = new ExcelFormParser(new ByteArrayInputStream(formDefBytes)).getForm()
        def xmlData = getXmlMappedData(instanceBytes, hForm)

        if (xmlData) {
            def result = insertTableRecordRecursive(coreFormExt, coreFormExt.extFormId, xmlData, null, collectedId, 0)

            if (result != null && !result.success) {
                // SOMETHING FAILED in the tree - Wipe everything for this collectedId
                cleanupExtensionData(coreFormExt, collectedId)
            }

            return result
        }
        return null
    }

    private CoreExtensionDatabaseService.SqlExecutionResult insertTableRecordRecursive(CoreFormExtension coreFormExt, String tableName, Map<String, Object> xmlData, Long parentId, String collectedId, int ordinalNumber) {
        def mapValues = new LinkedHashMap<String, Object>()
        def childRepeats = new LinkedHashMap<String, List<Map<String, Object>>>()

        // 1. Map columns for the current table
        mapXmlToTableValues(coreFormExt, tableName, xmlData, mapValues, childRepeats)

        // Add system columns
        mapValues.put(ExtensionDatabaseColumns.COLLECTED_ID, collectedId)
        if (parentId != null) {
            def parentIdModel = CoreFormExtensionModel.findByCoreFormAndDbColumnTableAndFormColumnName(coreFormExt, tableName, ExtensionDatabaseColumns.FORM_PARENT_ID)
            if (parentIdModel) {
                mapValues.put(parentIdModel.dbColumnName, parentId)
            }
        }
        if (ordinalNumber > 0) {
            mapValues.put(ExtensionDatabaseColumns.ORDINAL_NUMBER, ordinalNumber)
        }

        // 2. Insert current record
        def result = coreExtensionDatabaseService.executeSqlInsert(tableName, mapValues)
        if (result != null && result.success) {
            def currentId = result.keys?.first() as Long

            // 3. Process child repeats recursively
            for (def entry : childRepeats){
                def repeatName = entry.key
                def instances = entry.value
                def repeatModel = CoreFormExtensionModel.findByCoreFormAndFormColumnNameAndFormColumnType(coreFormExt, repeatName, FormColumnType.REPEAT_GROUP)

                if (repeatModel && repeatModel.repeatPerTable) {
                    def index = -1
                    for (def instance : instances) {
                        def instanceData = instance
                        index += 1
                        def innerResult = insertTableRecordRecursive(coreFormExt, repeatModel.dbColumnTable, (Map<String, Object>) instanceData, currentId, collectedId, index + 1)

                        if (!innerResult.success) {
                            // Propagate error up and stop processing
                            result.success = false
                            result.errorMessage = innerResult.errorMessage
                            return result
                        }
                    }
                }
            }
        }

        return result
    }

    private void cleanupExtensionData(CoreFormExtension coreFormExt, String collectedId) {
        def models = CoreFormExtensionModel.findAllByCoreForm(coreFormExt)
        def tables = models.collect { it.dbColumnTable }.unique()
        tables.each { tableName ->
            coreExtensionDatabaseService.executeSqlDeleteByCollectedId(tableName, collectedId)
        }
        // Also clean the main table
        coreExtensionDatabaseService.executeSqlDeleteByCollectedId(coreFormExt.extFormId, collectedId)
    }

    private void mapXmlToTableValues(CoreFormExtension coreFormExt, String tableName, Map<String, Object> xmlData, Map<String, Object> mapValues, Map<String, List<Map<String, Object>>> childRepeats) {
        xmlData.each { key, value ->
            if (value instanceof List) {
                childRepeats.put(key, (List<Map<String, Object>>) value)
            } else if (value instanceof Map) {
                mapXmlToTableValues(coreFormExt, tableName, (Map<String, Object>) value, mapValues, childRepeats)
            } else {
                processNodeValueForTable(coreFormExt, tableName, key, value as String, mapValues)
            }
        }
    }

    private void processNodeValueForTable(CoreFormExtension coreFormExtension, String tableName, String key, String textValue, Map<String, Object> mapValues) {

        def multiModels = CoreFormExtensionModel.findAllByCoreFormAndDbColumnTableAndFormColumnNameAndFormColumnType(coreFormExtension, tableName, key, FormColumnType.MULTIPLE_ITEMS)
        if (multiModels) {
            def choices = textValue.split(",")
            multiModels.each { model ->
                if (choices.contains(model.formChoiceValue)) {
                    mapValues.put(model.dbColumnName, model.formChoiceValue)
                }
            }
            return
        }

        def model = CoreFormExtensionModel.findByCoreFormAndDbColumnTableAndFormColumnName(coreFormExtension, tableName, key)
        if (model && model.formColumnType != FormColumnType.REPEAT_GROUP) {
            mapValues.put(model.dbColumnName, getObjectValueByType(model.dbColumnType, textValue))
        }
    }

    private Map<String, Object> getXmlMappedData(byte[] instanceBytes, HForm form) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
            DocumentBuilder builder = factory.newDocumentBuilder()
            Document doc = builder.parse(new ByteArrayInputStream(instanceBytes))
            Node node = doc.getElementsByTagName(form.getFormId()).item(0)
            if (node != null) {
                return readNodesRecursive(node, form)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return new LinkedHashMap<String, Object>()
    }

    private Map<String, Object> readNodesRecursive(Node parentNode, HForm form) {
        Map<String, Object> map = new LinkedHashMap<>()
        NodeList nodes = parentNode.getChildNodes()
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i)
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = n.getNodeName()
                if (form.isRepeatColumnName(nodeName)) {
                    // Handle Repeat Group
                    List<Map<String, Object>> repeatList = (List<Map<String, Object>>) map.get(nodeName)
                    if (repeatList == null) {
                        repeatList = new ArrayList<Map<String, Object>>()
                        map.put(nodeName, repeatList)
                    }

                    // Each child of a repeat node is an instance of the repeat
                    NodeList instances = n.getChildNodes()
                    for (int j = 0; j < instances.getLength(); j++) {
                        Node instanceNode = instances.item(j)
                        if (instanceNode.getNodeType() == Node.ELEMENT_NODE) {
                            repeatList.add(readNodesRecursive(instanceNode, form))
                        }
                    }
                } else if (hasElementChildren(n)) {
                    // Handle Group (nested elements but not a repeat)
                    map.put(nodeName, readNodesRecursive(n, form))
                } else {
                    // Simple leaf node
                    map.put(nodeName, n.getTextContent()?.trim() ?: "")
                }
            }
        }
        return map
    }

    private boolean hasElementChildren(Node node) {
        NodeList children = node.getChildNodes()
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true
            }
        }
        return false
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
}
