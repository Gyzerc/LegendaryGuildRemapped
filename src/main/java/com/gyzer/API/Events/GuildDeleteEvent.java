package com.gyzer.API.Events;

import com.gyzer.Data.Guild.Guild;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildDeleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    private Guild guild;
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public GuildDeleteEvent(Guild guild) {
        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }
}
