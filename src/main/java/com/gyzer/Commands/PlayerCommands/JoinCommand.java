package com.gyzer.Commands.PlayerCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Player.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class JoinCommand extends LegendaryCommand {
    public JoinCommand() {
        super("LegendaryGuild.join", "join", 2, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            String guildName = args[1];
            GuildAPI.sendApplication(legendaryGuild.getUserManager().getUser(sender.getName()),guildName);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(legendaryGuild.getGuildsManager().getGuilds(),1,Arrays.asList("join"),0)
                .build(args);
    }
}
