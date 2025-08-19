package com.gyzer.Commands.PlayerCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DeleteCommand extends LegendaryCommand {
    public DeleteCommand() {
        super("LegendaryGuild.delete", "delete", 2, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            String confirm = args[1];
            if (confirm.equals("confirm")) {
                Player p = (Player) sender;
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                if (!user.hasGuild()) {
                    p.sendMessage(lang.plugin + lang.nothasguild);
                    return;
                }
                if (!user.getPosition().equals(legendaryGuild.getPositionsManager().getOwnerPosition().getId())) {
                    p.sendMessage(lang.plugin + lang.notowner);
                    return;
                }
                Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                legendaryGuild.getGuildsManager().deleteGuild(guild);
                p.sendMessage(lang.plugin+lang.delete_message.replace("%value%",guild.getDisplay()));
                return;
            }
            sender.sendMessage(lang.plugin+lang.delete_confirm);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return  new CommandTabBuilder()
                .addTab(Arrays.asList("confirm"),1,Arrays.asList("delete"),0)
                .build(args);
    }
}
