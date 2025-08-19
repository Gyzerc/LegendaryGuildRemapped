package com.gyzer.Comp.Attribute;

import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.Buff;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import org.bukkit.entity.Player;
import org.serverct.ersha.jd.AttributeAPI;

import java.util.ArrayList;
import java.util.List;

public class AttributePlus2 extends AttributePluginProvider{
    @Override
    public void updateBuff(Player p) {
        User user = legendaryGuild.getUserManager().getUser(p.getName());

        AttributeAPI.deleteAttribute(p,"LegendaryGuild_GuildBuff");
        AttributeAPI.deleteAttribute(p,"LegendaryGuild_GuildPosition");

        if (user.hasGuild()){
            Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());

            //公会buff属性
            List<String> GuildBuff_Attrs = new ArrayList<>();
            StringStore buffs = guild.getBuffs();
            for (String id : (List<String>)buffs.Values()){
                Buff buff = legendaryGuild.getBuffsManager().getBuff(id).orElse(null);
                if (buff != null){
                    int level  = Integer.parseInt(buffs.getValue(id,0).toString()) ;
                    if (level > 0 ){
                        GuildBuff_Attrs.addAll(buff.getAttr(level));
                    }
                }
            }
            AttributeAPI.addAttribute(p,"LegendaryGuild_GuildBuff",GuildBuff_Attrs,false);

            //职位加成
            Position position = legendaryGuild.getPositionsManager().getPosition(user.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
            List<String> Position_Attrs = position.getAttrs();
            AttributeAPI.addAttribute(p,"LegendaryGuild_GuildPosition",Position_Attrs,false);

            return;
        }
    }
}
