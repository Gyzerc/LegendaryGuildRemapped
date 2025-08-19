package com.gyzer.Menu.Panels;

import com.gyzer.Data.Other.Guild_Redpacket;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Manager.Guild.GuildRedpacketManager;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.ReplaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedPacketsMenu extends MenuDraw {

    private int page;
    private boolean hasNext;
    private int currentIndex = 0;
    public RedPacketsMenu(Player p,int page) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().REDPACKETS);
        this.page = page;
        this.inv = Bukkit.createInventory(this, provider.getSize() , provider.getTitle());

        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild_Redpacket guildRedpacket = legendaryGuild.getGuildRedpacketManager().getRedPacketData(user.getGuild());

        List<UUID> uuids = guildRedpacket.getRedpackets().keySet().stream().collect(Collectors.toList());
        this.hasNext = hasPage((page + 1), uuids , "redpacket");
        List<UUID> redpackets = getPage(page , uuids , "redpacket");
        DrawEssentailSpecial(inv , menuItem -> {
            if (menuItem.getFuction().equals("redpacket")) {
                if (uuids.size() > currentIndex) {
                    UUID uuid = redpackets.get(currentIndex);
                    Guild_Redpacket.Redpacket redpacket = guildRedpacket.getRedpackets().get(uuid);
                    if (redpacket != null) {
                        if (redpacket.getLess() <= 0) {
                            guildRedpacket.getRedpackets().remove(uuid);
                            legendaryGuild.getGuildRedpacketManager().updateRedPacket(guildRedpacket);
                            return;
                        }

                        boolean claimed = redpacket.getHistory().containsKey(p.getName());
                        String state = claimed ? provider.getPlaceHolder("claim_state_yes") : provider.getPlaceHolder("claim_state_no");
                        String claim = claimed ? provider.getPlaceHolder("already_claim") : provider.getPlaceHolder("wait_claim");

                        String luck = provider.getPlaceHolder("luck_null");
                        double luck_value = 0.0;
                        GuildRedpacketManager.RedPacketHistoryData first = legendaryGuild.getGuildRedpacketManager().getFirst(redpacket.getHistory());
                        if (first != null) {
                            luck = first.getPlayer();
                            luck_value = first.getAmount();
                        }

                        double finalLuck_value = luck_value;
                        String finalLuck = luck;

                        ItemStack i = menuItem.getItem(p).clone();
                        ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                                .addSinglePlaceHolder("placeholder_state", state)
                                .addSinglePlaceHolder("placeholder_claim", claim)
                                .addSinglePlaceHolder("owner", redpacket.getPlayer())
                                .addSinglePlaceHolder("date", redpacket.getDate())
                                .addSinglePlaceHolder("less", redpacket.getLess() + "")
                                .addSinglePlaceHolder("total", redpacket.getTotal() + "")
                                .addSinglePlaceHolder("amount", redpacket.getAmount() + "")
                                .addSinglePlaceHolder("less_amount", (redpacket.getAmount() - redpacket.getHistory().size()) + "")
                                .addSinglePlaceHolder("luck", finalLuck)
                                .addSinglePlaceHolder("luck_value", finalLuck_value + "");
                        menuItem.setItem(replaceHolderUtils.startReplace(i, true, p.getName()));
                        menuItem.setTarget(uuid);
                        currentIndex++;
                        return;
                    }
                }
                menuItem.setPut(false);

            }
        });

    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (!dealEssentialsButton(e.getRawSlot())){
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            User user = legendaryGuild.getUserManager().getUser(p.getName());
            Guild_Redpacket guild_redpacket=legendaryGuild.getGuildRedpacketManager().getRedPacketData(user.getGuild());
            if (menuItem!=null){
                switch (menuItem.getFuction()){
                    case "pre" : {
                        if (page <= 1){
                            return;
                        }
                        RedPacketsMenu redPacketsPanel =new RedPacketsMenu(p,(page-1));
                        redPacketsPanel.open();
                        break;
                    }
                    case "next" : {
                        if (hasNext){
                            RedPacketsMenu redPacketsPanel = new RedPacketsMenu(p,(page+1));
                            redPacketsPanel.open();
                        }
                        break;
                    }
                    case "redpacket" : {
                        UUID uuid = (UUID) getTarget(e.getRawSlot());
                        Guild_Redpacket.Redpacket redpacket = guild_redpacket.getRedpackets().get(uuid);
                        if (redpacket == null){
                            p.sendMessage(lang.plugin+lang.redpacket_garb_no);
                            return;
                        }
                        if (legendaryGuild.getGuildRedpacketManager().grabRedPacket(user.getGuild(),uuid,p)){
                            RedPacketsMenu redPacketsPanel = new RedPacketsMenu(p,page);
                            redPacketsPanel.open();
                        }
                    }
                }
            }
        }
    }
}
