package com.gyzer.Data.Player;

import com.google.common.collect.Iterables;
import com.gyzer.API.GuildAPI;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.BungeeCord.NetWorkMessage;
import com.gyzer.Utils.BungeeCord.NetWorkMessageBuilder;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class User {
    private final Language lang = LegendaryGuild.getLegendaryGuild().getLanguageManager();
    private String player;
    private String guild;
    private String position;
    private String date;
    private WaterDataStore waterDataStore;
    private long cooldown;
    private boolean wish;
    private boolean teleport_guild_home;
    private double points;
    private double total_points;
    private PvpType pvp;
    private boolean chat;

    public User(String player, String guild, String position, String date, WaterDataStore waterDataStore, long cooldown, boolean wish, boolean teleport_guild_home, double points, double total_points,PvpType pvp ) {
        this.player = player;
        this.guild = guild;
        this.position = position;
        this.date = date;
        this.waterDataStore = waterDataStore;
        this.cooldown = cooldown;
        this.wish = wish;
        this.teleport_guild_home = teleport_guild_home;
        this.points = points;
        this.total_points = total_points;
        this.pvp = pvp;
        this.chat = false;
    }


    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }


    public boolean hasGuild(){

        if (guild == null || guild.isEmpty() || (guild != null && guild.equals(lang.default_guild))){
            return false;
        }
        Guild guild = LegendaryGuild.getLegendaryGuild().getGuildsManager().getGuild(this.guild);
        if (guild == null) {
            LegendaryGuild.getLegendaryGuild().info("检测到玩家"+player+"目前所在的公会:"+this.guild+"并不存在, 已重置该玩家的公会数据...", Level.SEVERE);
            LegendaryGuild.getLegendaryGuild().info("Detected player "+player+"'s current guild: "+this.guild+" does not exist. The guild data for this player has been reset", Level.SEVERE);
            setGuild(lang.default_guild);
            setTeleport_guild_home(false);
            setChat(false);
            setPosition(lang.default_position);
            setPoints(0,false);
            setTotal_points(0);
            update(false);
            return false;
        }
        return true;
    }
    public PvpType getPvp() {
        return pvp;
    }

    public String getPlayer() {
        return player;
    }

    public String getGuild() {
        return guild;
    }

    public String getPosition() {
        return position;
    }

    public String getDate() {
        return date;
    }

    public WaterDataStore getWaterDataStore() {
        return waterDataStore;
    }

    public long getCooldown() {
        return cooldown;
    }

    public int getCooldownSeconds() {
        Long start = cooldown;
        if (start == null) return 0;
        if (start == 0) return 0;
        long endTime = cooldown + TimeUnit.SECONDS.toMillis(LegendaryGuild.getLegendaryGuild().getConfigManager().COOLDOWN);
        long cd = endTime - System.currentTimeMillis();
        return (int) (cd/1000);
    }

    public boolean isInCoolDown() {
        Long start = cooldown;
        if (start == null) return false;
        if (start == 0) return false;

        long endTime = cooldown + TimeUnit.SECONDS.toMillis(LegendaryGuild.getLegendaryGuild().getConfigManager().COOLDOWN);
        return System.currentTimeMillis() < endTime;
    }


    public boolean isWish() {
        return wish;
    }

    public boolean isTeleport_guild_home() {
        return teleport_guild_home;
    }

    public double getPoints() {
        return points;
    }

    public double getTotal_points() {
        return total_points;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public void setPvp(PvpType pvp) {
        this.pvp = pvp;
    }

    public void setWish(boolean wish) {
        this.wish = wish;
    }

    public void setWaterDataStore(WaterDataStore waterDataStore) {
        this.waterDataStore = waterDataStore;
    }

    public void setTeleport_guild_home(boolean teleport_guild_home) {
        this.teleport_guild_home = teleport_guild_home;
    }



    public void setTotal_points(double total_points) {
        this.total_points = total_points;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addPoints(double amount,boolean sendMsg){
        this.points += amount;
        this.total_points += amount;
        if (sendMsg){
            MsgUtils.sendMessage(player,lang.plugin+lang.uset_recive_points.replace("%value%",amount+""));
        }
    }
    public void takePoints(double amount,boolean sendMsg){
        this.points = this.points - amount >= 0? this.points - amount : 0;
        if (sendMsg){
            MsgUtils.sendMessage(player,lang.plugin+lang.uset_decrease_points.replace("%value%",""+amount));
        }
    }
    public void setPoints(double points,boolean sendMsg) {
        this.points = points;

        if (sendMsg){
            MsgUtils.sendMessage(player,lang.plugin+lang.uset_set_points.replace("%value%",points+""));
        }
    }


    public void update(boolean r) {
        LegendaryGuild.getLegendaryGuild().getUserManager().updateUser(this, r);
        if (LegendaryGuild.getLegendaryGuild().getNetWork().isEnable()) {
            Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(),null);
            if (p != null) {
                new NetWorkMessageBuilder()
                        .setReciver("ALL")
                        .setNetWorkMessage(new NetWorkMessage(NetWorkMessage.NetWorkType.UPDATE_USER, this.player))
                        .setMessageType(NetWorkMessageBuilder.MessageType.Forward)
                        .sendPluginMessage(p);
            }
        }
    }

    public enum PvpType {
        ALL,
        NO_SAME_GUILD,
        BLACK_GUILD;
        public static PvpType getType(String str){
            try {
                PvpType type = PvpType.valueOf(str);
                return type;
            } catch (IllegalArgumentException e){
                return ALL;
            }
        }
    }
}
