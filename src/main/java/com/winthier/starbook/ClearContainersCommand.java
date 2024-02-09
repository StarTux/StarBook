package com.winthier.starbook;

import com.cavetale.core.item.ItemKinds;
import com.cavetale.core.struct.Cuboid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
final class ClearContainersCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) throw new StarBookCommandException("[starbook:clearcontainers] player expected");
        boolean testing = false;
        if (c.args.length == 1 && c.args[0].equals("test")) {
            testing = true;
        } else if (c.args.length != 0) {
            throw new StarBookCommandException(c);
        }
        final World world = c.player.getWorld();
        final Cuboid selection = Cuboid.selectionOf(c.player);
        if (selection == null) throw new StarBookCommandException("No selection!");
        final int ax = selection.ax >> 4;
        final int az = selection.az >> 4;
        final int bx = selection.bx >> 4;
        final int bz = selection.bz >> 4;
        int total = 0;
        int count = 0;
        for (int z = az; z <= bz; z += 1) {
            for (int x = ax; x <= bx; x += 1) {
                final boolean wasLoaded = world.isChunkLoaded(x, z);
                if (!wasLoaded) {
                    world.loadChunk(x, z);
                }
                final Chunk chunk = world.getChunkAt(x, z);
                for (BlockState blockState : chunk.getTileEntities(selection::contains, false)) {
                    if (!(blockState instanceof Container container)) continue;
                    total += 1;
                    if (container.getInventory().isEmpty()) continue;
                    final List<Component> components = new ArrayList<>();
                    for (ItemStack item : container.getInventory()) {
                        if (item == null || item.isEmpty()) continue;
                        components.add(ItemKinds.chatDescription(item));
                    }
                    final String coords = blockState.getX() + " " + blockState.getY() + " " + blockState.getZ();
                    final String name = blockState.getBlock().getType().getKey().getKey();
                    c.sender.sendMessage(textOfChildren(text(name + " at " + coords + " had "),
                                                        join(separator(space()), components))
                                         .color(DARK_RED)
                                         .hoverEvent(text(coords, GRAY))
                                         .insertion("/tp " + coords));
                    if (!testing) {
                        container.getInventory().clear();
                    }
                    count += 1;
                }
                if (!wasLoaded) {
                    world.unloadChunk(x, z, true);
                }
            }
        }
        if (testing) {
            c.sender.sendMessage(text("Tested " + count + "/" + total + " containers within " + selection, AQUA));
        } else {
            c.sender.sendMessage(text("Cleared " + count + "/" + total + " containers within " + selection, AQUA));
        }
    }
}
