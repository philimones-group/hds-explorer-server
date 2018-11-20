package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import net.betainteractive.io.LogOutput
import org.philimone.hds.explorer.authentication.Role
import org.philimone.hds.explorer.authentication.User
import org.philimone.hds.explorer.authentication.UserRole
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.openhds.model.Fieldworker
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.logs.LogStatus
import org.philimone.hds.explorer.server.model.main.StudyModule

@Transactional
class ImportDataFromOpenHDSService {
    static datasource = ['openhds']

    def generalUtilitiesService
    def sessionFactory

    def importFieldWorkersFromOpenHDS(long logReportId){
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
}
