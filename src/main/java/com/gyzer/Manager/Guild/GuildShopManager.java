package com.gyzer.Manager.Guild;

import com.gyzer.Configurations.Files.GuildShopConfigManager;
import com.gyzer.Data.Guild.Shop.Item.ShopItem;
import com.gyzer.Data.Player.PlayerShopData;
import com.gyzer.LegendaryGuild;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

public class GuildShopManager {
    private boolean enable;
    private HashMap<String , PlayerShopData> caches;
    private GuildShopConfigManager guildShopConfigManager;
    public GuildShopManager() {
        caches = new HashMap<>();
        loadConfigs();
        if (enable) {
            guildShopConfigManager.loadItems();
        }
    }
    public void reloadCache() {
        caches = new HashMap<>();
    }
    public LinkedList<ShopItem> getItems(){
        return new LinkedList<>(guildShopConfigManager.getCache().values());
    }
    public void loadConfigs() {
        guildShopConfigManager = new GuildShopConfigManager(this);
    }

    public PlayerShopData getPlayerShopData(String name) {
        PlayerShopData data = caches.get(name);
        if (data == null) {
            data = LegendaryGuild.getLegendaryGuild().getDatabaseManager().getShopData(name).orElse(new PlayerShopData(name,null,new HashMap<>()));
            caches.put(name,data);
        }
        return data;
    }

    public void update(PlayerShopData data,boolean r) {
        LegendaryGuild.getLegendaryGuild().getDatabaseManager().saveShopData(data);
        if (r) {
            caches.remove(data.getName());
            return;
        }
        caches.put(data.getName(),data);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public GuildShopConfigManager getGuildShopConfigManager() {
        return guildShopConfigManager;
    }

    public ShopItem getShopItem(String id) {
        return getGuildShopConfigManager().getCache().get(id);
    }
    public void reloadData(String value) {
        caches.remove(value);
    }

}
