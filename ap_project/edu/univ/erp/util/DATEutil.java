package edu.univ.erp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DATEutil {

    private static final DateTimeFormatter fmtr =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final DateTimeFormatter dtfmtr =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public static String dateproper(LocalDate date) {
        return date != null ? date.format(fmtr) : "N/A";
    }

    public static String datetimeproper(LocalDateTime dt) {
        return dt != null ? dt.format(dtfmtr) : "N/A";
    }

    public static boolean isRegistrationOpen(LocalDate deadline) {
        return LocalDate.now().isBefore(deadline) || LocalDate.now().isEqual(deadline);
    }
}
