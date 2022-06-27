package org.philimone.hds.explorer.server.model.main

import com.google.common.io.Files
import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.philimone.hds.explorer.Application
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.SubjectType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class TrackingListService {

    def moduleService
    def errorMessageService
    def codeGeneratorService

    def delete(TrackingList trackingListInstance) {

        TrackingListGroup.findAllByTrackingList(trackingListInstance).each {
            TrackingListMapping.executeUpdate("delete from TrackingListMapping m where m.group=?", [it])
        }

        TrackingListGroup.executeUpdate("delete from TrackingListGroup g where g.trackingList=?", [trackingListInstance])

        trackingListInstance.delete(flush:true)
    }

    TrackListSaveResult save(TrackingList currentWebInstance, TrackingListValidationResult validationResult) {

        def errors = new ArrayList<RawMessage>()

        def trackingListResult = getTrackingList(validationResult.xlsContent)
        def trackingList = trackingListResult.instance
        def trackingListGroups = trackingListResult.groups

        if (currentWebInstance != null) {
            trackingList.filename = currentWebInstance.filename
            trackingList.name = currentWebInstance.name
            trackingList.enabled = currentWebInstance.enabled
            trackingList.modules = currentWebInstance.modules
        }

        if (!trackingList.validate()){
            if (trackingList.hasErrors()) {
                errors.addAll(errorMessageService.getRawMessages(trackingList))
                return new TrackListSaveResult(trackingList, errors)
            }
        }

        //delete previous groups and lists
        if (validationResult.isUpdating) {
            TrackingListGroup.findAllByTrackingList(trackingList).each {
                TrackingListMapping.executeUpdate("delete from TrackingListMapping m where m.group=?", [it])
            }

            TrackingListGroup.executeUpdate("delete from TrackingListGroup g where g.trackingList=?", [trackingList])
        }

        trackingListGroups.each {
            trackingList.addToGroups(it)
        }

        if (!trackingList.save()){
            errors.addAll(errorMessageService.getRawMessages(trackingList))
        }

        return new TrackListSaveResult(trackingList, errors)
    }

    TrackListResult createXML(List<TrackingList> lists) {
        def xml = "<trackinglists>"+"\n"
        def count = 0

        lists.each {
            TrackListResult result = createXML(it)
            xml += result.xml
            count += result.listCount
        }

        xml += "</trackinglists>"

        def result = new TrackListResult()
        result.xml = xml
        result.listCount = count

        return result
    }

    TrackListResult createXML(TrackingList trackingList) {
        TrackListResult result = new TrackListResult()

        def xml = ""


        //READING CONTENT
        //VALIDATE CONTENT
        // - remove spaces from "forms and modules"
        // - verify if module exists
        // - verify if user exists

        int i = 0
        int tlistCount=0
        String lastCode = ""
        int lastCodeCount = 0
        String lastListId = ""
        int lastListIdCount = 0

        trackingList.groups.each { group ->
            group.lists.each { row ->
                def code = group.groupCode
                def name = group.groupName
                def title = group.groupTitle
                def details = group.groupDetails
                def modules = toCommaList(trackingList.modules)
                def listid = row.listId+""
                def listtitle = row.listTitle
                //def listforms = row.subjectForms
                def sub_code = row.subjectCode
                def sub_type = row.subjectType.code
                def sub_forms = row.subjectForms
                def sub_visit_code = row.subjectVisitCode
                def sub_visit_uuid = row.subjectVisitUuid


                //Removing whitespaces
                if (!sub_forms.isEmpty()){
                    sub_forms = sub_forms.replaceAll("\\s+", "")
                }

                if (!code.equals(lastCode)){ //new tracking_list
                    i++

                    if (!lastCode.isEmpty()){ //close tag

                        if (!lastListId.isEmpty()){
                            xml += "</list>" + "\n"
                        }

                        xml += "</tracking_list>" + "\n"
                    }

                    xml += "<tracking_list id=\"${i}\" code=\"${code}\" name=\"${name}\" details=\"${details}\" title=\"${title}\" modules=\"${modules}\">"  + "\n"

                    tlistCount++
                    lastCode = code
                    lastListId = ""
                }

                if (!listid.equals(lastListId)){ //new list

                    if (!lastListId.isEmpty()){ //create new tag
                        xml += "</list>" + "\n"
                    }

                    xml += "<list id=\"${listid}\" title=\"${listtitle}\">" + "\n"

                    lastListId = listid
                }

                if (sub_code != null || !sub_code.isEmpty()){
                    xml += "<subject code=\"${sub_code}\" type=\"${sub_type}\" forms=\"${sub_forms}\" visit_code=\"${sub_visit_code}\" visit_uuid=\"${sub_visit_uuid}\"/>" + "\n"
                }


            }
        }

        if (!lastListId.isEmpty()){
            xml += "</list>" + "\n"
        }

        if (!lastCode.isEmpty()){
            xml += "</tracking_list>" + "\n"
        }

        result.listCount = tlistCount;
        result.xml = xml

        return result
    }

    File createTempTrackingListXLS(TrackingList trackingList) {
        def fileTemplate = getSampleFileEmptyXLS()
        def fileXls = new File("/tmp/tracklist-${GeneralUtil.generateUUID()}.xlsx")

        Files.copy(fileTemplate, fileXls);

        def xlsInputStream = new FileInputStream(fileXls)
        def xlsFormWorkbook = new XSSFWorkbook(xlsInputStream)

        //get sheet lists and settings
        def sheetLists = xlsFormWorkbook.getSheetAt(0)
        def sheetSettings = xlsFormWorkbook.getSheetAt(1)

        Map<String, Integer> mapSettingHeaders = new LinkedHashMap<>()
        Map<String, Integer> mapListsHeaders = new LinkedHashMap<>()

        def headerRow = sheetSettings.getRow(0)
        def listsHeaderRow = sheetLists.getRow(0)

        try {
            for (Cell cell : headerRow) {
                String cellValue = getStringCellValue(cell)
                if (cellValue != null)
                    mapSettingHeaders.put(cellValue, cell.getColumnIndex())
            }

            for (Cell cell : listsHeaderRow) {
                String cellValue = getStringCellValue(cell)
                if (cellValue != null)
                    mapListsHeaders.put(cellValue, cell.getColumnIndex())
            }
        } catch (Exception ex) {
            ex.printStackTrace()

            return null
        }

        try {
            //retrieve settings columns
            def uuid_index = mapSettingHeaders.get("uuid")
            def code_index = mapSettingHeaders.get("code")
            def name_index = mapSettingHeaders.get("name")
            def modules_index = mapSettingHeaders.get("modules")
            def enabled_index = mapSettingHeaders.get("enabled")

            def gcode_index = mapListsHeaders.get("group_code")
            def gname_index = mapListsHeaders.get("group_name")
            def gtitle_index = mapListsHeaders.get("group_title")
            def gdetails_index = mapListsHeaders.get("group_details")
            def lid_index = mapListsHeaders.get("list_id")
            def ltitle_index = mapListsHeaders.get("list_title")
            def scode_index = mapListsHeaders.get("subject_code")
            def stype_index = mapListsHeaders.get("subject_type")
            def sforms_index = mapListsHeaders.get("subject_forms")
            def svcode_index = mapListsHeaders.get("subject_visit_code")
            def svuuid_index = mapListsHeaders.get("subject_visit_uuid")

            //create row and set values
            def row_stg = sheetSettings.createRow(1)
            row_stg.createCell(uuid_index).setCellValue(trackingList.id)
            row_stg.createCell(code_index).setCellValue(trackingList.code)
            row_stg.createCell(name_index).setCellValue(trackingList.name)
            row_stg.createCell(modules_index).setCellValue(toCommaList(trackingList.modules))
            row_stg.createCell(enabled_index).setCellValue(trackingList.enabled ? "yes" : "no")

            def row_index = 1
            trackingList.groups.each { group ->
                group.lists.each { item ->
                    def row = sheetLists.createRow(row_index++)

                    row.createCell(gcode_index).setCellValue(group.groupCode)
                    row.createCell(gname_index).setCellValue(group.groupName)
                    row.createCell(gtitle_index).setCellValue(group.groupTitle)
                    row.createCell(gdetails_index).setCellValue(group.groupDetails)
                    row.createCell(lid_index).setCellValue(item.listId+"")
                    row.createCell(ltitle_index).setCellValue(item.listTitle)
                    row.createCell(scode_index).setCellValue(item.subjectCode)
                    row.createCell(stype_index).setCellValue(item.subjectType.code)
                    row.createCell(sforms_index).setCellValue(item.subjectForms)
                    row.createCell(svcode_index).setCellValue(item.subjectVisitCode)
                    row.createCell(svuuid_index).setCellValue(item.subjectVisitUuid)
                }
            }

            def output = new FileOutputStream(fileXls)
            xlsFormWorkbook.write(output)
            output.close()

            return fileXls

        } catch (Exception ex) {
            ex.printStackTrace()

            return null
        }

    }

    TrackingListValidationResult validateXls(String xlsFilename) {

        //OPEN FILE
        def xlsFormInput = new FileInputStream(xlsFilename)

        return validateXls(xlsFormInput)

    }

    TrackingListValidationResult validateXls(InputStream xlsInputStream){
        def xlsFormWorkbook = new XSSFWorkbook(xlsInputStream)

        //get sheet lists and settings
        def sheetLists = null as XSSFSheet
        def sheetSettings = null as XSSFSheet

        try {
            sheetLists = xlsFormWorkbook.getSheetAt(0)
            sheetSettings = xlsFormWorkbook.getSheetAt(1)
        } catch(Exception ex) {
            ex.printStackTrace()

            def message = errorMessageService.getRawMessage("trackingList.validation.sheets.error")
            def result = new TrackingListValidationResult(ValidationStatus.ERROR, [message])

            return result
        }

        //get headers column names
        Map<String, Integer> mapSettingHeaders = new LinkedHashMap<>()
        Map<String, Integer> mapListsHeaders = new LinkedHashMap<>()

        def headerRow = sheetSettings.getRow(0)
        def listsHeaderRow = sheetLists.getRow(0)


        try {
            for (Cell cell : headerRow) {
                String cellValue = getStringCellValue(cell)
                if (cellValue != null)
                    mapSettingHeaders.put(cellValue, cell.getColumnIndex())
            }

            for (Cell cell : listsHeaderRow) {
                String cellValue = getStringCellValue(cell)
                if (cellValue != null)
                    mapListsHeaders.put(cellValue, cell.getColumnIndex())
            }
        } catch (Exception ex) {
            ex.printStackTrace()

            def message = errorMessageService.getRawMessage("trackingList.validation.headers.error")
            def result = new TrackingListValidationResult(ValidationStatus.ERROR, [message])

            return result
        }

        def errorMessages = new ArrayList<RawMessage>()
        def xlsContent = new XLSContent()

        //retrieve settings columns
        def t_uuid_index = mapSettingHeaders.get("uuid")
        def t_code_index = mapSettingHeaders.get("code")
        def t_name_index = mapSettingHeaders.get("name")
        def t_modules_index = mapSettingHeaders.get("modules")
        def t_enabled_index = mapSettingHeaders.get("enabled")

        if (t_uuid_index == null) errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.missing.uuid.column.error"))
        if (t_code_index == null) errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.missing.code.column.error"))
        if (t_name_index == null) errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.missing.name.column.error"))
        if (t_modules_index == null) errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.missing.modules.column.error"))
        if (t_enabled_index == null) errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.missing.enabled.column.error"))


        //ends if there is an error
        if (errorMessages.size()>0) {
            return new TrackingListValidationResult(ValidationStatus.ERROR, errorMessages)
        }


        def settingsValuesRow = sheetSettings.getRow(1)

        def t_uuid = getStringCellValue(settingsValuesRow.getCell(t_uuid_index)) //nullable/blank
        def t_code = getStringCellValue(settingsValuesRow.getCell(t_code_index)) //nullable/blank
        def t_name = getStringCellValue(settingsValuesRow.getCell(t_name_index))
        def t_modules = getStringCellValue(settingsValuesRow.getCell(t_modules_index))
        def t_enabled = getStringCellValue(settingsValuesRow.getCell(t_enabled_index))

        def isUpdateXls = false
        def trackingList = null as TrackingList

        if (!StringUtil.isBlank(t_uuid)) { //validate if is not null - check existence
            trackingList = TrackingList.findById(t_uuid)

            if (trackingList == null) {
                def msg = errorMessageService.getRawMessage("trackingList.validation.field.uuid.notfound.error", [t_uuid])
                errorMessages.add(msg)
            } else {
                isUpdateXls = true
            }
        }

        if (!StringUtil.isBlank(t_code)) { //validate if is not null
            if (TrackingList.countByCode(t_code)==0) {
                def msg = errorMessageService.getRawMessage("trackingList.validation.field.code.notfound.error", [t_code])
                errorMessages.add(msg)
            }
        }

        if (StringUtil.isBlank(t_name)) { //validate cannot be blank
            errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.field.blank.error", ['name']))
        }

        if (StringUtil.isBlank(t_modules)) { //validate cannot be blank
            errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.field.blank.error", ['modules']))
        }

        if (StringUtil.isBlank(t_enabled)) { //validate cannot be blank
            errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.field.blank.error", ['enabled']))
        }

        //ends if there is an error
        if (errorMessages.size()>0) {
            return new TrackingListValidationResult(ValidationStatus.ERROR, errorMessages)
        }


        xlsContent.uuid = t_uuid
        xlsContent.code = t_code
        xlsContent.name = t_name
        xlsContent.modules.addAll(getStringList(t_modules)) //convert to list
        xlsContent.enabled = getBoolean(t_enabled) //convert to boolean


        //validate lists sheet row existence

        def gcode_index = mapListsHeaders.get("group_code")
        def gname_index = mapListsHeaders.get("group_name")
        def gtitle_index = mapListsHeaders.get("group_title")
        def gdetails_index = mapListsHeaders.get("group_details")
        def lid_index = mapListsHeaders.get("list_id")
        def ltitle_index = mapListsHeaders.get("list_title")
        def scode_index = mapListsHeaders.get("subject_code")
        def stype_index = mapListsHeaders.get("subject_type")
        def sforms_index = mapListsHeaders.get("subject_forms")
        def svcode_index = mapListsHeaders.get("subject_visit_code")
        def svuuid_index = mapListsHeaders.get("subject_visit_uuid")


        if (gcode_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["group_code"]))
        if (gname_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["group_name"]))
        if (gtitle_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["group_title"]))
        if (gdetails_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["group_details"]))
        if (lid_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["list_id"]))
        if (ltitle_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["list_title"]))
        if (scode_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["subject_code"]))
        if (stype_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["subject_type"]))
        if (sforms_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["subject_forms"]))
        if (svcode_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["subject_visit_code"]))
        if (svuuid_index== null)  errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.missing.error", ["subject_visit_uuid"]))

        //ends if there is an error
        if (errorMessages.size()>0) {
            return new TrackingListValidationResult(ValidationStatus.ERROR, errorMessages)
        }

        //read lists rows
        sheetLists.rowIterator().each { row ->

            def rowNum = row.getRowNum()
            if (row.getRowNum()==0) return

            def gcode = getStringCellValue(row.getCell(gcode_index)) //not blank - if is an update it must exists
            def gname = getStringCellValue(row.getCell(gname_index)) //not blank
            def gtitle = getStringCellValue(row.getCell(gtitle_index)) //not blank
            def gdetails = getStringCellValue(row.getCell(gdetails_index)) //not blank
            def listid = getStringCellValue(row.getCell(lid_index)) //not blank, must be a number
            def ltitle = getStringCellValue(row.getCell(ltitle_index)) //not blank
            def scode = getStringCellValue(row.getCell(scode_index)) //not blank, must be a existent subject id
            def stype = getStringCellValue(row.getCell(stype_index)) //not blank, must be a valid type
            def sforms = getStringCellValue(row.getCell(sforms_index)) //not blank, must exists in Form
            def svcode = getStringCellValue(row.getCell(svcode_index)) //not blank
            def svuuid = getStringCellValue(row.getCell(svuuid_index)) //blank, if is an update and not blank must exists

            if (StringUtil.isBlank(gcode)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["group_code", "${rowNum}"]))
            }
            //Not needed because we can delete the non-existent follow-up lists and create new ones
            /*else if (isUpdateXls && TrackingListGroup.countByGroupCodeAndTrackingList(gcode, trackingList)==0){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.groupcode.notfound.error", [gcode]))
            }*/

            if (StringUtil.isBlank(gname)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["group_name", "${rowNum}"]))
            }

            if (StringUtil.isBlank(gtitle)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["group_title", "${rowNum}"]))
            }

            if (StringUtil.isBlank(gdetails)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["group_details", "${rowNum}"]))
            }

            if (StringUtil.isBlank(listid)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["list_id", "${rowNum}"]))
            } else if (!listid.isInteger()) {
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.listid.integer.error", ["${rowNum}"]))
            }

            if (StringUtil.isBlank(ltitle)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["list_title", "${rowNum}"]))
            }

            if (StringUtil.isBlank(scode)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["subject_code", "${rowNum}"]))
            } else {
                def type = SubjectType.getFrom(stype)

                if (type == null) {

                } else if (type == SubjectType.REGION && Region.countByCode(scode)==0) {
                    errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.subjectcode.notfound.error", [scode, "${rowNum}", stype]))
                } else if (type == SubjectType.HOUSEHOLD && Household.countByCode(scode)==0) {
                    errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.subjectcode.notfound.error", [scode, "${rowNum}", stype]))
                } else if (type == SubjectType.MEMBER && Member.countByCode(scode)==0) {
                    errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.subjectcode.notfound.error", [scode, "${rowNum}", stype]))
                } else if (type == SubjectType.USER && User.countByCode(scode)==0) {
                    errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.subjectcode.notfound.error", [scode, "${rowNum}", stype]))
                }

            }

            if (StringUtil.isBlank(stype)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["subject_type", "${rowNum}"]))
            } else if (SubjectType.getFrom(stype)==null) {
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.subjecttype.error", ["${rowNum}"]))
            }

            if (StringUtil.isBlank(sforms)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["subject_forms", "${rowNum}"]))
            } else {
                def forms = getStringList(sforms)

                forms.each { formid ->
                    if (Form.countByFormId(formid)==0) {
                        errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.form.notfound.error", [formid, "${rowNum}"]))
                    }
                }
            }

            if (StringUtil.isBlank(svcode)){
                errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["subject_visit_code", "${rowNum}"]))
            }

            //if (StringUtil.isBlank(svuuid)){
            //    errorMessages.add(errorMessageService.getRawMessage("trackingList.validation.lists.field.blank.error", ["subject_visit_uuid", "${rowNum}"]))
            //}
            
            if (errorMessages.size()==0){
                ListItem item = new ListItem()

                item.group_code = gcode
                item.group_name = gname
                item.group_title = gtitle
                item.group_details = gdetails
                item.list_id = Integer.parseInt(listid)
                item.list_title = ltitle
                item.subject_code = scode
                item.subject_type = SubjectType.getFrom(stype)
                item.subject_forms = sforms.replaceAll("\\s+", "")
                item.subject_visit_code = svcode
                item.subject_visit_uuid = svuuid

                xlsContent.rows.add(item)
                
            }
        }

        if (errorMessages.size()>0) {
            xlsContent = null

            return new TrackingListValidationResult(ValidationStatus.ERROR, errorMessages)
        }


        def result = new TrackingListValidationResult(xlsContent)
        result.isUpdating = isUpdateXls
        xlsContent.isUpdating = isUpdateXls

        return result
    }

    TrackListGetResult getTrackingList(XLSContent xlsContent){

        def trackingList = null as TrackingList
        def trackingListGroups = new ArrayList<TrackingListGroup>()

        if (xlsContent.isUpdating) {
            trackingList = TrackingList.findById(xlsContent.uuid)
        } else {
            trackingList = new TrackingList()
            trackingList.code = codeGeneratorService.generateTrackingListCode()
        }

        trackingList.name = xlsContent.name
        //trackingList.filename = fileName
        trackingList.enabled = xlsContent.enabled
        xlsContent.modules.each { module ->
            trackingList.addToModules(module)
        }

        //delete all groups and mappings - do this when you are really saving the content to database
        /*
        if (xlsContent.isUpdating) {
            TrackingListMapping.executeUpdate("delete from TrackingListMapping m where m.group.trackingList=?", [trackingList])
            TrackingListGroup.executeUpdate("delete from TrackingListGroup g where g.trackingList=?", [trackingList])
        }*/

        //create new groups and lists/mappings
        xlsContent.rows.each { listItem ->
            //def group = trackingList.groups.find { it.groupCode==listItem.group_code}
            def group = trackingListGroups.find { it.groupCode==listItem.group_code}
            //println("important: group(${listItem.group_code}) found = ${group!=null}")
            if (group == null) {
                group = new TrackingListGroup()
                group.groupCode = listItem.group_code
                group.groupName = listItem.group_name
                group.groupTitle = listItem.group_title
                group.groupDetails = listItem.group_details

                //add to trackinglist
                trackingListGroups.add(group)
            }

            def map = new TrackingListMapping()
            map.listId = listItem.list_id
            map.listTitle = listItem.list_title
            map.subjectCode = listItem.subject_code
            map.subjectType = listItem.subject_type
            map.subjectForms = listItem.subject_forms
            map.subjectVisitCode = listItem.subject_visit_code
            map.subjectVisitUuid = (StringUtil.isBlank(listItem.subject_visit_uuid)) ? GeneralUtil.generateUUID() : listItem.subject_visit_uuid


            //add to trackinglist group
            group.addToLists(map)
        }

        return new TrackListGetResult(trackingList, trackingListGroups)

    }

    boolean getBoolean(String text) {
        text = text.toLowerCase().replaceAll("\\s+","")

        if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("yes")) {
            return true
        }

        return false
    }

    List<String> getStringList(String commaList) {
        def list = new ArrayList<String>()

        def sf = commaList.replaceAll("\\s+","")
        sf.split(",").each {
            list.add(it)
        }

        return list
    }

    String toCommaList(Collection<? extends String> collection){
        def str = ""

        collection.each {
            str += "${str.empty ? '' : ','}${it}"
        }

        return str
    }

    String getStringCellValue(Cell cell){
        DataFormatter df = new DataFormatter();
        String value = df.formatCellValue(cell);
        return value==null ? "" : value
    }

    File getSampleFileXLS() {
        try {
            def url = getClass().classLoader.getResource("samples/tracking-lists/tracking_list_sample.xlsx")

            return new File(url.toURI())
        }catch (Exception ex){
            //ex.printStackTrace()

            return null
        }
    }

    File getSampleFileEmptyXLS() {
        try {
            def url = getClass().classLoader.getResource("samples/tracking-lists/tracking_list_sample_empty.xlsx")

            return new File(url.toURI())
        }catch (Exception ex){
            //ex.printStackTrace()

            return null
        }
    }

    def updateModules(TrackingList trackingList, List<Module> modules) {

        //delete previous modules
        trackingList.modules.each {
            trackingList.removeFromModules(it)
        }
        //add new modules
        modules.each {
            trackingList.addToModules(it.code)
        }

    }

    class TrackListResult {
        int listCount;
        String xml;
    }

    class TrackListGetResult {
        TrackingList instance
        List<TrackingListGroup> groups;

        TrackListGetResult(TrackingList instance, List<TrackingListGroup> groups) {
            this.instance = instance
            this.groups = groups
        }
    }

    class TrackListSaveResult {
        TrackingList instance
        List<RawMessage> errors = new ArrayList<>()

        TrackListSaveResult(TrackingList instance, List<RawMessage> errors) {
            this.instance = instance
            this.errors.addAll(errors)
        }
    }

    class TrackingListValidationResult {
        ValidationStatus status
        List<RawMessage> errorMessages = new ArrayList<>()
        XLSContent xlsContent
        boolean isUpdating;

        TrackingListValidationResult(ValidationStatus status, List<RawMessage> errors) {
            this.status = status
            this.errorMessages.addAll(errors)
        }

        TrackingListValidationResult(XLSContent xlsContent) {
            this.status = ValidationStatus.SUCCESS
            this.xlsContent = xlsContent
        }
    }

    class XLSContent {
        String uuid
        String code
        String name
        List<String> modules = new ArrayList<>()
        boolean enabled

        boolean isUpdating

        List<ListItem> rows = new ArrayList<>()
    }

    class ListItem {
        def group_code = ""
        def group_name = ""
        def group_title = ""
        def group_details = ""
        def list_id = 0
        def list_title = ""
        def subject_code = ""
        def subject_type = null as SubjectType
        def subject_forms = ""
        def subject_visit_code = ""
        def subject_visit_uuid = ""

    }

    enum ValidationStatus { SUCCESS, ERROR
    }
}