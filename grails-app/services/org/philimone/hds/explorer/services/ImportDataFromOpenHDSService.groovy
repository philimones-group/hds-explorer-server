package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import net.betainteractive.io.LogOutput
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.authentication.Role
import org.philimone.hds.explorer.authentication.User
import org.philimone.hds.explorer.authentication.UserRole
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.openhds.model.Death
import org.philimone.hds.explorer.openhds.model.Fieldworker
import org.philimone.hds.explorer.openhds.model.Individual
import org.philimone.hds.explorer.openhds.model.Location
import org.philimone.hds.explorer.openhds.model.Locationhierarchy
import org.philimone.hds.explorer.openhds.model.Locationhierarchylevel
import org.philimone.hds.explorer.openhds.model.Relationship
import org.philimone.hds.explorer.openhds.model.Socialgroup
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.logs.LogStatus
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.StudyModule
import org.philimone.hds.explorer.server.model.settings.ApplicationParam

import static grails.async.Promises.task
import static grails.async.Promises.waitAll

class ImportDataFromOpenHDSService {
    static datasource = ['openhds']

    static transactional = false

    def generalUtilitiesService
    def sessionFactory

    def importRegions(long logReportId){
        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "db-hierarchies-from-openhds");
        PrintStream output = log.output

        if (output == null) return;

        int processed = 0
        int errors = 0

        def locationHiearachies = []


        //Update hierarchy levels
        Locationhierarchylevel.withTransaction {
            Locationhierarchylevel.list().each { hierarchy ->
                def level = getHierarchyLevel(hierarchy.uuid)

                ApplicationParam.withTransaction {
                    ApplicationParam.executeUpdate("update ApplicationParam p set p.value=? where p.name=?", [hierarchy.name, level])
                }
            }
        }

        println "reading location hierarchies"
        Locationhierarchy.withTransaction {
            //                                                              0       1        2           3              4
            locationHiearachies = Locationhierarchy.executeQuery("select l.uuid, l.extId, l.name, l.parent.extId, l.level.uuid from Locationhierarchy l")
        }

        println "total location_hierarchies ${locationHiearachies.size()}"

        int from = 0;
        int to = 0;
        int max = 50

