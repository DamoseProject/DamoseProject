package util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeManager {

    public static String getDate(long unixTime) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime zdt = LocalDateTime.ofInstant(instant, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = zdt.format(formatter);
        return date;
    }


    public static String getDate(long unixTime, String format) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime zdt = LocalDateTime.ofInstant(instant, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String date = zdt.format(formatter);
        return date;
    }





}
