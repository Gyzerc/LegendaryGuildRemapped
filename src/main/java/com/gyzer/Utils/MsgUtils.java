package com.gyzer.Utils;

import com.google.common.collect.Iterables;
import com.gyzer.LegendaryGuild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MsgUtils {

    public static void sendConsole(String msg) {
        Bukkit.getConsoleSender().sendMessage(new StringBuilder("§b§lLegendaryGu§3§lildRemapped§f - ") .append(msg.replace("&","§")).toString());
    }

    public static void sendMessage(String playerName,String msg){
        Player p = Bukkit.getPlayerExact(playerName);
        if (p != null){
            p.sendMessage(color(msg));
            return;
        }
        if (LegendaryGuild.getLegendaryGuild().getNetWork().isEnable()){
            if (Bukkit.getOnlinePlayers().size() > 0) {
                Player sender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
                LegendaryGuild.getLegendaryGuild().getNetWork().sendMessage(sender,playerName,color(msg));
            }
        }
    }
    public static void sendGuildMessage(LinkedList<String> members, String msg){
        for (String target : members){
            sendMessage(target,msg);
        }
    }
    public static void sendBroad(String msg){
        if (LegendaryGuild.getLegendaryGuild().getNetWork().isEnable() && Bukkit.getOnlinePlayers().size() > 0){
            Player sender = Iterables.getFirst(Bukkit.getOnlinePlayers(),null);
            LegendaryGuild.getLegendaryGuild().getNetWork().sendALLMessage(sender,color(msg));
        }
        else {
            Bukkit.broadcastMessage(color(msg));
        }
    }
    public static List<String> color(List<String> s) {
        return s.stream().map(d -> color(d)).collect(Collectors.toList());
    }
    public static String color( String textToColor) {
        if (textToColor == null){
            return null;
        }
        if(LegendaryGuild.getLegendaryGuild().version_high) {
            Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
            // Use matcher to find hex patterns in given text.
            Matcher matcher = HEX_PATTERN.matcher(textToColor);
            // Increase buffer size by 32 like it is in bungee cord api. Use buffer because it is sync.
            StringBuffer buffer = new StringBuffer(textToColor.length() + 32);

            while (matcher.find()) {
                String group = matcher.group(1);

                if (group.length() == 6) {
                    // Parses #ffffff to a color text.
                    matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                            + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                            + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                            + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5));
                } else {
                    // Parses #fff to a color text.
                    matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                            + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(0)
                            + ChatColor.COLOR_CHAR + group.charAt(1) + ChatColor.COLOR_CHAR + group.charAt(1)
                            + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(2));
                }
            }

            // transform normal codes and strip spaces after color code.
            return stripSpaceAfterColorCodes(
                    ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString()));
        }
        return textToColor.replace("&","§");
    }
    private static String stripSpaceAfterColorCodes(String textToStrip) {

        textToStrip = textToStrip.replaceAll("(" + ChatColor.COLOR_CHAR + ".)[\\s]", "$1");
        return textToStrip;
    }
}
