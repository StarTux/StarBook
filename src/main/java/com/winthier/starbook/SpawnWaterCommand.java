package com.winthier.starbook;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

final class SpawnWaterCommand extends AbstractCommand {
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
                Block block = loc.getWorld().getBlockAt(x, y - 1, z);
                if (block.getType() == Material.WHITE_STAINED_GLASS) {
                        fillColumn(block);
                        count += 1;
                }
            }
        }
        msg(c.player, "%d columns cleared", count);
    }

    void fillColumn(Block block) {
        block.setType(Material.WATER, false);
        while (true) {
            block = block.getRelative(0, -1, 0);
            if (block.getY() < 0) return;
            if (block.getType() == Material.AIR
                || block.getType() == Material.WATER) {
                block.setType(Material.WATER, false);
            }
        }
    }
}
