package com.gyzer.Listeners;

import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent e){
        Player p =e.getPlayer();
        User user = LegendaryGuild.getLegendaryGuild().getUserManager().getUser(p.getName());

        //更新数据
        LegendaryGuild.getLegendaryGuild().getUserManager().updateUser(user,true);

    }
}
