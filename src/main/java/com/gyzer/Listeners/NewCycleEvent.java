package com.gyzer.Listeners;

import com.gyzer.API.Events.PlayerNewCycleEvent;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.Shop.Item.ShopType;
import com.gyzer.Data.Player.PlayerShopData;
import com.gyzer.Data.Player.User;
import com.gyzer.Data.Player.WaterDataStore;
import com.gyzer.LegendaryGuild;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NewCycleEvent implements Listener {
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private Language lang = legendaryGuild.getLanguageManager();

    @EventHandler
    public void newCycle(com.gyzer.API.Events.NewCycleEvent e){
        switch (e.getId()) {
            case 0 : {
                //公会活跃度检测
                LegendaryGuild.getLegendaryGuild().getGuildActivityManager().checkCycle();
            }
        }
    }

    @EventHandler
    public void newPlayerCycle( PlayerNewCycleEvent e) {
        Player p = e.getPlayer();
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        if (user.hasGuild()) {
            PlayerShopData data = legendaryGuild.getGuildShopManager().getPlayerShopData(p.getName());
            int id = e.getId();
            switch (id) {
                case 0: {
                    //刷新每日许愿
                    user.setWish(false);
                    p.sendMessage(lang.plugin + lang.tree_wish_refresh);

                    //神树水壶
                    WaterDataStore waterDataStore = user.getWaterDataStore();
                    waterDataStore.clearWaterDay();
                    user.setWaterDataStore(waterDataStore);
                    p.sendMessage(lang.plugin + lang.tree_water_refresh);

                    //更新数据
                    user.update(false);

                    //公会商店每日限购
                    data.refresh(ShopType.DAY);
                    data.update(false);
                    p.sendMessage(lang.plugin + lang.shop_refresh_day);
                    break;
                }
                case 1: {

                    //公会商店每周限购
                    data.refresh(ShopType.WEEK);
                    data.update(false);
                    p.sendMessage(lang.plugin + lang.shop_refresh_week);
                    break;
                }
                case 2: {

                    //公会商店每周限购
                    data.refresh(ShopType.MONTH);
                    data.update(false);
                    p.sendMessage(lang.plugin + lang.shop_refresh_month);
                    break;
                }
            }
        }
    }
}
