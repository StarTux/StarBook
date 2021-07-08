package com.winthier.starbook;

import com.cavetale.core.font.Emoji;
import com.cavetale.core.font.GlyphPolicy;
import com.winthier.generic_events.GenericEvents;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Value;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

final class SignEditCommand extends AbstractCommand implements Listener {
    private Map<UUID, Entry> playerComponentMap = new HashMap<>();
    private Component tag = Component.text("[SignEdit]", TextColor.color(0xFFFF00));

    @Value
    private final class Entry {
        private final int linum;
        private final Component component;
    }

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) {
            c.sender.sendMessage(ChatColor.YELLOW + "[starbook:signedit] Player expected!");
            return;
        }
        if (c.args.length >= 2) {
            int linum;
            try {
                linum = Integer.parseInt(c.args[0]);
            } catch (NumberFormatException nfe) {
                linum = -1;
            }
            if (linum < 1 || linum > 4) {
                throw new StarBookCommandException("Invalid line number: " + c.args[0]);
            }
            String text = String.join(" ", Arrays.copyOfRange(c.args, 1, c.args.length));
            if (c.player.hasPermission("starbook.signedit.color")) {
                text = ChatColor.translateAlternateColorCodes('&', text);
            }
            Component component = null;
            if (c.player.hasPermission("starbook.signedit.emoji")) {
                GlyphPolicy glyphPolicy = c.player.hasPermission("starbook.signedit.emoji.hidden")
                    ? GlyphPolicy.HIDDEN
                    : GlyphPolicy.PUBLIC;
                component = Emoji.maybeReplaceText(text, glyphPolicy, false);
            }
            if (component == null) component = Component.text(text);
            StringBuilder sb = new StringBuilder();
            ComponentFlattener.textOnly().flatten(component, txt -> sb.append(txt));
            int length = sb.toString().length();
            if (length > 15) {
                throw new StarBookCommandException("Text too long: " + length + ". Max 15!");
            }
            playerComponentMap.put(c.player.getUniqueId(), new Entry(linum, component));
            Component message = Component.text()
                .append(tag).append(Component.space())
                .append(Component.text("Click a sign to replace line " + linum + " with ", NamedTextColor.YELLOW))
                .append(component)
                .build();
            c.sender.sendMessage(message);
        } else if (c.args.length == 0) {
            playerComponentMap.put(c.player.getUniqueId(), new Entry(0, null));
            Component message = Component.text()
                .append(tag).append(Component.space())
                .append(Component.text("Click a sign to edit", NamedTextColor.YELLOW))
                .build();
            c.sender.sendMessage(message);
        } else {
            StarBookCommandException.usage(c);
        }
    }

    private void cancelMessage(Player player) {
        Component message = Component.text()
            .append(tag).append(Component.space())
            .append(Component.text("Sign edit cancelled", NamedTextColor.RED))
            .build();
        player.sendMessage(message);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK:
        case LEFT_CLICK_BLOCK:
            break; // OK
        default: return;
        }
        Player player = event.getPlayer();
        Entry entry = playerComponentMap.remove(player.getUniqueId());
        if (entry == null) return;
        if (event.isCancelled() || !player.hasPermission("starbook.signedit")) {
            cancelMessage(player);
            return;
        }
        Block block = event.getClickedBlock();
        if (!GenericEvents.playerCanBuild(player, block)) {
            cancelMessage(player);
            return;
        }
        BlockState state = block.getState();
        if (!(state instanceof Sign)) {
            cancelMessage(player);
            return;
        }
        event.setCancelled(true);
        Sign sign = (Sign) state;
        if (entry.linum > 0) {
            List<Component> lines = new ArrayList<>(sign.lines());
            lines.set(entry.linum - 1, entry.component);
            SignChangeEvent signChangeEvent = new SignChangeEvent(block, player, lines);
            Bukkit.getPluginManager().callEvent(signChangeEvent);
            if (signChangeEvent.isCancelled()) {
                cancelMessage(player);
                return;
            }
            sign.line(entry.linum - 1, entry.component);
            sign.update();
            Component message = Component.text()
                .append(tag).append(Component.space())
                .append(Component.text("Edited line " + entry.linum, NamedTextColor.YELLOW))
                .build();
            player.sendMessage(message);
        } else {
            player.openSign(sign);
            Component message = Component.text()
                .append(tag).append(Component.space())
                .append(Component.text("Editing sign " + entry.linum, NamedTextColor.YELLOW))
                .build();
            player.sendMessage(message);
        }
    }

    protected List<String> onTabComplete(CommandContext c) {
        if (c.player == null) return null;
        if (c.args.length == 1) {
            return c.args[0].isEmpty()
                ? Arrays.asList("1", "2", "3", "4")
                : Collections.emptyList();
        } else if (c.args.length >= 2) {
            if (c.player.hasPermission("starbook.signedit.emoji")) {
                String arg = c.args[c.args.length - 1];
                GlyphPolicy glyphPolicy = c.player.hasPermission("starbook.signedit.emoji.hidden")
                    ? GlyphPolicy.HIDDEN
                    : GlyphPolicy.PUBLIC;
                return Emoji.tabComplete(arg, glyphPolicy);
            }
        }
        return null;
    }
}
