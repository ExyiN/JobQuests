package me.exyin.jobQuests.utils;

import lombok.Getter;
import me.exyin.jobQuests.JobQuests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
public class TimeUtil {
    private final JobQuests jobQuests;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

    public TimeUtil(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

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
        if (years > 0) {
            stringBuilder.append(years).append(jobQuests.getGuiConfig().getQuestGuiYear()).append(" ");
        }
        if (months > 0) {
            stringBuilder.append(months).append(jobQuests.getGuiConfig().getQuestGuiMonth()).append(" ");
        }
        if (days > 0) {
            stringBuilder.append(days).append(jobQuests.getGuiConfig().getQuestGuiDay()).append(" ");
        }
        if (hours > 0) {
            stringBuilder.append(hours).append(jobQuests.getGuiConfig().getQuestGuiHour()).append(" ");
        }
        if (minutes > 0) {
            stringBuilder.append(minutes).append(jobQuests.getGuiConfig().getQuestGuiMinute()).append(" ");
        }
        if (seconds > 0) {
            stringBuilder.append(seconds).append(jobQuests.getGuiConfig().getQuestGuiSecond());
        }
        return stringBuilder.toString();
    }
}
