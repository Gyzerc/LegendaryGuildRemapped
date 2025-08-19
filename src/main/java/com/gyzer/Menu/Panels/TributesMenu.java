package com.gyzer.Menu.Panels;

import com.gyzer.API.Events.GuildTributesEvent;
import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.TributeItem;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.MsgUtils;
import com.gyzer.Utils.ReplaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TributesMenu extends MenuDraw {
    public TributesMenu(Player p) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().TRIBUTES);
        this.inv = Bukkit.createInventory(this,provider.getSize(),provider.getTitle());
        DrawEssentailSpecial(inv,menuItem->{
            if (menuItem.getFuction().equals("tribute")) {
                menuItem.setPut(false);
            }
        });
        refreshConfirmButton();
    }
    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.isShiftClick()) {
            e.setCancelled(true);
        }
        if (dealEssentialsButton(e.getRawSlot())) {
            e.setCancelled(true);
        }
        else {
            if (e.getRawSlot() >= 0 && e.getRawSlot() < inv.getSize()) {
                MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
                if (menuItem != null) {
                    if (menuItem.getFuction().equals("confirm")) {
                        e.setCancelled(true);
                        sell(e.getInventory());
                        return;
                    }
                    if (provider.getMenuItem(e.getRawSlot()).getFuction().equals("tribute")) {
                        refreshConfirmButton();
                    }
                }
            }
        }
    }


    public void sell(Inventory inventory)
    {
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        HashMap<TributeItem,Integer> broads = new HashMap<>();
        List<TributeItem> tributeItems = new ArrayList<>();

        double points=0.0;
        double exp=0.0;
        for (int slot = 0 ; slot < inv.getSize() ; slot ++ ) {
            if (provider.getMenuItem(slot).getFuction().equals("tribute")) {
                ItemStack i = inventory.getItem(slot);
                if (i != null && !i.getType().equals(Material.AIR)) {
                    Optional<TributeItem> tributesId= legendaryGuild.getTributesManager().getTributeItem(i);
                    if (tributesId.isPresent())
                    {
                        int amount = i.getAmount();
                        TributeItem tributes= tributesId.get();

                        points+=tributes.getPoints()*amount;
                        exp+=tributes.getExp()*amount;

                        i.setAmount(0);

                        if (tributes.isBroad()){
                            broads = addValue(broads,tributes,amount);
                        }
                        if (!tributeItems.contains(tributes)){
                            tributeItems.add(tributes);
                        }
                    }
                }
            }
        }

        if (points != 0 || exp != 0) {
            user.addPoints(points, true);
            user.update(false);
            GuildAPI.addGuildExp(p.getName(), guild, exp);
            Bukkit.getPluginManager().callEvent(new GuildTributesEvent(p,tributeItems));
        }
        if (!broads.isEmpty()){
            for (TributeItem tributeItem : broads.keySet()){
                MsgUtils.sendGuildMessage(guild.getMembers(),tributeItem.getMessage().replace("%player%",p.getName()).replace("%amount%",""+broads.get(tributeItem)));
            }
        }
        //刷新按钮
        refreshConfirmButton();
    }

    private HashMap<TributeItem,Integer> addValue(HashMap<TributeItem,Integer> broads,TributeItem tributeItem,int amount){
        HashMap<TributeItem,Integer> returnMap = broads;
        if (returnMap.containsKey(tributeItem)){
            int old = returnMap.get(tributeItem);
            returnMap.put(tributeItem,(old+amount));
        }
        else {
            returnMap.put(tributeItem,amount);
        }
        return returnMap;
    }

    public void refreshConfirmButton() {
        legendaryGuild.sync(()->{
            double points=0.0;
            double exp=0.0;
            //检测价值
            for (int slot = 0 ; slot < inv.getSize() ; slot ++ ) {
                if (provider.getMenuItem(slot).getFuction().equals("tribute")) {


                    ItemStack i = inv.getItem(slot);
                    if (i != null && !i.getType().equals(Material.AIR)) {
                        Optional<TributeItem> tributesId= legendaryGuild.getTributesManager().getTributeItem(i);
                        if (tributesId.isPresent())
                        {
                            TributeItem tributes= tributesId.get();
                            points+=tributes.getPoints() * i.getAmount();
                            exp+=tributes.getExp() * i.getAmount();
                        }
                    }
                }
            }

            //更换按钮
            int slot = provider.getSlot("confirm");
            ItemStack i = provider.getMenuItem(slot).getItem(p).clone();
            ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                    .addSinglePlaceHolder("points",""+points)
                    .addSinglePlaceHolder("exp",""+exp);
            inv.setItem(slot , replaceHolderUtils.startReplace(i,true,p.getName()));
        },5);
    }

    @Override
    public void onDrag(InventoryDragEvent e) {
        for (int slot : e.getRawSlots()) {
            if (slot >= 0 && slot < inv.getSize()) {
                if (provider.getMenuItem(slot).getFuction().equals("tribute")) {
                    legendaryGuild.sync(new Runnable() {
                        @Override
                        public void run() {
                            refreshConfirmButton();
                        }
                    },5);
                }
            }
        }
    }
}
