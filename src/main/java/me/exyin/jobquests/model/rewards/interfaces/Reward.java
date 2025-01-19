package me.exyin.jobquests.model.rewards.interfaces;

import java.util.UUID;

public interface Reward {
    String getDescription(double quantity);
    void giveReward(UUID uuid, String jobId, double quantity);
}
