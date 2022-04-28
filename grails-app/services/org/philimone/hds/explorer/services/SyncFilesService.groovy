package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.io.LogOutput
import net.betainteractive.io.writers.ZipMaker
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.Dataset
import org.philimone.hds.explorer.server.model.main.Death
import org.philimone.hds.explorer.server.model.main.Form
import org.philimone.hds.explorer.server.model.main.FormMapping
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.InMigration
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.OutMigration
import org.philimone.hds.explorer.server.model.main.PregnancyOutcome
import org.philimone.hds.explorer.server.model.main.PregnancyRegistration
import org.philimone.hds.explorer.server.model.main.RedcapApi
import org.philimone.hds.explorer.server.model.main.RedcapMapping
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Residency
import org.philimone.hds.explorer.server.model.main.Round
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.TrackingList
import org.philimone.hds.explorer.server.model.main.Visit
import org.philimone.hds.explorer.server.model.settings.ApplicationParam
import org.philimone.hds.explorer.server.model.enums.SyncEntity
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *  Responsible for generating XML/Zip Files to be exported - eg. Households, Members, Users...
 */
@Transactional
class SyncFilesService {

    def generalUtilitiesService
    def trackingListService
    def syncFilesReportService
    def moduleService

    def generateSettingsXML(LogReportCode logReportId) {
        generateAppParametersXML(logReportId)
        generateModulesXML(logReportId)
        generateFormsXML(logReportId)
        generateCoreFormsXML(logReportId)
        generateUsersXML(logReportId)
    }

    def generateHouseholdDatasets(LogReportCode logReportId) {
        generateRegionsXML(logReportId)
        generateRoundsXML(logReportId)
        generateHouseHoldsXML(logReportId)
        generateMembersXML(logReportId)
        generateResidenciesXML(logReportId)
    }

    def generateDemographicEvents(LogReportCode logReportId) {
        generateVisitsXML(logReportId)
        generateHeadRelationshipsXML(logReportId)
        generateMaritalRelationshipsXML(logReportId)
        generatePregnancyRegistrationsXML(logReportId)
        generateDeathsXML(logReportId)
        //These events are not needed in the mobile app
        //generateInMigrationsXML(logReportId)
        //generateOutMigrationsXML(logReportId)
        //generatePregnancyOutcomesXML(logReportId)

    }

    def generateDatasetsXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-dataset-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            def resultDatasets = []

