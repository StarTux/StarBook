package com.winthier.starbook;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;

final class FindChestCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length == 0) StarBookCommandException.usage(c);
        String text = String.join(" ", c.args).toLowerCase();
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState bs : chunk.getTileEntities()) {
                    if (!(bs instanceof Chest chest)) continue;
                    Component customName = chest.customName();
                    if (customName == null) continue;
                    String name = PlainTextComponentSerializer.plainText().serialize(customName);
                    if (!name.toLowerCase().contains(text)) continue;
                    count += 1;
                    c.sender.sendMessage("Chest matches: " + world.getName()
                                         + " " + bs.getX() + " " + bs.getY() + " " + bs.getZ());
                }
            }
        }
        c.sender.sendMessage("Result: " + count);
    }
}
