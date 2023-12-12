package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.connect.NetworkServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftCommand extends AbstractCommand<JavaPlugin> {
    protected CraftCommand(final JavaPlugin plugin) {
        super(plugin, "craft");
    }

    @Override
    protected void onEnable() {
        if (NetworkServer.current() == NetworkServer.SKYBLOCK) return;
        rootNode.description("Open a crafting table").denyTabCompletion()
            .playerCaller(player -> player.openWorkbench(null, true));
    }
}
