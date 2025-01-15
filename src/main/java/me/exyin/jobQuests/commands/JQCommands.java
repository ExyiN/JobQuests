package me.exyin.jobQuests.commands;

import me.exyin.jobQuests.JobQuests;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class JQCommands implements CommandExecutor {
    private final JobQuests jobQuests;

    public JQCommands(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 1) {
            return true;
        }
        if(args[0].equals("reload")) {
            jobQuests.reloadJobs();
        }
        return true;
    }
}
