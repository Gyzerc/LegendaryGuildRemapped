package com.gyzer.Commands.AdminCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AddCommand extends com.gyzer.Commands.LegendaryCommand {
    public AddCommand( ) {
        super("", "add", 4, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String guildName = args[3];
        String player = args[2];
        if (getPlayer(player) == null){
            sender.sendMessage(lang.plugin+lang.notplayer);
            return;
        }
        Player p = Bukkit.getPlayerExact(player);
        User user = legendaryGuild.getUserManager().getUser(player);
        if (user.hasGuild()) {
            p.sendMessage(lang.plugin+lang.already_in_guild);
            return;
        }

        if (!legendaryGuild.getGuildsManager().isExists(guildName)){
            sender.sendMessage(lang.plugin+lang.notguild);
            return;
        }

        Guild guild = legendaryGuild.getGuildsManager().getGuild(guildName);
        GuildAPI.JoinGuild(user,guild);
        p.sendMessage(lang.plugin + lang.admin_add_member.replace("%target%" , player).replace("%value%",guildName));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(getOnlines() , 2 , Arrays.asList("add") , 1)
                .addTab(legendaryGuild.getGuildsManager().getGuilds(),3, Arrays.asList("add"),1)
                .build(args);
    }
}
