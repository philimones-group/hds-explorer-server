package net.betainteractive.utilities

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * Created by paul on 4/16/16.
 */
class GeneralUtil {
    static List splitList(List list, int parts){
        if (parts <= 0 || list.size()==0) return []
        if (parts > list.size()) { parts = list.size() }
        if (parts == 1) return list;

        def result = []
        int c = 0;
        int d = list.size()/parts
        int r = list.size() - (d * parts)
        int s = 0;
        int e = d;

        while (c < parts){
            int x=0
            if (c+1 == parts && r>0){
                x = r
            }

            result.add( list.subList(s, e + x) )
            s = e
            e += d
            c++
        }

        return result
    }

    static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    static double gpsDistanceKm(){

    }

    static int getAge(LocalDate dobDate){
        def age = Period.between(dobDate, LocalDate.now()).years
        return age
    }

    static int getAge(LocalDate dobDate, LocalDate endDate){
        def age = Period.between(dobDate, endDate).years
        return age
    }

    static int getAgeInDays(LocalDate dobDate, LocalDate endDate){
        def age = Period.between(dobDate, endDate).days
        return age
    }

    static String getDurationText(LocalDateTime endTime, LocalDateTime startTime){
        def duration = Duration.between(startTime, endTime)

        List buffer = new ArrayList();
        int days = duration.toDays()
        int hours = duration.toHours()
        int minutes = duration.toMinutes()
        int seconds = duration.get(ChronoUnit.SECONDS)
        int millis = duration.toMillis()

        if (days != 0) buffer.add(days + " days");
        if (hours != 0) buffer.add(hours + " hours");
        if (minutes != 0) buffer.add(minutes + " minutes");

        if (seconds != 0 || millis != 0) {
            int norm_millis = millis % 1000;
            int norm_seconds = seconds + DefaultGroovyMethods.intdiv(millis - norm_millis, 1000).intValue();
            CharSequence millisToPad = "" + Math.abs(norm_millis);
            buffer.add((norm_seconds == 0 ? (norm_millis < 0 ? "-0" : "0") : norm_seconds) + "." + StringGroovyMethods.padLeft(millisToPad, 3, "0") + " seconds");
        }

        if (!buffer.isEmpty()) {
            return DefaultGroovyMethods.join(buffer.iterator(), ", ");
        } else {
            return "0";
        }
    }

    static LocalDate getDate(int y, int m, int d){
        //Calendar cal = Calendar.getInstance()
        //cal.set(y, m, d, 0, 0, 0)
        //return cal.getTime()

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        //return sdf.parse("${y}-${m}-${d}")

        return LocalDate.of(y, m, d)
    }

    static LocalDateTime getDate(int y, int m, int d, int hour, int min, int sec){
        //Calendar cal = Calendar.getInstance()
        //cal.set(y, m, d, hour, min, sec)
        //return cal.getTime()

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        //return sdf.parse("${y}-${m}-${d} ${hour}:${min}:${sec}")

        return LocalDateTime.of(y, m, d, hour, min, sec)
    }

    static LocalDate addDaysToDate(LocalDate date, int days){
        return date.plusDays(days)
    }

}
