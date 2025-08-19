package com.gyzer.Commands.AdminCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Guild.Guild;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TeamShopCommand extends LegendaryCommand {
    public TeamShopCommand( ) {
        super("", "teamshop", 4, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String guildName = args[3];
        Guild guild = legendaryGuild.getGuildsManager().getGuild(guildName);
        if (guild != null) {
            GuildAPI.refreshGuildTeamShopItem(guild);
            sender.sendMessage(lang.plugin + lang.admin_teamshop_refresh.replace("%target%",guild.getDisplay()));
            return;
        }
        sender.sendMessage(lang.plugin + lang.notguild);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(Arrays.asList("refresh") , 2 , Arrays.asList("teamshop") , 1)
                .addTab(legendaryGuild.getGuildsManager().getGuilds() , 3 , Arrays.asList("refresh") , 2)
                .build(args);
    }
}
