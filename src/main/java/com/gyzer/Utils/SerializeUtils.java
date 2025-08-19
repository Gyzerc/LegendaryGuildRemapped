package com.gyzer.Utils;

import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.LegendaryGuild;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.logging.Level;

public class SerializeUtils {
    public static <T> HashMap<String,T> getMap(String str) {
        HashMap<String,T> map = new HashMap<>();
        if (str != null && !str.isEmpty()) {
            for (String args : str.split(";")) {
                String[] values = args.split("=");
                map.put(values[0], (T) values[1]);
            }
        }
        return map;
    }
    public static StringStore StringToActivityData(String str){
        StringStore stringStore = new StringStore();
        if (str != null && !str.isEmpty()){
            for (String sub : str.split("⁝")){
                String[] data = sub.split(":");
                stringStore.setValue(data[0],StrToList(data[1]),null);
            }
        }
        return stringStore;
    }
    public static String ApplicationsToStr(LinkedList<Guild.Application> application){
        StringBuilder builder=new StringBuilder();
        application.forEach(application1 -> {
            builder.append(application1.getPlayer()).append(",").append(application1.getDate()).append(";");
        });
        return builder.toString();
    }
    public static String LinkListToStr(LinkedList<String> list){
        StringBuilder builder=new StringBuilder();
        for (String str:list){
            builder.append(str).append(",");
        }
        return builder.toString();
    }
    public static String LocationToStr(Guild.GuildHomeLocation loc){
        if (loc == null){
            return null;
        }
        StringBuilder builder=new StringBuilder();
        builder.append(loc.getWorld()).append(";").append(loc.getX()).append(";").append(loc.getY()).append(";").append(loc.getZ());
        return builder.toString();
    }
    public static <T> String getMapString(HashMap<String, T> map) {
        StringBuilder builder = new StringBuilder();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String,T> entry : map.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
        }
        return builder.toString();
    }


    public static HashMap<String,Integer> StrToMap_string_int(String str){
        HashMap<String,Integer> map = new HashMap<>();
        String[] args = str.split(";");
        for (String arg:args){
            if (arg.isEmpty()){
                continue;
            }
            try {
                String[] sub=arg.split(",");
                String id = sub[0];
                int amount = Integer.parseInt(sub[1]);
                map.put(id,amount);
            }
            catch (ClassCastException e){
                LegendaryGuild.getLegendaryGuild().info("转化数据类型出错！ -> "+arg, Level.SEVERE,e);
            }
            catch (NullPointerException e){
                LegendaryGuild.getLegendaryGuild().info("获取内容出错！ -> "+arg,Level.SEVERE,e);
            }
        }
        return map;
    }


    public static HashMap<String,Integer> toShopData(String str){
        HashMap<String,Integer> map = new HashMap<>();
        String[] args = str.split(";");
        for (String arg:args){
            if (arg.isEmpty()){
                continue;
            }
            try {
                String[] sub=arg.split("=");
                String id = sub[0];
                int amount = Integer.parseInt(sub[1]);
                map.put(id,amount);
            }
            catch (ClassCastException e){
                LegendaryGuild.getLegendaryGuild().info("转化数据类型出错！ -> "+arg, Level.SEVERE,e);
            }
            catch (NullPointerException e){
                LegendaryGuild.getLegendaryGuild().info("获取内容出错！ -> "+arg,Level.SEVERE,e);
            }
        }
        return map;
    }

    public static List<String> StrToList(String str){
        if (str == null || str.isEmpty()){
            return new ArrayList<>();
        }
        String[] args = str.split(",");
        return Arrays.asList(args);
    }
    public static LinkedList<String> StrToLinkList(String str){
        if (str == null || str.isEmpty()){
            return new LinkedList<>();
        }
        String[] args = str.split(",");
        LinkedList list = new LinkedList();
        for (String arg:args){
            list.add(arg);
        }
        return list;
    }
    public static LinkedList<Guild.Application> StrToApplications(String str){
        str = str.replace(" ","");
        if (str == null || str.isEmpty()){
            return new LinkedList<>();
        }
        LinkedList<Guild.Application> list = new LinkedList<>();
        for (String sub : str.split(";")){
            String[] appStr=sub.split(",");
            list.add(new Guild.Application(appStr[0],appStr[1]));
        }
        return list;

    }
    public static Guild.GuildHomeLocation StrToLocation(String server,String str){
        if (str == null || str.isEmpty()){
            return null;
        }
        String[] args = str.split(";");
        World world= Bukkit.getWorld(args[0]);
        if (world == null){
            return null;
        }
        return new Guild.GuildHomeLocation(args[0],server,Double.parseDouble(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3]));
    }

    public static String ListToStr(List<String> list){
        StringBuilder builder=new StringBuilder();
        for (String str:list){
            builder.append(str).append(",");
        }
        return builder.toString();
    }
    public static StringStore StringToStringStore(String str){
        StringStore stringStore = new StringStore();
        if (str != null && !str.isEmpty()){
            for (String sub : str.split("⁝")){
                String[] data = sub.split(":");
                stringStore.setValue(data[0],data[1],null);
            }
        }
        return stringStore;
    }
}
