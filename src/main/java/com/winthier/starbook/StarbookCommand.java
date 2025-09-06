package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class StarbookCommand extends AbstractCommand<StarBookPlugin> {
    public StarbookCommand(final StarBookPlugin plugin) {
        super(plugin, "starbook");
    }

    @Override
    public void onEnable() {
        rootNode.addChild("make_leaves_persistent").denyTabCompletion()
            .playerCaller(this::makeLeavesPersistent);
    }

    private void makeLeavesPersistent(Player player) {
        for (Material mat : Tag.LEAVES.getValues()) {
            final String key = mat.getKey().getKey();
            final String cmd = "/replace " + key + "[persistent=false] " + key + "[persistent=true]";
            player.sendMessage(textOfChildren(text("Performing command: ", GRAY),
                                              text("/" + cmd, YELLOW)));
            player.performCommand(cmd);
        }
    }
}
