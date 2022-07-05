package com.winthier.starbook;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
final class ParticleCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length < 1) StarBookCommandException.usage(c);
        Particle particle;
        try {
            particle = Particle.valueOf(c.args[0].toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new StarBookCommandException("Particle not found: " + c.args[0]);
        }
        int count = 1;
        if (c.args.length >= 2) {
            try {
                count = Integer.parseInt(c.args[1]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad count arg: " + c.args[1]);
            }
            if (count < 0) throw new StarBookCommandException("Bad count arg: " + count);
        }
        double offsetX = 0;
        double offsetY = 0;
        double offsetZ = 0;
        if (count > 1) {
            offsetX = 0.25;
            offsetY = 0.25;
            offsetZ = 0.25;
        }
        if (c.args.length >= 3) {
            if (c.args[2].contains(",")) {
                String[] toks = c.args[2].split(",");
                if (toks.length != 3) {
                    throw new StarBookCommandException("Expected 3 comma-separated numbers, got: " + c.args[2]);
                }
                try {
                    offsetX = Double.parseDouble(toks[0]);
                } catch (NumberFormatException nfe) {
                    throw new StarBookCommandException("Invalid x-offset: " + toks[0]);
                }
                try {
                    offsetY = Double.parseDouble(toks[1]);
                } catch (NumberFormatException nfe) {
                    throw new StarBookCommandException("Invalid y-offset: " + toks[1]);
                }
                try {
                    offsetZ = Double.parseDouble(toks[2]);
                } catch (NumberFormatException nfe) {
                    throw new StarBookCommandException("Invalid z-offset: " + toks[2]);
                }
            } else {
                try {
                    offsetX = Double.parseDouble(c.args[2]);
                    offsetY = offsetX;
                    offsetZ = offsetZ;
                } catch (IllegalArgumentException iae) {
                    throw new StarBookCommandException("Bad offset arg: " + c.args[2]);
                }
            }
        }
        double extra = 0;
        if (c.args.length >= 4) {
            try {
                extra = Double.parseDouble(c.args[3]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad extra arg: " + c.args[3]);
            }
        }
        Object data = null;
        if (c.args.length >= 5) {
            String arg = c.args[4];
            final Class<?> dataClass = particle.getDataType();
            if (dataClass == null || dataClass.equals(Void.class)) {
                throw new StarBookCommandException("Unexpected data argument for particle " + particle.name());
            } else if (dataClass.equals(BlockData.class)) {
                try {
                    Material mat = Material.valueOf(arg.toUpperCase());
                    data = mat.createBlockData();
                } catch (IllegalArgumentException iae) {
                    throw new StarBookCommandException("Bad material argument: " + arg);
                }
            } else if (dataClass.equals(ItemStack.class)) {
                try {
                    Material mat = Material.valueOf(arg.toUpperCase());
                    data = new ItemStack(mat);
                } catch (IllegalArgumentException iae) {
                    throw new StarBookCommandException("Bad material argument: " + arg);
                }
            } else if (dataClass.equals(Particle.DustOptions.class)) {
                try {
                    String[] toks = arg.split(",", 4);
                    data = new Particle.DustOptions(org.bukkit.Color.fromRGB(Integer.parseInt(toks[0]),
                                                                             Integer.parseInt(toks[1]),
                                                                             Integer.parseInt(toks[2])),
                                                    Float.parseFloat(toks[3]));
                } catch (Exception e) {
                    throw new StarBookCommandException("Bad dust options argument: " + arg);
                }
            } else {
                throw new StarBookCommandException("Unsupported data argument type: " + dataClass.getName());
            }
        }
        Player target = c.player;
        if (data == null) {
            target.spawnParticle(particle,
                                 target.getEyeLocation().add(target.getEyeLocation().getDirection().multiply(2)),
                                 count, offsetX, offsetY, offsetZ, extra);
        } else {
            target.spawnParticle(particle,
                                 target.getEyeLocation().add(target.getEyeLocation().getDirection().multiply(2)),
                                 count, offsetX, offsetY, offsetZ, extra, data);
        }
        c.sender.sendMessage(join(noSeparators(),
                                  text("Showing particle for "),
                                  text(target.getName(), GREEN),
                                  text(": "),
                                  text(count, GREEN),
                                  text("x", GRAY),
                                  text(particle.name(), GREEN),
                                  text(" offset"),
                                  text(":", GRAY),
                                  text(String.format("%.2f", offsetX), GREEN),
                                  text(",", GRAY),
                                  text(String.format("%.2f", offsetY), GREEN),
                                  text(",", GRAY),
                                  text(String.format("%.2f", offsetZ), GREEN),
                                  text(" extra"),
                                  text(":", GRAY),
                                  text(String.format("%.2f", extra), GREEN),
                                  text(" data"),
                                  text(":", GRAY),
                                  text("" + data, GREEN)));
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        List<String> result;
        String cmd;
        switch (c.args.length) {
        case 1:
            result = new ArrayList<>();
            cmd = c.args[0].toLowerCase();
            for (Particle particle: Particle.values()) {
                if (particle.name().toLowerCase().contains(cmd)) {
                    result.add(particle.name());
                }
            }
            return result;
        case 2:
            if (c.args[1].isEmpty()) return List.of("1");
            else return null;
        case 3:
            if (c.args[2].isEmpty()) return List.of("0,0,0");
            else return null;
        case 4:
            if (c.args[3].isEmpty()) return List.of("0");
            else return null;
        case 5:
            result = new ArrayList<>();
            cmd = c.args[4].toUpperCase();
            for (Material mat: Material.values()) {
                if (mat.name().contains(cmd)) result.add(mat.name());
            }
            return result;
        default:
            return null;
        }
    }
}
