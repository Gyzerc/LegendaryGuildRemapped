package com.gyzer.Database;

import com.gyzer.Data.Guild.Guild;
import com.gyzer.Data.Guild.GuildActivityData;
import com.gyzer.Data.Other.Guild_Redpacket;
import com.gyzer.Data.Player.PlayerShopData;
import com.gyzer.Data.Guild.TeamShop.GuildTeamShopData;
import com.gyzer.Data.Other.StringStore;
import com.gyzer.Data.Player.User;
import com.gyzer.Data.Player.WaterDataStore;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.SerializeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.gyzer.Data.Player.WaterDataStore.WaterData.processWaterData;

public class Database {
    private LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    private DataProvider dataProvider;
    public Database(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    public Optional<User> getUser(String player) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM user_data WHERE player = ? LIMIT 1")) {

            statement.setString(1, player);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // 直接处理结果集，避免Optional包装
                    String guild = rs.getString("guild");
                    String position = rs.getString("position");
                    String date = Optional.ofNullable(rs.getString("date")).orElse("...");

                    User.PvpType pvp = User.PvpType.getType(rs.getString("pvp"));
                    boolean wish = rs.getBoolean("wish");
                    boolean teleport_guild_home = rs.getBoolean("teleport_guild_home");
                    double points = rs.getDouble("points");
                    double total_points = rs.getDouble("total_points");
                    int cooldown = rs.getInt("cooldown");

                    // 优化水壶数据处理
                    String water_day = rs.getString("water_today");
                    String water_total = rs.getString("water_total");

                    HashMap<String, WaterDataStore.WaterData> water = processWaterData(
                            water_day, water_total
                    );

                    WaterDataStore store = new WaterDataStore(water);

                    return Optional.of(new User(player, guild, position, date, store, cooldown,
                            wish, teleport_guild_home, points, total_points, pvp));
                }
            }
        } catch (SQLException ex) {
            legendaryGuild.info("获取玩家数据时出错: " + player, Level.SEVERE, ex);
        }
        return Optional.empty();
    }

    public void saveUser(User user) {
        try (Connection connection = dataProvider.getConnection()) {
            WaterDataStore store=user.getWaterDataStore();
            String day = store.toString_Day();
            String total = store.toString_Total();

            dataProvider.setData(connection, DataProvider.DatabaseTable.USER_DATA.getBuilder(),
                    user.getPlayer(),          // 1
                    user.getGuild(),           // 2
                    user.getPosition(),        // 3
                    user.getDate(),            // 4
                    day,      // 5
                    total,    // 6
                    user.getPoints(),          // 7
                    user.getTotal_points(),    // 8
                    user.getCooldown(),        // 9
                    user.isWish(),             // 10
                    user.isTeleport_guild_home(), // 11
                    user.getPvp()              // 12 枚举类型会自动处理
            );
        } catch (SQLException ex) {
            // 自定义异常包含更多上下文信息
            ex.printStackTrace();
        }
    }

    public List<String> getUserNames() {
        String GET_ALL_USERS_SQL = "SELECT player FROM user_data";

        List<String> userNames = new ArrayList<>();

        // 使用单个try-with-resources管理所有资源
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userNames.add(rs.getString("player"));
            }

        } catch (SQLException e) {
            // 使用自定义日志方法包含更多上下文
            e.printStackTrace();
        }
        return userNames;
    }

    public Optional<Guild> getGuild(String guild) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM guild_data WHERE guild = ? LIMIT 1")) {

            statement.setString(1, guild);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // 直接处理结果集，避免Optional包装
                    String owner = rs.getString("owner");
                    String icon = rs.getString("icon");

                    String date = rs.getString("date") != null ? rs.getString("date") : "...";

                    List<String> intro = SerializeUtils.StrToList(rs.getString("intro"));
                    List<String> notice = SerializeUtils.StrToList(rs.getString("notice"));
                    LinkedList<String> members = SerializeUtils.StrToLinkList(rs.getString("members"));
                    LinkedList<Guild.Application> applications = SerializeUtils.StrToApplications(rs.getString("applications"));
                    List<String> unlock_icons = SerializeUtils.StrToList(rs.getString("unlock_icons"));
                    List<String> friends = SerializeUtils.StrToList(rs.getString("friends"));
                    StringStore buff = SerializeUtils.StringToStringStore(rs.getString("buffs"));

                    String home_server = rs.getString("home_server");
                    Guild.GuildHomeLocation location = SerializeUtils.StrToLocation(home_server,rs.getString("home_location"));

                    double money = rs.getDouble("money");
                    double exp = rs.getDouble("exp");
                    double treeexp = rs.getDouble("treeexp");

                    int level = rs.getInt("level");
                    int treelevel = rs.getInt("treelevel");

                    int extra_members = rs.getInt("extra_members");

                    Guild data = new Guild(guild,owner,icon,date,money,exp,treeexp,level,treelevel,intro,notice,friends,buff,unlock_icons,members,applications,location,extra_members);
                    return Optional.of(data);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveGuild(Guild guild) {
        try (Connection connection = dataProvider.getConnection()) {
            Guild.GuildHomeLocation location = guild.getHome();
            String server = location!=null ? location.getServer() : legendaryGuild.SERVER;
            String loc = location != null ? SerializeUtils.LocationToStr(location) : null;

            dataProvider.setData(connection, DataProvider.DatabaseTable.GUILD_DATA.getBuilder(),
                    guild.getGuild(),
                    guild.getOwner(),
                    guild.getIcon(),

                    SerializeUtils.ListToStr(guild.getIntro()),
                    SerializeUtils.ListToStr(guild.getNotice()),
                    SerializeUtils.LinkListToStr(guild.getMembers()),

                    SerializeUtils.ApplicationsToStr(guild.getApplications()),
                    SerializeUtils.ListToStr(guild.getUnlock_icons()),

                    server,
                    loc,

                    guild.getDate(),
                    SerializeUtils.ListToStr(guild.getFriends()),
                    guild.getBuffs().toString(),



                    guild.getMoney(),
                    guild.getExp(),
                    guild.getTreeexp(),
                    guild.getLevel(),
                    guild.getTreelevel(),
                    guild.getExtra_members()
            );
        } catch (SQLException ex) {
            // 自定义异常包含更多上下文信息
            ex.printStackTrace();
        }
    }

    public void delGuild(String guild) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM guild_data WHERE guild = ?")) {

            ps.setString(1, guild);
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                legendaryGuild.info("已删除公会: " + guild,Level.INFO);
            } else {
                legendaryGuild.info("尝试删除不存在的公会: " + guild,Level.SEVERE);
            }

        } catch (SQLException e) {
            // 使用自定义异常处理
            e.printStackTrace();
        }
    }

    public List<String> getGuilds() {
        String GET_ALL_USERS_SQL = "SELECT guild FROM guild_data";

        List<String> userNames = new ArrayList<>();

        // 使用单个try-with-resources管理所有资源
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userNames.add(rs.getString("guild"));
            }

        } catch (SQLException e) {
            // 使用自定义日志方法包含更多上下文
            e.printStackTrace();
        }
        return userNames;
    }

    public Optional<GuildActivityData> getGuildActivityData(String guild) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM guild_activity_data WHERE guild = ? LIMIT 1")) {

            statement.setString(1, guild);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    double points = rs.getDouble("points");
                    double total = rs.getDouble("total");
                    String claimedStr = rs.getString("claimed");
                    StringStore claimed = SerializeUtils.StringToActivityData(claimedStr);
                    HashMap<String,Double> current = (HashMap<String, Double>) dataProvider.getMap(rs.getString("current")).entrySet().stream().collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> Double.parseDouble(String.valueOf(e.getValue()))
                    ));
                    HashMap<String,Double> history = (HashMap<String, Double>) dataProvider.getMap(rs.getString("history")).entrySet().stream().collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> Double.parseDouble(String.valueOf(e.getValue()))
                    ));
                    return Optional.of(new GuildActivityData(guild,points,total,claimed,current,history));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveGuildActivityData(GuildActivityData data) {

        try (Connection connection = dataProvider.getConnection()){
            dataProvider.setData(connection, DataProvider.DatabaseTable.GUILD_ACTIVITY_DATA.getBuilder(),
                    data.getGuild(),
                    data.getPoints(),
                    data.getTotal_points(),
                    data.getClaimed().toString(),
                    SerializeUtils.getMapString(data.getCurrent()),
                    SerializeUtils.getMapString(data.getHistory())
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getGuildActivityDatas() {
        String GET_ALL_USERS_SQL = "SELECT guild FROM guild_activity_data";

        List<String> userNames = new ArrayList<>();

        // 使用单个try-with-resources管理所有资源
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userNames.add(rs.getString("guild"));
            }

        } catch (SQLException e) {
            // 使用自定义日志方法包含更多上下文
            e.printStackTrace();
        }
        return userNames;
    }

    public Optional<String> getSystemData(String key) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM system_placeholder WHERE name = ? LIMIT 1")) {
            statement.setString(1, key);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString("value"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveSystemData(String key,String value) {
        try (Connection connection = dataProvider.getConnection()){
            dataProvider.setData(connection, DataProvider.DatabaseTable.SYSTEM_PLACEHODER.getBuilder(),
                    key,
                    value
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Optional<PlayerShopData> getShopData(String player) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM guild_shop_data WHERE player = ? LIMIT 1")) {
            statement.setString(1, player);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    HashMap<PlayerShopData.DateType, Integer> lastDate = new HashMap<>();
                    lastDate.put(PlayerShopData.DateType.DATE , rs.getInt("date"));
                    lastDate.put(PlayerShopData.DateType.WEEK , rs.getInt("week"));
                    lastDate.put(PlayerShopData.DateType.MONTH , rs.getInt("month"));

                    String data = rs.getString("data");
                    HashMap<String,Integer> buys = SerializeUtils.toShopData(data);
                    return Optional.of(new PlayerShopData(player,lastDate,buys));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public void saveShopData(PlayerShopData data) {
        try (Connection connection = dataProvider.getConnection()){
            dataProvider.setData(connection, DataProvider.DatabaseTable.GUILD_SHOP_DATA.getBuilder(),
                    data.getName(),
                    data.getLastDate(PlayerShopData.DateType.DATE),
                    data.getLastDate(PlayerShopData.DateType.WEEK),
                    data.getLastDate(PlayerShopData.DateType.MONTH),
                    SerializeUtils.getMapString(data.getBuy())
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public Optional<GuildTeamShopData> getGuildTeamShopData(String guild) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM guild_teamshop WHERE guild = ? LIMIT 1")) {
            statement.setString(1, guild);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    double current = rs.getDouble("current");
                    HashMap<String, Double> bargains = (HashMap<String, Double>) SerializeUtils.getMap(rs.getString("bargains"))
                            .entrySet().stream().collect(
                                    Collectors.toMap(
                                            Map.Entry::getKey,
                                            entry -> Double.parseDouble(String.valueOf(entry.getValue())
                                            )
                                    )
                            );

                    HashMap<String, Integer> buy = (HashMap<String, Integer>)  SerializeUtils.getMap(rs.getString("buy")).entrySet().stream().collect(
                            Collectors.toMap(
                                    e -> e.getKey(),
                                    entry -> Integer.parseInt(String.valueOf(entry.getValue())))
                    );
                    return Optional.of(new GuildTeamShopData(guild, id, current, bargains, buy));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void setGuildTeamShopData(GuildTeamShopData data) {
        try (Connection connection = dataProvider.getConnection()){
            dataProvider.setData(connection, DataProvider.DatabaseTable.GUILD_TEAMSHOP.getBuilder(),
                    data.getGuild(),
                    data.getTodayShopId(),
                    data.getCurrentPrice(),
                    SerializeUtils.getMapString(data.getBargains()),
                    SerializeUtils.getMapString(data.getBuy())
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void clearGuildTeamShopData(String guild) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement ps = createClearStatement(connection, guild)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            legendaryGuild.info("刷新公会团购数据出错！ ", Level.SEVERE, ex);
        }
    }
    private PreparedStatement createClearStatement(Connection connection, String guild)
            throws SQLException {
        if (guild == null) {
            return connection.prepareStatement(
                    "DELETE FROM " + DataProvider.DatabaseTable.GUILD_TEAMSHOP.getName()
            );
        } else {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM `" + DataProvider.DatabaseTable.GUILD_TEAMSHOP.getName() + "` WHERE guild=?"
            );
            ps.setString(1, guild);
            return ps;
        }
    }

    public Optional<Guild_Redpacket> getRedPacket(String guild) {
        try (Connection connection = dataProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM guild_redpacket WHERE guild = ? LIMIT 1")) {
            statement.setString(1, guild);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Guild_Redpacket redpacket = Guild_Redpacket.toData(guild,rs.getString("data"));
                    return Optional.of(redpacket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveRedPacket(Guild_Redpacket redpacket) {
        try (Connection connection = dataProvider.getConnection()){
            dataProvider.setData(connection, DataProvider.DatabaseTable.GUILD_REDPACKET.getBuilder(),
                    redpacket.getGuild(),
                    redpacket.toString()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public void close() {
        dataProvider.closeDataBase();
    }
}
