package net.betainteractive.utilities;

import java.text.Normalizer;
import java.text.ParseException;
import java.util.Date;

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

    public static String removeAcentuation(String text) {

        String converted = Normalizer.normalize(text, Normalizer.Form.NFD);
        converted = converted.replaceAll("\\p{M}", "");

        return converted;
    }

    public static String removeLetters(String text){
        String converted = text.replaceAll("[^\\d.,]", "");
        return converted;
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

    public static String format(Date date, String format){
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

    public static String getFullname(String firstName, String lastName){
        return (firstName.trim() + " " + (lastName==null ? "" : lastName.trim()) ).trim();
    }

    public static String getFullname(String firstName, String middleName, String lastName){
        return ((firstName==null ? "" : firstName.trim()) + " " + (middleName==null ? "" : middleName.trim()) + " " + (lastName==null ? "" : lastName.trim()) ).trim();
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

    public static String capitalize(String text) {
        try {
            text = text.substring(0,1).toUpperCase() + text.toLowerCase().substring(1);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return text;
    }

    public static interface StringConverter {
        public String convert(String value);
    }

}
