package com.gyzer.Commands.AdminCommands;

import com.gyzer.Commands.LegendaryCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends LegendaryCommand {
    public ReloadCommand() {
        super("LegendaryGuild.admin", "reload", 2, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        long time = System.currentTimeMillis();
        legendaryGuild.reload();
        sender.sendMessage(lang.plugin+"reloaded!");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
