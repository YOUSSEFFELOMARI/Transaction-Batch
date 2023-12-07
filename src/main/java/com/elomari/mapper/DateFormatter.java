package com.elomari.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    static public String mapDateToFormattedDate(LocalDateTime date){
        if(date==null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm");
        return date.format(formatter);
    }

    static public LocalDateTime mapFormattedDateToDate(String s){
        if(s.isEmpty()) return LocalDateTime.now();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("d/M/yyyy HH:mm");
        LocalDateTime dateTime= LocalDateTime.parse(s,formatter);
        return dateTime;
    }
}
