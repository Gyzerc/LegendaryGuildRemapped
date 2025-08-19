package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Manager.Guild.GuildRedpacketManager;

public class GuildRedpacketConfigManager extends FileProvider {
    private GuildRedpacketManager guildRedpacketManager;
    public double MIN_REDPACKET_TOTAL;
    public int MIN_REDPACKET_AMOUNT;

    public GuildRedpacketConfigManager(GuildRedpacketManager guildRedpacketManager) {
        super("config.yml", "RedPacket/", "./plugins/LegendaryGuildRemapped/RedPacket");
        this.guildRedpacketManager = guildRedpacketManager;
        guildRedpacketManager.setEnable(getValue("enable",true));
        MIN_REDPACKET_AMOUNT = getValue("redpacket.min_amount",2);
        MIN_REDPACKET_TOTAL = getValue("redpacket。min_total",100.0);
    }

    @Override
    protected void readDefault() {

    }
}
