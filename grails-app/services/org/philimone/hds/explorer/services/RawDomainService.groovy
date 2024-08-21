package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
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
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.PartiallyDisabled
import org.philimone.hds.explorer.server.model.main.Residency
import org.philimone.hds.explorer.server.model.main.Visit

@Transactional
class RawDomainService {

    def visitService
    def residencyService
    def headRelationshipService
    def maritalRelationshipService
    def codeGeneratorService
    def generalUtilitiesService

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
        def result = Member.executeQuery("select m.code, m.name, m.gender, m.dob from Member m where m.code = ?0", [memberCode])
        def m = result.size()>0 ? result.first() : null
        return new JMember(m[0], m[1], m[2], m[3])
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

    def toHeadRelationshipValues(HeadRelationship obj) {
        ['id':     obj.id,
         'code':     obj.memberCode,
         'name':     obj.member.name,
         'gender':   obj.member.gender?.code,
         'dob':      StringUtil.formatLocalDate(obj.member.dob),
         'household':       obj.household?.code,
         'head':            obj.head?.code,
         'headRelationshipType': obj.relationshipType?.code,
         'startType':       obj.startType?.code,
         'startDate':       StringUtil.formatLocalDate(obj.startDate),
         'endType':         obj.endType?.code,
         'endDate':         StringUtil.formatLocalDate(obj.endDate),
         'statusText':      generalUtilitiesService.getMessage(obj.status==null ? ValidatableStatus.ACTIVE.name : obj.status.name),
         'status':          getValidationStatus(obj.status)
        ]
    }

    def toResidencyValues(Residency obj) {
        ['id':     obj.id,
         'code':     obj.memberCode,
         'name':     obj.member.name,
         'gender':   obj.member.gender?.code,
         'dob':      StringUtil.formatLocalDate(obj.member.dob),
         'household':  obj.household?.code,         
         'startType':  obj.startType?.code,
         'startDate':  StringUtil.formatLocalDate(obj.startDate),
         'endType':    obj.endType?.code,
         'endDate':    StringUtil.formatLocalDate(obj.endDate),
         'statusText': generalUtilitiesService.getMessage(obj.status==null ? ValidatableStatus.ACTIVE.name : obj.status.name),
         'status':     getValidationStatus(obj.status)
        ]
    }

    def toMaritalRelationshipValues(MaritalRelationship obj) {
        ['id':     obj.id,
         'memberA_code': obj.memberA_code,
         'memberB_code': obj.memberB_code,
         'isPolygamic':  obj.isPolygamic==true,
         'startStatus':  obj.startStatus?.code,
         'startDate':    StringUtil.formatLocalDate(obj.startDate),
         'endStatus':    obj.endStatus?.code,
         'endDate':      StringUtil.formatLocalDate(obj.endDate),
         'statusText':   generalUtilitiesService.getMessage(obj.status==null ? ValidatableStatus.ACTIVE.name : obj.status.name),
         'status':       getValidationStatus(obj.status)
        ]
    }
}
