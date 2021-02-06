package com.winthier.starbook;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class SlapCommand extends AbstractCommand {
    final Random random = new Random(System.currentTimeMillis());

    @Override
    public void onCommand(CommandContext c) {
        boolean violent = false;
        boolean high = false;
        boolean silent = false;
        Player target = null;
        boolean everybody = false;
        for (String arg: c.args) {
            if (arg.startsWith("-")) {
                if (arg.contains("v")) violent = true;
                if (arg.contains("h")) high = true;
                if (arg.contains("s")) silent = true;
            } else if (arg.equals("*")) {
                everybody = true;
            } else {
                if (target != null) throw new StarBookCommandException(c);
                target = Bukkit.getServer().getPlayer(arg);
                if (target == null) throw new StarBookCommandException("Player not found: %s", arg);
            }
        }
        if (!everybody && target == null) throw new StarBookCommandException(c);
        String targetName;
        if (everybody) {
            targetName = "everybody";
            for (Player online: Bukkit.getServer().getOnlinePlayers()) {
                online.setVelocity(getVelo(violent, high));
            }
        } else {
            targetName = target.getName();
            target.setVelocity(getVelo(violent, high));
        }
        if (silent) {
            msg(c.sender, "&eYou slapped %s!", targetName);
        } else {
            for (Player recipient: Bukkit.getServer().getOnlinePlayers()) {
                msg(recipient, "&e%s slapped %s", c.sender.getName(), targetName);
            }
        }
    }

    Vector getVelo(boolean violent, boolean high) {
        if (violent) {
            return new Vector(random.nextDouble() * 10.0 - 5.0, random.nextDouble() * 10.0, random.nextDouble() * 10.0 - 5.0);
        } else if (high) {
            return new Vector(random.nextDouble() * 5.0 - 2.5, random.nextDouble() * 5.0, random.nextDouble() * 5.0 - 2.5);
        } else {
            return new Vector(random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 1.0, random.nextDouble() * 2.0 - 1.0);
        }
    }
}
