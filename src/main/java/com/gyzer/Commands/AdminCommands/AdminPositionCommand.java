package com.gyzer.Commands.AdminCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AdminPositionCommand extends com.gyzer.Commands.LegendaryCommand {
    public AdminPositionCommand( ) {
        super("", "position", 4, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String player = args[2];
        if (getPlayer(player) == null){
            sender.sendMessage(lang.plugin+lang.notplayer);
            return;
        }
        Player p = Bukkit.getPlayerExact(player);
        User user = legendaryGuild.getUserManager().getUser(player);
        if (user.hasGuild()) {
            Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
            String id = args[3];
            Position position = legendaryGuild.getPositionsManager().getPosition(id).orElse(null);
            if (position != null) {
                if (id.equals(legendaryGuild.getPositionsManager().getOwnerPosition().getId())) {

                    guild.setOwner(player);
                    guild.update();

                    User oldOwner = legendaryGuild.getUserManager().getUser(guild.getOwner());
                    oldOwner.setPosition(legendaryGuild.getPositionsManager().getDefaultPosition().getId());
                    oldOwner.update(false);

                    user.setPosition(id);
                    user.update(false);

                } else {
                    user.setPosition(id);
                    user.update(false);
                }
                sender.sendMessage(lang.plugin + lang.admin_position.replace("%target%",player).replace("%value%",position.getDisplay()));
            }
            return;
        }
        sender.sendMessage(lang.plugin + lang.nothasguild);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(getOnlines() , 2 , Arrays.asList("position") , 1)
                .addTab(legendaryGuild.getPositionsManager().getPositionIds(), 3, Arrays.asList("position"),1)
                .build(args);
    }
}
