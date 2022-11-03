package com.winthier.starbook;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

final class FindSignCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length == 0) StarBookCommandException.usage(c);
        String text = String.join(" ", c.args).toLowerCase();
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState bs : chunk.getTileEntities()) {
                    if (!(bs instanceof Sign)) continue;
                    Sign sign = (Sign) bs;
                    LINES: for (Component component : sign.lines()) {
                        String line = plainText().serialize(component);
                        if (line.toLowerCase().contains(text)) {
                            count += 1;
                            String xyz = bs.getX() + " " + bs.getY() + " " + bs.getZ();
                            String cmd = "/tp " + xyz;
                            c.sender.sendMessage(text("Sign matches: " + world.getName() + " " + xyz)
                                                 .hoverEvent(showText(text(cmd, GRAY)))
                                                 .clickEvent(runCommand(cmd)));
                            break LINES;
                        }
                    }
                }
            }
        }
        c.sender.sendMessage("Result: " + count);
    }
}
