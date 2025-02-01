package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.main.CoreFormColumnOptions
import org.philimone.hds.explorer.server.model.main.CoreFormColumnOptionsService
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorIncrementalRule

@Transactional
class SettingsService {

    def applicationParamService
    def coreFormColumnOptionsService

    def getCodeGeneratorsIncrementalRules() {
        def incs = CodeGeneratorIncrementalRule.values()
        def list = new ArrayList<JConstant>()
        incs.each {
            list << new JConstant(name: it.name, value: it.code)
        }

        return list

    }

    boolean getRegionHeadSupport() {
        def value = applicationParamService.getBooleanValue(Codes.PARAMS_SYSTEM_REGION_HEAD_SUPPORT)

        if (value != null) {
            Codes.SYSTEM_REGION_HEAD_SUPPORT = value
        } else {
            Codes.SYSTEM_REGION_HEAD_SUPPORT = false
        }

        return value
    }

    boolean isRegionHeadSupported(Region region) {
        def sysRegionHeadSupport = getRegionHeadSupport()
        def regionSupportsHead = applicationParamService.getBooleanValue(region.hierarchyLevel.getHeadParamName())

        return sysRegionHeadSupport && regionSupportsHead;
    }

    void afterUpdateSystemLanguage(){
        coreFormColumnOptionsService.updateColumnOptionLabels()
    }
}
