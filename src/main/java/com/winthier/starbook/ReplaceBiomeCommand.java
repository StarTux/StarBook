package com.winthier.starbook;

import com.cavetale.core.struct.Cuboid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
final class ReplaceBiomeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) throw new StarBookCommandException("[starbook:replacebiome] player expected");
        if (c.args.length != 2) throw new StarBookCommandException(c);
        final Biome from;
        final Biome to;
        String fromArg = c.args[0];
        String toArg = c.args[1];
        try {
            from = Biome.valueOf(fromArg.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new StarBookCommandException("Invalid biome: " + fromArg);
        }
        try {
            to = Biome.valueOf(toArg.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new StarBookCommandException("Invalid biome: " + toArg);
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
            private int blocksChanged = 0;
            @Override public void run() {
                long then = System.currentTimeMillis();
                long now;
                do {
                    progress += 1;
                    Block block = world.getBlockAt(x, y, z);
                    if (!block.getChunk().isLoaded()) {
                        block.getChunk().load();
                        block.getChunk().addPluginChunkTicket(StarBookPlugin.getInstance());
                    }
                    if (block.getBiome() == from) {
                        block.setBiome(to);
                        blocksChanged += 1;
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
                                world.removePluginChunkTickets(StarBookPlugin.getInstance());
                                c.player.sendMessage("ReplaceBiome done: " + blocksChanged
                                                     + " blocks changed from "
                                                     + from.name().toLowerCase()
                                                     + " to "
                                                     + to.name().toLowerCase());
                                return;
                            }
                        }
                    }
                    now = System.currentTimeMillis();
                    if (now - lastReport >= 5000L) {
                        lastReport = now;
                        long percent = (100L * (long) progress) / (long) total;
                        c.player.sendMessage("ReplaceBiome " + progress + "/" + total
                                             + " (" + percent + "%)");
                    }
                } while (now - then < 20L);
            }
        }.runTaskTimer(StarBookPlugin.getInstance(), 1L, 1L);
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        if (c.args.length > 2) return List.of();
        List<String> result = new ArrayList<>();
        String cmd = c.args[c.args.length - 1].toLowerCase();
        for (Biome biome : Biome.values()) {
            String lower = biome.name().toLowerCase();
            if (lower.contains(cmd)) {
                result.add(lower);
            }
        }
        return result;
    }
}
