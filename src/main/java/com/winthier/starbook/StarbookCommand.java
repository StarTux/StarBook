package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandWarn;
import com.cavetale.core.struct.Cuboid;
import com.cavetale.core.struct.Vec3i;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class StarbookCommand extends AbstractCommand<StarBookPlugin> {
    public StarbookCommand(final StarBookPlugin plugin) {
        super(plugin, "starbook");
    }

    @Override
    public void onEnable() {
        rootNode.addChild("make_leaves_persistent").denyTabCompletion()
            .playerCaller(this::makeLeavesPersistent);
        rootNode.addChild("find_block").arguments("<material>")
            .completers(CommandArgCompleter.enumLowerList(Material.class))
            .playerCaller(this::findBlock);
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

    private boolean findBlock(Player player, String[] args) {
        if (args.length != 1) return false;
        final Material material = CommandArgCompleter.requireEnum(Material.class, args[0]);
        int count = 0;
        for (Vec3i vec : Cuboid.requireSelectionOf(player)) {
            if (vec.toBlock(player.getWorld()).getType() != material) continue;
            count += 1;
            final String cmd = "/tp " + vec.x + " " + vec.y + " " + vec.z;
            player.sendMessage(textOfChildren(text("#" + count + " ", GRAY),
                                              text(material.getKey().getKey(), YELLOW),
                                              text(" at ", WHITE),
                                              text("" + vec, YELLOW))
                               .hoverEvent(showText(text(cmd, GRAY)))
                               .clickEvent(runCommand(cmd)));
        }
        if (count == 0) throw new CommandWarn("Nothing found");
        return true;
    }
}
