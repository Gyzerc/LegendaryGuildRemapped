package com.gyzer.Commands.AdminCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Data.Player.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AdminKickCommand extends com.gyzer.Commands.LegendaryCommand {
    public AdminKickCommand( ) {
        super("", "kick", 3, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String player = args[2];
        if (getPlayer(player) == null){
            sender.sendMessage(lang.plugin+lang.notplayer);
            return;
        }
        User user = legendaryGuild.getUserManager().getUser(player);
        if (user.hasGuild()) {
            GuildAPI.kick(legendaryGuild.getGuildsManager().getGuild(user.getGuild()), sender , player);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(getOnlines(),2, Arrays.asList("kick"),1)
                .build(args);
    }
}
