package com.gyzer.Database.SQLite;

import com.gyzer.Database.DataProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

public class SQLiteDatabase extends DataProvider {
    protected HikariDataSource connectPool;
    private File file;
    protected boolean isMySQL = false;
    public SQLiteDatabase() {
        this.file = new File(legendaryGuild.getDataFolder() , "LegendaryGuildData.db");
        initDataBase();
        Arrays.stream(DatabaseTable.values()).forEach(databaseTable -> createTable(databaseTable));
    }

    @Override
    protected void initDataBase() throws RuntimeException {
        if (!file.exists())
            try {
                file.createNewFile();
                legendaryGuild.info("create the sqlite database & 成功创建SQLite数据库", Level.INFO);
            } catch (IOException e) {
                legendaryGuild.info("An exception occurred creating sqlite database & 创建SQLite数据库时出现问题", Level.SEVERE,e);
            }


        HikariConfig hikariConfig = new HikariConfig();
        Optional<ConfigurationSection> sectionOptional = legendaryGuild.getConfigManager().getSection("HikariCP");
        if (sectionOptional.isPresent()) {
            ConfigurationSection section = sectionOptional.get();
            hikariConfig.setConnectionTimeout(section.getLong("connectionTimeout"));
            hikariConfig.setMinimumIdle(section.getInt("minimumIdle"));
            hikariConfig.setMaximumPoolSize(section.getInt("maximumPoolSize"));
            section = sectionOptional.get();
            String url = "jdbc:sqlite:" + file;
            hikariConfig.setJdbcUrl(url);
            hikariConfig.setAutoCommit(true);
            connectPool = new HikariDataSource(hikariConfig);
            return;
        }
        legendaryGuild.info("config.yml中缺少了 HikariCP 配置,请重新生成配置文件进行修改..", Level.SEVERE);

        legendaryGuild.info("成功连接SQLite数据库",Level.INFO);

    }


    @Override
    public void createTable(DatabaseTable table) {
        if (isExist(table)){
            return;
        }
        if (executeUpdate(table.getBuilder().toString())){
            legendaryGuild.info("成功创建表 "+table.getName(),Level.INFO);
        }
    }


    @Override
    public boolean isExist(DatabaseTable table) {
        if (connectPool == null){
            return false;
        }
        try (Connection connection = connectPool.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT `"+table.getBuilder().getMainKey()+"` FROM `"+table.getName()+"` LIMIT 1;")){
            statement.executeQuery();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean executeUpdate(String execute) {
        try (Connection connection = connectPool.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(execute);
            return true;
        } catch (SQLException e) {
            // 优化日志输出，避免暴露完整SQL给终端用户
            String safeMessage = execute.length() > 100 ? execute.substring(0, 100) + "..." : execute;
            legendaryGuild.info("执行更新失败: " + safeMessage, Level.SEVERE, e);
            return false;
        }
    }
    @Override
    public void closeDataBase() {
        if (connectPool != null && !connectPool.isClosed()) {
            connectPool.close();
            legendaryGuild.info("成功断开SQLite数据库连接 & Successfully disconnected SQLite database connection",Level.INFO);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return connectPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
