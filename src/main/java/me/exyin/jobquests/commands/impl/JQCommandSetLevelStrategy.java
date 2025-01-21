package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import me.exyin.jobquests.model.Job;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class JQCommandSetLevelStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandSetLevelStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 4) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getAdminHelp());
            return;
        }
        OfflinePlayer offlinePlayer = jobQuests.getServer().getOfflinePlayer(args[1]);
        if (!offlinePlayer.hasPlayedBefore()) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getPlayerNotFound());
            return;
        }
        if (!jobQuests.getJobManager().existsJob(args[2])) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getJobNotFound());
            return;
        }
        try {
            long level = Long.parseLong(args[3]);
            jobQuests.getPlayerManager().setJobLevel(offlinePlayer.getUniqueId(), args[2], level);
            jobQuests.getMessageUtil().sendMessage(commandSender, MessageFormat.format(jobQuests.getMessageConfig().getSetLevel(), offlinePlayer.getName(), args[2], level));
        } catch (NumberFormatException e) {
            jobQuests.getMessageUtil().sendMessage(commandSender, MessageFormat.format(jobQuests.getMessageConfig().getNotANumber(), args[3]));
        }
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return jobQuests.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if (args.length == 3) {
            return jobQuests.getJobManager().getJobs().stream().map(Job::getId).toList();
        }
        return List.of();
    }
}
