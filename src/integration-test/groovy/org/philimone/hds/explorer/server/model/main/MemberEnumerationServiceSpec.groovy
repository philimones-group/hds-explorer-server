package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem
import org.philimone.hds.explorer.server.model.enums.VisitReason
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.OutMigrationType
import org.philimone.hds.explorer.server.model.main.*
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorService
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.LocalDateTime

@Integration
@Transactional //@Rollback
class MemberEnumerationServiceSpec extends Specification {

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
    @Autowired
    OutMigrationService outMigrationService
    @Autowired
    HeadRelationshipService headRelationshipService
    @Autowired
    MemberEnumerationService memberEnumerationService


    def setupAll() {
        setupUsers()
        setupRegions()
        setupHouseholds()
        setupMembers()
        setupRounds()
        setupVisits()
        setupInMigrations()
        setupHeadRelationships()
        setupOutmigrations()
    }

    def setupUsers(){
        def admin = Role.findByName(Role.LABEL_ADMINISTRATOR)
        def user = new User(code: "DF1", firstName: "Dragon", lastName: "Fire", username: "dragon", password: "fire")

        userService.addUser(user, [admin])
    }

    def setupRegions(){
        def rg1 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Maputo"), regionName: "Maputo", parentCode: "")
        def rg11 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Matola"), regionName: "Matola", parentCode: "MAP")
        def rg111 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Txumene"), regionName: "Txumene", parentCode: "MAT")
        def rg112 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Fomento"), regionName: "Fomento", parentCode: "MAT")
        def rg2 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Gaza"), regionName: "Gaza", parentCode: "")
        def rg21 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Xai-Xai"), regionName: "Xai-Xai", parentCode: "GAZ")
        def rg211 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Zongoene"), regionName: "Zongoene", parentCode: "XAI")
        def rg212 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode(null, "Limpopo"), regionName: "Limpopo", parentCode: "XAI")

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

