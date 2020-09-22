package org.philimone.hds.explorer.services

import net.betainteractive.io.LogOutput
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.odk.model.CensusHouseholdCore
import org.philimone.hds.explorer.odk.model.CensusMemberCore
import org.philimone.hds.explorer.openhds.model.Round
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.openhds.http.RestApiAccess

import org.philimone.hds.explorer.server.openhds.xml.CensusData
import org.philimone.hds.explorer.server.openhds.xml.model.Baseline
import org.philimone.hds.explorer.server.openhds.xml.model.Individual
import org.philimone.hds.explorer.server.openhds.xml.model.Location
import org.philimone.hds.explorer.server.openhds.xml.model.Membership
import org.philimone.hds.explorer.server.openhds.xml.model.Relationship
import org.philimone.hds.explorer.server.openhds.xml.model.Residency
import org.philimone.hds.explorer.server.openhds.xml.model.SocialGroup
import org.philimone.hds.explorer.server.openhds.xml.model.Visit

class DssSynchronizationService {

    static datasource = ['odk', 'openhds']

    static transactional = false

    def generalUtilitiesService

    def baseUrl = ""

    def readCensusDataAndExecute(long logReportId) {
        List<CensusData> locations = []
        List<CensusData> visits = []
        List<CensusData> individuals = []
        List<CensusData> socialgroups = []
        List<CensusData> memberships = []
        List<CensusData> relationships = []


        println "updating census household and member"
        CensusHouseholdCore.withTransaction {
            CensusHouseholdCore.executeUpdate("update CensusHouseholdCore set locationProcessed=0 where locationProcessed is null");
            CensusHouseholdCore.executeUpdate("update CensusHouseholdCore set socialgroupProcessed=0 where socialgroupProcessed is null");

            CensusMemberCore.executeUpdate("update CensusMemberCore set individualProcessed=0 where individualProcessed is null");
            CensusMemberCore.executeUpdate("update CensusMemberCore set residencyProcessed=0 where residencyProcessed is null");
            CensusMemberCore.executeUpdate("update CensusMemberCore set membershipProcessed=0 where membershipProcessed is null");
            CensusMemberCore.executeUpdate("update CensusMemberCore set relationshipProcessed=0 where relationshipProcessed is null");
        }


        println "reading all census household and member"
        CensusHouseholdCore.withTransaction {

            def households = CensusHouseholdCore.executeQuery("select h from CensusHouseholdCore h where locationProcessed=0 or socialgroupProcessed=0 order by h.householdId")
            def members = CensusMemberCore.executeQuery("select m from CensusMemberCore m where individualProcessed=0 or residencyProcessed=0 or membershipProcessed=0 or relationshipProcessed=0 order by m.code")

            households.each { core ->
                Location location = getLocation(core)
                Visit visit = getVisit(core)
                SocialGroup socialgroup = getSocialgroup(core)

                if (core.locationProcessed==0){
                    locations.add(new CensusData(key: location.extId, locationXml: location, household: core))
                }
                if (core.locationProcessed==0) {
                    visits.add(new CensusData(key: visit.extId, visitXml: visit, household: core))
                }
                if (core.socialgroupProcessed==0){
                    socialgroups.add(new CensusData(key: socialgroup.extId, socialGroupXml: socialgroup, household: core))
                }
            }

            members.each { core ->
                Baseline baseline = getBaseline(core)
                Membership membership = getMembership(core)
                Relationship relationship = getRelationship(core)

                if (core.individualProcessed==0){
                    individuals.add(new CensusData(key: baseline.extId, baselineXml: baseline, member: core))
                }
                if (core.membershipProcessed==0){
                    memberships.add(new CensusData(key: membership.individual, membershipXml: membership, member: core))
                }

                if (relationship != null && core.relationshipProcessed==0){
                    relationships.add(new CensusData(key: relationship.individualA, relationshipXml: relationship, member: core))
                }
            }

            individuals = reorderByLessDependents(individuals)
        }

        //from Household we get Visit, Location and Socialgroup
        //from Member    we get Baseline, Membership, Relationship

        /*
        1 - Location
        2 - Visit
        3 - Baseline - order by father/mother dependencies
        4 - Socialgroup
        5 - Membership
        6 - Relationship
        */

        println "data to execute"
        //locations.each { println "location: ${it.key}, ${it.locationXml.name}" }
        //visits.each { println "visit: ${it.visitXml.extId}" }
        //individuals.each { println "individual: ${it.key}, ${it.baselineXml.lastName}, father: ${it.baselineXml.father}, mother: ${it.baselineXml.mother}" }
        //socialgroups.each { println "socialgroup: ${it.key}, ${it.socialGroupXml.groupName}" }
        //memberships.each { println "membership: ${it.key}, ${it.membershipXml.startDate}" }

        println "locations: ${locations.size()}"
        println "visits: ${visits.size()}"
        println "individuals: ${individuals.size()}"
        println "socialgroups: ${socialgroups.size()}"
        println "memberships: ${memberships.size()}"
        println "relationships: ${relationships.size()}"

        baseUrl  = generalUtilitiesService.getConfigValue("hds.explorer.openhds_web.url")+"/api/rest/"
        def user = generalUtilitiesService.getConfigValue("hds.explorer.openhds_web.username")
        def pass = generalUtilitiesService.getConfigValue("hds.explorer.openhds_web.password")


        println baseUrl
        //println user
        //println pass

        def webApi = new RestApiAccess(user, pass)

        syncLocations(logReportId, webApi, locations)
        syncVisits(logReportId, webApi, visits)
        syncIndividuals(logReportId, webApi, individuals)
        syncSocialgroups(logReportId, webApi, socialgroups)
        syncMemberships(logReportId, webApi, memberships)
        syncRelationships(logReportId, webApi, relationships)

    }

