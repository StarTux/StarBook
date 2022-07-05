package com.winthier.starbook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
class GetBlockCommand extends AbstractCommand {
    static final Vector VZERO = new Vector(0, 0, 0);
    static final Vector VONE = new Vector(1, 1, 1);
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        Block block = c.player.getLocation().getBlock();
        Component colon = text(":", GRAY);
        c.player.sendMessage(join(noSeparators(),
                                  text(block.getBlockData().getAsString()),
                                  space(),
                                  text("l"), colon, text(block.getLightLevel(), YELLOW),
                                  text(" (", GRAY),
                                  text("sky"), colon, text(block.getLightFromSky(), YELLOW),
                                  text("b"), colon, text(block.getLightFromBlocks(), YELLOW),
                                  text(") ", GRAY),
                                  text("temp"), colon, text(String.format("%.2f", block.getTemperature()), YELLOW),
                                  space(),
                                  text("bb"), colon, text(toString(adjust(block, block.getBoundingBox())), YELLOW),
                                  text("vs"), colon, text(toString(block.getCollisionShape().getBoundingBoxes()), YELLOW)));
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
