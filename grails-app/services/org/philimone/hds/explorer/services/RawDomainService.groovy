package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyOutcome
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyRegistration
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.ValidatableEntity
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.PartiallyDisabledEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType
import org.philimone.hds.explorer.server.model.json.JActionResult
import org.philimone.hds.explorer.server.model.json.JMember
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.InMigration
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.OutMigration
import org.philimone.hds.explorer.server.model.main.PartiallyDisabled
import org.philimone.hds.explorer.server.model.main.PregnancyOutcome
import org.philimone.hds.explorer.server.model.main.PregnancyRegistration
import org.philimone.hds.explorer.server.model.main.Residency
import org.philimone.hds.explorer.server.model.main.Visit
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

import java.time.LocalDate

@Transactional
class RawDomainService {

    def visitService
    def residencyService
    def headRelationshipService
    def maritalRelationshipService
    def deathService
    def codeGeneratorService
    def dataReconciliationService
    def generalUtilitiesService
    def errorMessageService

    String getHouseholdCode(String code) {
        if (codeGeneratorService.isVisitCodeValid(code)) {
            def household_code = code

            def result = Visit.executeQuery("select v.householdCode from Visit v where v.code=?0", [code])

            if (result?.size() > 0) {
                return result.first()
            }
        }


        return code
    }

    JMember getBasicMember(String memberCode) {
        def result = Member.executeQuery("select m.code, m.name, m.gender, m.dob, m.householdCode, m.householdName from Member m where m.code = ?0", [memberCode])
        def m = result.size()>0 ? result.first() : null

        if (m == null) return null

        return new JMember(m[0], m[1], m[2], m[3], m[4], m[5])
    }

    def getValidationStatus(ValidatableStatus status) {
        return (status==null || status==ValidatableStatus.ACTIVE || status==ValidatableStatus.PARTIALLY_INACTIVE) //true is ACTIVE, false is TEMPORARILY INACTIVE
    }

