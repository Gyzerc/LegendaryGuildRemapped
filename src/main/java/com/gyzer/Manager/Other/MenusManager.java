package com.gyzer.Manager.Other;

import com.gyzer.Menu.MenuProvider;


public class MenusManager {
    public MenuProvider BUFF;
    public MenuProvider TEAMSHOP;
    public MenuProvider REDPACKETS;
    public MenuProvider ACTIVITY;
    public MenuProvider POSITIONS ;
    public MenuProvider  ICONSHOP ;
    public MenuProvider MEMBERS;
    public MenuProvider APPLICATIONS ;
    public MenuProvider TRIBUTES;
    public MenuProvider MAIN_MENU;
    public MenuProvider TREE;
    public MenuProvider SHOP;
    public MenusManager() {
        reloadMenuConfigs();
    }

    public void reloadMenuConfigs() {
        MAIN_MENU = new MenuProvider("main.yml","MainMenus/","./plugins/LegendaryGuildRemapped/MainMenus");
        MEMBERS = new MenuProvider("members.yml","MainMenus/","./plugins/LegendaryGuildRemapped/MainMenus");
        ICONSHOP = new MenuProvider("icons.yml","MainMenus/","./plugins/LegendaryGuildRemapped/MainMenus");
        APPLICATIONS = new MenuProvider("applications.yml","MainMenus/","./plugins/LegendaryGuildRemapped/MainMenus");
        TREE = new MenuProvider("menu.yml", "Tree/","./plugins/LegendaryGuildRemapped/Tree");
        POSITIONS = new MenuProvider("menu.yml","Position/","./plugins/LegendaryGuildRemapped/Position");
        SHOP = new MenuProvider("menu.yml", "Shop/","./plugins/LegendaryGuildRemapped/Shop");
        TRIBUTES = new MenuProvider("menu.yml","Tributes/","./plugins/LegendaryGuildRemapped/Tributes");
        ACTIVITY = new MenuProvider("menu.yml","Activity/","./plugins/LegendaryGuildRemapped/Activity");
        REDPACKETS = new MenuProvider("menu.yml","RedPacket/","./plugins/LegendaryGuildRemapped/RedPacket");
        TEAMSHOP = new MenuProvider("menu.yml","TeamShop/","./plugins/LegendaryGuildRemapped/TeamShop");
        BUFF = new MenuProvider("menu.yml","Buff/","./plugins/LegendaryGuildRemapped/Buff");
    }
}
