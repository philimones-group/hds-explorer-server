package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.ChangeHeadReason
import org.philimone.hds.explorer.server.model.enums.CoreForm
import org.philimone.hds.explorer.server.model.enums.DeathCause
import org.philimone.hds.explorer.server.model.enums.DeathPlace
import org.philimone.hds.explorer.server.model.enums.EducationType
import org.philimone.hds.explorer.server.model.enums.ExtInmigrationReason
import org.philimone.hds.explorer.server.model.enums.IncompleteVisitReason
import org.philimone.hds.explorer.server.model.enums.InmigrationReason
import org.philimone.hds.explorer.server.model.enums.NoVisitReason
import org.philimone.hds.explorer.server.model.enums.OutmigrationReason
import org.philimone.hds.explorer.server.model.enums.ReligionType
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem
import org.philimone.hds.explorer.server.model.enums.VisitReason
import org.philimone.hds.explorer.server.model.enums.VisitRespondentRelationship
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.json.JActionResult

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
        def list = CoreFormColumnMap.findAllByFormName(formName)?.collect { new JConstant(name: it.columnName, value: it.columnCode) }
        return list
    }

    List<CoreFormColumnOptions> getOptions(String columnName) {
        return CoreFormColumnOptions.findAllByColumnCode(columnName)
    }

    JActionResult updateOptions(String editedColumnName, String id, String newValue) {
        //get column that is being customized CoreFormColumnOptions.columnName
        def columnOption = CoreFormColumnOptions.findById(id)
        def columnName = columnOption?.columnName

        //check readonly
        if (columnOption != null && columnOption.readonly) {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.readonly.label"))
        }

        //check if newValue is blank
        if (StringUtil.isBlank(newValue)) {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.notblank.label"))
        }

        //check if newValue is duplicated for that columnName
        /*
        def resultCountOpts = CoreFormColumnOptions.executeQuery("select count(c.id) from CoreFormColumnOptions c where c.columnName = ?0 and c."+editedColumnName+" = ?1", [columnName, newValue])

        println "result count: ${resultCountOpts}"

        if (resultCountOpts.size() > 0 && resultCountOpts[0] > 0) {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.option.unique.label"))
        }*/

        //update the record
        //CoreFormColumnOptions.executeUpdate("update CoreFormColumnOptions set " + editedColumnName + " = ?0 where id = ?1", [newValue, id])
        columnOption."${editedColumnName}" = newValue
        columnOption.save(flush: true)

        if (columnOption.hasErrors()) {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: errorMessageService.getRawMessagesText(columnOption))
        }

        return new JActionResult(result: JActionResult.Result.SUCCESS.name(), message: generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.updated.label"))
    }

    JActionResult createCustomOptions(String id) {
        def otherOpt = CoreFormColumnOptions.get(id)
        def maxOpt = CoreFormColumnOptions.findAllByColumnName(otherOpt.columnName).max { it.ordinal }
        def optionValue = createSampleOptionValue(otherOpt.columnName)
        def optionLabel = createSampleOptionLabel(otherOpt.columnName)

        def newOpt = new CoreFormColumnOptions(columnCode: otherOpt.columnCode, columnName: otherOpt.columnName, ordinal: maxOpt ? maxOpt.ordinal + 1 : 1, optionValue: optionValue, optionLabel: optionLabel, optionLabelCode: "")
        newOpt = newOpt.save(flush:true)

        println "${newOpt?.errors}"

        if (!newOpt.hasErrors()) {
            return new JActionResult(result: JActionResult.Result.SUCCESS, message:  generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.updated.label"), data: [newOpt])
        } else {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: "" + errorMessageService.formatErrors(opt))
        }

        return null
    }

    JActionResult deleteCustomOptions(String id) {
        def opt = CoreFormColumnOptions.get(id)
        //checks
        opt.delete(flush: true)
        if (!opt.hasErrors()) {
            return new JActionResult(result: JActionResult.Result.SUCCESS, message:  generalUtilitiesService.getMessageWeb("settings.coreformoptions.message.deleted.label"))
        } else {
            return new JActionResult(result: JActionResult.Result.ERROR.name(), message: "" + errorMessageService.formatErrors(opt))
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

    void updateColumnOptionLabels(){
        CoreFormColumnOptions.list().each {
            def msg = generalUtilitiesService.getMessage(it.optionLabelCode)
            if (msg != null) {
                it.optionLabel = msg;
                it.save()
            }
        }
    }

    void createDefaultOptionsFor(String columnCode) {
        def svc = generalUtilitiesService

        //re-insert missing options
        if (columnCode?.equalsIgnoreCase("#.education")) {
            //Education
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 1, optionValue: EducationType.NO_EDUCATION.code, optionLabel: svc.getMessage(EducationType.NO_EDUCATION.name), optionLabelCode: EducationType.NO_EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 2, optionValue: EducationType.PRIMARY_EDUCATION.code, optionLabel: svc.getMessage(EducationType.PRIMARY_EDUCATION.name), optionLabelCode: EducationType.PRIMARY_EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 3, optionValue: EducationType.SECONDARY_EDUCATION.code, optionLabel: svc.getMessage(EducationType.SECONDARY_EDUCATION.name), optionLabelCode: EducationType.SECONDARY_EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 4, optionValue: EducationType.BACHELORS_DEGRE.code, optionLabel: svc.getMessage(EducationType.BACHELORS_DEGRE.name), optionLabelCode: EducationType.BACHELORS_DEGRE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 5, optionValue: EducationType.MASTERS_DEGRE.code, optionLabel: svc.getMessage(EducationType.MASTERS_DEGRE.name), optionLabelCode: EducationType.MASTERS_DEGRE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 6, optionValue: EducationType.DOCTORATE_DEGRE.code, optionLabel: svc.getMessage(EducationType.DOCTORATE_DEGRE.name), optionLabelCode: EducationType.DOCTORATE_DEGRE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 7, optionValue: EducationType.OTHER.code, optionLabel: svc.getMessage(EducationType.OTHER.name), optionLabelCode: EducationType.OTHER.name, readonly: true).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.education", columnName: "education", ordinal: 8, optionValue: EducationType.UNKNOWN.code, optionLabel: svc.getMessage(EducationType.UNKNOWN.name), optionLabelCode: EducationType.UNKNOWN.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("#.religion")) {
            //Religion
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 1, optionValue: ReligionType.CHRISTIAN_CATHOLIC.code,   optionLabel: svc.getMessage(ReligionType.CHRISTIAN_CATHOLIC.name), optionLabelCode: ReligionType.CHRISTIAN_CATHOLIC.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 2, optionValue: ReligionType.CHRISTIAN_ORTHODOX.code,   optionLabel: svc.getMessage(ReligionType.CHRISTIAN_ORTHODOX.name), optionLabelCode: ReligionType.CHRISTIAN_ORTHODOX.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 3, optionValue: ReligionType.CHRISTIAN_PROTESTANT.code, optionLabel: svc.getMessage(ReligionType.CHRISTIAN_PROTESTANT.name), optionLabelCode: ReligionType.CHRISTIAN_PROTESTANT.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 4, optionValue: ReligionType.MUSLIM.code,               optionLabel: svc.getMessage(ReligionType.MUSLIM.name), optionLabelCode: ReligionType.MUSLIM.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 5, optionValue: ReligionType.JEWISH.code,               optionLabel: svc.getMessage(ReligionType.JEWISH.name), optionLabelCode: ReligionType.JEWISH.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 6, optionValue: ReligionType.HINDU.code,                optionLabel: svc.getMessage(ReligionType.HINDU.name), optionLabelCode: ReligionType.HINDU.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 7, optionValue: ReligionType.BUDHIST.code,              optionLabel: svc.getMessage(ReligionType.BUDHIST.name), optionLabelCode: ReligionType.BUDHIST.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 8, optionValue: ReligionType.TRADITIONAL_FAITH.code,    optionLabel: svc.getMessage(ReligionType.TRADITIONAL_FAITH.name), optionLabelCode: ReligionType.TRADITIONAL_FAITH.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 9, optionValue: ReligionType.ATHEIST.code,              optionLabel: svc.getMessage(ReligionType.ATHEIST.name), optionLabelCode: ReligionType.ATHEIST.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 10, optionValue: ReligionType.NO_RELIGION.code,          optionLabel: svc.getMessage(ReligionType.NO_RELIGION.name), optionLabelCode: ReligionType.NO_RELIGION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 11, optionValue: ReligionType.OTHER.code,                optionLabel: svc.getMessage(ReligionType.OTHER.name), optionLabelCode: ReligionType.OTHER.name, readonly: true).save(flush: true)
            new CoreFormColumnOptions(columnCode: "#.religion", columnName: "religion", ordinal: 12, optionValue: ReligionType.UNKNOWN.code,              optionLabel: svc.getMessage(ReligionType.UNKNOWN.name), optionLabelCode: ReligionType.UNKNOWN.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.CHANGE_HEAD_FORM.code}.reason")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 1, optionValue: ChangeHeadReason.RESIDENCY_CHANGE.code,   optionLabel: svc.getMessage(ChangeHeadReason.RESIDENCY_CHANGE.name), optionLabelCode: ChangeHeadReason.RESIDENCY_CHANGE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 2, optionValue: ChangeHeadReason.OLD_AGE.code,   optionLabel: svc.getMessage(ChangeHeadReason.OLD_AGE.name), optionLabelCode: ChangeHeadReason.OLD_AGE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 3, optionValue: ChangeHeadReason.HEAD_OUTMIGRATING.code,   optionLabel: svc.getMessage(ChangeHeadReason.HEAD_OUTMIGRATING.name), optionLabelCode: ChangeHeadReason.HEAD_OUTMIGRATING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 4, optionValue: ChangeHeadReason.DIVORCE.code,   optionLabel: svc.getMessage(ChangeHeadReason.DIVORCE.name), optionLabelCode: ChangeHeadReason.DIVORCE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 5, optionValue: ChangeHeadReason.WORK.code,   optionLabel: svc.getMessage(ChangeHeadReason.WORK.name), optionLabelCode: ChangeHeadReason.WORK.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 6, optionValue: ChangeHeadReason.HEALTH_REASONS.code,   optionLabel: svc.getMessage(ChangeHeadReason.HEALTH_REASONS.name), optionLabelCode: ChangeHeadReason.HEALTH_REASONS.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 7, optionValue: ChangeHeadReason.DEATH.code,   optionLabel: svc.getMessage(ChangeHeadReason.DEATH.name), optionLabelCode: ChangeHeadReason.DEATH.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.CHANGE_HEAD_FORM.code}.reason", columnName: "reason", ordinal: 8, optionValue: ChangeHeadReason.OTHER.code,   optionLabel: svc.getMessage(ChangeHeadReason.OTHER.name), optionLabelCode: ChangeHeadReason.OTHER.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.DEATH_FORM.code}.deathCause")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 1, optionValue: DeathCause.MALARIA.code,   optionLabel: svc.getMessage(DeathCause.MALARIA.name), optionLabelCode: DeathCause.MALARIA.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 2, optionValue: DeathCause.HIV_AIDS.code,   optionLabel: svc.getMessage(DeathCause.HIV_AIDS.name), optionLabelCode: DeathCause.HIV_AIDS.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 3, optionValue: DeathCause.TB.code,   optionLabel: svc.getMessage(DeathCause.TB.name), optionLabelCode: DeathCause.TB.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 4, optionValue: DeathCause.OTHER_VB_INFECTION.code,   optionLabel: svc.getMessage(DeathCause.OTHER_VB_INFECTION.name), optionLabelCode: DeathCause.OTHER_VB_INFECTION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 5, optionValue: DeathCause.ACCIDENT.code,   optionLabel: svc.getMessage(DeathCause.ACCIDENT.name), optionLabelCode: DeathCause.ACCIDENT.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 6, optionValue: DeathCause.CRIME.code,   optionLabel: svc.getMessage(DeathCause.CRIME.name), optionLabelCode: DeathCause.CRIME.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 7, optionValue: DeathCause.AGE.code,   optionLabel: svc.getMessage(DeathCause.AGE.name), optionLabelCode: DeathCause.AGE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 8, optionValue: DeathCause.UNKNOWN.code,   optionLabel: svc.getMessage(DeathCause.UNKNOWN.name), optionLabelCode: DeathCause.UNKNOWN.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 9, optionValue: DeathCause.OTHER.code,   optionLabel: svc.getMessage(DeathCause.OTHER.name), optionLabelCode: DeathCause.OTHER.name, readonly: true).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathCause", columnName: "deathCause", ordinal: 10, optionValue: DeathCause.DONT_KNOW.code,   optionLabel: svc.getMessage(DeathCause.DONT_KNOW.name), optionLabelCode: DeathCause.DONT_KNOW.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.DEATH_FORM.code}.deathPlace")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathPlace", columnName: "deathPlace", ordinal: 1, optionValue: DeathPlace.HOME.code,   optionLabel: svc.getMessage(DeathPlace.HOME.name), optionLabelCode: DeathPlace.HOME.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathPlace", columnName: "deathPlace", ordinal: 2, optionValue: DeathPlace.HOSPITAL.code,   optionLabel: svc.getMessage(DeathPlace.HOSPITAL.name), optionLabelCode: DeathPlace.HOSPITAL.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathPlace", columnName: "deathPlace", ordinal: 3, optionValue: DeathPlace.HEALTH_CENTER.code,   optionLabel: svc.getMessage(DeathPlace.HEALTH_CENTER.name), optionLabelCode: DeathPlace.HEALTH_CENTER.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathPlace", columnName: "deathPlace", ordinal: 4, optionValue: DeathPlace.TRAD_HEALER.code,   optionLabel: svc.getMessage(DeathPlace.TRAD_HEALER.name), optionLabelCode: DeathPlace.TRAD_HEALER.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathPlace", columnName: "deathPlace", ordinal: 5, optionValue: DeathPlace.PRIVATE_MAT_HOME.code,   optionLabel: svc.getMessage(DeathPlace.PRIVATE_MAT_HOME.name), optionLabelCode: DeathPlace.PRIVATE_MAT_HOME.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.DEATH_FORM.code}.deathPlace", columnName: "deathPlace", ordinal: 6, optionValue: DeathPlace.OTHER.code,   optionLabel: svc.getMessage(DeathPlace.OTHER.name), optionLabelCode: DeathPlace.OTHER.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 1, optionValue: ExtInmigrationReason.CAME_WITH_RELATIVES.code,   optionLabel: svc.getMessage(ExtInmigrationReason.CAME_WITH_RELATIVES.name), optionLabelCode: ExtInmigrationReason.CAME_WITH_RELATIVES.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 2, optionValue: ExtInmigrationReason.FARMING.code,   optionLabel: svc.getMessage(ExtInmigrationReason.FARMING.name), optionLabelCode: ExtInmigrationReason.FARMING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 3, optionValue: ExtInmigrationReason.MARITAL_CHANGE.code,   optionLabel: svc.getMessage(ExtInmigrationReason.MARITAL_CHANGE.name), optionLabelCode: ExtInmigrationReason.MARITAL_CHANGE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 4, optionValue: ExtInmigrationReason.FISHING.code,   optionLabel: svc.getMessage(ExtInmigrationReason.FISHING.name), optionLabelCode: ExtInmigrationReason.FISHING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 5, optionValue: ExtInmigrationReason.EDUCATION.code,   optionLabel: svc.getMessage(ExtInmigrationReason.EDUCATION.name), optionLabelCode: ExtInmigrationReason.EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 6, optionValue: ExtInmigrationReason.HEALTH_REASON.code,   optionLabel: svc.getMessage(ExtInmigrationReason.HEALTH_REASON.name), optionLabelCode: ExtInmigrationReason.HEALTH_REASON.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 7, optionValue: ExtInmigrationReason.WORK.code,   optionLabel: svc.getMessage(ExtInmigrationReason.WORK.name), optionLabelCode: ExtInmigrationReason.WORK.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 8, optionValue: ExtInmigrationReason.NEW_HOUSE.code,   optionLabel: svc.getMessage(ExtInmigrationReason.NEW_HOUSE.name), optionLabelCode: ExtInmigrationReason.NEW_HOUSE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 9, optionValue: ExtInmigrationReason.OTHER.code,   optionLabel: svc.getMessage(ExtInmigrationReason.OTHER.name), optionLabelCode: ExtInmigrationReason.OTHER.name, readonly: true).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.EXT_INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 10, optionValue: ExtInmigrationReason.DONT_KNOW.code,   optionLabel: svc.getMessage(ExtInmigrationReason.DONT_KNOW.name), optionLabelCode: ExtInmigrationReason.DONT_KNOW.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.INMIGRATION_FORM.code}.migrationReason")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 1, optionValue: InmigrationReason.CAME_WITH_RELATIVES.code,   optionLabel: svc.getMessage(InmigrationReason.CAME_WITH_RELATIVES.name), optionLabelCode: InmigrationReason.CAME_WITH_RELATIVES.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 2, optionValue: InmigrationReason.FARMING.code,   optionLabel: svc.getMessage(InmigrationReason.FARMING.name), optionLabelCode: InmigrationReason.FARMING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 3, optionValue: InmigrationReason.MARITAL_CHANGE.code,   optionLabel: svc.getMessage(InmigrationReason.MARITAL_CHANGE.name), optionLabelCode: InmigrationReason.MARITAL_CHANGE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 4, optionValue: InmigrationReason.FISHING.code,   optionLabel: svc.getMessage(InmigrationReason.FISHING.name), optionLabelCode: InmigrationReason.FISHING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 5, optionValue: InmigrationReason.EDUCATION.code,   optionLabel: svc.getMessage(InmigrationReason.EDUCATION.name), optionLabelCode: InmigrationReason.EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 6, optionValue: InmigrationReason.HEALTH_REASON.code,   optionLabel: svc.getMessage(InmigrationReason.HEALTH_REASON.name), optionLabelCode: InmigrationReason.HEALTH_REASON.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 7, optionValue: InmigrationReason.WORK.code,   optionLabel: svc.getMessage(InmigrationReason.WORK.name), optionLabelCode: InmigrationReason.WORK.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 8, optionValue: InmigrationReason.NEW_HOUSE.code,   optionLabel: svc.getMessage(InmigrationReason.NEW_HOUSE.name), optionLabelCode: InmigrationReason.NEW_HOUSE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 9, optionValue: InmigrationReason.OTHER.code,   optionLabel: svc.getMessage(InmigrationReason.OTHER.name), optionLabelCode: InmigrationReason.OTHER.name, readonly: true).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 10, optionValue: InmigrationReason.DONT_KNOW.code,   optionLabel: svc.getMessage(InmigrationReason.DONT_KNOW.name), optionLabelCode: InmigrationReason.DONT_KNOW.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.OUTMIGRATION_FORM.code}.migrationReason")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 1, optionValue: OutmigrationReason.WENT_WITH_RELATIVES.code,   optionLabel: svc.getMessage(OutmigrationReason.WENT_WITH_RELATIVES.name), optionLabelCode: OutmigrationReason.WENT_WITH_RELATIVES.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 2, optionValue: OutmigrationReason.FARMING.code,   optionLabel: svc.getMessage(OutmigrationReason.FARMING.name), optionLabelCode: OutmigrationReason.FARMING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 3, optionValue: OutmigrationReason.MARITAL_CHANGE.code,   optionLabel: svc.getMessage(OutmigrationReason.MARITAL_CHANGE.name), optionLabelCode: OutmigrationReason.MARITAL_CHANGE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 4, optionValue: OutmigrationReason.FISHING.code,   optionLabel: svc.getMessage(OutmigrationReason.FISHING.name), optionLabelCode: OutmigrationReason.FISHING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 5, optionValue: OutmigrationReason.EDUCATION.code,   optionLabel: svc.getMessage(OutmigrationReason.EDUCATION.name), optionLabelCode: OutmigrationReason.EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 6, optionValue: OutmigrationReason.HEALTH_REASON.code,   optionLabel: svc.getMessage(OutmigrationReason.HEALTH_REASON.name), optionLabelCode: OutmigrationReason.HEALTH_REASON.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 7, optionValue: OutmigrationReason.WORK.code,   optionLabel: svc.getMessage(OutmigrationReason.WORK.name), optionLabelCode: OutmigrationReason.WORK.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 8, optionValue: OutmigrationReason.NEW_HOUSE.code,   optionLabel: svc.getMessage(OutmigrationReason.NEW_HOUSE.name), optionLabelCode: OutmigrationReason.NEW_HOUSE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 9, optionValue: OutmigrationReason.OTHER.code,   optionLabel: svc.getMessage(OutmigrationReason.OTHER.name), optionLabelCode: OutmigrationReason.OTHER.name, readonly: true).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.OUTMIGRATION_FORM.code}.migrationReason", columnName: "migrationReason", ordinal: 10, optionValue: OutmigrationReason.DONT_KNOW.code,   optionLabel: svc.getMessage(OutmigrationReason.DONT_KNOW.name), optionLabelCode: OutmigrationReason.DONT_KNOW.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.VISIT_FORM.code}.respondentRelationship")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.respondentRelationship", columnName: "respondentRelationship", ordinal: 1, optionValue: VisitRespondentRelationship.FIELDWORKER.code,   optionLabel: svc.getMessage(VisitRespondentRelationship.FIELDWORKER.name), optionLabelCode: VisitRespondentRelationship.FIELDWORKER.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.respondentRelationship", columnName: "respondentRelationship", ordinal: 2, optionValue: VisitRespondentRelationship.NEIGHBOR.code,   optionLabel: svc.getMessage(VisitRespondentRelationship.NEIGHBOR.name), optionLabelCode: VisitRespondentRelationship.NEIGHBOR.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.respondentRelationship", columnName: "respondentRelationship", ordinal: 3, optionValue: VisitRespondentRelationship.FAMILY.code,   optionLabel: svc.getMessage(VisitRespondentRelationship.FAMILY.name), optionLabelCode: VisitRespondentRelationship.FAMILY.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.respondentRelationship", columnName: "respondentRelationship", ordinal: 4, optionValue: VisitRespondentRelationship.OTHER.code,   optionLabel: svc.getMessage(VisitRespondentRelationship.OTHER.name), optionLabelCode: VisitRespondentRelationship.OTHER.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.VISIT_FORM.code}.visitReason")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitReason", columnName: "visitReason", ordinal: 1, optionValue: VisitReason.UPDATE_ROUNDS.code,   optionLabel: svc.getMessage(VisitReason.UPDATE_ROUNDS.name), optionLabelCode: VisitReason.UPDATE_ROUNDS.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitReason", columnName: "visitReason", ordinal: 2, optionValue: VisitReason.BASELINE.code,   optionLabel: svc.getMessage(VisitReason.BASELINE.name), optionLabelCode: VisitReason.BASELINE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitReason", columnName: "visitReason", ordinal: 3, optionValue: VisitReason.NEW_HOUSEHOLD.code,   optionLabel: svc.getMessage(VisitReason.NEW_HOUSEHOLD.name), optionLabelCode: VisitReason.NEW_HOUSEHOLD.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitReason", columnName: "visitReason", ordinal: 4, optionValue: VisitReason.DATA_CLEANING.code,   optionLabel: svc.getMessage(VisitReason.DATA_CLEANING.name), optionLabelCode: VisitReason.DATA_CLEANING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitReason", columnName: "visitReason", ordinal: 5, optionValue: VisitReason.OTHER.code,   optionLabel: svc.getMessage(VisitReason.OTHER.name), optionLabelCode: VisitReason.OTHER.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.VISIT_FORM.code}.visitNotPossibleReason")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 1, optionValue: NoVisitReason.HOUSE_OCCUPIED.code,   optionLabel: svc.getMessage(NoVisitReason.HOUSE_OCCUPIED.name), optionLabelCode: NoVisitReason.HOUSE_OCCUPIED.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 2, optionValue: NoVisitReason.HOUSE_NOT_FOUND.code,   optionLabel: svc.getMessage(NoVisitReason.HOUSE_NOT_FOUND.name), optionLabelCode: NoVisitReason.HOUSE_NOT_FOUND.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 3, optionValue: NoVisitReason.HOUSE_DESTROYED.code,   optionLabel: svc.getMessage(NoVisitReason.HOUSE_DESTROYED.name), optionLabelCode: NoVisitReason.HOUSE_DESTROYED.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 4, optionValue: NoVisitReason.HOUSE_ABANDONED.code,   optionLabel: svc.getMessage(NoVisitReason.HOUSE_ABANDONED.name), optionLabelCode: NoVisitReason.HOUSE_ABANDONED.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 5, optionValue: NoVisitReason.HOUSE_VACANT.code,   optionLabel: svc.getMessage(NoVisitReason.HOUSE_VACANT.name), optionLabelCode: NoVisitReason.HOUSE_VACANT.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 6, optionValue: NoVisitReason.NO_RESPONDENT.code,   optionLabel: svc.getMessage(NoVisitReason.NO_RESPONDENT.name), optionLabelCode: NoVisitReason.NO_RESPONDENT.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 7, optionValue: NoVisitReason.REFUSE.code,   optionLabel: svc.getMessage(NoVisitReason.REFUSE.name), optionLabelCode: NoVisitReason.REFUSE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitNotPossibleReason", columnName: "visitNotPossibleReason", ordinal: 8, optionValue: NoVisitReason.OTHER.code,   optionLabel: svc.getMessage(NoVisitReason.OTHER.name), optionLabelCode: NoVisitReason.OTHER.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.VISIT_FORM.code}.visitLocation")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitLocation", columnName: "visitLocation", ordinal: 1, optionValue: VisitLocationItem.HOME.code,   optionLabel: svc.getMessage(VisitLocationItem.HOME.name), optionLabelCode: VisitLocationItem.HOME.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitLocation", columnName: "visitLocation", ordinal: 2, optionValue: VisitLocationItem.WORK_PLACE.code,   optionLabel: svc.getMessage(VisitLocationItem.WORK_PLACE.name), optionLabelCode: VisitLocationItem.WORK_PLACE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitLocation", columnName: "visitLocation", ordinal: 3, optionValue: VisitLocationItem.HEALTH_UNIT.code,   optionLabel: svc.getMessage(VisitLocationItem.HEALTH_UNIT.name), optionLabelCode: VisitLocationItem.HEALTH_UNIT.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.VISIT_FORM.code}.visitLocation", columnName: "visitLocation", ordinal: 4, optionValue: VisitLocationItem.OTHER_PLACE.code,   optionLabel: svc.getMessage(VisitLocationItem.OTHER_PLACE.name), optionLabelCode: VisitLocationItem.OTHER_PLACE.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason")) {

            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 1, optionValue: IncompleteVisitReason.UNAVAILABLE_TODAY.code,   optionLabel: svc.getMessage(IncompleteVisitReason.UNAVAILABLE_TODAY.name), optionLabelCode: IncompleteVisitReason.UNAVAILABLE_TODAY.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 2, optionValue: IncompleteVisitReason.UNAVAILABLE_ROUND.code,   optionLabel: svc.getMessage(IncompleteVisitReason.UNAVAILABLE_ROUND.name), optionLabelCode: IncompleteVisitReason.UNAVAILABLE_ROUND.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 3, optionValue: IncompleteVisitReason.UNWILLING.code,   optionLabel: svc.getMessage(IncompleteVisitReason.UNWILLING.name), optionLabelCode: IncompleteVisitReason.UNWILLING.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 4, optionValue: IncompleteVisitReason.HOSPITALIZED.code,   optionLabel: svc.getMessage(IncompleteVisitReason.HOSPITALIZED.name), optionLabelCode: IncompleteVisitReason.HOSPITALIZED.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 5, optionValue: IncompleteVisitReason.RELOCATED.code,   optionLabel: svc.getMessage(IncompleteVisitReason.RELOCATED.name), optionLabelCode: IncompleteVisitReason.RELOCATED.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 6, optionValue: IncompleteVisitReason.WITHDREW.code,   optionLabel: svc.getMessage(IncompleteVisitReason.WITHDREW.name), optionLabelCode: IncompleteVisitReason.WITHDREW.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 7, optionValue: IncompleteVisitReason.DEAD.code,   optionLabel: svc.getMessage(IncompleteVisitReason.DEAD.name), optionLabelCode: IncompleteVisitReason.DEAD.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.INCOMPLETE_VISIT_FORM.code}.reason", columnName: "reason", ordinal: 8, optionValue: IncompleteVisitReason.OTHER.code,   optionLabel: svc.getMessage(IncompleteVisitReason.OTHER.name), optionLabelCode: IncompleteVisitReason.OTHER.name, readonly: true).save(flush: true)

            return
        }

        if (columnCode?.equalsIgnoreCase("${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace")) {
            new CoreFormColumnOptions(columnCode: "${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace", columnName: "birthPlace", ordinal: 1, optionValue: BirthPlace.HOME.code,   optionLabel: svc.getMessage(BirthPlace.HOME.name), optionLabelCode: BirthPlace.HOME.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace", columnName: "birthPlace", ordinal: 2, optionValue: BirthPlace.HOSPITAL.code,   optionLabel: svc.getMessage(BirthPlace.HOSPITAL.name), optionLabelCode: BirthPlace.HOSPITAL.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace", columnName: "birthPlace", ordinal: 3, optionValue: BirthPlace.TRADITIONAL_MIDWIFE.code,   optionLabel: svc.getMessage(BirthPlace.TRADITIONAL_MIDWIFE.name), optionLabelCode: BirthPlace.TRADITIONAL_MIDWIFE.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace", columnName: "birthPlace", ordinal: 4, optionValue: BirthPlace.HEALTH_CENTER_CLINIC.code,   optionLabel: svc.getMessage(BirthPlace.HEALTH_CENTER_CLINIC.name), optionLabelCode: BirthPlace.HEALTH_CENTER_CLINIC.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace", columnName: "birthPlace", ordinal: 5, optionValue: BirthPlace.ON_THE_WAY.code,   optionLabel: svc.getMessage(BirthPlace.ON_THE_WAY.name), optionLabelCode: BirthPlace.ON_THE_WAY.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace", columnName: "birthPlace", ordinal: 6, optionValue: BirthPlace.PRIVATE_MAT_HOME.code,   optionLabel: svc.getMessage(BirthPlace.PRIVATE_MAT_HOME.name), optionLabelCode: BirthPlace.PRIVATE_MAT_HOME.name).save(flush: true)
            new CoreFormColumnOptions(columnCode: "${CoreForm.PREGNANCY_OUTCOME_FORM.code}.birthPlace", columnName: "birthPlace", ordinal: 7, optionValue: BirthPlace.OTHER.code,   optionLabel: svc.getMessage(BirthPlace.OTHER.name), optionLabelCode: BirthPlace.OTHER.name, readonly: true).save(flush: true)

            return
        }
    }


}
