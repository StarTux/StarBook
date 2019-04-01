package com.winthier.starbook;

import cn.nukkit.Player;
import cn.nukkit.Server;
import java.util.Random;

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
                target = Server.getInstance().getPlayer(arg);
                if (target == null) throw new StarBookCommandException("Player not found: %s", arg);
            }
        }
        if (!everybody && target == null) throw new StarBookCommandException(c);
        final double x, y, z;
        if (violent) {
            x = random.nextDouble() * 10.0 - 5.0;
            y = random.nextDouble() * 10.0;
            z = random.nextDouble() * 10.0 - 5.0;
        } else if (high) {
            x = random.nextDouble() * 5.0 - 2.5;
            y = random.nextDouble() * 5.0;
            z = random.nextDouble() * 5.0 - 2.5;
        } else {
            x = random.nextDouble() * 2.0 - 1.0;
            y = random.nextDouble() * 1.0;
            z = random.nextDouble() * 2.0 - 1.0;
        }
        String targetName;
        if (everybody) {
            targetName = "everybody";
            for (Player online: Server.getInstance().getOnlinePlayers().values()) {
                online.addMotion(x, y, z);
            }
        } else {
            targetName = target.getName();
            target.addMotion(x, y, z);
        }
        if (silent) {
            msg(c.sender, "&eYou slapped %s!", targetName);
        } else {
            for (Player recipient: Server.getInstance().getOnlinePlayers().values()) {
                msg(recipient, "&e%s slapped %s", c.sender.getName(), targetName);
            }
        }
    }
}
