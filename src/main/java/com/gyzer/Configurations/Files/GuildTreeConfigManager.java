package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Manager.Guild.GuildTreeManager;
import com.gyzer.Utils.ItemUtils;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class GuildTreeConfigManager extends FileProvider {
    private GuildTreeManager guildTreeManager;
    public HashMap<Integer, Double> TREEEXP;
    public HashMap<Integer, List<String>> TREE_REQUIREMENTS;
    public int TREE_BAR_LENGTH;
    public String TREE_BAR_COMPLETED;
    public String TREE_BAR_EMPTRY;
    public int MAX_TREE_LEVEL;
    public HashMap<Integer, List<String>> WISH;
    private GuildTreeIcon treeIcon;
    public GuildTreeConfigManager(GuildTreeManager guildTreeManager) {
        super("config.yml", "Tree/", "./plugins/LegendaryGuildRemapped/Tree");
        this.guildTreeManager = guildTreeManager;
        boolean enable = getValue("enable",true);
        guildTreeManager.setEnable(enable);
        reload();
    }

    public void reload() {
        TREEEXP = new HashMap<>();
        TREE_REQUIREMENTS = new HashMap<>();
        WISH = new HashMap<>();
        //公会神树每级经验
        MAX_TREE_LEVEL = getValue("level.max",5);
        List<Double> doubles = getValue("level.requireExp", Arrays.asList(1000.0,5000.0,10000.0,50000.0,100000.0));
        for (int a = 0 ; a<=MAX_TREE_LEVEL ; a++){
            if (doubles.size() > a) {
                TREEEXP.put(a, doubles.get(a));
            }
            else {
                TREEEXP.put(a,9999999.9);
            }
        }

        //公会经验进度
        TREE_BAR_LENGTH = getValue("level.bar.length",10);
        TREE_BAR_COMPLETED = MsgUtils.color(getValue("level.bar.completed","&a■"));
        TREE_BAR_EMPTRY = MsgUtils.color(getValue("level.bar.empty","&c■"));

        //公会神树升级花费
        ConfigurationSection section = getSection("level.requirements").orElse(null);
        if (section == null){
            legendaryGuild.info("公会神树升级花费配置缺失！插件关闭.", Level.SEVERE);
            Bukkit.getPluginManager().disablePlugin(legendaryGuild);
            return;
        }
        for (String levelStr : section.getKeys(false)){
            TREE_REQUIREMENTS.put(Integer.parseInt(levelStr),getValue("level.requirements."+levelStr,new ArrayList<>()));
        }

        //公会神树许愿奖励
        section = getSection("level.wish").orElse(null);
        if (section == null){
            legendaryGuild.info("公会神树许愿配置缺失！插件关闭.",Level.SEVERE);
            Bukkit.getPluginManager().disablePlugin(legendaryGuild);
            return;
        }
        for (String levelStr : section.getKeys(false)){
            WISH.put(Integer.parseInt(levelStr),getValue("level.wish."+levelStr+".run",new ArrayList<>()));
        }

        HashMap<Integer, ItemStack> item = new HashMap<>();
        section = getSection("tree").orElse(null);
        if (section != null){
            for (String levelStr : section.getKeys(false)){
                item.put(Integer.parseInt(levelStr), ItemUtils.readItem(section,levelStr));
            }
        }
        treeIcon = new GuildTreeIcon(item);

    }

    @Override
    protected void readDefault() {

    }

    public GuildTreeIcon getTreeIcon() {
        return treeIcon;
    }

    public class GuildTreeIcon {

        HashMap<Integer, ItemStack> item;

        public GuildTreeIcon(HashMap<Integer, ItemStack> item) {
            this.item = item;
        }

        public ItemStack getPreviewItem(int level) {
            return item.containsKey(level) ? item.get(level) : new ItemStack(Material.PAPER);
        }
    }
}
