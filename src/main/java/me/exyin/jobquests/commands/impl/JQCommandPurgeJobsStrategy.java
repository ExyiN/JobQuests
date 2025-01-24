package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class JQCommandPurgeJobsStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandPurgeJobsStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public boolean canExecute(CommandSender commandSender) {
        return commandSender.hasPermission("jobquests.admin");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getAdminHelp());
            return;
        }
        OfflinePlayer offlinePlayer = jobQuests.getServer().getOfflinePlayer(args[1]);
        if (!offlinePlayer.hasPlayedBefore()) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getPlayerNotFound());
            return;
        }
        List<String> jobsRemoved = jobQuests.getPlayerManager().purgePlayerJobs(offlinePlayer.getUniqueId());
        jobQuests.getMessageUtil().sendMessage(commandSender, MessageFormat.format(jobQuests.getMessageConfig().getPurgePlayer(), offlinePlayer.getName(), jobsRemoved.toString()));
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return jobQuests.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }
}
