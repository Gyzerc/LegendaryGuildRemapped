package com.gyzer.Menu.Panels;

import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildIcon;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.ReplaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class IconsShopMenu extends MenuDraw {
    private int page;
    private int currentIndex = 0;
    private boolean hasNextPage;
    public IconsShopMenu(Player p, int page) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().ICONSHOP);
        this.page = page;
        this.inv = Bukkit.createInventory(this, provider.getSize(), provider.getTitle());
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());

        this.hasNextPage = hasPage((page + 1),legendaryGuild.getGuildIconsManager().getIcons(),"icon");
        List<GuildIcon> icons = getPage(page , legendaryGuild.getGuildIconsManager().getIcons(),"icon");

        String locked = provider.getPlaceHolder("locked");
        String unlocked = provider.getPlaceHolder("unlocked");
        String putting = provider.getPlaceHolder("putting");
        DrawEssentailSpecial(inv , menuItem -> {
            if (menuItem.getFuction().equals("icon")) {
                if (icons.size() > currentIndex) {
                    GuildIcon icon = icons.get(currentIndex);

                    String current = locked;
                    if (guild.getIcon().equals(icon.getId())){
                        current=putting;
                    }
                    else if (guild.getUnlock_icons().contains(icon.getId())){
                        current=unlocked;
                    }

                    ItemStack i = menuItem.getItem(p).clone();
                    i.setType(icon.getMaterial());
                    i.setDurability((short) icon.getData());
                    ItemMeta id = i.getItemMeta();
                    if (legendaryGuild.isVersion_high()) {
                        if (id.hasCustomModelData()) {
                            id.setCustomModelData(icon.getModel());
                        }
                    }
                    i.setItemMeta(id);

                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addListPlaceHolder("description",icon.getDescription())
                            .addSinglePlaceHolder("icon",icon.getDisplay())
                            .addSinglePlaceHolder("placeholder",current);

                    menuItem.setTarget(icon);
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
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
        if (!dealEssentialsButton(e.getRawSlot())) {
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null) {
                switch (menuItem.getFuction()) {
                    case "pre": {
                        if (page <= 1) {
                            return;
                        }
                        IconsShopMenu guildIconsShopPanel = new IconsShopMenu(p, (page - 1));
                        guildIconsShopPanel.open();
                        break;
                    }
                    case "next": {
                        if (hasNextPage) {
                            IconsShopMenu guildIconsShopPanel = new IconsShopMenu(p, (page + 1));
                            guildIconsShopPanel.open();
                        }
                        break;
                    }
                    case "icon" : {
                        GuildIcon icon = (GuildIcon) getTarget(e.getRawSlot());
                        User user = legendaryGuild.getUserManager().getUser(p.getName());
                        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                        if (user.getPosition().equals(legendaryGuild.getPositionsManager().getOwnerPosition().getId())) {
                            if (e.isLeftClick()) {
                                if (!guild.getIcon().equals(icon.getId())) {
                                    if (guild.getUnlock_icons().contains(icon.getId())) {
                                        guild.setIcon(icon.getId());
                                        guild.update();

                                        p.sendMessage(lang.plugin + lang.icon_put.replace("%value%", icon.getDisplay()));

                                        IconsShopMenu guildIconsShopPanel = new IconsShopMenu(p, page);
                                        guildIconsShopPanel.open();
                                        return;
                                    }
                                    p.sendMessage(lang.plugin + lang.icon_locked);
                                    return;
                                }
                                return;
                            }
                            if (e.isRightClick()) {
                                if (guild.getUnlock_icons().contains(icon.getId())) {
                                    return;
                                }
                                if (legendaryGuild.getRequirementsManager().check(p, icon.getRequirements())) {
                                    legendaryGuild.getRequirementsManager().deal(p, icon.getRequirements());

                                    List<String> icons = new ArrayList<>(guild.getUnlock_icons());
                                    icons.add(icon.getId());

                                    guild.setUnlock_icons(icons);
                                    guild.update();

                                    p.sendMessage(lang.plugin + lang.icon_unlock.replace("%value%", icon.getDisplay()));
                                    IconsShopMenu guildIconsShopPanel = new IconsShopMenu(p, page);
                                    guildIconsShopPanel.open();
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        p.sendMessage(lang.plugin + lang.nopass_position);
                        return;
                    }
                }
            }
        }
    }
}
