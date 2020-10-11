package com.winthier.starbook;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        new HighlightTask(c.player, a, b).runTaskTimer(plugin, 1, 1);
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

    @RequiredArgsConstructor
    class HighlightTask extends BukkitRunnable {
        private final Player player;
        private final Block a;
        private final Block b;
        private int iter = 0;
        private int count = 0;
        @Override
        public void run() {
            if (!player.isValid() || !player.getWorld().equals(a.getWorld())) {
                cancel();
                return;
            }
            boolean active = false;
            if (iter <= b.getX() - a.getX()) {
                highlight(a.getX(), a.getY(), a.getZ(), 0);
                highlight(a.getX(), a.getY(), b.getZ(), 0);
                highlight(a.getX(), b.getY(), a.getZ(), 0);
                highlight(a.getX(), b.getY(), b.getZ(), 0);
                active = true;
            }
            if (iter <= b.getY() - a.getY()) {
                highlight(a.getX(), a.getY(), a.getZ(), 1);
                highlight(a.getX(), a.getY(), b.getZ(), 1);
                highlight(b.getX(), a.getY(), a.getZ(), 1);
                highlight(b.getX(), a.getY(), b.getZ(), 1);
                active = true;
            }
            if (iter <= b.getZ() - a.getZ()) {
                highlight(a.getX(), a.getY(), a.getZ(), 2);
                highlight(a.getX(), b.getY(), a.getZ(), 2);
                highlight(b.getX(), a.getY(), a.getZ(), 2);
                highlight(b.getX(), b.getY(), a.getZ(), 2);
                active = true;
            }
            if (!active) {
                iter = 0;
                count += 1;
                if (count > 2) cancel();
            } else {
                iter += 1;
            }
        }

        void highlight(int x, int y, int z, int axis) {
            switch (axis) {
            case 0: x += iter; break;
            case 1: y += iter; break;
            case 2: z += iter; break;
            default: break;
            }
            Block block = a.getWorld().getBlockAt(x, y, z);
            player.spawnParticle(Particle.CRIT, block.getLocation().add(.5, .5, .5), 3, .0, .0, .0, .0);
        }
    }
}
