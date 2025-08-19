package com.gyzer.Manager.Other;

import com.gyzer.LegendaryGuild;
import com.gyzer.Listeners.*;
import org.bukkit.Bukkit;

public class ListenersManager {

    public ListenersManager() {
        Bukkit.getPluginManager().registerEvents(new PlayerClickEvent(), LegendaryGuild.getLegendaryGuild());
        Bukkit.getPluginManager().registerEvents(new PlayerMoveEvent(), LegendaryGuild.getLegendaryGuild());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), LegendaryGuild.getLegendaryGuild());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitEvent(), LegendaryGuild.getLegendaryGuild());
        Bukkit.getPluginManager().registerEvents(new PlayerDamageEvent(), LegendaryGuild.getLegendaryGuild());
        Bukkit.getPluginManager().registerEvents(new NewCycleEvent(), LegendaryGuild.getLegendaryGuild());
    }
}
