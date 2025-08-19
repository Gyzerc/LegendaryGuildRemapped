package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Manager.Other.TributesManager;

public class TributesConfigManager extends FileProvider {
    private TributesManager tributesManager;
    public TributesConfigManager(TributesManager tributesManager) {
        super("config.yml", "Tributes/", "./plugins/LegendaryGuildRemapped/Tributes");
        this.tributesManager = tributesManager;
        tributesManager.setEnable(getValue("enable",true));
    }

    @Override
    protected void readDefault() {

    }
}
