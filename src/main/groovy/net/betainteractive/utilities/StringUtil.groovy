package net.betainteractive.utilities;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by paul on 6/7/15.
 */
public class StringUtil {
    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

    public static Time getTime(String timeStr){

        if (timeStr == null || timeStr.trim().isEmpty()) return null; //dont know

        if (timeStr.equals("99")){
            return new Time(true);
        }

        String[] tm = timeStr.split(":");

        Time time = new Time();

        try{
            if (tm.length==3){ //dd:hh:mm
                time.setDays(Integer.parseInt(tm[0]));
                time.setHours(Integer.parseInt(tm[1]));
                time.setMinutes(Integer.parseInt(tm[2]));
                return time;
            }

            if (!timeStr.contains(":")){
                time.setHours(Integer.parseInt(timeStr));
            }else{
                time.setHours(Integer.parseInt(tm[0]));
                time.setMinutes(Integer.parseInt(tm[1]));
            }

        }catch(ArrayIndexOutOfBoundsException ex){
            System.out.println("tm content: "+timeStr+", err-msg: "+ex.getMessage());
            //ex.printStackTrace();
            return null;
        }

        return time;
    }

    public static String getTimeStr(){
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    }

    public static String removeAcentuation(String text) {

        String converted = Normalizer.normalize(text, Normalizer.Form.NFD);
        converted = converted.replaceAll("\\p{M}", "");

        return converted;
    }

    public static String removeNonAlphanumeric(String str) {
        // replace the given string
        // with empty string
        // except the pattern "[^a-zA-Z0-9]"
        str = str.replaceAll("[^a-zA-Z0-9]", "")

        return str
    }

    public static String removeLetters(String text){
        String converted = text.replaceAll("[^\\d.,]", "");
        return converted;
    }

    public static String remove(String allText, String[] textsToRemove){
        for (String t : textsToRemove){
            allText = allText.replace(t, "");
        }

        return allText;
    }

    public static String remove(String allText, List<String> textsToRemove){
        for (String t : textsToRemove){
            allText = allText.replace(t, "");
        }

        return allText;
    }

    public static String removeQuotes(String text) {
        return text.replaceAll('^\"|\"$', "");
    }

    public static String getStringValueOrZero(String text){
        if (text == null) return "0";

        String converted = text.replaceAll("[^\\d.,]", "");
        converted = converted.replaceAll(",", ".");

        int indexComma = converted.indexOf(".");

        if (indexComma != -1){
            converted = converted.substring(0, indexComma);
        }

        if (converted=="") converted="0";

        return converted;
    }

    public static String getStringValue(String text){
        if (text == null) return "";

        String converted = text.replaceAll("[^\\d.,]", "");
        converted = converted.replaceAll(",", ".");

        int indexComma = converted.indexOf(".");

        if (indexComma != -1){
            converted = converted.substring(0, indexComma);
        }

        return converted;
    }

    public static boolean isUppercase(String text){
        return text.equals(text.toUpperCase());
    }

