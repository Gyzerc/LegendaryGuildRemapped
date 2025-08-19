package com.gyzer.Menu.Panels;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.Buff;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.ReplaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class BuffMenu extends MenuDraw {
    private int page;
    private boolean hasNext;
    private int currentIndex = 0;
    public BuffMenu(Player p, int page) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().BUFF);
        this.page = page;
        this.inv = Bukkit.createInventory(this,provider.getSize(),provider.getTitle());
        this.hasNext = hasPage((page + 1) , legendaryGuild.getBuffsManager().getBuffs(),"buff");

        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        StringStore data = guild.getBuffs();

        List<Buff> buffs = getPage(page , legendaryGuild.getBuffsManager().getBuffs(),"buff");
        DrawEssentailSpecial(inv , menuItem -> {
            if (menuItem.getFuction().equals("buff")) {
                if (buffs.size() > currentIndex) {
                    Buff buff = buffs.get(currentIndex);
                    String buffId = buff.getId();
                    ItemStack i = buff.getPreview(Integer.parseInt(data.getValue(buffId,0).toString())).clone();
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("buff_display",buff.getDisplay())
                            .addSinglePlaceHolder("buff_maxlevel",buff.getMax()+"")
                            .addSinglePlaceHolder("buff_level",String.valueOf(data.getValue(buff.getId(),0)));
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                    menuItem.setTarget(buff);
                    currentIndex++;
                    return;
                }
                menuItem.setPut(false);
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
                    case "pre": {
                        if (page <= 1) {
                            return;
                        }
                        BuffMenu buffPanel = new BuffMenu(p, (page - 1));
                        buffPanel.open();
                        break;
                    }
                    case "next": {
                        if (!hasNext) {
                            BuffMenu buffPanel = new BuffMenu(p, (page + 1));
                            buffPanel.open();
                        }
                        break;
                    }
                    case "buff": {
                        Buff buff = (Buff) getTarget(e.getRawSlot());
                        if (buff != null) {
                            User user = legendaryGuild.getUserManager().getUser(p.getName());
                            Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                            if (guild.getOwner().equals(p.getName())) {
                                if (GuildAPI.addGuildBuffLevel(guild, p, buff)) {
                                    BuffMenu panel = new BuffMenu(p, page);
                                    panel.open();
                                }
                                return;
                            }
                            p.sendMessage(lang.plugin + lang.notowner);
                        }
                    }
                }
            }
        }
    }
}
