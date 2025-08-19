package com.gyzer.Requirement.Sub;

import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Player.User;
import com.gyzer.Requirement.Requirement;
import org.bukkit.entity.Player;

public class ActivityRequirement extends Requirement {
    @Override
    public String getSymbol() {
        return "activity";
    }

    @Override
    public boolean canPass(Player p, String str) {
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        String[] args=str.split(";");
        double price = Double.parseDouble(args[1]);
        if (user.hasGuild()) {
            GuildActivityData guildActivityData = legendaryGuild.getGuildActivityManager().getData(user.getGuild());
            return guildActivityData.getPlayerActivity(p.getName()) >= price;
        }
        p.sendMessage(lang.plugin + lang.requirement_notenough_activity.replace("%value%",args[1]));
        return false;
    }

    @Override
    public void deal(Player p, String str) {
    }
}
