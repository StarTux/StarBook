package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftCommand extends AbstractCommand<JavaPlugin> {
    protected CraftCommand(final JavaPlugin plugin) {
        super(plugin, "craft");
    }

    @Override
    protected void onEnable() {
        rootNode.description("Open a crafting table").denyTabCompletion()
            .playerCaller(player -> player.openWorkbench(null, true));
    }
}
