package com.winthier.starbook;

import com.cavetale.core.command.RemotePlayer;
import com.cavetale.core.connect.Connect;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
class TPPCommand extends AbstractCommand {
    private final JavaPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        RemotePlayer player = c.player != null
            ? RemotePlayer.wrap(c.player)
            : (c.sender instanceof RemotePlayer rp ? rp : null);
        if (player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 1) StarBookCommandException.usage(c);
        RemotePlayer target = Connect.get().getRemotePlayer(c.args[0]);
        if (target == null) throw new StarBookCommandException("Player not found: " + c.args[0]);
        if (target.isPlayer()) {
            player.bring(plugin, target.getPlayer().getLocation(), entity -> {
                    entity.sendMessage(text("Teleported to " + target.getName(), YELLOW));
                });
        } else if (player.isPlayer()) {
            Connect.get().dispatchRemoteCommand(player.getPlayer(),
                                                "tpp " + target.getName(),
                                                target.getOriginServerName());
        }
    }

    @Override
    protected List<String> onTabComplete(CommandContext c) {
        if (c.args.length != 1) return List.of();
        String arg = c.args[0].toLowerCase();
        List<String> result = new ArrayList<>();
        for (RemotePlayer player : Connect.get().getRemotePlayers()) {
            if (player.getName().toLowerCase().contains(arg)) {
                result.add(player.getName());
            }
        }
        return result;
    }
}
