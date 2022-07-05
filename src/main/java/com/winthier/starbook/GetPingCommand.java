package com.winthier.starbook;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public final class GetPingCommand extends AbstractCommand {
    private Component getPingQuality(int i) {
        if (i == 0) return text("N/A", DARK_GRAY);
        if (i < 100) return text("Outstanding", GREEN, BOLD);
        if (i < 250) return text("Excellent", GREEN);
        if (i < 350) return text("Okay", YELLOW);
        return text("High network latency", DARK_RED);
    }

    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length > 1) {
            StarBookCommandException.usage(c);
            return;
        }
        if (c.args.length == 1) {
            if (!c.sender.hasPermission("starbook.getping.other")) {
                StarBookCommandException.usage(c);
                return;
            }
            Player target = Bukkit.getPlayerExact(c.args[0]);
            if (target == null) {
                throw new StarBookCommandException("Player not found: " + c.args[0]);
            }
            int ping = target.getPing();
            if (ping < 0) ping = 0;
            c.sender.sendMessage(join(noSeparators(),
                                      text("Ping of "),
                                      text(target.getName(), GREEN),
                                      text(" is "),
                                      text(ping, YELLOW),
                                      text(" ("),
                                      getPingQuality(ping),
                                      text(")")));
            return;
        }
        if (c.player == null) {
            throw StarBookCommandException.playerExpected();
        }
        int ping = c.player.getPing();
        if (ping < 0) ping = 0;
        c.sender.sendMessage(join(noSeparators(),
                                  text("Your ping is "),
                                  text(ping, YELLOW),
                                  text(" ("),
                                  getPingQuality(ping),
                                  text(")")));
    }
}
