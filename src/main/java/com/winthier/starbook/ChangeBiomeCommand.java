package com.winthier.starbook;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
final class ChangeBiomeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) throw new StarBookCommandException("[starbook:changebiome] player expected");
        if (c.args.length != 1) throw new StarBookCommandException(c);
        String biomeArg = c.args[0];
        final Biome biome;
        try {
            biome = Biome.valueOf(biomeArg.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new StarBookCommandException("Invalid biome: " + biomeArg);
        }
        final World world = c.player.getWorld();
        final Cuboid selection = WorldEditHighlightCommand.getSelection(c.player);
        if (selection == null) throw new StarBookCommandException("No selection!");
        final int total = selection.volume();
        new BukkitRunnable() {
            private int x = selection.ax;
            private int y = selection.ay;
            private int z = selection.az;
            private int progress = 0;
            private long lastReport;
            @Override public void run() {
                long then = System.currentTimeMillis();
                long now;
                do {
                    progress += 1;
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getBiome() != biome) {
                        block.setBiome(biome);
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
                                c.player.sendMessage("ChangeBiome done: " + total
                                                     + " blocks changed to "
                                                     + biome.name().toLowerCase());
                                return;
                            }
                        }
                    }
                    now = System.currentTimeMillis();
                    if (now - lastReport >= 5000L) {
                        lastReport = now;
                        long percent = (100L * (long) progress) / (long) total;
                        c.player.sendMessage("ChangeBiome " + progress + "/" + total
                                             + " (" + percent + "%)");
                    }
                } while (now - then < 20L);
            }
        }.runTaskTimer(StarBookPlugin.getInstance(), 1L, 1L);
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        if (c.args.length != 1) return null;
        List<String> result = new ArrayList<>();
        String cmd = c.args[0].toLowerCase();
        for (Biome biome : Biome.values()) {
            String lower = biome.name().toLowerCase();
            if (lower.contains(cmd)) {
                result.add(lower);
            }
        }
        return result;
    }
}
