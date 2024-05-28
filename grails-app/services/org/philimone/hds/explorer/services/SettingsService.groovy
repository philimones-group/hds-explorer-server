package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorIncrementalRule

@Transactional
class SettingsService {

    def getCodeGeneratorsIncrementalRules() {
        def incs = CodeGeneratorIncrementalRule.values()
        def list = new ArrayList<JConstant>()
        incs.each {
            list << new JConstant(name: it.name, value: it.code)
        }

        return list

    }
}
