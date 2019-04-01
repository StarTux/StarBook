package com.winthier.starbook;

import cn.nukkit.Player;
import cn.nukkit.Server;

final class HealCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length > 1) StarBookCommandException.usage(c);
        Player target;
        if (c.args.length >= 1) {
            target = Server.getInstance().getPlayerExact(c.args[0]);
        } else {
            target = c.player;
        }
        if (target == null) StarBookCommandException.playerExpected();
        if (c.label.equalsIgnoreCase("feed")) {
            target.getFoodData().setFoodLevel(20);
            target.getFoodData().setFoodSaturationLevel(20.0f);
            msg(c.sender, "%s fed.", target.getName());
        } else if (c.label.equalsIgnoreCase("starve")) {
            target.getFoodData().setFoodLevel(0);
            target.getFoodData().setFoodSaturationLevel(0.0f);
            msg(c.sender, "%s starved.", target.getName());
        } else {
            target.setHealth(target.getMaxHealth());
        }
    }
}
