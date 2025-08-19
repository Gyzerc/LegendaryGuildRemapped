package com.gyzer.Menu.Panels;

import com.gyzer.API.Events.GuildShopBuyEvent;
import com.gyzer.Data.Guild.Shop.Item.ShopItem;
import com.gyzer.Data.Guild.Shop.Item.ShopType;
import com.gyzer.Data.Player.PlayerShopData;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ShopMenu extends MenuDraw {
    private HashMap<Integer, ShopItem> slot_item;
    private int currentIndex = 0;
    private int page;
    public ShopMenu(Player p, int page) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().SHOP);
        this.page=page;
        this.inv = Bukkit.createInventory(this,provider.getSize(),provider.getTitle());
        slot_item = new HashMap<>();
        if (hasPage(page , legendaryGuild.getGuildShopManager().getItems(),"shop")) {
            User user = legendaryGuild.getUserManager().getUser(p.getName());
            PlayerShopData shopData = legendaryGuild.getGuildShopManager().getPlayerShopData(p.getName());
            List<ShopItem> shopItems = getPage(page , legendaryGuild.getGuildShopManager().getItems(),"shop");

            DrawEssentailSpecial(inv , menuItem -> {
                if (menuItem.getFuction().equals("shop")) {
                    if (shopItems.size() > currentIndex) {
                        ShopItem shopItem = shopItems.get(currentIndex);

                        String limit = "";
                        if (!shopItem.getType().equals(ShopType.UNLIMITED)){
                            limit = provider.getPlaceHolder(shopItem.getType().name().toLowerCase())
                                    .replace("%left%",""+shopData.getBuyAmount(p.getName()))
                                    .replace("%max%",""+shopItem.getLimitAmount());
                        }

                        ItemStack i = shopItem.getDisplay().clone();
                        ItemMeta id = i.getItemMeta();
                        List<String> lore = id.hasLore() ? id.getLore() : new ArrayList<>();
                        id.setLore(lore);
                        i.setItemMeta(id);

                        ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                                .addSinglePlaceHolder("points",String.valueOf(user.getPoints()))
                                .addSinglePlaceHolder("limit",limit);

                        menuItem.setTarget(shopItem.getId());
                        menuItem.setItem( replaceHolderUtils.startReplace(i,true,p.getName()));


                        currentIndex ++;
                        return;
                    }
                    menuItem.setPut(false);
                }
            });
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!dealEssentialsButton(e.getRawSlot())){
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null){
                switch (menuItem.getFuction()){
                    case "pre" : {
                        if (page <= 1) {
                            return;
                        }
                        ShopMenu shopPanel = new ShopMenu(p, (page - 1));
                        shopPanel.open();
                        break;
                    }
                    case "next": {
                        LinkedList<ShopItem> items = legendaryGuild.getGuildShopManager().getItems();
                        if (hasPage(page + 1, items,"shop")) {
                            ShopMenu shopPanel = new ShopMenu(p, (page + 1));
                            shopPanel.open();
                        }
                        break;
                    }
                    case "shop" : {
                        String id = (String) getTarget(e.getRawSlot());
                        ShopItem shopItem = legendaryGuild.getGuildShopManager().getShopItem(id);
                        if (shopItem != null) {
                            PlayerShopData guildShopData = legendaryGuild.getGuildShopManager().getPlayerShopData(p.getName());
                            boolean reachMax = false;
                            if (!shopItem.getType().equals(ShopType.UNLIMITED)) {
                                int max = shopItem.getLimitAmount();
                                int hasBuy = guildShopData.getBuyAmount(id);
                                reachMax = (hasBuy >= max);
                            }

                            if (legendaryGuild.getRequirementsManager().check(p, shopItem.getRequirements())) {
                                if (reachMax) {
                                    p.sendMessage(lang.plugin + lang.shop_limit);
                                    return;
                                }
                                legendaryGuild.getRequirementsManager().deal(p, shopItem.getRequirements());

                                RunUtils runUtils = new RunUtils(shopItem.getRuns(), p);
                                runUtils.start();

                                //更新限购数据
                                guildShopData.addBuyAmount(id , 1);
                                guildShopData.update(false);

                                String name = e.getCurrentItem().getItemMeta().hasDisplayName() ? e.getCurrentItem().getItemMeta().getDisplayName() : e.getCurrentItem().getType().name();
                                p.sendMessage(lang.plugin + lang.shop_buy.replace("%value%", name));

                                Bukkit.getPluginManager().callEvent(new GuildShopBuyEvent(p, shopItem));

                                ShopMenu shopPanel = new ShopMenu(p, page);
                                shopPanel.open();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

}
