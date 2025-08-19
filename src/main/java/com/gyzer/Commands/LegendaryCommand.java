package com.gyzer.Commands;

import com.gyzer.Configurations.Language;
import com.gyzer.LegendaryGuild;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class LegendaryCommand {

    public final LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    public final Language lang = legendaryGuild.getLanguageManager();
    private String permission;
    private String command;
    private List<Integer> length;
    private boolean admin;

    public boolean checkIsNumber(String arg) {
        Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
        Matcher isNum = pattern.matcher(arg);
        return isNum.matches();
    }
    public LegendaryCommand(String permission, String command, int length, boolean admin) {
        this.permission = permission;
        this.command = command;
        this.length = Collections.singletonList(length);
        this.admin = admin;
    }
    public LegendaryCommand(String permission, String command, List<Integer> length, boolean admin) {
        this.permission = permission;
        this.command = command;
        this.length = length;
        this.admin = admin;
    }
    public abstract void handle(CommandSender sender,String[] args);
    public abstract List<String> complete(CommandSender sender,String[] args);

    public String getPermission() {
        return permission;
    }

    public String getCommand() {
        return command;
    }

    public List<Integer> getLength() {
        return length;
    }

    public boolean isAdmin() {
        return admin;
    }

    public OfflinePlayer getPlayer(String name) {
        OfflinePlayer p = Bukkit.getPlayerExact(name);
        if (p == null) {
            // Not the best option, but Spigot doesn't offer a good replacement (as usual)
            p = Bukkit.getOfflinePlayer(name);
            return p.hasPlayedBefore() ? p : null;
        }
        return p;
    }

    public List<String> getOnlines() {
        return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
    }

}