    public static boolean isInteger(String number){
        try{
            Integer.parseInt(number);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    public static boolean isBlank(String value){
        return value==null || value.trim().isEmpty();
    }

    public static boolean isBlankDate(LocalDate value){
        return value==null;
    }

    public static boolean isBlankBoolean(Boolean value){
        return value==null;
    }

    public static boolean isBlankInteger(Integer value){
        return value==null;
    }

    public static boolean isBlankDouble(Double value){
        return value==null;
    }

    //Search if a certain string is inside a text returning percentage

    //levenstein algorhytm

    /*
        This method searchs all portions of the word on a text, and returns a report about the search
        Without using symbol sensivite
    */
    static SearchReport search(String word, String onText) {
        return search(word, onText, false);
    }

    /*
        This method searchs all portions of the word on a text, and returns a report about the search
    */
    static SearchReport search(String word, String onText, boolean isSymbolSensivite) {

        if (!isSymbolSensivite) {
            onText = removeAcentuation(onText.toLowerCase());
            word = removeAcentuation(word.toLowerCase());
        }

        String[] words = word.split("\\s++");
        boolean[] check = new boolean[words.length];
        int[] indexes = new int[words.length];

        for (int i = 0; i < check.length; i++) {

            if (onText.contains(words[i])) {
                check[i] = true;
                indexes[i] = onText.indexOf(words[i]);
            } else {
                check[i] = false;
            }
        }

        return new SearchReport(indexes, words, check);
    }

    public static double isSimilar(String str1, String str2) {
        return isSimilar(str1, str2, false);
    }

    public static double isSimilar(String str1, String str2, boolean isSymbolSensivite) {
        if (str1 == null || str2 == null) {
            return 0;
        }

        double maxlength = Math.max(str1.length(), str2.length());
        double distance = levenshteinDistance(str1, str2, isSymbolSensivite);
        return 1 - (distance / maxlength);
    }

    private static int levenshteinDistance(String s0, String s1, boolean isSymbolSensivite) {
        int len0 = s0.length() + 1;
        int len1 = s1.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        //search lower strings without acentuation
        if (!isSymbolSensivite) {
            s0 = removeAcentuation(s0.toLowerCase());
            s1 = removeAcentuation(s1.toLowerCase());
        }

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) {
            cost[i] = i;
        }

        // dynamically computing the array of distances
        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }

    public static class SearchReport {

        private int[] indexesOfSearchedWords;
        private String[] searchedWords;
        private boolean found;
        private double percentage;

        public SearchReport(int[] indexes, String[] words, boolean[] checked) {
            this.indexesOfSearchedWords = indexes;
            this.searchedWords = words;
            percentage = calculatePercentage(checked);
            this.found = percentage > 0.9;
        }

        private double calculatePercentage(boolean[] checked) {
            double checkedOnes = 0;
            double total = checked.length;

            for (boolean b : checked) {
                if (b == true) {
                    checkedOnes++;
                }
            }

            return total == 0 ? 0.0 : (checkedOnes / total);
        }

        public boolean hasFound() {
            return found;
        }

        public double getPercentage() {
            return percentage;
        }

        public int[] getIndexesOfSearchedWords() {
            return indexesOfSearchedWords;
        }

        public String[] getSearchedWords() {
            return searchedWords;
        }
    }

    public static class Time {
        boolean unknown = false;
        int days = -1;
        int hours = -1;
        int minutes = 0;
        int seconds = -1;

        public Time(){

        }

        public Time(int days, int hours, int minutes, int seconds){
            this(hours, minutes, -1);
            this.days = days;
        }

        public Time(boolean dontKnown){
            this.unknown = dontKnown;
        }

        public Time(int hours, int minutes, int seconds) {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        public boolean isUnknown(){
            return unknown;
        }

        public int getTotalHoursAprx(){
            return minutes >= 30 ? hours+1 : hours;
        }

        public double getFloatHours(){
            return (hours*1.0) + (minutes*0.01);
        }

        public double getHoursWithPercentageMinutes(){
            double minf = (minutes*1.0) / 60.0;
            return (hours*1.0) + minf;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public int getMinutes() {
            return minutes;
        }

        public int getHours() {
            return hours;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public String getFormattedTime(){
            if (isUnknown()){
                return "99";
            }
            if (days == -1 && seconds == -1){
                return String.format("%02d", hours) + ":" + String.format("%02d", minutes);
            }
            if (days >= 0){
                return String.format("%02d", days) + ":" + String.format("%02d", hours) + ":" + String.format("%02d", minutes);
            }

            return "99";
        }
    }

    public static String format(Date date){
        if (date == null) return "null";
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static String format(LocalDate date){
        if (date == null) return "";
        //"yyyy-MM-dd"
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String formatLocalDate(LocalDate date){
        if (date == null) return "";
        //"yyyy-MM-dd"
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String formatLocalDateTime(LocalDate date){
        if (date == null) return "";
        //"yyyy-MM-dd and TIME"
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String format(LocalDate date, String format) {
        if (date == null) return "";

        DateTimeFormatter formatter = null;

        if (isBlank(format)){
            formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        }else {
            formatter = DateTimeFormatter.ofPattern(format);
        }

        return date.format(formatter);
    }

    public static String format(LocalDateTime dateTime){
        if (dateTime == null) return "";

        //"yyyy-MM-dd"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static String format(LocalDateTime dateTime, String format){
        if (dateTime == null) return "";

        DateTimeFormatter formatter = null;

        if (isBlank(format)){
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }else {
            formatter = DateTimeFormatter.ofPattern(format);
        }

        return dateTime.format(formatter);
    }

    public static LocalDate toLocalDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ex){
            //ex.printStackTrace();
            return null;
        }
    }

    public static LocalDate toLocalDateFromDate(Date date) {
        try {
            def dateString = format(date,"yyyy-MM-dd")

            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ex){
            //ex.printStackTrace();
            return null;
        }
    }

    public static LocalDateTime toLocalDateTime(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ex) {
            //ex.printStackTrace();
            return null;
        }
    }

    public static LocalDateTime toLocalDateTime(String dateString, DateTimeFormatter format) {
        try {
            return LocalDateTime.parse(dateString, format);
        } catch (Exception ex) {
            //ex.printStackTrace();
            return null;
        }
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        try {
            def dateString = format(date,"yyyy-MM-dd HH:mm:ss.SSS")

            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        } catch (Exception ex) {
            //ex.printStackTrace();
            return null;
        }
    }

    public static LocalDateTime toLocalDateTimePrecise(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        } catch (Exception ex) {
            //ex.printStackTrace();
            return null;
        }
    }
/*
    public static String format(Date date, boolean longDateFormat){
        if (date == null) return "null";
        java.text.DateFormat formatter = new java.text.SimpleDateFormat(longDateFormat ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd");
        return formatter.format(date);
    }

    public static String format(LocalDate date, boolean longDateFormat){ //RECHECK PRINT OUR DATES
        DateTimeFormatter formatter = longDateFormat ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") : DateTimeFormatter.ISO_LOCAL_DATE;
        return date.format(formatter);
    }*/

    public static String format(Date date, String format){
        if (date == null) return "";

        java.text.DateFormat formatter = new java.text.SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date toDate(String date, String format){
        java.text.DateFormat formatter = new java.text.SimpleDateFormat(format);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date toDate(String date){
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getFullname(String firstName, String lastName){
        return (firstName.trim() + " " + (lastName==null ? "" : lastName.trim()) ).trim();
    }

    public static String getFullname(String firstName, String middleName, String lastName){
        String name = "";
        firstName = firstName==null ? "" : firstName;
        middleName = middleName==null ? "" : middleName;
        lastName = lastName==null ? "" : lastName;

        name += firstName.trim();
        name += (firstName.isEmpty()  ? "" : middleName.isEmpty() ? "" : " ") + (middleName.trim());
        name += (name.isEmpty() ? "" : " ") + lastName.trim();

        return name.trim();
    }

    public static String[] splitFullname(String fullName){
        fullName = fullName.trim();
        int index = fullName.lastIndexOf(" ");

        String[] names = new String[2];

        if (index != -1){
            names[0] = fullName.substring(0, index);
            names[1] = fullName.substring(index+1);
        }else{
            names[0] = fullName;
            names[1] = "";
        }

        return names;
    }

    public static Name splitName(String fullName){
        fullName = fullName.trim();
        int index = fullName.lastIndexOf(" ");

        Name name = new Name();

        if (index != -1){
            name.firstName = fullName.substring(0, index);
            name.lastName = fullName.substring(index+1);
        }else{
            name.firstName = fullName;
            name.lastName = "";
        }

        return name;
    }

    public static List<String> toList(String listText){
        def list = new ArrayList<String>()

        listText = listText.replaceAll("\\s+", "") //remove spaces
        listText = listText.replace("[", "")
        listText = listText.replace("]", "")

        list.addAll(listText.split(","))

        return list
    }

    public static String capitalize(String text) {
        try {
            text = text.substring(0,1).toUpperCase() + text.toLowerCase().substring(1);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return text;
    }

    public static String removeCamelCaseAndCapitalize(String text){
        String str = "";
        boolean upperNext = false;

        for (int i=0; i < text.length(); i++){
            char c = text.charAt(i);

            if (i==0) {  str += Character.toUpperCase(c); continue; }

            if (c == ' ' || c == '_'){
                upperNext = true;
            }else {
                str += upperNext ? Character.toUpperCase(c) : c;
                upperNext = false;
            }
        }

        return str;
    }

    public static String removePascalCase(String text){
        if (text == null) return null;

        def str = toSnakeCase(text)
        def spt = str.split("_")
        str = ""

        for (def s : spt){
            str += (str.empty ? "" : " ") + capitalize(s)
        }

        return str
    }

    public static String toSnakeCase(String s){
        String ss = "";
        String last = "";
        int repeatNr = 0;
        boolean lastIsUpper = false

        for (int i=0; i < s.length(); i++) {

            char ch = s.charAt(i)
            String it = ""+ch+"";

            if (i==0 || lastIsUpper){
                it = it.toLowerCase() //first character will be always lowercase
                ch = ch.toLowerCase()
            }

            repeatNr = it.matches("[0-9]+") ? ++repeatNr : 0;
            //println "rp "+repeatNr

            if (Character.isUpperCase(ch)){

                if (repeatNr<2) {ss += "_" + it;} else {ss += it;}

            }else{
                ss += it;
            }
            last = it;

            lastIsUpper = Character.isUpperCase(s.charAt(i)) //last/current char is uppercase
        }

        return ss;
    }

    public static String removePackageNames(String text){
        String packageExpClass = "class ([a-zA-Z_]+\\.)+";
        String packageExp = "([a-zA-Z_]+\\.)+";

        text = text.replaceAll(packageExpClass, "");
        text = text.replaceAll(packageExp, "");

        return text;
    }

    /**Numbers**/
    public static boolean isDouble(String str){
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from section 3.10.2 of
                        // The Java Language Specification.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\.("+Digits+")("+Exp+")?)|"+

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        /*
        if (Pattern.matches(fpRegex, str))
            Double.valueOf(str); // Will not throw NumberFormatException
        else {
            // Perform suitable alternative action
        }*/

        return Pattern.matches(fpRegex, str);
    }

    public static Double getDouble(String value){
        try{
            return Double.parseDouble(value);
        } catch (Exception ex){
            return null;
        }
    }

    public static Integer getInteger(String value){
        if (value == null) return null;

        try{
            return Integer.parseInt(value);
        } catch (Exception ex){
            return null;
        }
    }

    public static Boolean getBoolean(String value){
        if (value == null) return null;

        try{
            return Boolean.parseBoolean(value);
        } catch (Exception ex){
            return null;
        }
    }

    public static interface StringConverter {
        public String convert(String value);
    }

    public static class Name {
        private String firstName;
        private String lastName;

        public Name() {
        }

        public Name(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

}
