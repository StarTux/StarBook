package com.winthier.starbook;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * Create an end gateway which never beams.
 * Idea taken from https://bugs.mojang.com/browse/MC-107824
 */
class SetEndGatewayCommand extends AbstractCommand {
    /**
     * The command.  This algorithm makes two passes: One sets the
     * BlockData via WorldEdit, so it can be undone via //undo.  The
     * second pass updates the blockstate with the desired age.  Thus,
     * a WorldEdit //redo will not redo the second step.
     */
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) {
            throw new StarBookCommandException("[starbook:setendgateway] player expected");
        }
        if (c.args.length != 0) {
            throw new StarBookCommandException(c);
        }
        Cuboid sel = WorldEdit.getSelection(c.player);
        if (sel == null) throw new StarBookCommandException("Make a selection first!");
        final int maxVolume = 1024;
        if (sel.getVolume() > maxVolume) {
            throw new StarBookCommandException("Max volume (" + maxVolume + ") exceeded!");
        }
        if (sel.getVolume() <= 0) {
            throw new StarBookCommandException("Selection is empty");
        }
        int blocksChangedCount = 0;
        var session = WorldEdit.begin(c.player);
        for (int y = sel.ay; y <= sel.by; y += 1) {
            for (int z = sel.az; z <= sel.bz; z += 1) {
                for (int x = sel.ax; x <= sel.bx; x += 1) {
                    if (!c.player.getWorld().isChunkLoaded(x >> 4, z >> 4)) continue;
                    Block block = c.player.getWorld().getBlockAt(x, y, z);
                    WorldEdit.set(session, block, Material.END_GATEWAY.createBlockData());
                    blocksChangedCount += 1;
                }
            }
        }
        WorldEdit.commit(session, c.player);
        for (int y = sel.ay; y <= sel.by; y += 1) {
            for (int z = sel.az; z <= sel.bz; z += 1) {
                for (int x = sel.ax; x <= sel.bx; x += 1) {
                    Block block = c.player.getWorld().getBlockAt(x, y, z);
                    if (block.getState() instanceof EndGateway blockState) {
                        blockState.setAge(Long.MIN_VALUE);
                        blockState.update();
                    }
                }
            }
        }
        c.sender.sendMessage(text(blocksChangedCount + " blocks updated", YELLOW));
    }
}
