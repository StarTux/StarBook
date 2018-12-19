package com.winthier.starbook;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.json.simple.JSONValue;

@RequiredArgsConstructor
class GetBlockCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        Block block = c.player.getLocation().getBlock();
        msg(c.player, "%s %s l=%d (sky=%d b=%d) t=%.2f", block.getType().name(), "?", block.getLightLevel(), block.getLightFromSky(), block.getLightFromBlocks(), block.getTemperature());
    }
}
