package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import net.betainteractive.utilities.StringUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.settings.ApplicationParam
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
@Transactional
class FakeDataGeneratorServiceSpec extends Specification {

    @Autowired
    CodeGeneratorService codeGeneratorService

    def fakeDataExecuted = 0
    def fakeDataUsers = new LinkedHashMap<String, FakeDataUser>()
    def fakeDataRegions = new LinkedHashMap<String, FakeDataRegion>()
    def surnamesMap = new LinkedHashMap<String, String>()
    def malenamesMap = new LinkedHashMap<String, String>()
    def femalenamesMap = new LinkedHashMap<String, String>()
    def malenamesList = new ArrayList<String>()
    def femalenamesList = new ArrayList<String>()
    def surnamesList = new ArrayList<String>()


    void setupAll(){

    }

    void "Upgrade Database to Fake Data"() {
        println "\n#### Creating Fake Datasets ####"


        //1. Change Hierarchy Levels
        //2. Change Users Names and Update Code Recursivelly
        //3. Change Region Names and Update Code Recursevelly
        //4. Change Household Names
        //5. Change Member Names

        setupAll()

        //createNewRegionCodes()
        //createNewUserCodes()

        fakeDataRegions = getRegionsXLS()
        fakeDataUsers = getUsersXLS()
        readFemaleNamesXLS()
        readMaleNamesXLS()

        //changeHierarchyLevels()
        //changeGpsCoords()
        //changeUsers()
        //changeRegionNames()
        //changeHouseholdMemberNames()
        //changeDatasetsModules()
        changeForms()


        fakeDataExecuted=1

        expect:
        fakeDataExecuted == 1
    }

    def changeHierarchyLevels() {
        ApplicationParam.executeUpdate("update ApplicationParam p set p.value='Country'  where p.name='hierarchy1'")
        ApplicationParam.executeUpdate("update ApplicationParam p set p.value='Province' where p.name='hierarchy2'")
        ApplicationParam.executeUpdate("update ApplicationParam p set p.value='District' where p.name='hierarchy3'")
        ApplicationParam.executeUpdate("update ApplicationParam p set p.value='Locality' where p.name='hierarchy4'")
        ApplicationParam.executeUpdate("update ApplicationParam p set p.value='Village'  where p.name='hierarchy5'")

    }

    def changeGpsCoords() {
        println "updating gps coords"

        def lat = -3.378008448349019
        def lon = -1.58715600273029

        Household.list().each {
            if (it.gpsLatitude != null && it.gpsLongitude !=null) {
                it.gpsLatitude = it.gpsLatitude + lat  //Move to Manica
                it.gpsLongitude = it.gpsLongitude + lon //Move

                it.sinLatitude = Math.sin(it.gpsLatitude)
                it.sinLongitude = Math.sin(it.gpsLongitude)
                it.cosLatitude = Math.cos(it.gpsLatitude)
                it.cosLongitude = Math.cos(it.gpsLongitude)
                it.save()
            }
        }

        Member.list().each {
            if (it.gpsLatitude != null && it.gpsLongitude !=null) {
                it.gpsLatitude = it.gpsLatitude + lat  //Move to Manica
                it.gpsLongitude = it.gpsLongitude + lon //Move

                it.sinLatitude = Math.sin(it.gpsLatitude)
                it.sinLongitude = Math.sin(it.gpsLongitude)
                it.cosLatitude = Math.cos(it.gpsLatitude)
                it.cosLongitude = Math.cos(it.gpsLongitude)
                it.save()
            }
        }

        println "finished gps coords"

        fakeDataExecuted=1
    }

