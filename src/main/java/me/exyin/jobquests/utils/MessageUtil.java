package me.exyin.jobquests.utils;

import me.exyin.jobquests.JobQuests;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public class MessageUtil {
    private final JobQuests jobQuests;
    private final MiniMessage miniMessage;

    public MessageUtil(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void sendMessage(Audience audience, String message) {
        if (message.isBlank()) {
            return;
        }
        audience.sendMessage(miniMessage.deserialize(jobQuests.getMessageConfig().getPrefix() + message));
    }

    public void sendMessage(Audience audience, List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        lines.forEach(line -> {
            stringBuilder.append(line);
            if (lines.indexOf(line) != lines.size() - 1) {
                stringBuilder.append("<newline>");
            }
        });
        audience.sendMessage(miniMessage.deserialize(jobQuests.getMessageConfig().getPrefix() + stringBuilder));
    }

    public Component toMiniMessageComponent(String message) {
        return miniMessage.deserialize(message);
    }
}
