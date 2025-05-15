package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import me.exyin.jobquests.model.Job;
import me.exyin.jobquests.model.player.JQPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class JQCommandPlayerInfoStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandPlayerInfoStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public boolean canExecute(CommandSender commandSender) {
        return commandSender.hasPermission("jobquests.use");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            if (commandSender.hasPermission("jobquests.admin")) {
                jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getAdminHelp());
                return;
            }
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getPlayerHelp());
            return;
        }
        OfflinePlayer offlinePlayer = jobQuests.getServer().getOfflinePlayer(args[1]);
        if (!offlinePlayer.hasPlayedBefore()) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getPlayerNotFound());
            return;
        }
        if (!jobQuests.getPlayerManager().isPlayerLoaded(offlinePlayer.getUniqueId())) {
            jobQuests.getPlayerManager().loadPlayer(offlinePlayer.getUniqueId());
        }
        JQPlayer jqPlayer = jobQuests.getPlayerManager().getJQPlayer(offlinePlayer.getUniqueId());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageFormat.format(jobQuests.getMessageConfig().getPlayerInfoTitle(), offlinePlayer.getName()));
        jqPlayer.getPlayerJobs().forEach(playerJob -> {
            Job job = jobQuests.getJobManager().getJob(playerJob.getJobId());
            stringBuilder.append("<newline>");
            stringBuilder.append(MessageFormat.format(jobQuests.getMessageConfig().getPlayerInfoLine(), job.getName(), playerJob.getLevel(), String.format("%.2f", playerJob.getXp()), String.format("%.2f", jobQuests.getPlayerManager().getNextLevelRequiredXp(playerJob.getLevel()))));
        });
        jobQuests.getMessageUtil().sendMessage(commandSender, stringBuilder.toString());
        if (jobQuests.getPlayerManager().isPlayerLoaded(offlinePlayer.getUniqueId()) && !offlinePlayer.isOnline()) {
            jobQuests.getPlayerManager().unloadPlayer(offlinePlayer.getUniqueId());
        }
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return jobQuests.getServer().getOnlinePlayers().stream().map(Player::getName).filter(player -> player.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }
        return List.of();
    }
}
