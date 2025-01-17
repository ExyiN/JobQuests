package me.exyin.jobQuests.commands;

import me.exyin.jobQuests.JobQuests;
import me.exyin.jobQuests.gui.JQGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JQCommands implements CommandExecutor {
    private final JobQuests jobQuests;

    public JQCommands(JobQuests jobQuests) {
        this.jobQuests = jobQuests;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(commandSender instanceof Player player)) {
                return true;
            }
            JQGui jqGui = new JQGui(jobQuests, player.getUniqueId());
            player.openInventory(jqGui.getInventory());
            return true;
        }

        if(args.length != 1) {
            return true;
        }

        if(args[0].equals("reload")) {
            jobQuests.reloadJobs();
            jobQuests.reloadMessages();
            jobQuests.reloadConfig();
            jobQuests.reloadGuiConfig();
            jobQuests.getMessageManager().sendMessage(commandSender, jobQuests.getMessageConfig().getReload());
        }
        return true;
    }
}
