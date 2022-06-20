package com.winthier.starbook;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

public final class StarBookPlugin extends JavaPlugin {

    @Getter protected static StarBookPlugin instance;
    Meta meta = new Meta(this);

    @Override public void onEnable() {
        instance = this;
        getCommand("slap").setExecutor(new SlapCommand());
        getCommand("rocket").setExecutor(new RocketCommand());
        getCommand("spawnmob").setExecutor(new SpawnMobCommand(this));
        getCommand("sound").setExecutor(new SoundCommand(this));
        getCommand("particles").setExecutor(new ParticleCommand(this));
        getCommand("playertime").setExecutor(new PlayerTimeCommand());
        getCommand("time").setExecutor(new TimeCommand());
        getCommand("moon").setExecutor(new MoonCommand());
        getCommand("cropcliff").setExecutor(new CropCliffCommand());
        getCommand("opme").setExecutor(new OpMeCommand());
        getCommand("near").setExecutor(new NearCommand());
        getCommand("script").setExecutor(new ScriptCommand(this));
        getCommand("worldedithighlight").setExecutor(new WorldEditHighlightCommand(this));
        getCommand("getblock").setExecutor(new GetBlockCommand(this));
        getCommand("poof").setExecutor(new PoofCommand(this));
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new HealCommand());
        getCommand("starve").setExecutor(new HealCommand());
        // getCommand("spawnwater").setExecutor(new SpawnWaterCommand());
        getCommand("reloaddata").setExecutor((s, c, l, a) -> {
                getServer().reloadData();
                s.sendMessage("Data packs reloaded");
                return true;
            });
        getCommand("findsign").setExecutor(new FindSignCommand());
        getCommand("findchest").setExecutor(new FindChestCommand());
        SignEditCommand signEditCommand = new SignEditCommand();
        getCommand("signedit").setExecutor(signEditCommand);
        getServer().getPluginManager().registerEvents(signEditCommand, this);
        getCommand("getping").setExecutor(new GetPingCommand());
        getCommand("makebook").setExecutor(new MakeBookCommand());
        getCommand("markblocks").setExecutor(new MarkBlocksCommand());
        getCommand("changebiome").setExecutor(new ChangeBiomeCommand());
        getCommand("changeblocks").setExecutor(new ChangeBlocksCommand());
        getCommand("setendgateway").setExecutor(new SetEndGatewayCommand());
        getCommand("transferaccount").setExecutor(new TransferAccountCommand());
        new GameModeCommand(this, "gmc", GameMode.CREATIVE).enable();
        new GameModeCommand(this, "gmp", GameMode.SPECTATOR).enable();
        new GameModeCommand(this, "gms", GameMode.SURVIVAL).enable();
        new GameModeCommand(this, "gma", GameMode.ADVENTURE).enable();
    }
}
