package com.winthier.starbook;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.winthier.connect.Connect;
import com.winthier.connect.OnlinePlayer;
import com.winthier.generic_events.GenericEvents;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class WhoCommand extends AbstractCommand {
    private final StarBookPlugin plugin;

    @Override
    void onCommand(CommandContext context) {
        if (context.args.length != 0) StarBookCommandException.usage(context);
        showOnlineList(context.sender);
    }

    private boolean isStaff(UUID uuid) {
        Player nukkitPlayer = plugin.getServer().getPlayer(uuid).orElse(null);
        if (nukkitPlayer != null) return nukkitPlayer.hasPermission("onlinelist.staff");
        return GenericEvents.playerHasPermission(uuid, "onlinelist.staff");
    }

    void showOnlineList(CommandSender sender) {
        if (plugin.getServer().getPluginManager().getPlugin("Connect") == null) {
            StringBuilder sb = new StringBuilder("" + plugin.getServer().getOnlinePlayers().size() + " Online Players:");
            for (Player player: plugin.getServer().getOnlinePlayers().values()) sb.append(" ").append(player.getName());
            sender.sendMessage(sb.toString());
            return;
        }
        Map<String, List<OnlinePlayer>> serverList = Connect.getInstance().listPlayers();
        int totalCount = 0;
        String[] serverNames = serverList.keySet().toArray(new String[0]);
        Arrays.sort(serverNames, (a, b) -> Integer.compare(serverList.get(b).size(), serverList.get(a).size()));
        for (String serverName: serverNames) {
            OnlinePlayer[] playerArray = serverList.get(serverName).toArray(new OnlinePlayer[0]);
            StringBuilder sb = new StringBuilder(TextFormat.AQUA + serverName + TextFormat.WHITE + "(" + TextFormat.DARK_AQUA + playerArray.length + TextFormat.WHITE + ")" + TextFormat.GRAY);
            for (OnlinePlayer p: playerArray) sb.append(" ").append(p.getName());
            sender.sendMessage(sb.toString());
        }
    }
}
