package com.gyzer.Commands.PlayerCommands;

import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Player.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PvpCommand extends LegendaryCommand {
    public PvpCommand() {
        super("LegendaryGuild.pvp", "pvp", 1, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            User user = legendaryGuild.getUserManager().getUser(sender.getName());
            if (user.hasGuild()){
                switch (user.getPvp()){
                    case ALL:
                        user.setPvp(User.PvpType.NO_SAME_GUILD);
                        user.update(false);

                        sender.sendMessage(lang.plugin+lang.pvp_enable);
                        break;
                    default:
                        user.setPvp(User.PvpType.ALL);
                        user.update(false);

                        sender.sendMessage(lang.plugin+lang.pvp_disable);
                        break;
                }
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
