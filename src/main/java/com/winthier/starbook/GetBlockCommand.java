package com.winthier.starbook;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;

@RequiredArgsConstructor
class GetBlockCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        Block block = c.player.getLocation().getBlock();
        msg(c.player, "%s:%d l=%d (sky=%d b=%d) t=%.2f", block.getType(), (int)block.getData(), block.getLightLevel(), block.getLightFromSky(), block.getLightFromBlocks(), block.getTemperature());
    }
}
