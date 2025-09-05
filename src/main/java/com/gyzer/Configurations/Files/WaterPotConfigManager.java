package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Data.Other.WaterPot;
import com.gyzer.Manager.Guild.GuildTreeManager;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;

public class WaterPotConfigManager extends FileProvider {
    private GuildTreeManager guildTreeManager;
    private HashMap<String, WaterPot> pots = new HashMap<>();

    public WaterPotConfigManager(GuildTreeManager guildTreeManager) {
        super("WaterPots.yml", "Tree/", "./plugins/LegendaryGuildRemapped/Tree");
        this.guildTreeManager = guildTreeManager;
        if (guildTreeManager.isEnable()) {
            loadPots();
        }
    }

    public HashMap<String, WaterPot> getPots() {
        return pots;
    }

    @Override
    protected void readDefault() {

    }

    public void loadPots() {
        reloadFile();
        pots = new HashMap<>();
        int a = 0;
        ConfigurationSection section = getSection("pots").orElse(null);
        if (section != null) {
            for (String id : section.getKeys(false)){
                String display = MsgUtils.color(section.getString(id+".display","&f水壶"));
                List<String> requirements = section.getStringList(id+".requirements");
                List<String> runs = section.getStringList(id+".run");
                double addExp = section.getDouble(id+".addExp",1);
                double addPoints = section.getDouble(id+".addPoints",1);
                int limit = section.getInt(id+".day",-1);
                pots.put(id,new WaterPot(id,display, addExp,addPoints,limit, requirements,runs));
                a++;
            }
        }
        MsgUtils.sendConsole("Load "+a+" Guild Tree Pots." );

    }
}
