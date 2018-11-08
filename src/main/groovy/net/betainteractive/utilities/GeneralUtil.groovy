package net.betainteractive.utilities

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

    def static Calendar getCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    static Date getDate(int y, int m, int d){
        Calendar cal = Calendar.getInstance()
        cal.set(y, m, d)
        return cal.getTime()
    }

    static Date getDate(int y, int m, int d, int hour, int min, int sec){
        Calendar cal = Calendar.getInstance()
        cal.set(y, m, d, hour, min, sec)
        return cal.getTime()
    }
}
