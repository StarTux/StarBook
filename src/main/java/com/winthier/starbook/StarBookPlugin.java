package com.winthier.starbook;

import org.bukkit.plugin.java.JavaPlugin;

public class StarBookPlugin extends JavaPlugin {

    @Override public void onEnable() {
        getCommand("test").setExecutor(new TestCommand());
        getCommand("slap").setExecutor(new SlapCommand());
        getCommand("rocket").setExecutor(new RocketCommand());
        getCommand("spawnmob").setExecutor(new SpawnMobCommand(this));
        getCommand("sound").setExecutor(new SoundCommand());
        getCommand("playertime").setExecutor(new PlayerTimeCommand());
        getCommand("time").setExecutor(new TimeCommand());
        getCommand("moon").setExecutor(new MoonCommand());
        getCommand("cropcliff").setExecutor(new CropCliffCommand());
        // getCommand("spawnwater").setExecutor(new SpawnWaterCommand());
    }
}
