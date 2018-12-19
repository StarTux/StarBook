package com.winthier.starbook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class HealCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length > 1) StarBookCommandException.usage(c);
        Player target;
        if (c.args.length >= 1) {
            target = Bukkit.getServer().getPlayerExact(c.args[0]);
        } else {
            target = c.player;
        }
        if (target == null) StarBookCommandException.playerExpected();
        if (c.label.equalsIgnoreCase("feed")) {
            target.setFoodLevel(20);
            target.setSaturation(20.0f);
            msg(c.sender, "%s fed.", target.getName());
        } else if (c.label.equalsIgnoreCase("starve")) {
            target.setFoodLevel(0);
            target.setSaturation(0.0f);
            msg(c.sender, "%s starved.", target.getName());
        } else {
            target.setHealth(target.getMaxHealth());
            msg(c.sender, "%s healed.", target.getName());
        }
    }
}
