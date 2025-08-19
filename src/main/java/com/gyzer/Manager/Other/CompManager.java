package com.gyzer.Manager.Other;

import com.gyzer.Comp.Sub.PlaceholderAPIHook;
import com.gyzer.Comp.Sub.PlayerPointsHook;
import com.gyzer.Comp.Sub.ProtocolLibHook;
import com.gyzer.Comp.Sub.VaultHook;

public class CompManager {

    private VaultHook vaultHook;
    private PlayerPointsHook playerPointsHook;
    private PlaceholderAPIHook placeholderAPIHook;
    private ProtocolLibHook protocolLibHook;

    public CompManager() {
        vaultHook = new VaultHook();
        placeholderAPIHook = new PlaceholderAPIHook();
        playerPointsHook =  new PlayerPointsHook();
        protocolLibHook = new ProtocolLibHook();
    }

    public ProtocolLibHook getProtocolLibHook() {
        return protocolLibHook;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public PlayerPointsHook getPlayerPointsHook() {
        return playerPointsHook;
    }

    public PlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }
}
