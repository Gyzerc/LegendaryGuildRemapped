package com.gyzer.Menu.Panels;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Guild.GuildIcon;
import com.gyzer.Data.Player.Position;
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
import java.util.List;

public class MainMenu extends MenuDraw {
    public MainMenu(Player p) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().MAIN_MENU);

        this.inv = Bukkit.createInventory(this , provider.getSize(), provider.getTitle());
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());

        DrawEssentailSpecial(inv , menuItem -> {
            switch (menuItem.getFuction()) {
                case "notice" : {
                    ItemStack i = menuItem.getItem(p);
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils();
                    menuItem.setItem( replaceHolderUtils.addListPlaceHolder("notice",guild.getNotice())
                            .startReplace(i,true,p.getName()));
                    return;
                }
                case "information": {
                    ItemStack i = menuItem.getItem(p);
                    ItemMeta id = i.getItemMeta();

                    GuildIcon icon = legendaryGuild.getGuildIconsManager().getIcon(guild.getIcon()).orElse(null);
                    if (icon != null){
                        ItemStack item = new ItemStack(icon.getMaterial(),1,(short) icon.getData());
                        ItemMeta itemid = item.getItemMeta();
                        if (id.hasDisplayName()){
                            itemid.setDisplayName(id.getDisplayName());
                        }
                        if (legendaryGuild.version_high && itemid.hasCustomModelData()){
                            itemid.setCustomModelData(icon.getModel());
                        }
                        if (id.hasLore()){
                            itemid.setLore(id.getLore());
                        }
                        item.setItemMeta(itemid);

                        i = item;
                    }

                    double next = config.EXP.getOrDefault(guild.getLevel(), Double.valueOf(-1));
                    double treenext = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.getOrDefault(guild.getTreelevel(), Double.valueOf(-1));
                    int maxmembers = guild.getMaxMembers();
                    GuildActivityData activityData = legendaryGuild.getGuildActivityManager().getData(guild.getGuild());
                    ReplaceHolderUtils replace = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("activity",activityData.getPoints()+"")
                            .addSinglePlaceHolder("total_activity",activityData.getTotal_points()+"")
                            .addSinglePlaceHolder("owner",guild.getOwner())
                            .addSinglePlaceHolder("level",""+guild.getLevel())
                            .addSinglePlaceHolder("exp",""+guild.getExp())
                            .addSinglePlaceHolder("next",""+next)
                            .addSinglePlaceHolder("treelevel",""+guild.getTreelevel())
                            .addSinglePlaceHolder("treeexp",""+guild.getTreeexp())
                            .addSinglePlaceHolder("treenext",""+treenext)
                            .addSinglePlaceHolder("money",""+guild.getMoney())
                            .addSinglePlaceHolder("members",""+guild.getMembers().size())
                            .addSinglePlaceHolder("maxmembers",""+maxmembers)
                            .addSinglePlaceHolder("date",guild.getDate())
                            .addListPlaceHolder("intro",guild.getIntro());
                    menuItem.setItem(replace.startReplace(i,true,p.getName()));
                    return;
                }
                case "tree" : {
                    ItemStack i = menuItem.getItem(p);
                    double treenext = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.getOrDefault(guild.getTreelevel(), Double.valueOf(-1));
                    ReplaceHolderUtils replace = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("level",guild.getTreelevel()+"")
                            .addSinglePlaceHolder("exp",guild.getTreeexp()+"")
                            .addSinglePlaceHolder("next",""+treenext)
                            .addSinglePlaceHolder("bar", GuildAPI.getGuildTreeExpProgressBar(guild));
                    menuItem.setItem(replace.startReplace(i,true,p.getName()));
                    return;
                }
                case "shop" : {
                    ItemStack i = menuItem.getItem(p);
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("points",user.getPoints()+"")
                            .addSinglePlaceHolder("total_points",user.getTotal_points()+"");
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                }
                case "positions" : {
                    ItemStack i = menuItem.getItem(p);
                    Position position = legendaryGuild.getPositionsManager().getPosition(user.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("position",position.getDisplay());
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                }
                case "activity" : {
                    ItemStack i = menuItem.getItem(p);
                    GuildActivityData activityData = legendaryGuild.getGuildActivityManager().getData(guild.getGuild());

                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("points",activityData.getPoints()+"")
                            .addSinglePlaceHolder("total_points",activityData.getTotal_points()+"");
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                }
                case "members" : {
                    ItemStack i = menuItem.getItem(p);
                    int maxmembers = guild.getMaxMembers();
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("members",guild.getMembers().size()+"")
                            .addSinglePlaceHolder("maxmembers",maxmembers+"");
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                }
                case "applications" : {
                    ItemStack i = menuItem.getItem(p);
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("amount",guild.getApplications().size()+"");
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                }
            }
        });
    }
    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!dealEssentialsButton(e.getRawSlot())) {
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null) {
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());

                if (guild != null) {
                    switch (menuItem.getFuction()) {
                        case "information": {
                            if (guild.getOwner().equals(p.getName())) {
                                if (e.isLeftClick()) {
                                    p.closeInventory();
                                    legendaryGuild.getChatControl().setModify(p.getUniqueId(),0);
                                    p.sendMessage(lang.plugin+lang.input);
//
                                    return;
                                }
                                if (e.isRightClick()){
                                    p.closeInventory();
                                    List<String> intro = new ArrayList<>(guild.getIntro());
                                    if (intro.size() > 0){
                                        String remove = intro.remove(intro.size()-1);
                                        guild.setIntro(intro);
                                        guild.update();

                                        p.sendMessage(lang.plugin+lang.intro_remove.replace("%value%", MsgUtils.color(remove)));
                                        MainMenu menuPanel = new MainMenu(p);
                                        menuPanel.open();
                                        return;
                                    }
                                }
                                return;
                            }
                            p.sendMessage(lang.plugin+lang.notowner);
                            return;
                        }
                        case "notice": {
                            if (guild.getOwner().equals(p.getName())) {
                                if (e.isLeftClick()) {
                                    p.closeInventory();
                                    legendaryGuild.getChatControl().setModify(p.getUniqueId(),1);
                                    p.sendMessage(lang.plugin+lang.input);
                                    return;
                                }
                                if (e.isRightClick()){
                                    List<String> notice = new ArrayList<>(guild.getNotice());
                                    if (notice.size() > 0){
                                        String remove = notice.remove(notice.size()-1);
                                        guild.setNotice(notice);
                                        guild.update();

                                        p.sendMessage(lang.plugin+lang.notice_remove.replace("%value%",MsgUtils.color(remove)));
                                        MainMenu menuPanel = new MainMenu(p);
                                        menuPanel.open();
                                        return;
                                    }
                                }
                                return;
                            }
                            p.sendMessage(lang.plugin+lang.notowner);
                            return;
                        }
                        case "tree" : {
                            if (legendaryGuild.getGuildTreeManager().isEnable()) {
                                TreeMenu treePanel = new TreeMenu(p);
                                treePanel.open();
                            }
                            return;
                        }
                        case "shop" : {
                            if (legendaryGuild.getGuildShopManager().isEnable()) {
                                ShopMenu shopPanel = new ShopMenu(p, 1);
                                shopPanel.open();
                            }
                            return;
                        }
                        case "tributes" : {
                            if (legendaryGuild.getTributesManager().isEnable()) {
                                TributesMenu tributesPanel = new TributesMenu(p);
                                tributesPanel.open();
                            }
                            return;
                        }
                        case "applications": {
                            ApplicationsMenu applicationsPanel = new ApplicationsMenu(p, 1);
                            applicationsPanel.open();
                            return;
                        }
                        case "members" : {
                            MembersMenu membersPanel = new MembersMenu(p,1, MembersMenu.Sort.POSITION);
                            membersPanel.open();
                            return;
                        }
                        case "icon" : {
                            IconsShopMenu guildIconsShopPanel = new IconsShopMenu(p,1);
                            guildIconsShopPanel.open();
                            return;
                        }
                        case "positions" : {
                            PositionsMenu positionsPanel = new PositionsMenu(p);
                            positionsPanel.open();
                            return;
                        }
                        case "activity": {
                            if (legendaryGuild.getGuildActivityManager().isEnable()) {
                                ActivityRewardsMenu activityRewardsPanel = new ActivityRewardsMenu(p);
                                activityRewardsPanel.open();
                            }
                            return;
                        }
                        case "redpackets" : {
                            if (legendaryGuild.getGuildRedpacketManager().isEnable()) {
                                RedPacketsMenu redPacketsPanel = new RedPacketsMenu(p, 1);
                                redPacketsPanel.open();
                            }
                            return;
                        }
                        case "teamshop" : {
                            if (legendaryGuild.getTeamShopManager().isEnable()) {
                                TeamShopMenu teamShopPanel = new TeamShopMenu(p, 1);
                                teamShopPanel.open();
                            }
                            return;
                        }
                        case "buff": {
                            if (legendaryGuild.getBuffsManager().isEnable()) {
                                BuffMenu buffPanel = new BuffMenu(p, 1);
                                buffPanel.open();
                            }
                            return;
                        }
                        case "pvp" : {
                            p.performCommand("legendaryguildremapped pvp");
                            return;
                        }
                        case "chat" : {
                            p.performCommand("legendaryguildremapped chat");
                            return;
                        }
                        case "home": {
                            if (e.isLeftClick()) {
                                GuildAPI.teleportGuildHome(p,user,guild);
                                p.closeInventory();
                                return;
                            }
                            if (e.isRightClick()) {
                                GuildAPI.setGuildHome(p,guild);
                                p.closeInventory();
                                return;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

}
