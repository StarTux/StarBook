package com.winthier.starbook;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class RocketCommand extends AbstractCommand {
    final Random random = new Random(System.currentTimeMillis());

    @Override
    public void onCommand(CommandContext c) {
        boolean high = false;
        boolean silent = false;
        Player target = null;
        boolean everybody = false;
        for (String arg: c.args) {
            if (arg.startsWith("-")) {
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
                online.setVelocity(getVelo(high));
            }
        } else {
            targetName = target.getName();
            target.setVelocity(getVelo(high));
        }
        if (silent) {
            msg(c.sender, "&eYou rocketed %s!", targetName);
        } else {
            for (Player recipient: Bukkit.getServer().getOnlinePlayers()) {
                msg(recipient, "&e%s rocketed %s", c.sender.getName(), targetName);
            }
        }
    }

    Vector getVelo(boolean high) {
        if (high) {
            return new Vector(0.0, 5.0, 0.0);
        } else {
            return new Vector(0.0, 1.0, 0.0);
        }
    }
}
