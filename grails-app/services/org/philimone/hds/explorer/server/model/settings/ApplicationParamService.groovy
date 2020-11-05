package org.philimone.hds.explorer.server.model.settings

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.enums.settings.ApplicationParamType
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Transactional
class ApplicationParamService {

    def ApplicationParam addApplicationParam(ApplicationParam param) {
        param.save(flush: true)
    }

    def ApplicationParam updateApplicationParam(ApplicationParam param){
        param.save(flush: true)
    }

    def ApplicationParam addParam(String name, Integer value) {
        def param = new ApplicationParam(name: name, type: ApplicationParamType.INTEGER, value: value.toString())
        addApplicationParam(param)
    }

    def ApplicationParam addParam(String name, String value) {
        def param = new ApplicationParam(name: name, type: ApplicationParamType.STRING, value: value)
        addApplicationParam(param)
    }

    def ApplicationParam addParam(String name, Boolean value) {
        def param = new ApplicationParam(name: name, type: ApplicationParamType.BOOLEAN, value: value)
        addApplicationParam(param)
    }

    def ApplicationParam addParam(String name, Date value) {
        def param = new ApplicationParam(name: name, type: ApplicationParamType.DATE, value: StringUtil.format(value, "yyyy-MM-dd HH:mm:ss" ))
        addApplicationParam(param)
    }

    Integer getIntegerValue(String paramName){
        def param = ApplicationParam.findByName(paramName)

        if (param != null && param.type == ApplicationParamType.INTEGER){
            try {
                return Integer.parseInt(param.value)
            }catch (Exception ex){ }
        }

        return null
    }

    Boolean getBooleanValue(String paramName){
        def param = ApplicationParam.findByName(paramName)

        if (param != null && param.type == ApplicationParamType.BOOLEAN){
            try {
                return Boolean.parseBoolean(param.value)
            }catch (Exception ex){ }
        }

        return null
    }

    Date getDateValue(String paramName){
        def param = ApplicationParam.findByName(paramName)
        if (param != null && param.type == ApplicationParamType.DATE){
            try {
                return StringUtil.toDate(param.value, "yyyy-MM-dd HH:mm:ss")
            }catch (Exception ex){ }
        }
        return null
    }

    String getStringValue(String paramName){
        def param = ApplicationParam.findByName(paramName)

        if (param != null && param.type == ApplicationParamType.INTEGER){
            return param.value
        }

        return null
    }
}
