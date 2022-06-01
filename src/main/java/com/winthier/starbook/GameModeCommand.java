package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class GameModeCommand extends AbstractCommand<StarBookPlugin> {
    private final GameMode gameMode;

    protected GameModeCommand(final StarBookPlugin plugin, final String label, final GameMode gameMode) {
        super(plugin, label);
        this.gameMode = gameMode;
    }

    @Override
    protected void onEnable() {
        rootNode.denyTabCompletion()
            .description("Set game mode to " + gameMode.name().toLowerCase())
            .playerCaller(this::call);
    }

    private void call(Player player) {
        player.setGameMode(gameMode);
        player.sendMessage("Set own game mode to " + gameMode.name().toLowerCase());
    }
}
