package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.connect.NetworkServer;
import com.cavetale.core.menu.MenuItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public final class CraftCommand extends AbstractCommand<JavaPlugin> implements Listener {
    protected CraftCommand(final JavaPlugin plugin) {
        super(plugin, "craft");
    }

    @Override
    protected void onEnable() {
        if (NetworkServer.current() == NetworkServer.SKYBLOCK) return;
        rootNode.description("Open a crafting table").denyTabCompletion()
            .playerCaller(player -> player.openWorkbench(null, true));
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onMenuItem(MenuItemEvent event) {
        if (event.getPlayer().hasPermission("fam.friends")) {
            final ItemStack icon = new ItemStack(Material.CRAFTING_TABLE);
            icon.editMeta(m -> m.displayName(text("Crafting Table", GOLD).decoration(ITALIC, false)));
            event.addItem(builder -> builder
                          .key("starbook:craft")
                          .command("craft")
                          .icon(icon));
        }
    }
}
