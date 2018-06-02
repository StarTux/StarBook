package com.winthier.starbook;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

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
            throw new StarBookCommandException("Particle not found: %s", c.args[0]);
        }
        int count = 1;
        if (c.args.length >= 2) {
            try {
                count = Integer.parseInt(c.args[1]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad count arg: %s", c.args[1]);
            }
            if (count < 0) throw new StarBookCommandException("Bad count arg: %d", count);
        }
        double offsetX = 0, offsetY = 0, offsetZ = 0;
        if (count > 1) {
            offsetX = 0.25;
            offsetY = 0.25;
            offsetZ = 0.25;
        }
        if (c.args.length >= 3) {
            if (c.args[2].contains(",")) {
                String[] toks = c.args[2].split(",");
                if (toks.length != 3) throw new StarBookCommandException("Expected 3 comma-separated numbers, got: %s", c.args[2]);
                try {
                    offsetX = Double.parseDouble(toks[0]);
                } catch (NumberFormatException nfe) {
                    throw new StarBookCommandException("Invalid x-offset: %s", toks[0]);
                }
                try {
                    offsetY = Double.parseDouble(toks[1]);
                } catch (NumberFormatException nfe) {
                    throw new StarBookCommandException("Invalid y-offset: %s", toks[1]);
                }
                try {
                    offsetZ = Double.parseDouble(toks[2]);
                } catch (NumberFormatException nfe) {
                    throw new StarBookCommandException("Invalid z-offset: %s", toks[2]);
                }
            } else {
                try {
                    offsetX = Double.parseDouble(c.args[2]);
                    offsetY = offsetX;
                    offsetZ = offsetZ;
                } catch (IllegalArgumentException iae) {
                    throw new StarBookCommandException("Bad offset arg: %s", c.args[2]);
                }
            }
        }
        double extra = 0;
        if (c.args.length >= 4) {
            try {
                extra = Double.parseDouble(c.args[3]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad extra arg: %s", c.args[3]);
            }
        }
        Object data = null;
        if (c.args.length >= 5) {
            String arg = c.args[4];
            final Class<?> dataClass = particle.getDataType();
            if (dataClass == null || dataClass.equals(Void.class)) {
                throw new StarBookCommandException("Unexpected data argument for particle %s", particle.name());
            } else if (dataClass.equals(MaterialData.class)) {
                try {
                    Material mat = Material.valueOf(arg.toUpperCase());
                    data = new MaterialData(mat);
                } catch (IllegalArgumentException iae) {
                    throw new StarBookCommandException("Bad material argument: %s", arg);
                }
            } else if (dataClass.equals(ItemStack.class)) {
                try {
                    Material mat = Material.valueOf(arg.toUpperCase());
                    data = new ItemStack(mat);
                } catch (IllegalArgumentException iae) {
                    throw new StarBookCommandException("Bad material argument: %s", arg);
                }
            } else {
                throw new StarBookCommandException("Unsupported data argument type: %s", dataClass.getName());
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
        msg(c.sender, "Showing particle for %s: %dx%s offset=%.2f,%.2f,%.2f extra=%.2f data=%s", target.getName(), count, particle.name(), offsetX, offsetY, offsetZ, extra, data);
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        List<String> result;
        String cmd;
        switch (c.args.length) {
        case 1:
            result = emptyTabList();
            cmd = c.args[0].toLowerCase();
            for (Particle particle: Particle.values()) {
                if (particle.name().toLowerCase().startsWith(cmd)) {
                    result.add(particle.name());
                }
            }
            return result;
        case 2:
            if (c.args[1].isEmpty()) return Arrays.asList("1");
            else return null;
        case 3:
            if (c.args[2].isEmpty()) return Arrays.asList("0,0,0");
            else return null;
        case 4:
            if (c.args[3].isEmpty()) return Arrays.asList("0");
            else return null;
        case 5:
            result = emptyTabList();
            cmd = c.args[4].toUpperCase();
            for (Material mat: Material.values()) {
                if (mat.name().startsWith(cmd)) result.add(mat.name());
            }
            return result;
        default:
            return null;
        }
    }
}
