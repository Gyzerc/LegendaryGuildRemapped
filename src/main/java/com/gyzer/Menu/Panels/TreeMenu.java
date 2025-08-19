package com.gyzer.Menu.Panels;

import com.gyzer.API.GuildAPI;
import com.gyzer.Configurations.Files.GuildTreeConfigManager;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.User;
import com.gyzer.Data.Player.WaterDataStore;
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

public class TreeMenu extends MenuDraw {

    public TreeMenu(Player p) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().TREE);
        this.inv = Bukkit.createInventory(this,provider.getSize(),provider.getTitle());
        DrawEssentailSpecial(inv,menuItem -> {
            if (menuItem.getFuction().equals("pot")){
                WaterDataStore waterDataStore = legendaryGuild.getUserManager().getUser(p.getName()).getWaterDataStore();
                ItemStack i = menuItem.getItem(p);
                if (legendaryGuild.getGuildTreeManager().getWaterPot(menuItem.getValue()).isPresent()){
                    String pot = menuItem.getValue();
                    ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                            .addSinglePlaceHolder("use",waterDataStore.getAmount(pot, WaterDataStore.WaterDataType.TODAY)+"");
                    menuItem.setItem(replaceHolderUtils.startReplace(i,true,p.getName()));
                    menuItem.setPut(true);
                }
            }
            if (menuItem.getFuction().equals("tree")) {
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
                int level = guild.getTreelevel();
                double next = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().TREEEXP.getOrDefault(level, Double.valueOf(-1));
                GuildTreeConfigManager.GuildTreeIcon icon = legendaryGuild.getGuildTreeManager().getGuildTreeConfigManager().getTreeIcon();

                ItemStack i = icon.getPreviewItem(level).clone();
                ItemMeta id = i.getItemMeta();
                List<String> lore = id.hasLore() ? id.getLore() : new ArrayList<>();
                id.setLore(lore);
                i.setItemMeta(id);

                ReplaceHolderUtils replaceHolderUtils = new ReplaceHolderUtils()
                        .addSinglePlaceHolder("level",""+level)
                        .addSinglePlaceHolder("exp",""+guild.getTreeexp())
                        .addSinglePlaceHolder("bar", GuildAPI.getGuildTreeExpProgressBar(guild))
                        .addSinglePlaceHolder("next",next+"");
                i = replaceHolderUtils.startReplace(i,true,p.getName());

                menuItem.setItem(i);
                menuItem.setPut(true);
            }
        });
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);

        if (!dealEssentialsButton(e.getRawSlot())) {
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null) {
                User user = legendaryGuild.getUserManager().getUser(p.getName());
                switch (menuItem.getFuction()) {
                    case "wish":
                        if (GuildAPI.GuildTreeWish(user)){
                            p.closeInventory();
                        }
                        break;
                    case "pot":
                        String potId = menuItem.getValue();
                        if (GuildAPI.GuildTreeWater(p,potId)){
                            TreeMenu tree = new TreeMenu(p);
                            tree.open();
                        }
                        break;
                    case "tree":
                        if (GuildAPI.addGuildTreeLevelByPlayer(p)) {
                            TreeMenu tree = new TreeMenu(p);
                            tree.open();
                        }
                }
            }
        }
    }
}
