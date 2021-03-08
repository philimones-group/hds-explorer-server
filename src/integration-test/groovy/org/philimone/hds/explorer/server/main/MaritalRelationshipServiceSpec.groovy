package org.philimone.hds.explorer.server.main

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.collect.raw.RawMaritalRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.HouseholdService
import org.philimone.hds.explorer.server.model.main.MaritalRelationshipService
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.MemberService
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.RegionService
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.model.settings.generator.CodeGeneratorService
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import java.time.LocalDateTime

@Integration
@Transactional //@Rollback
class MaritalRelationshipServiceSpec extends Specification {

    @Autowired
    ErrorMessageService errorMessageService
    @Autowired
    MemberService memberService
    @Autowired
    HouseholdService householdService
    @Autowired
    RegionService regionService
    @Autowired
    UserService userService
    @Autowired
    MaritalRelationshipService maritalRelationshipService
    @Autowired
    CodeGeneratorService codeGeneratorService

    def setupAll() {

        setupUsers()
        setupRegions()
        setupHouseholds()
        setupMembers()
    }

    def setupUsers(){
        def admin = Role.findByName(Role.LABEL_ADMINISTRATOR)
        def user = new User(code: "DF1", firstName: "Dragon", lastName: "Fire", username: "dragon", password: "fire")

        userService.addUser(user, [admin])
    }

    def setupRegions(){
        def rg1 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Maputo"), regionName: "Maputo", parentCode: "")
        def rg11 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Matola"), regionName: "Matola", parentCode: "MAP")
        def rg111 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Txumene"), regionName: "Txumene", parentCode: "MAT")
        def rg112 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Fomento"), regionName: "Fomento", parentCode: "MAT")
        def rg2 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Gaza"), regionName: "Gaza", parentCode: "")
        def rg21 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Xai-Xai"), regionName: "Xai-Xai", parentCode: "GAZ")
        def rg211 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Zongoene"), regionName: "Zongoene", parentCode: "XAI")
        def rg212 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Limpopo"), regionName: "Limpopo", parentCode: "XAI")

        regionService.createRegion(rg1)
        regionService.createRegion(rg11)
        regionService.createRegion(rg111)
        regionService.createRegion(rg112)
        regionService.createRegion(rg2)
        regionService.createRegion(rg21)
        regionService.createRegion(rg211)
        regionService.createRegion(rg212)
    }

