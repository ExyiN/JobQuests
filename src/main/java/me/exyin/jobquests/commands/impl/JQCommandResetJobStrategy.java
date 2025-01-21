package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import me.exyin.jobquests.model.Job;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class JQCommandResetJobStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandResetJobStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 3) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getAdminHelp());
            return;
        }
        OfflinePlayer playerToResetJob = jobQuests.getServer().getOfflinePlayer(args[1]);
        if (!playerToResetJob.hasPlayedBefore()) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getPlayerNotFound());
            return;
        }
        if (!jobQuests.getJobManager().existsJob(args[2])) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getJobNotFound());
            return;
        }
        jobQuests.getPlayerManager().resetPlayerJob(playerToResetJob.getUniqueId(), args[2]);
        jobQuests.getMessageUtil().sendMessage(commandSender, MessageFormat.format(jobQuests.getMessageConfig().getResetJob(), args[2], playerToResetJob.getName()));
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
