package me.exyin.jobquests.commands.interfaces;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface JQCommand {
    void execute(CommandSender commandSender, String[] args);
    List<String> getTabCompletion(String[] args);
}
