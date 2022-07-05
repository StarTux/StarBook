package com.winthier.starbook;

import com.cavetale.core.command.RemotePlayer;
import com.cavetale.core.connect.Connect;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

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
                if (target == null) throw new StarBookCommandException("Player not found: " + arg);
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
            c.sender.sendMessage(text("You slapped " + targetName, YELLOW));
        } else {
            for (RemotePlayer recipient : Connect.get().getRemotePlayers()) {
                recipient.sendMessage(text(c.sender.getName() + " slapped " + targetName, YELLOW));
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
