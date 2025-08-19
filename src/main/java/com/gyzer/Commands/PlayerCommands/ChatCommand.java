package com.gyzer.Commands.PlayerCommands;

import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Player.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatCommand extends LegendaryCommand {
    public ChatCommand() {
        super("LegendaryGuild.chat", "chat", 1, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            User user = legendaryGuild.getUserManager().getUser(sender.getName());
            if (!user.hasGuild()) {
                sender.sendMessage(lang.plugin+lang.nothasguild);
                return;
            }
            if (user.isChat()) {
                user.setChat(false);
                sender.sendMessage(lang.plugin+lang.chat_disable);
                return;
            }
            user.setChat(true);
            sender.sendMessage(lang.plugin+lang.chat_enable);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
