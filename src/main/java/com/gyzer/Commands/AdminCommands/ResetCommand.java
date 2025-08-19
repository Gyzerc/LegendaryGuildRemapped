package com.gyzer.Commands.AdminCommands;

import com.gyzer.API.GuildAPI;
import com.gyzer.Commands.CommandTabBuilder;
import com.gyzer.Commands.LegendaryCommand;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Guild.Shop.Item.ShopType;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.PlayerShopData;
import com.gyzer.Data.Player.User;
import com.gyzer.Data.Player.WaterDataStore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResetCommand extends LegendaryCommand {
    public ResetCommand( ) {
        super("", "reset", Arrays.asList(5,6), true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String symbol = args[2];
        String id = args[3];
        if (args.length == 5 ) {
            if (symbol.equalsIgnoreCase("guild")) {
                String guildName = args[4];
                switch (id) {
                    case "activity": {
                        Guild guild = legendaryGuild.getGuildsManager().getGuild(guildName);
                        if (guild != null) {
                            GuildActivityData data = legendaryGuild.getGuildActivityManager().getData(guildName);
                            data.setClaimed(new StringStore());
                            data.setPoints(0);
                            data.update();
                            sender.sendMessage(lang.plugin + lang.reset_activity.replace("%guild%", guild.getDisplay()));
                            return;
                        }
                        sender.sendMessage(lang.plugin + lang.notguild);
                        return;
                    }
                    case "teamshop": {
                        Guild guild = legendaryGuild.getGuildsManager().getGuild(guildName);
                        if (guild != null) {
                            GuildAPI.resetGuildTeamShopData(guild);
                            sender.sendMessage(lang.plugin + lang.reset_guild_teamshop.replace("%guild%", guild.getDisplay()));
                            return;
                        }
                        sender.sendMessage(lang.plugin + lang.notguild);
                        return;
                    }
                }
            }
            return;
        }
        if (args.length == 6 && symbol.equalsIgnoreCase("user")) {
            String player = args[5];
            String value = args[4];
            if (getPlayer(player) == null) {
                sender.sendMessage(lang.plugin + lang.notplayer);
                return;
            }
            User user = legendaryGuild.getUserManager().getUser(player);
            switch (id) {
                case "cooldown" : {
                    int sec = Integer.parseInt(value);
                    if( sec >= 0) {
                        user.setCooldown(sec);
                        user.update(false);
                        sender.sendMessage(lang.plugin + lang.reset_user_cooldown.replace("%player%",player).replace("%second%",value));
                    }
                    return;
                }
                case "teamshop" : {
                    Guild guild = legendaryGuild.getGuildsManager().getGuild(player);
                    if (guild != null) {
                        int amount = Integer.parseInt(value);
                        if (amount >= 0) {
                            GuildAPI.resetGuildTeamShopData(guild, player, amount);
                            sender.sendMessage(lang.plugin + lang.reset_user_teamshop.replace("%player%", player));
                        }
                        return;
                    }
                    sender.sendMessage(lang.plugin + lang.nothasguild);
                    return;
                }
                case "shop": {
                    ShopType type = ShopType.valueOf(value.toUpperCase());
                    PlayerShopData shopData = legendaryGuild.getGuildShopManager().getPlayerShopData(player);
                    shopData.clear();
                    shopData.update(false);
                    sender.sendMessage(lang.plugin + lang.reset_shop.replace("%player%",player).replace("%type%",type.name()));
                    return;
                }
                case "tree" : {
                    if (value.equalsIgnoreCase("wish")) {
                        user.setWish(false);
                        user.update(false);
                        sender.sendMessage(lang.plugin + lang.reset_wish.replace("%player%",player));
                        return;
                    }
                    return;
                }
                case "pot" : {
                    if (legendaryGuild.getGuildTreeManager().getWaterPot(value).isPresent()) {
                        WaterDataStore waterDataStore = user.getWaterDataStore();
                         waterDataStore.clearWater(value);
                         user.setWaterDataStore(waterDataStore);
                         user.update(false);
                        sender.sendMessage(lang.plugin + lang.reset_pot.replace("%player%",player).replace("%pot%",value));
                        return;
                    }
                    return;
                }
            }
            return;
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder()
                .addTab(Arrays.asList("guild","user") , 2 , Arrays.asList("reset") , 1)

                .addTab(Arrays.asList("activity","teamshop") , 3 , Arrays.asList("guild") , 2)
                .addTab(legendaryGuild.getGuildsManager().getGuilds() , 4 , Arrays.asList("activity") , 3)
                .addTab(Arrays.asList("0","1","2","10","32","64") , 4 , Arrays.asList("teamshop") , 3)

                .addTab(Arrays.asList("shop","tree","pot","teamshop","cooldown") , 3 , Arrays.asList("user") , 2)

                .addTab(Arrays.asList("day","week","month","once") , 4 , Arrays.asList("shop") , 3)
                .addTab(Arrays.asList("wish") , 4 , Arrays.asList("tree") , 3)
                .addTab(legendaryGuild.getGuildTreeManager().getPots() , 4 , Arrays.asList("pot") , 3)
                .addTab(Arrays.asList("0","60","180","3600","...") , 4 , Arrays.asList("cooldown") , 3)

                .addTab(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), 5 , Arrays.asList("user") , 2)
                .build(args)
                ;
    }
}
