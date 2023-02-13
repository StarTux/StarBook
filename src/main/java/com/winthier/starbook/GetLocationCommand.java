package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class GetLocationCommand extends AbstractCommand<JavaPlugin> {
    protected GetLocationCommand(final JavaPlugin plugin) {
        super(plugin, "getlocation");
    }

    @Override
    protected void onEnable() {
        rootNode.description("Get current location").denyTabCompletion()
            .playerCaller(this::getLocation);
    }

    private void getLocation(Player player) {
        Location location = player.getLocation();
        String world = location.getWorld().getName();
        String coords = String.format("%.02f %.02f %.02f %.02f %.02f",
                                      location.getX(),
                                      location.getY(),
                                      location.getZ(),
                                      location.getYaw(),
                                      location.getPitch());
        player.sendMessage(textOfChildren(text(world, GRAY).insertion(world),
                                          text(" "),
                                          text(coords, GOLD).insertion(coords))
                           .clickEvent(suggestCommand("/tp " + player.getName() + " " + coords)));
    }
}
