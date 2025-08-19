package com.gyzer.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerNewCycleEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    private Player p;
    private int id;
    private int old;
    private int value;
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public PlayerNewCycleEvent(Player p,int id,int old,int value) {
        this.id = id;
        this.p = p;
        this.old = old;
        this.value = value;
    }

    public Player getPlayer() {
        return p;
    }

    public int getOld() {
        return old;
    }

    public int getValue() {
        return value;
    }

    public int getId() {
        return id;
    }
}
