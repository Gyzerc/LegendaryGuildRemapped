package com.gyzer.Menu.Panels;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.MenuDraw;
import com.gyzer.Menu.MenuProvider;
import com.gyzer.Utils.MsgUtils;
import com.gyzer.Utils.ReplaceHolderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ApplicationsMenu extends MenuDraw {
    private int page;
    private int currentIndex = 0;
    private boolean hasNextPage;
    public ApplicationsMenu(Player p, int page) {
        super(p, LegendaryGuild.getLegendaryGuild().getMenusManager().APPLICATIONS);
        this.page = page;
        this.inv = Bukkit.createInventory(this, provider.getSize(), provider.getTitle());
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());
        List<Guild.Application> applications = getPage(page, guild.getApplications() , "application");
        hasNextPage = hasPage((page + 1),guild.getApplications() ,"application");

        DrawEssentailSpecial(inv, menuItem -> {
            if (menuItem.getFuction().equals("application")) {
                if (applications.size() > currentIndex) {
                    Guild.Application application = applications.get(currentIndex);

                    ItemStack i = menuItem.getItem(p).clone();
                    menuItem.setItem(new ReplaceHolderUtils()
                            .addSinglePlaceHolder("player", application.getPlayer())
                            .addSinglePlaceHolder("apply_date", application.getDate())
                            .startReplace(i, true, application.getPlayer()));

                    menuItem.setTarget(application);
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
        if (!dealEssentialsButton(e.getRawSlot())){
            MenuProvider.MenuItem menuItem = provider.getMenuItem(e.getRawSlot());
            if (menuItem != null){
                switch (menuItem.getFuction()){
                    case "pre": {
                        if (page <= 1){
                            return;
                        }
                        ApplicationsMenu applicationsPanel = new ApplicationsMenu(p,(page-1));
                        applicationsPanel.open();
                        break;
                    }
                    case "next": {
                        if (hasNextPage){
                            ApplicationsMenu applicationsPanel = new ApplicationsMenu(p,(page+1));
                            applicationsPanel.open();
                            return;
                        }
                        break;
                    }
                    case "application" : {
                        Guild.Application application = (Guild.Application) getTarget(e.getRawSlot());
                        if (application != null) {
                            User clickUser = legendaryGuild.getUserManager().getUser(p.getName());
                            Guild guild = legendaryGuild.getGuildsManager().getGuild(clickUser.getGuild());
                            Position position = legendaryGuild.getPositionsManager().getPosition(clickUser.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
                            if (position.isAccept()){

                                if (e.isRightClick()){

                                    guild.removeApplication(application.getPlayer());
                                    guild.update();

                                    p.sendMessage(lang.plugin+lang.application_deny.replace("%value%",application.getPlayer()));
                                    MsgUtils.sendMessage(application.getPlayer(),lang.plugin+lang.application_deny_target.replace("%value%",guild.getDisplay()));

                                    ApplicationsMenu applicationsPanel = new ApplicationsMenu(p,page);
                                    applicationsPanel.open();
                                    return;

                                }

                                if (guild.getMembers().size() >= guild.getMaxMembers()){
                                    p.sendMessage(lang.plugin+lang.member_max);
                                    return;
                                }

                                User targetUser = legendaryGuild.getUserManager().getUser(application.getPlayer());

                                guild.removeApplication(application.getPlayer());
                                guild.update();

                                if (!targetUser.hasGuild()){
                                    GuildAPI.JoinGuild(targetUser,guild);
                                }


                                ApplicationsMenu applicationsPanel = new ApplicationsMenu(p,page);
                                applicationsPanel.open();
                                return;
                            }
                            p.sendMessage(lang.plugin+lang.nopass_position);
                            return;
                        }
                    }
                }
            }
        }
    }
}