            Region.withTransaction {
                resultDatasets = Dataset.findAllByEnabled(true)
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("datasets");
            doc.appendChild(rootElement);


            int count = 0;

            resultDatasets.each { dataSet ->
                count++;
                Element element = createDataSet(doc, dataSet);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + File.separator + "datasets.xml"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved! - datasets.xml");
            output.println("File saved! - datasets.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "datasets.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "datasets.xml")
            def b = zipMaker.makeZip()

            println "creating zip - datasets.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.DATASETS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateTrackingListsXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-tracklist-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            List<TrackingList> resultLists = []

            TrackingList.withTransaction {
                resultLists = TrackingList.findAllByEnabled(true)
            }

            def result = trackingListService.createXMLfrom(resultLists)

            println "creating xml file ${resultLists.size()}"
            PrintStream outputFile = new PrintStream(new FileOutputStream(SystemPath.generatedFilesPath + "/trackinglists.xml"), true)

            outputFile.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
            outputFile.print(result.xml)

            outputFile.close()

            int count = result.listCount

            System.out.println("File saved! - trackinglists.xml");
            output.println("File saved! - trackinglists.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "trackinglists.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "trackinglists.xml")
            def b = zipMaker.makeZip()

            println "creating zip - trackinglists.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.TRACKING_LISTS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateAppParametersXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-app-param-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todos users
            def resultParams = []

            ApplicationParam.withTransaction {
                resultParams = ApplicationParam.list()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("applicationParams");
            doc.appendChild(rootElement);


            int count = 0;

            resultParams.each { param ->
                count++;
                Element element = createAppParameter(doc, param);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + File.separator + "params.xml"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved! - params.xml");
            output.println("File saved! - params.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "params.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "params.xml")
            def b = zipMaker.makeZip()

            println "creating zip - params.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.PARAMETERS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateModulesXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-modules-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read all modules
            def resultModules = []

            Module.withTransaction {
                resultModules = Module.list()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("modules");
            doc.appendChild(rootElement);


            int count = 0;
            resultModules.each { module ->
                count++;
                //members
                Element element = createModule(doc, module);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/modules.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - modules.xml");
            output.println("File saved! - modules.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/modules.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/modules.xml")
            def b = zipMaker.makeZip()

            println "creating zip - modules.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.MODULES, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }


        output.close();

    }

    def generateUsersXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-users-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todos users
            def resultUsers = []

            User.withTransaction {
                resultUsers = User.findAllByEnabled(true)
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            doc.appendChild(rootElement);


            int count = 0;

            resultUsers.each { user ->
                count++;
                Element elementUser = createUser(doc, user);
                rootElement.appendChild(elementUser);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + File.separator + "users.xml"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved! - users.xml");
            output.println("File saved! - users.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "users.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "users.xml")
            def b = zipMaker.makeZip()

            println "creating zip - users.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.USERS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            println "error 2: ${logReport.errors}"
        }

        output.close();

    }

    def generateFormsXML(LogReportCode logReportId) { //read forms

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-forms-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultForms = []

            Form.withTransaction {
                resultForms = Form.findAllByEnabled(true) //get only the enabled forms
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("forms");
            doc.appendChild(rootElement);

            int count = 0;
            resultForms.each { Form form ->
                count++;

                def formMappingList = form.mappings
                def redcapMappingList = form.redcapMappings
                def redcapApi = form.redcapApi

                Element element = createForm(doc, form, formMappingList, redcapApi, redcapMappingList);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/forms.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - forms.xml");
            output.println("File saved! - forms.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/forms.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/forms.xml")
            def b = zipMaker.makeZip()

            println "creating zip - forms.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.FORMS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateCoreFormsXML(LogReportCode logReportId) { //read forms

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-cforms-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultForms = []

            CoreFormExtension.withTransaction {
                resultForms = CoreFormExtension.findAllByEnabled(true) //get only the enabled forms
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("coreformsexts");
            doc.appendChild(rootElement);

            int count = 0;
            resultForms.each { CoreFormExtension form ->
                count++;


                Element element = createCoreFormExt(doc, form);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/${SyncEntity.CORE_FORMS_EXT.xmlFilename}"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - ${SyncEntity.CORE_FORMS_EXT}.xml");
            output.println("File saved! - ${SyncEntity.CORE_FORMS_EXT}.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/${SyncEntity.CORE_FORMS_EXT.zipFilename}")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/${SyncEntity.CORE_FORMS_EXT.xmlFilename}")
            def b = zipMaker.makeZip()

            println "creating zip - ${SyncEntity.CORE_FORMS_EXT.zipFilename} - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.CORE_FORMS_EXT, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateRoundsXML(LogReportCode logReportId) { //read forms

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-rounds-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultRounds = []

            Round.withTransaction {
                resultRounds = Round.findAll()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("rounds");
            doc.appendChild(rootElement);

            int count = 0;
            resultRounds.each { Round round ->
                count++;

                Element element = createRound(doc, round);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/rounds.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - rounds.xml");
            output.println("File saved! - rounds.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/rounds.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/rounds.xml")
            def b = zipMaker.makeZip()

            println "creating zip - rounds.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.ROUNDS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateRegionsXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-regions-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todos users
            def resultRegions = []

            Region.withTransaction {
                resultRegions = Region.list()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("regions");
            doc.appendChild(rootElement);


            int count = 0;

            resultRegions.each { region ->
                count++;
                Element element = createRegion(doc, region);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + File.separator + "regions.xml"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved! - regions.xml");
            output.println("File saved! - regions.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "regions.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "regions.xml")
            def b = zipMaker.makeZip()

            println "creating zip - regions.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.REGIONS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateHouseHoldsXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.logsPath, "/generate-households-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todas dIndividuos
            def households = []

            Household.withTransaction {
                households = Household.executeQuery("select h.id from Household h")
            }

            println "creating xml file ${households.size()}"
            PrintStream outputFile = new PrintStream(new FileOutputStream(SystemPath.generatedFilesPath + "/households.xml"), true)

            outputFile.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><households>")

            int count = 0
            Household.withTransaction {
                households.each { id ->
                    count++
                    def h = Household.get(id)
                    outputFile.print(toXML(h))
                    h = null
                }
            }

            outputFile.print("</households>")
            outputFile.close()

            System.out.println("File saved! - households.xml");
            output.println("File saved! - households.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.generatedFilesPath + "/households.zip")
            zipMaker.addFile(SystemPath.generatedFilesPath + "/households.xml")
            def b = zipMaker.makeZip()

            println "creating zip - households.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.HOUSEHOLDS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateMembersXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.logsPath, "/generate-members-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todas dIndividuos
            println "reading members"
            def members = []
            Member.withTransaction {
                members = Member.executeQuery("select m.id from Member m")
            }

            println "creating xml file ${members.size()}"
            PrintStream outputFile = new PrintStream(new FileOutputStream(SystemPath.generatedFilesPath + "/members.xml"), true)

            outputFile.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><members>")

            int count = 0
            Member.withTransaction {
                members.each { id ->
                    count++

                    def m = Member.get(id)
                    outputFile.print(toXML(m))
                    m = null
                }

                //println "" + (m as XML)
            }
            outputFile.print("</members>")
            outputFile.close()

            //StreamResult result = new StreamResult(new File(SystemPath.DSS_XML_RESOURCES_PATH + "/members.xml"));

            println("File saved! - members.xml");
            output.println("File saved! - members.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.generatedFilesPath + "/members.zip")
            zipMaker.addFile(SystemPath.generatedFilesPath + "/members.xml")
            def b = zipMaker.makeZip()

            println "creating zip - members.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.MEMBERS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            println "" + ex.toString()
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }
        output.close();

    }

    def generateResidenciesXML(LogReportCode logReportId) {
        String filename = SyncEntity.RESIDENCIES.filename
        String xmlFilename = SyncEntity.RESIDENCIES.xmlFilename
        String zipFilename = SyncEntity.RESIDENCIES.zipFilename


        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-${filename}-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todos users
            def resultResidencies = []

            Residency.withTransaction {
                resultResidencies = Residency.list()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("${filename}");
            doc.appendChild(rootElement);


            int count = 0;

            resultResidencies.each { region ->
                count++;
                Element element = createResidency(doc, region);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + File.separator + "${xmlFilename}"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved! - ${xmlFilename}");
            output.println("File saved! - ${xmlFilename}");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "${zipFilename}")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "${xmlFilename}")
            def b = zipMaker.makeZip()

            println "creating zip - ${zipFilename} - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.RESIDENCIES, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateVisitsXML(LogReportCode logReportId) { //read forms

        String filename = SyncEntity.VISITS.filename
        String xmlFilename = SyncEntity.VISITS.xmlFilename
        String zipFilename = SyncEntity.VISITS.zipFilename

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-${filename}-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultVisits = []

            Visit.withTransaction {
                resultVisits = Visit.list()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("${filename}");
            doc.appendChild(rootElement);

            int count = 0;
            resultVisits.each { Visit visit ->
                count++;

                Element element = createVisit(doc, visit);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/${xmlFilename}"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - ${xmlFilename}");
            output.println("File saved! - ${xmlFilename}");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/${zipFilename}")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/${xmlFilename}")
            def b = zipMaker.makeZip()

            println "creating zip - ${zipFilename} - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.VISITS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateHeadRelationshipsXML(LogReportCode logReportId) {
        String filename = SyncEntity.HEAD_RELATIONSHIPS.filename
        String xmlFilename = SyncEntity.HEAD_RELATIONSHIPS.xmlFilename
        String zipFilename = SyncEntity.HEAD_RELATIONSHIPS.zipFilename


        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-${filename}-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todos users
            def resultHeadRelationships = []

            HeadRelationship.withTransaction {
                resultHeadRelationships = HeadRelationship.list()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("${filename}");
            doc.appendChild(rootElement);


            int count = 0;

            resultHeadRelationships.each { region ->
                count++;
                Element element = createHeadRelationship(doc, region);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + File.separator + "${xmlFilename}"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved! - ${xmlFilename}");
            output.println("File saved! - ${xmlFilename}");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "${zipFilename}")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "${xmlFilename}")
            def b = zipMaker.makeZip()

            println "creating zip - ${zipFilename} - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.HEAD_RELATIONSHIPS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateMaritalRelationshipsXML(LogReportCode logReportId) {
        String filename = SyncEntity.MARITAL_RELATIONSHIPS.filename
        String xmlFilename = SyncEntity.MARITAL_RELATIONSHIPS.xmlFilename
        String zipFilename = SyncEntity.MARITAL_RELATIONSHIPS.zipFilename


        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-${filename}-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //Ler todos users
            def resultMaritalRelationships = []

            MaritalRelationship.withTransaction {
                resultMaritalRelationships = MaritalRelationship.list()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("${filename}");
            doc.appendChild(rootElement);


            int count = 0;

            resultMaritalRelationships.each { region ->
                count++;
                Element element = createMaritalRelationship(doc, region);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + File.separator + "${xmlFilename}"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved! - ${xmlFilename}");
            output.println("File saved! - ${xmlFilename}");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + File.separator + "${zipFilename}")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + File.separator + "${xmlFilename}")
            def b = zipMaker.makeZip()

            println "creating zip - ${zipFilename} - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.MARITAL_RELATIONSHIPS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateInMigrationsXML(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-inmigrations-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultInMigrations = []

            InMigration.withTransaction {
                resultInMigrations = InMigration.findAll()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("inmigrations");
            doc.appendChild(rootElement);

            int count = 0;
            resultInMigrations.each { InMigration inmigration ->
                count++;

                Element element = createInMigration(doc, inmigration);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/inmigrations.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - inmigrations.xml");
            output.println("File saved! - inmigrations.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/inmigrations.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/inmigrations.xml")
            def b = zipMaker.makeZip()

            println "creating zip - inmigrations.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.INMIGRATIONS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateOutMigrationsXML(LogReportCode logReportId) { //read forms

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-outmigrations-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultOutMigrations = []

            OutMigration.withTransaction {
                resultOutMigrations = OutMigration.findAll()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("outmigrations");
            doc.appendChild(rootElement);

            int count = 0;
            resultOutMigrations.each { OutMigration outmigration ->
                count++;

                Element element = createOutMigration(doc, outmigration);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/outmigrations.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - outmigrations.xml");
            output.println("File saved! - outmigrations.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/outmigrations.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/outmigrations.xml")
            def b = zipMaker.makeZip()

            println "creating zip - outmigrations.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.OUTMIGRATIONS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generatePregnancyRegistrationsXML(LogReportCode logReportId) { //read forms

        String filename = SyncEntity.PREGNANCY_REGISTRATIONS.filename
        String xmlFilename = SyncEntity.PREGNANCY_REGISTRATIONS.xmlFilename
        String zipFilename = SyncEntity.PREGNANCY_REGISTRATIONS.zipFilename

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-${filename}-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultPregnancyRegistrations = []

            PregnancyRegistration.withTransaction {
                resultPregnancyRegistrations = PregnancyRegistration.findAll()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("${filename}");
            doc.appendChild(rootElement);

            int count = 0;
            resultPregnancyRegistrations.each { PregnancyRegistration pregnancyregistration ->
                count++;

                Element element = createPregnancyRegistration(doc, pregnancyregistration);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/${xmlFilename}"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - ${xmlFilename}");
            output.println("File saved! - ${xmlFilename}");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/${zipFilename}")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/${xmlFilename}")
            def b = zipMaker.makeZip()

            println "creating zip - ${zipFilename} - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.PREGNANCY_REGISTRATIONS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generatePregnancyOutcomesXML(LogReportCode logReportId) { //read forms

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-pregnancyoutcomes-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultPregnancyOutcomes = []

            PregnancyOutcome.withTransaction {
                resultPregnancyOutcomes = PregnancyOutcome.findAll()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("pregnancyoutcomes");
            doc.appendChild(rootElement);

            int count = 0;
            resultPregnancyOutcomes.each { PregnancyOutcome pregnancyoutcome ->
                count++;

                Element element = createPregnancyOutcome(doc, pregnancyoutcome);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/pregnancyoutcomes.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - pregnancyoutcomes.xml");
            output.println("File saved! - pregnancyoutcomes.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/pregnancyoutcomes.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/pregnancyoutcomes.xml")
            def b = zipMaker.makeZip()

            println "creating zip - pregnancyoutcomes.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.PREGNANCY_OUTCOMES, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateDeathsXML(LogReportCode logReportId) { //read forms

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-deaths-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read forms
            def resultDeaths = []

            Death.withTransaction {
                resultDeaths = Death.findAll()
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("deaths");
            doc.appendChild(rootElement);

            int count = 0;
            resultDeaths.each { Death death ->
                count++;

                Element element = createDeath(doc, death);
                rootElement.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(SystemPath.getGeneratedFilesPath() + "/deaths.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved! - deaths.xml");
            output.println("File saved! - deaths.xml");

            //zip file
            ZipMaker zipMaker = new ZipMaker(SystemPath.getGeneratedFilesPath() + "/deaths.zip")
            zipMaker.addFile(SystemPath.getGeneratedFilesPath() + "/deaths.xml")
            def b = zipMaker.makeZip()

            println "creating zip - deaths.zip - success=" + b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.ROUNDS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())

            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    private Element createUser(Document doc, User user) {
        Element userElement = doc.createElement("user");

        userElement.appendChild(createAttributeNonNull(doc, "code", user.getCode()));
        userElement.appendChild(createAttributeNonNull(doc, "username", user.getUsername()));
        userElement.appendChild(createAttributeNonNull(doc, "password", user.getPassword()));
        userElement.appendChild(createAttributeNonNull(doc, "firstName", user.getFirstName()));
        userElement.appendChild(createAttributeNonNull(doc, "lastName", user.getLastName()));
        userElement.appendChild(createAttributeNonNull(doc, "fullName", user.toString()));
        userElement.appendChild(createAttributeNonNull(doc, "email", user.getEmail() == null ? "" : user.getEmail()));
        userElement.appendChild(createAttributeNonNull(doc, "modules", moduleService.getListModulesAsText(user.modules)));


        return userElement;
    }

    private Element createModule(Document doc, Module module) {
        Element element = doc.createElement("module");

        element.appendChild(createAttributeNonNull(doc, "code", module.getCode()));
        element.appendChild(createAttributeNonNull(doc, "name", module.getName()));
        element.appendChild(createAttributeNonNull(doc, "description", module.getDescription()));

        return element;
    }

    private Element createForm(Document doc, Form form, Collection<FormMapping> formMappingList, RedcapApi redcapApi, Collection<RedcapMapping> redcapMappingList) {
        Element element = doc.createElement("form");

        String formMap = "";
        String redcapMap = "";
        String redcapApiStr = "";

        formMappingList.each {
            if (formMap.isEmpty()) {
                formMap = it.toString()
            } else {
                formMap += ";" + it.toString()
            }
        }

        redcapMappingList.each {
            if (redcapMap.isEmpty()) {
                redcapMap = it.toString()
            } else {
                redcapMap += ";" + it.toString()
            }
        }

        if (redcapApi != null) {
            redcapApiStr = redcapApi.toString()
        }

        //group is disabled
        element.appendChild(createAttributeNonNull(doc, "formId", form.getFormId()));
        element.appendChild(createAttributeNonNull(doc, "formName", form.getFormName()));
        element.appendChild(createAttributeNonNull(doc, "formDescription", form.getFormDescription()));
        element.appendChild(createAttributeNonNull(doc, "formDependencies", form.getDependenciesAsText()));
        element.appendChild(createAttributeNonNull(doc, "regionLevel", "" + form.getRegionLevel()));
        element.appendChild(createAttributeNonNull(doc, "gender", form.getGender()));
        element.appendChild(createAttributeNonNull(doc, "minAge", form.getMinAge() + ""));
        element.appendChild(createAttributeNonNull(doc, "maxAge", form.getMaxAge() + ""));
        element.appendChild(createAttributeNonNull(doc, "modules", moduleService.getListModulesAsText(form.modules)));
        element.appendChild(createAttributeNonNull(doc, "isRegionForm", "" + form.getIsRegionForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isHouseholdForm", "" + form.getIsHouseholdForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isHouseholdHeadForm", "" + form.getIsHouseholdHeadForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isMemberForm", "" + form.getIsMemberForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isFollowUpForm", "" + form.isFollowUpForm.toString()));
        //println "is follow up only"
        element.appendChild(createAttributeNonNull(doc, "multiCollPerSession", form.multiCollPerSession.toString()));
        element.appendChild(createAttributeNonNull(doc, "formMap", formMap));
        element.appendChild(createAttributeNonNull(doc, "redcapApi", redcapApiStr));
        element.appendChild(createAttributeNonNull(doc, "redcapMap", redcapMap));


        return element;
    }

    private Element createCoreFormExt(Document doc, CoreFormExtension form) {
        Element element = doc.createElement("coreformext");

        //group is disabled
        element.appendChild(createAttributeNonNull(doc, "formName", form.formName));
        element.appendChild(createAttributeNonNull(doc, "formId", form.formId));
        element.appendChild(createAttributeNonNull(doc, "extFormId", form.extFormId));
        element.appendChild(createAttributeNonNull(doc, "required", form.required+""));
        element.appendChild(createAttributeNonNull(doc, "enabled", "" + form.enabled+""));
        element.appendChild(createAttributeNonNull(doc, "columnsMapping", form.columnsMapping));


        return element;
    }

    private Element createAppParameter(Document doc, ApplicationParam appParam) {
        Element element = doc.createElement("applicationParam");

        element.appendChild(createAttributeNonNull(doc, "name", appParam.name));
        element.appendChild(createAttributeNonNull(doc, "type", appParam.type.name()));
        element.appendChild(createAttributeNonNull(doc, "value", appParam.value == null ? "" : appParam.value));

        return element;
    }

    private Element createRound(Document doc, Round round) {
        Element element = doc.createElement("round");

        element.appendChild(createAttributeNonNull(doc, "roundNumber", round.roundNumber))
        element.appendChild(createAttributeNonNull(doc, "startDate", StringUtil.format(round.startDate, "yyyy-MM-dd")))
        element.appendChild(createAttributeNonNull(doc, "endDate", StringUtil.format(round.endDate, "yyyy-MM-dd")))
        element.appendChild(createAttributeNonNull(doc, "description", round.description))

        return element;
    }

    private Element createRegion(Document doc, Region region) {
        Element element = doc.createElement("region");

        element.appendChild(createAttributeNonNull(doc, "code", region.code));
        element.appendChild(createAttributeNonNull(doc, "name", region.name));
        element.appendChild(createAttributeNonNull(doc, "hierarchyLevel", region.hierarchyLevel?.code));
        element.appendChild(createAttributeNonNull(doc, "parent", region.parentCode));
        element.appendChild(createAttributeNonNull(doc, "modules", moduleService.getListModulesAsText(region.modules)));

        return element;
    }

    private Element createDataSet(Document doc, Dataset dataset) {
        Element element = doc.createElement("dataset");

        String createdDate = dataset.createdDate == null ? null : StringUtil.format(dataset.createdDate, "yyyy-MM-dd");
        String updatedDate = dataset.updatedDate == null ? null : StringUtil.format(dataset.updatedDate, "yyyy-MM-dd");

        //group is disabled
        element.appendChild(createAttributeNonNull(doc, "datasetId", dataset.getId() + ""));
        //used to download the dataset zip file from the tablet
        element.appendChild(createAttributeNonNull(doc, "name", dataset.name));
        element.appendChild(createAttributeNonNull(doc, "label", dataset.label));
        element.appendChild(createAttributeNonNull(doc, "keyColumn", dataset.keyColumn));
        element.appendChild(createAttributeNonNull(doc, "tableName", dataset.tableName));
        element.appendChild(createAttributeNonNull(doc, "tableColumn", dataset.tableColumn));
        element.appendChild(createAttributeNonNull(doc, "tableColumnLabels", dataset.tableColumnLabels));
        element.appendChild(createAttributeNonNull(doc, "modules", moduleService.getListModulesAsText(dataset.modules)));
        element.appendChild(createAttributeNonNull(doc, "createdBy", dataset.createdBy?.toString()));
        element.appendChild(createAttributeNonNull(doc, "creationDate", createdDate));
        element.appendChild(createAttributeNonNull(doc, "updatedBy", dataset.updatedBy == null ? "" : dataset.updatedBy?.toString()));
        element.appendChild(createAttributeNonNull(doc, "updatedDate", updatedDate == null ? "" : updatedDate));



        return element;
    }

    private Element createResidency(Document doc, Residency residency) {
        Element element = doc.createElement("residency");

        element.appendChild(createAttributeNonNull(doc, "householdCode", residency.householdCode));
        element.appendChild(createAttributeNonNull(doc, "memberCode", residency.memberCode));
        element.appendChild(createAttributeNonNull(doc, "startType", residency.startType.code));
        element.appendChild(createAttributeNonNull(doc, "startDate", residency.startDate));
        element.appendChild(createAttributeNonNull(doc, "endType", residency.endType.code));
        element.appendChild(createAttributeNonNull(doc, "endDate", residency.endDate==null ? "" : StringUtil.format(residency.endDate)));

        return element;
    }

    private Element createVisit(Document doc, Visit visit) {
        Element element = doc.createElement("visit")

        element.appendChild(createAttributeNonNull(doc, "code", visit.code))
        element.appendChild(createAttributeNonNull(doc, "householdCode", visit.householdCode))
        element.appendChild(createAttributeNonNull(doc, "visitDate", StringUtil.format(visit.visitDate, "yyyy-MM-dd")))
        element.appendChild(createAttributeNonNull(doc, "visitReason", visit.visitReason==null ? "" : visit.visitReason.code))
        element.appendChild(createAttributeNonNull(doc, "visitLocation", visit.visitLocation==null ? "" : visit.visitLocation.code))
        element.appendChild(createAttributeNonNull(doc, "visitLocationOther", visit.visitLocationOther==null ? "" : visit.visitLocationOther))
        element.appendChild(createAttributeNonNull(doc, "roundNumber", visit.roundNumber))
        element.appendChild(createAttributeNonNull(doc, "respondentCode", visit.respondentCode==null ? "" : visit.respondentCode))
        element.appendChild(createAttributeNonNull(doc, "hasInterpreter", visit.hasInterpreter==null ? "" : "${visit.hasInterpreter}"))
        element.appendChild(createAttributeNonNull(doc, "interpreterName", visit.interpreterName==null ? "" : visit.interpreterName))
        element.appendChild(createAttributeNonNull(doc, "gpsAccuracy", visit.gpsAccuracy==null ? "" : ""+visit.gpsAccuracy))
        element.appendChild(createAttributeNonNull(doc, "gpsAltitude", visit.gpsAltitude==null ? "" : ""+visit.gpsAltitude))
        element.appendChild(createAttributeNonNull(doc, "gpsLatitude", visit.gpsLatitude==null ? "" : ""+visit.gpsLatitude))
        element.appendChild(createAttributeNonNull(doc, "gpsLongitude", visit.gpsLongitude==null ? "" : ""+visit.gpsLongitude))

        element.appendChild(createAttributeNonNull(doc, "collectedId", visit.collectedId==null ? "" : visit.collectedId))


        return element
    }

    private Element createHeadRelationship(Document doc, HeadRelationship headRelationship) {
        Element element = doc.createElement("headRelationship");

        element.appendChild(createAttributeNonNull(doc, "householdCode", headRelationship.householdCode));
        element.appendChild(createAttributeNonNull(doc, "memberCode", headRelationship.memberCode));
        element.appendChild(createAttributeNonNull(doc, "headCode", headRelationship.headCode));
        element.appendChild(createAttributeNonNull(doc, "relationshipType", headRelationship.relationshipType.code));
        element.appendChild(createAttributeNonNull(doc, "startType", headRelationship.startType.code));
        element.appendChild(createAttributeNonNull(doc, "startDate", headRelationship.startDate));
        element.appendChild(createAttributeNonNull(doc, "endType", headRelationship.endType.code));
        element.appendChild(createAttributeNonNull(doc, "endDate", headRelationship.endDate==null ? "" : StringUtil.format(headRelationship.endDate)));

        return element;
    }

    private Element createMaritalRelationship(Document doc, MaritalRelationship maritalRelationship) {
        Element element = doc.createElement("maritalRelationship");

        element.appendChild(createAttributeNonNull(doc, "memberA_code", maritalRelationship.memberA_code));
        element.appendChild(createAttributeNonNull(doc, "memberB_code", maritalRelationship.memberB_code));
        element.appendChild(createAttributeNonNull(doc, "startStatus", maritalRelationship.startStatus.code));
        element.appendChild(createAttributeNonNull(doc, "startDate", maritalRelationship.startDate));
        element.appendChild(createAttributeNonNull(doc, "endStatus", maritalRelationship.endStatus==null ? "" : maritalRelationship.endStatus.code));
        element.appendChild(createAttributeNonNull(doc, "endDate", maritalRelationship.endDate==null ? "" : StringUtil.format(maritalRelationship.endDate)));

        element.appendChild(createAttributeNonNull(doc, "collectedId", maritalRelationship.collectedId==null ? "" : maritalRelationship.collectedId))

        return element;
    }

    private Element createInMigration(Document doc, InMigration inmigration) {
        Element element = doc.createElement("inmigration")

        element.appendChild(createAttributeNonNull(doc, "memberCode", inmigration.memberCode))
        element.appendChild(createAttributeNonNull(doc, "type", inmigration.type.code))
        element.appendChild(createAttributeNonNull(doc, "originCode", inmigration.originCode==null ? "" : inmigration.originCode))
        element.appendChild(createAttributeNonNull(doc, "originOther", inmigration.originOther==null ? "" : inmigration.originOther))
        element.appendChild(createAttributeNonNull(doc, "destinationCode", inmigration.destinationCode))
        //element.appendChild(createAttributeNonNull(doc, "destinationResidency", inmigration.destinationResidency))
        element.appendChild(createAttributeNonNull(doc, "migrationDate", StringUtil.format(inmigration.migrationDate, "yyyy-MM-dd")))
        element.appendChild(createAttributeNonNull(doc, "migrationReason", inmigration.migrationReason==null ? "" : inmigration.migrationReason))
        element.appendChild(createAttributeNonNull(doc, "visitCode", inmigration.visitCode))

        element.appendChild(createAttributeNonNull(doc, "collectedId", inmigration.collectedId==null ? "" : inmigration.collectedId))

        return element
    }

    private Element createOutMigration(Document doc, OutMigration outmigration) {
        Element element = doc.createElement("outmigration")

        element.appendChild(createAttributeNonNull(doc, "memberCode", outmigration.memberCode))
        element.appendChild(createAttributeNonNull(doc, "migrationType", outmigration.migrationType.code))
        element.appendChild(createAttributeNonNull(doc, "originCode", outmigration.originCode))
        //element.appendChild(createAttributeNonNull(doc, "originResidency", outmigration.originResidency))
        element.appendChild(createAttributeNonNull(doc, "destinationCode", outmigration.destinationCode==null ? "" : outmigration.destinationCode))
        element.appendChild(createAttributeNonNull(doc, "destinationOther", outmigration.destinationOther==null ? "" : outmigration.destinationOther))
        element.appendChild(createAttributeNonNull(doc, "migrationDate", StringUtil.format(outmigration.migrationDate, "yyyy-MM-dd")))
        element.appendChild(createAttributeNonNull(doc, "migrationReason", outmigration.migrationReason==null ? "" : outmigration.migrationReason))
        element.appendChild(createAttributeNonNull(doc, "visitCode", outmigration.visitCode))

        element.appendChild(createAttributeNonNull(doc, "collectedId", outmigration.collectedId==null ? "" : outmigration.collectedId))

        return element
    }

    private Element createPregnancyRegistration(Document doc, PregnancyRegistration pregnancyregistration) {
        Element element = doc.createElement("pregnancyregistration")

        element.appendChild(createAttributeNonNull(doc, "code", pregnancyregistration.code))
        element.appendChild(createAttributeNonNull(doc, "motherCode", pregnancyregistration.motherCode))
        element.appendChild(createAttributeNonNull(doc, "recordedDate", StringUtil.format(pregnancyregistration.recordedDate, "yyyy-MM-dd")))
        element.appendChild(createAttributeNonNull(doc, "pregMonths", pregnancyregistration.pregMonths==null ? "" : "${pregnancyregistration.pregMonths}"))
        element.appendChild(createAttributeNonNull(doc, "eddKnown", pregnancyregistration.eddKnown==null ? "" : "${pregnancyregistration.eddKnown}"))
        element.appendChild(createAttributeNonNull(doc, "hasPrenatalRecord", pregnancyregistration.hasPrenatalRecord==null ? "" : "${pregnancyregistration.hasPrenatalRecord}"))
        element.appendChild(createAttributeNonNull(doc, "eddDate", pregnancyregistration.eddDate==null ? "" : StringUtil.format(pregnancyregistration.eddDate)))
        element.appendChild(createAttributeNonNull(doc, "eddType", pregnancyregistration.eddType==null ? "" : pregnancyregistration.eddType.code))
        element.appendChild(createAttributeNonNull(doc, "lmpKnown", pregnancyregistration.lmpKnown==null ? "" : "${pregnancyregistration.lmpKnown}"))
        element.appendChild(createAttributeNonNull(doc, "lmpDate", pregnancyregistration.lmpDate==null ? "" : StringUtil.format(pregnancyregistration.lmpDate)))
        element.appendChild(createAttributeNonNull(doc, "expectedDeliveryDate", pregnancyregistration.expectedDeliveryDate==null ? "" : StringUtil.format(pregnancyregistration.expectedDeliveryDate)))
        element.appendChild(createAttributeNonNull(doc, "status", pregnancyregistration.status.code))
        element.appendChild(createAttributeNonNull(doc, "visitCode", pregnancyregistration.visitCode))

        element.appendChild(createAttributeNonNull(doc, "collectedId", pregnancyregistration.collectedId==null ? "" : pregnancyregistration.collectedId))

        return element
    }

    private Element createPregnancyOutcome(Document doc, PregnancyOutcome pregnancyoutcome) {
        Element element = doc.createElement("pregnancyoutcome")

        element.appendChild(createAttributeNonNull(doc, "code", pregnancyoutcome.code))
        element.appendChild(createAttributeNonNull(doc, "motherCode", pregnancyoutcome.motherCode))
        element.appendChild(createAttributeNonNull(doc, "fatherCode", pregnancyoutcome.fatherCode))
        element.appendChild(createAttributeNonNull(doc, "numberOfOutcomes", pregnancyoutcome.numberOfOutcomes))
        element.appendChild(createAttributeNonNull(doc, "numberOfLivebirths", pregnancyoutcome.numberOfLivebirths))
        element.appendChild(createAttributeNonNull(doc, "outcomeDate", pregnancyoutcome.outcomeDate==null ? "" : StringUtil.format(pregnancyoutcome.outcomeDate)))
        element.appendChild(createAttributeNonNull(doc, "birthPlace", pregnancyoutcome.birthPlace==null ? "" : pregnancyoutcome.birthPlace.code))
        element.appendChild(createAttributeNonNull(doc, "birthPlaceOther", pregnancyoutcome.birthPlaceOther==null ? "" : pregnancyoutcome.birthPlaceOther))
        element.appendChild(createAttributeNonNull(doc, "visitCode", pregnancyoutcome.visitCode))

        element.appendChild(createAttributeNonNull(doc, "collectedId", pregnancyoutcome.collectedId==null ? "" : pregnancyoutcome.collectedId))

        return element
    }

    private Element createDeath(Document doc, Death death) {
        Element element = doc.createElement("death")

        element.appendChild(createAttributeNonNull(doc, "memberCode", death.memberCode))
        element.appendChild(createAttributeNonNull(doc, "deathDate", StringUtil.format(death.deathDate)))
        element.appendChild(createAttributeNonNull(doc, "ageAtDeath", death.ageAtDeath))
        element.appendChild(createAttributeNonNull(doc, "deathCause", death.deathCause==null ? "" : death.deathCause))
        element.appendChild(createAttributeNonNull(doc, "deathPlace", death.deathPlace==null ? "" : death.deathPlace))
        element.appendChild(createAttributeNonNull(doc, "visitCode", death.visitCode))

        element.appendChild(createAttributeNonNull(doc, "collectedId", death.collectedId==null ? "" : death.collectedId))

        return element
    }

    private Element createAttribute(Document doc, String name, String value){
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        return element;
    }

    private Element createAttributeNonNull(Document doc, String name, String value){
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value==null ? "" : value));
        return element;
    }

    private Element createAttributeNonNull(Document doc, String name, LocalDate value){
        def date = value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return createAttributeNonNull(doc, name, date);
    }

    private Element createAttributeNonNull(Document doc, String name, Integer value){
        return createAttributeNonNull(doc, name, value.toString());
    }

    private Element createAttribute(Document doc, String name, int value){
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(""+value));
        return element;
    }

    /* Convert to XML */
    private String toXML(Household h){
        return ("<household>") +
                ((h.code == null || h.code.isEmpty()) ?               "<code />"   : "<code>${h.code}</code>") +
                ((h.region == null || h.region.isEmpty()) ?           "<region />" : "<region>${h.region}</region>") +
                ((h.name == null || h.name.isEmpty()) ?               "<name />"   : "<name>${h.name}</name>") +

                ((h.headCode == null || h.headCode.isEmpty()) ?       "<headCode />" : "<headCode>${h.headCode}</headCode>") +
                ((h.headName == null || h.headName.isEmpty()) ?       "<headName />" : "<headName>${h.headName}</headName>") +
                ((h.secHeadCode == null || h.secHeadCode.isEmpty()) ? "<secHeadCode />" : "<secHeadCode>${h.secHeadCode}</secHeadCode>") +

                ((h.hierarchy1 == null || h.hierarchy1.isEmpty()) ? "<hierarchy1 />" : "<hierarchy1>${h.hierarchy1}</hierarchy1>") +
                ((h.hierarchy2 == null || h.hierarchy2.isEmpty()) ? "<hierarchy2 />" : "<hierarchy2>${h.hierarchy2}</hierarchy2>") +
                ((h.hierarchy3 == null || h.hierarchy3.isEmpty()) ? "<hierarchy3 />" : "<hierarchy3>${h.hierarchy3}</hierarchy3>") +
                ((h.hierarchy4 == null || h.hierarchy4.isEmpty()) ? "<hierarchy4 />" : "<hierarchy4>${h.hierarchy4}</hierarchy4>") +
                ((h.hierarchy5 == null || h.hierarchy5.isEmpty()) ? "<hierarchy5 />" : "<hierarchy5>${h.hierarchy5}</hierarchy5>") +
                ((h.hierarchy6 == null || h.hierarchy6.isEmpty()) ? "<hierarchy6 />" : "<hierarchy6>${h.hierarchy6}</hierarchy6>") +
                ((h.hierarchy7 == null || h.hierarchy7.isEmpty()) ? "<hierarchy7 />" : "<hierarchy7>${h.hierarchy7}</hierarchy7>") +
                ((h.hierarchy8 == null || h.hierarchy8.isEmpty()) ? "<hierarchy8 />" : "<hierarchy8>${h.hierarchy8}</hierarchy8>") +

                ((h.gpsAccuracy == null) ?   "<gpsAccuracy />" : "<gpsAccuracy>${h.gpsAccuracy}</gpsAccuracy>") +
                ((h.gpsAltitude == null) ?   "<gpsAltitude />" : "<gpsAltitude>${h.gpsAltitude}</gpsAltitude>") +
                ((h.gpsLatitude == null) ?   "<gpsLatitude />" : "<gpsLatitude>${h.gpsLatitude}</gpsLatitude>") +
                ((h.gpsLongitude == null) ? "<gpsLongitude />" : "<gpsLongitude>${h.gpsLongitude}</gpsLongitude>") +

                ((h.collectedId == null) ? "<collectedId />" : "<collectedId>${h.collectedId}</collectedId>") +

                ((h.modules.empty)        ?  "<modules />"     : "<modules>${moduleService.getListModulesAsText(h.modules)}</modules>")+
                ("</household>")
    }

    private String toXML(Member m){
        return  ("<member>") +
                ((m.code==null || m.code.isEmpty()) ?                   "<code />" : "<code>${m.code}</code>") +
                ((m.name==null || m.name.isEmpty()) ?                   "<name />" : "<name>${m.name}</name>") +
                ((m.gender==null ) ?                                    "<gender />" : "<gender>${m.gender.code}</gender>") +
                ((m.dob==null) ?                                        "<dob />" : "<dob>${StringUtil.format(m.dob)}</dob>") +
                ((m.age==null) ?                                        "<age />" : "<age>${m.age}</age>") +

                ((m.ageAtDeath==null) ?                                 "<ageAtDeath />" : "<ageAtDeath>${m.ageAtDeath}</ageAtDeath>") +

                ((m.motherCode==null || m.motherCode.isEmpty()) ?       "<motherCode />" : "<motherCode>${m.motherCode}</motherCode>") +
                ((m.motherName==null || m.motherName.isEmpty()) ?       "<motherName />" : "<motherName>${m.motherName}</motherName>") +
                ((m.fatherCode==null || m.fatherCode.isEmpty()) ?       "<fatherCode />" : "<fatherCode>${m.fatherCode}</fatherCode>") +
                ((m.fatherName==null || m.fatherName.isEmpty()) ?       "<fatherName />" : "<fatherName>${m.fatherName}</fatherName>") +

                ((m.maritalStatus==null) ?                              "<maritalStatus />" : "<maritalStatus>${m.maritalStatus.code}</maritalStatus>") +
                ((m.spouseCode==null || m.spouseCode.isEmpty()) ?       "<spouseCode />" : "<spouseCode>${m.spouseCode}</spouseCode>") +
                ((m.spouseName==null || m.spouseName.isEmpty()) ?       "<spouseName />" : "<spouseName>${m.spouseName}</spouseName>") +
                /*((m.spouseType==null || m.spouseType.isEmpty()) ?       "<spouseType />" : "<spouseType>${m.spouseType}</spouseType>") + */
                ((m.householdCode==null || m.householdCode.isEmpty()) ? "<householdCode />" : "<householdCode>${m.householdCode}</householdCode>") +
                ((m.householdName==null || m.householdName.isEmpty()) ? "<householdName />" : "<householdName>${m.householdName}</householdName>") +

                ((m.startType==null) ?         "<startType />" : "<startType>${m.startType.code}</startType>") +
                ((m.startDate==null)                        ?           "<startDate />" : "<startDate>${StringUtil.format(m.startDate)}</startDate>") +
                ((m.endType==null)     ?         "<endType />"   : "<endType>${m.endType.code}</endType>") +
                ((m.endDate==null)                          ?           "<endDate />"   : "<endDate>${StringUtil.format(m.endDate)}</endDate>") +

                ((m.entryHousehold==null || m.entryHousehold.isEmpty()) ? "<entryHousehold />" : "<entryHousehold>${m.entryHousehold}</entryHousehold>") +
                ((m.entryType==null)           ? "<entryType />" : "<entryType>${m.entryType.code}</entryType>") +
                ((m.entryDate==null)                                    ? "<entryDate />" : "<entryDate>${StringUtil.format(m.entryDate)}</entryDate>") +

                ((m.headRelationshipType==null)                       ? "<headRelationshipType />" : "<headRelationshipType>${m.headRelationshipType.code}</headRelationshipType>") +
                ((m.headRelationshipType==null)                       ? "<isHouseholdHead />" : "<isHouseholdHead>${m.isHouseholdHead()}</isHouseholdHead>") +
                //((m.isSecHouseholdHead==null)                         ? "<isSecHouseholdHead />" : "<isSecHouseholdHead>${m.isSecHouseholdHead}</isSecHouseholdHead>") +

                ((m.gpsAccuracy == null)                              ? "<gpsAccuracy />" : "<gpsAccuracy>${m.gpsAccuracy}</gpsAccuracy>") +
                ((m.gpsAltitude == null)                              ? "<gpsAltitude />" : "<gpsAltitude>${m.gpsAltitude}</gpsAltitude>") +
                ((m.gpsLatitude == null)                              ? "<gpsLatitude />" : "<gpsLatitude>${m.gpsLatitude}</gpsLatitude>") +
                ((m.gpsLongitude == null)                             ? "<gpsLongitude />" : "<gpsLongitude>${m.gpsLongitude}</gpsLongitude>") +

                ((m.collectedId == null) ? "<collectedId />" : "<collectedId>${m.collectedId}</collectedId>") +

                ((m.modules.empty)        ?  "<modules />"     : "<modules>${moduleService.getListModulesAsText(m.modules)}</modules>")+

                ("</member>")
    }
}
