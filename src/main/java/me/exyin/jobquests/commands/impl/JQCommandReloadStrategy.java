package me.exyin.jobquests.commands.impl;

import me.exyin.jobquests.JobQuests;
import me.exyin.jobquests.commands.interfaces.JQCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class JQCommandReloadStrategy implements JQCommand {
    private final JobQuests jobQuests;

    public JQCommandReloadStrategy(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        jobQuests.reloadPlugin();
        jobQuests.getServer().getOnlinePlayers().forEach(player -> jobQuests.getPlayerManager().updatePlayer(player.getUniqueId()));
        jobQuests.getMessageUtil().sendMessage(commandSender, jobQuests.getMessageConfig().getReload());
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return List.of();
    }
}
