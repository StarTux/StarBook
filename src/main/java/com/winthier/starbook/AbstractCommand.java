package com.winthier.starbook;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

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
                sender.sendMessage(text(sbce.getMessage(), RED));
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
}
