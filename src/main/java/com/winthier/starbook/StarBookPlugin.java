package com.winthier.starbook;

import org.bukkit.plugin.java.JavaPlugin;

public class StarBookPlugin extends JavaPlugin {

    @Override public void onEnable() {
        getCommand("test").setExecutor(new TestCommand());
        getCommand("slap").setExecutor(new SlapCommand());
        getCommand("rocket").setExecutor(new RocketCommand());
        getCommand("spawnmob").setExecutor(new SpawnMobCommand(this));
        getCommand("sound").setExecutor(new SoundCommand(this));
        getCommand("playertime").setExecutor(new PlayerTimeCommand());
        getCommand("time").setExecutor(new TimeCommand());
        getCommand("moon").setExecutor(new MoonCommand());
        getCommand("cropcliff").setExecutor(new CropCliffCommand());
        getCommand("opme").setExecutor(new OpMeCommand());
        getCommand("near").setExecutor(new NearCommand());
        getCommand("script").setExecutor(new ScriptCommand(this));
        getCommand("worldedithighlight").setExecutor(new WorldEditHighlightCommand(this));
        getCommand("getblock").setExecutor(new GetBlockCommand(this));
        getCommand("plugininfo").setExecutor(new PluginInfoCommand());
        getCommand("poof").setExecutor(new PoofCommand(this));
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new HealCommand());
        getCommand("starve").setExecutor(new HealCommand());
        // getCommand("spawnwater").setExecutor(new SpawnWaterCommand());
    }
}
