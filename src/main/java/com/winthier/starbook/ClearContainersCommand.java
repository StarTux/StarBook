package com.winthier.starbook;

import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
final class ClearContainersCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) throw new StarBookCommandException("[starbook:clearcontainers] player expected");
        if (c.args.length != 0) throw new StarBookCommandException(c);
        final World world = c.player.getWorld();
        final Cuboid selection = WorldEditHighlightCommand.getSelection(c.player);
        if (selection == null) throw new StarBookCommandException("No selection!");
        final int ax = selection.ax >> 4;
        final int az = selection.az >> 4;
        final int bx = selection.bx >> 4;
        final int bz = selection.bz >> 4;
        int total = 0;
        int count = 0;
        for (int z = az; z <= bz; z += 1) {
            for (int x = ax; x <= bx; x += 1) {
                Chunk chunk = world.getChunkAt(x, z);
                for (BlockState blockState : chunk.getTileEntities(selection::contains, false)) {
                    if (blockState instanceof Container container) {
                        total += 1;
                        if (container.getInventory().isEmpty()) continue;
                        container.getInventory().clear();
                        count += 1;
                    }
                }
            }
        }
        c.sender.sendMessage(text("Cleared " + count + "/" + total + " containers within " + selection, AQUA));
    }
}
