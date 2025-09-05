package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Data.Guild.Shop.Item.ShopItem;
import com.gyzer.Data.Guild.Shop.Item.ShopType;
import com.gyzer.Manager.Guild.GuildShopManager;
import com.gyzer.Utils.ItemUtils;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

public class GuildShopConfigManager extends FileProvider {
    private GuildShopManager guildShopManager;
    private LinkedHashMap<String, ShopItem> cache;

    public GuildShopConfigManager( GuildShopManager guildShopManager) {
        super("config.yml", "Shop/", "./plugins/LegendaryGuildRemapped/Shop");
        this.guildShopManager = guildShopManager;
        guildShopManager.setEnable(getValue("enable",true));
        cache = new LinkedHashMap<>();
    }

    public void loadItems() {
        reloadFile();
        int a = 0;
        ConfigurationSection section = getSection("items").orElse(null);
        if (section != null){
            for (String shopId : section.getKeys(false)){
                ItemStack i = ItemUtils.readItem(section,shopId);
                List<String> requirements = MsgUtils.color(section.getStringList(shopId+".requirements"));
                List<String> runs = MsgUtils.color(section.getStringList(shopId+".run"));
                ShopType type = getType(section.getString(shopId+".buy_limit.type","unlimited"));
                int limit = section.getInt(shopId+".buy_limit.amount",-1);
                cache.put(shopId,new ShopItem(shopId,i,type,limit,requirements,runs));
                a++;
            }
        }
        MsgUtils.sendConsole("Load "+a +" Guild Shop items." );
    }
    @Override
    protected void readDefault() {

    }

    private ShopType getType(String type){
        try {
            return ShopType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            legendaryGuild.info("商品限购类型配置出错，已默认为无限购商品 -> "+type,Level.SEVERE);
            legendaryGuild.info("Product purchase restriction type configuration error, defaulted to unlimited purchase product",Level.SEVERE);
            return ShopType.UNLIMITED;
        }
    }

    public LinkedHashMap<String, ShopItem> getCache() {
        return cache;
    }
}
