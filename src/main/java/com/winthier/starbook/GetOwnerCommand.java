package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandWarn;
import com.cavetale.core.playercache.PlayerCache;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class GetOwnerCommand extends AbstractCommand<StarBookPlugin> {
    public GetOwnerCommand(final StarBookPlugin plugin) {
        super(plugin, "getowner");
    }

    @Override
    protected void onEnable() {
        rootNode.denyTabCompletion()
            .description("Get the owner of an entity")
            .senderCaller(this::getOwner);
    }

    private boolean getOwner(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        final UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Invalid UUID: " + args[0]);
        }
        final Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) {
            throw new CommandWarn("Entity not found: " + uuid);
        }
        if (!(entity instanceof Tameable tameable)) {
            throw new CommandWarn("Entity not tameable: " + entity.getType());
        }
        if (!(tameable.isTamed())) {
            throw new CommandWarn("Tameable is not tamed: " + tameable.getType());
        }
        final UUID owner = tameable.getOwnerUniqueId();
        if (owner == null) {
            throw new CommandWarn("Tameable is not owned: " + tameable.getType());
        }
        final String name = PlayerCache.nameForUuid(owner);
        sender.sendMessage(text(tameable.getType() + " is owned by " + name + " (" + owner + ")", YELLOW)
                           .hoverEvent(showText(text(name + " " + owner, GRAY)))
                           .insertion(name + " " + owner));
        return true;
    }
}
