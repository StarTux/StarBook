package com.winthier.starbook;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
final class WandCommand extends AbstractCommand implements Listener {
    private final StarBookPlugin plugin;
    private static final String WAND_KEY = "Wand";
    private static final String SELECTION_A_KEY = "SelectionA";
    private static final String SELECTION_B_KEY = "SelectionB";

    boolean hasWand(Player player) {
        for (MetadataValue v: player.getMetadata(WAND_KEY)) {
            if (v.getOwningPlugin() == plugin) {
                return v.value() == Boolean.TRUE;
            }
        }
        return false;
    }

    Block getSelection(Player player, int i) {
        Block result = null;
        String key = i == 0 ? SELECTION_A_KEY : SELECTION_B_KEY;
        for (MetadataValue v: player.getMetadata(key)) {
            if (v.getOwningPlugin() == plugin) {
                @SuppressWarnings("unchecked")
                List<Integer> list = (List<Integer>)v.value();
                result = player.getWorld().getBlockAt(list.get(0), list.get(1), list.get(2));
            }
        }
        return result;
    }

    void setSelection(Player player, int i, Block block) {
        String key = i == 0 ? SELECTION_A_KEY : SELECTION_B_KEY;
        player.setMetadata(key, new FixedMetadataValue(plugin, Arrays.asList(block.getX(), block.getY(), block.getZ())));
    }

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        switch (c.label) {
        case "wand":
            if (c.args.length == 0) {
                if (hasWand(c.player)) {
                    c.player.removeMetadata(WAND_KEY, plugin);
                    c.player.removeMetadata(SELECTION_A_KEY, plugin);
                    c.player.removeMetadata(SELECTION_B_KEY, plugin);
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
            makeSelection(c.player, 0, c.player.getLocation().getBlock());
            break;
        case "sel2":
            makeSelection(c.player, 1, c.player.getLocation().getBlock());
            break;
        case "sel":
            if (c.args.length == 0) {
                Block selA = getSelection(c.player, 0);
                Block selB = getSelection(c.player, 1);
                if (selA != null && selB != null) {
                    printSelectionInfo(c.player, selA, selB);
                } else {
                    msg(c.player, "&cNo selection made");
                }
            }
        default:
            StarBookCommandException.usage(c);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!hasWand(player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() != Material.WOODEN_SHOVEL) return;
        switch (event.getAction()) {
        case LEFT_CLICK_BLOCK:
            makeSelection(player, 0, event.getClickedBlock());
            event.setCancelled(true);
            break;
        case RIGHT_CLICK_BLOCK:
            makeSelection(player, 1, event.getClickedBlock());
            event.setCancelled(true);
            break;
        default: return;
        }
    }

    void makeSelection(Player player, int i, Block block) {
        Block selA = getSelection(player, 0);
        Block selB = getSelection(player, 1);
        if (i == 0 || selA == null) {
            selA = block;
            setSelection(player, 0, selA);
        }
        if (i == 1 || selB == null) {
            selB = block;
            setSelection(player, 1, selB);
        }
        printSelectionInfo(player, selA, selB);
    }

    void printSelectionInfo(Player player, Block selA, Block selB) {
        int dx = Math.abs(selB.getX() - selA.getX()) + 1;
        int dy = Math.abs(selB.getY() - selA.getY()) + 1;
        int dz = Math.abs(selB.getZ() - selA.getZ()) + 1;
        msg(player, "Selection: (%d,%d,%d)-(%d,%d,%d) (%dx%dx%d) %d blocks total",
            selA.getX(), selA.getY(), selA.getZ(),
            selB.getX(), selB.getY(), selB.getZ(),
            dx, dy, dz, dx * dy * dz);
    }
}
