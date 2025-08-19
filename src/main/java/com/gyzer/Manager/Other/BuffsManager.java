package com.gyzer.Manager.Other;

import com.gyzer.Comp.Attribute.AttributePluginProvider;
import com.gyzer.Configurations.Files.BuffsConfigManager;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.Buff;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

public class BuffsManager {

    private boolean enable;
    private BuffsConfigManager buffsConfigManager;
    private AttributePluginProvider provider;
    private AttributePluginProvider.AttributePlugin plugin;

    public BuffsManager() {
        buffsConfigManager = new BuffsConfigManager(this);
        if (enable) {
            this.plugin = AttributePluginProvider.AttributePlugin.valueOf(buffsConfigManager.getValue("plugin","AP3"));
            if (AttributePluginProvider.HookPlugin(plugin,this)) {
                buffsConfigManager.readBuffs();
            } else {
                enable = false;
            }
        }
    }

    public void updatePlayerBuffAttribute(Player p) {
        if (!enable)return;
        if (provider==null)return;
        provider.updateBuff(p);
    }


    public void updateGuildMembersBuff(Guild guild) {
        if (!enable)return;
        if (provider==null)return;
        guild.getMembers().stream().filter(member -> Bukkit.getPlayerExact(member) != null).collect(Collectors.toList()).forEach(p -> {
            provider.updateBuff(Bukkit.getPlayerExact(p));
        });
    }


    public void hook(AttributePluginProvider attributePluginProvider) {
        if (!enable) return;
        provider = attributePluginProvider;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public BuffsConfigManager getBuffsConfigManager() {
        return buffsConfigManager;
    }


    public Optional<Buff> getBuff(String id){
        return buffsConfigManager.getCaches().containsKey(id) ? Optional.of(buffsConfigManager.getCaches().get(id)) : Optional.empty();
    }
    public LinkedList<Buff> getBuffs(){
        return new LinkedList<>(buffsConfigManager.getCaches().values());
    }

}
