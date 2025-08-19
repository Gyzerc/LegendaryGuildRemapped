package com.gyzer.Configurations;

import com.gyzer.Configurations.Provider.FileProvider;
import com.gyzer.Database.DataProvider;
import org.bukkit.Sound;

import java.util.*;
import java.util.logging.Level;

public class Config extends FileProvider {
    public  String GUILD_CHAT ;
    public boolean CROSS_SERVER;
    public DataProvider.DatabaseType STORE;
    public Lang LANGUAGE;
    public int max_length;

    public HashMap<Integer, Double> EXP;
    public int MAXLEVEL;
    public int DESC_MAX_LENGTH;
    public int NOTICE_MAX_LENGTH;
    public HashMap<Integer, Integer> MEMBERS;
    public boolean checkData;

    public double MONEY_TO_POINTS;

    public int HOME_WAIT;
    public Sound HOME_SOUND_SECOND;
    public Sound HOME_SOUND_TELEPORT;
    public Sound HOME_SOUND_CANCEL;
    public List<String> HOME_BLACK_WORLD;
    public List<String> HOME_BLACK_SERVER;
    public int COOLDOWN;
    public String SERVER;
    public Config() {
        super("config.yml", "", "./plugins/LegendaryGuildRemapped");
    }
    @Override
    protected void readDefault() {
        EXP = new HashMap<>();
        MEMBERS = new HashMap<>();
        checkData = getValue("settings.DataCheck",true);
        STORE = DataProvider.DatabaseType.getType(getValue("Store","SQLite"));
        LANGUAGE = Lang.getType(getValue("lang","English"));
        CROSS_SERVER = getValue("settings.Cross_Server.enable",false);
        SERVER = getValue("settings.Cross_Server.server_name","Server");
        DESC_MAX_LENGTH = getValue("settings.guild.introduce-max-line",5);
        NOTICE_MAX_LENGTH = getValue("settings.guild.notice-max-line",5);

        max_length = getValue("settings.create.max-length",6);

        MONEY_TO_POINTS = getValue("settings.guild.moneyToPoints",0.1);

        COOLDOWN = getValue("settings.create.cooldown",240);

        //公会每级经验
        MAXLEVEL = getValue("settings.guild.level.max",5);
        List<Double> doubles = getValue("settings.guild.level.require", Arrays.asList(1000.0,5000.0,10000.0,50000.0,100000.0));
        for (int a = 0 ; a<=MAXLEVEL ; a++){
            if (doubles.size() > a) {
                EXP.put(a, doubles.get(a));
            }
            else {
                EXP.put(a,9999999.9);
            }
        }
        //公会每级最大人数
        List<Integer> integers = getValue("settings.guild.level.maxmembers", Arrays.asList(5,10,15,20,25));
        for (int a = 0 ; a<=MAXLEVEL ; a++){
            if (integers.size() > a) {
                MEMBERS.put(a, integers.get(a));
            }
            else {
                MEMBERS.put(a,100);
            }
        }

        //公会驻地设置
        HOME_WAIT = getValue("settings.guild.home.teleport_wait",5);
        HOME_SOUND_SECOND = getSound(getValue("settings.guild.home.sound.second","block_note_block_banjo")).orElse(null);
        HOME_SOUND_TELEPORT = getSound(getValue("settings.guild.home.sound.teleport","ENTITY_ENDERMAN_TELEPORT")).orElse(null);
        HOME_SOUND_CANCEL = getSound(getValue("settings.guild.home.sound.cancel","entity_villager_trade")).orElse(null);

        HOME_BLACK_WORLD = getValue("settings.guild.home.black_world",new ArrayList<>());
        HOME_BLACK_SERVER = getValue("settings.guild.home.black_server",new ArrayList<>());

        GUILD_CHAT = getValue("settings.guild.chat.format","&f[&e公会聊天&f][%position%&f]&3%player%&f: %message%");
    }



    private Optional<Sound> getSound(String sound){
        if (sound == null){
            return Optional.empty();
        }
        try {
            return Optional.of(Sound.valueOf(sound.toUpperCase()));
        } catch (Exception e){
            legendaryGuild.info("音效ID出错！"+file.getName()+" ->" +sound, Level.SEVERE);
            return Optional.empty();
        }
    }
    public enum Lang {
        English,Chinese;

        public static Lang getType(String str) {
            try {
                return Lang.valueOf(str);
            } catch (IllegalArgumentException e) {
                return English;
            }
        }
    }
}
