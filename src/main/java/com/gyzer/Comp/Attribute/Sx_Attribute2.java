package com.gyzer.Comp.Attribute;

import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.Buff;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Sx_Attribute2 extends AttributePluginProvider{
    @Override
    public void updateBuff(Player p) {

        User user = legendaryGuild.getUserManager().getUser(p.getName());

        SXAttribute.getApi().removeEntityAPIData(LegendaryGuild.class,p.getUniqueId());
        if (user.hasGuild()) {
            Guild guild = legendaryGuild.getGuildsManager().getGuild(user.getGuild());

            //公会buff 以及 职位BUff 属性
            List<String> Attrs = new ArrayList<>();

            //公会buff
            StringStore buffs = guild.getBuffs();
            for (String id : (List<String>) buffs.Values()) {
                Buff buff = legendaryGuild.getBuffsManager().getBuff(id).orElse(null);
                if (buff != null) {
                    int level = Integer.parseInt(buffs.getValue(id, 0).toString());
                    if (level > 0) {
                        Attrs.addAll(buff.getAttr(level));
                    }
                }
            }

            //职位buff
            Position position = legendaryGuild.getPositionsManager().getPosition(user.getPosition()).orElse(legendaryGuild.getPositionsManager().getDefaultPosition());
            List<String> Position_Attrs = position.getAttrs();
            Attrs.addAll(Position_Attrs);

            SXAttributeData data = SXAttribute.getApi().getLoreData(null, null, Attrs);
            SXAttribute.getApi().setEntityAPIData(LegendaryGuild.class, p.getUniqueId(), data);
        }
    }
}
