package com.gyzer.Requirement;

import com.gyzer.Configurations.Language;
import com.gyzer.LegendaryGuild;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Requirement {
    public final LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    public final Language lang = legendaryGuild.getLanguageManager();

    public abstract String getSymbol();

    public abstract boolean canPass(Player p, String str);

    public abstract void deal(Player p, String str);

    public <T> T getFromArray(String[] str, int pos, T def) {
        if (str != null && str.length > pos) {
            return (T) str[pos];
        }
        return def;
    }
    public boolean checkIsNumber(String arg) {
        Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
        Matcher isNum = pattern.matcher(arg);
        return isNum.matches();
    }
}