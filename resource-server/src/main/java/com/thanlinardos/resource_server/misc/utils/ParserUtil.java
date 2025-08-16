package com.thanlinardos.resource_server.misc.utils;

import jakarta.ws.rs.core.Response;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ParserUtil {

    private ParserUtil() {
    }

    public static Integer safeParseInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static Float safeParseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static Double safeParseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static OffsetDateTime safeParseOffsetDateTime(String str) {
        try {
            return OffsetDateTime.parse(str);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    public static UUID safeParseUUID(String str) {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    @Nullable
    public static Boolean safeParseBoolean(String value) {
        if (value == null) {
            return null;
        }

        return Boolean.parseBoolean(value);
    }

    public static String safeParseString(Object o) {
        if (o == null) {
            return null;
        }

        return o.toString();
    }

    public static UUID safeParseUUID(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return UUID.fromString(o.toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Optional<UUID> safeParseOptionalUUID(Object o) {
        if (o == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(o.toString()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static List<String> safeParseListString(List<Object> list) {
        List<String> res = new ArrayList<>();
        for (Object o : list) {
            res.add(safeParseString(o));
        }
        return res;
    }

    public static Long safeParseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getPathParameterFromLocationURI(Response response) {
        String[] segments = response.getLocation().getPath().split("/");
        if (segments.length == 0) {
            return null;
        }
        return segments[segments.length - 1];
    }
}
