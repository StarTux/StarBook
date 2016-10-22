package com.winthier.starbook;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class AbstractCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandContext context = new CommandContext(sender, command, label, args);
        try {
            onCommand(context);
        } catch (StarBookCommandException sbce) {
            msg(sender, "&cUsage: %s", sbce.getMessage());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        CommandContext context = new CommandContext(sender, command, label, args);
        onTabComplete(context);
        return context.tabCompletions;
    }

    abstract void onCommand(CommandContext context);

    void onTabComplete(CommandContext context) {}

    protected List<String> emptyTabList() {
        return new ArrayList<>();
    }

    public static String format(String msg, Object... args) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (args.length > 0) msg = String.format(msg, args);
        return msg;
    }

    public static void msg(CommandSender sender, String msg, Object... args) {
        sender.sendMessage(format(msg, args));
    }
}

