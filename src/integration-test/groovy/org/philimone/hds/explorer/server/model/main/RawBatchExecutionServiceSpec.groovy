package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.collect.raw.aggregate.RawEvent
import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem
import org.philimone.hds.explorer.server.model.enums.VisitReason
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.OutMigrationType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.ApplicationParamService
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.model.settings.generator.CodeGeneratorService
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.LocalDateTime

@Integration
@Transactional //@Rollback
class RawBatchExecutionServiceSpec extends Specification {

    @Autowired
    ErrorMessageService errorMessageService    
    @Autowired
    UserService userService
    @Autowired
    CodeGeneratorService codeGeneratorService
    @Autowired
    RoundService roundService
    @Autowired
    RawBatchExecutionService rawBatchExecutionService
    @Autowired
    ApplicationParamService applicationParamService

    def setupAll() {
        setupAppParams()
        setupUsers()
        setupRounds()
        setupRegions()
        setupHouseholds()
        setupVisits()
        setupMembers()

        setupOutmigrations()
        setupExternalInMigrations()
        setupChangeHead()
    }

    def setupUsers(){
        def admin = Role.findByName(Role.LABEL_ADMINISTRATOR)
        def user = new User(code: "DF1", firstName: "Dragon", lastName: "Fire", username: "dragon", password: "fire")

        userService.addUser(user, [admin])
    }

    def setupAppParams(){
        applicationParamService.updateApplicationParam("hierarchy1", "Province")
        applicationParamService.updateApplicationParam("hierarchy2", "District")
        applicationParamService.updateApplicationParam("hierarchy3", "Neighborhood")
    }

