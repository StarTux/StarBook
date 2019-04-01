package com.winthier.starbook;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

class NearCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) return;
        List<Prox> proxies = new ArrayList<>();
        Location loc = c.player.getLocation();
        for (Player player: c.player.getLevel().getPlayers().values()) {
            if (player.equals(c.player)) continue;
            proxies.add(new Prox(player.getName(), loc.distance(player.getLocation())));
        }
        Collections.sort(proxies);
        for (Prox prox: proxies) {
            msg(c.player, "&e%d&r %s", (int)prox.dist, prox.name);
        }
    }
}

@RequiredArgsConstructor
class Prox implements Comparable<Prox> {
    final String name;
    final double dist;

    @Override
    public int compareTo(Prox o) {
        return Double.compare(dist, o.dist);
    }
}
