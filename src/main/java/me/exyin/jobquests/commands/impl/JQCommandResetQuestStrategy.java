package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.JQCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

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
        OfflinePlayer playerToResetQuest = jobQuests.getServer().getOfflinePlayer(args[1]);
        if (!playerToResetQuest.hasPlayedBefore()) {
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
            jobQuests.getPlayerManager().resetPlayerQuest(playerToResetQuest.getUniqueId(), args[2], questId);
            jobQuests.getMessageUtil().sendMessage(commandSender, MessageFormat.format(jobQuests.getMessageConfig().getResetQuest(), questId, args[2], playerToResetQuest.getName()));
        } catch (NumberFormatException e) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getQuestNotFound());
        }
    }
}
