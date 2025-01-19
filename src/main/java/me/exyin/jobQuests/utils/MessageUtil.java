package me.exyin.jobQuests.utils;

import me.exyin.jobQuests.JobQuests;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageUtil {
    private final JobQuests jobQuests;
    private final MiniMessage miniMessage;

    public MessageUtil(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void sendMessage(Audience audience, String message) {
        if(message.isBlank()) {
            return;
        }
        audience.sendMessage(miniMessage.deserialize(jobQuests.getMessageConfig().getPrefix() + message));
    }

    public Component toMiniMessageComponent(String message) {
        return miniMessage.deserialize(message);
    }
}
