package com.gyzer.API;

import com.google.common.collect.Iterables;
import com.gyzer.API.Events.*;
import com.gyzer.Configurations.Config;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Guild.CreateGuildSection;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Guild.TeamShop.GuildTeamShopData;
import com.gyzer.Data.Guild.TeamShop.TeamShopItem;
import com.gyzer.Data.Other.Buff;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Other.WaterPot;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import com.gyzer.Data.Player.WaterDataStore;
import com.gyzer.LegendaryGuild;
import com.gyzer.Listeners.PlayerMoveEvent;
import com.gyzer.Utils.BungeeCord.NetWorkMessage;
import com.gyzer.Utils.BungeeCord.NetWorkMessageBuilder;
import com.gyzer.Utils.MsgUtils;
import com.gyzer.Utils.RunTaskUtils;
import com.gyzer.Utils.RunUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GuildAPI {
    private static LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private static Config config = legendaryGuild.getConfigManager();
    private static Language lang = legendaryGuild.getLanguageManager();
    public static boolean createGuild(Player p,String guild){
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        //是否处于冷却
        if (user.isInCoolDown()){
            p.sendMessage(lang.plugin+lang.create_cooldown.replace("%value%",user.getCooldownSeconds()+""));
            return false;
        }
        //是否在公会内
        if (user.hasGuild()){
            p.sendMessage(lang.plugin+lang.already_in_guild);
            return false;
        }
        //获取玩家权限对应的创建组
        CreateGuildSection section = legendaryGuild.getCreateGroupsManager().getPlayerGroup(p).orElse(null);
        if (section == null){
            legendaryGuild.info("创建公会的默认组缺失,请重新生成config.yml...", Level.SEVERE);
            return false;
        }
        //检测是否符合创建条件组
        if (!legendaryGuild.getCreateGroupsManager().checkGroup(p,section)){
            return false;
        }
        //检测名字是否过长
        if (guild.length() > config.max_length){
            p.sendMessage(lang.plugin+lang.create_length);
            return false;
        }
        //检测是否有同名的公会
        if (legendaryGuild.getGuildsManager().getGuilds().contains(guild)){
            p.sendMessage(lang.plugin+lang.create_exists);
            return false;
        }
        //处理条件组
        legendaryGuild.getCreateGroupsManager().dealGroup(p,section);

        //创建公会..
        Guild data = legendaryGuild.getGuildsManager().createGuild(guild,p);

        //发送消息
        MsgUtils.sendMessage(p.getName(),lang.plugin+lang.create_message.replace("%value%",data.getDisplay()));
        lang.create_broad.forEach(msg -> {
            MsgUtils.sendBroad(msg.replace("%value%",data.getDisplay()).replace("%target%",user.getPlayer()));
        });


        //触发事件
        Bukkit.getPluginManager().callEvent(new CreateGuildEvent(p,data));
        return true;
    }

    public static void takeGuildExp(Guild guild,double amount){
        int level = guild.getLevel();
        double exp = guild.getExp();
        if (level <= 0){
            double take = exp -amount >= 0 ? amount : exp;
            guild.setExp(exp - take);
            guild.update();
            return;
        }
        if (exp - amount >= 0){
            guild.setExp(exp - amount);
            guild.update();
            return;
        }
        guild.setExp(config.EXP.get(level-1));
        guild.update();
        double less = amount - exp;
        takeGuildLevel(guild,1);
        takeGuildExp(guild,less);

        Bukkit.getPluginManager().callEvent(new GuildExpChangeEvent(guild,amount));
    }

    public static void setGuildExp(Guild guild,double amount){
        if (amount >= 0){
            if (guild.getLevel() >= config.MAXLEVEL){
                return;
            }
            double set = Math.min(amount,config.EXP.get(guild.getLevel()));
            guild.setExp(set);
            guild.update();

            Bukkit.getPluginManager().callEvent(new GuildExpChangeEvent(guild,amount));
        }
    }
    public static void addGuildExp(String player,Guild guild,double amount) {
        int level = guild.getLevel();
        if (level == config.MAXLEVEL) {
            return;
        }
        double exp = guild.getExp();
        double total = exp + amount;
        double next = config.EXP.get(level);
        if (amount > 0) {
            MsgUtils.sendMessage(player, lang.plugin + lang.level_expadd.replace("%value%", amount + ""));
        }
        //检测是否可以升级
        if (total >= next) {
            double less = total - next;
            level += 1;

            guild.setExp(less);
            guild.update();

            //升级
            addGuildLevel(guild, 1);
            //检测是否还能升级
            if (level < config.MAXLEVEL && less >= config.EXP.get(level)) {
                //再次进行判定
                addGuildExp(player, guild, 0);
            }
        } else {
            guild.setExp(total);
            guild.update();
        }

        Bukkit.getPluginManager().callEvent(new GuildExpChangeEvent(guild, amount));
    }
    public static void addGuildLevel(Guild guild,int amount){
        int level = guild.getLevel();
        if (level == config.MAXLEVEL){
            return;
        }
        int add = level+amount <= config.MAXLEVEL ? amount : config.MAXLEVEL-level;
        guild.setLevel(level+add);
        guild.update();

        MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.level_levelup.replace("%value%",""+(level+add)));
        lang.level_levelup_broad.forEach(msg -> {
            MsgUtils.sendBroad(msg.replace("%target%",guild.getDisplay()).replace("%value%",""+(level+add)));
        });

        Bukkit.getPluginManager().callEvent(new GuildLevelupEvent(guild,level,(level+add)));
    }

    public static void takeGuildLevel(Guild guild,int amount){

        int level = guild.getLevel();
        if (level == 0){
            return;
        }
        int take = level -amount >= 0 ? amount : level;
        guild.setLevel(level-take);
        guild.update();

    }

    public static void setGuildLevel(Guild guild,int amount){
        if (amount >= 0) {
            int set = Math.min(amount, config.MAXLEVEL);
            guild.setLevel(set);
            guild.update();
        }
    }

    public static void addGuildTreeLevel(String player,Guild guild,int amount){
        int level = guild.getLevel();
        int treelevel = guild.getTreelevel();
        if (treelevel+1 > level){
            MsgUtils.sendMessage(player,lang.plugin+lang.tree_level_large);
            return;
        }
        if (treelevel == legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().MAX_TREE_LEVEL){
            MsgUtils.sendMessage(player,lang.plugin+lang.tree_level_max);
            return;
        }
        int up = amount + treelevel <= legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().MAX_TREE_LEVEL ? amount : legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().MAX_TREE_LEVEL-treelevel;
        int finalLevel = treelevel+up;
        MsgUtils.sendMessage(player,lang.plugin+lang.tree_levelup_byplayer);
        MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.tree_levelup.replace("%value%",""+finalLevel));


        guild.setTreelevel(finalLevel);
        //更新数据库并通知其他子服务器
        guild.update();

        Bukkit.getPluginManager().callEvent(new GuildTreeLevelupEvent(guild,treelevel,finalLevel));
    }

    public static boolean addGuildTreeLevelByPlayer(Player p){
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        if (!user.hasGuild()){
            p.sendMessage(lang.plugin+lang.nothasguild);
            return false;
        }
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        if (!guild.getOwner().equals(p.getName())){
            p.sendMessage(lang.plugin+lang.notowner);
            return false;
        }
        int level = guild.getTreelevel();
        double exp = guild.getTreeexp();
        double next = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.get(level);
        if (level + 1 > guild.getLevel()){
            p.sendMessage(lang.plugin+lang.tree_level_large);
            return false;
        }
        if (exp < next){
            p.sendMessage(lang.plugin+lang.tree_levelup_cant);
            return false;
        }
        List<String> requirements = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREE_REQUIREMENTS.get(level);
        if (legendaryGuild.getRequirementsManager().check(p,requirements)){
            legendaryGuild.getRequirementsManager().deal(p,requirements);

            guild.setTreeexp(0);
            guild.update();

            addGuildTreeLevel(p.getName(),guild,1);
            return true;
        }
        return false;

    }


    public static void takeGuildTreeLevel(String player,Guild guild,int amount) {
        int level = guild.getLevel();
        int take = level-amount >= 0 ? amount : level;
        guild.setTreelevel(level-take);
        //更新数据库并通知其他子服务器
        guild.update();
    }

    public static void setGuildTreeLevel(String player,Guild guild,int amount) {
        if (amount >= 0) {
            int set = Math.min(amount, legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().MAX_TREE_LEVEL);
            guild.setTreelevel(set);
            //更新数据库并通知其他子服务器
            guild.update();
        }
    }


    public static void addGuildTreeExp(String player,Guild guild,double amount){
        if (guild.getTreelevel() == legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().MAX_TREE_LEVEL){
            return;
        }
        double exp = guild.getTreeexp();
        double next =  legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.get(guild.getTreelevel());
        double set = Math.min(exp + amount, next);
        guild.setTreeexp(set);
        guild.update();

        MsgUtils.sendMessage(player,lang.plugin+lang.tree_expadd_byplayer.replace("%target%",guild.getDisplay()).replace("%value%",(set-exp)+""));
        Bukkit.getPluginManager().callEvent(new GuildTreeExpChangeEvent(guild,set));
    }

    public static void takeGuildTreeExp(String player,Guild guild,double amount){
        double exp = guild.getTreeexp();
        double take = exp - amount >= 0 ? amount : exp;
        guild.setTreeexp(exp - take);
        guild.update();

        Bukkit.getPluginManager().callEvent(new GuildTreeExpChangeEvent(guild,(exp - take)));
    }

    public static void setGuildTreeExp(String player,Guild guild,double amount){
        if (amount >= 0) {
            if (guild.getTreelevel() == legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().MAX_TREE_LEVEL){
                return;
            }
            double next = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.get(guild.getTreelevel());
            double set = Math.min(amount,next);
            guild.setTreeexp(set);
            guild.update();

            Bukkit.getPluginManager().callEvent(new GuildTreeExpChangeEvent(guild,set));
        }
    }

    public static String getGuildTreeExpProgressBar(Guild guild){
        StringBuilder builder = new StringBuilder();
        if (legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREE_BAR_LENGTH > 0) {
            int length = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREE_BAR_LENGTH;

            int level = guild.getTreelevel();
            double exp = guild.getTreeexp();
            double next = level + 1 <= legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().MAX_TREE_LEVEL ? legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.get(level) : exp;


            float percent = Float.isNaN((float) (exp / next)) ? 100.0F : (float) Math.abs(Math.min(exp / next, 100.0F));
            int completedAmount = (int) (percent * length);

            int a = 0;
            while (a < length){
                if (a < completedAmount){
                    builder.append(legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREE_BAR_COMPLETED);
                } else {
                    builder.append(legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREE_BAR_EMPTRY);
                }
                a++;
            }
        }
        return builder.toString();
    }

    public static void addGuildActivity(Player p, Guild guild, double amount, boolean player){

        Guild addGuild = guild;
        GuildActivityData data = null;
        if (player) {
            if (p != null) {
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                if (!user.hasGuild()) {
                    return;
                }
                addGuild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                data = legendaryGuild.getGuildActivityManager().getData(addGuild.getGuild());

                p.sendMessage(lang.plugin + lang.activity_gain.replace("%value%", "" + amount));

                data.setPlayerHistoryActivity(p.getName(), data.getPlayerTotalActivity(p.getName()) + amount);
                data.setPlayerActivity(p.getName(), data.getPlayerActivity(p.getName()) + amount);
            }
        } else {
            data = legendaryGuild.getGuildActivityManager().getData(addGuild.getGuild());
        }
        data.setPoints( data.getPoints() + amount);
        data.setTotal_points( data.getTotal_points() + amount);
        data.update();

        Bukkit.getPluginManager().callEvent(new GuildActivityChangeEvent(addGuild, GuildActivityChangeEvent.ChangeType.Add,amount));
    }

    public static double takeGuildActivity(Player p, Guild guild,double amount, boolean player){
        Guild takeGuild = guild;
        if (player){
            if (p!=null){
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                if (user.hasGuild()){
                    takeGuild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                }
            }
            return 0.0;
        }

        GuildActivityData data = legendaryGuild.getGuildActivityManager().getData(takeGuild.getGuild());
        double take = Math.min(data.getPoints(), amount);
        data.setPoints( data.getPoints() - take);
        data.update();

        Bukkit.getPluginManager().callEvent(new GuildActivityChangeEvent(takeGuild, GuildActivityChangeEvent.ChangeType.Take,take));

        return take;
    }

    public static double setGuildActivity(Player p, Guild guild,double amount, boolean player){
        Guild setGuild = guild;
        if (player){
            if (p!=null){
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                if (user.hasGuild()){
                    setGuild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                }
            }
            return 0.0;
        }

        GuildActivityData data = legendaryGuild.getGuildActivityManager().getData(setGuild.getGuild());
        double set = Math.min(0,amount);
        data.setPoints(set);
        data.update();

        Bukkit.getPluginManager().callEvent(new GuildActivityChangeEvent(setGuild, GuildActivityChangeEvent.ChangeType.Set,set));

        return set;
    }


    public static void giveMoney(Player p,double amount){
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        if (!user.hasGuild()){
            p.sendMessage(lang.plugin+lang.nothasguild);
            return;
        }
        Guild guild = legendaryGuild.getGuildsManager().getGuild(p.getName());
        if (guild != null){
            if (legendaryGuild.getCompManager().getVaultHook().isEnable()) {
                if (legendaryGuild.getCompManager().getVaultHook().get(p) >=amount) {

                    //转化为贡献点
                    double toPoints = config.MONEY_TO_POINTS;
                    double addPoints = 0;
                    if (toPoints > 0) {
                        addPoints = amount * toPoints;
                        user.addPoints(addPoints, true);
                        user.update(false);
                    }
                    //扣除金币
                    legendaryGuild.getCompManager().getVaultHook().take(p,amount);

                    //增加公会资金
                    guild.addMoney(amount);
                    guild.update();

                    p.sendMessage(lang.plugin+lang.money_message.replace("%value%",""+amount));
                    MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.money_message_broad.replace("%target%",p.getName()).replace("%value%",""+amount));
                    Bukkit.getPluginManager().callEvent(new GiveGuildMoneyEvent(p,amount));
                    return;
                }
                p.sendMessage(lang.plugin+lang.vault_noenough.replace("%value%",""+amount));
                return;
            }
        }
    }

    public static void sendApplication(User user, String guildName) {
        //玩家是否在公会内
        if (user.hasGuild()){
            MsgUtils.sendMessage(user.getPlayer(),lang.plugin+lang.already_in_guild);
            return;
        }
        //是否存在该公会
        if (!legendaryGuild.getGuildsManager().isExists(guildName)){
            MsgUtils.sendMessage(user.getPlayer(),lang.plugin+lang.notguild);
            return;
        }
        //是否处于冷却
        if (user.isInCoolDown()){
            MsgUtils.sendMessage(user.getPlayer(),lang.plugin+lang.create_cooldown.replace("%value%",user.getCooldownSeconds()+""));
            return;
        }
        Guild guild = legendaryGuild.getGuildsManager().getGuild(guildName);

        LinkedList<Guild.Application> applications=guild.getApplications();
        List<Guild.Application> list = applications.stream().filter(application -> {
            if (application.getPlayer().equals(user.getPlayer())){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        //是否已经发送过申请
        if (list.size() > 0){
            MsgUtils.sendMessage(user.getPlayer(),lang.plugin+lang.application_already);
            return;
        }
        //添加到申请列表中
        guild.addApplication(user.getPlayer());
        //更新数据库
        guild.update();


        //发送消息
        MsgUtils.sendMessage(user.getPlayer(),lang.plugin+lang.application_send.replace("%value%",guild.getDisplay()));
        MsgUtils.sendMessage(guild.getOwner(),lang.plugin+lang.application_recive.replace("%player%",user.getPlayer()));

        //触发事件
        Bukkit.getPluginManager().callEvent(new PlayerApplicationGuildEvent(user, guild));
    }

    public static boolean setPlayerPositionByPlayer(Player seter, String target, String positionId){
        if (seter.getName().equals(target)){
            seter.sendMessage(lang.plugin+lang.isowner);
            return false;
        }
        User user = legendaryGuild.getUserManager().getUser(seter.getName());
        //检测是否有公会
        if (!user.hasGuild()){
            seter.sendMessage(lang.plugin+lang.nothasguild);
            return false;
        }
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        //检测玩家是否是会长
        if (!guild.getOwner().equalsIgnoreCase(seter.getName())){
            seter.sendMessage(lang.plugin+lang.notowner);
            return false;
        }
        //检测该玩家在不在公会内
        if (!guild.getMembers().contains(target)){
            seter.sendMessage(lang.plugin+lang.notmember);
            return false;
        }
        //检测目标职位是否是会长
        if (positionId.equalsIgnoreCase(legendaryGuild.getPositionsManager().getOwnerPosition().getId())){
            seter.sendMessage(lang.plugin+lang.admin_set_position_cant_owner);
            return false;
        }
        Position position = legendaryGuild.getPositionsManager().getPosition(positionId).orElse(null);
        //检测是否有该职位
        if (position == null){
            seter.sendMessage(lang.plugin+lang.admin_set_position_null);
            return false;
        }
        //检测该职位是否人员达到上限
        if (getGuildPositionAmount(guild,position) >= position.getMax()){
            seter.sendMessage(lang.plugin+lang.admin_set_position_cant_max);
            return false;
        }

        User TargetUser = legendaryGuild.getUserManager().getUser(target);
        if (!TargetUser.hasGuild()){
            seter.sendMessage(lang.plugin+lang.admin_target_nothasguild);
            return false;
        }
        if (!TargetUser.getGuild().equals(guild.getGuild())){
            seter.sendMessage(lang.plugin+lang.notmember);
            return false;
        }
        String oldPosition = TargetUser.getPosition();
        TargetUser.setPosition(positionId);
        TargetUser.update(false);

        seter.sendMessage(lang.plugin+lang.positions_message.replace("%target%",target).replace("%value%",position.getDisplay()));
        MsgUtils.sendMessage(target,lang.plugin+lang.positions_message_target.replace("%value%",position.getDisplay()));

        Bukkit.getPluginManager().callEvent(new PlayerPositionChangeEvent(TargetUser,oldPosition,positionId));
        return true;
    }

    public static int getGuildPositionAmount(Guild guild, Position position){
        List<String> samePositionMembers = guild.getMembers().stream().filter(member -> {
            User target = legendaryGuild.getUserManager().getUser(member);
            return target.getPosition().equals(position.getId());
        }).collect(Collectors.toList());
        return samePositionMembers.size();
    }
    public static void quitGuild(Player p){
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        if (!user.hasGuild()){
            p.sendMessage(lang.plugin+lang.nothasguild);
            return;
        }
        if (user.getPosition().equals(legendaryGuild.getPositionsManager().getOwnerPosition().getId())){
            p.sendMessage(lang.plugin+lang.quit_owner);
            return;
        }
        String guildName = user.getGuild();
        String position = legendaryGuild.getPositionsManager().getPosition(user.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition()).getDisplay();

        user.setGuild(lang.default_guild);
        user.setPosition(lang.default_position);
        user.setPoints(0,false);
        user.setTotal_points(0);
        user.setCooldown(System.currentTimeMillis());
        user.update(false);

        Guild guild = legendaryGuild.getGuildsManager().getGuild(guildName);
        LinkedList<String> members = guild.getMembers();
        members.remove(members.indexOf(p.getName()));
        guild.setMembers(members);
        guild.update();


        //删除玩家所有的活跃度
        GuildActivityData activityData = legendaryGuild.getGuildActivityManager().getData(guildName);
        activityData.clearPlayerData(user.getPlayer());
        activityData.update();

        //刷新公会buff
        updatePlayerBuffAttribute(p);


        Bukkit.getPluginManager().callEvent(new PlayerQuitGuildEvent(p,guild));

        MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.quit_broad.replace("%value%",p.getName()).replace("%position%",position));
        p.sendMessage(lang.plugin+lang.quit_message.replace("%value%",guild.getDisplay()));
    }



    public static void resetGuildTeamShopData(Guild guild) {
        GuildTeamShopData teamShopData = guild.getGuildTeamShopData();
        teamShopData.clearBuys();
        teamShopData.update(false);
    }
    public static void resetGuildTeamShopData(Guild guild,String player,int amount) {

        GuildTeamShopData teamShopData = legendaryGuild.getTeamShopManager().getGuildTeamShopData(guild.getGuild());
        teamShopData.resetPlayerBuys(player,amount);
        teamShopData.update(false);

    }

    public static void refreshGuildTeamShopItem(Guild guild) {
        GuildTeamShopData teamShopData = guild.getGuildTeamShopData();
        teamShopData.randomShop();
        teamShopData.update(false);
    }

    public static void setGuildHome(Player setter,Guild guild) {
        if (guild != null){
            if (!guild.getOwner().equals(setter.getName())){
                setter.sendMessage(lang.plugin+lang.notowner);
                return;
            }

            Location loc = setter.getLocation();
            String world = loc.getWorld().getName();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();

            if (config.HOME_BLACK_SERVER.contains(legendaryGuild.SERVER)){
                setter.sendMessage(lang.plugin+lang.home_cant_server);
                return;
            }
            if (config.HOME_BLACK_WORLD.contains(world)){
                setter.sendMessage(lang.plugin+lang.home_cant_world);
                return;
            }
            Guild.GuildHomeLocation homeLocation = new Guild.GuildHomeLocation(world, legendaryGuild.SERVER, x,y,z);
            guild.setHome(homeLocation);
            guild.update();

            setter.sendMessage(lang.plugin+lang.home_set);
            MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.home_set_broad);

            Bukkit.getPluginManager().callEvent(new GuildHomeChangeEvent(setter,guild,homeLocation));
        }
    }

    public static void teleportGuildHome(Player p,User user,Guild guild) {
        if (!user.hasGuild()){
            p.sendMessage(lang.plugin+lang.nothasguild);
            return;
        }
        if (guild != null){
            Guild.GuildHomeLocation location = guild.getHome();
            if (location == null){
                p.sendMessage(lang.plugin+lang.home_home_null);
                return;
            }

            GuildHomeTeleportEvent event = new GuildHomeTeleportEvent(p);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            PlayerMoveEvent.addPlayerWaitTeleport(p.getName());
            new RunTaskUtils(20,config.HOME_WAIT,p)
                    .setTaskEveryPeriod(m -> {
                        if (PlayerMoveEvent.hasPlayerWaitTeleport(p.getName())) {
                            if (config.HOME_SOUND_SECOND != null) {
                                p.playSound(p.getLocation(), config.HOME_SOUND_SECOND, 1, 1);
                            }
                            int less = config.HOME_WAIT - m.getSec();
                            p.sendMessage(lang.plugin + lang.home_wait.replace("%value%", "" + less));
                        }
                        else {
                            m.cancel();
                        }
                    })
                    .setConsumerEnd(m -> {
                        teleport(p,user,location);
                    }).start();
        }
    }
    private static void teleport(Player p,User user, Guild.GuildHomeLocation location){
        PlayerMoveEvent.deletePlayerWaitTeleport(p.getName());
        if (location.getServer().equals(legendaryGuild.SERVER)){
            if (location.getLocation().isPresent()){
                legendaryGuild.sync(()->
                {
                    p.teleport(location.getLocation().get());
                    p.sendMessage(lang.plugin+lang.home_teleport);
                    if (config.HOME_SOUND_TELEPORT != null) {
                        p.playSound(p.getLocation(), config.HOME_SOUND_TELEPORT, 1, 1);
                    }
                });
            }
        }
        else {
            user.setTeleport_guild_home(true);
            user.update(false);
            legendaryGuild.getNetWork().teleportServer(p,location.getServer());
        }
    }
    public static boolean removePlayerPosition(Player seter,String target){
        User user = legendaryGuild.getUserManager().getUser(seter.getName());
        //检测是否有公会
        if (!user.hasGuild()){
            seter.sendMessage(lang.plugin+lang.nothasguild);
            return false;
        }
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        //检测玩家是否是会长
        if (!guild.getOwner().equalsIgnoreCase(seter.getName())){
            seter.sendMessage(lang.plugin+lang.notowner);
            return false;
        }
        //检测该玩家在不在公会内
        if (!guild.getMembers().contains(target)){
            seter.sendMessage(lang.plugin+lang.notmember);
            return false;
        }

        String defaultId = legendaryGuild.getPositionsManager().getDefaultPosition().getId();
        User targetUser = legendaryGuild.getUserManager().getUser(target);
        String old = targetUser.getPosition();
        if (old.equals(legendaryGuild.getPositionsManager().getOwnerPosition().getId())){
            seter.sendMessage(lang.plugin+lang.admin_set_position_cant);
            return false;
        }
        if (!targetUser.getPosition().equals(defaultId)) {
            targetUser.setPosition(defaultId);
            targetUser.update(false);
            Bukkit.getPluginManager().callEvent(new PlayerPositionChangeEvent(targetUser,old,defaultId));
        }

        seter.sendMessage(lang.plugin+lang.positions_message_cancel.replace("%value%",target));
        MsgUtils.sendMessage(target,lang.plugin+lang.positions_message_cancel_target);

        return true;
    }

    public static boolean GuildTreeWish(User user) {
        Player p = Bukkit.getPlayerExact(user.getPlayer());
        if (p != null) {
            if (user.hasGuild()) {
                if (user.isWish()) {
                    MsgUtils.sendMessage(user.getPlayer(), lang.plugin + lang.tree_wish_already);
                    return false;
                }
                user.setWish(true);
                //更新数据库并通知其他子服务器
                user.update(false);

                MsgUtils.sendMessage(user.getPlayer(), lang.plugin + lang.tree_wish);
                Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                MsgUtils.sendGuildMessage(guild.getMembers(), lang.plugin + lang.tree_wish_broad.replace("%target%", user.getPlayer()));

                int level = guild.getTreelevel();
                List<String> rewards = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().WISH.get(level) != null ? legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().WISH.get(level) : new ArrayList<>();
                RunUtils runUtils = new RunUtils(rewards, p);
                runUtils.start();

                Bukkit.getPluginManager().callEvent(new GuilTreeWishEvent(p,guild));
                return true;
            }
        }
        return false;
    }

    public static boolean GuildTreeWater(Player p, String potId) {
        Optional<WaterPot> potOp = legendaryGuild.getGuildTreeManager().getWaterPot(potId);
        if (potOp.isPresent()) {
            User user = legendaryGuild.getUserManager().getUser(p.getName());
            if (user.hasGuild()) {
                WaterDataStore waterDataStore = user.getWaterDataStore();
                WaterPot pot = potOp.get();

                List<String> requirements = pot.getRequirements();
                if (legendaryGuild.getRequirementsManager().check(p,requirements)) {
                    if (pot.getLimit_day() != -1) {
                        if (waterDataStore.getAmount(potId, WaterDataStore.WaterDataType.TODAY) >= pot.getLimit_day()) {
                            p.sendMessage(lang.plugin + lang.tree_water_limit);
                            return false;
                        }
                    }
                    legendaryGuild.getRequirementsManager().deal(p, requirements);
                    waterDataStore.addAmount(potId, WaterDataStore.WaterDataType.TODAY, 1);
                    waterDataStore.addAmount(potId, WaterDataStore.WaterDataType.TOTAL, 1);
                    user.setWaterDataStore(waterDataStore);
                    user.update(false);

                    p.sendMessage(lang.plugin + lang.tree_water.replace("%value%", pot.getDisplay()));
                    Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                    MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.tree_water_broad.replace("%target%",p.getName()).replace("%value%",pot.getDisplay()));

                    pot.use(p,user,guild);
                    Bukkit.getPluginManager().callEvent(new GuildTreeWaterEvent(p,pot,guild));
                    return true;
                }
            }
        }
        return false;
    }

    public static void JoinGuild(User user, Guild guild) {
        if (user.hasGuild()){
            return;
        }
        if (guild.getMembers().size() >= guild.getMaxMembers()){
            return;
        }

        //设置玩家数据
        user.setGuild(guild.getGuild());
        user.setPosition(legendaryGuild.getPositionsManager().getDefaultPosition().getId());
        user.setPoints(0.0,false);
        user.setTotal_points(0.0);
        user.setDate(legendaryGuild.getDate());
        //更新数据库并同步子服数据
        user.update(false);

        //添加成员
        guild.getMembers().add(user.getPlayer());
        //更新数据库发送同步更新信息
        guild.update();

        //发送通知
        MsgUtils.sendMessage(user.getPlayer(),lang.plugin+lang.application_join.replace("%value%",guild.getDisplay()));
        MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.application_join_broad.replace("%value%",user.getPlayer()));

        Bukkit.getPluginManager().callEvent(new PlayerJoinGuildEvent(user,guild));

        //更新BUFF
        updatePlayerBuffAttribute(user.getPlayer() , true);
    }
    public static boolean kick(Guild guild, CommandSender sender, String name){
        if (guild.getOwner().equals(name)) {
            return false;
        }

        User user = legendaryGuild.getUserManager().getUser(name);
        if (user != null) {
            //设置用户数据
            user.setGuild(lang.default_guild);
            user.setPoints(0, false);
            user.setTotal_points(0);
            user.setPosition(lang.default_position);
            user.setCooldown(config.COOLDOWN * 60);
            user.setDate("");
            //更新数据库并通知其他子服务器
            user.update(false);
        }


        //设置公会数据
        guild.getMembers().remove(name);
        //更新数据库通知其他子服务器
        guild.update();

        //删除玩家所有的活跃度
        GuildActivityData activityData = legendaryGuild.getGuildActivityManager().getData(guild.getGuild());
        activityData.clearPlayerData(name);
        activityData.update();

        //发送消息
        if (sender != null){
            sender.sendMessage(lang.plugin+lang.members_kick.replace("%value%",name));
        }
        MsgUtils.sendMessage(name,lang.plugin+lang.members_bekick.replace("%value%",guild.getDisplay()));
        MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.members_kick_broad.replace("%value%",name));

        Bukkit.getPluginManager().callEvent(new PlayerBeKickFromGuildEvent(name,guild));


        //刷新玩家BUFF
        updatePlayerBuffAttribute(name,true);
        return true;
    }



    public static boolean buyGuildTeamShop(Player p, User user, Guild guild, TeamShopItem shopItem, GuildTeamShopData teamShopData) {

        if (shopItem.getLimit() > 0 && (shopItem.getLimit() <= teamShopData.getBuyAmount(p.getName()))) {
            p.sendMessage(lang.plugin + lang.bargain_buy_limit.replace("%limit%",String.valueOf(shopItem.getLimit())));
            return false;
        }

        boolean canBuy = false;
        double price = teamShopData.getCurrentPrice();;
        switch (shopItem.getCurrency()) {
            case VAULT: {
                if (legendaryGuild.getCompManager().getVaultHook().isEnable()) {
                    if (legendaryGuild.getCompManager().getVaultHook().get(p) >= price) {
                        canBuy = true;
                        legendaryGuild.getCompManager().getVaultHook().take(p,price);
                    }
                    p.sendMessage(lang.plugin + lang.reuirement_notenough_vault.replace("%value%" , String.valueOf(price)));
                    break;
                }
                legendaryGuild.info("You not install Vault .",Level.SEVERE);
                break;
            }
            case PLAYERPOINTS: {
                if (legendaryGuild.getCompManager().getPlayerPointsHook().isEnable()) {
                    if (legendaryGuild.getCompManager().getPlayerPointsHook().get(p.getUniqueId()) >= price) {
                        canBuy = true;
                        legendaryGuild.getCompManager().getPlayerPointsHook().take(p.getUniqueId(), (int) price);
                        break;
                    }
                    p.sendMessage(lang.plugin + lang.reuirement_notenough_playerpoints.replace("%value%" , String.valueOf(price)));
                    break;
                }
                legendaryGuild.info("You not install PlayerPoints",Level.SEVERE);
                break;
            }
            case GUILD_POINTS: {
                if (user.getPoints() >= price) {
                    canBuy = true;
                    user.takePoints(price , false);
                    user.update(false);
                    break;
                }
                break;
            }
        }
        if (canBuy) {
            //增加购买次数
            teamShopData.addBuyAmount(p.getName(),1);

            String display = shopItem.getDisplay();
            p.sendMessage(lang.plugin + lang.bargain_buy.replace("%display%",display)
                    .replace("%price%",String.valueOf(price)));
            MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin + lang.bargain_buy_broad.replace("%player%",p.getName())
                    .replace("%display%",display)
                    .replace("%price%",String.valueOf(price)));
            new RunUtils(shopItem.getRun(),p).start();

            Bukkit.getPluginManager().callEvent(new TeamShopBuyEvent(p,shopItem.getId()));
        }
        return canBuy;
    }

    public static boolean addGuildBuffLevel(Guild guild,Player owner, Buff buff){

        StringStore data = guild.getBuffs();
        int current = Integer.parseInt(data.getValue(buff.getId(),0).toString());
        if (current >= guild.getLevel()){
            owner.sendMessage(lang.plugin+lang.buff_cant);
            return false;
        }
        int max = buff.getMax();
        if (current >= max) {
            owner.sendMessage(lang.plugin+lang.buff_max);
            return false;
        }

        List<String> requirements = buff.getRequirements(current + 1);
        if (!legendaryGuild.getRequirementsManager().check(owner,requirements)){
            return false;
        }
        legendaryGuild.getRequirementsManager().deal(owner,requirements);

        //处理等级并同步数据
        data.setValue(buff.getId(), (current+1) ,1);
        guild.setBuffs(data);
        guild.update();

        //更新buff属性
        updateGuildMembersBuff(guild);

        MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.buff_levelup.replace("%target%",buff.getDisplay()).replace("%value%",""+(current+1)));
        Bukkit.getPluginManager().callEvent(new GuildBuffLevelupEvent(guild,buff,1));
        return true;
    }
    public static void updatePlayerBuffAttribute(String name,boolean netWork) {
        Player p = Bukkit.getPlayerExact(name);
        if (p != null) {
            updatePlayerBuffAttribute(p);
            return;
        }

        if (netWork && LegendaryGuild.getLegendaryGuild().getNetWork().isEnable()) {
            p = Iterables.getFirst(Bukkit.getOnlinePlayers(),null);
            if (p != null) {
                new NetWorkMessageBuilder()
                        .setReciver("ALL")
                        .setNetWorkMessage(new NetWorkMessage(NetWorkMessage.NetWorkType.UPDATE_PLAYER_BUFF, name))
                        .setMessageType(NetWorkMessageBuilder.MessageType.Forward)
                        .sendPluginMessage(p);
            }
        }
    }



    public static void updatePlayerBuffAttribute(Player p) {
        legendaryGuild.getBuffsManager().updatePlayerBuffAttribute(p);
    }

    public static void updateGuildMembersBuff(Guild guild){
        updateGuildMembersBuff(guild,true);
    }

    public static void updateGuildMembersBuff(Guild guild,boolean netWork) {
        legendaryGuild.getBuffsManager().updateGuildMembersBuff(guild);
        if (netWork && LegendaryGuild.getLegendaryGuild().getNetWork().isEnable()) {
            Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(),null);
            if (p != null) {
                new NetWorkMessageBuilder()
                        .setReciver("ALL")
                        .setNetWorkMessage(new NetWorkMessage(NetWorkMessage.NetWorkType.UPDATE_GUILD_BUFF, guild.getGuild()))
                        .setMessageType(NetWorkMessageBuilder.MessageType.Forward)
                        .sendPluginMessage(p);
            }
        }
    }
}
