package com.winthier.starbook;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

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
                    LINES: for (String line : sign.getLines()) {
                        if (line.toLowerCase().contains(text)) {
                            count += 1;
                            c.sender.sendMessage("Sign matches: " + world.getName()
                                                 + " " + bs.getX() + " " + bs.getY() + " " + bs.getZ());
                            break LINES;
                        }
                    }
                }
            }
        }
        c.sender.sendMessage("Result: " + count);
    }
}
