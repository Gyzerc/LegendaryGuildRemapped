package com.gyzer.Listeners;

import com.gyzer.Menu.MenuDraw;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class PlayerClickEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof MenuDraw) {
            MenuDraw draw = (MenuDraw) e.getInventory().getHolder();
            draw.onClick(e);
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof MenuDraw) {
            MenuDraw draw = (MenuDraw) e.getInventory().getHolder();
            draw.onClose(e);
        }

    }
    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof MenuDraw) {
            MenuDraw draw = (MenuDraw) e.getInventory().getHolder();
            draw.onDrag(e);
        }
    }
}
