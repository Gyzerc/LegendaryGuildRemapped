package com.gyzer.Commands.PlayerCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MoneyCommand extends LegendaryCommand {
    public MoneyCommand() {
        super("LegendaryGuild.money", "money", 2, false);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            String amountStr = args[1];
            if (!checkIsNumber(amountStr)){
                sender.sendMessage(lang.plugin+lang.notmath);
                return;
            }
            int amount = Integer.parseInt(amountStr);
            Player p = (Player) sender;
            GuildAPI.giveMoney(p,amount);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(Arrays.asList("数量 (Amount)"),1,Arrays.asList("money"),0)
                .build(args);
    }
}
