package com.gyzer.Database;

import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.SerializeUtils;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class DataProvider extends SerializeUtils {

    protected LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    protected abstract void initDataBase() throws SQLException;
    public abstract void createTable(DatabaseTable table);
    public abstract boolean isExist(DatabaseTable table);
    public abstract void closeDataBase();
    public abstract Connection getConnection() throws SQLException;

    public void checkTable(DatabaseTable table,Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            Builder builder = table.getBuilder();
            for (Builder.Column key : builder.keys) {
                if (!metaData.getColumns(null,null,builder.getTableName(), key.getColumn()).next()) {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("ALTER TABLE " + builder.getTableName() + " ADD COLUMN " + key.getColumn()+" " + key.getType());
                    legendaryGuild.info("检测到表 "+builder.getTableName() +" 缺失列 "+key.getColumn()+" 已自动补全..",Level.INFO);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    protected Optional<ResultSet> getDataStringResult(Connection connection, DataProvider.Builder builder, String target) {
        if (connection == null) {
            return Optional.empty();
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + builder.getTableName() + " WHERE `" + builder.getMainKey() + "` = '" + target + "' LIMIT 1;");
            resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(resultSet) : Optional.empty();
        } catch (SQLException e) {
            closeQuietly(statement,resultSet);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    protected Optional<ResultSet> getDataStrings(Connection connection, DataProvider.Builder builder) {
        if (connection == null) {
            return Optional.empty();
        }
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM "+builder.getTableName()+";");
            rs = statement.executeQuery();
            return rs.next() ? Optional.of(rs) : Optional.empty();
        } catch (SQLException e) {
            closeQuietly(statement,rs);
            e.printStackTrace();
            return Optional.empty();
        }

    }
    protected void delData(Connection connection, DataProvider.Builder builder, String target) {


        if (connection == null) {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `"+builder.getTableName()+"` WHERE `"+builder.getMainKey()+"` = ?")) {
            statement.setString(1,target);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除数据失败:" + target,e);
        }
    }

    protected <T> void setData(Connection connection, DataProvider.Builder builder, T... ts) {
        if (connection == null) {
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(builder.getInsertString())) {
            for (int i = 0; i < ts.length; i++) {
                ps.setObject(i + 1, ts[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("数据写入失败", e);
        }
    }

    protected void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) try { res.close(); } catch (Exception ignored) {}
        }
    }

    public enum DatabaseType {
        MYSQL,SQLite;
        public static DatabaseType getType(String str) {
            if (str != null && !str.isEmpty()) {
                try {
                    return DatabaseType.valueOf(str.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return SQLite;
                }
            }
            return SQLite;
        }
    }

    public enum DatabaseTable{
        GUILD_TEAMSHOP("guild_teamshop",new Builder("guild_teamshop")
                .addVarcharKey("guild",32)
                .addTextKey("id")
                .addDoubleKey("current")
                .addTextKey("bargains")
                .addTextKey("buy")
                .build("guild")),
        SYSTEM_PLACEHODER("system_placeholder",new Builder("system_placeholder")
                .addVarcharKey("name",32)
                .addTextKey("value")
                .build("name")),

        GUILD_DATA("guild_data",
                new Builder("guild_data")
                        .addVarcharKey("guild",32)
                        .addTextKey("owner")
                        .addTextKey("icon")
                        .addTextKey("intro")
                        .addTextKey("notice")
                        .addTextKey("members")
                        .addTextKey("applications")
                        .addTextKey("unlock_icons")
                        .addTextKey("home_server")
                        .addTextKey("home_location")
                        .addTextKey("date")
                        .addTextKey("friends")
                        .addTextKey("buffs")
                        .addDoubleKey("money")
                        .addDoubleKey("exp")
                        .addDoubleKey("treeexp")
                        .addIntegerKey("level")
                        .addIntegerKey("treelevel")
                        .addIntegerKey("extra_members")
                        .build("guild")
        ),
        USER_DATA("user_data",
                new Builder("user_data")
                        .addVarcharKey("player",32)
                        .addTextKey("guild")
                        .addTextKey("position")
                        .addTextKey("date")
                        .addTextKey("water_today")
                        .addTextKey("water_total")
                        .addDoubleKey("points")
                        .addDoubleKey("total_points")
                        .addLongKey("cooldown")
                        .addBooleanKey("wish")
                        .addBooleanKey("teleport_guild_home")
                        .addTextKey("pvp")
                        .build("player")
        ),
        STORE_DATA("store_data",new Builder("store_data")
                .addVarcharKey("guild",32)
                .addTextKey("data")
                .build("guild")),

        GUILD_REDPACKET("guild_redpacket",new Builder("guild_redpacket")
                .addVarcharKey("guild",32)
                .addTextKey("data")
                .build("guild")),

        GUILD_SHOP("guild_shop",new Builder("guild_shop")
                .addVarcharKey("type",32)
                .addTextKey("data")
                .build("type")),
        GUILD_SHOP_DATA("guild_shop_data",new Builder("guild_shop_data")
                .addVarcharKey("player",32)
                .addIntegerKey("date")
                .addIntegerKey("week")
                .addIntegerKey("month")
                .addTextKey("data")
                .build("player")),

        GUILD_ACTIVITY_DATA("guild_activity_data",new Builder("guild_activity_data")
                .addVarcharKey("guild",32)
                .addDoubleKey("points")
                .addDoubleKey("total")
                .addTextKey("claimed")
                .addTextKey("current")
                .addTextKey("history")
                .build("guild"));


        private String name;
        private Builder builder;
        DatabaseTable(String name,Builder builder){
            this.name = name;
            this.builder = builder;
        }
        public String getName() {
            return name;
        }

        public Builder getBuilder() {
            return builder;
        }
    }


    public static class Builder {
        private String tableName;
        private String mainKey;
        private StringBuilder stringBuilder;
        private List<Column> keys;

        public Builder(String tableName) {
            this.keys = new ArrayList<>();
            this.tableName = tableName;
            stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableName+" (");
        }

        public Builder addTextKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("").append(keyName).append(" TEXT DEFAULT NULL");
            keys.add(new Column(keyName,"TEXT DEFAULT NULL"));
            return this;
        }

        public Builder addUUIDKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("").append(keyName).append(" UUID DEFAULT NULL");
            keys.add(new Column(keyName,"UUID DEFAULT NULL"));
            return this;
        }

        public Builder addBlobKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("").append(keyName).append(" BLOB DEFAULT NULL");
            keys.add(new Column(keyName,"BLOB DEFAULT NULL"));
            return this;
        }

        public Builder addIntegerKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` INTEGER DEFAULT 0");
            keys.add(new Column(keyName,"INTEGER DEFAULT 0"));
            return this;
        }

        public Builder addDoubleKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` DOUBLE DEFAULT 0");
            keys.add(new Column(keyName,"DOUBLE DEFAULT 0"));
            return this;
        }
        public Builder addLongKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` LONG NOT NULL");
            keys.add(new Column(keyName,"LONG NOT NULL"));
            return this;
        }
        public Builder addVarcharKey(String keyName,int length){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` varchar("+length+") NOT NULL");
            keys.add(new Column(keyName,"varchar(" + length + ") NOT NULL"));
            return this;
        }
        public Builder addBooleanKey(String keyName){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            stringBuilder.append("`").append(keyName).append("` BOOLEAN NOT NULL");
            keys.add(new Column(keyName,"BOOLEAN NOT NULL"));
            return this;
        }
        public Builder build(String mainKey){
            if (!stringBuilder.toString().endsWith(",") && !stringBuilder.toString().endsWith("(")){
                stringBuilder.append(",");
            }
            this.mainKey = mainKey;
            stringBuilder.append("PRIMARY KEY (`"+mainKey+"`));");
            return this;
        }

        public String getTableName() {
            return tableName;
        }

        public String getMainKey() {
            return mainKey;
        }

        @Override
        public String toString(){
            return stringBuilder.toString();
        }

        public String getInsertString() { //`
            StringBuilder main = new StringBuilder("REPLACE INTO "+tableName+" ");
            StringBuilder keys = new StringBuilder("(");
            StringBuilder keys_unknow = new StringBuilder("(");
            for (int i =0 ; i < this.keys.size() ; i ++) {
                keys.append("`").append(this.keys.get(i).getColumn()).append("`");
                keys_unknow.append("?");
                if (i == this.keys.size() - 1 ) {
                    keys.append(")");
                    keys_unknow.append(")");
                    break;
                } else {
                    keys.append(",");
                    keys_unknow.append(",");
                }
            }
            main.append(keys).append(" VALUES ").append(keys_unknow);
            return main.toString();
        }


        public class Column {
            private String column;
            private String type;

            public Column(String column, String type) {
                this.column = column;
                this.type = type;
            }

            public String getColumn() {
                return column;
            }

            public String getType() {
                return type;
            }
        }
    }

}