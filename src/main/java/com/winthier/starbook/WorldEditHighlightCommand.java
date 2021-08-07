package com.winthier.starbook;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
final class WorldEditHighlightCommand extends AbstractCommand {
    private final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        WorldEditPlugin we = getWorldEdit();
        if (we == null) throw new StarBookCommandException("WorldEdit not found!");
        Cuboid sel = getSelection(c.player);
        if (sel == null) throw new StarBookCommandException("Make a selection first!");
        World w = c.player.getWorld();
        Block a = sel.getMinBlock(w);
        Block b = sel.getMaxBlock(w);
        highlight(a, b, l -> l.getWorld().spawnParticle(Particle.END_ROD, l, 1, 0.0, 0.0, 0.0, 0.0));
    }

    public static WorldEditPlugin getWorldEdit() {
        return (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    public static Cuboid getSelection(Player player) {
        WorldEditPlugin we = getWorldEdit();
        LocalSession session = we.getSession(player);
        com.sk89q.worldedit.world.World world = session.getSelectionWorld();
        final Region region;
        try {
            region = session.getSelection(world);
        } catch (Exception e) {
            return null;
        }
        if (region == null) return null;
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        return new Cuboid(min.getBlockX(), min.getBlockY(), min.getBlockZ(),
                          max.getBlockX(), max.getBlockY(), max.getBlockZ());
    }

    public boolean highlight(Block a, Block b, Consumer<Location> callback) {
        World world = a.getWorld();
        final int ax = a.getX();
        final int ay = a.getY();
        final int az = a.getZ();
        final int bx = b.getX();
        final int by = b.getY();
        final int bz = b.getZ();
        Location loc = a.getLocation();
        int sizeX = bx - ax + 1;
        int sizeY = by - ay + 1;
        int sizeZ = bz - az + 1;
        for (int y = 0; y < sizeY; y += 1) {
            double dy = (double) y;
            callback.accept(loc.clone().add(0, dy, 0));
            callback.accept(loc.clone().add(0, dy, sizeZ));
            callback.accept(loc.clone().add(sizeX, dy, 0));
            callback.accept(loc.clone().add(sizeX, dy, sizeZ));
        }
        for (int z = 0; z < sizeZ; z += 1) {
            double dz = (double) z;
            callback.accept(loc.clone().add(0, 0, dz));
            callback.accept(loc.clone().add(0, sizeY, dz));
            callback.accept(loc.clone().add(sizeX, 0, dz));
            callback.accept(loc.clone().add(sizeX, sizeY, dz));
        }
        for (int x = 0; x < sizeX; x += 1) {
            double dx = (double) x;
            callback.accept(loc.clone().add(dx, 0, 0));
            callback.accept(loc.clone().add(dx, 0, sizeZ));
            callback.accept(loc.clone().add(dx, sizeY, 0));
            callback.accept(loc.clone().add(dx, sizeY, sizeZ));
        }
        return true;
    }
}