        while (processed < locationHiearachies.size()) {
            from = to
            to = (locationHiearachies.size() > to + max) ? (to + max) : locationHiearachies.size();
            //reading only some records to speed up the process

            Region.withNewTransaction {

                locationHiearachies.subList(from, to).each {

                    def region = new Region()

                    region.code = it[1]
                    region.name = it[2]
                    region.hierarchyLevel = getHierarchyLevel(it[4])
                    region.parent = Region.findByCode(it[3])

                    processed++

                    if (!region.save(flush: true)) {
                        errors++
                        def msg = "Couldnt create/save Region copied from OpenHDS Location Hiearchy with uuid=${fw.uuid}"
                        def msgErr = "Errors: ${user.errors}"

                        output.println(msg)
                        output.println(msgErr)
                        println(msg)
                        println(msgErr)
                    }
                }
            }
        }


        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(LogStatus.FINISHED)
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        output.close()
    }

    def importFieldWorkers(long logReportId){
        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "db-fieldworkers-from-openhds");
        PrintStream output = log.output

        if (output == null) return;

        int processed = 0
        int errors = 0
        def start = new Date()

        println "starting copy of fieldworkers - ${start}"

        println "reading existing fieldworkers  - ${TimeCategory.minus(new Date(), start)}"

        def fieldworkers = []

        Fieldworker.withTransaction {
            fieldworkers = Fieldworker.findAll()
        }

        println "creating/updating fieldworkers ${fieldworkers.size()} - ${TimeCategory.minus(new Date(), start)}"

        int from = 0;
        int to = 0;
        int max = 50

        while (processed < fieldworkers.size()){
            from = to
            to = (fieldworkers.size() > to+max) ? (to+max) : fieldworkers.size(); //reading only some records to speed up the process

            User.withNewTransaction {

                def role = Role.findByAuthority(Role.ROLE_FIELD_WORKER)
                def module = StudyModule.findByCode(StudyModule.DSS_SURVEY_MODULE)

                fieldworkers.subList(from, to).each { Fieldworker fw ->
                    //perform saves
                    def extid = fw.extId
                    def firstName = fw.firstName
                    def lastName = fw.lastName
                    def password = fw.passwordHash

                    User user = User.findOrCreateByUsername(extid)
                    //User user = new User()
                    user.firstName = firstName
                    user.lastName = lastName
                    user.username = extid
                    user.password = password
                    user.isPasswordEncoded = true
                    user.enabled = true


                    if (user.modules == null || user.modules.size()==0){
                        //println "adding module dss for user=${user.username}, ${user.modules}"
                        user.addToModules(module)
                    }

                    processed++

                    if (user.save(flush: true)) {
                        UserRole.create(user, role)
                    }else{
                        errors++
                        def msg = "Couldnt create/save User copied from OpenHDS Fielworker with uuid=${fw.uuid}"
                        def msgErr = "Errors: ${user.errors}"

                        output.println(msg)
                        output.println(msgErr)
                        println(msg)
                        println(msgErr)
                    }

                    //println("saving ${processed}, ${individual.errors}")
                }

                println("${processed}/${max} reached clearing session - ${TimeCategory.minus(new Date(), start)}")
                sessionFactory.currentSession.flush() //clearing cache save us a lot of time
                sessionFactory.currentSession.clear()
            }


        }

        println("finished creating/updating fieldworkers!! - ${TimeCategory.minus(new Date(), start)}")


        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(LogStatus.FINISHED)
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        output.close()
    }

    def importHouseholds(long logReportId){
        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "db-households-from-openhds");
        PrintStream output = log.output

        if (output == null) return;

        int processed = 0
        int errors = 0
        def start = new Date()

        println "starting copy of households - ${start}"

        println "reading existing households  - ${TimeCategory.minus(new Date(), start)}"


        /* Import Functions here */
        def houses = []
        def heads = []
        def mapHeads = [:]

        Location.withTransaction {
            houses = Location.executeQuery("select l.extId, l.locationName, l.accuracy, l.latitude, l.longitude, l.altitude, l.locationhierarchy.uuid from Location l")
            heads = Socialgroup.executeQuery("select s.extId, s.individual.extId, s.individual.firstName, s.individual.middleName, s.individual.lastName  from Socialgroup s")
        }

        println  "houses to process ${houses.size()}"
        println  "heads found ${heads.size()}"

        heads.each {
            mapHeads.put(it[0], [it[1], it[2], it[3], it[4]]) // save socialgroup id and extid,fName,mName,lName
        }

        Household.withTransaction {
            Household.executeUpdate("delete from Household ")
        }

        int from = 0;
        int to = 0;
        int max = 500

        while (processed < houses.size()) {
            from = to
            to = (houses.size() > to + max) ? (to + max) : houses.size();
            //reading only some records to speed up the process

            Household.withTransaction {
                houses.subList(from, to).each { obj ->
                    processed++

                    def hierarchy = getHierarchies(obj[6])
                    def household = new Household()

                    household.code = obj[0]
                    household.name = obj[1]
                    household.headCode = "UNK"
                    household.headName = "Unknown Individual"
                    household.secHeadCode = "UNK"

                    household.region = hierarchy.region
                    household.hierarchy1 = hierarchy.hierarchy1
                    household.hierarchy2 = hierarchy.hierarchy2
                    household.hierarchy3 = hierarchy.hierarchy3
                    household.hierarchy4 = hierarchy.hierarchy4
                    household.hierarchy5 = hierarchy.hierarchy5
                    household.hierarchy6 = hierarchy.hierarchy6
                    household.hierarchy7 = hierarchy.hierarchy7
                    household.hierarchy8 = hierarchy.hierarchy8

                    household.gpsAccuracy = getDoubleValue(obj[2])
                    household.gpsLatitude = getDoubleValue(obj[3])
                    household.gpsLongitude = getDoubleValue(obj[4])
                    household.gpsAltitude = getDoubleValue(obj[5])
                    household.gpsNull = household.gpsLatitude==null


                    household.cosLatitude =  household.gpsLatitude==null ?  null : Math.cos(household.gpsLatitude*Math.PI / 180.0)
                    household.sinLatitude =  household.gpsLatitude==null ?  null : Math.sin(household.gpsLatitude*Math.PI / 180.0)
                    household.cosLongitude = household.gpsLongitude==null ? null : Math.cos(household.gpsLongitude*Math.PI / 180.0)
                    household.sinLongitude = household.gpsLongitude==null ? null : Math.sin(household.gpsLongitude*Math.PI / 180.0)


                    if (mapHeads.containsKey(obj[0] + "00")){
                        def objhead = mapHeads.get(obj[0] + "00")
                        household.headCode = objhead[0]
                        household.headName = StringUtil.getFullname(objhead[1], objhead[2], objhead[3])
                    }

                    boolean result = household.save(flush: true)

                    //println "${processed} result ${result} - ${TimeCategory.minus(new Date(), start)}"

                    if (!result){
                        errors++
                        println "errors: ${household.errors}"
                        log.output.println "${household.code}/${household.name}: errors: ${household.errors}"
                    }
                }

                println("${processed}/${max} reached clearing session - ${TimeCategory.minus(new Date(), start)}")
                sessionFactory.currentSession.flush() //clearing cache save us a lot of time
                sessionFactory.currentSession.clear()

            }



        }


        println("finished creating/updating households!! - ${TimeCategory.minus(new Date(), start)}")


        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(LogStatus.FINISHED)
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        output.close()
    }

    def importIndividuals(long logReportId){
        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "db-members-from-openhds");
        PrintStream output = log.output

        if (output == null) return;

        int processed = 0
        int errors = 0
        def start = new Date()

        println "starting copy of members - ${start}"
        println "reading existing members - ${TimeCategory.minus(new Date(), start)}"


        /* Import Functions here */

        def List<Household>houses = []
        def members = []
        def membersExtIds = [:]
        def membersFirstHhs = [:]
        def memberSpouses = [:]
        def memberDeaths = [:]



        Household.withNewTransaction {
            println "reading households"
            houses = Household.list()

            println "deleting members"
            Member.executeUpdate("delete from Member")
        }

        Individual.withTransaction {
            println "reading members"            //     0              1           2           3           4              5          6       7           8                 9                   10                11                 12               13                   14                   15              16           17          18          19
            members = Individual.executeQuery("select i.uuid, r.location.extId, i.extId, i.firstName, i.middleName,  i.lastName, i.gender, i.dob, i.father.extId, i.father.firstName, i.father.middleName, i.father.lastName, i.mother.extId, i.mother.firstName, i.mother.middleName, i.mother.lastName, r.startType, r.startDate, r.endType, r.endDate from Individual i, Residency r where i.uuid=r.individual.uuid and r.startDate=(select max(r2.startDate) from Residency r2 where r2.individual.uuid=r.individual.uuid )")

            println "reading members first house"      //    0        1            2              3            4
            def memberx = Individual.executeQuery("select i.uuid, i.extId, r.location.extId, r.startType, r.startDate from Individual i, Residency r where i.uuid=r.individual.uuid and r.startDate=(select min(r2.startDate) from Residency r2 where r2.individual.uuid=r.individual.uuid )")

            println "reading relationships"                          //        0                   1                     2                        3                         4                5          6
            def relationshipsAb = Relationship.executeQuery("select r.individualA.extId, r.individualB.extId, r.individualB.firstName, r.individualB.middleName, r.individualB.lastName, r.aisToB, r.startDate from Relationship r, Individual i where r.individualA.uuid=i.uuid and r.startDate=(select max(r2.startDate) from Relationship r2 where r2.individualA.uuid=r.individualA.uuid)")
            def relationshipsBa = Relationship.executeQuery("select r.individualB.extId, r.individualA.extId, r.individualA.firstName, r.individualA.middleName, r.individualA.lastName, r.aisToB, r.startDate from Relationship r, Individual i where r.individualB.uuid=i.uuid and r.startDate=(select max(r2.startDate) from Relationship r2 where r2.individualB.uuid=r.individualB.uuid)")


            println "pulling extId->firstName"
            membersExtIds = members.collectEntries{ [(it[2]) : it[3]]}

            println "pulling list of member first house"
            membersFirstHhs = memberx.collectEntries{ [ (it[1]) : [it[2],it[3],it[4]] ]}

            println "pulling list of deaths dates"
            memberDeaths = Death.executeQuery("select d.individual.extId, d.deathDate from Death d").collectEntries{ [(it[0]): (it[1]) ]  }

            //read relationships
            println "pulling list of relationships A->B: ${relationshipsAb.size()}"
            relationshipsAb.each { r ->
                //println "${r[0]}, ${r[1]}, ${r[3]}, ${r[2]}"

                memberSpouses.put(r[0], [r[1], r[2], r[3], r[4], r[5]])
            }

            println "pulling list of relationships  B->A: ${relationshipsBa.size()}"
            relationshipsBa.each { r ->
                //println "${r[0]}, ${r[1]}, ${r[3]}, ${r[2]}"

                memberSpouses.put(r[0], [r[1], r[2], r[3], r[4], r[5]])
            }

            relationshipsAb.clear()
            relationshipsBa.clear()
            memberx.clear()
        }

        println  "houses to process ${houses.size()}"
        println  "members found ${members.size()}"

        println "spliting list"
        //createMember(members, houses, membersExtIds, membersFirstHhs, memberSpouses, memberDeaths, 1, log)

        def listMembers = GeneralUtil.splitList(members, 6)

        def p1 = task { createMember(listMembers[0], houses, membersExtIds, membersFirstHhs, memberSpouses, memberDeaths, 1, log) }
        def p2 = task { createMember(listMembers[1], houses, membersExtIds, membersFirstHhs, memberSpouses, memberDeaths, 2, log) }
        def p3 = task { createMember(listMembers[2], houses, membersExtIds, membersFirstHhs, memberSpouses, memberDeaths, 3, log) }
        def p4 = task { createMember(listMembers[3], houses, membersExtIds, membersFirstHhs, memberSpouses, memberDeaths, 4, log) }
        def p5 = task { createMember(listMembers[4], houses, membersExtIds, membersFirstHhs, memberSpouses, memberDeaths, 5, log) }
        def p6 = task { createMember(listMembers[5], houses, membersExtIds, membersFirstHhs, memberSpouses, memberDeaths, 6, log) }

        println "executing multithread"
        waitAll(p1, p2, p3, p4, p5, p6)


        println("finished creating/updating members!! - ${TimeCategory.minus(new Date(), start)}")


        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(LogStatus.FINISHED)
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        output.close()
    }

    def createMember(def members, def List<Household> houses, Map membersExtIds, Map membersFirstHouses, Map memberSpouses, Map memberDeaths, int threadNumber, LogOutput log){
        def processed = 0
        def errors = 0

        def start = new Date()
        int from = 0;
        int to = 0;
        int max = 500
        while (processed < members.size()) {
            from = to
            to = (members.size() > to + max) ? (to + max) : members.size();
            //reading only some records to speed up the process

            Member.withNewTransaction {
                members.subList(from, to).each { mem ->
                    processed++

                    Household household = houses.find { it.code == mem[1]}

                    def member = new Member()

                    member.code = mem[2]
                    member.name = StringUtil.getFullname(mem[3], mem[4], mem[5]) //3, 4, 5
                    member.gender = mem[6]
                    member.dob = mem[7]
                    member.age = GeneralUtil.getAge(member.dob)
                    //ageAtDeath

                    member.fatherCode = mem[8]
                    member.fatherName = StringUtil.getFullname(mem[9], mem[10], mem[11])  // 9, 10, 11
                    member.motherCode = mem[12]
                    member.motherName = StringUtil.getFullname(mem[13], mem[14], mem[15]) // 13, 14, 15


                    member.startType = mem[16]==null ? "" : mem[16]
                    member.startDate = mem[17] //StringUtil.format(mem[17], "yyyy-MM-dd")
                    member.endType =   mem[18]==null ? "" : mem[18]
                    member.endDate =   mem[19] //StringUtil.format(mem[19], "yyyy-MM-dd")

                    if (member.endType == "DTH"){
                        def dthDate = memberDeaths.get(member.code)
                        if (dthDate != null){
                            member.ageAtDeath = GeneralUtil.getYearsDiff(member.dob, dthDate)
                        }
                    }

                    if (household != null) {
                        member.householdCode = household.code
                        member.householdName = household.name
                        member.gpsNull = household.gpsNull
                        member.gpsAccuracy = household.gpsAccuracy
                        member.gpsAltitude = household.gpsAltitude
                        member.gpsLatitude = household.gpsLatitude
                        member.gpsLongitude = household.gpsLongitude

                        member.cosLatitude = household.cosLatitude
                        member.sinLatitude = household.sinLatitude
                        member.cosLongitude = household.cosLongitude
                        member.sinLongitude = household.sinLongitude

                        if (household.headCode.equals(member.code)){
                            member.isHouseholdHead = true
                        }
                    }

                    if (memberSpouses.containsKey(member.code)){
                        def obj = memberSpouses.get(member.code)

                        member.spouseCode = obj[0] //extId
                        member.spouseName = StringUtil.getFullname(obj[1], obj[2], obj[3])  //1,2,3 - name
                        member.spouseType = getRelationshipType(obj[4]) // spouse type
                    }

                    if (membersFirstHouses.containsKey(member.code)){
                        def obj = membersFirstHouses.get(member.code)
                        member.entryHousehold = obj[0]
                        member.entryType = obj[1]
                        member.entryDate = obj[2]
                    }

                    if (!member.save()){
                        errors++
                        println "${member.code}/${member.name}, errors: ${member.errors}"
                        log.output.println "${member.code}/${member.name}: errors: ${member.errors}"
                    }
                }

                //session.flush()
                //session.clear()

                println("thread-${threadNumber}: ${processed}/${max} reached clearing session - ${TimeCategory.minus(new Date(), start)}")
                Member.withSession {
                    sessionFactory.currentSession.flush() //clearing cache save us a lot of time
                    sessionFactory.currentSession.clear()
                }

            }


        }


    }

    String getRelationshipType(String openhdsRelationshipType){

        if (openhdsRelationshipType=="2") return "MAR"; // = Married            = 2
        if (openhdsRelationshipType=="3") return "SEP"; // = Separated/Divorced = 3
        if (openhdsRelationshipType=="4") return "WID"; // = Widowed            = 4
        if (openhdsRelationshipType=="5") return "LIV"; // = Living Together    = 5

        return "NA"
    }

    HierarchyRegion getHierarchies(String lastHiearchyUuid){
        def hierarchy = new HierarchyRegion()

        Locationhierarchy.withTransaction {
            def lh = Locationhierarchy.findByUuid(lastHiearchyUuid)

            if (lh != null){
                hierarchy.region = lh.extId

                def parent = lh
                while (parent != null){

                    def level = parent.level

                    if (level != null){
                        //println "${parent.extId}, parent=${parent.parent}, ${level}"
                        hierarchy."hierarchy${level.keyIdentifier}" = parent.extId
                    }

                    parent = parent.parent // get the previous hierarchy level
                }
            }
        }

        return hierarchy
    }

    String getDoubleStringOrNull(String str){
        return (str != null) && StringUtil.isDouble(str) ? str : null;
    }

    Double getDoubleValue(String str){
        str = getDoubleStringOrNull(str)
        return str==null ? null : Double.parseDouble(str)
    }

    def String getHierarchyLevel(String openhdsLevelId){
        switch (openhdsLevelId){
            case "hierarchyLevelId1" : return "hierarchy1"
            case "hierarchyLevelId2" : return "hierarchy2"
            case "hierarchyLevelId3" : return "hierarchy3"
            case "hierarchyLevelId4" : return "hierarchy4"
            case "hierarchyLevelId5" : return "hierarchy5"
            case "hierarchyLevelId6" : return "hierarchy6"
            case "hierarchyLevelId7" : return "hierarchy7"
            case "hierarchyLevelId8" : return "hierarchy8"
        }

        return "not_supported"
    }

    public class HierarchyRegion {
        String region
        String hierarchy1
        String hierarchy2
        String hierarchy3
        String hierarchy4
        String hierarchy5
        String hierarchy6
        String hierarchy7
        String hierarchy8
    }
}
