package com.gyzer.Menu.Panels;

import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.Position;
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
import java.util.stream.Collectors;

public class PositionsMenu extends MenuDraw {
    public PositionsMenu(Player p ) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().POSITIONS);
        this.inv = Bukkit.createInventory(this,provider.getSize(),provider.getTitle());
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        DrawEssentailSpecial(inv , menuItem -> {
            if (menuItem.getFuction().equals("position")) {
                String positionId = menuItem.getValue();
                Position position = legendaryGuild.getPositionsManager().getPosition(positionId).orElse(null);
                if (position != null) {
                    List<String> list = guild.getMembers().stream().filter(m -> {
                        User memberUser = legendaryGuild.getUserManager().getUser(m);
                        return memberUser.getPosition().equals(positionId);
                    }).collect(Collectors.toList());


                    ItemStack i = menuItem.getItem(p).clone();

                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("display",position.getDisplay())
                            .addSinglePlaceHolder("amount",list.size()+"")
                            .addListPlaceHolder("members",list);


                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
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
                    case "position": {
                        User user = legendaryGuild.getUserManager().getUser(p.getName());
                        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                        if (guild != null && guild.getOwner().equals(p.getName())) {
                            String positionId = menuItem.getValue();
                            Position position = legendaryGuild.getPositionsManager().getPosition(positionId).orElse(null);
                            if (position != null) {
                                if (e.isLeftClick()) {
                                    p.closeInventory();
                                    p.sendMessage(lang.plugin + lang.positions_add_write.replace("%position%",position.getDisplay()));
                                    legendaryGuild.getChatControl().setModify(p.getUniqueId(),2,positionId);
                                    return;
                                }
                                if (e.isRightClick()){
                                    p.closeInventory();
                                    p.sendMessage(lang.positions_remove_write.replace("%position%",position.getDisplay()));
                                    legendaryGuild.getChatControl().setModify(p.getUniqueId(),3);
                                    return;
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
