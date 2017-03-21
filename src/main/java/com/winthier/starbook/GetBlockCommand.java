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
        int data = (int)block.getData();
        msg(c.player, "%s:%d (%s)", block.getType(), data, Integer.toBinaryString(data));
    }
}
