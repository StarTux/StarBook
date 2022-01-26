package com.winthier.starbook;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

/**
 * WorldEdit access. Based on WorldEdit 7.3.0.
 * Nastily copied from HotSwap.
 */
public final class WorldEdit {
    private WorldEdit() { }

    public static WorldEditPlugin getWorldEdit() {
        return (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    public static Cuboid getSelection(Player player) {
        WorldEditPlugin we = getWorldEdit();
        LocalSession session = we.getSession(player);
        World world = session.getSelectionWorld();
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

    public static EditSession begin(Player player) {
        return com.sk89q.worldedit.WorldEdit.getInstance()
            .getSessionManager()
            .get(BukkitAdapter.adapt(player))
            .createEditSession(BukkitAdapter.adapt(player));
    }

    public static void set(EditSession editSession, Block block, BlockData blockData) {
        try {
            editSession.setBlock(BukkitAdapter.asBlockVector(block.getLocation()), BukkitAdapter.adapt(blockData));
        } catch (MaxChangedBlocksException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void commit(EditSession editSession, Player player) {
        editSession.commit();
        com.sk89q.worldedit.WorldEdit.getInstance()
            .getSessionManager()
            .get(BukkitAdapter.adapt(player))
            .remember(editSession);
        editSession.close();
        editSession = null;
    }
}
