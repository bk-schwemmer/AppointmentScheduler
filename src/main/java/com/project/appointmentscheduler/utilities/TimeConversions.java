package com.project.appointmentscheduler.utilities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Manages conversions between Local, Eastern and UTC LocalDateTimes
 */
public class TimeConversions {
    /**
     * Converts a provided time into UTC
     * @param local the time to convert to UTC
     * @return a UTC representation of the given time
     */
    public static LocalDateTime convertLocalToUTC(LocalDateTime local) {
        ZonedDateTime zonedLocal = local.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedUTC = zonedLocal.withZoneSameInstant(ZoneId.of("UTC"));
        return zonedUTC.toLocalDateTime();
    }

    /**
     * Converts a provided time into Eastern Time
     * @param local the time to convert to ET
     * @return an ET representation of the given time
     */
    public static LocalDateTime convertLocalToET(LocalDateTime local) {
        ZonedDateTime zonedLocal = local.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedET = zonedLocal.withZoneSameInstant(ZoneId.of("America/New_York"));
        return zonedET.toLocalDateTime();
    }

    /**
     * Converts a provided time from UTC to the system default time zone
     * @param utc the time to convert from UTC
     * @return a system default time zone representation of the given time
     */
    public static LocalDateTime convertUTCToLocal(LocalDateTime utc) {
        ZonedDateTime zonedUTC = utc.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedLocal = zonedUTC.withZoneSameInstant(ZoneId.systemDefault());
        return zonedLocal.toLocalDateTime();
    }

    /**
     * Converts a provided time from Eastern Time to the system default time zone
     * @param eastern the time to convert from ET
     * @return a system default time zone representation of the given time
     */
    public static LocalDateTime convertETToLocal(LocalDateTime eastern) {
        ZonedDateTime zonedET = eastern.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime zonedLocal = zonedET.withZoneSameInstant(ZoneId.systemDefault());
        return zonedLocal.toLocalDateTime();
    }
}
