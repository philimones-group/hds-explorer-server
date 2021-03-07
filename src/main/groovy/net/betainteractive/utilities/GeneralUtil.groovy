package net.betainteractive.utilities

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

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
        UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        return uid.randomUUID().toString()
    }

    static double gpsDistanceKm(){

    }

    static int getAge(Date dobDate){
        Calendar now = Calendar.getInstance()
        Calendar dob = Calendar.getInstance()
        dob.setTime(dobDate)

        def age = now.get(Calendar.YEAR)-dob.get(Calendar.YEAR) + (now.get(Calendar.DAY_OF_YEAR)<dob.get(Calendar.DAY_OF_YEAR) ? -1 : 0)

        return age
    }

    static int getAge(Date dobDate, Date endDate){
        Calendar end = Calendar.getInstance()
        Calendar dob = Calendar.getInstance()
        end.setTime(endDate)
        dob.setTime(dobDate)

        def age = end.get(Calendar.YEAR)-dob.get(Calendar.YEAR) + (end.get(Calendar.DAY_OF_YEAR)<dob.get(Calendar.DAY_OF_YEAR) ? -1 : 0)

        return age
    }

    static int getAgeInDays(Date dobDate, Date endDate){
        Calendar end = Calendar.getInstance()
        Calendar dob = Calendar.getInstance()
        end.setTime(endDate)
        dob.setTime(dobDate)

        return end - dob
    }

    def static int getYearsDiff(Date firstDate, Date secondDate){
        Calendar second = Calendar.getInstance()
        Calendar first = Calendar.getInstance()
        first.setTime(firstDate)
        second.setTime(secondDate)

        def diff = second.get(Calendar.YEAR)-first.get(Calendar.YEAR) + (second.get(Calendar.DAY_OF_YEAR)<first.get(Calendar.DAY_OF_YEAR) ? -1 : 0)

        return diff
    }

    def static Calendar getCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    static boolean dateEquals(Date dateA, Date dateB){
        if (dateA == null || dateB == null) return false

        def calA = getCalendar(dateA)
        def calB = getCalendar(dateB)


        return calA.compareTo(calB)==0
    }

    static Date getDate(int y, int m, int d){
        //Calendar cal = Calendar.getInstance()
        //cal.set(y, m, d, 0, 0, 0)

        //return cal.getTime()

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        return sdf.parse("${y}-${m}-${d}")
    }

    static Date getDate(int y, int m, int d, int hour, int min, int sec){
        //Calendar cal = Calendar.getInstance()
        //cal.set(y, m, d, hour, min, sec)
        //return cal.getTime()

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        return sdf.parse("${y}-${m}-${d} ${hour}:${min}:${sec}")
    }

    static Date addDaysToDate(Date date, int days){
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.getTime()
    }

    static Date getDateStart(Date date){
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        return cal.getTime()
    }

    static Date getDateEnd(Date date){
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)

        return cal.getTime()
    }

    static Date getDateStart(Date date, int plusDays){
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        cal.add(Calendar.DAY_OF_YEAR, plusDays)

        return cal.getTime()
    }

    static Date getDateEnd(Date date, int plusDays){
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)

        cal.add(Calendar.DAY_OF_YEAR, plusDays)

        return cal.getTime()
    }
}
