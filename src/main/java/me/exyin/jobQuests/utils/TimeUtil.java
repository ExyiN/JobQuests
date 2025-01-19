package me.exyin.jobQuests.utils;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
public class TimeUtil {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

    public LocalDateTime getRefreshDate(LocalDateTime dateTime, String refreshTime) {
        String[] splitRefreshTime = refreshTime.split(" ");
        String unit = "m";
        if (splitRefreshTime.length == 0) {
            return LocalDateTime.now();
        }
        if (splitRefreshTime.length > 1) {
            unit = splitRefreshTime[1];
        }
        int time;
        try {
            time = Integer.parseInt(splitRefreshTime[0]);
        } catch (NumberFormatException e) {
            time = 1;
        }
        return switch (unit) {
            case "d" -> dateTime.plusDays(time);
            case "h" -> dateTime.plusHours(time);
            case "s" -> dateTime.plusSeconds(time);
            default -> dateTime.plusMinutes(time);
        };
    }

    public String getTimeFormattedFromDates(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        LocalDateTime tempDateTime = fromDateTime;
        long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);
        long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);
        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);
        long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);
        long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);
        long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

        return getTimeFormatted(years, months, days, hours, minutes, seconds);
    }

    private String getTimeFormatted(long years, long months, long days, long hours, long minutes, long seconds) {
        StringBuilder stringBuilder = new StringBuilder();
        if(years > 0) {
            stringBuilder.append(years).append("y ");
        }
        if(months > 0) {
            stringBuilder.append(months).append("m ");
        }
        if(days > 0) {
            stringBuilder.append(days).append("d ");
        }
        if(hours > 0) {
            stringBuilder.append(hours).append("h ");
        }
        if(minutes > 0) {
            stringBuilder.append(minutes).append("m ");
        }
        if(seconds > 0) {
            stringBuilder.append(seconds).append("s ");
        }
        return stringBuilder.toString();
    }
}
