package com.gyzer.Comp.Sub;

import com.gyzer.API.GuildAPI;
import com.gyzer.Comp.Hook;
import com.gyzer.Configurations.Config;
import com.gyzer.Configurations.Language;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.Panels.MainMenu;
import com.gyzer.Utils.MsgUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
public class ProtocolLibHook extends Hook {

    @Override
    public boolean getHook() {
        modify = new HashMap<>();
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            MsgUtils.sendConsole("Hooked ProtocolLib plugin." );
            register();
            return true;
        }
        legendaryGuild.info("未检测到ProtocolLib, 请安装该插件！ & ProtocolLib not detected, please install the plugin", Level.SEVERE);
        return false;
    }
    private HashMap<UUID,Input> modify;
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private Language lang = legendaryGuild.getLanguageManager();
    private Config config = legendaryGuild.getConfigManager();
    public ProtocolLibHook() {
        modify = new HashMap<>();
    }
    public void setModify(UUID uuid,int id) {
        modify.put(uuid,new Input(id,""));
    }
    public void setModify(UUID uuid,int id,String value) {
        modify.put(uuid,new Input(id,value));
    }
    public void removeModify(UUID uuid) {
        modify.remove(uuid);
    }
    public void register() {
        //公会聊天
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(LegendaryGuild.getLegendaryGuild(), PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player p = event.getPlayer();
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                PacketContainer container = event.getPacket();
                String str = container.getStrings().read(0);
                if (user.hasGuild()) {
                    UUID uuid = p.getUniqueId();
                    //编辑公会
                    if (modify.containsKey(uuid)) {
                        event.setCancelled(true);
                        Input input = modify.remove(uuid);
                        int mode = input.getId();
                        String value = input.getValue();
                        if (!str.equalsIgnoreCase("cancel")) {
                            Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                            String target = MsgUtils.color(str);
                            switch (mode) {
                                case 0 :
                                    List<String> intro = new ArrayList<>(guild.getIntro());
                                    if (intro.size() < config.DESC_MAX_LENGTH) {
                                        intro.add(target);
                                        guild.setIntro(intro);
                                        guild.update();
                                        p.sendMessage(lang.plugin + lang.intro_add.replace("%value%", target));
                                        openMain(p);
                                        return;
                                    }
                                    p.sendMessage(lang.plugin + lang.max_length_intro);
                                    break;
                                case 1:
                                    List<String> notice = new ArrayList<>(guild.getNotice());
                                    if (notice.size() < config.NOTICE_MAX_LENGTH) {
                                        notice.add(target);
                                        guild.setNotice(notice);
                                        guild.update();
                                        p.sendMessage(lang.plugin + lang.notice_add.replace("%value%", target));
                                        openMain(p);
                                        return;
                                    }
                                    p.sendMessage(lang.plugin+lang.max_length_notice);
                                    break;
                                case 2:
                                    GuildAPI.setPlayerPositionByPlayer(p,target,value);
                                    return;
                                case 3:
                                    GuildAPI.removePlayerPosition(p,target);
                                    return;
                            }
                        }
                        MainMenu menuPanel = new MainMenu(p);
                        legendaryGuild.sync(()->menuPanel.open());
                        return;
                    }
                    if (user.isChat()) {
                        if (str.startsWith("/")) {
                            return;
                        }
                        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                        Position position = legendaryGuild.getPositionsManager().getPosition(user.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
                        if (guild != null) {
                            event.setCancelled(true);
                            String deal = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(user.getPlayer()),config.GUILD_CHAT.replace("%player%",user.getPlayer())
                                    .replace("%message%",str)
                                    .replace("%position%",position.getDisplay()));
                            MsgUtils.sendGuildMessage(guild.getMembers(),deal);
                        }
                    }
                }
            }
        });
    }

    private void openMain(Player p) {
        MainMenu menuPanel = new MainMenu(p);
        legendaryGuild.sync(()->menuPanel.open());
    }

    public class Input {
        private int id;
        private String value;

        public Input(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public String getValue() {
            return value;
        }
    }
}
