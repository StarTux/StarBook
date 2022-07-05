package com.winthier.starbook;

import com.cavetale.core.command.RemotePlayer;
import com.cavetale.core.connect.Connect;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

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
                if (target != null) {
                    throw new StarBookCommandException(c);
                }
                target = Bukkit.getServer().getPlayer(arg);
                if (target == null) {
                    throw new StarBookCommandException("Player not found: " + arg);
                }
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
            c.sender.sendMessage(text("You rocketed " + targetName, YELLOW));
        } else {
            for (RemotePlayer recipient : Connect.get().getRemotePlayers()) {
                recipient.sendMessage(text(c.sender.getName() + " rocketed " + targetName, YELLOW));
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
