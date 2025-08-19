package com.gyzer.API.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NewCycleEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    private int id;
    private int old;
    private int value;
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public NewCycleEvent(int id,int old,int value) {
        this.id = id;
        this.old = old;
        this.value = value;
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