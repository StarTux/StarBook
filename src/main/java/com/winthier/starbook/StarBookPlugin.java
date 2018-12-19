package com.winthier.starbook;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class StarBookPlugin extends JavaPlugin implements Listener {
    private WhoCommand whoCommand = null;
    private WandCommand wandCommand = null;
    @Getter private static StarBookPlugin instance;

    @Override public void onEnable() {
        instance = this;
        reloadConfig();
        saveDefaultConfig();
        getCommand("slap").setExecutor(new SlapCommand());
        getCommand("rocket").setExecutor(new RocketCommand());
        getCommand("spawnmob").setExecutor(new SpawnMobCommand(this));
        getCommand("sound").setExecutor(new SoundCommand(this));
        getCommand("playertime").setExecutor(new PlayerTimeCommand());
        getCommand("time").setExecutor(new TimeCommand());
        getCommand("moon").setExecutor(new MoonCommand());
        getCommand("opme").setExecutor(new OpMeCommand());
        getCommand("near").setExecutor(new NearCommand());
        getCommand("script").setExecutor(new ScriptCommand(this));
        getCommand("getblock").setExecutor(new GetBlockCommand(this));
        getCommand("plugininfo").setExecutor(new PluginInfoCommand());
        getCommand("poof").setExecutor(new PoofCommand(this));
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new HealCommand());
        getCommand("starve").setExecutor(new HealCommand());
        getServer().getPluginManager().registerEvents(this, this);
        whoCommand = new WhoCommand(this);
        getCommand("who").setExecutor(whoCommand);
        wandCommand = new WandCommand(this);
        getCommand("wand").setExecutor(wandCommand);
        getCommand("sel").setExecutor(wandCommand);
        getCommand("sel1").setExecutor(wandCommand);
        getCommand("sel2").setExecutor(wandCommand);
        getServer().getPluginManager().registerEvents(wandCommand, this);
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (getConfig().getBoolean("who.ShowOnLogin")) {
            if (event.getPlayer().hasPermission("starbook.who")) {
                whoCommand.showOnlineList(event.getPlayer());
            }
        }
    }
}
