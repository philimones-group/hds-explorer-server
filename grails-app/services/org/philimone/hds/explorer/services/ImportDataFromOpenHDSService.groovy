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
import org.philimone.hds.explorer.openhds.model.Relationship
import org.philimone.hds.explorer.openhds.model.Socialgroup
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.logs.LogStatus
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.StudyModule
import static grails.async.Promises.task
import static grails.async.Promises.waitAll

@Transactional
class ImportDataFromOpenHDSService {
    static datasource = ['openhds']

    def generalUtilitiesService
    def sessionFactory

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


                    if (user.modules == null || user.modules.size()==0){
                        println "adding module dss for user=${user.username}, ${user.modules}"
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
            }

            println("${max} reached clearing session - ${TimeCategory.minus(new Date(), start)}")
            sessionFactory.currentSession.flush() //clearing cache save us a lot of time
            sessionFactory.currentSession.clear()
        }

        println("finished creating/updating fieldworkers!! - ${TimeCategory.minus(new Date(), start)}")


        LogReport.withTransaction {
            LogReport logReport = LogReport.get(logReportId)
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
            heads = Socialgroup.executeQuery("select s.extId, s.individual.extId from Socialgroup s")
        }

        println  "houses to process ${houses.size()}"
        println  "heads found ${heads.size()}"

        heads.each {
            mapHeads.put(it[0], it[1]) // save socialgroup id and extid
        }

        Household.withTransaction {
            Household.executeUpdate("delete from Household ")
        }

        int from = 0;
        int to = 0;
        int max = 100

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


                    if (mapHeads.containsKey(obj[1] + "00")){
                        household.headCode = mapHeads.get(obj[1] + "00")
                    }

                    boolean result = household.save(flush: true)

                    //println "${processed} result ${result} - ${TimeCategory.minus(new Date(), start)}"

                    if (!result){
                        errors++
                        println "errors: ${household.errors}"
                        log.output.println "${household.code}/${household.name}: errors: ${household.errors}"
                    }
                }
            }

            println("${processed}/${max} reached clearing session - ${TimeCategory.minus(new Date(), start)}")
            sessionFactory.currentSession.flush() //clearing cache save us a lot of time
            sessionFactory.currentSession.clear()
        }


        println("finished creating/updating households!! - ${TimeCategory.minus(new Date(), start)}")


        LogReport.withTransaction {
            LogReport logReport = LogReport.get(logReportId)
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

    HierarchyRegion getHierarchies(String lastHiearchyUuid){
        def hierarchy = new HierarchyRegion()
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

        return hierarchy
    }

    String getDoubleStringOrNull(String str){
        return (str != null) && StringUtil.isDouble(str) ? str : null;
    }

    Double getDoubleValue(String str){
        str = getDoubleStringOrNull(str)
        return str==null ? null : Double.parseDouble(str)
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
