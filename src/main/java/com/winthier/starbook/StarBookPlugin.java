package com.winthier.starbook;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StarBookPlugin extends JavaPlugin implements Listener {
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
        getCommand("winthier").setExecutor(new ServerCommand(this));
        getCommand("winnilla").setExecutor(new ServerCommand(this));
        getCommand("creative").setExecutor(new ServerCommand(this));
        getCommand("hub").setExecutor(new ServerCommand(this));
        getCommand("museum").setExecutor(new ServerCommand(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("starbook.signcolor")) return;
        for (int i = 0; i < 4; i += 1) {
            String line = event.getLine(i);
            if (line == null) continue;
            line = ChatColor.translateAlternateColorCodes('&', line);
            if (!event.getPlayer().hasPermission("starbook.signcolor.magic")) {
                line = line.replace(ChatColor.MAGIC.toString(), "");
            }
            event.setLine(i, line);
        }
    }
}