    def syncLocations(long logReportId, RestApiAccess webApi, List<CensusData> locations){

        LogOutput log = null
        def processed = 0
        def errors = 0
        def start = new Date()
        def logStatusValue = LogStatus.FINISHED

        try {
            log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "census-locations");
            def output = log.output

            if (output == null) throw new Exception("Output Log file is inaccessible, log.output is null")

            locations.each { cData ->
                processed++

                def xml = cData.locationXml
                def obj = cData.household


                //println "xml\n${xml.getXml()}"

                RestApiAccess.Response result = webApi.send(baseUrl+"locations", xml)

                //println "status: "+result.getStatus()
                //println("response: "+result.bodyText)

                def error_msg = "Location: with extId=${xml.extId}, retrieved from CENSUS_HOUSEHOLD_CORE with _URI=${obj.uri} could not be saved!"

                if (result.hasErrors()){
                    errors++

                    def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                    println msg
                    output.println msg

                    //flag the odk record with an error - we will not flag unexpected errors
                    CensusHouseholdCore.withTransaction {
                        CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.locationProcessed=2 where h.uri=?", [obj.uri])
                    }

                } else {

                    if (result.code >= HttpURLConnection.HTTP_CREATED && result.code < HttpURLConnection.HTTP_BAD_REQUEST){ //Did Execute Well
                        //No need for messages
                        //println "location sucessfully executed: ${xml.getExtId()}"
                        //flag the odk record
                        CensusHouseholdCore.withTransaction {
                            CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.locationProcessed=1 where h.uri=?", [obj.uri])
                        }

                    } else {
                        errors++

                        def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                        println msg
                        output.println msg

                        //flag the odk record
                        CensusHouseholdCore.withTransaction {
                            CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.locationProcessed=2 where h.uri=?", [obj.uri])
                        }
                    }
                }

            }
        } catch (Exception ex){
            println "catching an error:"
            ex.printStackTrace()

            if (log.output != null){
                log.output.println("Error:\n")
                ex.printStackTrace(log.output)
            }

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        log.output.close()
    }

