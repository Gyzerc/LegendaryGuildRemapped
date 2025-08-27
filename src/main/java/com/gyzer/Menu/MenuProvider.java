package com.gyzer.Menu;

import com.gyzer.Configurations.Config;
import com.gyzer.Configurations.Language;
import com.gyzer.LegendaryGuild;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MenuProvider {
    protected LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();
    protected Config config = legendaryGuild.getConfigManager();
    protected Language lang = legendaryGuild.getLanguageManager();
    private File file;
    private YamlConfiguration yml;
    private String title;
    private Optional<Sound> sound;
    private int size;

    public HashMap<Integer,MenuItem> item;
    public List<MenuItem> buttons;
    public HashMap<String,String> placeholder;
    public List<String> layout;
    private HashMap<String,Integer> functionAmounts;
    public MenuProvider(String name,String internalPath,String path) {
        functionAmounts = new HashMap<>();
        item = new HashMap<>();
        placeholder = new HashMap<>();
        layout = new ArrayList<>();
        buttons = new ArrayList<>();
        file = new File(path , name);
        if (!file.exists()) {
            legendaryGuild.saveResource(internalPath + name,false);
        }
        this.yml = YamlConfiguration.loadConfiguration(file);

        readEssentails();
    }


    private Optional<ConfigurationSection> getSection(String path) {
        return yml.getConfigurationSection(path) != null ? Optional.ofNullable(yml.getConfigurationSection(path)) : Optional.empty();
    }


    public int getSlot(String function) {
        for (Map.Entry<Integer,MenuItem> entry : item.entrySet()) {
            if (entry.getValue().getFuction().equals(function)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private void readEssentails(){


        this.layout = yml.getStringList("gui.layout");
        this.title = MsgUtils.color(yml.getString("gui.title"," "));
        this.size = yml.getInt("gui.size",54);
        this.sound = getSound(yml.getString("gui.sound"));

        getSection("placeholder").ifPresent(section -> {
            for (String key : section.getKeys(false)) {
                placeholder.put(key.toLowerCase(),MsgUtils.color(section.getString(key)));
            }
        });

        getSection("gui.Items").ifPresent(sec -> {
            for (String key:sec.getKeys(false)){
                String display = MsgUtils.color(sec.getString(key+".display",""));
                Material material = getMaterial(sec.getString(key+".material","STONE"));
                int amount = sec.getInt(key+".amount",1);
                int data = sec.getInt(key+".data",0);
                int model = sec.getInt(key+".model",0);
                boolean skull = sec.getBoolean(key+".player-skull",false);
                List<String> lore = MsgUtils.color(sec.getStringList(key+".lore"));
                String fuction = sec.getString(key+".function.type","none");
                String value = sec.getString(key+".function.value","");
                ItemStack i = new ItemStack(material,amount,(short) data);
                ItemMeta id = i.getItemMeta();
                id.setDisplayName(display);
                id.setLore(lore);
                if (model != 0 && legendaryGuild.isVersion_high()) {
                    id.setCustomModelData(model);
                }
                i.setItemMeta(id);
                MenuItem menuItem = new MenuItem(key,i,fuction,value,skull);
                buttons.add(menuItem);
            }
        });


        int lineAmount = 0;
        for (String line : layout){
            char[] chars = line.toCharArray();
            for (int a = 0 ; a < 9 ; a++){
                if (chars.length > a ){
                    char c = chars[a];
                    List<MenuItem> get = buttons.stream().filter(i -> i.getId().equals(c+"")).collect(Collectors.toList());
                    if (!get.isEmpty()){
                        MenuItem menuItem = get.get(0);
                        item.put((lineAmount * 9 + a), menuItem);
                        int amount = functionAmounts.getOrDefault(menuItem.getFuction(),0);
                        functionAmounts.put(menuItem.getFuction() , (amount + 1));
                    }
                }
            }
            lineAmount++;
        }
        size = Math.max((lineAmount*9),9);
    }

    public int getFunctionTypeAmount(String function) {
        return functionAmounts.getOrDefault(function,0);
    }
    public Optional<Sound> getSound() {
        return sound;
    }

    public HashMap<Integer, MenuItem> getItem() {
        return item;
    }

    public List<MenuItem> getButtons() {
        return buttons;
    }

    public List<String> getLayout() {
        return layout;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public YamlConfiguration getYml() {
        return yml;
    }

    public MenuItem getMenuItem(int slot){
        return item.get(slot);
    }

    public String getPlaceHolder(String key){
        return placeholder.get(key) != null ? placeholder.get(key) : "";
    }

    private Optional<Sound> getSound(String sound){
        if (sound == null){
            return Optional.empty();
        }
        try {
            return Optional.of(Sound.valueOf(sound.toUpperCase()));
        } catch (Exception e){
            legendaryGuild.info("&4Cant find the sound named "+sound+" in current server version. ->"+file.getName(), Level.SEVERE);
            return Optional.empty();
        }
    }

    public Material getMaterial(String arg){
        String str=arg.toUpperCase();
        Material material = Material.getMaterial(str);
        if (material == null){
            material = Material.STONE;
            legendaryGuild.info("&4Cant find the material id named "+arg+" in current server version. -> "+file.getName(),Level.SEVERE);
        }
        return material;
    }





    public static class MenuItem<T> {
        private String id;
        private ItemStack item;
        private String fuction;
        private boolean useHead;
        private boolean put;
        private String value;
        private T target;

        public T getTarget() {
            return target;
        }

        public void setTarget(T target) {
            this.target = target;
        }

        public MenuItem(String id, ItemStack item, String fuction) {
            this.id = id;
            this.item = item;
            this.fuction = fuction;
            this.useHead = false;
            this.put = true;
            this.value = null;
        }

        public boolean isPut() {
            return put;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setPut(boolean put) {
            this.put = put;
        }

        public MenuItem(String id, ItemStack item, String fuction, String value, boolean useHead) {
            this.id = id;
            this.item = item;
            this.fuction = fuction;
            this.useHead = useHead;
            this.value = value;
            this.put = true;
        }

        public void setUseHead(boolean useHead) {
            this.useHead = useHead;
        }

        public boolean isUseHead() {
            return useHead;
        }

        public String getId() {
            return id;
        }

        public ItemStack getItem(Player p) {
            ItemStack i = item.clone();
            if (p != null) {
                ItemMeta id = i.getItemMeta();
                if (id != null) {
                    List<String> lore = id.hasLore() ? id.getLore().stream().map(l -> LegendaryGuild.getLegendaryGuild().getCompManager().getPlaceholderAPIHook().replaceHolder(l, p)).collect(Collectors.toList()) : new ArrayList<>();
                    id.setLore(lore);
                    i.setItemMeta(id);
                }
            }
            return i;
        }

        public String getFuction() {
            return fuction;
        }

        public void setItem(ItemStack item) {
            this.item = item;
        }

        public void setFuction(String fuction) {
            this.fuction = fuction;
        }
    }
}
