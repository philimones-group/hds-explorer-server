package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.io.LogOutput
import net.betainteractive.io.writers.ZipMaker
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.logs.LogStatus
import org.philimone.hds.explorer.server.model.main.DataSet
import org.philimone.hds.explorer.server.model.main.Form
import org.philimone.hds.explorer.server.model.main.FormMapping
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.RedcapApi
import org.philimone.hds.explorer.server.model.main.RedcapMapping
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.StudyModule
import org.philimone.hds.explorer.server.model.main.TrackingList
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

/**
 *  Responsible for generating XML/Zip Files to be exported - eg. Households, Members, Users...
 */
@Transactional
class SyncFilesService {

    def generalUtilitiesService
    def trackingListService
    def syncFilesReportService

    def generateUsersXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-users-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
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

            println "creating zip - users.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            println "error 2: ${logReport.errors}"
        }

        output.close();

    }

    def generateSettingsXML(long logReportId){
        generateAppParametersXML(logReportId)
        generateRegionsXML(logReportId)
        generateModulesXML(logReportId)
        generateFormsXML(logReportId)
        generateDatasetsXML(logReportId)
    }

    def generateModulesXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-modules-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            //read all modules
            def resultModules = []

            StudyModule.withTransaction {
                resultModules = StudyModule.list()
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

            println "creating zip - modules.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }


        output.close();

    }

    def generateFormsXML(long logReportId) { //read forms

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-exp-settings-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
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

            println "creating zip - forms.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateHouseHoldsXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.logsPath, "/generate-households-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
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
                    outputFile.print(h.toXML())
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

            println "creating zip - households.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateMembersXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.logsPath, "/generate-members-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
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
                    outputFile.print(m.toXML())
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

            println "creating zip - members.zip - success="+b

            processed = 1

            //Save number of records
            syncFilesReportService.update(SyncEntity.MEMBERS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            println ""+ex.toString()
            output.println(ex.toString())

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
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }
        output.close();

    }

    def generateAppParametersXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-app-param-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
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

            println "creating zip - params.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateRegionsXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-regions-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
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

            println "creating zip - regions.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateDatasetsXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-dataset-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            def resultDatasets = []

            Region.withTransaction {
                resultDatasets = DataSet.findAllByEnabled(true)
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

            println "creating zip - datasets.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def generateTrackingListsXML(long logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "generate-tracklist-xml-zip");
        PrintStream output = log.output
        if (output == null) return;

        def start = new Date();
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

            println "creating zip - trackinglists.zip - success="+b

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
            LogReportFile reportFile = new LogReportFile(creationDate: new Date(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = new Date()
            reportFile.creationDate = new Date()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = new Date()
            logReport.status = LogStatus.findByName(logStatusValue)
            logReport.addToLogFiles(reportFile)
            logReport.save()

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    private Element createUser(Document doc, User user) {
        Element userElement = doc.createElement("user");

        userElement.appendChild(createAttributeNonNull(doc, "username", user.getUsername()));
        userElement.appendChild(createAttributeNonNull(doc, "password", user.getPassword()));
        userElement.appendChild(createAttributeNonNull(doc, "firstName", user.getFirstName()));
        userElement.appendChild(createAttributeNonNull(doc, "lastName", user.getLastName()));
        userElement.appendChild(createAttributeNonNull(doc, "fullName", user.toString()));
        userElement.appendChild(createAttributeNonNull(doc, "email", user.getEmail()));
        userElement.appendChild(createAttributeNonNull(doc, "modules", user.getModulesAsText()));


        return userElement;
    }

    private Element createModule(Document doc, StudyModule module) {
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
            if (formMap.isEmpty()){
                formMap = it.toString()
            }else{
                formMap += ";" + it.toString()
            }
        }

        redcapMappingList.each {
            if (redcapMap.isEmpty()){
                redcapMap = it.toString()
            }else{
                redcapMap += ";" + it.toString()
            }
        }

        if (redcapApi != null){
            redcapApiStr = redcapApi.toString()
        }

        //group is disabled
        element.appendChild(createAttributeNonNull(doc, "formId", form.getFormId()));
        element.appendChild(createAttributeNonNull(doc, "formName", form.getFormName()));
        element.appendChild(createAttributeNonNull(doc, "formDescription", form.getFormDescription()));
        element.appendChild(createAttributeNonNull(doc, "formDependencies", form.getDependenciesAsText()));
        element.appendChild(createAttributeNonNull(doc, "regionLevel", ""+form.getRegionLevel()));
        element.appendChild(createAttributeNonNull(doc, "gender", form.getGender()));
        element.appendChild(createAttributeNonNull(doc, "minAge", form.getMinAge()+""));
        element.appendChild(createAttributeNonNull(doc, "maxAge", form.getMaxAge()+""));
        element.appendChild(createAttributeNonNull(doc, "modules", form.getModulesAsText()));
        element.appendChild(createAttributeNonNull(doc, "isRegionForm", ""+form.getIsRegionForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isHouseholdForm", ""+form.getIsHouseholdForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isHouseholdHeadForm", ""+form.getIsHouseholdHeadForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isMemberForm", ""+form.getIsMemberForm().toString()));
        element.appendChild(createAttributeNonNull(doc, "isFollowUpForm", ""+form.isFollowUpForm.toString())); //println "is follow up only"
        element.appendChild(createAttributeNonNull(doc, "multiCollPerSession", form.multiCollPerSession.toString()));
        element.appendChild(createAttributeNonNull(doc, "formMap", formMap));
        element.appendChild(createAttributeNonNull(doc, "redcapApi", redcapApiStr));
        element.appendChild(createAttributeNonNull(doc, "redcapMap", redcapMap));


        return element;
    }

    private Element createAppParameter(Document doc, ApplicationParam appParam) {
        Element element = doc.createElement("applicationParam");

        element.appendChild(createAttributeNonNull(doc, "name", appParam.name));
        element.appendChild(createAttributeNonNull(doc, "type", appParam.type));
        element.appendChild(createAttributeNonNull(doc, "value", appParam.value));

        return element;
    }

    private Element createRegion(Document doc, Region region) {
        Element element = doc.createElement("region");

        element.appendChild(createAttributeNonNull(doc, "code", region.code));
        element.appendChild(createAttributeNonNull(doc, "name", region.name));
        element.appendChild(createAttributeNonNull(doc, "hierarchyLevel", region.hierarchyLevel));
        element.appendChild(createAttributeNonNull(doc, "parent", region.parentCode ));

        return element;
    }

    private Element createDataSet(Document doc, DataSet dataSet) {
        Element element = doc.createElement("dataset");

        String labels = "";

        dataSet.mappingLabels.each {
            if (labels.isEmpty()){
                labels = it.toString()
            }else{
                labels += ";" + it.toString()
            }
        }

        String createdDate = dataSet.createdDate==null ? null : StringUtil.format(dataSet.createdDate, "yyyy-MM-dd");
        String updatedDate = dataSet.updatedDate==null ? null : StringUtil.format(dataSet.updatedDate, "yyyy-MM-dd");

        //group is disabled
        element.appendChild(createAttributeNonNull(doc, "datasetId", dataSet.getId()+""));  //used to download the dataset zip file from the tablet
        element.appendChild(createAttributeNonNull(doc, "name", dataSet.name));
        element.appendChild(createAttributeNonNull(doc, "keyColumn", dataSet.keyColumn));
        element.appendChild(createAttributeNonNull(doc, "tableName", dataSet.tableName));
        element.appendChild(createAttributeNonNull(doc, "tableColumn", dataSet.tableColumn));
        element.appendChild(createAttributeNonNull(doc, "createdBy", dataSet.createdBy?.toString()));
        element.appendChild(createAttributeNonNull(doc, "creationDate", createdDate));
        element.appendChild(createAttributeNonNull(doc, "updatedBy", dataSet.updatedBy?.toString()));
        element.appendChild(createAttributeNonNull(doc, "updatedDate", updatedDate));
        element.appendChild(createAttributeNonNull(doc, "labels", labels));


        return element;
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

    private Element createAttribute(Document doc, String name, int value){
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(""+value));
        return element;
    }
}