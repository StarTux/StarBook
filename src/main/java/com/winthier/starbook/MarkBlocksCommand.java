package com.winthier.starbook;

import com.cavetale.core.struct.Cuboid;
import com.cavetale.worldmarker.block.BlockMarker;
import org.bukkit.block.Block;

class MarkBlocksCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) throw new StarBookCommandException("[starbook:makebook] player expected");
        if (c.args.length != 1) throw new StarBookCommandException(c);
        String worldMarkerId = "-".equals(c.args[0]) ? null : c.args[0];
        Cuboid selection = Cuboid.selectionOf(c.player);
        if (selection == null) throw new StarBookCommandException("No selection!");
        int count = 0;
        for (int y = selection.ay; y <= selection.by; y += 1) {
            for (int z = selection.az; z <= selection.bz; z += 1) {
                for (int x = selection.ax; x <= selection.bx; x += 1) {
                    Block block = c.player.getWorld().getBlockAt(x, y, z);
                    if (worldMarkerId == null) {
                        if (BlockMarker.hasId(block)) {
                            BlockMarker.resetId(block);
                            count += 1;
                        }
                    } else {
                        if (!BlockMarker.hasId(block, worldMarkerId)) {
                            BlockMarker.setId(block, worldMarkerId);
                            count += 1;
                        }
                    }
                }
            }
        }
        if (worldMarkerId != null) {
            c.player.sendMessage(count + " blocks marked with " + worldMarkerId);
        } else {
            c.player.sendMessage(count + " blocks reset");
        }
    }
}
