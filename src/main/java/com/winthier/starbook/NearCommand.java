package com.winthier.starbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class NearCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) return;
        List<Prox> proxies = new ArrayList<>();
        Location loc = c.player.getLocation();
        for (Player player: c.player.getWorld().getPlayers()) {
            if (player.equals(c.player)) continue;
            proxies.add(new Prox(loc, player));
        }
        if (proxies.isEmpty()) {
            c.player.sendMessage(Component.text("Nobody is nearby!", NamedTextColor.RED));
            return;
        }
        Collections.sort(proxies);
        for (Prox prox: proxies) {
            msg(c.player, "&e%d&r %s &7%s", (int) prox.dist, prox.name, prox.dir);
        }
    }
}

class Prox implements Comparable<Prox> {
    final String name;
    final double dist;
    final String dir;

    Prox(final Location center, final Player player) {
        Location loc = player.getLocation();
        this.name = player.getName();
        this.dist = loc.distance(center);
        StringBuilder sb = new StringBuilder();
        double dz = loc.getZ() - center.getZ();
        if (Math.abs(dz) >= 1.0) {
            sb.append(dz < 0 ? "N" : "S");
        }
        double dx = loc.getX() - center.getX();
        if (Math.abs(dx) >= 1.0) {
            sb.append(dx < 0 ? "W" : "E");
        }
        double dy = loc.getY() - center.getY();
        if (Math.abs(dy) > 0) {
            sb.append(dy < 0 ? " below" : " above");
        }
        this.dir = sb.toString();
    }

    @Override
    public int compareTo(Prox o) {
        return Double.compare(dist, o.dist);
    }
}
