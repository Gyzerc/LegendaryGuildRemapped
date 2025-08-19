package com.gyzer.Manager.Other;

import com.gyzer.Configurations.Files.TeamShopConfigManager;
import com.gyzer.Data.Guild.TeamShop.GuildTeamShopData;
import com.gyzer.Data.Guild.TeamShop.TeamShopItem;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.ItemUtils;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TeamShopManager {
    private boolean enable;
    private TeamShopConfigManager teamShopConfigManager;
    private HashMap<String, TeamShopItem> items;
    private HashMap<String, GuildTeamShopData> caches;

    public TeamShopManager() {
        caches = new HashMap<>();
        teamShopConfigManager = new TeamShopConfigManager(this);
        loadTeamShopItems();

    }

    public void loadTeamShopItems() {
        items = new HashMap<>();
        if (!enable) return;
        ConfigurationSection section = teamShopConfigManager.getSection("shops").orElse(null);
        if (section != null) {
            for (String id : section.getKeys(false)) {
                String display = MsgUtils.color(section.getString(id+".display","公会团购礼包"));
                double base = section.getDouble(id+".price.base");
                double min = section.getDouble(id+".price.min",0);
                int limit = section.getInt(id+".limit",-1);
                TeamShopItem.TeamShopItemCurrency currency = TeamShopItem.TeamShopItemCurrency.valueOf(section.getString(id+".currency","PLAYERPOINTS").toUpperCase());
                ItemStack preview = ItemUtils.readItem(section,id+".preview");
                List<String> run = section.getStringList(id+".run");
                items.put(id,new TeamShopItem(id,display,base,min,limit,currency,preview,run));

            }
        }
        MsgUtils.sendConsole("Load "+items.size() +" Team Shop Items." );
    }

    public TeamShopConfigManager getTeamShopConfigManager() {
        return teamShopConfigManager;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public TeamShopItem getShopItem(String id) {
        return items.get(id);
    }
    public List<String> getShopItems() {
        return items.keySet().stream().collect(Collectors.toList());
    }


    public GuildTeamShopData getGuildTeamShopData(String guild) {
        GuildTeamShopData data = caches.get(guild);
        if (data == null) {
            data = LegendaryGuild.getLegendaryGuild().getDatabaseManager().getGuildTeamShopData(guild).orElse(new GuildTeamShopData(guild,"",0,new HashMap<>(),new HashMap<>()));
            caches.put(guild,data);
        }
        return data;
    }
    public void removeIfCached(String guild) {
        caches.remove(guild);
    }
    public void update(GuildTeamShopData data,boolean r) {
        LegendaryGuild.getLegendaryGuild().getDatabaseManager().setGuildTeamShopData(data);
        if (r) {
            caches.remove(data.getGuild());
            return;
        }
        caches.put(data.getGuild(),data);
    }

    public void reloadCache() {
        caches = new HashMap<>();
    }
}
