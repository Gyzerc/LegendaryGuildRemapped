package com.gyzer.Data.Player;

import com.google.common.collect.Iterables;
import com.gyzer.Data.Guild.Shop.Item.ShopItem;
import com.gyzer.Data.Guild.Shop.Item.ShopType;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.BungeeCord.NetWorkMessage;
import com.gyzer.Utils.BungeeCord.NetWorkMessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerShopData {
    private String name;
    private HashMap<DateType , Integer> lastDate;
    private HashMap<String,Integer> buy;
    public PlayerShopData(String name, HashMap<DateType, Integer> lastDate, HashMap<String, Integer> buy) {
        this.name = name;
        if (lastDate == null) {
            this.lastDate = new HashMap<>();
            Calendar calendar = Calendar.getInstance();
            this.lastDate.put(DateType.DATE , calendar.get(Calendar.DATE));
            this.lastDate.put(DateType.WEEK , calendar.get(Calendar.WEEK_OF_MONTH));
            this.lastDate.put(DateType.MONTH , calendar.get(Calendar.MONTH));
        } else {
            this.lastDate = lastDate;
        }
        this.buy = buy;
    }



    public String getName() {
        return name;
    }

    public int getLastDate(DateType type) {
        return lastDate.getOrDefault(type,0);
    }

    public void setLastDate(DateType type,int a) {
        lastDate.put(type,a);
    }

    public int getBuyAmount(String id) {
        return buy.getOrDefault(id,0);
    }

    public void addBuyAmount(String id,int amount) {
        buy.put(id , (getBuyAmount(id) + amount));
    }

    public void setBuyAmount(String id,int amount) {
        buy.put(id,amount);
    }

    public HashMap<String, Integer> getBuy() {
        return buy;
    }
    public void update(boolean r) {
        LegendaryGuild.getLegendaryGuild().getGuildShopManager().update(this,r);
        if (LegendaryGuild.getLegendaryGuild().getNetWork().isEnable()) {
            Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(),null);
            if (p != null) {
                new NetWorkMessageBuilder()
                        .setReciver("ALL")
                        .setNetWorkMessage(new NetWorkMessage(NetWorkMessage.NetWorkType.UPDATE_GUILD_SHOP, this.name))
                        .setMessageType(NetWorkMessageBuilder.MessageType.Forward)
                        .sendPluginMessage(p);
            }
        }
    }


    public void clear() {
        buy.clear();
    }

    public void refresh(ShopType type) {

        List<ShopItem> items = LegendaryGuild.getLegendaryGuild().getGuildShopManager().getItems().stream().filter(shopItem -> shopItem.getType().equals(type)).collect(Collectors.toList());
        if (items != null && !items.isEmpty()) {
            for (ShopItem shopItem : items) {
                buy.remove(shopItem.getId());
            }
        }
    }


    public static enum DateType {
        DATE,
        WEEK,
        MONTH;
    }
}
