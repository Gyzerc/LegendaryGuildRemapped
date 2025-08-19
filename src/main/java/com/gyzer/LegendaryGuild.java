package com.gyzer;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.gyzer.API.Events.NewCycleEvent;
import com.gyzer.Commands.Commands;
import com.gyzer.Comp.Sub.ProtocolLibHook;
import com.gyzer.Configurations.Config;
import com.gyzer.Configurations.Language;
import com.gyzer.Database.Database;
import com.gyzer.Database.MySQL.MySQLDatabase;
import com.gyzer.Database.SQLite.SQLiteDatabase;
import com.gyzer.Manager.Guild.*;
import com.gyzer.Manager.Other.*;
import com.gyzer.Manager.Player.PositionsManager;
import com.gyzer.Manager.Player.UserManager;
import com.gyzer.Utils.BungeeCord.NetWork;
import com.gyzer.Utils.BungeeCord.NetWorkHandle;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

public class LegendaryGuild extends JavaPlugin {
    private static LegendaryGuild legendaryGuild;
    public String SERVER = "Server";
    public boolean version_high;
    private TaskScheduler scheduler;
    private Config configManager;
    private Language languageManager;
    private Database databaseManager;
    private NetWork netWork;
    private UserManager userManager;
    private GuildsManager guildsManager;
    private CreateGroupsManager createGroupsManager;
    private RequirementsManager requirementsManager;
    private PositionsManager positionsManager;
    private GuildIconsManager guildIconsManager;
    private GuildActivityManager guildActivityManager;
    private GuildTreeManager guildTreeManager;
    private GuildShopManager guildShopManager;
    private TributesManager tributesManager;
    private TeamShopManager teamShopManager;
    private GuildRedpacketManager guildRedpacketManager;
    private CompManager compManager;
    private BuffsManager buffsManager;
    private MenusManager menusManager;
    @Override
    public void onEnable() {
        legendaryGuild = this;
        //获取是否高版本
        version_high = BukkitVersionHigh();
        scheduler = UniversalScheduler.getScheduler(this);

        //载入配置文件
        loadConfigs();
        //加载数据库
        initDatabase();

        //跨服模式
        netWork = new NetWorkHandle();

        //管控
        userManager = new UserManager();
        guildsManager = new GuildsManager();
        guildActivityManager = new GuildActivityManager();
        guildShopManager = new GuildShopManager();

        new ListenersManager();
        new MenusManager();
        compManager = new CompManager();


        Commands commands = new Commands();
        Bukkit.getPluginCommand("LegendaryGuildRemapped").setExecutor(commands);
        Bukkit.getPluginCommand("LegendaryGuildRemapped").setTabCompleter(commands);

        //检测周期
        sync(new Runnable() {
            @Override
            public void run() {
                checkDate();
            }
        },20,200);
    }


    @Override
    public void onDisable() {
        databaseManager.close();
        netWork.disable();
    }

    private void loadConfigs() {
        configManager = new Config();
        languageManager = new Language(configManager.LANGUAGE.name());
        requirementsManager = new RequirementsManager();
        createGroupsManager = new CreateGroupsManager();
        positionsManager = new PositionsManager();
        guildIconsManager = new GuildIconsManager();
        guildTreeManager = new GuildTreeManager();
        tributesManager = new TributesManager();
        teamShopManager = new TeamShopManager();
        guildRedpacketManager = new GuildRedpacketManager();
        buffsManager = new BuffsManager();
        menusManager = new MenusManager();
    }

    public void reload() {
        configManager = new Config();
        languageManager = new Language(configManager.LANGUAGE.name());
        requirementsManager = new RequirementsManager();
        createGroupsManager = new CreateGroupsManager();
        positionsManager = new PositionsManager();
        guildIconsManager = new GuildIconsManager();

        guildTreeManager.loadWaterPots();
        guildActivityManager.loadRewards();
        guildShopManager.getGuildShopConfigManager().loadItems();
        tributesManager.loadTributes();
        teamShopManager.loadTeamShopItems();
        buffsManager.getBuffsConfigManager().readBuffs();

        menusManager.reloadMenuConfigs();
    }


    public void reloadData(){

        userManager = new UserManager();
        guildsManager = new GuildsManager();
        guildRedpacketManager.reloadCache();
        guildShopManager.reloadCache();
        guildActivityManager.reloadCache();
        teamShopManager.reloadCache();

    }

