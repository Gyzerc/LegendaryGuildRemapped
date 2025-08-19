package com.gyzer.API.Events;

import com.gyzer.Data.Guild.Guild;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CreateGuildEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlers;
    }
    private Player p;
    private Guild guild;

    public CreateGuildEvent(Player p, Guild guild) {
        this.p = p;
        this.guild = guild;
    }

    public Player getPlayer() {
        return p;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
