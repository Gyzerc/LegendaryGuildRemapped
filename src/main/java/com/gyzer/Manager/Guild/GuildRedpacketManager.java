package com.gyzer.Manager.Guild;

import com.google.common.collect.Iterables;
import com.gyzer.Configurations.Files.GuildRedpacketConfigManager;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.Guild_Redpacket;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.BungeeCord.NetWorkMessage;
import com.gyzer.Utils.BungeeCord.NetWorkMessageBuilder;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GuildRedpacketManager {
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private Language lang = legendaryGuild.getLanguageManager();
    private boolean enable;
    private HashMap<String, Guild_Redpacket> cache;
    private GuildRedpacketConfigManager guildRedpacketConfigManager;
    public GuildRedpacketManager() {
        cache = new HashMap<>();
        guildRedpacketConfigManager=new GuildRedpacketConfigManager(this);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public Guild_Redpacket getRedPacketData(String guild){
        Guild_Redpacket redpacket = cache.get(guild);
        if (redpacket == null) {
            redpacket= legendaryGuild.getDatabaseManager().getRedPacket(guild).orElse(new Guild_Redpacket(guild,new HashMap<>()));
            cache.put(guild,redpacket);
        }
        return redpacket;
    }

    public GuildRedpacketConfigManager getGuildRedpacketConfigManager() {
        return guildRedpacketConfigManager;
    }

    public void reloadCache() {
        cache = new HashMap<>();
    }

    public void createRedPacket(Player p, double total, int amount) {
        if (enable) {
            User user = legendaryGuild.getUserManager().getUser(p.getName());
            if (!user.hasGuild()) {
                p.sendMessage(lang.plugin + lang.nothasguild);
                return;
            }
            Guild guildData = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
            if (total < guildRedpacketConfigManager.MIN_REDPACKET_TOTAL) {
                p.sendMessage(lang.plugin + lang.redpacket_min_total);
                return;
            }
            if (amount < guildRedpacketConfigManager.MIN_REDPACKET_AMOUNT) {
                p.sendMessage(lang.plugin + lang.redpacket_min_amount);
                return;
            }
            if (amount > guildData.getMembers().size()){
                p.sendMessage(lang.plugin+lang.redpacket_create_amount_max);
                return;
            }
            if (legendaryGuild.getCompManager().getVaultHook().get(p) < total){
                p.sendMessage(lang.plugin+lang.vault_noenough.replace("%value%",total+""));
                return;
            }

            legendaryGuild.getCompManager().getVaultHook().take(p,total);

            UUID uuid = UUID.randomUUID();
            Guild_Redpacket redpackets = getRedPacketData(guildData.getGuild());
            Guild_Redpacket.Redpacket redpacket = new Guild_Redpacket.Redpacket(uuid, legendaryGuild.getDate(), p.getName(), total, total, amount, new HashMap<>());
            HashMap<UUID, Guild_Redpacket.Redpacket> list = redpackets.getRedpackets();
            list.put(uuid,redpacket);
            redpackets.setRedpackets(list);
            //更新数据库并通知其他子服务器
            updateRedPacket(redpackets);

            //发送消息
            p.sendMessage(lang.plugin+lang.redpacket_create.replace("%total%",""+total).replace("%amount%",""+amount));
            MsgUtils.sendGuildMessage(guildData.getMembers(),lang.plugin+lang.redpacket_create_broad.replace("%target%",p.getName()).replace("%total%",""+total).replace("%amount%",""+amount));
        }
    }

    public boolean grabRedPacket(String guildName, UUID uuid, Player p){
        if (!enable) return false;
        Language lang = LegendaryGuild.getLegendaryGuild().getLanguageManager();
        Guild guild = LegendaryGuild.getLegendaryGuild().getGuildsManager().getGuild(guildName);

        Guild_Redpacket guild_redpacket = getRedPacketData(guildName);
        Guild_Redpacket.Redpacket redpacket=guild_redpacket.getRedpackets().get(uuid);

        //检测是否已经领取过
        HashMap<String,Double> history = redpacket.getHistory();
        if (history.containsKey(p.getName())){
            p.sendMessage(lang.plugin+lang.redpacket_garb_already);
            return false;
        }

        int lessAmount = redpacket.getAmount() - redpacket.getHistory().size();
        if (redpacket.getLess() <= 0 || lessAmount <=0){
            p.sendMessage(lang.plugin+lang.redpacket_garb_no);
            return false;
        }

        HashMap<UUID, Guild_Redpacket.Redpacket> map = guild_redpacket.getRedpackets();

        double less = redpacket.getLess();
        double finaAmount = less;

        //如果只剩下最后一份则获取红包剩余的金额并删去红包缓存
        if (lessAmount == 1){
            //加入领取记录
            history.put(p.getName(),finaAmount);
            //排序领取记录并获取第一名
            //发送消息
            RedPacketHistoryData first = getFirst(history);
            MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin + lang.redpacket_garb.replace("%target%",redpacket.getPlayer()).replace("%value%",p.getName()).replace("%money%",""+finaAmount).replace("%less%",""+lessAmount));
            MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin+lang.redpacket_garb_finally.replace("%target%",redpacket.getPlayer()).replace("%luck%",""+first.getPlayer()).replace("%value%",first.getAmount()+""));

            //更新数据
            map.remove(uuid);
            guild_redpacket.setRedpackets(map);

        }
        else {
            //随机金额
            double maxRoll =( redpacket.getTotal() / redpacket.getAmount() ) * 2;
            finaAmount = (new Random()).nextInt((int) (maxRoll * 10)) / 10;

            //更新数据
            history.put(p.getName(),finaAmount);
            redpacket.setLess(less - finaAmount >= 0 ? less-finaAmount : 0);
            map.put(uuid,redpacket);

            //排序领取记录并获取第一名
            //发送消息
            RedPacketHistoryData first = getFirst(history);
            MsgUtils.sendGuildMessage(guild.getMembers(),lang.plugin + lang.redpacket_garb.replace("%target%",redpacket.getPlayer()).replace("%value%",p.getName()).replace("%money%",""+finaAmount).replace("%less%",""+lessAmount));

        }

        //更新数据库并通知其他子服务器
        updateRedPacket(guild_redpacket);
        //给与奖励
        //legendaryGuild.getHookManager().getVaultHook().getEconomy().depositPlayer(p,finaAmount);

        return true;
    }

    public void reloadRedPacket(String guild){
        cache.remove(guild);
    }
    public void updateRedPacket(Guild_Redpacket redpacket){
        if (!enable) return;
        LegendaryGuild.getLegendaryGuild().getDatabaseManager().saveRedPacket(redpacket);
        cache.remove(redpacket.getGuild());

        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(),null);
        if (p != null) {
            //通知其他子服务器
            new NetWorkMessageBuilder().setMessageType(NetWorkMessageBuilder.MessageType.Forward)
                    .setReciver("ALL")
                    .setNetWorkMessage(new NetWorkMessage(NetWorkMessage.NetWorkType.UPDATE_REDPACKEY, redpacket.getGuild()))
                    .sendPluginMessage(p);
        }
    }

    public RedPacketHistoryData getFirst(HashMap<String,Double> map){
        if (!enable) return null;
        if (map != null && !map.isEmpty()) {
            List<RedPacketHistoryData> list = new ArrayList<>();
            map.forEach((p,a)->{
                list.add(new RedPacketHistoryData(p,a));
            });
            Collections.sort(list, new Comparator<RedPacketHistoryData>() {
                @Override
                public int compare(RedPacketHistoryData o1, RedPacketHistoryData o2) {
                    return (o1.getAmount() > o2.getAmount()) ? -1 : ((o1.getAmount() == o2.getAmount()) ? 0 : 1);
                }
            });
            return list.get(0);
        }
        return null;
    }


    public class RedPacketHistoryData {
        private String player;
        private double amount;

        public RedPacketHistoryData(String player, double amount) {
            this.player = player;
            this.amount = amount;
        }

        public String getPlayer() {
            return player;
        }

        public double getAmount() {
            return amount;
        }
    }
}