        def rv1 = new RawVisit(id: "uuuid1", code: codeGeneratorService.generateVisitCode(household1), householdCode: household1.code, visitReason: VisitReason.BASELINE,
                visitDate: GeneralUtil.getDate(2021, 2, 3), visitLocation: VisitLocationItem.HOME.code, roundNumber: 0,
                respondentCode: member11.code, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2021, 3, 4, 0, 0, 0)
        )

        def rv2 = new RawVisit(id: "uuuid2", code: codeGeneratorService.generateVisitCode(household1), householdCode: household1.code, visitReason: VisitReason.BASELINE,
                visitDate: GeneralUtil.getDate(2021, 2, 4), visitLocation: VisitLocationItem.HOME.code, roundNumber: 0,
                respondentCode: member12.code, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2021, 3, 4, 0, 0, 0)
        )

        def rv3 = new RawVisit(id: "uuuid3", code: codeGeneratorService.generateVisitCode(household2), householdCode: household2.code, visitReason: VisitReason.BASELINE,
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

    def setupInMigrations(){
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
                originOther: "BEIRA",
                destinationCode: household1.code,
                migrationDate: GeneralUtil.getDate(2020, 1, 19),
                migrationReason: "FROM MINES",
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
                visitCode: visitHousehold2v1,
                memberCode: member21.code,
                migrationType: InMigrationType.EXTERNAL.code,
                originCode: null,
                originOther: "SA - capetown",
                destinationCode: household2.code,
                migrationDate: GeneralUtil.getDate(2020, 2, 19),
                migrationReason: "job changes",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )

        def rin4 = new RawInMigration(
                id: "uuuid4",
                visitCode: visitHousehold2v1,
                memberCode: member22.code,
                migrationType: InMigrationType.EXTERNAL.code,
                originCode: null,
                originOther: "Gaza",
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




        def result1 = inMigrationService.createInMigration(rin1)
        def result2 = inMigrationService.createInMigration(rin2)
        def result3 = inMigrationService.createInMigration(rin3)
        def result4 = inMigrationService.createInMigration(rin4)

    }

    def setupHeadRelationships(){
        //create new headRelationship
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

        //create new head
        def rw1 = new RawHeadRelationship(
                id: "uuuid1",
                memberCode: member11.code,
                householdCode: household1.code,
                relationshipType: "HOH",
                startType: "ENU",
                startDate: GeneralUtil.getDate(2020,5,17),
                endType: "",
                endDate: ""
        )

        def rw2 = new RawHeadRelationship(
                id: "uuuid2",
                memberCode: member12.code,
                householdCode: household1.code,
                relationshipType: "SPO",
                startType: "ENU",
                startDate: GeneralUtil.getDate(2020,05,04),
                endType: "",
                endDate: ""
        )

        def rw3 = new RawHeadRelationship(
                id: "uuuid3",
                memberCode: member21.code,
                householdCode: household2.code,
                relationshipType: "HOH",
                startType: "ENU",
                startDate: GeneralUtil.getDate(2020,04,17),
                endType: "",
                endDate: ""
        )

        def rw4 = new RawHeadRelationship(
                id: "uuuid4",
                memberCode: member22.code,
                householdCode: household2.code,
                relationshipType: "SPO",
                startType: "ENU",
                startDate: GeneralUtil.getDate(2020,05,04),
                endType: "",
                endDate: ""
        )

        rw1.save()
        rw2.save()
        rw3.save()
        rw4.save()

        //println "Raw Member Errors:"
        //printRawMessages(errorMessageService.getRawMessages(rw1))
        //println()
        //printRawMessages(errorMessageService.getRawMessages(rw2))
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result1 = headRelationshipService.createHeadRelationship(rw1)
        def result2 = headRelationshipService.createHeadRelationship(rw2)
        def result3 = headRelationshipService.createHeadRelationship(rw3)
        def result4 = headRelationshipService.createHeadRelationship(rw4)
    }

    def setupOutmigrations(){
        //create new residency
        def household1 = Household.findByName("Macandza House")
        def household2 = Household.findByName("George Benson")
        def member11 = Member.findByName("John Benedit Macandza")
        def member12 = Member.findByName("Catarina Loyd Macandza")
        def member21 = Member.findByName("George Benson")
        def member22 = Member.findByName("Joyce Mary Benson")

        def visitHousehold1v1 = Visit.findAllByHousehold(household1).first().code
        def visitHousehold2v1 = Visit.findByHousehold(household2)?.code

        //println "household1: ${household1}, check: ${Household.count()}"
        //println "member1: ${member11}, check: ${Member.count()}"
        //println "household2: ${household2}, check: ${Household.count()}"
        //println "member2: ${member21}, check: ${Member.count()}"

        //println "rounds: ${Round.count()}"

        def rout1 = new RawOutMigration(
                id: "uuuid1",
                visitCode: visitHousehold2v1,
                memberCode: member22.code,
                migrationType: OutMigrationType.EXTERNAL.code,
                originCode: household2.code,
                destinationOther: "USA-MICHIGAN",
                migrationDate: GeneralUtil.getDate(2020, 5, 19),
                migrationReason: "GOING TO USA WORK",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 6, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 6, 14, 0, 0, 0)
        )
/*
        def rout2 = new RawOutMigration(
                id: "uuuid2",
                visitCode: visitHousehold1v1,
                memberCode: member12.code,
                migrationType: OutMigrationType.INTERNAL.code,
                originCode: household2.code,
                destinationCode: household1.code,
                migrationDate: GeneralUtil.getDate(2020, 3, 19),
                migrationReason: "MARRIED",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )*/


        //rv1.save()
        //rv2.save()
        //rv3.save()

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints




        def result1 = outMigrationService.createOutMigration(rout1)
        //def result2 = outMigrationService.createOutMigration(rout2)
        //def result4 = outMigrationService.createOutMigration(rout4)

        //printResults(result1)

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

    def printEnu(Member member){
        if (member==null) return println("EMPTY")
        println "member(id=${member.id},code=${member.code},destination=${member.householdCode},visit.code=${'not used'},date=${StringUtil.format(member.startDate)})"
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

    void "Test Enumerations"() {
        println "\n#### Enumerations ####"

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

        def rmenu1 = new RawMemberEnu(
                id: "uuuid1",
                /*visitCode: visitHousehold2v1,*/
                code: codeGeneratorService.generateMemberCode(household2),
                name: "Peter Benson" ,
                gender: "M",
                dob: GeneralUtil.getDate(2010, 1, 19),
                motherCode: member22.code,
                fatherCode: Codes.MEMBER_UNKNOWN_CODE,
                householdCode: household2.code,
                householdName: "",
                headRelationshipType: "SON",
                residencyStartDate: GeneralUtil.getDate(2020, 1, 19),

                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )


        println "id1-${rmenu1.id},id2=${rmenu1}, ${rmenu1.hasErrors()}"

        rmenu1.id = "uuid1"
        rmenu1.save()


        println("first enu")
        def result1 = memberEnumerationService.createMemberEnumeration(rmenu1)
        printResults(result1)
        printEnu(result1?.domainInstance?.member)
        println()

        /*
        println("second enu")
        def result2 = memberEnumerationService.createMemberEnumeration(rmenu1)
        printResults(result2)
        printEnu(result2?.domainInstance)
        println()*/

        expect:
        Member.count()==6
    }
}
