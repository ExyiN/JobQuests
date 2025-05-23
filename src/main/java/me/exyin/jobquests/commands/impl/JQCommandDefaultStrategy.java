package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class JQCommandDefaultStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandDefaultStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public boolean canExecute(CommandSender commandSender) {
        return commandSender.hasPermission("jobquests.use");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("jobquests.admin")) {
            jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getAdminHelp());
            return;
        }
        jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getPlayerHelp());
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return List.of();
    }
}
