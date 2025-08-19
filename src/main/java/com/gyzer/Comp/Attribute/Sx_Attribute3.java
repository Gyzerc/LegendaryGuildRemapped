package com.gyzer.Comp.Attribute;

import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.Buff;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.Position;
import com.gyzer.Data.Player.User;
import com.gyzer.LegendaryGuild;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sx_Attribute3 extends AttributePluginProvider{
    private Object SxApi;
    private Method setData;
    private Method loadAttrs;
    private Method removeData;

    public Sx_Attribute3(){
        try {
            Class<?> c = Class.forName("github.saukiya.sxattribute.SXAttribute");
            Method get = c.getMethod("getApi");
            SxApi = get.invoke(c);
            setData = SxApi.getClass().getMethod("setEntityAPIData", Class.class, UUID.class, SXAttributeData.class);
            loadAttrs = SxApi.getClass().getMethod("loadListData", List.class);
            removeData = SxApi.getClass().getMethod("removeEntityAPIData", Class.class, UUID.class);
            System.out.println("初始化成功！");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void updateBuff(Player p) {
        User user = legendaryGuild.getUserManager().getUser(p.getName());

        removeAttribute(p.getUniqueId());
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


            //更新属性
            setAttribute(p.getUniqueId(),Attrs);
        }
    }

    private void removeAttribute(UUID uuid){
        try {
            removeData.invoke(SxApi, LegendaryGuild.class,uuid);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAttribute(UUID uuid,List<String> attrs){
        try {
            SXAttributeData data = (SXAttributeData) loadAttrs.invoke(SxApi,attrs);
            setData.invoke(SxApi,LegendaryGuild.class,uuid,data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
