package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Data.Other.Buff;
import com.gyzer.Data.Other.IntStore;
import com.gyzer.Manager.Other.BuffsManager;
import com.gyzer.Utils.ItemUtils;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

public class BuffsConfigManager extends FileProvider {
    private BuffsManager buffsManager;
    private LinkedHashMap<String, Buff> caches;

    public BuffsConfigManager(BuffsManager buffsManager) {
        super("config.yml", "Buff/", "./plugins/LegendaryGuildRemapped/Buff");
        this.buffsManager = buffsManager;
        buffsManager.setEnable(getValue("enable",true));
    }

    public void readBuffs() {
        caches = new LinkedHashMap<>();
        if (!buffsManager.isEnable()) return;
        int a = 0;
        ConfigurationSection section = getSection("buffs").orElse(null);
        if (section != null){
            for (String id : section.getKeys(false)){
                String display = MsgUtils.color(section.getString(id+".display","未命名的Buff"));
                int max = section.getInt(id+".max",5);
                IntStore<List<String>> requirements = new IntStore<>();
                IntStore<ItemStack> preview = new IntStore<>();
                IntStore<List<String>> attr = new IntStore<>();
                ConfigurationSection upgrades = section.getConfigurationSection(id+".upgrade");
                if (upgrades != null){
                    for (String levelStr : upgrades.getKeys(false)){
                        int level = Integer.parseInt( levelStr);
                        requirements.setValue(level,upgrades.getStringList(levelStr+".requirements"),new ArrayList<>());
                        preview.setValue(level, ItemUtils.readItem(upgrades,levelStr+".preview"),null);
                        attr.setValue(level,upgrades.getStringList(levelStr+".attr"),new ArrayList<>());
                    }
                }
                caches.put(id,new Buff(id,display,max,preview,requirements,attr));
                a++;
            }
        }
        MsgUtils.sendConsole("Load "+a+" Guild Buffs." );
    }

    public LinkedHashMap<String, Buff> getCaches() {
        return caches;
    }

    @Override
    protected void readDefault() {

    }
}
