package com.gyzer.API.Events;

import com.gyzer.Data.Guild.Guild;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildLevelupEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Guild guild;
    private int oldLevel;
    private int newLevel;

    public GuildLevelupEvent(Guild guild, int oldLevel, int newLevel) {
        this.guild = guild;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public Guild getGuild() {
        return guild;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
