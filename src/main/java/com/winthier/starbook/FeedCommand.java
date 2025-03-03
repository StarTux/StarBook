package com.winthier.starbook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

final class FeedCommand extends AbstractCommand {
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
        target.setFoodLevel(20);
        target.setSaturation(20.0f);
        target.setExhaustion(0f);
        c.sender.sendMessage(text(target.getName() + " was fed", GREEN));
    }
}
