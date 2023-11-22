package net.betainteractive.io.odk.util

import org.javarosa.core.model.FormDef
import org.javarosa.xform.util.XFormUtils

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
        return mainInstance?.root?.name
    }

}