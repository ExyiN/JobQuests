package me.exyin.jobquests.commands;

import org.bukkit.command.CommandSender;

public interface JQCommand {
    void execute(CommandSender commandSender, String[] args);
}
