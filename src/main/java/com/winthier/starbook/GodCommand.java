package com.winthier.starbook;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@RequiredArgsConstructor
class GodCommand extends AbstractCommand implements Listener {
    final StarBookPlugin plugin;
    static final String GOD_KEY = "starbook.god";

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 0) StarBookCommandException.usage(c);
        boolean isGod = plugin.meta.has(c.player, GOD_KEY);
        switch (c.label) {
        case "god":
            if (isGod) {
                throw new StarBookCommandException("You're already in god mode!");
            }
            plugin.meta.set(c.player, GOD_KEY, true);
            c.player.sendMessage(ChatColor.GOLD + "God mode enabled.");
            break;
        case "ungod":
            if (!isGod) {
                throw new StarBookCommandException("You're not in god mode!");
            }
            plugin.meta.remove(c.player, GOD_KEY);
            c.player.sendMessage(ChatColor.GOLD + "God mode disabled.");
            break;
        default: break;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!plugin.meta.has(player, GOD_KEY)) return;
        event.setCancelled(true);
        return;
    }
}
