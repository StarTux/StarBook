package com.winthier.starbook;

import com.cavetale.dirty.Dirty;
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
        Object json = Dirty.getBlockTag(block);
        String jsonstr = json == null ? "{}" : JSONValue.toJSONString(json);
        msg(c.player, "%s %s l=%d (sky=%d b=%d) t=%.2f", block.getBlockData().getAsString(), jsonstr,
            block.getLightLevel(), block.getLightFromSky(), block.getLightFromBlocks(), block.getTemperature());
    }
}
