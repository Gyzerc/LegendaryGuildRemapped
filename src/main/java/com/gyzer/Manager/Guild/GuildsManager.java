package com.gyzer.Manager.Guild;

import com.gyzer.API.Events.GuildDeleteEvent;
import com.gyzer.API.Events.GuildGiveEvent;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.BungeeCord.NetWorkMessage;
import com.gyzer.Utils.BungeeCord.NetWorkMessageBuilder;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GuildsManager {
    private LegendaryGuild legendaryGuild  =LegendaryGuild.getLegendaryGuild();
    private Language lang = legendaryGuild.getLanguageManager();
    private HashMap<String, Guild> caches;

    public GuildsManager() {
        caches = new HashMap<>();
        loadGuilds();
    }

    public boolean isExists(String guildName){
        return getGuilds().contains(guildName);
    }

    public void removeGuildCache(String guild){
        caches.remove(guild);
    }


    public List<Guild> getGuildsBy(Sort sort){

        List<Guild> guilds = getGuilds().stream().map(s -> {
            return getGuild(s);
        }).collect(Collectors.toList());

        if (sort.equals(Sort.DEFAULT)){
            return guilds;
        }

        Collections.sort(guilds, new Comparator<Guild>() {
            @Override
            public int compare(Guild o1, Guild o2) {
                switch (sort){
                    case LEVEL:
                        return (o1.getLevel() > o2.getLevel()) ? -1 : ((o1.getLevel() == o2.getLevel()) ? 0 : 1);
                    case MONEY:
                        return (o1.getMoney() > o2.getMoney()) ? -1 : ((o1.getMoney() == o2.getMoney()) ? 0 : 1);
                    case MEMBERS:
                        int a1 = o1.getMembers().size();
                        int a2 = o2.getMembers().size();
                        return (a1 > a2) ? -1 : ((a1 == a2) ? 0 : 1);
                    case ACTIVITY:
                        GuildActivityData data1 = legendaryGuild.getGuildActivityManager().getData(o1.getGuild());
                        GuildActivityData data2 = legendaryGuild.getGuildActivityManager().getData(o2.getGuild());
                        return (data1.getPoints() > data2.getPoints()) ? -1 : ((data1.getPoints() == data2.getPoints()) ? 0 : 1);
                    case TREELEVEL:
                        return (o1.getTreelevel() > o2.getTreelevel()) ? -1 : ((o1.getTreelevel() == o2.getTreelevel()) ? 0 : 1);
                }
                return -1;
            }
        });
        return guilds;
    }

    public Guild createGuild(String guild, Player p){
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        String owner = p.getName();
        SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(System.currentTimeMillis());

        LinkedList<String> members = new LinkedList<>();
        members.add(owner);
        Guild guildData = new Guild(guild,owner,"",date,0.0,0.0,0.0,0,0,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new StringStore(),new ArrayList<>(), members,new LinkedList<>(),new Guild.GuildHomeLocation("null",legendaryGuild.SERVER,0.0,0.0,0.0),0);
        caches.put(guild,guildData);
        //更新公会数据库
        updateGuild(guildData,false);

        //设置基础数据
        user.setDate(date);
        user.setGuild(guild);
        user.setPosition(legendaryGuild.getPositionsManager().getOwnerPosition().getId());
        user.setPoints(0.0,false);
        //更新数据库数据以及通知其他子服务器更新数据
        user.update(false);

        //防止残留
        GuildActivityData data = legendaryGuild.getGuildActivityManager().getData(guild);
        data.setCurrent(new HashMap<>());
        data.setHistory(new HashMap<>());
        data.setTotal_points(0);
        data.setPoints(0);
        data.update();

        //通知其他子服务器更新公会数据
        //....
        new NetWorkMessageBuilder().setMessageType(NetWorkMessageBuilder.MessageType.Forward)
                .setNetWorkMessage(new NetWorkMessage(NetWorkMessage.NetWorkType.UPDATE_GUILD, guild))
                .setReciver("ALL")
                .sendPluginMessage(p);
        return guildData;

    }

    public void deleteGuild(Guild guild){
        guild.getMembers().forEach(m -> {

            User user = legendaryGuild.getUserManager().getUser(m);
            user.setGuild(lang.default_guild);
            user.setPosition(lang.default_position);
            user.setPoints(0,false);
            user.setTotal_points(0);
            user.update(false);

            //发送消息
            MsgUtils.sendMessage(m,lang.plugin+lang.delete_broad_members);
        });

        //发送通报
        lang.delete_broad.forEach(msg -> {
            MsgUtils.sendBroad(msg.replace("%value%",guild.getDisplay()));
        });

        //移除公会数据并同步至其他子服务器
        guild.delete();
        Bukkit.getPluginManager().callEvent(new GuildDeleteEvent(guild));
    }

    public void giveGuild(Player owner,String target){
        User user = legendaryGuild.getUserManager().getUser(owner.getName());
        if (!user.hasGuild()){
            owner.sendMessage(lang.plugin+lang.nothasguild);
            return;
        }
        if (!user.getPosition().equals(legendaryGuild.getPositionsManager().getOwnerPosition().getId())){
            owner.sendMessage(lang.plugin+lang.notowner);
            return;
        }
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        User tagetUser = legendaryGuild.getUserManager().getUser(target);
        if (!tagetUser.hasGuild() || !guild.getMembers().contains(target)){
            owner.sendMessage(lang.plugin+lang.notmember);
            return;
        }

        guild.setOwner(target);
        guild.update();

        user.setPosition(legendaryGuild.getPositionsManager().getDefaultPosition().getId());
        user.update(false);

        tagetUser.setPosition(legendaryGuild.getPositionsManager().getOwnerPosition().getId());
        tagetUser.update(false);


        //删除玩家所有的活跃度
        GuildActivityData activityData = legendaryGuild.getGuildActivityManager().getData(guild.getGuild());
        activityData.clearPlayerData(user.getPlayer());
        activityData.update();

        owner.sendMessage(lang.plugin+lang.give_message.replace("%value%",guild.getDisplay()).replace("%target%",target));

        MsgUtils.sendMessage(target,lang.plugin+lang.give_message_target.replace("%value%",guild.getDisplay()));
        MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.give_broad.replace("%value%",target));

        Bukkit.getPluginManager().callEvent(new GuildGiveEvent(owner,target,guild));
    }

    public Guild getGuild(String guild){
        Guild data = caches.get(guild) ;
        if (data == null) {
            data = legendaryGuild.getDatabaseManager().getGuild(guild).orElse(null);
            if (data != null){
                caches.put(guild,data);
            }
        }
        return data;
    }


    public void updateGuild(Guild guild,boolean removeCache){
        legendaryGuild.getDatabaseManager().saveGuild(guild);
        if (removeCache){
            caches.remove(guild.getGuild());
            return;
        }
        caches.put(guild.getGuild(),guild);
    }

    public List<String> getGuilds(){
        return legendaryGuild.getDatabaseManager().getGuilds();
    }

    public void loadGuilds(){
        legendaryGuild.sync(new Runnable() {
            @Override
            public void run() {
                getGuilds().forEach(g -> {
                    Guild guild = legendaryGuild.getDatabaseManager().getGuild(g).orElse(null);
                    if (guild != null) {
                        caches.put(g,guild);
                    }
                });
                MsgUtils.sendConsole("Load "+caches.size()+" guild data." );
            }
        });

    }


    public static enum Sort{
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

