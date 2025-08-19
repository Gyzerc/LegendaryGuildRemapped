package com.gyzer.Menu.Panels;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.ReplaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MembersMenu extends MenuDraw {
    private int page;
    private Sort sort;
    private int currentIndex = 0;
    private boolean hasNext;
    public MembersMenu(Player p, int page, Sort sort) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().MEMBERS);
        this.page = page;
        this.sort = sort;
        this.inv = Bukkit.createInventory(this,provider.getSize(),provider.getTitle().replace("%sort%",provider.getPlaceHolder(sort.getPlaceholder())));
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        this.hasNext = hasPage((page + 1), guild.getMembers(),"member");
        List<String> members = getPage(page , getBy(guild , sort),"member");

        GuildActivityData activityData = legendaryGuild.getGuildActivityManager().getData(guild.getGuild());
        DrawEssentailSpecial(inv , menuItem -> {
            if (menuItem.getFuction().equals("sort")) {
                menuItem.setItem(new ReplaceHolderUtils()
                        .addSinglePlaceHolder("placeholder_sort",provider.getPlaceHolder(sort.getPlaceholder()))
                        .startReplace(menuItem.getItem(p).clone(),true,p.getName()));
            }
            if (menuItem.getFuction().equals("member")) {
                if (members.size() > currentIndex) {
                    String member = members.get(currentIndex);
                    User targetUser = legendaryGuild.getUserManager().getUser(member);
                    Position position = legendaryGuild.getPositionsManager().getPosition(targetUser.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
                    menuItem.setItem(new ReplaceHolderUtils()
                            .addSinglePlaceHolder("player",member)
                            .addSinglePlaceHolder("date",targetUser.getDate())
                            .addSinglePlaceHolder("total_points", String.valueOf(targetUser.getTotal_points()))
                            .addSinglePlaceHolder("position", position.getDisplay())
                            .addSinglePlaceHolder("activity", String.valueOf(activityData.getPlayerActivity(member)))
                            .addSinglePlaceHolder("total_activity" , String.valueOf(activityData.getPlayerTotalActivity(member)))
                            .startReplace(menuItem.getItem(p).clone(),true,member));
                    menuItem.setTarget(member);
                    currentIndex ++;
                    return;
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
                    case "pre":{
                        if (page <= 1){
                            return;
                        }
                        MembersMenu membersPanel = new MembersMenu(p,page-1,sort);
                        membersPanel.open();
                        break;
                    }
                    case "next" : {
                        if (hasNext) {
                            MembersMenu membersPanel = new MembersMenu(p, page + 1, sort);
                            membersPanel.open();
                        }
                        break;
                    }
                    case "sort" : {
                        int max = Sort.values().length;
                        int index = sort.ordinal();
                        int next = index+1 == max ? 0 : index+1;
                        Sort nextSort = Sort.values()[next];
                        MembersMenu membersPanel = new MembersMenu(p,1,nextSort);
                        membersPanel.open();
                        break;
                    }
                    case "member" : {
                        String member = (String) getTarget(e.getRawSlot());
                        if (member != null && !member.isEmpty()) {
                            if (e.isShiftClick() && e.isRightClick()) {
                                if (member.equals(p.getName())){
                                    return;
                                }
                                User user = legendaryGuild.getUserManager().getUser(p.getName());
                                Position position = legendaryGuild.getPositionsManager().getPosition(user.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());

                                if (position.isKick()) {
                                    if (GuildAPI.kick(legendaryGuild.getGuildsManager().getGuild(user.getGuild()), p ,member)){
                                        MembersMenu membersPanel = new MembersMenu(p,1,sort);
                                        membersPanel.open();
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public List<String> getBy(Guild guild,Sort sort){
        LinkedList<String> members = guild.getMembers();
        List<String> list = members.stream().collect(Collectors.toList());
        if (sort.equals(Sort.DATE)){
            return list;
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                User u1 = legendaryGuild.getUserManager().getUser(o1);
                User u2 = legendaryGuild.getUserManager().getUser(o2);
                if (u1 == null) {
                    return  1;
                }
                if (u2 == null) {
                    return -1;
                }
                switch (sort) {
                    case POSITION: {
                        Position p1 = legendaryGuild.getPositionsManager().getPosition(u1.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
                        Position p2 = legendaryGuild.getPositionsManager().getPosition(u2.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
                        return (p2.getWeight() > p1.getWeight()) ? -1 : ((p2.getWeight() == p1.getWeight()) ? 0 : 1);
                    }
                    case POINTS: {
                        return (u1.getTotal_points() > u2.getTotal_points()) ? -1 : ((u1.getTotal_points() == u2.getTotal_points()) ? 0 : 1);
                    }
                }
                return 0;
            }
        });
        return list;
    }

    public enum Sort{
        POSITION("sort_position"),
        POINTS("sort_points"),
        DATE("sort_date");

        private String placeholder;
        Sort(String placeholder){
            this.placeholder = placeholder;
        }

        public String getPlaceholder() {
            return placeholder;
        }
    }
}
