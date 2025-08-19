package com.gyzer.Manager.Player;

import com.gyzer.Data.Player.User;
import com.gyzer.Data.Player.WaterDataStore;
import com.gyzer.LegendaryGuild;

import java.util.HashMap;

public class UserManager {
    private HashMap<String , User> caches;

    public UserManager() {
        caches = new HashMap<>();
    }

    public User getUser(String player) {
        User user = caches.get(player);
        if (user == null) {
            user = LegendaryGuild.getLegendaryGuild().getDatabaseManager().getUser(player).orElse(
                    new User(player, LegendaryGuild.getLegendaryGuild().getLanguageManager().default_guild, LegendaryGuild.getLegendaryGuild().getLanguageManager().default_position, "", new WaterDataStore(new HashMap<>()), 0,false, false, 0, 0, User.PvpType.ALL)
            );
            caches.put(player,user);
        }
        return user;
    }

    public void updateUser(User user,boolean removeInCache){
        LegendaryGuild.getLegendaryGuild().getDatabaseManager().saveUser(user);
        if (removeInCache){
            caches.remove(user.getPlayer());
            return;
        }
        caches.put(user.getPlayer(),user);
    }

    public void reloadUserDataIfCached(String target){
        caches.remove(target);
    }
}
