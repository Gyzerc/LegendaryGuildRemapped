package com.gyzer.Comp.Sub;

import com.gyzer.Comp.Hook;
import com.gyzer.Utils.MsgUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.logging.Level;

public class PlaceholderAPIHook extends Hook {

    public String replaceHolder(String str, OfflinePlayer offlinePlayer){
        if (!enable){return str;}
        return PlaceholderAPI.setPlaceholders(offlinePlayer, str);
    }

    public List<String> replaceHolder(List<String> list,OfflinePlayer offlinePlayer) {
        if (!enable){return list;}
        return PlaceholderAPI.setPlaceholders(offlinePlayer,list);
    }
    @Override
    public boolean getHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            MsgUtils.sendConsole("Hooked PlaceholderAPI plugin." );
            return true;
        }
        return false;
    }
}
