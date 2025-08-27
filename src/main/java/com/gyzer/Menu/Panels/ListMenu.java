package com.gyzer.Menu.Panels;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Guild.GuildIcon;
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

import java.util.List;

public class ListMenu extends MenuDraw {

    private Sort sort;
    private int page;
    private int currentIndex = 0;
    private boolean hasNext;
    public ListMenu(Player p, int page,Sort sort) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().LIST);
        this.page = page;
        this.sort = sort;
        this.inv = Bukkit.createInventory(this,provider.getSize(), MsgUtils.color(provider.getTitle().replace("%sort%",provider.getPlaceHolder(sort.getPlaceholder()))));
        List<String> allguilds = legendaryGuild.getGuildsManager().getGuilds();

        this.hasNext = hasPage((page + 1) , allguilds , "guild");
        List<String> guilds = getPage(page , allguilds ,"guild");

        DrawEssentailSpecial(inv , menuItem -> {
            if (menuItem.getFuction().equals("sort")) {
                ItemStack i = menuItem.getItem(p);
                ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                        .addSinglePlaceHolder("placeholder_sort",provider.getPlaceHolder(sort.getPlaceholder()));
                menuItem.setItem(replaceHolderUtils.startReplace(i,false,null));
            }
            if (menuItem.getFuction().equals("guild")) {
                if (guilds.size() > currentIndex) {
                    String guildId = guilds.get(currentIndex);
                    Guild guild = legendaryGuild.getGuildsManager().getGuild(guildId);
                    if (guild != null) {
                        GuildIcon icon = legendaryGuild.getGuildIconsManager().getIcon(guild.getIcon()).orElse(null);
                        GuildActivityData activityData = legendaryGuild.getGuildActivityManager().getData(guildId);


                        ItemStack i = menuItem.getItem(p).clone();
                        if (icon != null) {
                            i.setType(icon.getMaterial());
                            i.setDurability((short) icon.getData());
                        }
                        ItemMeta id = i.getItemMeta();
                        if (legendaryGuild.isVersion_high()) {
                            id.setCustomModelData(icon.getModel());
                        }
                        i.setItemMeta(id);

                        ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                                .addSinglePlaceHolder("guild",guild.getDisplay())
                                .addSinglePlaceHolder("date",guild.getDate())
                                .addSinglePlaceHolder("owner",guild.getOwner())
                                .addSinglePlaceHolder("level",String.valueOf(guild.getLevel()))
                                .addSinglePlaceHolder("exp",String.valueOf(guild.getExp()))
                                .addSinglePlaceHolder("exp_next",String.valueOf(config.EXP.get(guild.getLevel())))
                                .addSinglePlaceHolder("money",String.valueOf(guild.getMoney()))
                                .addSinglePlaceHolder("members", String.valueOf(guild.getMembers().size()))
                                .addSinglePlaceHolder("maxmembers",String.valueOf(guild.getMaxMembers()))
                                .addSinglePlaceHolder("treelevel",String.valueOf(guild.getTreelevel()))
                                .addSinglePlaceHolder("treeexp",String.valueOf(guild.getTreeexp()))
                                .addSinglePlaceHolder("treeexp_next",String.valueOf(legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.get(guild.getLevel())))
                                .addSinglePlaceHolder("activity", String.valueOf(activityData.getPoints()))
                                .addSinglePlaceHolder("total_activity",String.valueOf(activityData.getTotal_points()))
                                .addListPlaceHolder("intro",guild.getIntro());

                        menuItem.setTarget(guildId);
                        menuItem.setItem(replaceHolderUtils.startReplace(i , true,p.getName()));
                        currentIndex ++;
                        return;
                    }

                }
                menuItem.setPut(false);
            }
        });

    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (!dealEssentialsButton(e.getRawSlot())){
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null){
                switch (menuItem.getFuction()){
                    case "pre": {
                        if (page <= 1) {
                            return;
                        }
                        ListMenu list = new ListMenu(p, (page - 1), sort);
                        list.open();
                        break;
                    }
                    case "next": {
                        if (hasNext) {
                            ListMenu list = new ListMenu(p, (page - 1), sort);
                            list.open();
                            return;
                        }
                        break;
                    }
                    case "sort": {
                        int max = Sort.values().length;
                        int index = sort.ordinal();
                        int next = index+1 == max ? 0 : index+1;
                        Sort nextSort = Sort.values()[next];
                        ListMenu list = new ListMenu(p,1,nextSort);
                        list.open();
                        break;
                    }
                    case "guild" : {
                        String guildId = (String) getTarget(e.getRawSlot());
                        User user = legendaryGuild.getUserManager().getUser(p.getName());
                        if (user.hasGuild()){
                            p.sendMessage(lang.plugin+lang.already_in_guild);
                            return;
                        }
                        GuildAPI.sendApplication(user,guildId);
                        return;
                    }
                }
            }
        }
    }



    public enum Sort{
        ACTIVITY("sort_activity"),
        MEMBERS("sort_members"),
        LEVEL("sort_level"),
        TREELEVEL("sort_treelevel"),
        MONEY("sort_money"),
        DEFAULT("sort_default");
        private String placeholder;

        Sort(String placeholder) {
            this.placeholder = placeholder;
        }

        public String getPlaceholder() {
            return placeholder;
        }
    }
}
