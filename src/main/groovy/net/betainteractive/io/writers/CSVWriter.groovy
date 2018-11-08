package net.betainteractive.io.writers

import net.betainteractive.utilities.StringUtil

import java.text.SimpleDateFormat

/**
 * Created by paul on 5/12/15.
 */
class CSVWriter {

    private PrintStream output
    private String fileName

    LinkedHashMap<String,String> columnsValues;

    public CSVWriter(String filename, String... columns){
        this.fileName = filename
        this.output = new PrintStream(new FileOutputStream(filename), true );
        this.columnsValues = new LinkedHashMap<>()

        columns.each { String key ->
            columnsValues.put(key, "")
        }

        writeColumnHeader()
    }

    /*
    public CSVWriter(String filename, String commaFields){
        this(filename, commaFields.split(","))
    }
    */

    public void rewrite(){
        clearValues()

        if (output != null){
            output.close()
            output = null
        }

        this.output = new PrintStream(new FileOutputStream(fileName), true );

        writeColumnHeader()
    }

    private void writeColumnHeader(){
        String header = ""

        columnsValues.keySet().each { String key ->
            header += '"'+ key +'",'
        }

        if (!header.isEmpty()){
            header = header.substring(0,header.length()-1)
        }

        output.println(header)
    }

    def save(){

        String row = ""

        columnsValues.keySet().each { String key ->
            String value = columnsValues.get(key)
            row +=  value.isEmpty() ? ","  : value +','
        }

        if (!row.isEmpty()){
            row = row.substring(0,row.length()-1)
        }

        output.println(row)

        clearValues()
    }

    def clearValues() {
        columnsValues.keySet().each { String key ->
            columnsValues.put(key, "")
        }
    }

    def insert(String columnName, String value){

        //println columnName.toString()

        if (value == null) return;

        if (columnsValues.containsKey(columnName)){
            columnsValues.put(columnName, '"'+value+'"')
        }else{
            println "wrong column name: "+columnName
        }
    }

    def insert(String columnName, String value, StringUtil.StringConverter converter){

        //println columnName.toString()

        if (value == null) return;

        if (columnsValues.containsKey(columnName)){
            columnsValues.put(columnName, '"'+converter.convert(value)+'"')
        }else{
            println "wrong column name: "+columnName
        }
    }

    def insert(String columnName, Integer value){
        if (value == null || value.toString().equals("null")){
            insertNonQuote(columnName, "0")
        }else{
            insertNonQuote(columnName, value.toString())
        }
    }

    def insertNonQuote(String columnName, String value){
        if (value == null) return;

        if (columnsValues.containsKey(columnName)){
            columnsValues.put(columnName, value)
        }else{
            println "wrong column name: "+columnName
        }
    }

    def insertDate(String columnName, Date value){
        if (value == null) return;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
        insert(columnName, date)
    }

    def insertDateNS(String columnName, Date value){
        if (value == null) return;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(value);
        insert(columnName, date)
    }

    def insertDate(String columnName, Date value, String dateFormat){
        if (value == null) return;
        String date = new SimpleDateFormat(dateFormat).format(value);
        insert(columnName, date)
    }

    def insertDateOnly(String columnName, Date value){
        if (value == null) return;
        String date = new SimpleDateFormat("yyyy-MM-dd").format(value);
        insert(columnName, date)
    }

    def insertDecimal(String columnName, BigDecimal value){
        if (value == null || value.toString().equals("null")){
            insertNonQuote(columnName, "0")
        }else{
            insertNonQuote(columnName, value.toString())
        }

    }

    def insertInt(String columnName, Integer value){
        if (value == null || value.toString().equals("null")){
            insertNonQuote(columnName, "0")
        }else{
            insertNonQuote(columnName, value.toString())
        }
    }

    def close() {
        if (output != null){
            output.close()
            output = null
        }
    }
}
