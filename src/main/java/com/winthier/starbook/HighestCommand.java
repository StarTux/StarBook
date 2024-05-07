package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import static com.cavetale.core.util.CamelCase.toCamelCase;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class HighestCommand extends AbstractCommand<StarBookPlugin> {
    public HighestCommand(final StarBookPlugin plugin) {
        super(plugin, "highest");
    }

    @Override
    protected void onEnable() {
        rootNode.denyTabCompletion()
            .description("Print highest block levels")
            .playerCaller(this::highest);
    }

    private void highest(Player player) {
        final Location location = player.getLocation();
        final int x = location.getBlockX();
        final int z = location.getBlockZ();
        final World world = location.getWorld();
        player.sendMessage(textOfChildren(text("Highest blocks at ", AQUA),
                                          text(world.getName(), WHITE),
                                          space(),
                                          text(x, WHITE),
                                          space(),
                                          text(z, WHITE))
                           .hoverEvent(showText(text(world.getName() + " " + x + " " + z, GRAY)))
                           .insertion(world.getName() + " " + x + " " + z));
        for (HeightMap heightMap : HeightMap.values()) {
            final int y = location.getWorld().getHighestBlockYAt(x, z, heightMap);
            player.sendMessage(textOfChildren(text(y, WHITE),
                                              text(" " + toCamelCase(" ", heightMap), GRAY)));
        }
    }
}
