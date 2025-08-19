package com.gyzer.Comp.Sub;

import com.gyzer.Comp.Hook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends Hook {
    private Economy economy;
    @Override
    public boolean getHook() {
        RegisteredServiceProvider<Economy> rsp = legendaryGuild.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public Economy getEconomy(){
        return economy;
    }

    public void add(Player p,double a) {
        if (!enable)return;
        economy.depositPlayer(p,a);
    }
    public void take(Player p,double a) {
        if (!enable)return;
        economy.withdrawPlayer(p,a);
    }
    public double get(Player p) {
        if (!enable)return 0.0;
        return economy.getBalance(p);
    }
}
