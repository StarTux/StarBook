package com.winthier.starbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

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
            c.player.sendMessage(join(noSeparators(),
                                      text((int) Math.round(prox.dist), YELLOW),
                                      text(" " + prox.name),
                                      text(" " + prox.dir, GRAY)));
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
