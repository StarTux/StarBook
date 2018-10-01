package com.winthier.starbook;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

@RequiredArgsConstructor
final class WandCommand extends AbstractCommand implements Listener {
    private final StarBookPlugin plugin;
    private static final String WAND_KEY = "Wand";
    private static final String SELECTION_AX = "SelectionAX";
    private static final String SELECTION_AY = "SelectionAY";
    private static final String SELECTION_AZ = "SelectionAZ";
    private static final String SELECTION_BX = "SelectionBX";
    private static final String SELECTION_BY = "SelectionBY";
    private static final String SELECTION_BZ = "SelectionBZ";

    // --- Meta utility

    MetadataValue getMeta(Metadatable metadatable, String key) {
        for (MetadataValue v: metadatable.getMetadata(key)) {
            if (v.getOwningPlugin() == plugin) return v;
        }
        return null;
    }

    boolean getMetaBoolean(Metadatable metadatable, String key) {
        MetadataValue v = getMeta(metadatable, key);
        if (v == null) return false;
        return v.asBoolean();
    }

    int getMetaInt(Metadatable metadatable, String key) {
        MetadataValue v = getMeta(metadatable, key);
        if (v == null) throw new NullPointerException("Not set: " + key);
        return v.asInt();
    }

    boolean hasWand(Player player) {
        return getMetaBoolean(player, WAND_KEY);
    }

    // --- Selection utility

    @Value static class Selection {
        private final int ax, ay, az, bx, by, bz;
    }

    public Selection getSelection(Player player) {
        try {
            return new Selection(getMetaInt(player, SELECTION_AX),
                                 getMetaInt(player, SELECTION_AY),
                                 getMetaInt(player, SELECTION_AZ),
                                 getMetaInt(player, SELECTION_BX),
                                 getMetaInt(player, SELECTION_BY),
                                 getMetaInt(player, SELECTION_BZ));
        } catch (NullPointerException npe) {
            return null;
        }
    }

    void setSelection(Player player, Selection selection) {
        player.setMetadata(SELECTION_AX, new FixedMetadataValue(plugin, selection.ax));
        player.setMetadata(SELECTION_AY, new FixedMetadataValue(plugin, selection.ay));
        player.setMetadata(SELECTION_AZ, new FixedMetadataValue(plugin, selection.az));
        player.setMetadata(SELECTION_BX, new FixedMetadataValue(plugin, selection.bx));
        player.setMetadata(SELECTION_BY, new FixedMetadataValue(plugin, selection.by));
        player.setMetadata(SELECTION_BZ, new FixedMetadataValue(plugin, selection.bz));
    }

    Selection makeSelection(Player player, int i, Block block) {
        Selection sel = getSelection(player);
        if (sel == null) {
            return new Selection(block.getX(), block.getY(), block.getZ(),
                                 block.getX(), block.getY(), block.getZ());
        }
        switch (i) {
        case 0:
            return new Selection(block.getX(), block.getY(), block.getZ(),
                                 sel.bx, sel.by, sel.bz);
        case 1:
            return new Selection(sel.ax, sel.ay, sel.az,
                                 block.getX(), block.getY(), block.getZ());
        default:
            throw new IllegalArgumentException("i must be 0 or 1");
        }
    }

    void printSelectionInfo(Player player, Selection sel) {
        int dx = Math.abs(sel.bx - sel.ax) + 1;
        int dy = Math.abs(sel.by - sel.ay) + 1;
        int dz = Math.abs(sel.bz - sel.az) + 1;
        msg(player, "Selection: (%d,%d,%d)-(%d,%d,%d) (%dx%dx%d) %d blocks",
            sel.ax, sel.ay, sel.ax,
            sel.bx, sel.by, sel.bz,
            dx, dy, dz, dx * dy * dz);
    }

    // --- Command interface

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        switch (c.label) {
        case "wand":
            if (c.args.length == 0) {
                if (hasWand(c.player)) {
                    c.player.removeMetadata(WAND_KEY, plugin);
                    c.player.removeMetadata(SELECTION_AX, plugin);
                    c.player.removeMetadata(SELECTION_AY, plugin);
                    c.player.removeMetadata(SELECTION_AZ, plugin);
                    c.player.removeMetadata(SELECTION_BX, plugin);
                    c.player.removeMetadata(SELECTION_BY, plugin);
                    c.player.removeMetadata(SELECTION_BZ, plugin);
                    c.player.getInventory().removeItem(new ItemStack(Material.WOODEN_SHOVEL));
                    msg(c.player, "&eSelection wand disabled, selection cleared");
                } else {
                    c.player.setMetadata(WAND_KEY, new FixedMetadataValue(plugin, true));
                    if (!c.player.getInventory().contains(Material.WOODEN_SHOVEL)) {
                        c.player.getInventory().addItem(new ItemStack(Material.WOODEN_SHOVEL));
                    }
                    msg(c.player, "&eSelection wand (wooden shovel) enabled");
                }
            }
            break;
        case "sel1":
            Selection sel = makeSelection(c.player, 0, c.player.getLocation().getBlock());
            setSelection(c.player, sel);
            printSelectionInfo(c.player, sel);
            break;
        case "sel2":
            sel = makeSelection(c.player, 1, c.player.getLocation().getBlock());
            setSelection(c.player, sel);
            printSelectionInfo(c.player, sel);
            break;
        case "sel":
            if (c.args.length == 0) {
                sel = getSelection(c.player);
                if (sel != null) {
                    printSelectionInfo(c.player, sel);
                } else {
                    msg(c.player, "&cNo selection made");
                }
            }
        default:
            StarBookCommandException.usage(c);
        }
    }

    // --- Event handling

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!hasWand(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() != Material.WOODEN_SHOVEL) return;
        switch (event.getAction()) {
        case LEFT_CLICK_BLOCK:
            Selection sel = makeSelection(player, 0, event.getClickedBlock());
            setSelection(player, sel);
            printSelectionInfo(player, sel);
            event.setCancelled(true);
            break;
        case RIGHT_CLICK_BLOCK:
            sel = makeSelection(player, 1, event.getClickedBlock());
            setSelection(player, sel);
            printSelectionInfo(player, sel);
            event.setCancelled(true);
            break;
        default: return;
        }
    }
}
