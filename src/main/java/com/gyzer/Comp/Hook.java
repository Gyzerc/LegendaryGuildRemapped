package com.gyzer.Comp;


import com.gyzer.LegendaryGuild;

public abstract class Hook {
    public LegendaryGuild legendaryGuild = LegendaryGuild.getLegendaryGuild();

    public Hook() {
        enable = getHook();
    }

    public boolean enable;
    public boolean isEnable(){
        return enable;
    }
    public abstract boolean getHook();
}