    def syncVisits(long logReportId, RestApiAccess webApi, List<CensusData> visits){

        LogOutput log = null
        def processed = 0
        def errors = 0
        def start = new Date()
        def logStatusValue = LogStatus.FINISHED


        try {
            log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "census-visits");
            def output = log.output

            if (output == null) throw new Exception("Output Log file is inaccessible, log.output is null")

            visits.each { cData ->
                processed++

                def xml = cData.visitXml
                def obj = cData.household

                RestApiAccess.Response result = webApi.send(baseUrl+"visits", xml)

                //println "status: "+result.getStatus()
                //println("response: "+result.bodyText)

                def error_msg = "Visit: with extId=${xml.extId}, retrieved from CENSUS_HOUSEHOLD_CORE with _URI=${obj.uri} could not be saved!"

                if (result.hasErrors()){
                    errors++

                    def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                    println msg
                    output.println msg

                    //flag the odk record with an error - visits dont need it

                    //CensusHouseholdCore.withTransaction {
                    //    CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.visitProcessed=2 where h.uri=?", [obj.uri])
                    //}
                } else {

                    if (result.code >= HttpURLConnection.HTTP_CREATED && result.code < HttpURLConnection.HTTP_BAD_REQUEST){ //Did Execute Well
                        //No need for messages
                        println "visit sucessfully executed: ${xml.getExtId()}"
                        //flag the odk record

                        //CensusHouseholdCore.withTransaction {
                        //    CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.visitProcessed=1 where h.uri=?", [obj.uri])
                        //}
                    } else {
                        errors++

                        def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                        println msg
                        output.println msg

                        //def msg = "Expected Error -> (Visit Id = ${xml.extId})\nDetails: ${result.getBodyText()}"
                        //println msg
                        //output.println msg

                        //flag the odk record

                        //CensusHouseholdCore.withTransaction {
                        //    CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.visitProcessed=2 where h.uri=?", [obj.uri])
                        //}
                    }
                }

            }
        }catch (Exception ex){
            println "catching an error:"
            ex.printStackTrace()

            if (log.output != null){
                log.output.println("Error:\n")
                ex.printStackTrace(log.output)
            }

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        log.output.close()
    }

    def syncIndividuals(long logReportId, RestApiAccess webApi, List<CensusData> individuals){

        LogOutput log = null
        def processed = 0
        def errors = 0
        def start = new Date()
        def logStatusValue = LogStatus.FINISHED

        try {
            log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "census-baseline_individuals");
            def output = log.output

            if (output == null) throw new Exception("Output Log file is inaccessible, log.output is null")

            individuals.each { cData ->
                processed++

                def xml = cData.baselineXml
                def obj = cData.member

                RestApiAccess.Response result = webApi.send(baseUrl+"baseline", xml)

                //println "status: "+result.getStatus()
                //println("response: "+result.bodyText)

                def error_msg = "Individual/Baseline: with extId=${xml.extId}, visitId=${xml.visitId} retrieved from CENSUS_MEMBER_CORE with _URI=${obj.uri} could not be saved!"

                if (result.hasErrors()){
                    errors++

                    def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n" //${xml.getXml()}\n"
                    println msg
                    output.println msg

                    //flag the odk record with an error - we will not flag unexpected errors
                    CensusMemberCore.withTransaction {
                        CensusMemberCore.executeUpdate("update CensusMemberCore h set h.individualProcessed=2 where h.uri=?", [obj.uri])
                    }

                } else {

                    if (result.code >= HttpURLConnection.HTTP_CREATED && result.code < HttpURLConnection.HTTP_BAD_REQUEST){ //Did Execute Well
                        //No need for messages
                        println "baseline sucessfully executed: ${xml.extId}"
                        //flag the odk record
                        CensusMemberCore.withTransaction {
                            CensusMemberCore.executeUpdate("update CensusMemberCore h set h.individualProcessed=1 where h.uri=?", [obj.uri])
                        }

                    } else {
                        errors++

                        def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n" //${xml.getXml()}\n"
                        println msg
                        output.println msg

                        //flag the odk record
                        CensusMemberCore.withTransaction {
                            CensusMemberCore.executeUpdate("update CensusMemberCore h set h.individualProcessed=2 where h.uri=?", [obj.uri])
                        }
                    }
                }

            }
        } catch (Exception ex){
            println "catching an error:"
            ex.printStackTrace()

            if (log.output != null){
                log.output.println("Error:\n")
                ex.printStackTrace(log.output)
            }

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        log.output.close()
    }

