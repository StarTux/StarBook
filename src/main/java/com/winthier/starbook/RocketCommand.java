package com.winthier.starbook;

import cn.nukkit.Player;
import cn.nukkit.Server;
import java.util.Random;

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
                target = Server.getInstance().getPlayer(arg);
                if (target == null) throw new StarBookCommandException("Player not found: %s", arg);
            }
        }
        if (!everybody && target == null) throw new StarBookCommandException(c);
        double y = high ? 5.0 : 1.0;
        String targetName;
        if (everybody) {
            targetName = "everybody";
            for (Player online: Server.getInstance().getOnlinePlayers().values()) {
                online.addMotion(0.0, y, 0.0);
            }
        } else {
            targetName = target.getName();
            target.addMotion(0.0, y, 0.0);
        }
        if (silent) {
            msg(c.sender, "&eYou rocketed %s!", targetName);
        } else {
            for (Player recipient: Server.getInstance().getOnlinePlayers().values()) {
                msg(recipient, "&e%s rocketed %s", c.sender.getName(), targetName);
            }
        }
    }
}
