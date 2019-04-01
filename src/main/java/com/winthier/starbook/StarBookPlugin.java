package com.winthier.starbook;

import cn.nukkit.command.PluginCommand;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;

public class StarBookPlugin extends PluginBase implements Listener {
    private WhoCommand whoCommand = null;
    @Getter private static StarBookPlugin instance;

    @Override public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ((PluginCommand)getCommand("slap")).setExecutor(new SlapCommand());
        ((PluginCommand)getCommand("rocket")).setExecutor(new RocketCommand());
        ((PluginCommand)getCommand("time")).setExecutor(new TimeCommand());
        ((PluginCommand)getCommand("moon")).setExecutor(new MoonCommand());
        ((PluginCommand)getCommand("opme")).setExecutor(new OpMeCommand());
        ((PluginCommand)getCommand("near")).setExecutor(new NearCommand());
        ((PluginCommand)getCommand("plugininfo")).setExecutor(new PluginInfoCommand());
        ((PluginCommand)getCommand("poof")).setExecutor(new PoofCommand());
        ((PluginCommand)getCommand("heal")).setExecutor(new HealCommand());
        ((PluginCommand)getCommand("feed")).setExecutor(new HealCommand());
        ((PluginCommand)getCommand("starve")).setExecutor(new HealCommand());
        whoCommand = new WhoCommand(this);
        ((PluginCommand)getCommand("who")).setExecutor(whoCommand);
        getServer().getPluginManager().registerEvents(this, this);
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
