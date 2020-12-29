package org.philimone.hds.explorer.server.model.settings.generator

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.testing.spring.AutowiredTest
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Visit
import spock.lang.Specification

class CodeGeneratorServiceSpec extends Specification implements ServiceUnitTest<CodeGeneratorService>, DataTest, AutowiredTest{

    def setup() {
        mockDomains User, Region, Household, Member, Visit

        setupRegions()
        setupHouseholds()
        setupMembers()
        setupVisits()
    }

    def cleanup() {
    }

    void setupRegions(){
        new Region(code: "MAP", name: "MAP", hierarchyLevel: RegionLevel.HIERARCHY_1, parent: null).save(flush:true )
        new Region(code: "MAT", name: "MAT", hierarchyLevel: RegionLevel.HIERARCHY_1, parent: null).save(flush:true)
        new Region(code: "MAX", name: "MAX", hierarchyLevel: RegionLevel.HIERARCHY_1, parent: null).save(flush:true)
        new Region(code: "MAN", name: "MAN", hierarchyLevel: RegionLevel.HIERARCHY_1, parent: null).save(flush:true)
        new Region(code: "MA2", name: "MA2", hierarchyLevel: RegionLevel.HIERARCHY_1, parent: null).save(flush:true)

        //println "regions: ${Region.count()}"
    }

    void setupHouseholds(){
        def region = Region.findByCode("MAP")

        new Household(code: "MAPTXI001", region: "MAP", name: "House 1", parentRegion: region).save(flush:true)
        new Household(code: "MAPTXI003", region: "MAP", name: "House 3", parentRegion: region).save(flush:true)
        new Household(code: "MAPTXI004", region: "MAP", name: "House 4", parentRegion: region).save(flush:true)
        new Household(code: "MAPTXI005", region: "MAP", name: "House 5", parentRegion: region).save(flush:true)
        new Household(code: "MAPTXI007", region: "MAP", name: "House 7", parentRegion: region).save(flush:true)

        //println "households: ${Household.count()}"
    }

    void setupMembers(){
        def household = Household.findByCode("MAPTXI001")

        new Member(code: "MAPTXI001001", name: "Member 1", gender: Gender.MALE, dob: new Date(), maritalStatus: MaritalStatus.SINGLE).save(flush:true)
        new Member(code: "MAPTXI001003", name: "Member 3", gender: Gender.MALE, dob: new Date(), maritalStatus: MaritalStatus.SINGLE).save(flush:true)
        new Member(code: "MAPTXI001005", name: "Member 5", gender: Gender.MALE, dob: new Date(), maritalStatus: MaritalStatus.SINGLE).save(flush:true)
        new Member(code: "MAPTXI001006", name: "Member 6", gender: Gender.MALE, dob: new Date(), maritalStatus: MaritalStatus.SINGLE).save(flush:true)
        new Member(code: "MAPTXI001007", name: "Member 7", gender: Gender.MALE, dob: new Date(), maritalStatus: MaritalStatus.SINGLE).save(flush:true)


        //println "members: ${Member.count()}"
    }

    void setupVisits(){
        def household = Household.findByCode("MAPTXI001")

        new Visit(code: "MAPTXI001001", household: household, householdCode: household.code,  visitDate: new Date(), roundNumber: 1).save(flush:true)
        new Visit(code: "MAPTXI001003", household: household, householdCode: household.code,  visitDate: new Date(), roundNumber: 1).save(flush:true)
        new Visit(code: "MAPTXI001005", household: household, householdCode: household.code,  visitDate: new Date(), roundNumber: 1).save(flush:true)
        new Visit(code: "MAPTXI001006", household: household, householdCode: household.code,  visitDate: new Date(), roundNumber: 1).save(flush:true)
        new Visit(code: "MAPTXI001007", household: household, householdCode: household.code,  visitDate: new Date(), roundNumber: 1).save(flush:true)


        //println "visits: ${Visit.count()}"
    }

