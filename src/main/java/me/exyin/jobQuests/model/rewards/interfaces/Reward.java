package me.exyin.jobQuests.model.rewards.interfaces;

import java.util.UUID;

public interface Reward {
    void giveReward(UUID uuid, String jobId, int quantity);
}
