package me.exyin.jobQuests.utils;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}
