package org.philimone.hds.explorer.server.main

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.collect.raw.RawInMigration
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.collect.raw.RawVisit
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.HouseholdService
import org.philimone.hds.explorer.server.model.main.InMigration
import org.philimone.hds.explorer.server.model.main.InMigrationService
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.MemberService
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.RegionService
import org.philimone.hds.explorer.server.model.main.Residency
import org.philimone.hds.explorer.server.model.main.Round
import org.philimone.hds.explorer.server.model.main.RoundService
import org.philimone.hds.explorer.server.model.main.Visit
import org.philimone.hds.explorer.server.model.main.VisitService
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.model.settings.generator.CodeGeneratorService
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.LocalDateTime

@Integration
@Transactional
class InMigrationServiceSpec extends Specification {

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
    CodeGeneratorService codeGeneratorService
    @Autowired
    RoundService roundService
    @Autowired
    VisitService visitService
    @Autowired
    InMigrationService inMigrationService

    def setupAll() {
        setupUsers()
        setupRegions()
        setupHouseholds()
        setupMembers()
        setupRounds()
        setupVisits()
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
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res1 = memberService.createMember(rw1)

        def rw2 = new RawMember(id: "uuuid2", code: memberService.generateCode(household1), name: "Catarina Loyd Macandza", gender: Gender.FEMALE.code, dob: GeneralUtil.getDate(1993,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res2 = memberService.createMember(rw2)

        def rw3 = new RawMember(id: "uuuid3", code: memberService.generateCode(household2), name: "George Benson", gender: Gender.MALE.code, dob: GeneralUtil.getDate(1988,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res3 = memberService.createMember(rw3)

        def rw4 = new RawMember(id: "uuuid4", code: memberService.generateCode(household2), name: "Joyce Mary Benson", gender: Gender.FEMALE.code, dob: GeneralUtil.getDate(1993,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", householdCode: household1.code,
                collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        def res4 = memberService.createMember(rw4)

        //printResults(res1)
        //printResults(res2)
        //printResults(res3)
        //printResults(res4)
    }

    def setupRounds(){
        def result1 = roundService.createRound(GeneralUtil.getDate(2021, 1, 1), GeneralUtil.getDate(2021, 1, 31), "baseline round")
        //def result2 = roundService.createRound(GeneralUtil.getDate(2021, 1, 31), GeneralUtil.getDate(2021, 1, 28), "first round")
        //def result3 = roundService.createRound(GeneralUtil.getDate(2021, 2, 1), GeneralUtil.getDate(2021, 1, 20), "second round")

        //printResults(result1)
        //printResults(result2)
        //printResults(result3)

        //print(result1?.domainInstance)
        //println()
        //print(result2?.domainInstance)
        //println()
        //print(result3?.domainInstance)
        //println()
    }

    def setupVisits(){
        //create new residency
        def household1 = Household.findByName("Macandza House")
        def household2 = Household.findByName("George Benson")
        def member11 = Member.findByName("John Benedit Macandza")
        def member12 = Member.findByName("Catarina Loyd Macandza")
        def member21 = Member.findByName("George Benson")
        //def member22 = Member.findByName("Joyce Mary Benson")

        //println "household1: ${household1}, check: ${Household.count()}"
        //println "member1: ${member11}, check: ${Member.count()}"
        //println "household2: ${household2}, check: ${Household.count()}"
        //println "member2: ${member21}, check: ${Member.count()}"

        //println "rounds: ${Round.count()}"

        def rv1 = new RawVisit(id: "uuuid1", code: codeGeneratorService.generateVisitCode(household1), householdCode: household1.code,
                visitDate: GeneralUtil.getDate(2021, 2, 3), visitLocation: VisitLocationItem.HOME.code, roundNumber: 0,
                respondentCode: member11.code, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2021, 3, 4, 0, 0, 0)
        )

        def rv2 = new RawVisit(id: "uuuid2", code: codeGeneratorService.generateVisitCode(household1), householdCode: household1.code,
                visitDate: GeneralUtil.getDate(2021, 2, 4), visitLocation: VisitLocationItem.HOME.code, roundNumber: 0,
                respondentCode: member12.code, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2021, 3, 4, 0, 0, 0)
        )

        def rv3 = new RawVisit(id: "uuuid3", code: codeGeneratorService.generateVisitCode(household2), householdCode: household2.code,
                visitDate: GeneralUtil.getDate(2021, 2, 4), visitLocation: VisitLocationItem.OTHER_PLACE.code, visitLocationOther: "HEALTHCENTER", roundNumber: 0,
                respondentCode: member21.code, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2021, 2, 4, 0, 0, 0)
        )

        //rv1.save()
        //rv2.save()
        //rv3.save()

        //println "Raw Member Errors:"
        //printRawMessages(errorMessageService.getRawMessages(rw1))
        //println()
        //printRawMessages(errorMessageService.getRawMessages(rw2))
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result1 = visitService.createVisit(rv1)
        def result2 = visitService.createVisit(rv2)
        def result3 = visitService.createVisit(rv3)

        //printResults(result1)
        //println(result1?.domainInstance)
        //printResults(result2)
        //println(result2?.domainInstance)
        //printResults(result3)
        //println(result3?.domainInstance)
    }


    def cleanup() {
    }

    def printResults(RawExecutionResult result){
        if (result == null) return
        println("status: ${result.status}")
        printRawMessages(result.errorMessages)
    }

    def printRawMessages(List<RawMessage> errorMessages){
        errorMessages.each { err ->
            println "${err.columns} -> ${err.text}"
        }
    }

    def printInMig(InMigration inmig){
        if (inmig==null) return println("EMPTY")
        println "inmig(id=${inmig.id},type=${inmig.type},v.code=${inmig.visitCode},v.household=${inmig.destinationCode},v.respondent=${inmig?.memberCode})"
    }

    def print(Residency residency){
        if (residency==null) return null
        println "residency(id=${residency.id},m.code=${residency.memberCode},h.code=${residency.householdCode},starttype=${residency.startType},startdate=${StringUtil.format(residency?.startDate)},endtype=${residency.endType},enddate=${StringUtil.format(residency?.endDate)})"
    }

    def print(HeadRelationship headRelationship){
        if (headRelationship==null) return null
        println "headRelationship(id=${headRelationship.id},m.code=${headRelationship.memberCode},h.code=${headRelationship.householdCode},starttype=${headRelationship.startType},startdate=${StringUtil.format(headRelationship?.startDate)},endtype=${headRelationship.endType},enddate=${StringUtil.format(headRelationship?.endDate)})"
    }

    def print(Visit visit){
        if (visit==null) return null
        println "visit(id=${visit.id},v.code=${visit.code},v.household=${visit.householdCode},v.round=${visit.roundNumber},v.visitDate=${StringUtil.format(visit?.visitDate)},v.location=${visit.visitLocation},v.respondent=${visit?.respondentCode})"
    }

    def print(Round round){
        if (round==null) return null
        println "round(id=${round.id},r.number=${round.roundNumber},r.startdate=${round.startDate},r.enddate=${round.endDate},r.description=${round?.description})"
    }

    void "Test InMigrations - Insert Using External InMigrations"() {
        println "\n#### Test Creation of In Migrations 1 ####"

        setupAll()

        //println "*3 households - ${Household.findAll().size()}"

        //create new residency
        def household1 = Household.findByName("Macandza House")
        def household2 = Household.findByName("George Benson")
        def member11 = Member.findByName("John Benedit Macandza")
        def member12 = Member.findByName("Catarina Loyd Macandza")
        def member21 = Member.findByName("George Benson")
        def member22 = Member.findByName("Joyce Mary Benson")

        def visitHousehold1v1 = Visit.findAllByHousehold(household1).first().code
        def visitHousehold1v2 = Visit.findAllByHousehold(household1).last().code
        def visitHousehold2v1 = Visit.findByHousehold(household2)?.code

        //println "household1: ${household1}, check: ${Household.count()}"
        //println "member1: ${member11}, check: ${Member.count()}"
        //println "household2: ${household2}, check: ${Household.count()}"
        //println "member2: ${member21}, check: ${Member.count()}"

        //println "rounds: ${Round.count()}"

        def rin1 = new RawInMigration(
                id: "uuuid1",
                visitCode: visitHousehold1v1,
                memberCode: member11.code,
                migrationType: InMigrationType.EXTERNAL.code,
                originCode: null,
                originOther: "SA - JOHANNESBURG",
                destinationCode: household1.code,
                migrationDate: GeneralUtil.getDate(2020, 1, 19),
                migrationReason: "RETURNING FROM MINES",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )

        def rin2 = new RawInMigration(
                id: "uuuid2",
                visitCode: visitHousehold1v1,
                memberCode: member12.code,
                migrationType: InMigrationType.EXTERNAL.code,
                originCode: null,
                originOther: "SA - JOHANNESBURG",
                destinationCode: household1.code,
                migrationDate: GeneralUtil.getDate(2020, 1, 19),
                migrationReason: "RETURNING FROM MINES",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )

        def rin3 = new RawInMigration(
                id: "uuuid3",
                visitCode: visitHousehold1v1,
                memberCode: member22.code,
                migrationType: InMigrationType.EXTERNAL.code,
                originCode: null,
                originOther: "SA - JOHANNESBURG",
                destinationCode: household1.code,
                migrationDate: GeneralUtil.getDate(2020, 2, 19),
                migrationReason: "job changes",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )

        def rin4 = new RawInMigration(
                id: "uuuid4",
                visitCode: visitHousehold2v1,
                memberCode: member12.code,
                migrationType: InMigrationType.INTERNAL.code,
                originCode: household1.code,
                originOther: null,
                destinationCode: household2.code,
                migrationDate: GeneralUtil.getDate(2020, 2, 28),
                migrationReason: "job changes",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )

        //rv1.save()
        //rv2.save()
        //rv3.save()

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints





        println("first inmig")
        def result1 = inMigrationService.createInMigration(rin1)
        printResults(result1)
        printInMig(result1?.domainInstance)
        println()

        println("second inmig")
        def result2 = inMigrationService.createInMigration(rin2)
        printResults(result2)
        printInMig(result2?.domainInstance)
        println()


        println("third inmig")
        def result3 = inMigrationService.createInMigration(rin3)
        printResults(result3)
        printInMig(result3?.domainInstance)
        println()

        println("fourth inmig")
        def result4 = inMigrationService.createInMigration(rin4)
        printResults(result4)
        printInMig(result4?.domainInstance)
        println()


        expect:
        InMigration.count()==3
    }
}
