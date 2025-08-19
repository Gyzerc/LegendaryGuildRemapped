package com.gyzer.Configurations.Files;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Data.Player.Position;
import com.gyzer.LegendaryGuild;
import com.gyzer.Manager.Player.PositionsManager;
import com.gyzer.Utils.MsgUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PositionConfig extends FileProvider {

     private PositionsManager positionsManager;
    public PositionConfig(PositionsManager positionsManager) {
        super("config.yml", "Position/", "./plugins/LegendaryGuildRemapped/Position");
        this.positionsManager = positionsManager;
        read();
    }

    private void read(){
        String owner = getValue("owner","会长");
        String member = getValue("default","普通成员");

        positionsManager.setOwnerPosition(new Position(owner, MsgUtils.color(getValue("positions."+owner+".display","&6会长")),getValue("positions."+owner+".weight",0),1,getValue("positions."+owner+".attrs",new ArrayList<>()),true,true));
        positionsManager.setDefaultPosition(new Position(member,MsgUtils.color(getValue("positions."+member+".display","&f普通成员")),getValue("positions."+member+".weight",99),-1,getValue("positions."+member+".attrs",new ArrayList<>()),false,false));


        getSection("positions").ifPresent(configurationSection -> {
            for (String id : configurationSection.getKeys(false)){
                if (id.equals(owner) || id.equals(member)){
                    continue;
                }
                //展示名检测
                String display = configurationSection.getString(id+".display");
                if (display == null){
                    legendaryGuild.info("公会职位ID: "+id+" 缺少display,该职位不生效...", Level.SEVERE);
                    continue;
                }
                display = MsgUtils.color(display);

                //权重检测
                if (configurationSection.get(id+".weight") == null){
                    legendaryGuild.info("公会职位ID: "+id+" 缺少weight,该职位不生效...", Level.SEVERE);
                    continue;
                }
                int weight = configurationSection.getInt(id+".weight");

                //最大人数检测
                int max = -1;
                if (configurationSection.get(id+".max") == null){
                    legendaryGuild.info("公会职位ID: "+id+" 缺少max,默认该职位最大人数为无限.", Level.SEVERE);
                }
                else {
                    max = configurationSection.getInt(id+".max");
                }

                boolean accept = configurationSection.getBoolean(id+".accept",false);
                boolean kick = configurationSection.getBoolean(id+".kick",false);

                List<String> attrs = configurationSection.getStringList(id+".attrs");
                positionsManager.addPostion(id,new Position(id,display,weight,max,attrs,accept,kick));
            }
        });
    }

    @Override
    protected void readDefault() {



    }
}
