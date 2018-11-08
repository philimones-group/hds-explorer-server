package net.betainteractive.io.readers;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

/**
 * Created by root on 9/30/14.
 */
public class XLSReader {
    private File file;
    private XSSFWorkbook workbook;

    public XLSReader(String fileName){
        this.file = new File(fileName);

        try {
            FileInputStream fis = new FileInputStream(file);
            this.workbook = new XSSFWorkbook(fis);
        }catch (Exception ex){

        }

    }

    public XSSFWorkbook getWorkbook(){
        return this.workbook;
    }

    public Iterator<Row> getRows(String sheetName){
        if (this.workbook != null){

            XSSFSheet sheet = this.workbook.getSheet(sheetName);

            return (sheet != null) ? sheet.iterator() : null;
        }

        return null;
    }

    public Iterator<Row> getRows(int sheetIndex){
        if (this.workbook != null){

            XSSFSheet sheet = this.workbook.getSheetAt(sheetIndex);

            return (sheet != null) ? sheet.iterator() : null;
        }

        return null;
    }

    /*
    Row row = rowIterator.next();

                String brady = row.getCell(0).getStringCellValue();
     */

}
