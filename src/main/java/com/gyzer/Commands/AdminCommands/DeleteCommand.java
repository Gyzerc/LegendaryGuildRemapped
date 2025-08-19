package com.gyzer.Commands.AdminCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Guild.Guild;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class DeleteCommand extends LegendaryCommand {
    public DeleteCommand() {
        super("LegendaryGuild.admin", "delete", 3, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String name = args[2];
        if (!legendaryGuild.getGuildsManager().isExists(name)){
            sender.sendMessage(lang.plugin+lang.notguild);
            return;
        }
        Guild guild = legendaryGuild.getGuildsManager().getGuild(name);
        legendaryGuild.getGuildsManager().deleteGuild(guild);
        sender.sendMessage(lang.plugin+lang.delete_message.replace("%value%",name));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(legendaryGuild.getGuildsManager().getGuilds(),2,Arrays.asList("delete"),1)
                .build(args);
    }
}