    def changeUsers() {

        /*
          1. change user code, username, firstname, lastname
          2. update dependent codes (household_code, member_code, visit_code)
        */

        fakeDataUsers.each { oldcode, f ->
            //change user code BALPF1001(001/-001-001)
            User.executeUpdate("update User u set u.code=?, u.username=?, u.firstName=?, u.lastName=? where u.id=?", [f.new_code, f.new_username, f.new_firstname, f.new_lastname, f.id])

            //Update all dependencies that use ${oldcode}

            Household.executeUpdate("update Household set code=concat(substring(code,1,3),:ncode,substring(code,7,length(code)-6)) where substring(code,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Household.executeUpdate("update Household set headCode=concat(substring(headCode,1,3),:ncode,substring(headCode,7,length(headCode)-6)) where substring(headCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            //Household.executeUpdate("update Household set region=:ncode where region=:ocode", [ncode:f.new_code, ocode:oldcode])

            Enumeration.executeUpdate("update Enumeration set householdCode=concat(substring(householdCode,1,3),:ncode,substring(householdCode,7,length(householdCode)-6)) where substring(householdCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            HeadRelationship.executeUpdate("update HeadRelationship set householdCode=concat(substring(householdCode,1,3),:ncode,substring(householdCode,7,length(householdCode)-6)) where substring(householdCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set destinationCode=concat(substring(destinationCode,1,3),:ncode,substring(destinationCode,7,length(destinationCode)-6)) where substring(destinationCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set originCode=concat(substring(originCode,1,3),:ncode,substring(originCode,7,length(originCode)-6)) where substring(originCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set householdCode=concat(substring(householdCode,1,3),:ncode,substring(householdCode,7,length(householdCode)-6)) where substring(householdCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set destinationCode=concat(substring(destinationCode,1,3),:ncode,substring(destinationCode,7,length(destinationCode)-6)) where substring(destinationCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set originCode=concat(substring(originCode,1,3),:ncode,substring(originCode,7,length(originCode)-6)) where substring(originCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Residency.executeUpdate("update Residency set householdCode=concat(substring(householdCode,1,3),:ncode,substring(householdCode,7,length(householdCode)-6)) where substring(householdCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Visit.executeUpdate("update Visit set householdCode=concat(substring(householdCode,1,3),:ncode,substring(householdCode,7,length(householdCode)-6)) where substring(householdCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set householdCode=concat(substring(householdCode,1,3),:ncode,substring(householdCode,7,length(householdCode)-6)) where substring(householdCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])

            Member.executeUpdate("update Member set motherCode=concat(substring(motherCode,1,3),:ncode,substring(motherCode,7,length(motherCode)-6)) where substring(motherCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set spouseCode=concat(substring(spouseCode,1,3),:ncode,substring(spouseCode,7,length(spouseCode)-6)) where substring(spouseCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set fatherCode=concat(substring(fatherCode,1,3),:ncode,substring(fatherCode,7,length(fatherCode)-6)) where substring(fatherCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Death.executeUpdate("update Death set memberCode=concat(substring(memberCode,1,3),:ncode,substring(memberCode,7,length(memberCode)-6)) where substring(memberCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Enumeration.executeUpdate("update Enumeration set memberCode=concat(substring(memberCode,1,3),:ncode,substring(memberCode,7,length(memberCode)-6)) where substring(memberCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            HeadRelationship.executeUpdate("update HeadRelationship set memberCode=concat(substring(memberCode,1,3),:ncode,substring(memberCode,7,length(memberCode)-6)) where substring(memberCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            HeadRelationship.executeUpdate("update HeadRelationship set headCode=concat(substring(headCode,1,3),:ncode,substring(headCode,7,length(headCode)-6)) where substring(headCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Household.executeUpdate("update Household set headCode=concat(substring(headCode,1,3),:ncode,substring(headCode,7,length(headCode)-6)) where substring(headCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set memberCode=concat(substring(memberCode,1,3),:ncode,substring(memberCode,7,length(memberCode)-6)) where substring(memberCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            IncompleteVisit.executeUpdate("update IncompleteVisit set memberCode=concat(substring(memberCode,1,3),:ncode,substring(memberCode,7,length(memberCode)-6)) where substring(memberCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            MaritalRelationship.executeUpdate("update MaritalRelationship set memberA_code=concat(substring(memberA_code,1,3),:ncode,substring(memberA_code,7,length(memberA_code)-6)) where substring(memberA_code,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            MaritalRelationship.executeUpdate("update MaritalRelationship set memberB_code=concat(substring(memberB_code,1,3),:ncode,substring(memberB_code,7,length(memberB_code)-6)) where substring(memberB_code,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set motherCode=concat(substring(motherCode,1,3),:ncode,substring(motherCode,7,length(motherCode)-6)) where substring(motherCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set spouseCode=concat(substring(spouseCode,1,3),:ncode,substring(spouseCode,7,length(spouseCode)-6)) where substring(spouseCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set fatherCode=concat(substring(fatherCode,1,3),:ncode,substring(fatherCode,7,length(fatherCode)-6)) where substring(fatherCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set memberCode=concat(substring(memberCode,1,3),:ncode,substring(memberCode,7,length(memberCode)-6)) where substring(memberCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyRegistration.executeUpdate("update PregnancyRegistration set motherCode=concat(substring(motherCode,1,3),:ncode,substring(motherCode,7,length(motherCode)-6)) where substring(motherCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyChild.executeUpdate("update PregnancyChild set childCode=concat(substring(childCode,1,3),:ncode,substring(childCode,7,length(childCode)-6)) where substring(childCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyOutcome.executeUpdate("update PregnancyOutcome set fatherCode=concat(substring(fatherCode,1,3),:ncode,substring(fatherCode,7,length(fatherCode)-6)) where substring(fatherCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyOutcome.executeUpdate("update PregnancyOutcome set motherCode=concat(substring(motherCode,1,3),:ncode,substring(motherCode,7,length(motherCode)-6)) where substring(motherCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Residency.executeUpdate("update Residency set memberCode=concat(substring(memberCode,1,3),:ncode,substring(memberCode,7,length(memberCode)-6)) where substring(memberCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Visit.executeUpdate("update Visit set respondentCode=concat(substring(respondentCode,1,3),:ncode,substring(respondentCode,7,length(respondentCode)-6)) where substring(respondentCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])

            Death.executeUpdate("update Death set visitCode=concat(substring(visitCode,1,3),:ncode,substring(visitCode,7,length(visitCode)-6)) where substring(visitCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Enumeration.executeUpdate("update Enumeration set visitCode=concat(substring(visitCode,1,3),:ncode,substring(visitCode,7,length(visitCode)-6)) where substring(visitCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set visitCode=concat(substring(visitCode,1,3),:ncode,substring(visitCode,7,length(visitCode)-6)) where substring(visitCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            IncompleteVisit.executeUpdate("update IncompleteVisit set visitCode=concat(substring(visitCode,1,3),:ncode,substring(visitCode,7,length(visitCode)-6)) where substring(visitCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set visitCode=concat(substring(visitCode,1,3),:ncode,substring(visitCode,7,length(visitCode)-6)) where substring(visitCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyRegistration.executeUpdate("update PregnancyRegistration set visitCode=concat(substring(visitCode,1,3),:ncode,substring(visitCode,7,length(visitCode)-6)) where substring(visitCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyOutcome.executeUpdate("update PregnancyOutcome set visitCode=concat(substring(visitCode,1,3),:ncode,substring(visitCode,7,length(visitCode)-6)) where substring(visitCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])

            TrackingListMapping.executeUpdate("update TrackingListMapping set subjectCode=concat(substring(subjectCode,1,3),:ncode,substring(subjectCode,7,length(subjectCode)-6)) where substring(subjectCode,4,3)=:ocode", [ncode:f.new_code, ocode:oldcode])


        }
    }

    def changeRegionNames(){

        //update region names and codes according to excel file
        //update region parent codes using their id
        //update all household, member, visit codes with new region prefix

        fakeDataRegions.each { oldcode, f ->
            //change region code BAL/PF1001(001/-001-001)
            Region.executeUpdate("update Region r set r.code=?, r.name=? where r.id=?", [f.new_code, f.new_name, f.id])
            Region.executeUpdate("update Region r set r.parentRegionCode=? where r.parentRegionCode=?", [f.new_code, oldcode])

            //Update all dependencies that use ${oldcode} region code

            Household.executeUpdate("update Household set code=concat(:ncode,substring(code,4,length(code)-3)) where substring(code,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Household.executeUpdate("update Household set headCode=concat(:ncode,substring(headCode,4,length(headCode)-3)) where substring(headCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Household.executeUpdate("update Household set region=:ncode where region=:ocode", [ncode:f.new_code, ocode:oldcode])

            Enumeration.executeUpdate("update Enumeration set householdCode=concat(:ncode,substring(householdCode,4,length(householdCode)-3)) where substring(householdCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            HeadRelationship.executeUpdate("update HeadRelationship set householdCode=concat(:ncode,substring(householdCode,4,length(householdCode)-3)) where substring(householdCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set destinationCode=concat(:ncode,substring(destinationCode,4,length(destinationCode)-3)) where substring(destinationCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set originCode=concat(:ncode,substring(originCode,4,length(originCode)-3)) where substring(originCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set householdCode=concat(:ncode,substring(householdCode,4,length(householdCode)-3)) where substring(householdCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set destinationCode=concat(:ncode,substring(destinationCode,4,length(destinationCode)-3)) where substring(destinationCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set originCode=concat(:ncode,substring(originCode,4,length(originCode)-3)) where substring(originCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Residency.executeUpdate("update Residency set householdCode=concat(:ncode,substring(householdCode,4,length(householdCode)-3)) where substring(householdCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Visit.executeUpdate("update Visit set householdCode=concat(:ncode,substring(householdCode,4,length(householdCode)-3)) where substring(householdCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set householdCode=concat(:ncode,substring(householdCode,4,length(householdCode)-3)) where substring(householdCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])

            Member.executeUpdate("update Member set motherCode=concat(:ncode,substring(motherCode,4,length(motherCode)-3)) where substring(motherCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set spouseCode=concat(:ncode,substring(spouseCode,4,length(spouseCode)-3)) where substring(spouseCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set fatherCode=concat(:ncode,substring(fatherCode,4,length(fatherCode)-3)) where substring(fatherCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Death.executeUpdate("update Death set memberCode=concat(:ncode,substring(memberCode,4,length(memberCode)-3)) where substring(memberCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Enumeration.executeUpdate("update Enumeration set memberCode=concat(:ncode,substring(memberCode,4,length(memberCode)-3)) where substring(memberCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            HeadRelationship.executeUpdate("update HeadRelationship set memberCode=concat(:ncode,substring(memberCode,4,length(memberCode)-3)) where substring(memberCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            HeadRelationship.executeUpdate("update HeadRelationship set headCode=concat(:ncode,substring(headCode,4,length(headCode)-3)) where substring(headCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Household.executeUpdate("update Household set headCode=concat(:ncode,substring(headCode,4,length(headCode)-3)) where substring(headCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set memberCode=concat(:ncode,substring(memberCode,4,length(memberCode)-3)) where substring(memberCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            IncompleteVisit.executeUpdate("update IncompleteVisit set memberCode=concat(:ncode,substring(memberCode,4,length(memberCode)-3)) where substring(memberCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            MaritalRelationship.executeUpdate("update MaritalRelationship set memberA_code=concat(:ncode,substring(memberA_code,4,length(memberA_code)-3)) where substring(memberA_code,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            MaritalRelationship.executeUpdate("update MaritalRelationship set memberB_code=concat(:ncode,substring(memberB_code,4,length(memberB_code)-3)) where substring(memberB_code,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set motherCode=concat(:ncode,substring(motherCode,4,length(motherCode)-3)) where substring(motherCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set spouseCode=concat(:ncode,substring(spouseCode,4,length(spouseCode)-3)) where substring(spouseCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Member.executeUpdate("update Member set fatherCode=concat(:ncode,substring(fatherCode,4,length(fatherCode)-3)) where substring(fatherCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set memberCode=concat(:ncode,substring(memberCode,4,length(memberCode)-3)) where substring(memberCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyRegistration.executeUpdate("update PregnancyRegistration set motherCode=concat(:ncode,substring(motherCode,4,length(motherCode)-3)) where substring(motherCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyChild.executeUpdate("update PregnancyChild set childCode=concat(:ncode,substring(childCode,4,length(childCode)-3)) where substring(childCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyOutcome.executeUpdate("update PregnancyOutcome set fatherCode=concat(:ncode,substring(fatherCode,4,length(fatherCode)-3)) where substring(fatherCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyOutcome.executeUpdate("update PregnancyOutcome set motherCode=concat(:ncode,substring(motherCode,4,length(motherCode)-3)) where substring(motherCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Residency.executeUpdate("update Residency set memberCode=concat(:ncode,substring(memberCode,4,length(memberCode)-3)) where substring(memberCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Visit.executeUpdate("update Visit set respondentCode=concat(:ncode,substring(respondentCode,4,length(respondentCode)-3)) where substring(respondentCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])

            Death.executeUpdate("update Death set visitCode=concat(:ncode,substring(visitCode,4,length(visitCode)-3)) where substring(visitCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            Enumeration.executeUpdate("update Enumeration set visitCode=concat(:ncode,substring(visitCode,4,length(visitCode)-3)) where substring(visitCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            InMigration.executeUpdate("update InMigration set visitCode=concat(:ncode,substring(visitCode,4,length(visitCode)-3)) where substring(visitCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            IncompleteVisit.executeUpdate("update IncompleteVisit set visitCode=concat(:ncode,substring(visitCode,4,length(visitCode)-3)) where substring(visitCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            OutMigration.executeUpdate("update OutMigration set visitCode=concat(:ncode,substring(visitCode,4,length(visitCode)-3)) where substring(visitCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyRegistration.executeUpdate("update PregnancyRegistration set visitCode=concat(:ncode,substring(visitCode,4,length(visitCode)-3)) where substring(visitCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
            PregnancyOutcome.executeUpdate("update PregnancyOutcome set visitCode=concat(:ncode,substring(visitCode,4,length(visitCode)-3)) where substring(visitCode,1,3)=:ocode", [ncode:f.new_code, ocode:oldcode])
        }
    }

    def changeHouseholdMemberNames() {
        println "updating members"
        Member.list().each {
            if (it.code.equals("UNK")) return

            //name
            def sptnames = it.name.toUpperCase().split("\\s+(?=\\S*\$)")
            def firstname = sptnames[0]
            def lastname = sptnames.length==1 ? "" : sptnames[1]

            if (it.gender == Gender.MALE) {
                def malename = malenamesMap.get(firstname)
                def surname = surnamesMap.get(lastname)

                if (malename == null) {
                    malename = getRandomMaleName()
                    malenamesMap.put(firstname, malename)
                }
                if (surname == null) {
                    surname = getRandomSurname()
                    surnamesMap.put(lastname, surname)
                }

                it.name = StringUtil.getFullname(malename, surname)
                it.save()
            }

            if (it.gender == Gender.FEMALE) {
                def femalename = femalenamesMap.get(firstname)
                def surname = surnamesMap.get(lastname)

                if (femalename == null) {
                    femalename = getRandomMaleName()
                    femalenamesMap.put(firstname, femalename)
                }
                if (surname == null) {
                    surname = getRandomSurname()
                    surnamesMap.put(lastname, surname)
                }

                it.name = StringUtil.getFullname(femalename, surname)
                it.save()
            }
        }

        println "update household names"
        Household.list().each {

            def sptnames = it.name.toUpperCase().split("\\s+(?=\\S*\$)")
            def firstname = sptnames[0]
            def lastname = sptnames.length==1 ? "" : sptnames[1]

            def newfirstname = malenamesMap.get(firstname)
            newfirstname = newfirstname==null ? femalenamesMap.get(firstname) : newfirstname
            newfirstname = newfirstname==null ? getRandomMaleName() : newfirstname

            def newlastname = surnamesMap.get(lastname)
            newlastname = newlastname==null ? getRandomSurname() : newlastname

            it.name = StringUtil.getFullname(newfirstname, newlastname)
            it.save()

        }

        println "update member father,mother,spouse names"
        //execute the file update_change_member_father_mother_spouse.sql
        //Member.executeUpdate("update Member m, Member b set m.fatherName=b.name where m.father.id=b.id")
        //Member.executeUpdate("update Member m, Member b set m.motherName=b.name where m.mother.id=b.id")
        //Member.executeUpdate("update Member m, Member b set m.spouseName=b.name where m.spouse.id=b.id")

    }

    def changeDatasetsModules() {
        Dataset.list().each {
            it.filename = it.filename.replace("hds2-explorer", "hds-explorer")
            it.save()
        }

        Module.executeUpdate("update Module set name=?, description=? where code=?", ["Study One", "First Study Project", "MX-002"])
        Module.executeUpdate("update Module set name=?, description=? where code=?", ["MALTEM Project", "Malaria Elimination Study", "MX-003"])
        Module.executeUpdate("update Module set name=?, description=? where code=?", ["CHAMPS Mozambique", "CHAMPS Project in Mozambique Site", "MX-004"])
    }

    def changeForms() {
        //remove forms

        def exclude_form_uuids = ['2c9fc91d818a29100181a3e2aab10050','ff80818180e8db410180e8db6eaa0190','ff80818180e8db410180e8db6ef6019f','ff80818180e8db410180e8db6f2c01ae','ff80818180e8db410180e8db6f5201b6','ff80818180e8db410180e8db6fb001be','ff80818180e8db410180e8db702301ec','ff80818180e8db410180e8db705d01fa','ff80818180e8db410180e8db70940207','ff80818180e8db410180e8db70c6020e','ff80818180e8db410180e8db70f30217','ff80818180e8db410180e8db711c0220','ff80818180e8db410180e8db714c0229','ff80818180e8db410180e8db71800238','ff80818180e8db410180e8db71ad0246','ff80818180e8db410180e8db71dc024b','ff80818180e8db410180e8db721c0254','ff80818180e8db410180e8db724f025b','ff80818180e8db410180e8db727a0266','ff80818180e8db410180e8db72af026d','ff80818180e8db410180e8db72f3027c','ff80818180e8db410180e8db732e028d','ff80818180e8db410180e8db736a0296','ff80818180e8db410180e8db73da02a7','ff80818180e8db410180e8db744e02d8','ff80818180e8db410180e8db748e02e7','ff80818180e8db410180e8db74cf02f9','ff80818180e8db410180e8db750a0307','ff80818180e8db410180e8db75420315','ff80818180e8db410180e8db756d0323','ff80818180e8db410180e8db758f0325','ff80818180e8db410180e8db75c30330','ff80818180e8db410180e8db75fe033c','ff80818180e8db410180e8db763c034c','ff80818180e8db410180e8db7677035c','ff80818180e8db410180e8db76ae0368','ff80818180e8db410180e8db76e30374','ff80818180e8db410180e8db77160380','ff80818180e8db410180e8db7749038c','ff80818180e8db410180e8db777d0398','ff80818180e8db410180e8db77ba03a4','ff80818180e8db410180e8db77f703b0','ff80818180e8db410180e8db783003be','ff80818180e8db410180e8db786703cb','ff80818180e8db410180e8db789d03d8','ff80818180e8db410180e8db78d403e4','ff80818180e8db410180e8db791103f4','ff80818180e8db410180e8db79510400','ff80818180e8db410180e8db7995040e','ff80818180e8db410180e8db79d2041b','ff80818180e8db410180e8db7a140428','ff80818180e8db410180e8db7a580437','ff80818180e8db410180e8db7a900443','ff80818180e8db410180e8db7ad10450','ff80818180e8db410180e8db7b14045c','ff80818180e8db410180e8db7b500468','ff80818180e8db410180e8db7b900474','ff80818180e8db410180e8db7bd00480','ff80818180e8db410180e8db7c12048d','ff80818180e8db410180e8db7c530499','ff80818180e8db410180e8db7c9504a6','ff80818180e8db410180e8db7cde04b2','ff80818180e8db410180e8db7d1e04be','ff80818180e8db410180e8db7d5c04ca','ff80818180e8db410180e8db7d9c04d6','ff80818180e8db410180e8db7deb04e3','ff80818180e8db410180e8db7e2e04f1','ff80818180e8db410180e8db7e6a04fd','ff80818180e8db410180e8db7eb30509','ff80818180e8db410180e8db7ef20515','ff80818180e8db410180e8db7f330521','ff80818180e8db410180e8db7f76052d','ff80818180e8db410180e8db7fb70539','ff80818180e8db410180e8db7ff90546','ff80818180e8db410180e8db80460552','ff80818180e8db410180e8db8087055e','ff80818180e8db410180e8db80cb056b','ff80818180e8db410180e8db810e0577','ff80818180e8db410180e8db814d0583','ff80818180e8db410180e8db818b0590','ff80818180e8db410180e8db81cf059c','ff80818180e8db410180e8db820c05a0','ff80818180e8db410180e8db825705af','ff80818180e8db410180e8db829e05be','ff80818180e8db410180e8db830b05c5','ff80818180e8db410180e8db839a05f0','ff80818180e8db410180e8db83f00601','ff80818180e8db410180e8db8438060c','ff80818180e8db410180e8db847f061a','ff80818180e8db410180e8db84d10628','ff80818180e8db410180e8db85260639','ff80818180e8db410180e8db8581064a','ff80818180e8db410180e8db85dc0659','ff80818180e8db410180e8db8623066a','ff80818180e8db410180e8db8660066b','ff80818180e8db410180e8db86b4067c','ff80818180e8db410180e8db86fc0687','ff80818180e8db410180e8db874e0692','ff80818180e8db410180e8db8796069e','ff80818180e8db410180e8db87dc06aa','ff80818180e8db410180e8db882306b5','ff80818180e8db410180e8db886606c1','ff80818180e8db410180e8db88b206cd','ff80818180e8db410180e8db88fc06d9','ff80818180e8db410180e8db894606e5','ff80818180e8db410180e8db899106f1','ff80818180e8db410180e8db8cbe0764','ff80818180e8db410180e8db8d060768','ff80818180e8db410180e8db8d8a077f','ff80818180e8db410180e8db8dce0788','ff80818180e8db410180e8db8e0d0791','ff80818180e8db410180e8db8e4c0799','ff80818180e8db410180e8db8e9307a0','ff80818180e8db410180e8db8ed507ae']

        FormMapping.executeUpdate("delete from FormMapping f where f.form.id in (:list)", [list:exclude_form_uuids])
        Form.executeUpdate("delete from Form where id in (:list)", [list: exclude_form_uuids])

        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["CHAMPS Form 1", "CHAMPS First Form", "2c9fc91d81ae6c480181c8ab04160058"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["CHAMPS Form 2", "CHAMPS Second Form", "2c9fc91d821045d20182106b9ec3004f"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["CHAMPS Form 3", "CHAMPS Third Form", "2c9fc91d821045d20182106c5cea0050"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["CHAMPS Form 4", "CHAMPS Fourth Form", "2c9fc91d823425cc018234e19833004f"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["CHAMPS Form 5", "CHAMPS Fiveth Form", "2c9fc91d82452398018258e26eb6004f"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["CHAMPS Form 6", "CHAMPS Sixth Form", "2c9fc91d826ef501018281a0140e004f"])

        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Household Enroll Form", "Maltem Form 1", "ff80818180e8db410180e8db89cd06fd"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Individual Enrollment Form", "Maltem Form 2", "ff80818180e8db410180e8db8a0b0701"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Individual Updates Form", "Maltem Form 3",	"ff80818180e8db410180e8db8a4c070a"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Specimen Collection Form", "Maltem Form 4", "ff80818180e8db410180e8db8a930714"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Form 5", "Maltem Form 5", "ff80818180e8db410180e8db8ada071f"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Student Withdrawal Form", "Maltem Form 6", "ff80818180e8db410180e8db8b30072a"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Form 7", "Maltem Form 7", "ff80818180e8db410180e8db8b820736"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Form 8", "Maltem Form 8", "ff80818180e8db410180e8db8bd60741"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Form 9", "Maltem Form 9", "ff80818180e8db410180e8db8c28074c"])
        Form.executeUpdate("update Form set formName=?, formDescription=? where id=?", ["MALTEM Form X", "Maltem Form X", "ff80818180e8db410180e8db8c790758"])


        //Tracking Lists

        TrackingList.executeUpdate("update TrackingList set name=? where code=?", ["MALTEM MDA Individuals", "TR-000001"])
        TrackingListGroup.list().each {
            it.groupName = "MDA Individuals"
            it.groupTitle = "Manica group ${it.groupCode.substring(1)}"
            it.groupDetails = "Manica Individuals drugs admin."
            it.save()
        }
        TrackingListMapping.executeUpdate("update TrackingListMapping set listTitle=?", ["Individuals List"])

    }

    def createNewRegionCodes() {
        println "creating new unique region codes"

        def excelInputFile = "/home/paul/local/personal/codes/hds-explorer/hds-explorer-resources/files/fake_data/fake_data_regions.xlsx"
        FileInputStream inputStream = null
        XSSFWorkbook inputWorkbook = null
        XSSFSheet sheet = null

        try {
            inputStream = new FileInputStream(excelInputFile)
            inputWorkbook = new XSSFWorkbook(inputStream)
            sheet = inputWorkbook.getSheetAt(0)
        } catch (Exception ex) {
            ex.printStackTrace()
            return
        }

        //navigate through rows
        def regionCodes = Region.list().collect { t -> t.code}

        sheet.rowIterator().eachWithIndex { row, index ->

            if (index == 0) return

            Cell newCodeCell = row.getCell(4)
            String newNameValue = getCellValue(row.getCell(5))

            println "${newNameValue}"

            def newCode = codeGeneratorService.codeGenerator.generateRegionCode(null, newNameValue, regionCodes)
            regionCodes.add(newCode)

            if (newCodeCell == null) {
                newCodeCell = row.createCell(4)
            }

            newCodeCell.setCellValue(newCode)
        }

        inputStream.close()

        def outputStream = new FileOutputStream(excelInputFile)
        inputWorkbook.write(outputStream)

        println "finished creation"
    }

    def createNewUserCodes() {
        println "creating new unique user codes"

        def excelInputFile = "/home/paul/local/personal/codes/hds-explorer/hds-explorer-resources/files/fake_data/fake_data_users.xlsx"
        FileInputStream inputStream = null
        XSSFWorkbook inputWorkbook = null
        XSSFSheet sheet = null

        try {
            inputStream = new FileInputStream(excelInputFile)
            inputWorkbook = new XSSFWorkbook(inputStream)
            sheet = inputWorkbook.getSheetAt(0)
        } catch (Exception ex) {
            ex.printStackTrace()
            return
        }

        //navigate through rows
        def userCodes = User.list().collect { t -> t.code}

        sheet.rowIterator().eachWithIndex { row, index ->

            if (index == 0) return

            Cell newCodeCell = row.getCell(5)
            Cell newUserCell = row.getCell(6)
            String newFirstNameValue = getCellValue(row.getCell(7))
            String newLastNameValue = getCellValue(row.getCell(8))

            println "${newFirstNameValue}"

            User user = new User()
            user.username = " "
            user.firstName = newFirstNameValue
            user.lastName = newLastNameValue

            def newCode = codeGeneratorService.codeGenerator.generateUserCode(user, userCodes)
            userCodes.add(newCode)

            if (newCodeCell == null) {
                newCodeCell = row.createCell(5)
                newUserCell = row.createCell(6)
            }

            newCodeCell.setCellValue(newCode)
            newUserCell.setCellValue("FW${newCode}")
        }

        inputStream.close()

        def outputStream = new FileOutputStream(excelInputFile)
        inputWorkbook.write(outputStream)

        println "finished creation"
    }

    Map<String, FakeDataRegion> getRegionsXLS(){
        println "reading fake data region codes"


        def map = new LinkedHashMap<String, FakeDataRegion>()

        def excelInputFile = "/home/paul/local/personal/codes/hds-explorer/hds-explorer-resources/files/fake_data/fake_data_regions.xlsx"
        FileInputStream inputStream = null
        XSSFWorkbook inputWorkbook = null
        XSSFSheet sheet = null

        try {
            inputStream = new FileInputStream(excelInputFile)
            inputWorkbook = new XSSFWorkbook(inputStream)
            sheet = inputWorkbook.getSheetAt(0)
        } catch (Exception ex) {
            ex.printStackTrace()
            return
        }

        //navigate through rows

        sheet.rowIterator().eachWithIndex { row, index ->

            if (index == 0) return

            String id = getCellValue(row.getCell(0))
            String code = getCellValue(row.getCell(1))
            String name = getCellValue(row.getCell(2))
            String level = getCellValue(row.getCell(3))
            String newcode = getCellValue(row.getCell(4))
            String newname = getCellValue(row.getCell(5))

            println "${newcode}-${newname}"

            def fakeData = new FakeDataRegion()
            fakeData.id = id
            fakeData.code = code
            fakeData.name = name
            fakeData.level = level
            fakeData.new_code = newcode
            fakeData.new_name = newname

            map.put(code, fakeData)
        }

        inputStream.close()

        println "finished reading"

        return map
    }

    Map<String, FakeDataUser> getUsersXLS(){
        println "reading fake data user codes"


        def map = new LinkedHashMap<String, FakeDataUser>()

        def excelInputFile = "/home/paul/local/personal/codes/hds-explorer/hds-explorer-resources/files/fake_data/fake_data_users.xlsx"
        FileInputStream inputStream = null
        XSSFWorkbook inputWorkbook = null
        XSSFSheet sheet = null

        try {
            inputStream = new FileInputStream(excelInputFile)
            inputWorkbook = new XSSFWorkbook(inputStream)
            sheet = inputWorkbook.getSheetAt(0)
        } catch (Exception ex) {
            ex.printStackTrace()
            return
        }

        //navigate through rows

        sheet.rowIterator().eachWithIndex { row, index ->

            if (index == 0) return

            String id = getCellValue(row.getCell(0))
            String code = getCellValue(row.getCell(1))
            String username = getCellValue(row.getCell(2))
            String firstname = getCellValue(row.getCell(3))
            String lastname = getCellValue(row.getCell(4))
            String newcode = getCellValue(row.getCell(5))
            String newusername = getCellValue(row.getCell(6))
            String newfirstname = getCellValue(row.getCell(7))
            String newlastname = getCellValue(row.getCell(8))

            println "${newcode}-${newfirstname}"

            def fakeData = new FakeDataUser()
            fakeData.id = id
            fakeData.code = code
            fakeData.username = username
            fakeData.firstname = firstname
            fakeData.lastname = lastname
            fakeData.new_code = newcode
            fakeData.new_username = newusername
            fakeData.new_firstname = newfirstname
            fakeData.new_lastname = newlastname

            map.put(code, fakeData)
        }

        inputStream.close()

        println "finished reading"


        return map
    }

    def readMaleNamesXLS() {
        println "reading fake data males names"

        def excelInputFile = "/home/paul/local/personal/codes/hds-explorer/hds-explorer-resources/files/fake_data/male_names_fake.xlsx"
        FileInputStream inputStream = null
        XSSFWorkbook inputWorkbook = null
        XSSFSheet sheet = null

        try {
            inputStream = new FileInputStream(excelInputFile)
            inputWorkbook = new XSSFWorkbook(inputStream)
            sheet = inputWorkbook.getSheetAt(0)

            //navigate through rows

            sheet.rowIterator().eachWithIndex { row, index ->
                if (index == 0) return

                String firstname = getCellValue(row.getCell(0))
                String lastname = getCellValue(row.getCell(1))

                malenamesList.add(firstname.toUpperCase())
                surnamesList.add(lastname.toUpperCase())
            }

            inputStream.close()

            println "finished reading"

        } catch (Exception ex) {
            ex.printStackTrace()
            return
        }

    }

    def readFemaleNamesXLS() {
        println "reading fake data females names"

        def excelInputFile = "/home/paul/local/personal/codes/hds-explorer/hds-explorer-resources/files/fake_data/female_names_fake.xlsx"
        FileInputStream inputStream = null
        XSSFWorkbook inputWorkbook = null
        XSSFSheet sheet = null

        try {
            inputStream = new FileInputStream(excelInputFile)
            inputWorkbook = new XSSFWorkbook(inputStream)
            sheet = inputWorkbook.getSheetAt(0)

            //navigate through rows

            sheet.rowIterator().eachWithIndex { row, index ->
                if (index == 0) return

                String firstname = getCellValue(row.getCell(0))
                String lastname = getCellValue(row.getCell(1))

                femalenamesList.add(firstname.toUpperCase())
                surnamesList.add(lastname.toUpperCase())
            }

            inputStream.close()

            println "finished reading"

        } catch (Exception ex) {
            ex.printStackTrace()
            return
        }

    }

    String getRandomMaleName() {
        int i = malenamesList.size()

        def random = new Random().nextInt(i)

        return malenamesList.get(random)
    }

    String getRandomFemaleName() {
        int i = femalenamesList.size()

        def random = new Random().nextInt(i)

        return femalenamesList.get(random)
    }

    String getRandomSurname() {
        int i = surnamesList.size()

        def random = new Random().nextInt(i)

        return surnamesList.get(random)
    }

    private String getCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        return value==null ? null : value.trim();
    }

    class FakeDataUser {
        String id
        String code
        String username
        String firstname
        String lastname
        String new_code
        String new_username
        String new_firstname
        String new_lastname

    }

    class FakeDataRegion {
        String id
        String code
        String name
        String level
        String new_code
        String new_name

    }
}
