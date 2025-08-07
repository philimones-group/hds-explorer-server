package net.betainteractive.utilities

import com.ibm.icu.util.EthiopicCalendar
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.*
import java.time.format.DateTimeFormatter

class DateUtil {

    enum SupportedCalendar { GREGORIAN, ETHIOPIAN }

    SupportedCalendar calendarType

    DateUtil() {
        this.calendarType = SupportedCalendar.GREGORIAN
    }

    DateUtil(SupportedCalendar calendarType) {
        this.calendarType = calendarType
    }

    static DateUtil getInstance() {
        return new DateUtil(Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR ? SupportedCalendar.ETHIOPIAN : SupportedCalendar.GREGORIAN)
    }

    /* ========= Formatters ========== */

    String formatYMD(LocalDate date) {
        if (!date) return null
        switch (calendarType) {
            case SupportedCalendar.GREGORIAN: return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            case SupportedCalendar.ETHIOPIAN: return formatEthiopianYMD(date)
        }
    }

    String formatYMDHMS(LocalDateTime dt, boolean underscored = false) {
        if (!dt) return null
        switch (calendarType) {
            case SupportedCalendar.GREGORIAN:
                def pattern = underscored ? "yyyy-MM-dd_HH_mm_ss" : "yyyy-MM-dd HH:mm:ss"
                return dt.format(DateTimeFormatter.ofPattern(pattern))
            case SupportedCalendar.ETHIOPIAN:
                return formatEthiopianYMDHMS(dt, underscored)
        }
    }

    String formatPrecise(LocalDateTime dt) {
        if (!dt) return null
        switch (calendarType) {
            case SupportedCalendar.GREGORIAN: return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
            case SupportedCalendar.ETHIOPIAN: return formatEthiopianPrecise(dt)
        }
    }

    String formatSpecialCode(LocalDateTime dt) {
        if (!dt) return null
        switch (calendarType) {
            case SupportedCalendar.GREGORIAN: return dt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss.SSS"))
            case SupportedCalendar.ETHIOPIAN: return formatEthiopianSpecialCode(dt)
        }
    }

    /* ========== Ethiopian Calendar Formatters ========== */

    static String formatEthiopianYMD(LocalDate date) {
        def cal = toEthiopicCalendar(date)
        if (!cal) return null

        def y = cal.get(Calendar.YEAR)
        def m = cal.get(Calendar.MONTH) + 1
        def d = cal.get(Calendar.DATE)

        return String.format("%04d-%02d-%02d EC", y, m, d)
    }

    static String formatEthiopianYMDHMS(LocalDateTime dt, boolean underscored = false) {
        def cal = toEthiopicCalendar(dt)
        if (!cal) return null

        def y = cal.get(Calendar.YEAR)
        def m = cal.get(Calendar.MONTH) + 1
        def d = cal.get(Calendar.DATE)
        def h = cal.get(Calendar.HOUR_OF_DAY)
        def min = cal.get(Calendar.MINUTE)
        def s = cal.get(Calendar.SECOND)

        return underscored ?
                String.format("%04d-%02d-%02d_%02d_%02d_%02d_EC", y, m, d, h, min, s) :
                String.format("%04d-%02d-%02d %02d:%02d:%02d EC", y, m, d, h, min, s)
    }

    static String formatEthiopianPrecise(LocalDateTime dt) {
        def cal = toEthiopicCalendar(dt)
        if (!cal) return null

        def y = cal.get(Calendar.YEAR)
        def m = cal.get(Calendar.MONTH) + 1
        def d = cal.get(Calendar.DATE)
        def h = cal.get(Calendar.HOUR_OF_DAY)
        def min = cal.get(Calendar.MINUTE)
        def s = cal.get(Calendar.SECOND)
        def ms = cal.get(Calendar.MILLISECOND)

        return String.format("%04d-%02d-%02d %02d:%02d:%02d.%03d EC", y, m, d, h, min, s, ms)
    }

    static String formatEthiopianSpecialCode(LocalDateTime dt) {
        def cal = toEthiopicCalendar(dt)
        if (!cal) return null

        def y = cal.get(Calendar.YEAR)
        def m = cal.get(Calendar.MONTH) + 1
        def d = cal.get(Calendar.DATE)
        def h = cal.get(Calendar.HOUR_OF_DAY)
        def min = cal.get(Calendar.MINUTE)
        def s = cal.get(Calendar.SECOND)
        def ms = cal.get(Calendar.MILLISECOND)

        return String.format("%04d%02d%02d-%02d%02d%02d.%03d_EC", y, m, d, h, min, s, ms)
    }

    /* ========== Ethiopian Calendar Conversion Helpers ========== */

    static EthiopicCalendar toEthiopicCalendar(LocalDate date) {
        if (!date) return null
        def gregDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        def ethiopic = new EthiopicCalendar()
        ethiopic.setTime(gregDate)
        return ethiopic
    }

    static EthiopicCalendar toEthiopicCalendar(LocalDateTime dt) {
        if (!dt) return null
        def gregDate = Date.from(dt.atZone(ZoneId.systemDefault()).toInstant())
        def ethiopic = new EthiopicCalendar()
        ethiopic.setTime(gregDate)
        return ethiopic
    }

    static EthiopicCalendar toEthiopicCalendar(int year, int month, int day, int hh, int mm, int ss) {
        try {
            def ethiopic = new EthiopicCalendar()
            ethiopic.set(year, month, day, hh, mm, ss)
            return ethiopic
        } catch (Exception e) {
            return null
        }
    }

    /* ========== Gregorian Date Parsers ========== */

    static LocalDate toLocalDate(String dateStr, String pattern = "yyyy-MM-dd") {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern))
        } catch (Exception e) {
            return null
        }
    }

    static LocalDateTime toLocalDateTime(String dateStr, String pattern = "yyyy-MM-dd HH:mm:ss") {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern))
        } catch (Exception e) {
            return null
        }
    }
}
