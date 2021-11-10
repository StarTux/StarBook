package com.winthier.starbook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
class GetBlockCommand extends AbstractCommand {
    static final Vector VZERO = new Vector(0, 0, 0);
    static final Vector VONE = new Vector(1, 1, 1);
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        Block block = c.player.getLocation().getBlock();
        msg(c.player, "%s l=%d (sky=%d b=%d) t=%.2f bb=%s vs=%s", block.getBlockData().getAsString(),
            block.getLightLevel(), block.getLightFromSky(), block.getLightFromBlocks(), block.getTemperature(),
            toString(adjust(block, block.getBoundingBox())),
            toString(block.getCollisionShape().getBoundingBoxes()));
    }

    static BoundingBox adjust(Block block, BoundingBox bb) {
        final double x = (double) block.getX();
        final double y = (double) block.getY();
        final double z = (double) block.getZ();
        return new BoundingBox(bb.getMinX() - x,
                               bb.getMinY() - y,
                               bb.getMinZ() - z,
                               bb.getMaxX() - x,
                               bb.getMaxY() - y,
                               bb.getMaxZ() - z);
    }

    static String toString(BoundingBox bb) {
        if (bb.getMin().equals(VZERO)) {
            Vector max = bb.getMax();
            if (max.equals(VZERO)) {
                return "ZERO";
            } else if (max.equals(VONE)) {
                return "ONE";
            } else if (max.getX() == 1 && max.getZ() == 1) {
                return toString(max.getY());
            }
        }
        return ""
            + "(" + toString(bb.getMinX())
            + "," + toString(bb.getMinY())
            + "," + toString(bb.getMinZ())
            + "-" + toString(bb.getMaxX())
            + "," + toString(bb.getMaxY())
            + "," + toString(bb.getMaxZ())
            + ")";
    }

    static String toString(double in) {
        if (in == 0) return "0";
        if (in == 1) return "1";
        if (in == 0.5) return "SLAB";
        if (in == 0.0625) return "1/16";
        if (in == 0.9375) return "15/16";
        if (in == 0.125) return "1/8";
        if (in == 0.25) return "1/4";
        if (in == 0.325) return "3/8";
        if (in == 0.625) return "5/8";
        if (in == 0.75) return "3/4";
        if (in == 0.875) return "7/8";
        if (in == 1.5) return "1.5";
        return "" + in;
    }

    static String toString(Collection<BoundingBox> bbs) {
        List<String> strings = new ArrayList<>(bbs.size());
        for (BoundingBox bb : bbs) {
            strings.add(toString(bb));
        }
        return "[" + bbs.size() + "]{"
            + String.join(";", strings)
            + "}";
    }
}
