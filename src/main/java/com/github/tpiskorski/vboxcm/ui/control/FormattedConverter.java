package com.github.tpiskorski.vboxcm.ui.control;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormattedConverter extends StringConverter<LocalDate> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String toString(LocalDate localDate) {
        if (localDate == null) {
            return "";
        }
        return DATE_TIME_FORMATTER.format(localDate);
    }

    @Override
    public LocalDate fromString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DATE_TIME_FORMATTER);
    }
}
