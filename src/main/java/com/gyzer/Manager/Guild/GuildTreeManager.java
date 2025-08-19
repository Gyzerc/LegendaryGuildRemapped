package com.gyzer.Manager.Guild;

import com.gyzer.Configurations.Files.GuildTreeConfigManager;
import com.gyzer.Configurations.Files.WaterPotConfigManager;
import com.gyzer.Data.Other.WaterPot;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuildTreeManager {
    private boolean enable;
    private GuildTreeConfigManager guildTreeConfigManager;
    private WaterPotConfigManager waterPotConfigManager;

    public GuildTreeManager() {
        guildTreeConfigManager = new GuildTreeConfigManager(this);
        waterPotConfigManager = new WaterPotConfigManager(this);
    }

    public void loadWaterPots() {
        waterPotConfigManager.loadPots();
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public GuildTreeConfigManager getGuildTreeConfigManager() {
        return guildTreeConfigManager;

    }

    public Optional<WaterPot> getWaterPot(String id) {
        return waterPotConfigManager.getPots().containsKey(id) ? Optional.of(waterPotConfigManager.getPots().get(id)) : Optional.empty();
    }
    public List<String> getPots() {
        return waterPotConfigManager.getPots().keySet().stream().collect(Collectors.toList());
    }

    public WaterPotConfigManager getWaterPotConfigManager() {
        return waterPotConfigManager;
    }
}
