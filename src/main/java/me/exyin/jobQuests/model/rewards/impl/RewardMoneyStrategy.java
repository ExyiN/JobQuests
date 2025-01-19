package me.exyin.jobQuests.model.rewards.impl;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.model.enums.RewardType;
import me.exyin.jobQuests.model.rewards.interfaces.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

public class RewardMoneyStrategy implements Reward {
    private final JobQuests jobQuests;

    public RewardMoneyStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public String getDescription(double quantity) {
        return MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiReward().get(RewardType.MONEY), jobQuests.getEconomy().format(quantity));
    }

    @Override
    public void giveReward(UUID uuid, String jobId, int quantity) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        jobQuests.getEconomy().depositPlayer(player, quantity);
        jobQuests.getMessageUtil().sendMessage(jobQuests.getServer().getPlayer(uuid), MessageFormat.format(jobQuests.getMessageConfig().getRewardMoney(), jobQuests.getEconomy().format(quantity)));
    }
}
