package com.winthier.starbook;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class GetPingCommand extends AbstractCommand {
    protected String getPingQuality(int i) {
        if (i == 0) return "" + ChatColor.YELLOW + "N/A";
        if (i < 100) return "" + ChatColor.GREEN + ChatColor.BOLD + "Outstanding";
        if (i < 250) return "" + ChatColor.GREEN + "Excellent";
        if (i < 350) return "" + ChatColor.YELLOW + "Okay";
        return "" + ChatColor.DARK_RED + "High network latency";
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
                msg(c.sender, "&cPlayer not found: %s", c.args[0]);
                return;
            }
            int ping = target.getPing();
            if (ping < 0) ping = 0;
            msg(c.sender, "&bPing of &3%s&b:&r %d (%s&r)",
                target.getName(), ping, getPingQuality(ping));
            return;
        }
        if (c.player == null) {
            msg(c.sender, "[starbook:getping] Player expected");
            return;
        }
        int ping = c.player.getPing();
        if (ping < 0) ping = 0;
        msg(c.player, "&bYour ping:&r %d (%s&r)", ping, getPingQuality(ping));
    }
}