    void "Test code generations - Regions - Default"() {

        def regions = ["MAP","MAT","MAX","MAN","MA2"]
        def regionNames = ["M","Matola","Maxixe","Manica","Matosinhos"]
        def count = 0
        def max = regions.size()

        println "region existent codes -> ${regions}"

        DefaultCodeGenerator generator = new DefaultCodeGenerator()

        regionNames.each { name ->
            def gen = generator.generateRegionCode(name, regions)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) count++
        }

        expect:
            count == max
    }

    void "Test code generations - Households - Default"() {

        def codes = ["MAPTXI001","MAPTXI003","MAPTXI004","MAPTXI005","MAPTXI007"]
        def regionNames = ["01","02","03","04","05"]
        def count = 0
        def max = codes.size()

        println "household existent codes -> ${codes}"

        DefaultCodeGenerator generator = new DefaultCodeGenerator()

        regionNames.each { name ->
            def gen = generator.generateHouseholdCode("MAPTXI", codes)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) {
                count++
                codes.add(gen)
            }
        }

        expect:
        count == max
    }

    void "Test code generations - Members - Default"() {

        def codes = ["MAPTXI001001","MAPTXI001003","MAPTXI001005","MAPTXI001006","MAPTXI001007"]
        def regionNames = ["01","02","03","04","05"]
        def count = 0
        def max = codes.size()

        println "members existent codes -> ${codes}"

        DefaultCodeGenerator generator = new DefaultCodeGenerator()

        regionNames.each { name ->
            def gen = generator.generateMemberCode("MAPTXI001", codes)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) {
                count++
                codes.add(gen)
            }
        }

        expect:
        count == max
    }

    void "Test code generations - Visit - Default"() {

        def codes = ["MAPTXI001001","MAPTXI001003","MAPTXI001005","MAPTXI001006","MAPTXI001007"]
        def regionNames = ["01","02","03","04","05"]
        def count = 0
        def max = codes.size()

        println "visits existent codes -> ${codes}"

        DefaultCodeGenerator generator = new DefaultCodeGenerator()

        regionNames.each { name ->
            def gen = generator.generateVisitCode("MAPTXI001", codes)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) {
                count++
                codes.add(gen)
            }
        }
        println()

        expect:
        count == max
    }

    void "Test code generations - Regions - Service"() {

        def regionNames = ["M","Matola","Maxixe","Manica","Matosinhos"]
        def count = 0
        def max = regionNames.size()

        println "service region existent codes -> ${Region.count()}"

        regionNames.each { name ->
            def gen = service.generateRegionCode(name)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) count++
        }
        println()

        expect:
        count == max
    }

    void "Test code generations - Households - Service"() {

        def names = ["01","02","03","04","05"]
        def count = 0
        def max = names.size()
        def region = Region.first()
        def user = new User(code: "TXI")

        println "service household existent codes -> ${Household.count()}"

        names.each { name ->
            def gen = service.generateHouseholdCode(region, user)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) {
                count++
                //codes.add(gen)

                new Household(code: gen, region: "MAP", name: "House ${name}", parentRegion: region).save(flush:true)
            }
        }
        println()

        expect:
        count == max
    }

    void "Test code generations - Members - Service"() {

        def names = ["01","02","03","04","05"]
        def count = 0
        def max = names.size()
        def household = Household.findByCode("MAPTXI001")

        println "service members existent codes -> ${Member.count()}"

        names.each { name ->
            def gen = service.generateMemberCode(household)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) {
                count++
                //codes.add(gen)
                new Member(code: gen, name: "Member ${name}", gender: Gender.MALE, dob: new Date(), maritalStatus: MaritalStatus.SINGLE).save(flush:true)
            }
        }
        println()

        expect:
        count == max
    }

    void "Test code generations - Visit - Service"() {

        def names = ["01","02","03","04","05"]
        def count = 0
        def max = names.size()
        def household = Household.findByCode("MAPTXI001")

        println "service visits existent codes -> ${Visit.count()}"

        names.each { name ->
            def gen = service.generateVisitCode(household)

            println "name=${name}, gen.code=${gen}"

            if (gen != null) {
                count++
                //codes.add(gen)
                new Visit(code: gen, household: household, householdCode: household.code,  visitDate: new Date(), roundNumber: 1).save(flush:true)
            }
        }
        println()

        expect:
        count == max
    }
}
