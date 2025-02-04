package me.exyin.jobquests.model.rewards.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.enums.RewardType;
import me.exyin.jobquests.model.player.PlayerJob;
import me.exyin.jobquests.model.rewards.interfaces.Reward;
import net.kyori.adventure.text.Component;

import java.text.MessageFormat;
import java.util.UUID;

public class RewardXpStrategy implements Reward {
    private final JobQuests jobQuests;

    public RewardXpStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public Component getDescription(double quantity) {
        return jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiReward().get(RewardType.XP), quantity));
    }

    @Override
    public void giveReward(UUID uuid, String jobId, double quantity) {
        PlayerJob playerJob = jobQuests.getPlayerManager().getPlayerJob(uuid, jobId);
        Job job = jobQuests.getJobManager().getJob(jobId);
        playerJob.setXp(playerJob.getXp() + quantity);
        jobQuests.getMessageUtil().sendMessage(jobQuests.getServer().getPlayer(uuid), MessageFormat.format(jobQuests.getMessageConfig().getRewardXp(), job.getName(), quantity));
    }
}
