package com.winthier.starbook;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
class PoofCommand extends AbstractCommand {
    private final StarBookPlugin plugin;
    private static final String METADATA_KEY = "StarBook.Poof.GameMode";

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.player.getGameMode() == GameMode.SPECTATOR) {
            restoreGameMode(c.player);
            if (c.player.getGameMode() == GameMode.SPECTATOR) c.player.setGameMode(GameMode.SURVIVAL);
            c.player.sendMessage(text("You reappeared", YELLOW));
        } else {
            storeGameMode(c.player);
            c.player.setGameMode(GameMode.SPECTATOR);
            c.player.sendMessage(text("You vanished", GREEN));
        }
    }

    void storeGameMode(Player player) {
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, player.getGameMode()));
    }

    void restoreGameMode(Player player) {
        for (MetadataValue meta: player.getMetadata(METADATA_KEY)) {
            if (meta.getOwningPlugin() != plugin) continue;
            GameMode gm = (GameMode) meta.value();
            player.setGameMode(gm);
        }
        player.removeMetadata(METADATA_KEY, plugin);
    }
}