    def setupRegions(){
        def user = User.findByUsername("dragon")

        def rg1 = new RawRegion(id: "u100", regionCode: codeGeneratorService.generateRegionCode(null, "Maputo"), regionName: "Maputo", parentCode: "", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rg11 = new RawRegion(id: "u101",regionCode: codeGeneratorService.generateRegionCode(null, "Matola"), regionName: "Matola", parentCode: "MAP", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rg111 = new RawRegion(id: "u102",regionCode: codeGeneratorService.generateRegionCode(null, "Txumene"), regionName: "Txumene", parentCode: "MAT", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rg112 = new RawRegion(id: "u103",regionCode: codeGeneratorService.generateRegionCode(null, "Fomento"), regionName: "Fomento", parentCode: "MAT", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rg2 = new RawRegion(id: "u104",regionCode: codeGeneratorService.generateRegionCode(null, "Gaza"), regionName: "Gaza", parentCode: "", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rg21 = new RawRegion(id: "u105",regionCode: codeGeneratorService.generateRegionCode(null, "Xai-Xai"), regionName: "Xai-Xai", parentCode: "GAZ", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rg211 = new RawRegion(id: "u106",regionCode: codeGeneratorService.generateRegionCode(null, "Zongoene"), regionName: "Zongoene", parentCode: "XAI", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rg212 = new RawRegion(id: "u107",regionCode: codeGeneratorService.generateRegionCode(null, "Limpopo"), regionName: "Limpopo", parentCode: "XAI", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        rg1.id = "u101"
        rg11.id = "u102"
        rg111.id = "u103"
        rg112.id = "u104"
        rg2.id = "u106"
        rg21.id = "u107"
        rg211.id = "u108"
        rg212.id = "u109"
        
        rg1.save(flush: true)
        rg11.save(flush: true)
        rg111.save(flush: true)
        rg112.save(flush: true)
        rg2.save(flush: true)
        rg21.save(flush: true)
        rg211.save(flush: true)
        rg212.save(flush: true)
    }

    def setupRounds(){
        def result1 = roundService.createRound(GeneralUtil.getDate(2010, 1, 1), GeneralUtil.getDate(2021, 12, 31), "baseline round")
        //def result2 = roundService.createRound(GeneralUtil.getDate(2021, 1, 31), GeneralUtil.getDate(2021, 1, 28), "first round")
        //def result3 = roundService.createRound(GeneralUtil.getDate(2021, 2, 1), GeneralUtil.getDate(2021, 1, 20), "second round")
    }

    def setupHouseholds(){

        def user = User.findByUsername("dragon")
        def region = new Region(code: "TXU")

        def rw1 = new RawHousehold(id: "u1", regionCode: region.code, householdCode: "TXUDF1001", householdName: "Macandza House", headCode: "", headName: "", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())
        def rw2 = new RawHousehold(id: "u2", regionCode: region.code, householdCode: "TXUDF1002", householdName: "George Benson", headCode: "", headName: "", collectedBy: user.username, collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())

        rw1.id = "u1"
        rw2.id = "u2"

        rw1.save()
        rw2.save()


        println "${rw1.errors}"
        println "${rw2.errors}"
    }

    def setupMembers(){

        //Insert Member through Enumeration + External InMigrations + Pregnancy Outcomes

        def household1 = "TXUDF1001" //RawHousehold.findByHouseholdName("Macandza House")
        def household2 = "TXUDF1002" //RawHousehold.findByHouseholdName("George Benson")
        def visit1 = "${household1}-000-001"
        def visit2 = "${household2}-000-001" //xen
        def visit3 = "${household2}-000-002" //bir
        def visit4 = "${household2}-000-003" //ext
        def visit5 = "${household2}-000-004" //xen


        //ENU + XEN + ENU + XEN + BIR

        def rawMemberEnu1 = new RawMemberEnu(
                id: "uuuid1",
                /*visitCode: visitHousehold2v1,*/
                code: "TXUDF1001001", //codeGeneratorService.generateMemberCode(new Household(code: household1)),
                name: "John Benedit Macandza" ,
                gender: Gender.MALE.code,
                dob: GeneralUtil.getDate(1978, 10, 19),
                motherCode: Codes.MEMBER_UNKNOWN_CODE,
                fatherCode: Codes.MEMBER_UNKNOWN_CODE,
                householdCode: household1,
                householdName: "",
                headRelationshipType: HeadRelationshipType.HEAD_OF_HOUSEHOLD.code,
                residencyStartDate: GeneralUtil.getDate(2010, 2, 19),
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2010, 2, 19, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2010, 2, 28, 0, 0, 0)
        )

        def rawMemberEnu2 = new RawMemberEnu(
                id: "uuuid2",
                /*visitCode: visitHousehold2v1,*/
                code: "TXUDF1002001", //codeGeneratorService.generateMemberCode(new Household(code: household2)),
                name: "George Benson" ,
                gender: Gender.MALE.code,
                dob: GeneralUtil.getDate(1982, 5, 17),
                motherCode: Codes.MEMBER_UNKNOWN_CODE,
                fatherCode: Codes.MEMBER_UNKNOWN_CODE,
                householdCode: household2,
                householdName: "",
                headRelationshipType: HeadRelationshipType.HEAD_OF_HOUSEHOLD.code,
                residencyStartDate: GeneralUtil.getDate(2010, 2, 19),
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2010, 2, 19, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2010, 2, 28, 0, 0, 0)
        )

        def rawMemberXen1 = new RawExternalInMigration(
                id: "uuuid1",
                visitCode: visit1,
                memberCode: "TXUDF1001002",
                memberName: "Catarina Loyd Macandza" ,
                memberGender: Gender.FEMALE.code,
                memberDob: GeneralUtil.getDate(1985, 1, 4),
                memberMotherCode: Codes.MEMBER_UNKNOWN_CODE,
                memberFatherCode: Codes.MEMBER_UNKNOWN_CODE,
                headRelationshipType: HeadRelationshipType.SPOUSE.code,
                migrationType: InMigrationType.EXTERNAL.code,
                extMigrationType: ExternalInMigrationType.ENTRY.name(),
                originCode: null,
                originOther: "ITALY",
                destinationCode: household1,
                migrationDate: GeneralUtil.getDate(2012, 10, 11),
                migrationReason: "MARRIAGE",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2012, 10, 30, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2012, 11, 05, 0, 0, 0)
        )

        def rawMemberXen2 = new RawExternalInMigration(
                id: "uuuid2",
                visitCode: visit2,
                memberCode: "TXUDF1002002",
                memberName: "Joyce Mary Benson" ,
                memberGender: Gender.FEMALE.code,
                memberDob: GeneralUtil.getDate(1989, 9, 14),
                memberMotherCode: Codes.MEMBER_UNKNOWN_CODE,
                memberFatherCode: Codes.MEMBER_UNKNOWN_CODE,
                headRelationshipType: HeadRelationshipType.SPOUSE.code,
                migrationType: InMigrationType.EXTERNAL.code,
                extMigrationType: ExternalInMigrationType.ENTRY.name(),
                originCode: null,
                originOther: "NAMPULA",
                destinationCode: household2,
                migrationDate: GeneralUtil.getDate(2012, 9, 10),
                migrationReason: "MARRIAGE",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2012, 9, 19, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2012, 9, 19, 0, 0, 0)
        )



        def rawPregReg1 = new RawPregnancyRegistration(
                id: "uuuid2",
                code: codeGeneratorService.generatePregnancyCode(new Member(code: rawMemberXen2.memberCode)),
                motherCode: rawMemberXen2.memberCode,
                recordedDate: GeneralUtil.getDate(2012, 9, 19),
                pregMonths: 6,
                eddKnown: false,
                hasPrenatalRecord: false,
                eddDate: null,
                eddType: null,
                lmpKnown: false,
                lmpDate: null,
                expectedDeliveryDate: GeneralUtil.getDate(2012, 12, 19),
                status: PregnancyStatus.PREGNANT.code,
                visitCode: visit3,
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2012, 12, 26, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2012, 12, 28, 0, 0, 0)
        )

        def rawPregOut1 = new RawPregnancyOutcome (
                id: "uuuid3",
                code: rawPregReg1.code,
                motherCode: rawMemberXen2.memberCode,
                fatherCode: rawMemberEnu2.code,
                numberOfOutcomes: 1,
                outcomeDate: GeneralUtil.getDate(2006, 12, 25),
                birthPlace: BirthPlace.HEALTH_CENTER_CLINIC.code,
                birthPlaceOther: null,
                visitCode: visit3,
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2012, 12, 26, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2012, 12, 28, 0, 0, 0)
        )

        def rawPregChild = new RawPregnancyChild(
                id: "uuuid4",
                /*outcome: rpo1,*/
                outcomeType: PregnancyOutcomeType.LIVEBIRTH.code,
                childCode: "TXUDF1002003",
                childName: "Peter Benson",
                childGender: Gender.MALE.code,
                childOrdinalPosition: 1,
                headRelationshipType: HeadRelationshipType.SON_DAUGHTER.code
        )



        rawMemberEnu1.id = GeneralUtil.generateUUID()
        rawMemberEnu2.id = GeneralUtil.generateUUID()
        rawMemberXen1.id = GeneralUtil.generateUUID()
        rawMemberXen2.id = GeneralUtil.generateUUID()
        rawPregReg1.id = GeneralUtil.generateUUID()
        rawPregOut1.id = GeneralUtil.generateUUID()
        rawPregChild.id = GeneralUtil.generateUUID()
        rawPregOut1.id = GeneralUtil.generateUUID()
        
        rawMemberEnu1.save(flush: true)
        rawMemberEnu2.save(flush: true)
        rawMemberXen1.save(flush: true)
        rawMemberXen2.save(flush: true)
        rawPregReg1.save(flush: true)
        rawPregOut1.save(flush: true)
        rawPregOut1.addToChilds(rawPregChild)
        rawPregOut1.save(flush: true)
    }

    def setupVisits(){
        //create new residency
        def household1 = "TXUDF1001" //RawHousehold.findByHouseholdName("Macandza House")
        def household2 = "TXUDF1002" //RawHousehold.findByHouseholdName("George Benson")
        def member11 = "TXUDF1001001" //Member.findByName("John Benedit Macandza")
        def member12 = "TXUDF1001002" //Member.findByName("Catarina Loyd Macandza")
        def member21 = "TXUDF1002001" //Member.findByName("George Benson")
        def member22 = "TXUDF1002002" //Member.findByName("Joyce Mary Benson")
        def member23 = "TXUDF1002003"
        def visit1 = "${household1}-000-001"
        def visit2 = "${household2}-000-001" //xen
        def visit3 = "${household2}-000-002" //bir
        def visit4 = "${household2}-000-003" //ext
        def visit5 = "${household2}-000-004" //xen

        def rv1 = new RawVisit(id: "uuuid1", code: visit1, householdCode: household1, visitReason: VisitReason.BASELINE,
                visitDate: GeneralUtil.getDate(2012, 10, 11), visitLocation: VisitLocationItem.HOME.code, roundNumber: 0,
                respondentCode: member11, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2012, 10, 15, 0, 0, 0)
        )

        def rv2 = new RawVisit(id: "uuuid2", code: visit2, householdCode: household2, visitReason: VisitReason.BASELINE,
                visitDate: GeneralUtil.getDate(2012, 9, 19), visitLocation: VisitLocationItem.HOME.code, roundNumber: 0,
                respondentCode: member12, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2012, 9, 22, 0, 0, 0)
        )

        def rv3 = new RawVisit(id: "uuuid3", code: visit3, householdCode: household2, visitReason: VisitReason.BASELINE,
                visitDate: GeneralUtil.getDate(2012, 12, 25), visitLocation: VisitLocationItem.OTHER_PLACE.code, visitLocationOther: "HEALTHCENTER", roundNumber: 0,
                respondentCode: member21, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2012, 12, 26, 0, 0, 0)
        )