    def syncSocialgroups(long logReportId, RestApiAccess webApi, List<CensusData> socialgroups){

        LogOutput log = null
        def processed = 0
        def errors = 0
        def start = new Date()
        def logStatusValue = LogStatus.FINISHED

        try {
            log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "census-socialgroups");
            def output = log.output

            if (output == null) throw new Exception("Output Log file is inaccessible, log.output is null")

            socialgroups.each { cData ->
                processed++

                def xml = cData.socialGroupXml
                def obj = cData.household

                RestApiAccess.Response result = webApi.send(baseUrl+"socialgroups", xml)

                //println "status: "+result.getStatus()
                //println("response: "+result.bodyText)

                def error_msg = "Socialgroup: with extId=${xml.extId}, groupHead=${xml.groupHead} retrieved from CENSUS_HOUSEHOLD_CORE with _URI=${obj.uri} could not be saved!"

                if (result.hasErrors()){
                    errors++

                    def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                    println msg
                    output.println msg

                    //flag the odk record with an error - we will not flag unexpected errors
                    CensusHouseholdCore.withTransaction {
                        CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.socialgroupProcessed=2 where h.uri=?", [obj.uri])
                    }

                } else {

                    if (result.code >= HttpURLConnection.HTTP_CREATED && result.code < HttpURLConnection.HTTP_BAD_REQUEST){ //Did Execute Well
                        //No need for messages
                        println "socialgroup sucessfully executed: ${xml.extId}"
                        //flag the odk record
                        CensusHouseholdCore.withTransaction {
                            CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.socialgroupProcessed=1 where h.uri=?", [obj.uri])
                        }

                    } else {
                        errors++

                        def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                        println msg
                        output.println msg

                        //flag the odk record
                        CensusHouseholdCore.withTransaction {
                            CensusHouseholdCore.executeUpdate("update CensusHouseholdCore h set h.socialgroupProcessed=2 where h.uri=?", [obj.uri])
                        }
                    }
                }

            }
        } catch (Exception ex){
            println "catching an error:"
            ex.printStackTrace()

            if (log.output != null){
                log.output.println("Error:\n")
                ex.printStackTrace(log.output)
            }

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        log.output.close()
    }

    def syncMemberships(long logReportId, RestApiAccess webApi, List<CensusData> memberships){

        LogOutput log = null
        def processed = 0
        def errors = 0
        def start = new Date()
        def logStatusValue = LogStatus.FINISHED

        try {
            log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "census-memberships");
            def output = log.output

            if (output == null) throw new Exception("Output Log file is inaccessible, log.output is null")

            memberships.each { cData ->
                processed++

                def xml = cData.membershipXml
                def obj = cData.member

                RestApiAccess.Response result = webApi.send(baseUrl+"memberships", xml)

                //println "status: "+result.getStatus()
                //println("response: "+result.bodyText)

                def error_msg = "Membership: with Individual.extId=${xml.individual} and Socialgroup.extId=${xml.socialGroup}, retrieved from CENSUS_MEMBER_CORE with _URI=${obj.uri} could not be saved!"

                if (result.hasErrors()){
                    errors++

                    def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                    println msg
                    output.println msg

                    //flag the odk record with an error - we will not flag unexpected errors
                    CensusMemberCore.withTransaction {
                        CensusMemberCore.executeUpdate("update CensusMemberCore h set h.membershipProcessed=2 where h.uri=?", [obj.uri])
                    }

                } else {

                    if (result.code >= HttpURLConnection.HTTP_CREATED && result.code < HttpURLConnection.HTTP_BAD_REQUEST){ //Did Execute Well
                        //No need for messages
                        println "membership sucessfully executed: Individual.extId=${xml.individual} and Socialgroup.extId=${xml.socialGroup}"
                        //flag the odk record
                        CensusMemberCore.withTransaction {
                            CensusMemberCore.executeUpdate("update CensusMemberCore h set h.membershipProcessed=1 where h.uri=?", [obj.uri])
                        }

                    } else {
                        errors++

                        def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                        println msg
                        output.println msg

                        //flag the odk record
                        CensusMemberCore.withTransaction {
                            CensusMemberCore.executeUpdate("update CensusMemberCore h set h.membershipProcessed=2 where h.uri=?", [obj.uri])
                        }
                    }
                }

            }
        } catch (Exception ex){
            println "catching an error:"
            ex.printStackTrace()

            if (log.output != null){
                log.output.println("Error:\n")
                ex.printStackTrace(log.output)
            }

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        log.output.close()
    }

