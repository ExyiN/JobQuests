package me.exyin.jobquests.model.rewards.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.model.enums.RewardType;
import me.exyin.jobquests.model.rewards.interfaces.Reward;
import net.kyori.adventure.text.Component;
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
    public Component getDescription(double quantity) {
        return jobQuests.getMessageUtil().toMiniMessageComponent(MessageFormat.format(jobQuests.getGuiConfig().getQuestGuiReward().get(RewardType.MONEY), jobQuests.getEconomy().format(quantity)));
    }

    @Override
    public void giveReward(UUID uuid, String jobId, double quantity) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        jobQuests.getEconomy().depositPlayer(player, quantity);
        jobQuests.getMessageUtil().sendMessage(jobQuests.getServer().getPlayer(uuid), MessageFormat.format(jobQuests.getMessageConfig().getRewardMoney(), jobQuests.getEconomy().format(quantity)));
    }
}
