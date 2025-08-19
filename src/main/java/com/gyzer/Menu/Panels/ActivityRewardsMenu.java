package com.gyzer.Menu.Panels;

import com.gyzer.API.Events.ActivityRewardClaimEvent;
import com.gyzer.Data.Guild.ActivityReward;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.ReplaceHolderUtils;
import com.gyzer.Utils.RunUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ActivityRewardsMenu extends MenuDraw {
    public ActivityRewardsMenu(Player p) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().ACTIVITY);
        this.inv = Bukkit.createInventory(this , provider.getSize() , provider.getTitle());
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        GuildActivityData data = legendaryGuild.getGuildActivityManager().getData(user.getGuild());
        StringStore store = data.getClaimed();
        DrawEssentailSpecial(inv,menuItem -> {
            if (menuItem.getFuction().equals("activity")){
                String activityId = menuItem.getValue();
                ActivityReward reward = legendaryGuild.getGuildActivityManager().getReward(activityId).orElse(null);
                if (reward != null){
                    List<String> claims = (List<String>) store.getValue(reward.getId(),new ArrayList<>());

                    String placeholder = provider.getPlaceHolder("cant");
                    if (reward.getPoints() <= data.getPoints()){
                        if (claims.contains(p.getName())){
                            placeholder = provider.getPlaceHolder("already");
                        }
                        else {
                            placeholder = provider.getPlaceHolder("wait");
                        }
                    }

                    ItemStack i = menuItem.getItem(p);
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("activity",""+data.getPoints())
                            .addSinglePlaceHolder("placeholder",placeholder);
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                    return;
                }
                menuItem.setPut(false);
            }
        });
    }


    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!dealEssentialsButton(e.getRawSlot())) {
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null) {
                switch (menuItem.getFuction()){
                    case "activity":{
                        String id = menuItem.getValue();
                        ActivityReward reward = legendaryGuild.getGuildActivityManager().getReward(id).orElse(null);
                        if (reward != null) {

                            User user = legendaryGuild.getUserManager().getUser(p.getName());
                            GuildActivityData data = legendaryGuild.getGuildActivityManager().getData(user.getGuild());
                            StringStore store = data.getClaimed();

                            List<String> claims = new ArrayList<>( (List<String>) store.getValue(reward.getId(), new ArrayList<>()));

                            if (reward.getPoints() <= data.getPoints()){
                                if (claims.contains(p.getName())){
                                    p.sendMessage(lang.plugin+lang.activity_already_claimed);
                                    return;
                                }

                                claims.add(p.getName());
                                store.setValue(id,claims,new ArrayList<>());
                                data.setClaimed(store);
                                data.update();
                                Bukkit.getPluginManager().callEvent(new ActivityRewardClaimEvent(p,reward));

                                p.sendMessage(lang.plugin+lang.activity_claim.replace("%value%", reward.getDisplay()));
                                new RunUtils(reward.getRun(),p).start();


                                ActivityRewardsMenu activityRewardsPanel = new ActivityRewardsMenu(p);
                                activityRewardsPanel.open();
                                return;
                            }
                            p.sendMessage(lang.plugin+lang.activity_cant_claim.replace("%value%",""+reward.getPoints()));
                            return;
                        }
                    }
                }
            }
        }
    }
}
