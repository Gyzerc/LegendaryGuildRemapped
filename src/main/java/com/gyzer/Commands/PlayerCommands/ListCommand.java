package com.gyzer.Commands.PlayerCommands;

import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Menu.Panels.ListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommand extends LegendaryCommand {
    public ListCommand() {
        super("LegendaryGuild.list", "list", 1, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            ListMenu listPanel = new ListMenu((Player) sender,1, ListMenu.Sort.DEFAULT);
            listPanel.open();
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
