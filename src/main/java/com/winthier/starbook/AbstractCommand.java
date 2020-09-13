package com.winthier.starbook;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * Superclass for all commands.
 */
public abstract class AbstractCommand implements TabExecutor {
    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandContext context = new CommandContext(sender, command, label, args);
        try {
            onCommand(context);
        } catch (StarBookCommandException sbce) {
            if (sbce.isUsage()) {
                return false;
            } else {
                if (sender instanceof Player) {
                    msg(sender, "&c%s", sbce.getMessage());
                } else {
                    StarBookPlugin.getInstance().getLogger().info(String.format("&c%s", sbce.getMessage()));
                }
            }
        }
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return onTabComplete(new CommandContext(sender, command, label, args));
    }

    abstract void onCommand(CommandContext context);

    /**
     * Return tab complete list.
     */
    protected List<String> onTabComplete(CommandContext context) {
        return null;
    }

    protected final List<String> emptyTabList() {
        return new ArrayList<>();
    }

    static final String format(String msg, Object... args) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (args.length > 0) msg = String.format(msg, args);
        return msg;
    }

    static final void msg(CommandSender sender, String msg, Object... args) {
        sender.sendMessage(format(msg, args));
    }
}

