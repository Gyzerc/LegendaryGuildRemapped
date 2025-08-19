package com.gyzer.Comp.Sub;

import com.gyzer.Comp.Hook;
import com.gyzer.Utils.MsgUtils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerPointsHook extends Hook {
    private PlayerPoints pp;

    @Override
    public boolean getHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")){
            MsgUtils.sendConsole("Hooked PlayerPoints plugin." );
            pp =  PlayerPoints.class.cast(Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints"));
            return true;
        }
        return false;
    }

    public PlayerPoints getPlayerPoints(){
        return pp;
    }

    public void add(UUID uuid,int a) {
        if (!enable)return;
        pp.getAPI().give(uuid,a);
    }
    public void take(UUID uuid,int a) {
        if (!enable)return;
        pp.getAPI().take(uuid,a);
    }
    public void set(UUID uuid,int a) {
        if (!enable)return;
        pp.getAPI().set(uuid,a);
    }
    public int get(UUID uuid) {
        if (!enable)return 0;
        return pp.getAPI().look(uuid);
    }
}
