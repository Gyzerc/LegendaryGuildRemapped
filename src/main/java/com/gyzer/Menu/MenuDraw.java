package com.gyzer.Menu;

import com.gyzer.Configurations.Config;
import com.gyzer.Configurations.Language;
import com.gyzer.LegendaryGuild;
import com.gyzer.Menu.Panels.MainMenu;
import com.gyzer.Utils.RunUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class MenuDraw implements InventoryHolder {
    protected LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    protected Config config = legendaryGuild.getConfigManager();
    protected Language lang = legendaryGuild.getLanguageManager();

    protected Player p;
    protected MenuProvider provider;
    protected Inventory inv;
    private HashMap<Integer,String> value;

    public MenuDraw(Player p, MenuProvider provider) {
        this.p = p;
        this.provider = provider;
        this.value = new HashMap<>();
        this.target = new HashMap<>();
    }
    public <T> boolean hasPage(int page, List<T> list , String functionName){
        int layout = provider.getFunctionTypeAmount(functionName);

        int start = 0 + (page-1) * layout;
        int end = layout + (page-1)*layout;
        return list.size() > start ;
    }


    public <T> List<T> getPage(int page, List<T> list , String functionName){
        int layout = provider.getFunctionTypeAmount(functionName);

        int start = 0 + (page-1) * layout;
        int end = layout + (page-1)*layout;
        List<T> rL = new ArrayList<>();
        for (int get = start;get < end ; get ++){
            if (list.size() > get) {
                rL.add(list.get(get));
            }
        }
        return rL;
    }


    public <T> List<T> getPage(int page,int perpage, List<T> list){
        int start = 0 + (page-1) * perpage;
        int end = perpage + (page-1)*perpage;
        List<T> rL = new ArrayList<>();
        for (int get = start;get < end ; get ++){
            if (list.size() > get) {
                rL.add(list.get(get));
            }
        }
        return rL;
    }

    public HashMap<Integer,Object> target;

    public void DrawEssentailSpecial(Inventory inv, Consumer<MenuProvider.MenuItem> consumer){
        legendaryGuild.sync(new Runnable() {
            @Override
            public void run() {
                for (int slot = 0 ; slot <= 53 ; slot++) {
                    if (provider.getItem().containsKey(slot)) {
                        MenuProvider.MenuItem menuItem = provider.getItem().get(slot);
                        MenuProvider.MenuItem newMenu = new MenuProvider.MenuItem(menuItem.getId(), menuItem.getItem(p).clone(), menuItem.getFuction(), menuItem.getValue(), menuItem.isUseHead());
                        consumer.accept(newMenu);
                        if (newMenu.isPut()) {
                            inv.setItem(slot, newMenu.getItem(p));
                            target.put(slot , newMenu.getTarget());
                        }
                        value.put(slot, newMenu.getValue());
                    }
                }
            }
        });
    }
    public boolean dealEssentialsButton(int slot){
        MenuProvider.MenuItem fuction = provider.getMenuItem(slot);
        if (fuction != null){
            switch (fuction.getFuction()){
                case "close": {
                    p.closeInventory();
                    return true;
                }
                case "cmd": {
                    new RunUtils(Arrays.asList(fuction.getValue()),p).start();
                    return true;
                }
                case "back": {
                    MainMenu guildMenuPanel = new MainMenu(p);
                    guildMenuPanel.open();
                    return true;
                }
                case "none":
                    return true;
            }
        }
        return false;
    }

    public String getValue(int slot) {
        return value.get(slot);
    }

    public void open() {
        provider.getSound().ifPresent(sound -> p.playSound(p.getLocation(),sound,1,1));
        p.openInventory(inv);
    }

    public Object getTarget(int slot) {
        return target.get(slot);
    }
    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void onClick(InventoryClickEvent e) {
    }

    public void onDrag(InventoryDragEvent e) {
    }

    public void onClose(InventoryCloseEvent e) {
    }
}
