package com.gyzer.Configurations.Provider;

import com.gyzer.LegendaryGuild;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

public abstract class FileProvider {
    protected LegendaryGuild legendaryGuild  = LegendaryGuild.getLegendaryGuild();
    public File file;
    public YamlConfiguration yml;
    private String fileName;
    private String internalPath;
    private String dirPath;
    public FileProvider(String fileName,String internalPath,String dirPath) {
        this.fileName = fileName;
        this.dirPath = dirPath;
        this.internalPath = internalPath;
        file = new File(dirPath , fileName);
        if (!file.exists()) {
            legendaryGuild.saveResource(internalPath+fileName,false);
            legendaryGuild.info("成功创建配置文件 " + internalPath + fileName, Level.INFO);
        }
        yml = YamlConfiguration.loadConfiguration(file);
        readDefault();
    }

    protected void reloadFile() {
        file = new File(dirPath , fileName);
        if (!file.exists()) {
            legendaryGuild.saveResource(internalPath+fileName,false);
            legendaryGuild.info("成功创建配置文件 " + internalPath + fileName, Level.INFO);
        }
        yml = YamlConfiguration.loadConfiguration(file);
    }

    protected abstract void readDefault();
    public<T> T getValue(String path,T defaultValue){
        if (yml.get(path) != null){
            try {
                return (T) yml.get(path);
            } catch (ClassCastException ex){
                legendaryGuild.info("强制转化出错！-> "+path, Level.SEVERE);
                return defaultValue;
            }
        }
        yml.set(path,defaultValue);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return defaultValue;
    }

    public Optional<ConfigurationSection> getSection(String path){
        ConfigurationSection section = yml.getConfigurationSection(path);
        return section !=null ? Optional.of(section) : Optional.empty();
    }

    public void saveYml(){
        try {
            yml.save(file);
        } catch (IOException e) {
            legendaryGuild.info("保存 "+file.getName() +" 失败.",Level.SEVERE);
        }
    }
}