    JActionResult disableResidency(Residency obj) {

        if (obj != null) {

            if (obj.status == ValidatableStatus.TEMPORARILY_INACTIVE) {
                //already inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.already.label"))
            }

            if (obj.status == ValidatableStatus.PARTIALLY_INACTIVE) {
                //already partially inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.already.label"))
            }

            if (!residencyService.isMostRecentResidency(obj)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.most.recent.label"))
            }

            //disable the record
            obj.status = ValidatableStatus.TEMPORARILY_INACTIVE
            obj.save(flush: true)

            def list = [toResidencyValues(obj)]

            if (obj.startType == ResidencyStartType.INTERNAL_INMIGRATION) {
                //These events have a dependant exit event so we must partially invalidate the previous event.
                //get previous event - list events of the member
                Residency previousRes = residencyService.getPreviousResidency(obj)
                println "${previousRes}"
                if (previousRes != null) {
                    //backup
                    def partDisabled = new PartiallyDisabled(entity: ValidatableEntity.RESIDENCY, household: previousRes.household, member: previousRes.member, householdCode: previousRes.householdCode, memberCode: previousRes.memberCode)
                    partDisabled.id = previousRes.id
                    partDisabled.endType = PartiallyDisabledEndType.getFromResidency(previousRes.endType)
                    partDisabled.endDate = previousRes.endDate
                    partDisabled.save(flush: true)

                    previousRes.status = ValidatableStatus.PARTIALLY_INACTIVE
                    previousRes.endDate = null
                    previousRes.endType = ResidencyEndType.NOT_APPLICABLE
                    previousRes.save(flush: true)

                    list.add(toResidencyValues(previousRes))
                }
            }


            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.success.label"), data: list)
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.found.label"))

    }

    JActionResult disableResidencyEndEvent(Residency obj) {

        if (obj != null) {

            if (obj.status == ValidatableStatus.TEMPORARILY_INACTIVE) {
                //already inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.already.label"))
            }

            if (obj.status == ValidatableStatus.PARTIALLY_INACTIVE) {
                //already partially inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.already.label"))
            }

            if (!residencyService.isMostRecentResidency(obj)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.most.recent.label"))
            }

            if (obj.endType == ResidencyEndType.INTERNAL_OUTMIGRATION) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partially.not.possible.label"))
            }

            //backup
            def partDisabled = new PartiallyDisabled(entity: ValidatableEntity.RESIDENCY, household: obj.household, member: obj.member, householdCode: obj.householdCode, memberCode: obj.memberCode)
            partDisabled.id = obj.id
            partDisabled.endType = PartiallyDisabledEndType.getFromResidency(obj.endType)
            partDisabled.endDate = obj.endDate
            partDisabled.save(flush: true)

            //disable the record
            obj.status = ValidatableStatus.PARTIALLY_INACTIVE
            obj.endDate = null
            obj.endType = ResidencyEndType.NOT_APPLICABLE
            obj.save(flush: true)

            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.success.label"), data: [toResidencyValues(obj)])
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.found.label"))

    }

    JActionResult deleteResidency(Residency obj) {

        if (obj != null) {

            if (InMigration.countByDestinationResidency(obj) > 0) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.delete.record.residency.inmigration.error.label"))
            }

            if (OutMigration.countByOriginResidency(obj) > 0) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.delete.record.residency.outmigration.error.label"))
            }

            try {
                obj.delete(flush: true)

                dataReconciliationService.memberStatusReconcialiationBy([obj.member?.id])

            } catch(Exception ex) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: errorMessageService.getRawMessagesText(ex))
            }

            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.delete.record.deleted.label"))
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.update.record.notfound.label", ["Residency"]))

    }

    JActionResult createResidencyBasedOn(Residency obj) {

        if (obj != null) {

            def lastRes = residencyService.getCurrentResidency(obj.member)

            if (lastRes != null) {
                if (lastRes.endType == ResidencyEndType.NOT_APPLICABLE) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.residency.record.na.error.label"))
                }

                if (lastRes.endType == ResidencyEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.residency.record.death.error.label"))
                }
            }

            //create new residency record
            def residency = new Residency()
            residency.household = obj.household
            residency.householdCode = obj.household?.code
            residency.member = obj.member
            residency.memberCode = obj.member?.code
            residency.startType = residencyService.getNextStartType(obj)
            residency.startDate = getNextDate(obj.endDate)
            residency.endType = ResidencyEndType.NOT_APPLICABLE //create as non-applicable
            residency.endDate = null
            residency.status = ValidatableStatus.ACTIVE

            residency.save(flush: true)

            if (residency.hasErrors()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: errorMessageService.getRawMessagesText(residency))
            }


            def data = [toResidencyValues(residency)]
            return new JActionResult(result: JActionResult.Result.SUCCESS, data: data, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.created.label"))
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.notfound.label", ["Residency"]))

    }

    JActionResult createResidencyBasedOnHeadRelationship(HeadRelationship obj) {

        if (obj != null) {

            def lastRes = residencyService.getCurrentResidency(obj.member)

            if (lastRes != null) {
                if (lastRes.endType == ResidencyEndType.NOT_APPLICABLE) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.residency.record.na.error.label"))
                }

                if (lastRes.endType == ResidencyEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.residency.record.death.error.label"))
                }
            }

            //create new residency record
            def residency = new Residency()
            residency.household = obj.household
            residency.householdCode = obj.household?.code
            residency.member = obj.member
            residency.memberCode = obj.member?.code
            residency.startType = ResidencyStartType.getFrom(obj.startType.code)==null ? ResidencyStartType.INTERNAL_INMIGRATION : ResidencyStartType.getFrom(obj.startType.code)
            residency.startDate = obj.startDate
            residency.endType = ResidencyEndType.getFrom(obj.endType.code)==null ? ResidencyEndType.NOT_APPLICABLE : ResidencyEndType.getFrom(obj.endType.code)
            residency.endDate = obj.endDate
            residency.status = ValidatableStatus.ACTIVE

            residency.save(flush: true)

            if (residency.hasErrors()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: errorMessageService.getRawMessagesText(residency))
            }


            def data = [toResidencyValues(residency)]
            return new JActionResult(result: JActionResult.Result.SUCCESS, data: data, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.created.label"))
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.notfound.label", ["Residency"]))

    }

    JActionResult disableHeadRelationship(HeadRelationship obj) {

        if (obj != null) {

            if (obj.status == ValidatableStatus.TEMPORARILY_INACTIVE) {
                //already inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.already.label"))
            }

            if (obj.status == ValidatableStatus.PARTIALLY_INACTIVE) {
                //already partially inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.already.label"))
            }

            if (!headRelationshipService.isMostRecentHeadRelationship(obj)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.most.recent.label"))
            }

            //disable the record
            obj.status = ValidatableStatus.TEMPORARILY_INACTIVE
            obj.save(flush: true)

            def list = [toHeadRelationshipValues(obj)]

            if (obj.startType == HeadRelationshipStartType.INTERNAL_INMIGRATION || obj.startType == HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD) {
                //These events have a dependant exit event so we must partially invalidate the previous event.
                //get previous event - list events of the member
                HeadRelationship previousHr = headRelationshipService.getPreviousHeadRelationship(obj)
                println "${previousHr}"
                if (previousHr != null) {
                    //backup
                    def partDisabled = new PartiallyDisabled(entity: ValidatableEntity.HEAD_RELATIONSHIP, household: previousHr.household, member: previousHr.member, householdCode: previousHr.householdCode, memberCode: previousHr.memberCode)
                    partDisabled.id = previousHr.id
                    partDisabled.endType = PartiallyDisabledEndType.getFromHeadRelationship(previousHr.endType)
                    partDisabled.endDate = previousHr.endDate
                    partDisabled.save(flush: true)

                    previousHr.status = ValidatableStatus.PARTIALLY_INACTIVE
                    previousHr.endDate = null
                    previousHr.endType = HeadRelationshipEndType.NOT_APPLICABLE
                    previousHr.save(flush: true)

                    list.add(toHeadRelationshipValues(previousHr))
                }
            }


            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.success.label"), data: list)
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.found.label"))

    }

    JActionResult disableHeadRelationshipEndEvent(HeadRelationship obj) {

        if (obj != null) {

            if (obj.status == ValidatableStatus.TEMPORARILY_INACTIVE) {
                //already inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.already.label"))
            }

            if (obj.status == ValidatableStatus.PARTIALLY_INACTIVE) {
                //already partially inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.already.label"))
            }

            if (!headRelationshipService.isMostRecentHeadRelationship(obj)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.most.recent.label"))
            }

            if (obj.endType == HeadRelationshipEndType.INTERNAL_OUTMIGRATION || obj.endType == HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partially.not.possible.label"))
            }

            //backup
            def partDisabled = new PartiallyDisabled(entity: ValidatableEntity.HEAD_RELATIONSHIP, household: obj.household, member: obj.member, householdCode: obj.householdCode, memberCode: obj.memberCode)
            partDisabled.id = obj.id
            partDisabled.endType = PartiallyDisabledEndType.getFromHeadRelationship(obj.endType)
            partDisabled.endDate = obj.endDate
            partDisabled.save(flush: true)

            //disable the record
            obj.status = ValidatableStatus.PARTIALLY_INACTIVE
            obj.endDate = null
            obj.endType = HeadRelationshipEndType.NOT_APPLICABLE
            obj.save(flush: true)

            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.success.label"), data: [toHeadRelationshipValues(obj)])
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.found.label"))

    }

    JActionResult deleteHeadRelationship(HeadRelationship obj) {

        if (obj != null) {

            try {
                obj.delete(flush: true)

                dataReconciliationService.memberStatusReconcialiationBy([obj.member?.id])

            } catch(Exception ex) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: errorMessageService.getRawMessagesText(ex))
            }

            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.delete.record.deleted.label"))
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.update.record.notfound.label", ["HeadRelationship"]))

    }

    JActionResult createHeadRelationshipBasedOn(HeadRelationship obj) {

        if (obj != null) {

            def lastHrel = headRelationshipService.getLastHeadRelationship(obj.member)

            if (lastHrel != null) {
                if (lastHrel.endType == HeadRelationshipEndType.NOT_APPLICABLE) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.headrelationship.record.na.error.label"))
                }

                if (lastHrel.endType == HeadRelationshipEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.headrelationship.record.death.error.label"))
                }
            }

            //create new residency record
            def headRelationship = new HeadRelationship()
            headRelationship.household = obj.household
            headRelationship.householdCode = obj.household?.code
            headRelationship.member = obj.member
            headRelationship.memberCode = obj.member?.code
            headRelationship.head = obj.head
            headRelationship.headCode = obj.head?.code
            headRelationship.relationshipType = obj.relationshipType
            headRelationship.startType = headRelationshipService.getNextStartType(obj)
            headRelationship.startDate = getNextDate(obj.endDate)
            headRelationship.endType = HeadRelationshipEndType.NOT_APPLICABLE //create as non-applicable
            headRelationship.endDate = null
            headRelationship.residency = obj.residency
            headRelationship.status = ValidatableStatus.ACTIVE

            headRelationship.save(flush: true)

            if (headRelationship.hasErrors()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: errorMessageService.getRawMessagesText(headRelationship))
            }


            def data = [toHeadRelationshipValues(headRelationship)]
            return new JActionResult(result: JActionResult.Result.SUCCESS, data: data, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.created.label"))
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.notfound.label", ["HeadRelationship"]))

    }

    JActionResult createHeadRelationshipBasedOnResidency(Residency obj) {

        if (obj != null) {

            def lastHrel = headRelationshipService.getLastHeadRelationship(obj.member)

            if (lastHrel != null) {
                if (lastHrel.endType == HeadRelationshipEndType.NOT_APPLICABLE) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.headrelationship.record.na.error.label"))
                }

                if (lastHrel.endType == HeadRelationshipEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.add.headrelationship.record.death.error.label"))
                }
            }

            //create new residency record
            def headRelationship = new HeadRelationship()
            headRelationship.household = obj.household
            headRelationship.householdCode = obj.household?.code
            headRelationship.member = obj.member
            headRelationship.memberCode = obj.member?.code
            headRelationship.head = findHeadFor(obj.member, obj.household, obj.startDate)
            headRelationship.headCode = headRelationship.head?.code
            headRelationship.relationshipType = HeadRelationshipType.DONT_KNOW
            headRelationship.startType = HeadRelationshipStartType.getFrom(obj.startType.code)
            headRelationship.startDate = obj.startDate
            headRelationship.endType = HeadRelationshipEndType.getFrom(obj.endType.code)
            headRelationship.endDate = obj.endDate
            headRelationship.residency = obj
            headRelationship.status = obj.status

            headRelationship.save(flush: true)

            if (headRelationship.hasErrors()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: errorMessageService.getRawMessagesText(headRelationship))
            }


            def data = [toHeadRelationshipValues(headRelationship)]
            return new JActionResult(result: JActionResult.Result.SUCCESS, data: data, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.created.label"))
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.add.record.notfound.label", ["HeadRelationship"]))

    }

    JActionResult disableMaritalRelationship(MaritalRelationship obj) {

        if (obj != null) {

            if (obj.status == ValidatableStatus.TEMPORARILY_INACTIVE) {
                //already inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.already.label"))
            }

            if (obj.status == ValidatableStatus.PARTIALLY_INACTIVE) {
                //already partially inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.already.label"))
            }

            if (!maritalRelationshipService.isMostRecentMaritalRelationship(obj)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.most.recent.label"))
            }

            //disable the record
            obj.status = ValidatableStatus.TEMPORARILY_INACTIVE
            obj.save(flush: true)

            def list = [toMaritalRelationshipValues(obj)]

            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.success.label"), data: list)
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.found.label"))

    }

    JActionResult disableMaritalRelationshipEndEvent(MaritalRelationship obj) {

        if (obj != null) {

            if (obj.status == ValidatableStatus.TEMPORARILY_INACTIVE) {
                //already inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.already.label"))
            }

            if (obj.status == ValidatableStatus.PARTIALLY_INACTIVE) {
                //already partially inactive
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.already.label"))
            }

            if (!maritalRelationshipService.isMostRecentMaritalRelationship(obj)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.most.recent.label"))
            }

            //backup
            def partDisabled = new PartiallyDisabled(entity: ValidatableEntity.MARITAL_RELATIONSHIP, member: obj.memberA, memberCode: obj.memberA_code, spouse: obj.memberB, spouseCode: obj.memberB_code)
            partDisabled.id = obj.id
            partDisabled.endType = PartiallyDisabledEndType.getFromMaritalRelationship(obj.endType)
            partDisabled.endDate = obj.endDate
            partDisabled.save(flush: true)

            //disable the record
            obj.status = ValidatableStatus.PARTIALLY_INACTIVE
            obj.endDate = null
            obj.endStatus = MaritalEndStatus.NOT_APPLICABLE
            obj.save(flush: true)

            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partial.success.label"), data: [toMaritalRelationshipValues(obj)])
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.not.found.label"))

    }

    JActionResult deleteMaritalRelationship(MaritalRelationship obj) {

        if (obj != null) {

            try {
                obj.delete(flush: true)

                dataReconciliationService.memberStatusReconcialiationBy([obj.memberA?.id, obj.memberB?.id])

            } catch(Exception ex) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: errorMessageService.getRawMessagesText(ex))
            }

            return new JActionResult(result: JActionResult.Result.SUCCESS, message: generalUtilitiesService.getMessage("rawDomain.helpers.delete.record.deleted.label"))
        }

        return new JActionResult(result: JActionResult.Result.ERROR, message: generalUtilitiesService.getMessage("rawDomain.helpers.update.record.notfound.label", ["MaritalRelationship"]))

    }

    JActionResult updateResidencyField(String id, String columnName, String columnValue) {

        /*
         Available editable columns: startType, startDate, endType, endDate
         */



        def residency = Residency.get(id)
        println "column: ${columnName}, value: ${columnValue}, res.enddate=${residency?.endDate}"
        if (residency == null) {
            return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.record.notfound.label", ["Residency"]))
        }

        if (columnName == null) {
            return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.record.column.not.available.label"))
        }

        def previousResidency = residencyService.getPreviousResidency(residency)
        def nextResidency = residencyService.getNextResidency(residency)

        if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_START_TYPE)) {
            def newStartType = ResidencyStartType.getFrom(columnValue) //BIR,ENU,ENT,XEN

            if (previousResidency != null) {
                def previousEndType = previousResidency.endType

                if (newStartType==ResidencyStartType.BIRTH || newStartType==ResidencyStartType.ENUMERATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.starttype.enu.bir.error.label"))
                }
                if (newStartType==ResidencyStartType.INTERNAL_INMIGRATION && previousEndType!=ResidencyEndType.INTERNAL_OUTMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.starttype.ent.previous.endtype.error.label"))
                }
                if (newStartType==ResidencyStartType.EXTERNAL_INMIGRATION && previousEndType!=ResidencyEndType.EXTERNAL_OUTMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.starttype.xen.previous.endtype.error.label"))
                }
                if (previousEndType==ResidencyEndType.NOT_APPLICABLE || previousEndType==ResidencyEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.starttype.previous.endtype.na.dth.error.label", [columnValue]))
                }
            } else {
                if (newStartType==ResidencyStartType.INTERNAL_INMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.starttype.ent.no.previous.error.label"))
                }
            }


            residency.startType = newStartType

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_START_DATE)) {
            def newStartDate = StringUtil.toLocalDate(columnValue)

            if (newStartDate == null) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.startdate.not.null.error.label"))
            }
            if (newStartDate > LocalDate.now()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.startdate.not.greater.today.error.label"))
            }
            if (residency?.member?.dob != null && newStartDate < residency?.member?.dob) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.startdate.not.before.dob.error.label"))
            }
            if (residency.endDate != null && newStartDate >= residency.endDate) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.startdate.not.greater.enddate.error.label"))
            }
            if (previousResidency != null) {
                if (previousResidency.endDate != null && newStartDate < previousResidency.endDate) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.startdate.not.before.previous.enddate.error.label", [columnValue]))
                }
            }


            residency.startDate = newStartDate

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_END_TYPE)) {
            def newEndType = ResidencyEndType.getFrom(columnValue) //CHG,EXT,DTH

            if (nextResidency != null) {

                if (newEndType==ResidencyEndType.NOT_APPLICABLE || newEndType==ResidencyEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.endtype.na.dth.exists.next.error.label", [columnValue]))
                }
                if (newEndType==ResidencyEndType.INTERNAL_OUTMIGRATION && nextResidency.startType!=ResidencyStartType.INTERNAL_INMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.endtype.chg.next.startype.error.label"))
                }
                if (newEndType==ResidencyEndType.EXTERNAL_OUTMIGRATION && nextResidency.startType!=ResidencyStartType.EXTERNAL_INMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.endtype.ext.next.startype.error.label"))
                }

            } else {
                if (newEndType==ResidencyEndType.INTERNAL_OUTMIGRATION) {
                    //Cannot be CHG because there is no a new residency with ENT
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.endtype.chg.no.next.error.label"))
                }
            }

            residency.endType = newEndType

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_END_DATE)) {
            def newEndDate = StringUtil.toLocalDate(columnValue)

            if (newEndDate == null && residency.endType != ResidencyEndType.NOT_APPLICABLE) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.enddate.null.endtype.not.na.error.label"))
            }
            if (newEndDate != null && newEndDate > LocalDate.now()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.enddate.not.greater.today.error.label"))
            }
            if (residency?.member?.dob != null && (newEndDate != null && newEndDate < residency?.member?.dob)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.enddate.not.before.dob.error.label"))
            }
            if (residency.startDate != null && (newEndDate != null && newEndDate <= residency.startDate)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.enddate.not.before.startdate.error.label"))
            }
            if (nextResidency != null) {
                if (nextResidency.startDate != null && newEndDate != null && newEndDate > nextResidency.startDate) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.enddate.not.greater.next.startdate.error.label", [columnValue]))
                }
            }

            residency.endDate = newEndDate
        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_HOUSEHOLD)) {
            //Evaluate existing of household and update values
            def household = Household.findByCode(columnValue)

            if (household == null) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.residency.household.not.found.error.label", [columnValue]))
            }

            residency.household = household
            residency.householdCode = household.code
        }

        residency.save(flush: true)

        return new JActionResult(result: JActionResult.Result.SUCCESS, message: message("rawDomain.helpers.update.record.updated.label"))
    }

    JActionResult updateHeadRelationshipField(String id, String columnName, String columnValue) {

        /*
         Available editable columns: startType, startDate, endType, endDate, headRelationshipType
         */

        //println "column: ${columnName}, value: ${columnValue}"

        def headRelationship = HeadRelationship.get(id)

        if (headRelationship == null) {
            return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.record.notfound.label", ["HeadRelationship"]))
        }

        if (columnName == null) {
            return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.record.column.not.available.label"))
        }

        def previousHeadRelationship = headRelationshipService.getPreviousHeadRelationship(headRelationship)
        def nextHeadRelationship = headRelationshipService.getNextHeadRelationship(headRelationship)

        if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_START_TYPE)) {
            def newStartType = HeadRelationshipStartType.getFrom(columnValue) //BIR,ENU,ENT,XEN,NHH*

            if (previousHeadRelationship != null) {
                def previousEndType = previousHeadRelationship.endType

                if (newStartType==HeadRelationshipStartType.BIRTH || newStartType==HeadRelationshipStartType.ENUMERATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.starttype.enu.bir.error.label"))
                }
                if (newStartType==HeadRelationshipStartType.INTERNAL_INMIGRATION && previousEndType!=HeadRelationshipEndType.INTERNAL_OUTMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.starttype.ent.previous.endtype.error.label"))
                }
                if (newStartType==HeadRelationshipStartType.EXTERNAL_INMIGRATION && previousEndType!=HeadRelationshipEndType.EXTERNAL_OUTMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.starttype.xen.previous.endtype.error.label"))
                }
                if (newStartType==HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD && (previousEndType!=HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD && previousEndType!=HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD)) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.starttype.nhh.previous.endtype.error.label"))
                }
                if (previousEndType==HeadRelationshipEndType.NOT_APPLICABLE || previousEndType==HeadRelationshipEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.starttype.previous.endtype.na.dth.error.label", [columnValue]))
                }
            } else {
                if (newStartType==HeadRelationshipStartType.INTERNAL_INMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.starttype.ent.no.previous.error.label"))
                }
            }


            headRelationship.startType = newStartType

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_START_DATE)) {
            def newStartDate = StringUtil.toLocalDate(columnValue)

            if (newStartDate == null) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.startdate.not.null.error.label"))
            }
            if (newStartDate > LocalDate.now()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.startdate.not.greater.today.error.label"))
            }
            if (headRelationship?.member?.dob != null && newStartDate < headRelationship?.member?.dob) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.startdate.not.before.dob.error.label"))
            }
            if (headRelationship.endDate != null && newStartDate >= headRelationship.endDate) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.startdate.not.greater.enddate.error.label"))
            }
            if (previousHeadRelationship != null) {
                if (previousHeadRelationship.endDate != null && newStartDate < previousHeadRelationship.endDate) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.startdate.not.before.previous.enddate.error.label", [columnValue]))
                }
            }


            headRelationship.startDate = newStartDate

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_END_TYPE)) {
            def newEndType = HeadRelationshipEndType.getFrom(columnValue) //CHG,EXT,DTH,DHH*,CHH*

            if (nextHeadRelationship != null) {

                if (newEndType==HeadRelationshipEndType.NOT_APPLICABLE || newEndType==HeadRelationshipEndType.DEATH) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.endtype.na.dth.exists.next.error.label", [columnValue]))
                }
                if (newEndType==HeadRelationshipEndType.INTERNAL_OUTMIGRATION && nextHeadRelationship.startType!=HeadRelationshipStartType.INTERNAL_INMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.endtype.chg.next.startype.error.label"))
                }
                if (newEndType==HeadRelationshipEndType.EXTERNAL_OUTMIGRATION && nextHeadRelationship.startType!=HeadRelationshipStartType.EXTERNAL_INMIGRATION) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.endtype.ext.next.startype.error.label"))
                }
                if ((newEndType==HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD || newEndType==HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD) && nextHeadRelationship.startType!=HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.endtype.chh.dhh.next.startype.error.label", [columnValue]))
                }

            } else {
                if (newEndType==HeadRelationshipEndType.INTERNAL_OUTMIGRATION) {
                    //Cannot be CHG because there is no a new headrelationship with ENT
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.endtype.chg.no.next.error.label"))
                }
                if (newEndType==HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD || newEndType==HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.endtype.chh.dhh.no.next.error.label"))
                }
            }

            headRelationship.endType = newEndType

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_END_DATE)) {
            def newEndDate = StringUtil.toLocalDate(columnValue)

            if (newEndDate == null && headRelationship.endType != HeadRelationshipEndType.NOT_APPLICABLE) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.enddate.null.endtype.not.na.error.label"))
            }
            if (newEndDate != null && newEndDate > LocalDate.now()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.enddate.not.greater.today.error.label"))
            }
            if (headRelationship?.member?.dob != null && newEndDate != null && newEndDate < headRelationship?.member?.dob) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.enddate.not.before.dob.error.label"))
            }
            if (headRelationship.startDate != null && newEndDate != null && newEndDate <= headRelationship.startDate) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.enddate.not.before.startdate.error.label"))
            }
            if (nextHeadRelationship != null) {
                if (nextHeadRelationship.startDate != null && newEndDate != null && newEndDate > nextHeadRelationship.startDate) {
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.enddate.not.greater.next.startdate.error.label", [columnValue]))
                }
            }

            headRelationship.endDate = newEndDate
        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_HEAD_RELATIONSHIP_TYPE)) {
            def newRelationshipType = HeadRelationshipType.getFrom(columnValue)

            //If nrt == HOH

            headRelationship.relationshipType = newRelationshipType
        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_HOUSEHOLD)) {
            //Evaluate existing of household and update values
            def household = Household.findByCode(columnValue)

            if (household == null) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.household.not.found.error.label", [columnValue]))
            }

            headRelationship.household = household
            headRelationship.householdCode = household.code
        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_HEAD)) {
            //Evaluate existing of household and update values
            def member = Member.findByCode(columnValue)

            if (member == null) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.headrelationship.head.not.found.error.label", [columnValue]))
            }

            headRelationship.head = member
            headRelationship.headCode = member.code
        }

        headRelationship.save(flush: true)

        return new JActionResult(result: JActionResult.Result.SUCCESS, message: message("rawDomain.helpers.update.record.updated.label"))
    }

    JActionResult updateMaritalRelationshipField(String id, String columnName, String columnValue) {

        /*
         Available editable columns: startStatus, startDate, endStatus, endDate, isPolygamic
         */

        //println "column: ${columnName}, value: ${columnValue}"

        def maritalRelationship = MaritalRelationship.get(id)

        if (maritalRelationship == null) {
            return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.record.notfound.label", ["MaritalRelationship"]))
        }

        if (columnName == null) {
            return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.record.column.not.available.label"))
        }

        def prevRelatMemberA = maritalRelationshipService.getPreviousRelationshipMemberA(maritalRelationship)
        def prevRelatMemberB = maritalRelationshipService.getPreviousRelationshipMemberB(maritalRelationship)
        def nextRelatMemberA = maritalRelationshipService.getNextRelationshipMemberA(maritalRelationship)
        def nextRelatMemberB = maritalRelationshipService.getNextRelationshipMemberB(maritalRelationship)
        def isPolygamicRelationship = maritalRelationshipService.isPolygamicRelationship(maritalRelationship)

        if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_START_STATUS)) {
            def newStartStatus = MaritalStartStatus.getFrom(columnValue)

            //MAR, LIV - Changing the startStatus doesnt create any issue

            maritalRelationship.startStatus = newStartStatus

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_START_DATE)) {
            def newStartDate = StringUtil.toLocalDate(columnValue)

            //default date checks despite polygamic
            if (newStartDate == null) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.startdate.not.null.error.label"))
            }
            if (newStartDate > LocalDate.now()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.startdate.not.greater.today.error.label"))
            }
            if ((maritalRelationship?.memberA?.dob != null && newStartDate < maritalRelationship?.memberA?.dob) ||
                (maritalRelationship?.memberB?.dob != null && newStartDate < maritalRelationship?.memberB?.dob)) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.startdate.not.before.dob.error.label"))
            }
            if (maritalRelationship.endDate != null && newStartDate >= maritalRelationship.endDate) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.startdate.not.greater.enddate.error.label"))
            }

            if (!isPolygamicRelationship) {
                //must check dates of other relationships
                if (prevRelatMemberA != null) {
                    if (prevRelatMemberA.endDate != null && newStartDate < prevRelatMemberA.endDate) {
                        return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.startdate.not.before.previous.enddate.error.label", [columnValue, "A"]))
                    }
                }
                if (prevRelatMemberB != null) {
                    if (prevRelatMemberB.endDate != null && newStartDate < prevRelatMemberB.endDate) {
                        return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.startdate.not.before.previous.enddate.error.label", [columnValue, "B"]))
                    }
                }
            }

            maritalRelationship.startDate = newStartDate

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_END_STATUS)) {
            def newEndStatus = MaritalEndStatus.getFrom(columnValue) //DIV,SEP,WID

            //if not polygamic - check NA and next relationship
            if (!isPolygamicRelationship) {
                if (newEndStatus==MaritalEndStatus.NOT_APPLICABLE) {
                    if (nextRelatMemberA != null && nextRelatMemberA.endStatus == MaritalEndStatus.NOT_APPLICABLE) { //member A is married in the next relationship record
                        return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.endstatus.na.exists.next.na.error.label", [columnValue, "A"]))
                    }
                    if (nextRelatMemberB != null && nextRelatMemberB.endStatus == MaritalEndStatus.NOT_APPLICABLE) { //member B is married in the next relationship record
                        return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.endstatus.na.exists.next.na.error.label", [columnValue, "B"]))
                    }
                }
            }

            //check deaths through WID despite of polygamic
            if (newEndStatus == MaritalEndStatus.WIDOWED) {
                def isDeadA = deathService.hasAnyDeathRecord(maritalRelationship.memberA)
                def isDeadB = deathService.hasAnyDeathRecord(maritalRelationship.memberB)

                if (!isDeadA && !isDeadB) {
                    //cannot set WID because neither members are dead
                    return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.endstatus.wid.nodeath.error.label"))
                }
            }

            maritalRelationship.endStatus = newEndStatus

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_END_DATE)) {
            def newEndDate = StringUtil.toLocalDate(columnValue)

            if (newEndDate == null && maritalRelationship.endStatus != MaritalEndStatus.NOT_APPLICABLE) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.enddate.null.endtype.not.na.error.label"))
            }
            if (newEndDate != null && newEndDate > LocalDate.now()) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.enddate.not.greater.today.error.label"))
            }
            if (newEndDate != null &&
                ((maritalRelationship?.memberA?.dob != null && newEndDate < maritalRelationship?.memberA?.dob) ||
                 (maritalRelationship?.memberB?.dob != null && newEndDate < maritalRelationship?.memberB?.dob))) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.enddate.not.before.dob.error.label"))
            }
            if (newEndDate != null && maritalRelationship.startDate != null && newEndDate <= maritalRelationship.startDate) {
                return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.enddate.not.before.startdate.error.label"))
            }

            if (!isPolygamicRelationship) {
                if (nextRelatMemberA != null) {
                    if (newEndDate != null && nextRelatMemberA.startDate != null && newEndDate > nextRelatMemberA.startDate) {
                        return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.enddate.not.greater.next.startdate.error.label", [columnValue, "A"]))
                    }
                }
                if (nextRelatMemberB != null) {
                    if (newEndDate != null && nextRelatMemberB.startDate != null && newEndDate > nextRelatMemberB.startDate) {
                        return new JActionResult(result: JActionResult.Result.ERROR, message: message("rawDomain.helpers.update.maritalrelationship.enddate.not.greater.next.startdate.error.label", [columnValue, "B"]))
                    }
                }
            }

            maritalRelationship.endDate = newEndDate

        } else if (columnName.equalsIgnoreCase(DataModelsService.COLUMN_IS_POLYGAMIC)) {
            def newIsPolygamic = "true".equalsIgnoreCase(columnValue)

            maritalRelationship.isPolygamic = newIsPolygamic
        }

        maritalRelationship.save(flush: true)

        return new JActionResult(result: JActionResult.Result.SUCCESS, message: message("rawDomain.helpers.update.record.updated.label"))
    }

    def toHeadRelationshipValues(HeadRelationship obj) {
        def dateUtil = generalUtilitiesService.getDateUtil()

        ['id':     obj.id,
         'code':     obj.memberCode,
         'name':     obj.member.name,
         'gender':   obj.member.gender?.code,
         'dob':      dateUtil.formatYMD(obj.member.dob),
         'household':       obj.household?.code,
         'head':            obj.head?.code,
         'relationshipType': obj.relationshipType?.code,
         'startType':       obj.startType?.code,
         'startDate':       dateUtil.formatYMD(obj.startDate), //StringUtil.formatLocalDate(obj.startDate),
         'endType':         obj.endType?.code,
         'endDate':         dateUtil.formatYMD(obj.endDate), //StringUtil.formatLocalDate(obj.endDate),
         'statusText':      generalUtilitiesService.getMessage(obj.status==null ? ValidatableStatus.ACTIVE.name : obj.status.name),
         'status':          getValidationStatus(obj.status)
        ]
    }

    def toResidencyValues(Residency obj) {
        def dateUtil = generalUtilitiesService.getDateUtil()

        ['id':     obj.id,
         'code':     obj.memberCode,
         'name':     obj.member.name,
         'gender':   obj.member.gender?.code,
         'dob':      dateUtil.formatYMD(obj.member.dob),
         'household':  obj.household?.code,         
         'startType':  obj.startType?.code,
         'startDate':  dateUtil.formatYMD(obj.startDate), //StringUtil.formatLocalDate(obj.startDate),
         'endType':    obj.endType?.code,
         'endDate':    dateUtil.formatYMD(obj.endDate), //StringUtil.formatLocalDate(obj.endDate),
         'statusText': generalUtilitiesService.getMessage(obj.status==null ? ValidatableStatus.ACTIVE.name : obj.status.name),
         'status':     getValidationStatus(obj.status)
        ]
    }

    def toMaritalRelationshipValues(MaritalRelationship obj) {
        def dateUtil = generalUtilitiesService.getDateUtil()

        ['id':     obj.id,
         'memberA_code': obj.memberA_code,
         'memberB_code': obj.memberB_code,
         'isPolygamic':  obj.isPolygamic==true,
         'startStatus':  obj.startStatus?.code,
         'startDate':    dateUtil.formatYMD(obj.startDate), // StringUtil.formatLocalDate(obj.startDate),
         'endStatus':    obj.endStatus?.code,
         'endDate':      dateUtil.formatYMD(obj.endDate), //StringUtil.formatLocalDate(obj.endDate),
         'statusText':   generalUtilitiesService.getMessage(obj.status==null ? ValidatableStatus.ACTIVE.name : obj.status.name),
         'status':       getValidationStatus(obj.status)
        ]
    }

    LocalDate getNextDate(LocalDate date) {
        if (date == null) return LocalDate.now()

        return date.plusDays(1)
    }

    Member findHeadFor(Member member, Household household, LocalDate residencyStartDate) {
        def relationships = headRelationshipService.getHouseholdHeadRelatioships(household)

        for (def hr : relationships) {
            if (hr.endDate != null) {
                if (residencyStartDate >= hr.startDate && residencyStartDate <= hr.endDate) {
                    return hr.member
                }
            } else if (hr.endType == HeadRelationshipEndType.NOT_APPLICABLE){
                if (residencyStartDate >= hr.startDate) {
                    return hr.member
                }
            }
        }

        return member
    }

    private String message(String code) {
        generalUtilitiesService.getMessage(code, code)
    }

    private String message(String code, List args) {
        generalUtilitiesService.getMessage(code, args.toArray(), code)
    }

    RawDependencyCheckResult checkDependencyErrors(List<RawMessage> rawMessages) {

        for (def rmsg : rawMessages) {
            if (rmsg?.text?.matches("The event with .+ will be skipped because a previous event .+ has errors")){
                def id = rmsg.text.replaceAll(".+\\)=>\\[id=", "")
                id = id.replaceAll("\\].+", "")

                return new RawDependencyCheckResult(hasDependencyError: true, dependencyEventId: id)
            }
        }

        return new RawDependencyCheckResult(hasDependencyError: false)
    }

    def updatePregnancyRegistration(RawPregnancyOutcome rawPregnancyOutcome, String previousCode) {
        //find raw pregnancy registration related to this outcome if it exists and change its code too
        def rawPregReg = RawPregnancyRegistration.findByCodeAndVisitCode(previousCode, rawPregnancyOutcome.visitCode)

        if (rawPregReg != null) {
            def newCode = rawPregnancyOutcome.code
            rawPregReg.code = newCode
            rawPregReg.save(flush: true)

            def prePreg = PregnancyRegistration.findByCollectedId(rawPregReg.id)
            if (prePreg != null) {
                prePreg.code = newCode
                prePreg.save(flush: true)
            }
        }
    }

    def updatePregnancyOutcome(RawPregnancyRegistration rawPregnancyRegistration, String previousCode) {
        //find raw pregnancy registration related to this outcome if it exists and change its code too
        def rawPregOut = RawPregnancyOutcome.findByCodeAndVisitCode(previousCode, rawPregnancyRegistration.visitCode)

        if (rawPregOut != null) {
            def newCode = rawPregnancyRegistration.code
            rawPregOut.code = newCode
            rawPregOut.save(flush: true)

            def preOut = PregnancyOutcome.findByCollectedId(rawPregOut.id)
            if (preOut != null) {
                preOut.code = newCode
                preOut.save(flush: true)
            }
        }
    }

    class RawDependencyCheckResult {
        boolean hasDependencyError = false
        String dependencyEventId = ""
    }
}