        def rv4 = new RawVisit(id: "uuuid3", code: visit4, householdCode: household2, visitReason: VisitReason.BASELINE,
                visitDate: GeneralUtil.getDate(2015, 2, 4), visitLocation: VisitLocationItem.OTHER_PLACE.code, visitLocationOther: "HEALTHCENTER", roundNumber: 0,
                respondentCode: member21, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2015, 2, 4, 0, 0, 0)
        )

        def rv5 = new RawVisit(id: "uuuid3", code: visit5, householdCode: household2, visitReason: VisitReason.BASELINE,
                visitDate: GeneralUtil.getDate(2019, 2, 4), visitLocation: VisitLocationItem.OTHER_PLACE.code, visitLocationOther: "HEALTHCENTER", roundNumber: 0,
                respondentCode: member21, hasInterpreter: false, collectedBy: "dragon", collectedDate: GeneralUtil.getDate(2019, 2, 4, 0, 0, 0)
        )

        rv1.id = GeneralUtil.generateUUID()
        rv2.id = GeneralUtil.generateUUID()
        rv3.id = GeneralUtil.generateUUID()
        rv4.id = GeneralUtil.generateUUID()
        rv5.id = GeneralUtil.generateUUID()

        rv1.save()
        rv2.save()
        rv3.save()
        rv4.save()
        rv5.save()
    }

    def setupOutmigrations(){
        //create new residency
        def household1 = "TXUDF1001" //RawHousehold.findByHouseholdName("Macandza House")
        def household2 = "TXUDF1002" //RawHousehold.findByHouseholdName("George Benson")
        def member11 = "TXUDF1001001" //Member.findByName("John Benedit Macandza")
        def member12 = "TXUDF1001002" //Member.findByName("Catarina Loyd Macandza")
        def member21 = "TXUDF1002001" //Member.findByName("George Benson")
        def member22 = "TXUDF1002002" //Member.findByName("Joyce Mary Benson")
        def member23 = "TXUDF1002003"
        def visit1 = "${household1}-000-001"
        def visit2 = "${household2}-000-001" //xen
        def visit3 = "${household2}-000-002" //bir
        def visit4 = "${household2}-000-003" //ext
        def visit5 = "${household2}-000-004" //xen


        def rawOut1 = new RawOutMigration(
                id: "uuuid1",
                visitCode: visit4,
                memberCode: member23,
                migrationType: OutMigrationType.EXTERNAL.code,
                originCode: household2,
                destinationOther: "USA-MICHIGAN",
                migrationDate: GeneralUtil.getDate(2015, 1, 23),
                migrationReason: "GOING TO STUDY",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2015, 2, 4, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2015, 2, 14, 0, 0, 0)
        )

        rawOut1.id = GeneralUtil.generateUUID()
        rawOut1.save(flush:true)

    }

    def setupExternalInMigrations() {
        //create new residency
        def household1 = "TXUDF1001" //RawHousehold.findByHouseholdName("Macandza House")
        def household2 = "TXUDF1002" //RawHousehold.findByHouseholdName("George Benson")
        def member11 = "TXUDF1001001" //Member.findByName("John Benedit Macandza")
        def member12 = "TXUDF1001002" //Member.findByName("Catarina Loyd Macandza")
        def member21 = "TXUDF1002001" //Member.findByName("George Benson")
        def member22 = "TXUDF1002002" //Member.findByName("Joyce Mary Benson")
        def member23 = "TXUDF1002003"
        def visit1 = "${household1}-000-001"
        def visit2 = "${household2}-000-001" //xen
        def visit3 = "${household2}-000-002" //bir
        def visit4 = "${household2}-000-003" //ext
        def visit5 = "${household2}-000-004" //xen

        def rawXen1 = new RawExternalInMigration( //returning
                id: "uuuid3",
                visitCode: visit5,
                memberCode: member23,
                headRelationshipType: HeadRelationshipType.SON_DAUGHTER.code,
                migrationType: InMigrationType.EXTERNAL.code,
                extMigrationType: ExternalInMigrationType.REENTRY.name(),
                originCode: null,
                originOther: "USA",
                destinationCode: household2,
                migrationDate: GeneralUtil.getDate(2019, 1, 19),
                migrationReason: "family",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2019, 2, 4, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2019, 2, 4, 0, 0, 0)
        )

        rawXen1.id = GeneralUtil.generateUUID()
        rawXen1.save(flush: true)
    }

    def setupChangeHead() {

        def household1 = "TXUDF1001" //RawHousehold.findByHouseholdName("Macandza House")
        def household2 = "TXUDF1002" //RawHousehold.findByHouseholdName("George Benson")
        def member11 = "TXUDF1001001" //Member.findByName("John Benedit Macandza")
        def member12 = "TXUDF1001002" //Member.findByName("Catarina Loyd Macandza")
        def member21 = "TXUDF1002001" //Member.findByName("George Benson")
        def member22 = "TXUDF1002002" //Member.findByName("Joyce Mary Benson")
        def member23 = "TXUDF1002003"
        def visit1 = "${household1}-000-001"
        def visit2 = "${household2}-000-001" //xen
        def visit3 = "${household2}-000-002" //bir
        def visit4 = "${household2}-000-003" //ext
        def visit5 = "${household2}-000-004" //xen

        def rawChgHead1 = new RawChangeHead( //returning
                id: "uuuid1",
                visitCode: visit5,
                householdCode: household2,
                oldHeadCode: member21,
                newHeadCode: member23,
                eventDate: GeneralUtil.getDate(2019, 01, 23),
                reason: "family",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2019, 02, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2019, 02, 14, 0, 0, 0)
        )

        rawChgHead1.id = GeneralUtil.generateUUID()
        rawChgHead1.save(flush: true)
        rawChgHead1.addToRelationships(new RawChangeHeadRelationship(changeHead: rawChgHead1, newMemberCode: member21, newRelationshipType: HeadRelationshipType.PARENT.code))
        rawChgHead1.addToRelationships(new RawChangeHeadRelationship(changeHead: rawChgHead1, newMemberCode: member22, newRelationshipType: HeadRelationshipType.PARENT.code))
        rawChgHead1.save(flush: true)
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

    def printInMig(InMigration migration){
        if (migration==null) return println("EMPTY")
        println "migration(id=${migration.id},code=${migration.memberCode},destination=${migration.destinationCode},visit.code=${migration.visitCode},date=${migration.migrationDate})"
    }

    def printHeadR(HeadRelationship headRelationship){
        if (headRelationship==null) return null
        println "headRelationship(id=${headRelationship.id},m.code=${headRelationship.memberCode},h.code=${headRelationship.householdCode},starttype=${headRelationship.startType},startdate=${StringUtil.formatLocalDate(headRelationship?.startDate)},endtype=${headRelationship.endType},enddate=${StringUtil.formatLocalDate(headRelationship?.endDate)})"
    }

    def print(Residency residency){
        if (residency==null) return null
        println "residency(id=${residency.id},m.code=${residency.memberCode},h.code=${residency.householdCode},starttype=${residency.startType},startdate=${StringUtil.formatLocalDate(residency?.startDate)},endtype=${residency.endType},enddate=${StringUtil.formatLocalDate(residency?.endDate)})"
    }

    def print(HeadRelationship headRelationship){
        if (headRelationship==null) return null
        println "headRelationship(id=${headRelationship.id},m.code=${headRelationship.memberCode},h.code=${headRelationship.householdCode},starttype=${headRelationship.startType},startdate=${StringUtil.formatLocalDate(headRelationship?.startDate)},endtype=${headRelationship.endType},enddate=${StringUtil.formatLocalDate(headRelationship?.endDate)})"
    }

    def print(Visit visit){
        if (visit==null) return null
        println "visit(id=${visit.id},v.code=${visit.code},v.household=${visit.householdCode},v.round=${visit.roundNumber},v.visitDate=${StringUtil.formatLocalDate(visit?.visitDate)},v.location=${visit.visitLocation},v.respondent=${visit?.respondentCode})"
    }

    def print(Round round){
        if (round==null) return null
        println "round(id=${round.id},r.number=${round.roundNumber},r.startdate=${round.startDate},r.enddate=${round.endDate},r.description=${round?.description})"
    }

    void "Testing Raw Batch Insertion"() {
        println "\n#### Saving Data ####"

        setupAll()

        rawBatchExecutionService.compileAndExecuteEvents()

        expect:
        RawEvent.count()==10
    }
}
