package com.gyzer.Data.Other;

import com.gyzer.API.GuildAPI;
import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Player.User;
import com.gyzer.Utils.RunUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WaterPot {
    private String id;
    private String display;
    private double addExp;
    private double addPoints;
    private int limit_day;
    private List<String> requirements;
    private List<String> runs;

    public WaterPot(String id,String display, double addExp,double addPoints, int limit_day, List<String> requirements,List<String> runs) {
        this.id = id;
        this.display = display;
        this.addExp = addExp;
        this.addPoints = addPoints;
        this.limit_day = limit_day;
        this.requirements = requirements;
        this.runs = runs;
    }

    public List<String> getRuns() {
        return runs;
    }

    public double getAddPoints() {
        return addPoints;
    }

    public String getDisplay() {
        return display;
    }

    public String getId() {
        return id;
    }

    public double getAddExp() {
        return addExp;
    }

    public int getLimit_day() {
        return limit_day;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void use(Player p, User user, Guild guild){
        user.addPoints(addPoints,true);
        user.update(false);
        GuildAPI.addGuildTreeExp(user.getPlayer(),guild,addExp);
        new RunUtils(runs,p).start();
    }
}