    private void initDatabase() {
        switch (configManager.STORE) {
            case MYSQL:
                databaseManager = new Database(new MySQLDatabase());
                return;
            case SQLite:
                databaseManager = new Database(new SQLiteDatabase());
                return;
        }
    }

    private void checkDate(){
        if (configManager.checkData || (!netWork.isEnable())) {
            int day = Integer.parseInt(databaseManager.getSystemData("last_date").orElse("0"));
            int week = Integer.parseInt(databaseManager.getSystemData("last_week").orElse("0"));
            int month = Integer.parseInt(databaseManager.getSystemData("last_month").orElse("0"));

            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DATE);
            int thisWeek = calendar.get(Calendar.WEEK_OF_MONTH);
            int thisMonth = calendar.get(Calendar.MONTH);

            if (day != today) {
                databaseManager.saveSystemData("last_date", today + "");
                scheduler.runTask(this, () -> Bukkit.getPluginManager().callEvent(new NewCycleEvent(0,day, today)));
            }
            if (week != thisWeek) {
                databaseManager.saveSystemData("last_week", thisWeek + "");
                scheduler.runTask(this, () -> Bukkit.getPluginManager().callEvent(new NewCycleEvent(1, week,thisWeek)));
            }
            if (month != thisMonth) {
                databaseManager.saveSystemData("last_month", thisMonth + "");
                scheduler.runTask(this, () -> Bukkit.getPluginManager().callEvent(new NewCycleEvent(2, month,thisMonth)));
            }
        }
    }

    public ProtocolLibHook getChatControl() {
        return compManager.getProtocolLibHook();
    }
    public MenusManager getMenusManager() {
        return menusManager;
    }

    public BuffsManager getBuffsManager() {
        return buffsManager;
    }

    public CompManager getCompManager() {
        return compManager;
    }

    public GuildRedpacketManager getGuildRedpacketManager() {
        return guildRedpacketManager;
    }

    public TributesManager getTributesManager() {
        return tributesManager;
    }

    public TeamShopManager getTeamShopManager() {
        return teamShopManager;
    }

    public GuildActivityManager getGuildActivityManager() {
        return guildActivityManager;
    }

    public GuildShopManager getGuildShopManager() {
        return guildShopManager;
    }

    public GuildTreeManager getGuildTreeManager() {
        return guildTreeManager;
    }


    public GuildIconsManager getGuildIconsManager() {
        return guildIconsManager;
    }

    public PositionsManager getPositionsManager() {
        return positionsManager;
    }

    public RequirementsManager getRequirementsManager() {
        return requirementsManager;
    }

    public CreateGroupsManager getCreateGroupsManager() {
        return createGroupsManager;
    }

    public GuildsManager getGuildsManager() {
        return guildsManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Language getLanguageManager() {
        return languageManager;
    }

    public Config getConfigManager() {
        return configManager;
    }

    public Database getDatabaseManager() {
        return databaseManager;
    }

    public boolean isVersion_high() {
        return version_high;
    }

    public NetWork getNetWork() {
        return netWork;
    }

    public static LegendaryGuild getLegendaryGuild() {
        return legendaryGuild;
    }


    private boolean BukkitVersionHigh() {
        String name = Bukkit.getServer().getBukkitVersion();
        String versionStr =  name.substring(0,name.indexOf("-"));

        List<String> groups = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (char c : versionStr.toCharArray()) {
            if (c == '.') {
                groups.add(builder.toString());
                builder = new StringBuilder();
                continue;
            }
            builder.append(c);
        }
        groups.add(builder.toString());

        int version = Integer.parseInt(groups.get(1));
        return (version >= 13);
    }
    public String getDate(){
        SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(System.currentTimeMillis());
        return date;
    }


    public void sync(Runnable consumer){
        scheduler.runTaskAsynchronously(this,consumer);
    }
    public void sync(Runnable runnable,int delay){
        scheduler.runTaskLaterAsynchronously(this,runnable,delay);
    }
    public void sync(Runnable runnable,int delay,int timer){
        scheduler.runTaskTimerAsynchronously(this,runnable,delay,timer);
    }
    public void info(String msg, Level level, Throwable throwable){
        MsgUtils.sendConsole("&c"+msg);
    }
    public void info(String msg, Level level){
        MsgUtils.sendConsole("&c"+msg);
    }


    public void async(Runnable runnable) {
        scheduler.runTask(runnable);
    }
}
