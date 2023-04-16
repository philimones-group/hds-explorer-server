package org.philimone.hds.explorer.services.testdata

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.philimone.hds.explorer.server.model.logs.LogReportFile

@Transactional
class TestDataService {

    def grailsDomainClassMappingContext //inject

    def repairKeyTimestamp(){
        //put key stamps and start end values

        long lastKey = 201910110001
        Date lastDate = null

        def results = LogReportFile.executeQuery("select l from LogReportFile l order by l.logReport.id, l.createdDate")

        results.each { LogReportFile log ->

            if (lastDate == null){
                lastDate = log.creationDate
            }

            def diff = TimeCategory.minus(log.creationDate, lastDate)
            println "diff ${diff}"


            if (diff.minutes < 10){ //less than 10 minutes
                //maintains
            }else{
                lastKey++
                lastDate = log.creationDate
            }

            log.keyTimestamp = lastKey
            log.start = log.creationDate
            log.end = log.creationDate

            log.save(flush:true)

        }
    }

    def List<PersistentProperty> getDomainProperties(Class clazz) {
        PersistentEntity entityClass = grailsDomainClassMappingContext.getPersistentEntity(clazz.name)
        List<PersistentProperty> persistentPropertyList = entityClass.persistentProperties
        persistentPropertyList.each { property ->
            //println "${property.name}, ${property.type.name}"
        }

        return persistentPropertyList
    }

    def String getRandomName(){
        def random = new Random()
        def nrNames = random.nextInt(2)
        def name = ""
        def n = listNames.size()

        nrNames = nrNames==0 ? 2 : (nrNames==1) ? 3 : nrNames

        if (nrNames == 2){
            name = "${listNames.get(random.nextInt(n))} ${listNames.get(random.nextInt(n))}"
        } else {
            name = "${listNames.get(random.nextInt(n))} ${listNames.get(random.nextInt(n))} ${listNames.get(random.nextInt(n))}"
        }

        return name
    }

    def insertHlcProcessingCore(){
/*
        if (HlcProcessingCore.count()==0)
        (1..12).each { n ->

            def core = new HlcProcessingCore()

            def uuid = GeneralUtil.generateUUID()


            def properties = getDomainProperties(core.class)
            def random = new Random()

            //fill with dummy data
            properties.each { prop ->
                def type = prop.type.name
                def name = prop.name

                if (StringUtil.containsAny(["Integer", "Long", "Double", "Decimal"], type)){
                    core."${prop.name}" = random.nextInt(500)
                }
                if (type.contains("String")){
                    if (name.toLowerCase().contains("name")){
                        core."${prop.name}" = getRandomName()
                        return
                    }

                    core."${prop.name}" = "CHEEK${StringUtil.leadZero(6, random.nextInt(999999))}"
                }

                if (type.contains("Character")){
                    core."${prop.name}" = 'y'
                }

                if (type.contains("Date")){
                    core."${prop.name}" = new Date()
                }

            }

            core.uri = uuid
            core.processedByDcs = 0
            core.save(flush:true)

            println("error: "+core.errors)

        }*/
    }

    def insertHlcRecordCore(){
/*
        if (HlcRecordCore.count()==0)
            (1..12).each { n ->

                def core = new HlcRecordCore()

                def uuid = GeneralUtil.generateUUID()


                def properties = getDomainProperties(core.class)
                def random = new Random()

                //fill with dummy data
                properties.each { prop ->
                    def type = prop.type.name
                    def name = prop.name

                    if (StringUtil.containsAny(["Integer", "Long", "Double", "Decimal"], type)){
                        core."${prop.name}" = random.nextInt(500)
                    }
                    if (type.contains("String")){
                        if (name.toLowerCase().contains("name")){
                            core."${prop.name}" = getRandomName()
                            return
                        }

                        core."${prop.name}" = "CHEEK${StringUtil.leadZero(6, random.nextInt(999999))}"
                    }

                    if (type.contains("Character")){
                        core."${prop.name}" = 'y'
                    }

                    if (type.contains("Date")){
                        core."${prop.name}" = new Date()
                    }

                }

                core.uri = uuid
                core.processedByDcs = 0
                core.save(flush:true)

                println("error: "+core.errors)

            }*/
    }


    def listNames = ["Smith", "Murphy", "Smith", "Li", "Smith", "Smith", "Jones", "O'Kelly", "Johnson", "Smith", "Jones", "Wilson", "Williams",
                     "O'Sullivan", "Williams", "Lam", "Williams", "Williams", "Brown", "Walsh", "Brown", "Martin", "Brown","Taylor", "Smith",
                     "Jones", "Gelbero", "Wilson", "Taylor", "Davies", "O'Brien", "Miller", "Roy", "Taylor", "Jones", "Wilson", "Byrne", "Davis",
                     "Tremblay", "Morton", "Singh", "Evans", "O'Ryan", "Garcia", "Lee", "White", "Wang", "Thomas", "O'Connor", "Rodriguez", "Gagnon",
                     "Martin", "Anderson", "Roberts", "O'Neill", "Wilson", "Wilson", "Anderson", "Li", "Amelia", "Margaret", "Emma", "Mary",
                     "Samantha", "Olivia", "Patricia", "Isla", "Bethany", "Sophia", "Jennifer", "Emily", "Elizabeth", "Isabella", "Elizabeth",
                     "Joanne", "Ava", "Linda", "Ava", "Megan", "Mia", "Barbara", "Isabella", "Victoria", "Emily", "Susan", "Jessica", "Lauren",
                     "Abigail", "Margaret", "Lily", "Michelle", "Madison", "Jessica", "Sophie", "Tracy", "Charlotte", "Sarah", "Poppy", "Olivia"]

}
