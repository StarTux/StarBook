package com.winthier.starbook;

import org.bukkit.block.Block;

final class TestCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        Block at = c.player.getLocation().getBlock();
        Block block = c.player.getWorld().getHighestBlockAt(at.getX(), at.getZ());
        msg(c.player, "%s %d/%d", block.getType(), block.getY(), at.getY());
    }
}
