package com.thanlinardos.resource_server.misc.utils;

import jakarta.annotation.Nullable;

import java.time.*;

public class DateUtils {

    private DateUtils() {
    }

    public static LocalDateTime getLocalDateTimeFromEpochMilli(Long epochMilli) {
        return getZonedDateTimeFromEpochMilli(epochMilli)
                .toLocalDateTime();
    }

    public static LocalDate getLocalDateFromEpochMilli(Long epochMilli) {
        return getZonedDateTimeFromEpochMilli(epochMilli)
                .toLocalDate();
    }

    public static ZonedDateTime getZonedDateTimeFromEpochMilli(Long epochMilli) {
        return getZonedDateTimeFromEpochMilli(epochMilli, ZoneId.systemDefault());
    }

    public static ZonedDateTime getZonedDateTimeFromEpochMilli(Long epochMilli, ZoneId zoneId) {
        return Instant.ofEpochMilli(epochMilli)
                .atZone(zoneId);
    }

    public static long getEpochMilliFromLocalDateTime(@Nullable LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return 0;
        }
        return getEpochMilliFromZonedDateTime(localDateTime.atZone(ZoneId.systemDefault()));
    }

    public static Long getEpochMilliFromZonedDateTime(ZonedDateTime localDate) {
        return localDate.toInstant()
                .toEpochMilli();
    }

    public static boolean isEqualTo(LocalDate date1, LocalDate date2) {
        return (date1 == null && date2 == null) || (date1 != null && date2 != null && date1.isEqual(date2));
    }

    public static boolean isNotEqualTo(LocalDate date1, LocalDate date2) {
        return !isEqualTo(date1, date2);
    }

    public static boolean isBefore(LocalDate date1, LocalDate date2) {
        return date1 == null || (date2 != null && date1.isBefore(date2));
    }

    public static boolean isAfter(LocalDate date1, LocalDate date2) {
        return date1 == null || (date2 != null && date1.isAfter(date2));
    }

    public static boolean isBeforeOrEqual(LocalDate date1, LocalDate date2) {
        return isEqualTo(date1, date2) || isBefore(date1, date2);
    }

    public static boolean isAfterOrEqual(LocalDate date1, LocalDate date2) {
        return isEqualTo(date1, date2) || isAfter(date1, date2);
    }
}
