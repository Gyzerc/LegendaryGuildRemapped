package com.gyzer.Commands.PlayerCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositionCommand extends LegendaryCommand {
    public PositionCommand() {
        super("legendaryguild.position", "position", 4, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase("set")) {
            String player = args[2];
            String id = args[3];
            if (getPlayer(player) == null){
                sender.sendMessage(lang.plugin+lang.notplayer);
                return;
            }
            GuildAPI.setPlayerPositionByPlayer((Player) sender,player,id);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(Arrays.asList("set"),1,Arrays.asList("position"),0)
                .addTab(Arrays.asList("成员ID (Member name)"),2,Arrays.asList("set"),1)
                .addTab(legendaryGuild.getPositionsManager().getPositionIds().stream().filter(id ->  !id.equalsIgnoreCase(legendaryGuild.getPositionsManager().getOwnerPosition().getId())).collect(Collectors.toList()), 3,Arrays.asList("set"),1)
                .build(args);
    }
}
