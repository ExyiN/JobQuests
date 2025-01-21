package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import me.exyin.jobquests.model.Job;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class JQCommandResetQuestStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandResetQuestStrategy(JobQuests jobQuests) {
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
            int questId = Integer.parseInt(args[3]);
            if (!jobQuests.getJobManager().existsQuest(args[2], questId)) {
                jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getQuestNotFound());
                return;
            }
            jobQuests.getPlayerManager().resetPlayerQuest(offlinePlayer.getUniqueId(), args[2], questId);
            jobQuests.getMessageUtil().sendMessage(commandSender, MessageFormat.format(jobQuests.getMessageConfig().getResetQuest(), questId, args[2], offlinePlayer.getName()));
        } catch (NumberFormatException e) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getQuestNotFound());
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
        if (args.length == 4) {
            String jobId = args[2];
            if (jobQuests.getJobManager().existsJob(jobId)) {
                return jobQuests.getJobManager().getJob(jobId).getQuests().stream().map(quest -> String.valueOf(quest.getId())).toList();
            }
        }
        return List.of();
    }
}
