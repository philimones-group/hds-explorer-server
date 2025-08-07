package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorIncrementalRule

@Transactional
class SettingsService {

    def applicationParamService
    def coreFormColumnOptionsService
    def generalUtilitiesService

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

    boolean getVisitGpsRequired() {
        def value = applicationParamService.getBooleanValue(Codes.PARAMS_SYSTEM_VISIT_GPS_REQUIRED)

        if (value != null) {
            Codes.SYSTEM_VISIT_GPS_REQUIRED = value
        } else {
            Codes.SYSTEM_VISIT_GPS_REQUIRED = false
        }

        return value
    }

    JConstant getCurrentSupportedCalendar() {
        def calendar1 = new JConstant(value: Codes.SYSTEM_SUPPORTED_CALENDAR_GREGORIAN, name: "settings.parameters.calendar.gregorian.label")
        def calendar2 = new JConstant(value: Codes.SYSTEM_SUPPORTED_CALENDAR_ETHIOPIAN, name: "settings.parameters.calendar.ethiopian.label")

        def selectedEthiopian = applicationParamService.getBooleanValue(Codes.PARAMS_SYSTEM_USE_ETHIOPIAN_CALENDAR)

        return selectedEthiopian ? calendar2 : calendar1
    }

    void afterUpdateSystemLanguage(){
        coreFormColumnOptionsService.updateColumnOptionLabels()
    }

    void afterUpdateSystemCalendar(){

    }
}
