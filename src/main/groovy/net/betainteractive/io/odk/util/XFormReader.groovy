package net.betainteractive.io.odk.util

import org.javarosa.core.model.DataType
import org.javarosa.core.model.FormDef
import org.javarosa.core.model.GroupDef
import org.javarosa.core.model.IFormElement
import org.javarosa.core.model.instance.FormInstance
import org.javarosa.core.model.instance.TreeElement
import org.javarosa.core.model.instance.TreeReference
import org.javarosa.xform.util.XFormUtils
import org.javarosa.xml.ElementParser
import org.javarosa.xml.TreeElementParser
import org.javarosa.xml.util.InvalidStructureException
import org.javarosa.xml.util.UnfullfilledRequirementsException
import org.kxml2.io.KXmlParser
import org.philimone.hds.explorer.server.model.enums.extensions.FormColumnType
import org.xmlpull.v1.XmlPullParserException

class XFormReader {

    static FormDef getFormDefinition(InputStream xformInputstream) {
        return XFormUtils.getFormFromInputStream(xformInputstream)
    }

    static FormDef getFormDefinition(byte[] xformBytes) {
        def xformInputstream = new ByteArrayInputStream(xformBytes)
        return XFormUtils.getFormFromInputStream(xformInputstream)
    }

    static FormDef getFormDefinition(String xformText) {
        return getFormDefinition(xformText.getBytes())
    }

    static String getFormId(FormDef formDef) {
        def mainInstance = formDef?.getMainInstance()
        return mainInstance?.root?.name
    }

    static String getFormId(byte[] xformBytes) {
        def formDef = getFormDefinition(xformBytes)
        def mainInstance = formDef?.getMainInstance()
        return mainInstance?.root?.getAttributeValue(null, "id")
    }

    static FormInstance getFormInstanceFrom(byte[] instanceXmlBytes) throws IOException, InvalidStructureException, XmlPullParserException, UnfullfilledRequirementsException {

        if (instanceXmlBytes == null || instanceXmlBytes.length == 0) return null

        try {

            InputStream inputStream = new ByteArrayInputStream(instanceXmlBytes)

            KXmlParser xmlParser = ElementParser.instantiateParser(inputStream);
            TreeElementParser treeElementParser = new TreeElementParser(xmlParser, 0, null);

            return new FormInstance(treeElementParser.parse());
        } catch (Exception ex) {
            ex.printStackTrace()
        }

        return null
    }

    static Map<String, FormColumnType> getFormColumns(byte[] xformBytes){
        def columnsMap = new LinkedHashMap<String, FormColumnType>()
        def formDef = getFormDefinition(xformBytes)
        def formInstance = formDef.getMainInstance()
        def rootElement = formInstance.getRoot()
        def columnIndex = 0

        //iterate treeElement - match the types and create the model
        def repeatCountCols = findRepeatCountColumns(formDef)

        getColumnsByProcessingChildren(columnsMap, formDef, rootElement, columnIndex, repeatCountCols, true)

        return columnsMap
    }

    private static int getColumnsByProcessingChildren(Map<String, FormColumnType> columnsMap, FormDef formDef, TreeElement rootElement, int columnIndex, List<TreeReference> repeatCountColumns, boolean ignoreSpecialColumns) {
        for (int i=0; i < rootElement.numChildren; i++) {
            def element = rootElement.getChildAt(i)
            def questDef = FormDef.findQuestionByRef(element.getRef(), formDef)

            def forname = element.getName()
            def fortype = element.getDataType()
            def repeatgroup = element.repeatable
            def hasChoices = questDef?.getNumChoices() > 0
            def choices = hasChoices ? questDef?.choices : null

            if (["instanceID", "instanceName"].contains(element.getName())) continue /* ignore these variables */

            if (fortype == DataType.NULL.value && !repeatgroup && element.numChildren > 0) { //Its a group
                getColumnsByProcessingChildren(columnsMap, formDef, element, columnIndex, repeatCountColumns, ignoreSpecialColumns)
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
                //create repeat datamodel

                //save repeat group
                if (!ignoreSpecialColumns){
                    columnsMap.put("#${forname}", FormColumnType.REPEAT_GROUP)
                }

                //process the repeat childs
                columnIndex = getColumnsByProcessingChildren(columnsMap, formDef, element, columnIndex, repeatCountColumns, ignoreSpecialColumns)
                continue
            }

            if (fortype == DataType.MULTIPLE_ITEMS.value) {
                //Create multiple choice data model answers
                if (hasChoices) {
                    columnsMap.put(forname, FormColumnType.MULTIPLE_ITEMS)
                }

                continue
            }

            if (fortype == DataType.GEOPOINT.value && !ignoreSpecialColumns) {
                //create gps variables
                columnsMap.put(forname, FormColumnType.GEOPOINT)
                continue
            }

            //others types
            columnsMap.put(forname, FormColumnType.getFrom(fortype))
        }

        return columnIndex
    }

    private static List<TreeReference> findRepeatCountColumns(FormDef formDef) {
        def list = new ArrayList<TreeReference>()

        if (formDef.children)
            for (int i = 0; i < formDef.children.size(); i++) {
                findRepeatCountColumns(formDef.children.get(i), list);
            }

        return list
    }

    private static def findRepeatCountColumns(IFormElement fe, List<TreeReference> list) {
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

}