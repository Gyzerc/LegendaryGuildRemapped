package com.gyzer.Database.MySQL;

import com.gyzer.Database.DataProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

public class MySQLDatabase extends DataProvider {
    public MySQLDatabase() {
        try {
            initDataBase();
            Arrays.stream(DatabaseTable.values()).forEach(databaseTable -> createTable(databaseTable));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean isMySQL = true;
    protected HikariDataSource connectPool;
    @Override
    protected void initDataBase() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        Optional<ConfigurationSection> sectionOptional = legendaryGuild.getConfigManager().getSection("HikariCP");
        if (sectionOptional.isPresent()) {
            ConfigurationSection section = sectionOptional.get();
            hikariConfig.setConnectionTimeout(section.getLong("connectionTimeout"));
            hikariConfig.setMinimumIdle(section.getInt("minimumIdle"));
            hikariConfig.setMaximumPoolSize(section.getInt("maximumPoolSize"));
            sectionOptional = legendaryGuild.getConfigManager().getSection("Mysql");
            if (sectionOptional.isPresent()) {
                section = sectionOptional.get();
                String url = "jdbc:mysql://" + section.getString("address") + ":" + section.getString("port") + "/" + section.getString("database") + "?useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai";
                hikariConfig.setJdbcUrl(url);
                hikariConfig.setUsername(section.getString("user"));
                hikariConfig.setPassword(section.getString("password"));
                hikariConfig.setAutoCommit(true);
                connectPool = new HikariDataSource(hikariConfig);

                return;
            }
            legendaryGuild.info("config.yml中缺少了 Mysql 配置,请重新生成配置文件进行修改..", Level.SEVERE);
            return;
        }
        legendaryGuild.info("config.yml中缺少了 HikariCP 配置,请重新生成配置文件进行修改..", Level.SEVERE);
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

    @Override
    public void closeDataBase() {
        if (connectPool != null && !connectPool.isClosed()){
            connectPool.close();
            legendaryGuild.info("成功关闭MySQL数据库连接.",Level.INFO);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectPool.getConnection();
    }
}
