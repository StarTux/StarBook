package com.winthier.starbook;

import org.bukkit.plugin.java.JavaPlugin;

public class StarBookPlugin extends JavaPlugin {

    @Override public void onEnable() {
        getCommand("slap").setExecutor(new SlapCommand());
        getCommand("spawnmob").setExecutor(new SpawnMobCommand(this));
        getCommand("sound").setExecutor(new SoundCommand());
        getCommand("cropcliff").setExecutor(new CropCliffCommand());
    }
}
