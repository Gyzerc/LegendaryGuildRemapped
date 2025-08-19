package com.gyzer.Commands.PlayerCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Player.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CreateGuildCommand extends LegendaryCommand {
    public CreateGuildCommand() {
        super("legendaryguild.create", "create", 2, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            String guildName = args[1];
            User user = legendaryGuild.getUserManager().getUser(sender.getName());
            GuildAPI.createGuild((Player) sender,guildName);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(Arrays.asList("公会名称 (Guild Name)"),1,Arrays.asList("create"),0)
                .build(args);
    }
}
