package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Manager.Guild.GuildIconsManager;

public class IconsConfigManager extends FileProvider {
    private GuildIconsManager guildIconsManager;
    public IconsConfigManager(GuildIconsManager guildIconsManager) {
        super("config.yml", "Icons/", "./plugins/LegendaryGuildRemapped/Icons");
        this.guildIconsManager = guildIconsManager;
    }

    @Override
    protected void readDefault() {

    }
}