    def syncRelationships(long logReportId, RestApiAccess webApi, List<CensusData> relationships){

        LogOutput log = null
        def processed = 0
        def errors = 0
        def start = new Date()
        def logStatusValue = LogStatus.FINISHED

        try {
            log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "census-relationships");
            def output = log.output

            if (output == null) throw new Exception("Output Log file is inaccessible, log.output is null")

            relationships.each { cData ->
                processed++

                def xml = cData.relationshipXml
                def obj = cData.member

                RestApiAccess.Response result = webApi.send(baseUrl+"relationships", xml)

                //println "status: "+result.getStatus()
                //println("response: "+result.bodyText)

                def error_msg = "Relationship: with Individual_A.extId=${xml.individualA} and Individual_B.extId=${xml.individualB}, retrieved from CENSUS_MEMBER_CORE with _URI=${obj.uri} could not be saved!"

                if (result.hasErrors()){
                    errors++

                    def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                    println msg
                    output.println msg

                    //flag the odk record with an error - we will not flag unexpected errors
                    CensusMemberCore.withTransaction {
                        CensusMemberCore.executeUpdate("update CensusMemberCore h set h.relationshipProcessed=2 where h.uri=?", [obj.uri])
                    }

                } else {

                    if (result.code >= HttpURLConnection.HTTP_CREATED && result.code < HttpURLConnection.HTTP_BAD_REQUEST){ //Did Execute Well
                        //No need for messages
                        println "relationship sucessfully executed"
                        //flag the odk record
                        CensusMemberCore.withTransaction {
                            CensusMemberCore.executeUpdate("update CensusMemberCore h set h.relationshipProcessed=1 where h.uri=?", [obj.uri])
                        }

                    } else {
                        errors++

                        def msg = "${error_msg}\nError Details:\n${result.getBodyText()}\n"
                        println msg
                        output.println msg

                        //flag the odk record
                        CensusMemberCore.withTransaction {
                            CensusMemberCore.executeUpdate("update CensusMemberCore h set h.relationshipProcessed=2 where h.uri=?", [obj.uri])
                        }
                    }
                }

            }
        } catch (Exception ex){
            println "catching an error:"
            ex.printStackTrace()

            if (log.output != null){
                log.output.println("Error:\n")
                ex.printStackTrace(log.output)
            }

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }

