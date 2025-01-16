package me.exyin.jobQuests.model.rewards.impl;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.player.PlayerJob;
import me.exyin.jobQuests.model.rewards.interfaces.Reward;

import java.util.UUID;

public class RewardXpStrategy implements Reward {
    private final JobQuests jobQuests;

    public RewardXpStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void giveReward(UUID uuid, String jobId, int quantity) {
        PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(uuid, jobId);
        playerJob.setXp(playerJob.getXp() + quantity);
    }
}
