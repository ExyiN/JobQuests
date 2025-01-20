package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.JQCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class JQCommandDefaultStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandDefaultStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getAdminHelp());
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return List.of();
    }
}
