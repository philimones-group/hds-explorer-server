package org.philimone.hds.explorer.server.model.main

import com.google.common.io.Files
import grails.gorm.transactions.Transactional
import net.betainteractive.io.writers.ZipMaker
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.philimone.hds.explorer.Application
import org.philimone.hds.explorer.io.SystemPath

@Transactional
class TrackingListService {

    /**
     * For tests purposes only
     */
    def copySampleFilesToExternalDocsPath() {


        def fileUrl1 = Application.class.classLoader.getResource("samples/tracking_list_sample_1.xlsx")
        def fileUrl2 = Application.class.classLoader.getResource("samples/tracking_list_sample_2.xlsx")
        def fileTo1 = new File(SystemPath.externalDocsPath + File.separator + "tracking_list_sample_1.xlsx")
        def fileTo2 = new File(SystemPath.externalDocsPath + File.separator + "tracking_list_sample_2.xlsx")

        //println "1 - ${fileUrl1}"
        //println "2 - ${fileUrl2}"

        def file1 = new File(fileUrl1.toURI())
        def file2 = new File(fileUrl2.toURI())

        Files.copy(file1, fileTo1 )
        Files.copy(file2, fileTo2 )
    }

    def String createXMLfrom(List<TrackingList> lists){
        def xml = "<trackinglists>"+"\n"

        lists.each {
            xml += createXMLfromFile(it, it.filename)
        }

        xml += "</trackinglists>"

        return xml
    }

    def String createXMLfromFile(TrackingList trackingList, String xlsFile){
        return createXMLfromFile(trackingList, new File(xlsFile))
    }

    def String createXMLfromFile(TrackingList trackingList, File xlsFile){
        def xml = ""

        //OPEN FILE
        def xlsFormInput = new FileInputStream(xlsFile)
        def xlsFormWorkbook = new XSSFWorkbook(xlsFormInput)
        def xlsFormSheet = xlsFormWorkbook.getSheetAt(0)


        //READING CONTENT
        //VALIDATE CONTENT
        // - remove spaces from "forms and modules"
        // - verify if module exists
        // - verify if user exists

        int i = 0
        String lastCode = ""
        int lastCodeCount = 0
        String lastListId = ""
        int lastListIdCount = 0

        xlsFormSheet.rowIterator().each { row ->

            def cellCode = row.getCell(0)
            def cellName = row.getCell(1)
            def cellTitle = row.getCell(2)
            def cellDetails = row.getCell(3)
            def cellModule = row.getCell(4)
            def cellListId = row.getCell(5)
            def cellListTitle = row.getCell(6)
            def cellListForms = row.getCell(7)
            def cellSubjectCode = row.getCell(8)
            def cellSubjectType = row.getCell(9)
            def cellSubjectForms = row.getCell(10)
            def cellSubjectVisit = row.getCell(11)

            
            if (row.getRowNum()==0){
                //VALIDATE HEADER ROWS
            }else {
                def code = getStringCellValue(cellCode)
                def name = getStringCellValue(cellName)
                def title = getStringCellValue(cellTitle)
                def details = getStringCellValue(cellDetails)
                def module = trackingList.module.code //getStringCellValue(cellModule)
                def listid = getStringCellValue(cellListId)
                def listtitle = getStringCellValue(cellListTitle)
                def listforms = getStringCellValue(cellListForms)
                def sub_code = getStringCellValue(cellSubjectCode)
                def sub_type = getStringCellValue(cellSubjectType)
                def sub_forms = getStringCellValue(cellSubjectForms)
                def sub_visit = getStringCellValue(cellSubjectVisit)

                if (code==null || code.isEmpty()) {
                    //println "last row - ${lastCode}, ${lastListId}"
                    return
                }

                //Removing whitespaces
                if (!module.isEmpty()){
                    module = module.replaceAll("\\s+", "")
                }
                if (!listforms.isEmpty()){
                    listforms = listforms.replaceAll("\\s+", "")
                }
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

                    xml += "<tracking_list id=\"${i}\" code=\"${code}\" name=\"${name}\" details=\"${details}\" title=\"${title}\" module=\"${module}\">"  + "\n"

                    lastCode = code
                    lastListId = ""
                }

                if (!listid.equals(lastListId)){ //new list

                    if (!lastListId.isEmpty()){ //create new tag
                        xml += "</list>" + "\n"
                    }

                    xml += "<list id=\"${listid}\" title=\"${listtitle}\" forms=\"${listforms}\" >" + "\n"

                    lastListId = listid
                }

                if (sub_code != null || !sub_code.isEmpty()){
                    xml += "<subject code=\"${sub_code}\" type=\"${sub_type}\" forms=\"${sub_forms}\" visit=\"${sub_visit}\"/>" + "\n"
                }

            }

        }


        if (!lastListId.isEmpty()){
            xml += "</list>" + "\n"
        }

        if (!lastCode.isEmpty()){
            xml += "</tracking_list>" + "\n"
        }


        //CLOSE FILE
        try {
            if (xlsFormInput != null){
                xlsFormInput.close()
            }
        }catch (Exception ex){
            ex.printStackTrace()
        }


        return xml
    }

    TrackingList getFirstTrackingList(String filename){
        def trackingList = new TrackingList()

        def xlsFormInput = new FileInputStream(filename)
        def xlsFormWorkbook = new XSSFWorkbook(xlsFormInput)
        def xlsFormSheet = xlsFormWorkbook.getSheetAt(0)

        xlsFormSheet.rowIterator().each { row ->

            if (row.getRowNum()>1) return

            def cellCode = row.getCell(0)
            def cellName = row.getCell(1)
            def cellTitle = row.getCell(2)
            def cellDetails = row.getCell(3)
            def cellModule = row.getCell(4)
            def cellListId = row.getCell(5)
            def cellListTitle = row.getCell(6)
            def cellListForms = row.getCell(7)
            def cellSubjectCode = row.getCell(8)
            def cellSubjectType = row.getCell(9)
            def cellSubjectForms = row.getCell(10)
            def cellSubjectVisit = row.getCell(11)


            if (row.getRowNum()==1){

                def code = getStringCellValue(cellCode)
                def name = getStringCellValue(cellName)
                def title = getStringCellValue(cellTitle)
                def details = getStringCellValue(cellDetails)
                def module = getStringCellValue(cellModule)
                def listid = getStringCellValue(cellListId)
                def listtitle = getStringCellValue(cellListTitle)
                def listforms = getStringCellValue(cellListForms)
                def sub_code = getStringCellValue(cellSubjectCode)
                def sub_type = getStringCellValue(cellSubjectType)
                def sub_forms = getStringCellValue(cellSubjectForms)
                def sub_visit = getStringCellValue(cellSubjectVisit)

                trackingList.code = code
                trackingList.name = name
            }


        }


        //CLOSE FILE
        try {
            if (xlsFormInput != null){
                xlsFormInput.close()
            }
        }catch (Exception ex){
            ex.printStackTrace()
        }


        return trackingList
    }

    def String getStringCellValue(Cell cell){
        DataFormatter df = new DataFormatter();
        String value = df.formatCellValue(cell);
        return value==null ? "" : value
    }
}
