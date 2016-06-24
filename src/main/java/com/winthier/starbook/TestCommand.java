package com.winthier.starbook;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

class TestCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length != 2) return;
        EntityType entityType = EntityType.valueOf(c.args[0].toUpperCase());
        int radius = Integer.parseInt(c.args[1]);
        int count = 0;
        double r = (double)radius;
        for (Entity e: c.player.getNearbyEntities(r, r, r)) {
            if (e.getType() == entityType) count += 1;
        }
        c.player.sendMessage("Count " + entityType + ": " + count);
    }
}
