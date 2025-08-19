package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Data.Guild.TeamShop.GuildTeamShopData;
import com.gyzer.Manager.Other.TeamShopManager;

public class TeamShopConfigManager extends FileProvider {
    private TeamShopManager teamShopManager;
    public GuildTeamShopData.BargainMode bargainMode;

    public TeamShopConfigManager(TeamShopManager teamShopManager) {
        super("config.yml", "TeamShop/", "./plugins/LegendaryGuildRemapped/TeamShop");
        this.teamShopManager = teamShopManager;
        teamShopManager.setEnable(getValue("enable",true));

        bargainMode = GuildTeamShopData.BargainMode.valueOf(getValue("bargain.mode","BASE_ON_MAXMEMBER").toUpperCase());
    }

    @Override
    protected void readDefault() {

    }
}
