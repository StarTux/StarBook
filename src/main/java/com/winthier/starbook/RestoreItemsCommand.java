package com.winthier.starbook;

import java.util.Base64;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class RestoreItemsCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length < 2) {
            throw StarBookCommandException.usage(c);
        }
        Player target = Bukkit.getPlayerExact(c.args[0]);
        if (target == null) {
            throw new StarBookCommandException("Player not found: " + c.args[0]);
        }
        int count = 0;
        for (int i = 1; i < c.args.length; i += 1) {
            String arg = c.args[i];
            if (arg.endsWith(",")) arg = arg.substring(0, arg.length() - 1);
            ItemStack item = deserialize(arg);
            target.getInventory().addItem(item);
            count += 1;
        }
        c.sender.sendMessage(text(count + " items restored and given to " + target.getName(), YELLOW));
    }

    private static ItemStack deserialize(String base64) {
        final byte[] bytes = Base64.getDecoder().decode(base64);
        ItemStack item = ItemStack.deserializeBytes(bytes);
        return item;
    }
}
