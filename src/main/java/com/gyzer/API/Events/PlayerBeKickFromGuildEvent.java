package com.gyzer.API.Events;

import com.gyzer.Data.Guild.Guild;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBeKickFromGuildEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    private String user;
    private Guild guild;
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public PlayerBeKickFromGuildEvent(String user, Guild guild) {
        this.user = user;
        this.guild = guild;
    }

    public String getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }
}
