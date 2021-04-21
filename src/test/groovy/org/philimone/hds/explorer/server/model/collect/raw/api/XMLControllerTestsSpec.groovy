package org.philimone.hds.explorer.server.model.collect.raw.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import grails.converters.JSON
import grails.converters.XML
import grails.testing.gorm.DataTest
import grails.testing.spring.AutowiredTest
import grails.testing.web.controllers.ControllerUnitTest
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.XmlFormatter
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyChild
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyOutcome
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType
import spock.lang.Specification

import java.lang.reflect.Type
import java.time.LocalDateTime

class XMLControllerTestsSpec extends Specification implements ControllerUnitTest<RawImportApiController>, DataTest, AutowiredTest {

    def setup() {
        mockDomains RawHousehold, RawPregnancyOutcome, RawPregnancyChild, RawRegion
    }

    def cleanup() {
    }

    RawHousehold getHouseholdsList(){

        def list = new ArrayList<RawHousehold>()

        def rw1 = new RawHousehold(
                id: "uuuid1",
                regionCode: "TXU",
                regionName: "Txumene",
                householdCode: "TXUDF1001",
                householdName: "Macandza House",
                headCode: "",
                headName: "",
                gpsLng: "ASDASD",
                gpsAlt: "10.1000",
                gpsLat: "10.1000",
                gpsAcc: "10.1000",

                collectedBy: "dragon",
                collectedDate: LocalDateTime.now(),
                uploadedDate: LocalDateTime.now()
        )

        def rw2 = new RawHousehold(
                id: "uuuid1",
                regionCode: "TXU",
                regionName: "Txumene",
                householdCode: "TXUDF1001",
                householdName: "Macandza House",
                headCode: "",
                headName: "",
                gpsLng: "ASDASD",
                gpsAlt: "10.1000",
                gpsLat: "10.1000",
                gpsAcc: "10.1000",

                collectedBy: "dragon",
                collectedDate: LocalDateTime.now(),
                uploadedDate: LocalDateTime.now()
        )

        list << rw1
        list << rw2

        return rw1
    }

    RawPregnancyOutcome getPregnancyOutcomes() {
        def list = new ArrayList<RawPregnancyOutcome>()

        def rpo1 = new RawPregnancyOutcome (
                id: "uuuid3",
                code: "SOME-01",
                motherCode: "123213213",
                fatherCode: "123133131",
                numberOfOutcomes: 1,
                outcomeDate: GeneralUtil.getDate(2020, 4, 12),
                birthPlace: BirthPlace.HEALTH_CENTER_CLINIC.code,
                birthPlaceOther: null,
                visitCode: "ppp",
                collectedBy: "dragon",
                collectedDate: GeneralUtil.getDate(2020, 4, 12, 0, 0, 0),
                uploadedDate: GeneralUtil.getDate(2020, 4, 14, 0, 0, 0)
        )

        def rpo1child1 = new RawPregnancyChild(
                id: "uuuid4",

                outcomeType: PregnancyOutcomeType.LIVEBIRTH.code,
                childCode: "PPPL-01",
                childName: "John Macandza",
                childGender: Gender.MALE.code,
                childOrdinalPosition: 1,
                headRelationshipType: HeadRelationshipType.SON_DAUGHTER.code
        )

        rpo1.addToChilds(rpo1child1)

        def result = rpo1.save(flush:true)

        rpo1.addToChilds(rpo1child1)
        rpo1.save()

        println "childs - ${rpo1.childs.size()}, result = ${result}"
        println "errors : ${rpo1.errors}"

        //rpo1.childs.add(rpo1child1)

        list << result

        return result
    }

    def printHousehold(RawHousehold rawHousehold){

        println "rawHousehold.id = ${rawHousehold.id}"
        println "rawHousehold.regionCode = ${rawHousehold.regionCode}"
        println "rawHousehold.regionName = ${rawHousehold.regionName}"
        println "rawHousehold.householdCode = ${rawHousehold.householdCode}"
        println "rawHousehold.householdName = ${rawHousehold.householdName}"
        println "rawHousehold.headCode = ${rawHousehold.headCode}"
        println "rawHousehold.headName = ${rawHousehold.headName}"
        println "rawHousehold.gpsLng = ${rawHousehold.gpsLng}"
        println "rawHousehold.gpsAlt = ${rawHousehold.gpsAlt}"
        println "rawHousehold.gpsLat = ${rawHousehold.gpsLat}"
        println "rawHousehold.gpsAcc = ${rawHousehold.gpsAcc}"
        println "rawHousehold.collectedBy = ${rawHousehold.collectedBy}"
        println "rawHousehold.collectedDate = ${rawHousehold.collectedDate}"
        println "rawHousehold.uploadedDate = ${rawHousehold.uploadedDate}"
        println "rawHousehold.processedStatus = ${rawHousehold.processedStatus.code}"
        println "rawHousehold.postExecution = ${rawHousehold.postExecution}"
    }

    void "testing gson something"() {

        def x = 1
        def y = 1



        RawHousehold rawHousehold = getHouseholdsList()


        def xml1 = (rawHousehold as XML).toString()
        def xml2 = (getPregnancyOutcomes() as XML).toString()
        def rg1 = new RawRegion(regionCode: "MAP", regionName: "Maputo", parentCode: "", collectedBy: "dragon", collectedDate: LocalDateTime.now(), uploadedDate: LocalDateTime.now())


        println "list of households json"
        println()
        println XmlFormatter.format(xml1)

        println()
        println "list of pregnancies json"
        println()
        println XmlFormatter.format(xml2)

        println "list of regions json"
        println()
        println XmlFormatter.format((rg1 as XML).toString())


        expect:"equality"
            x== y
    }
}
