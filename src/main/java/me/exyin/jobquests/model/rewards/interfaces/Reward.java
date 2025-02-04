package me.exyin.jobquests.model.rewards.interfaces;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface Reward {
    Component getDescription(double quantity);
    void giveReward(UUID uuid, String jobId, double quantity);
}
