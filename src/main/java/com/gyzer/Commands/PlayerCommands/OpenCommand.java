package com.gyzer.Commands.PlayerCommands;

import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Player.User;
import com.gyzer.Menu.Panels.MainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenCommand extends LegendaryCommand {
    public OpenCommand() {
        super("LegendaryGuild.open", "open", 1, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            User user = legendaryGuild.getUserManager().getUser(sender.getName());
            if (!user.hasGuild()){
                sender.sendMessage(lang.plugin+lang.nothasguild);
                return;
            }
            MainMenu guildMenuPanel = new MainMenu((Player) sender);
            guildMenuPanel.open();
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
