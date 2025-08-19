package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Manager.Guild.GuildActivityManager;

public class ActivityConfigManager extends FileProvider {
    private GuildActivityManager guildActivityManager;
    public int ACTIVITY_CYCLE;
    public ActivityConfigManager(GuildActivityManager guildActivityManager) {
        super("config.yml", "Activity/", "./plugins/LegendaryGuildRemapped/Activity");
        this.guildActivityManager = guildActivityManager;
        guildActivityManager.setEnable(getValue("enable",true));
        ACTIVITY_CYCLE = getValue("cycle",7);
    }

    @Override
    protected void readDefault() {

    }
}
