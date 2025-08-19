package com.gyzer.Listeners;

import com.gyzer.API.Events.PlayerNewCycleEvent;
import com.gyzer.API.GuildAPI;
import com.gyzer.Configurations.Config;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.PlayerShopData;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Calendar;

public class PlayerJoinEvent implements Listener {
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private Config config = legendaryGuild.getConfigManager();
    private Language lang = legendaryGuild.getLanguageManager();
    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent e){

        Player p=e.getPlayer();

        //防止跨服模式下数据未及时更新
        if (legendaryGuild.getNetWork().isEnable()) {
            if (Bukkit.getOnlinePlayers().size() == 1) {
                legendaryGuild.reloadData();
            }
        }

        //初始化数据
        legendaryGuild.getUserManager().reloadUserDataIfCached(p.getName());


        //检测周期
        checkDate(p);

        User user = legendaryGuild.getUserManager().getUser(p.getName());
        if (user.hasGuild()){
            Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());

            //检测是否是会长已经发送入会申请待处理消息
            if (p.getName().equals(guild.getOwner())){
                 if (guild.getApplications().size() > 0){
                    legendaryGuild.sync(new Runnable() {
                        @Override
                        public void run() {
                            p.sendMessage(lang.plugin+lang.application_wait.replace("%value%",""+guild.getApplications().size()));
                        }
                    },100);
                }
            }

            //检测是否是要传送至驻地
            legendaryGuild.sync(()->{
                if (user.isTeleport_guild_home()){
                    if (guild != null && guild.getHome()!=null){
                        if (guild.getHome().getServer().equals(legendaryGuild.SERVER)) {
                            user.setTeleport_guild_home(false);
                            user.update(false);

                            Location location = guild.getHome().getLocation().orElse(null);
                            if (location != null) {
                                legendaryGuild.async(()->p.teleport(location));
                                if (config.HOME_SOUND_TELEPORT != null) {
                                    p.playSound(p.getLocation(), config.HOME_SOUND_TELEPORT, 1, 1);
                                }
                                p.sendMessage(lang.plugin + lang.home_teleport);
                            }
                        }
                    }
                }
            },20);
        }

        //刷新公会buff属性
        GuildAPI.updatePlayerBuffAttribute(p);
    }

    private void checkDate(Player p) {
        PlayerShopData data = legendaryGuild.getGuildShopManager().getPlayerShopData(p.getName());
        Calendar calendar = Calendar.getInstance();
        boolean change = false;

        int today = calendar.get(Calendar.DATE);
        int thisWeek = calendar.get(Calendar.WEEK_OF_MONTH);
        int thisMonth = calendar.get(Calendar.MONTH);

        if (today != data.getLastDate(PlayerShopData.DateType.DATE)) {
            Bukkit.getPluginManager().callEvent(new PlayerNewCycleEvent(p,0,data.getLastDate(PlayerShopData.DateType.DATE),today));
            data.setLastDate(PlayerShopData.DateType.DATE,today);
            change = true;
        }
        if (thisWeek != data.getLastDate(PlayerShopData.DateType.WEEK)) {
            Bukkit.getPluginManager().callEvent(new PlayerNewCycleEvent(p,1,data.getLastDate(PlayerShopData.DateType.WEEK),thisWeek));
            data.setLastDate(PlayerShopData.DateType.WEEK,thisWeek);
            change = true;
        }
        if (thisMonth != data.getLastDate(PlayerShopData.DateType.MONTH)) {
            Bukkit.getPluginManager().callEvent(new PlayerNewCycleEvent(p,2,data.getLastDate(PlayerShopData.DateType.MONTH),thisMonth));
            data.setLastDate(PlayerShopData.DateType.MONTH,thisMonth);
            change = true;
        }
        if (change) {
            data.update(false);
        }
    }
}
