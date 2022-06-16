package org.philimone.hds.explorer.server.model.collect.raw

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.PregnancyChild
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Residency
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class RawEditExecutionService {

    def regionService
    def householdService
    def memberService
    def errorMessageService


    RawExecutionResult<Region> updateRegion(RawRegion rawDomainInstance) {
        List<RawMessage> errors = new ArrayList<>()


        println "update member"


        Region domainInstance = Region.findByCode(rawDomainInstance.regionCode)

        if (domainInstance != null) {
            domainInstance.name = rawDomainInstance.regionName
            domainInstance.updatedBy = User.findByUsername(rawDomainInstance.collectedBy)
            domainInstance.updatedDate = rawDomainInstance.collectedDate
            domainInstance.save(flush:true)

            if (domainInstance.hasErrors()) {
                //throw an error
                errors.addAll(errorMessageService.getRawMessages(RawEntity.REGION, domainInstance))
            }

        } else {
            //Domain not found -> Can't update Region, Region with code=? was not found in the database
            errors << errorMessageService.getRawMessage("rawEditDomain.region.not.found.label", [rawDomainInstance.regionCode])
        }


        if (errors.size()>0) {
            RawExecutionResult<Region> obj = RawExecutionResult.newErrorResult(RawEntity.REGION, errors)
            return obj
        }

        RawExecutionResult<Region> obj = RawExecutionResult.newSuccessResult(RawEntity.REGION, domainInstance)
        return obj

    }

    RawExecutionResult<Household> updateHousehold(RawHousehold rawDomainInstance) {
        List<RawMessage> errors = new ArrayList<>()

        Household domainInstance = Household.findByCode(rawDomainInstance.householdCode)

        if (domainInstance != null) {
            domainInstance.name = rawDomainInstance.householdName
            ;
            if (domainInstance.gpsLatitude != null){
                domainInstance.gpsLatitude = Double.parseDouble(rawDomainInstance.gpsLat)
                domainInstance.gpsLongitude = Double.parseDouble(rawDomainInstance.gpsLon)
                domainInstance.gpsAltitude = Double.parseDouble(rawDomainInstance.gpsAlt)
                domainInstance.gpsAccuracy = Double.parseDouble(rawDomainInstance.gpsAcc)
            }

            domainInstance.updatedBy = User.findByUsername(rawDomainInstance.collectedBy)
            domainInstance.updatedDate = rawDomainInstance.collectedDate
            domainInstance.save(flush:true)

            if (!domainInstance.hasErrors()) {
                //update members with householdName
                Member.executeUpdate("update Member m set m.householdName=? where m.householdCode=?", [domainInstance.name, domainInstance.code])

            } else {
                //throw an error
                errors.addAll(errorMessageService.getRawMessages(RawEntity.HOUSEHOLD, domainInstance))
            }

        } else {
            //Domain not found -> Can't update Household, Household with code=? was not found in the database

            errors << errorMessageService.getRawMessage("rawEditDomain.household.not.found.label", [rawDomainInstance.householdCode])
        }


        if (errors.size()>0) {
            RawExecutionResult<Household> obj = RawExecutionResult.newErrorResult(RawEntity.HOUSEHOLD, errors)
            return obj
        }

        RawExecutionResult<Household> obj = RawExecutionResult.newSuccessResult(RawEntity.HOUSEHOLD, domainInstance)
        return obj
    }

    RawExecutionResult<Member> updateMember(RawMember rawDomainInstance) {
        List<RawMessage> errors = new ArrayList<>()

        Member domainInstance = Member.findByCode(rawDomainInstance.code)

        if (domainInstance != null) {
            domainInstance.name = rawDomainInstance.name
            domainInstance.gender = Gender.getFrom(rawDomainInstance.gender)
            domainInstance.dob = rawDomainInstance.dob
            domainInstance.mother = Member.findByCode(rawDomainInstance.motherCode)
            domainInstance.motherCode = rawDomainInstance.motherCode
            domainInstance.motherName = rawDomainInstance.motherName
            domainInstance.father = Member.findByCode(rawDomainInstance.fatherCode)
            domainInstance.fatherCode = rawDomainInstance.fatherCode
            domainInstance.fatherName = rawDomainInstance.fatherName
            domainInstance.updatedBy = User.findByUsername(rawDomainInstance.collectedBy)
            domainInstance.updatedDate = rawDomainInstance.collectedDate
            domainInstance.save(flush:true)

            if (!domainInstance.hasErrors()) {
                //update dependants
                Household.executeUpdate("update Household h set h.headName=? where h.headCode=?", [domainInstance.name, domainInstance.code])
                Member.executeUpdate("update Member m set m.fatherName=? where m.fatherCode=?", [domainInstance.name, domainInstance.code])
                Member.executeUpdate("update Member m set m.motherName=? where m.motherCode=?", [domainInstance.name, domainInstance.code])
                Member.executeUpdate("update Member m set m.spouseName=? where m.spouseCode=?", [domainInstance.name, domainInstance.code])

                Residency.executeUpdate("update Residency r set r.startDate=? where r.member.id=? and r.startType=?", [domainInstance.dob, domainInstance.id, ResidencyStartType.BIRTH])
                HeadRelationship.executeUpdate("update HeadRelationship r set r.startDate=? where r.member.id=? and r.startType=?", [domainInstance.dob, domainInstance.id, HeadRelationshipStartType.BIRTH])

                def childMember = PregnancyChild.findByChild(domainInstance)
                if (childMember != null) {
                    def pregnacny = childMember.outcome
                    pregnacny.outcomeDate = domainInstance.dob
                    pregnacny.save(flush:true)
                }


            } else {

                //throw an error
                errors.addAll(errorMessageService.getRawMessages(RawEntity.MEMBER, domainInstance))
            }

        } else {
            //Domain not found -> Can't update Member, Member with code=? was not found in the database

            errors << errorMessageService.getRawMessage("rawEditDomain.member.not.found.label", [rawDomainInstance.memberCode])
        }


        if (errors.size()>0) {
            RawExecutionResult<Member> obj = RawExecutionResult.newErrorResult(RawEntity.MEMBER, errors)
            return obj
        }

        RawExecutionResult<Member> obj = RawExecutionResult.newSuccessResult(RawEntity.MEMBER, domainInstance)
        return obj
    }

}