    def setupHouseholds(){

        def user = User.findByUsername("dragon")
        def region = Region.findByCode("TXU")

        def rw1 = new RawHousehold(id: "u1", regionCode: region.code, householdCode: householdService.generateCode(region, user), householdName: "Macandza House", headCode: "", headName: "", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        rw1.save()
        def res1 = householdService.createHousehold(rw1)

        def rw2 = new RawHousehold(id: "u2", regionCode: region.code, householdCode: householdService.generateCode(region, user), householdName: "George Benson", headCode: "", headName: "", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        rw2.save()
        def res2 = householdService.createHousehold(rw2)

        //println "household 1 - ${res1.domainInstance}"
        //printResults(res1)
        //println "household 2 - ${res2.domainInstance}"
        //printResults(res2)

        //println "households - ${Household.findAll().size()}"
    }

    def setupMembers(){

        //println "*2 households - ${Household.findAll().size()}"

        def user = User.findByUsername("dragon")
        def household1 = Household.findAll().first()
        def household2 = Household.findAll().last()

        /*
        def m = new Member(id: "Unknown Member", code: Codes.MEMBER_UNKNOWN_CODE, name: "member.unknown.label", gender: Gender.MALE, dob: GeneralUtil.getDate(1900,1,1), maritalStatus: MaritalStatus.SINGLE)
        m.save(flush: true)
        println("member: "+m.errors)
        */

        def rw1 = new RawMember(id: "uuuid1", code: memberService.generateCode(household1), name: "John Benedit Macandza", gender: Gender.MALE.code, dob: GeneralUtil.getDate(1988,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res1 = memberService.createMember(rw1)

        def rw2 = new RawMember(id: "uuuid2", code: memberService.generateCode(household1), name: "Catarina Loyd Macandza", gender: Gender.FEMALE.code, dob: GeneralUtil.getDate(1993,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res2 = memberService.createMember(rw2)

        def rw3 = new RawMember(id: "uuuid3", code: memberService.generateCode(household2), name: "George Benson", gender: Gender.MALE.code, dob: GeneralUtil.getDate(1988,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res3 = memberService.createMember(rw3)

        def rw4 = new RawMember(id: "uuuid4", code: memberService.generateCode(household2), name: "Joyce Mary Benson", gender: Gender.FEMALE.code, dob: GeneralUtil.getDate(1993,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res4 = memberService.createMember(rw4)

        //printResults(res1)
        //printResults(res2)
        //printResults(res3)
        //printResults(res4)
    }

    def cleanup() {
    }

    def printResults(RawExecutionResult result){
        if (result == null) return
        println("status: ${result.status}")
        printRawMessages(result.errorMessages)
        println()
    }

    def printRawMessages(List<RawMessage> errorMessages){
        errorMessages.each { err ->
            println "${err.columns} -> ${err.text}"
        }
    }

    def printMaritalRelationship(MaritalRelationship maritalRelationship){
        if (maritalRelationship==null) return null
        println "maritalRelationship(id=${maritalRelationship.id},memberA=${maritalRelationship.memberA_code},memberB=${maritalRelationship.memberB_code},startstatus=${maritalRelationship.startStatus},startdate=${StringUtil.format(maritalRelationship?.startDate)},endstatus=${maritalRelationship.endStatus},enddate=${StringUtil.format(maritalRelationship?.endDate)})"
    }

    /*
     * 1. Test Creation of maritalRelationship
     * 2. Test Closing the maritalRelationship
     * 3. Test Creating 2 Relationships of same member
     * 4. Test Closing a Closed maritalRelationship
     */
    @Ignore
    void "Test Creation of Marital Relationship"() {

        println "\n#### Test Creation of Marital Relationship ####"

        def count = -1

        setupAll()

        //println "*3 households - ${Household.findAll().size()}"


        //create new maritalRelationship
        def household1 = Household.findByName("Macandza House")
        def household2 = Household.findByName("George Benson")
        def member11 = Member.findByName("John Benedit Macandza")
        def member12 = Member.findByName("Catarina Loyd Macandza")
        def member21 = Member.findByName("George Benson")
        def member22 = Member.findByName("Joyce Mary Benson")

        //println "household1: ${household1}, check: ${Household.count()}"
        //println "member1: ${member11}, check: ${Member.count()}"
        //println "household2: ${household2}, check: ${Household.count()}"
        //println "member2: ${member21}, check: ${Member.count()}"

        //create new marital relation
        def rw1 = new RawMaritalRelationship (
                id: "uuuid1",
                memberA: member11.code,
                memberB: member12.code,
                startStatus: "LIV",
                startDate: GeneralUtil.getDate(2020,06,17),
                endStatus: "",
                endDate: ""
        )

        def rw2 = new RawMaritalRelationship(
                id: "uuuid2",
                memberA: member12.code,
                memberB: member21.code,
                startStatus: "MAR",
                startDate: GeneralUtil.getDate(2020,05,04),
                endStatus: "",
                endDate: ""
        )

        rw1.save()
        rw2.save()

        //println "Raw Member Errors:"
        //printRawMessages(errorMessageService.getRawMessages(rw1))
        //println()
        //printRawMessages(errorMessageService.getRawMessages(rw2))
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result1 = maritalRelationshipService.createMaritalRelationship(rw1)
        printResults(result1)

        def result2 = maritalRelationshipService.createMaritalRelationship(rw2)
        printResults(result2)

        printMaritalRelationship(result1?.domainInstance)
        printMaritalRelationship(result2?.domainInstance)


        count = MaritalRelationship.count()

        expect:
        count == 1
    }

    //@Ignore
    void "Test Closing of Marital Relationship"() {
        println "\n#### Test Closing of Marital Relationship ####"

        setupAll()

        //println "*3 households - ${Household.findAll().size()}"

        //create new maritalRelationship
        def household1 = Household.findByName("Macandza House")
        def household2 = Household.findByName("George Benson")
        def member11 = Member.findByName("John Benedit Macandza")
        def member12 = Member.findByName("Catarina Loyd Macandza")
        def member21 = Member.findByName("George Benson")
        def member22 = Member.findByName("Joyce Mary Benson")

        //println "household1: ${household1}, check: ${Household.count()}"
        //println "member1: ${member11}, check: ${Member.count()}"
        //println "household2: ${household2}, check: ${Household.count()}"
        //println "member2: ${member21}, check: ${Member.count()}"

        def rw1 = new RawMaritalRelationship (
                id: "uuuid1",
                memberA: member11.code,
                memberB: member12.code,
                startStatus: "LIV",
                startDate: GeneralUtil.getDate(2020,6,17),
                endStatus: "",
                endDate: ""
        )

        def rw2 = new RawMaritalRelationship(
                id: "uuuid2",
                memberA: member12.code,
                memberB: member21.code,
                startStatus: "MAR",
                startDate: GeneralUtil.getDate(2020,10,4),
                endStatus: "",
                endDate: ""
        )

        def rw1close = new RawMaritalRelationship(
                id: "uuuid3",
                memberA: member12.code,
                memberB: member11.code,
                startStatus: "",
                startDate: null,
                endStatus: "DIV",
                endDate: GeneralUtil.getDate(2020,8,15)
        )

        rw1.save()
        rw1close.save()
        rw2.save()

        //println "Raw Member Errors:"
        //printRawMessages(errorMessageService.getRawMessages(rw1))
        //println()
        //printRawMessages(errorMessageService.getRawMessages(rw2))
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result1 = maritalRelationshipService.createMaritalRelationship(rw1)
        printResults(result1)
        printMaritalRelationship(result1?.domainInstance)

        def result2 = maritalRelationshipService.closeMaritalRelationship(rw1close)
        printResults(result2)
        printMaritalRelationship(result2?.domainInstance)

        def result4 = maritalRelationshipService.closeMaritalRelationship(rw1close)
        printResults(result4)
        printMaritalRelationship(result4?.domainInstance)

        def result3 = maritalRelationshipService.createMaritalRelationship(rw2)
        printResults(result3)
        printMaritalRelationship(result3?.domainInstance)

        expect:
        MaritalRelationship.count()==2
    }
}
