package com.winthier.starbook;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

final class FindSignCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        final String text = c.args.length > 0
            ? String.join(" ", c.args).toLowerCase()
            : null;
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState blockState : chunk.getTileEntities()) {
                    if (!(blockState instanceof Sign sign)) continue;
                    if (text != null && !hasText(sign, text)) continue;
                    if (text == null && !hasText(sign)) continue;
                    count += 1;
                    printSign(c, sign);
                }
            }
        }
        c.sender.sendMessage(text("Result: " + count, GRAY));
    }

    private boolean hasText(Sign sign, String text) {
        for (Side side : Side.values()) {
            final SignSide signSide = sign.getSide(side);
            for (Component component : signSide.lines()) {
                String line = plainText().serialize(component);
                if (line.toLowerCase().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasText(Sign sign) {
        for (Side side : Side.values()) {
            final SignSide signSide = sign.getSide(side);
            for (Component component : signSide.lines()) {
                if (!empty().equals(component)) return true;
            }
        }
        return false;
    }

    private void printSign(CommandContext c, Sign sign) {
        final String xyz = sign.getX() + " " + sign.getY() + " " + sign.getZ();
        final String cmd = "/tp " + xyz;
        final List<Component> tooltip = new ArrayList<>();
        tooltip.add(text(cmd, GRAY));
        for (Side side : Side.values()) {
            tooltip.add(empty());
            for (Component line : sign.getSide(side).lines()) {
                tooltip.add(line.color(GRAY));
            }
        }
        c.sender.sendMessage(text("Sign matches: " + sign.getWorld().getName() + " " + xyz)
                             .hoverEvent(showText(join(separator(newline()), tooltip)))
                             .clickEvent(runCommand(cmd)));
    }
}
