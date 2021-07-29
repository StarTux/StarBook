package com.winthier.starbook;

import com.cavetale.core.event.block.PlayerCanBuildEvent;
import com.cavetale.core.font.Emoji;
import com.cavetale.core.font.GlyphPolicy;
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
    private boolean signChangeEventLock = false;

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
                text = translateColors(text, c.player);
            }
            Component component;
            if (c.player.hasPermission("starbook.signedit.emoji")) {
                GlyphPolicy glyphPolicy = c.player.hasPermission("starbook.signedit.emoji.hidden")
                    ? GlyphPolicy.HIDDEN
                    : GlyphPolicy.PUBLIC;
                component = Emoji.replaceText(text, glyphPolicy, false).asComponent();
            } else {
                component = Component.text(text);
            }
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

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
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
        if (!player.hasPermission("starbook.signedit")) {
            cancelMessage(player);
            return;
        }
        Block block = event.getClickedBlock();
        if (!PlayerCanBuildEvent.call(player, block)) {
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
            signChangeEventLock = true;
            Bukkit.getPluginManager().callEvent(signChangeEvent);
            signChangeEventLock = false;
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

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        if (signChangeEventLock) {
            signChangeEventLock = false;
            return;
        }
        Player player = event.getPlayer();
        for (int i = 0; i < 4; i += 1) {
            String line = event.getLine(i);
            if (line == null) continue;
            if (player.hasPermission("starbook.signedit.color")) {
                line = translateColors(line, player);
            }
            Component component;
            if (player.hasPermission("starbook.signedit.emoji")) {
                GlyphPolicy glyphPolicy = player.hasPermission("starbook.signedit.emoji.hidden")
                    ? GlyphPolicy.HIDDEN
                    : GlyphPolicy.PUBLIC;
                component = Emoji.replaceText(line, glyphPolicy, false).asComponent();
            } else {
                component = Component.text(line);
            }
            event.line(i, component);
        }
    }

    private String translateColors(String in, Player player) {
        return translateColors(in,
                               player.hasPermission("starbook.signedit.color.decorate"),
                               player.hasPermission("starbook.signedit.color.obfuscate"));
    }

    private String translateColors(String in, boolean decorate, boolean obfuscate) {
        if (!in.contains("&")) return in;
        StringBuilder sb = new StringBuilder();
        String tail = in;
        do {
            int index = tail.indexOf('&');
            if (index < 0 || index >= tail.length() - 1) break;
            sb.append(tail.substring(0, index));
            String key = tail.substring(index, index + 2);
            tail = tail.substring(index + 2);
            ChatColor colorCode = ChatColor.getByChar(key.charAt(1));
            if (colorCode == null) {
                sb.append(key);
            } else if (colorCode.isColor()) {
                sb.append(colorCode.toString());
            } else if (colorCode == ChatColor.RESET) {
                sb.append(decorate ? colorCode.toString() : key);
            } else if (colorCode == ChatColor.MAGIC) {
                sb.append(obfuscate ? colorCode.toString() : key);
            } else if (colorCode.isFormat()) {
                sb.append(decorate ? colorCode.toString() : key);
            } else {
                throw new IllegalStateException("colorCode=" + colorCode);
            }
        } while (!tail.isEmpty());
        sb.append(tail);
        return sb.toString();
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
