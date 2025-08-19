package com.gyzer.Manager.Guild;

import com.gyzer.Configurations.Files.ActivityConfigManager;
import com.gyzer.Data.Guild.ActivityReward;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class GuildActivityManager {
    private boolean enable;
    private HashMap<String, GuildActivityData> cache;
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private ActivityConfigManager activityConfigManager;
    private HashMap<String, ActivityReward> rewards;

    public GuildActivityManager() {
        cache = new HashMap<>();
        rewards = new HashMap<>();
        activityConfigManager = new ActivityConfigManager(this);
        if (enable) {
            loadRewards();
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public void removeAll(){
        cache = new HashMap<>();
    }
    public GuildActivityData getData(String guild){
        GuildActivityData data = cache.get(guild);
        if (data == null){
            data = legendaryGuild.getDatabaseManager().getGuildActivityData(guild).orElse(new GuildActivityData(guild));
        }
        cache.put(guild,data);
        return data;
    }

    public void reloadGuildIfCached(String guild){
        if (!enable) return;
        if (cache.containsKey(guild)) {
            cache.put(guild, legendaryGuild.getDatabaseManager().getGuildActivityData(guild).orElse(new GuildActivityData(guild)));
        }
    }
    public void updataGuildActivityData(GuildActivityData data,boolean removeCache){
        if (!enable) return;
        legendaryGuild.getDatabaseManager().saveGuildActivityData(data);

        if (removeCache){
            cache.remove(data.getGuild());
            return;
        }
        cache.put(data.getGuild(),data);
    }


    public void checkCycle(){
        if (!enable) return;
        int targetInt = activityConfigManager.ACTIVITY_CYCLE;
        if (targetInt > 0) {
            int value = Integer.parseInt(legendaryGuild.getDatabaseManager().getSystemData("activity_day").orElse("0"));
            int set = value + 1;
            if (value >= (targetInt - 1)) {
                set = 0;
                legendaryGuild.info("刷新所有公会活跃度", Level.INFO);
                legendaryGuild.info("Refresh all guild activity levels", Level.INFO);
                resetAllGuild();
            }
            legendaryGuild.getDatabaseManager().saveSystemData("activity_day", set + "");
        }
    }

    private void resetAllGuild(){
        if (!enable) return;
        legendaryGuild.sync(new Runnable() {
            @Override
            public void run() {
                for (String guild : legendaryGuild.getDatabaseManager().getGuildActivityDatas()){
                    GuildActivityData data = getData(guild);
                    data.setPoints(0);
                    data.setCurrent(new HashMap<>());
                    data.setClaimed(new StringStore());
                    data.update();
                }
            }
        });

    }


    public void loadRewards() {
        int a = 0;
        ConfigurationSection configurationSection = activityConfigManager.getSection("rewards").orElse(null);
        if (configurationSection != null) {
            for (String id : configurationSection.getKeys(false)) {
                String display = MsgUtils.color(configurationSection.getString(id + ".display", ""));
                double points = configurationSection.getDouble(id + ".activity", 1000);
                List<String> run = configurationSection.getStringList(id + ".run");
                rewards.put(id, new ActivityReward(id, display, points, run));
                a++;
            }
        }
        MsgUtils.sendConsole("Load " + a + " Activity Rewards.");
    }

    public Optional<ActivityReward> getReward(String id){
        return rewards.containsKey(id) ? Optional.of(rewards.get(id)) : Optional.empty();
    }

    public void reloadCache() {
        cache = new HashMap<>();
    }
}
