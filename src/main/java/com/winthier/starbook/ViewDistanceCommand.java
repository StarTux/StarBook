package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class ViewDistanceCommand extends AbstractCommand<StarBookPlugin> {
    public static final int MIN = 2;
    public static final int MAX = 32;

    public ViewDistanceCommand(final StarBookPlugin plugin) {
        super(plugin, "viewdistance");
    }

    @Override
    protected void onEnable() {
        rootNode.arguments("<value>")
            .description("Set view distance")
            .completers(CommandArgCompleter.integer(i -> i >= MIN && i <= MAX))
            .playerCaller(this::viewDistance);
    }

    private boolean viewDistance(Player player, String[] args) {
        if (args.length == 0) {
            final int viewDistance = player.getViewDistance();
            player.sendMessage(text("Your view distance is " + viewDistance, AQUA));
            return true;
        } else if (args.length == 1) {
            final int viewDistance = CommandArgCompleter.requireInt(args[0], i -> i >= MIN && i <= MAX);
            player.setViewDistance(viewDistance);
            player.sendMessage(text("Your view distance was set to " + viewDistance, YELLOW));
            return true;
        } else {
            return false;
        }
    }
}
