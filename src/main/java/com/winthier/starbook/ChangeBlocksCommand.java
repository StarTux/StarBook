package com.winthier.starbook;

import com.cavetale.core.struct.Cuboid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
final class ChangeBlocksCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) throw new StarBookCommandException("[starbook:changeblocks] player expected");
        final BlockData from;
        final String blockArg;
        if (c.args.length == 2) {
            try {
                from = Bukkit.createBlockData(c.args[0]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Invalid from block data: " + c.args[0]);
            }
            blockArg  = c.args[1];
        } else if (c.args.length == 1) {
            from = null;
            blockArg = c.args[0];
        } else {
            throw new StarBookCommandException(c);
        }
        final BlockData blockData;
        try {
            blockData = Bukkit.createBlockData(blockArg);
        } catch (IllegalArgumentException iae) {
            throw new StarBookCommandException("Invalid block data: " + blockArg);
        }
        final World world = c.player.getWorld();
        final Cuboid selection = Cuboid.selectionOf(c.player);
        if (selection == null) throw new StarBookCommandException("No selection!");
        final int total = selection.getVolume();
        new BukkitRunnable() {
            private int x = selection.ax;
            private int y = selection.ay;
            private int z = selection.az;
            private int progress = 0;
            private long lastReport;
            private int changedCount;
            @Override public void run() {
                long then = System.currentTimeMillis();
                long now;
                do {
                    progress += 1;
                    Block block = world.getBlockAt(x, y, z);
                    BlockData oldBlockData = block.getBlockData();
                    if (!oldBlockData.matches(blockData) && (from == null || from.matches(oldBlockData)))  {
                        block.setBlockData(blockData, false);
                        changedCount += 1;
                    }
                    x += 1;
                    if (x > selection.bx) {
                        x = selection.ax;
                        z += 1;
                        if (z > selection.bz) {
                            z = selection.az;
                            y += 1;
                            if (y > selection.by) {
                                cancel();
                                if (from != null) {
                                    c.player.sendMessage("ChangeBlocks done: " + changedCount
                                                         + " blocks changed from " + from.getAsString()
                                                         + " to " + blockData.getAsString());
                                } else {
                                    c.player.sendMessage("ChangeBlocks done: " + changedCount
                                                         + " blocks changed to "
                                                         + blockData.getAsString());
                                }
                                return;
                            }
                        }
                    }
                    now = System.currentTimeMillis();
                    if (now - lastReport >= 5000L) {
                        lastReport = now;
                        long percent = (100L * (long) progress) / (long) total;
                        c.player.sendMessage("ChangeBlocks " + progress + "/" + total
                                             + " (" + percent + "%)");
                    }
                } while (now - then < 20L);
            }
        }.runTaskTimer(StarBookPlugin.getInstance(), 1L, 1L);
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        if (c.args.length != 1 && c.args.length != 2) return null;
        List<String> result = new ArrayList<>();
        String cmd = c.args[c.args.length - 1].toLowerCase();
        for (Material material : Material.values()) {
            String key = material.getKey().getKey();
            if (key.contains(cmd)) {
                result.add(key);
            }
        }
        return result;
    }
}
