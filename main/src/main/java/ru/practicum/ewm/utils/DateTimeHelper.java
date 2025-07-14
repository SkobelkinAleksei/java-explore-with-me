package ru.practicum.ewm.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeHelper {
    private final DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime fromStringToLocalDateTime(String date) {
        return LocalDateTime.parse(date, formatter);
    }

    public String fromDateToLocalDateTime(LocalDateTime date) {
        return date.format(formatter);
    }
}