        log.output.close()

    }

    List<CensusData> reorderByLessDependents(List<CensusData> individuals) {

        List<CensusData> newList = (List<CensusData>) individuals.clone()
        List<String> listExtIds = []

        org.philimone.hds.explorer.openhds.model.Individual.withTransaction {
            listExtIds = org.philimone.hds.explorer.openhds.model.Individual.executeQuery("select extId from Individual")
        }

        individuals.each { censusData ->

            def child_index = newList.indexOf(censusData)
            def new_index = -1
            def father_index = -1
            def mother_index = -1


            //check existence of father and mother
            def individual = censusData.baselineXml
            def mother = individual.mother
            def father = individual.father

            father_index = newList.indexOf(CensusData.createData(father));

            if (father_index != -1 && father_index > child_index) { //has dependency on father - father should be before the individual
                def obj = newList.remove(father_index) //remove father
                newList.add(child_index, obj)
            }

            mother_index = newList.indexOf(CensusData.createData(mother));
            child_index = newList.indexOf(censusData)

            if (mother_index != -1 && mother_index > child_index) { //has dependency on mother - mother should be before the individual
                def obj = newList.remove(mother_index) //remove mother
                newList.add(child_index, obj)
            }

        }

        return newList
    }

    Location getLocation(CensusHouseholdCore household){
        Location obj = new Location()

        obj.collectedBy = household.fieldWorkerId;
        obj.extId = household.householdId;
        obj.name = household.householdName;
        obj.type = "RUR"
        obj.accuracy = household.gpsAcc.toString()
        obj.altitude = household.gpsAlt.toString()
        obj.latitude = household.gpsLat.toString()
        obj.longitude = household.gpsLng.toString()
        obj.locationLevel = household.regionId

        return obj;
    }

    SocialGroup getSocialgroup(CensusHouseholdCore household){
        SocialGroup obj = new SocialGroup()

        obj.collectedBy = household.fieldWorkerId
        obj.extId = household.householdId + "00"
        obj.groupName = household.householdName
        obj.groupHead = household.headCode
        obj.groupType = "FAM"

        return obj;
    }

    Visit getVisit(CensusHouseholdCore household){
        def suffix = String.format("%03d", getCurrentRoundNumber())

        Visit obj = new Visit()

        obj.extId = household.householdId + "001" //"00" + suffix
        obj.location = household.householdId
        obj.date = household.visitDate
        obj.round = getCurrentRoundNumber()
        obj.collectedBy = household.fieldWorkerId

        return obj
    }

    Visit getVisit(CensusHouseholdCore household, int roundNumber){
        Visit obj = new Visit()

        obj.extId = household.householdId + "001"
        obj.location = household.householdId
        obj.date = household.visitDate
        obj.round = roundNumber+""
        obj.collectedBy = household.fieldWorkerId

        return obj
    }

    Baseline getBaseline(CensusMemberCore member){
        def suffix = String.format("%03d", getCurrentRoundNumber())

        Baseline obj = new Baseline()

        obj.collectedBy = member.fieldWorkerId
        obj.visitId = member.householdId + "001" //"00" + suffix

        def name = StringUtil.splitName(member.name)

        obj.extId = member.code
        obj.firstName = name.firstName
        obj.middleName = ""
        obj.lastName = name.lastName.isEmpty() ? "NULL" : name.lastName
        obj.gender = member.gender
        obj.dob = StringUtil.format(member.dob, "yyyy-MM-dd")
        obj.mother = member.motherId
        obj.father = member.fatherId

        obj.origin = "BASELINE"
        obj.reason = "BASELINE"
        obj.recordedDate = member.residencyStartDate
        obj.migType = "BASELINE"

        return obj
    }

    Individual getIndividual(CensusMemberCore member){
        Individual obj = new Individual()

        def name = StringUtil.splitName(member.name)

        obj.collectedBy = member.fieldWorkerId
        obj.extId = member.code
        obj.firstName = name.firstName
        obj.middleName = ""
        obj.lastName = name.lastName.isEmpty() ? "NULL" : name.lastName
        obj.gender = member.gender
        obj.dob = StringUtil.format(member.dob, "yyyy-MM-dd")
        obj.mother = member.motherId
        obj.father = member.fatherId
        //obj.endType = member.

        return obj
    }

    Residency getResidency(CensusMemberCore member){
        Residency obj = new Residency()

        obj.collectedBy = member.fieldWorkerId
        obj.startDate = member.residencyStartDate
        obj.startType = "ENU"
        obj.endDate = ""
        obj.endType = "NA"
        obj.individual = member.code
        obj.location = member.householdId

        return obj
    }

    Membership getMembership(CensusMemberCore member){
        Membership obj = new Membership()

        obj.collectedBy = member.fieldWorkerId
        obj.individual = member.code
        obj.socialGroup = member.householdId + "00"
        obj.startDate = member.residencyStartDate
        obj.startType = "ENU"
        obj.endDate = ""
        obj.endType = "NA"
        obj.bIsToA = getRelationshipWithHead(member)

        return obj
    }

    Relationship getRelationship(CensusMemberCore member){

        if (member.spouseType == null || member.spouseType=="1"){ //solteiro
            return null;
        }

        Relationship obj = new Relationship()

        obj.collectedBy = member.fieldWorkerId
        obj.individualA = member.code
        obj.individualB = member.spouseId
        obj.aIsToB = member.spouseType     //the type of relationship
        obj.startDate = member.spouseDate  //we need a question asking the date this relationship started


        return obj
    }

    String getRelationshipWithHead(CensusMemberCore censusMemberCore) {
        def relation = censusMemberCore.isHouseholdHead.equals("1") ? "1" : censusMemberCore.relationshipWithHead
        return relation
    }

    int getCurrentRoundNumber(){
        int round = 0;

        Round.withTransaction {
            def result = Round.executeQuery("select max(r.roundNumber) from Round r")
            round = result.first()
        }

        return round
    }
}
