package com.gyzer.Manager.Guild;

import com.gyzer.Configurations.Files.IconsConfigManager;
import com.gyzer.Data.Guild.GuildIcon;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Level;

public class GuildIconsManager {
    private LinkedHashMap<String, GuildIcon> cache;
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private IconsConfigManager iconsConfigManager;
    public GuildIconsManager() {
        this.cache = new LinkedHashMap<>();
        iconsConfigManager = new IconsConfigManager(this);
        readIcons();
    }

    private void readIcons() {
       ConfigurationSection section = iconsConfigManager.getSection("icons").orElse(null);
       int a = 0;
       if (section != null){
            for (String iconId : section.getKeys(false)){
                Material material = Material.getMaterial(section.getString(iconId+".material","APPLE")) != null ? Material.getMaterial(section.getString(iconId+".material","APPLE")) : Material.APPLE;
                String display = MsgUtils.color(section.getString(iconId+".display","&f图标"));
                int data = section.getInt(iconId+".data",0);
                int model = section.getInt(iconId+".model",0);
                List<String> description = MsgUtils.color(section.getStringList(iconId+".description"));
                List<String> requirements = MsgUtils.color(section.getStringList(iconId+".requirements"));

                cache.put(iconId,new GuildIcon(iconId,display,material,data,model,description,requirements));
                a ++ ;
            }
        }
        MsgUtils.sendConsole("Load "+a+" Guild Icons." );
    }

    public Optional<GuildIcon> getIcon(String iconId){
        if (iconId == null) {return Optional.empty();}
        return cache.containsKey(iconId) ? Optional.of(cache.get(iconId)) : Optional.empty();
    }

    public LinkedList<GuildIcon> getIcons(){
        return new LinkedList<>(cache.values());
    }
}
