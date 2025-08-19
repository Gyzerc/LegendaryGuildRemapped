package com.gyzer.Requirement.Sub;

import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import com.gyzer.Requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class GuildPositionRequirement extends Requirement {
    @Override
    public String getSymbol() {
        return "guild_position";
    }

    @Override
    public boolean canPass(Player p, String str) {
        User user = legendaryGuild.getUserManager().getUser(p.getName());
        if (!user.hasGuild()){
            p.sendMessage(lang.plugin+lang.nothasguild);
            return false;
        }

        String[] args = str.split(";");
        String positions = args[1];
        List<String> positionList=new ArrayList<>();
        if (positions.contains(",")){
            for (String id : positions.split(",")){
                positionList.add(id);
            }
        } else {
            positionList.add(positions);
        }

        if (positionList.contains(user.getPosition())){
            return true;
        }
        p.sendMessage(lang.plugin+lang.nopass_position);
        return false;
    }


    private Position getPosition(String id){
       Position position = legendaryGuild.getPositionsManager().getPosition(id).orElse(null);
       if (position != null){
           return position;
       }
       legendaryGuild.info("职位id不存在！->"+id,Level.SEVERE);
       return null;

    }
    @Override
    public void deal(Player p, String str) {

    }
}
