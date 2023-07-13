package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.json.JConstant

@Transactional
class CoreFormColumnOptionsService {

    def generalUtilitiesService
    def errorMessageService

    List<JConstant> getCustomOptionsForms() {
        def forms = CoreFormColumnMap.list().collect { it.formName }.unique()
        def list = new ArrayList<JConstant>()

        forms.each {
            def code = CoreFormExtension.findByFormId(it)?.formName

            if (it.equalsIgnoreCase("rawExternalInMigration")) {
                code = "coreFormExtension.extinmigration.label"
            }

            list << new JConstant(value: it, name: generalUtilitiesService.getMessageWeb(code))
        }

        return list
    }

    List<String> getCustomOptionsColumns(String formName) {
        def list = CoreFormColumnMap.findAllByFormName(formName)?.collect { it.columnName }
        return list
    }

    List<CoreFormColumnOptions> getOptions(String columnName) {
        return CoreFormColumnOptions.findAllByColumnName(columnName)
    }

    UpdateResult updateOptions(String editedColumnName, String id, String newValue) {
        //get column that is being customized CoreFormColumnOptions.columnName
        def columnOption = CoreFormColumnOptions.findById(id)
        def columnName = columnOption?.columnName

        //check if newValue is blank
        if (StringUtil.isBlank(newValue)) {
            return new UpdateResult(result: UpdateResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.notblank.label"))
        }

        //check if newValue is duplicated for that columnName
        def resultCountOpts = CoreFormColumnOptions.executeQuery("select count(c.id) from CoreFormColumnOptions c where c.columnName = ?0 and c."+editedColumnName+" = ?1", [columnName, newValue])

        println "result count: ${resultCountOpts}"

        if (resultCountOpts.size() > 0 && resultCountOpts[0] > 0) {
            return new UpdateResult(result: UpdateResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.option.unique.label"))
        }

        return new UpdateResult(result: UpdateResult.Result.SUCCESS.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.updated.label"))
    }

    CoreFormColumnOptions createCustomOptions(String id) {
        def otherOpt = CoreFormColumnOptions.get(id)
        def maxOpt = CoreFormColumnOptions.findAllByColumnName(otherOpt.columnName).max { it.ordinal }
        def optionValue = createSampleOptionValue(otherOpt.columnName)
        def optionLabel = createSampleOptionLabel(otherOpt.columnName)

        def newOpt = new CoreFormColumnOptions(columnName: otherOpt.columnName, ordinal: maxOpt ? maxOpt.ordinal + 1 : 1, optionValue: optionValue, optionLabel: optionLabel, optionLabelCode: "")
        newOpt = newOpt.save(flush:true)

        println "${newOpt?.errors}"

        return newOpt
    }

    UpdateResult deleteCustomOptions(String id) {
        def opt = CoreFormColumnOptions.get(id)
        //checks
        opt.delete(flush: true)
        if (!opt.hasErrors()) {
            return new UpdateResult(result: UpdateResult.Result.SUCCESS, message:  generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.deleted.label"))
        } else {
            return new UpdateResult(result: UpdateResult.Result.ERROR.name(), message: "" + errorMessageService.formatErrors(opt))
        }
    }

    String createSampleOptionValue(String columnName) {
        def maxOpt = CoreFormColumnOptions.findAllByColumnNameAndOptionValueLike(columnName, "NEW_VALUE_%").max { it.optionValue }
        def newvalue = "NEW_VALUE_"

        println "maxvalue = ${maxOpt?.optionValue}"

        if (maxOpt == null) {
            newvalue = newvalue + String.format("%02d", 1)
        } else {
            def nstr = maxOpt?.optionValue.replace(newvalue, "")
            newvalue = newvalue + String.format("%02d", Integer.parseInt(nstr)+1)
        }

        return newvalue
    }

    String createSampleOptionLabel(String columnName) {
        def maxOpt = CoreFormColumnOptions.findAllByColumnNameAndOptionLabelLike(columnName, "New Value Label %").max { it.optionLabel }
        def newlabel = "New Value Label "

        println "maxvalue = ${maxOpt?.optionLabel}"

        if (maxOpt == null) {
            newlabel = newlabel + String.format("%02d", 1)
        } else {
            def nstr = maxOpt?.optionLabel.replace(newlabel, "")
            newlabel = newlabel + String.format("%02d", Integer.parseInt(nstr)+1)
        }

        return newlabel
    }

    class UpdateResult {
        enum Result {
            ERROR, SUCCESS
        }

        String result = Result.ERROR.name()
        String message = ""
    }
}
