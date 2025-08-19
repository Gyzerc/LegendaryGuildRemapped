package com.gyzer.Listeners;

import com.gyzer.API.Events.PlayerDamagedBySameGuildMemberEvent;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageEvent implements Listener {
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private Language lang = legendaryGuild.getLanguageManager();

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e){
        if (e.isCancelled()){return;}
        if (e.getDamager() instanceof Player || e.getDamager() instanceof Arrow){
            Player damager = null;
            if(e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();
            }
            else {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player){
                    damager = (Player) arrow.getShooter();
                }
            }
            if (damager == null ) {
                return;
            }
            User DamageUser = legendaryGuild.getUserManager().getUser(damager.getName());
            if (DamageUser.hasGuild() && e.getEntity() instanceof Player){
                Player p = (Player) e.getEntity();
                User entityUser = legendaryGuild.getUserManager().getUser(p.getName());
                if (entityUser.hasGuild() ) {
                    if (entityUser.getPvp().equals(User.PvpType.NO_SAME_GUILD) && entityUser.getGuild().equals(DamageUser.getGuild())) {
                        e.setCancelled(true);
                        damager.sendMessage(lang.plugin + lang.pvp_cant);
                        Bukkit.getPluginManager().callEvent(new PlayerDamagedBySameGuildMemberEvent(p, damager, true));
                    }
                }
            }
            return;
        }

        if (e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            User user = legendaryGuild.getUserManager().getUser(p.getName());
            if (user.hasGuild()){
                Player damager = null;
                if (e.getDamager() instanceof  Player){
                    damager = (Player) e.getDamager();
                }
                if (e.getDamager() instanceof Arrow){
                    Arrow arrow = (Arrow) e.getDamager();
                    if (arrow.getShooter() instanceof Player){
                        damager = (Player) arrow.getShooter();
                    }
                }
                if (damager != null){
                    User DamagerUser = legendaryGuild.getUserManager().getUser(damager.getName());
                    if (DamagerUser.hasGuild()){
                        //当受伤一方开启公会保护时 且 攻击者是同一个公会的
                        if (user.getPvp().equals(User.PvpType.NO_SAME_GUILD) && DamagerUser.getGuild().equals(user.getGuild())){
                            e.setCancelled(true);
                            damager.sendMessage(lang.plugin+lang.pvp_cant_target);
                            Bukkit.getPluginManager().callEvent(new PlayerDamagedBySameGuildMemberEvent(p,damager,true));
                            return;
                        }
                    }
                }

            }
        }
    }
}
