package net.betainteractive.io.odk.util

import org.javarosa.core.model.FormDef
import org.javarosa.core.model.instance.FormInstance
import org.javarosa.core.model.instance.TreeElement
import org.javarosa.xform.util.XFormUtils
import org.javarosa.xml.ElementParser
import org.javarosa.xml.TreeElementParser
import org.javarosa.xml.util.InvalidStructureException
import org.javarosa.xml.util.UnfullfilledRequirementsException
import org.kxml2.io.KXmlParser
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
        try (InputStream inputStream = new ByteArrayInputStream(instanceXmlBytes)) {
            KXmlParser xmlParser = ElementParser.instantiateParser(inputStream);
            TreeElementParser treeElementParser = new TreeElementParser(xmlParser, 0, null);
            return new FormInstance(treeElementParser.parse());
        }
    }

}