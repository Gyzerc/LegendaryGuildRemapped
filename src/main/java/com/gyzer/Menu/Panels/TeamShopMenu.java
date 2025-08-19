package com.gyzer.Menu.Panels;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.TeamShop.GuildTeamShopData;
import com.gyzer.Data.Guild.TeamShop.TeamShopItem;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.MsgUtils;
import com.gyzer.Utils.ReplaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamShopMenu extends MenuDraw {
    private int page;
    private List<String> list;
    private List<String> currentPage;
    private int layout;
    public TeamShopMenu(Player p, int page) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().TEAMSHOP);
        this.page = page;
        this.inv = Bukkit.createInventory(this , provider.getSize() , provider.getTitle());

        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        GuildTeamShopData teamShopData = guild.getGuildTeamShopData();
        TeamShopItem shopItem = legendaryGuild.getTeamShopManager().getShopItem(teamShopData.getTodayShopId());
        if (shopItem == null) {
            teamShopData.randomShop();
            shopItem = legendaryGuild.getTeamShopManager().getShopItem(teamShopData.getTodayShopId());
        }

        layout = getShowPerPage();
        list = getBargainsLore(teamShopData.getBargains(),guild);
        currentPage = getPage(page,layout,list);

        TeamShopItem finalShopItem = shopItem;
        DrawEssentailSpecial(inv , menuItem -> {
            ItemStack i = menuItem.getItem(p).clone();
            if (menuItem.getFuction().equals("item")) {
                ItemStack preview = finalShopItem.getPreview().clone();

                String placeholder_whether_bargain_already = teamShopData.hasBargain(p.getName()) ?
                        provider.getPlaceHolder("whether-bargain-already") : provider.getPlaceHolder("whether_bargain-wait");
                String placeholder_limit = finalShopItem.getLimit() > 0 ?
                        provider.getPlaceHolder("limit").replace("%purchased%",String.valueOf(teamShopData.getBuyAmount(p.getName()))).replace("%limit%",String.valueOf(finalShopItem.getLimit())) :
                        "";

                ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                        .addSinglePlaceHolder("placeholder_whether_bargain",placeholder_whether_bargain_already)
                        .addSinglePlaceHolder("bargain", String.valueOf(teamShopData.getBargains().size()))
                        .addSinglePlaceHolder("members", String.valueOf(guild.getMembers().size()))
                        .addSinglePlaceHolder("current_price",String.valueOf(teamShopData.getCurrentPrice()))
                        .addSinglePlaceHolder("placeholder_limit",placeholder_limit);

                menuItem.setItem(replaceHolderUtils.startReplace(preview,true,p.getName()));
                return;
            }
            if (menuItem.getFuction().equals("bargains")) {
                int index = 0;

                ItemMeta id = i.getItemMeta();
                List<String> lore = new ArrayList<>();
                for (String l : id.getLore()) {
                    if (l.contains("%placeholder_bargains%")) {
                        if (currentPage.size() > index) {
                            lore.add(currentPage.get(index));
                            index++;
                        }
                        continue;
                    }
                    lore.add(l);
                }

                id.setLore(lore);
                i.setItemMeta(id);

                ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                        .addSinglePlaceHolder("bargain",String.valueOf(teamShopData.getBargains().size()))
                        .addSinglePlaceHolder("members",String.valueOf(guild.getMembers().size()));

                menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
            }
        });
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!dealEssentialsButton(e.getRawSlot())) {
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null) {
                switch (menuItem.getFuction()) {
                    case "item" : {
                        User user = legendaryGuild.getUserManager().getUser(p.getName());
                        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                        if (e.isLeftClick()) {
                            GuildTeamShopData teamShopData = guild.getGuildTeamShopData();
                            if (teamShopData.hasBargain(p.getName())) {
                                p.sendMessage(lang.plugin + lang.bargain_already);
                                return;
                            }
                            teamShopData.bargain(guild,p.getName());

                            p.sendMessage(lang.plugin + lang.bargain_success.replace("%current%",String.valueOf(teamShopData.getCurrentPrice())).replace("%bargain%",String.valueOf(teamShopData.getBargainPrice(p.getName()))));
                            MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin + lang.bargain_bargain_broad.replace("%player%",p.getName()).replace("%current%",String.valueOf(teamShopData.getCurrentPrice())).replace("%bargain%",String.valueOf(teamShopData.getBargainPrice(p.getName()))));

                            TeamShopMenu teamShopPanel = new TeamShopMenu(p,page);
                            teamShopPanel.open();
                            return;
                        }
                        if (e.isRightClick()) {
                            GuildTeamShopData teamShopData = guild.getGuildTeamShopData();
                            TeamShopItem shopItem = legendaryGuild.getTeamShopManager().getShopItem(teamShopData.getTodayShopId());

                            if (GuildAPI.buyGuildTeamShop(p,user,guild,shopItem,teamShopData)) {
                                new TeamShopMenu(p, page).open();
                            }
                            return;
                        }
                        return;
                    }
                    case "bargains" : {
                        if (e.isRightClick()) {
                            if (page > 1) {
                                new TeamShopMenu(p,(page-1)).open();
                            }
                            return;
                        }
                        if (e.isLeftClick()) {
                            if (!getPage((page + 1) , layout , list).isEmpty()) {
                                new TeamShopMenu(p, (page+1)).open();
                            }
                        }
                    }
                }
            }
        }
    }


    private List<String> getBargainsLore(HashMap<String, Double> bargains, Guild guild) {
        List<String> notBargainMembers = guild.getMembers().stream().filter(name -> !bargains.containsKey(name)).collect(Collectors.toList());
        List<String> lore = new ArrayList<>();
        String bargained = provider.getPlaceHolder("bargains-already");
        String wait = provider.getPlaceHolder("bargains-not");
        bargains.forEach((player,bargain) -> {
            lore.add(bargained.replace("%player%" , player).replace("%bargain-price%",String.valueOf(bargain)));
        });
        notBargainMembers.forEach(player -> lore.add(wait.replace("%player%",player)));
        return lore;
    }

    private int getShowPerPage() {
        int showPerPage = 0;
        
        for (Map.Entry<Integer , MenuProvider.MenuItem> entry : provider.getItem().entrySet()) {
            MenuProvider.MenuItem menuItem = entry.getValue();
            if (menuItem.getFuction().equals("bargains")) {
                ItemStack i = menuItem.getItem(p);
                ItemMeta id = i.getItemMeta();
                List<String> lore = id.hasLore() ? id.getLore() : new ArrayList<>();
                for (String l : lore) {
                    if (l.contains("%placeholder_bargains%")) {
                        showPerPage ++;
                    }
                }
            }
        }
        
        return showPerPage;
    }
}
