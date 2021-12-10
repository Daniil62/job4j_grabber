package ru.job4j.grabber.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );
    private static final String TODAY = "сегодня,";
    private static final String YESTERDAY = "вчера,";
    private static final String DATE_FORMAT = "dd.MM.yy, ";
    private static final String DATE_TIME_FORMAT = "dd.MM.yy, HH:mm";
    private static final int SHORT_DATE_SEQUENCE = 2;
    private static final int FULL_DATE_SEQUENCE = 4;

    @Override
    public LocalDateTime parse(String dateText) {
        String[] dividedDate = dateText.split(" ");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        if (dividedDate.length == SHORT_DATE_SEQUENCE) {
            String date = dividedDate[0];
            if (TODAY.equals(date) || YESTERDAY.equals(date)) {
                if (YESTERDAY.equals(date)) {
                    calendar.add(Calendar.DATE, -1);
                }
                date = dateFormat.format(calendar.getTime());
                dividedDate[0] = date;
            }
        } else if (dividedDate.length == FULL_DATE_SEQUENCE) {
            String month = dividedDate[1];
            dividedDate[1] = MONTHS.get(month);
        }
        return LocalDateTime.parse(glueDate(dividedDate), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    private String glueDate(String[] dividedDate) {
        StringBuilder builder = new StringBuilder();
        for (String element : dividedDate) {
            if (element.length() == 1) {
                element = "0" + element;
            }
            builder.append(element);
            if (element.length() == 2) {
                builder.append(".");
            } else if (element.endsWith(",")) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
}
