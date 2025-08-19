package com.gyzer.Utils.BungeeCord;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class NetWorkHandle extends NetWork implements PluginMessageListener {
    @Override
    public void initNetwork() {
        if (legendaryGuild.getConfigManager().CROSS_SERVER){
            Bukkit.getMessenger().registerIncomingPluginChannel(legendaryGuild, "BungeeCord", this);
            Bukkit.getMessenger().registerOutgoingPluginChannel(legendaryGuild, "BungeeCord");
            legendaryGuild.info("成功启用跨服同步功能 & Enabled cross server synchronization function", Level.INFO);
            setEnable(true);
        }
    }

    @Override
    public void disable() {
        if (legendaryGuild.getConfigManager().CROSS_SERVER){
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(legendaryGuild);
            Bukkit.getMessenger().unregisterIncomingPluginChannel(legendaryGuild);
            legendaryGuild.info("关闭跨服同步功能 & Closed cross server synchronization function", Level.INFO);
        }
    }

    @Override
    public void handle(NetWorkMessage.NetWorkType type, String value) {
        switch (type){
            case UPDATE_USER: {
                legendaryGuild.getUserManager().reloadUserDataIfCached(value);
                break;
            }
            case UPDATE_GUILD: {
                legendaryGuild.getGuildsManager().update(value);
                break;
            }
            case REMOVE_GUILD: {
                legendaryGuild.getGuildsManager().removeGuildCache(value);
                break;
            }
            case UPDATE_GUILD_SHOP:
                legendaryGuild.getGuildShopManager().reloadData(value);
                break;
            case UPDATE_REDPACKEY: {
                legendaryGuild.getGuildRedpacketManager().reloadRedPacket(value);
                break;
            }
            case UPDATE_GUILD_BUFF: {
                Guild guild = legendaryGuild.getGuildsManager().getGuild(value);
                if (guild != null) {
                    GuildAPI.updateGuildMembersBuff(guild,false);
                }
                break;
            }
            case UPDATE_PLAYER_BUFF: {
                GuildAPI.updatePlayerBuffAttribute(value,false);
                break;
            }

            case UPDATE_GUILD_ACTIVITY_DATA:
                legendaryGuild.getGuildActivityManager().reloadGuildIfCached(value);
                break;
            case REFRESH_ACTIVITY:
                legendaryGuild.getGuildActivityManager().removeAll();
                break;
            case UPDATE_TEAMSHOPDATA:
                legendaryGuild.getTeamShopManager().removeIfCached(value);
                break;

        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes) {
        recive(s,player,bytes);
    }
}
