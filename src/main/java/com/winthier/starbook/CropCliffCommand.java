package com.winthier.starbook;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

class CropCliffCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        int radius = 8;
        if (c.args.length >= 1) {
            radius = Integer.parseInt(c.args[0]);
        }
        Location loc = c.player.getLocation();
        int cx = loc.getBlockX();
        int y = loc.getBlockY();
        int cz = loc.getBlockZ();
        int count = 0;
        for (int z = cz - radius; z <= cz + radius; ++z) {
            for (int x = cx - radius; x <= cx + radius; ++x) {
                Block block = loc.getWorld().getBlockAt(x, y, z);
                Material mat = block.getType();
                if (mat == Material.AIR ||
                    (!mat.isOccluding() && !mat.isSolid())) {
                    Block b2 = loc.getWorld().getBlockAt(x, y - 1, z);
                    Material m2 = b2.getType();
                    if (m2.isOccluding() &&
                        m2.isSolid()) {
                        clearLine(b2);
                        count += 1;
                    }
                }
            }
        }
        msg(c.player, "%d columns cleared", count);
    }

    void clearLine(Block block) {
        while (true) {
            if (block.getY() < 0) return;
            if (block.getType() == Material.AIR) return;
            block.setType(Material.AIR, false);
            block = block.getRelative(0, -1, 0);
        }
    }
}
