package com.winthier.starbook;

import com.winthier.connect.Connect;
import com.winthier.connect.OnlinePlayer;
import com.winthier.generic_events.GenericEvents;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
final class WhoCommand extends AbstractCommand {
    private final StarBookPlugin plugin;

    @Override
    void onCommand(CommandContext context) {
        if (context.args.length != 0) StarBookCommandException.usage(context);
        showOnlineList(context.sender);
    }

    private boolean isStaff(UUID uuid) {
        Player bukkitPlayer = plugin.getServer().getPlayer(uuid);
        if (bukkitPlayer != null) return bukkitPlayer.hasPermission("onlinelist.staff");
        return GenericEvents.playerHasPermission(uuid, "onlinelist.staff");
    }

    void showOnlineList(CommandSender sender) {
        if (plugin.getServer().getPluginManager().getPlugin("Connect") == null) {
            StringBuilder sb = new StringBuilder("" + plugin.getServer().getOnlinePlayers().size() + " Online Players:");
            for (Player player: plugin.getServer().getOnlinePlayers()) sb.append(" ").append(player.getName());
            sender.sendMessage(sb.toString());
            return;
        }
        Map<String, List<OnlinePlayer>> serverList = Connect.getInstance().listPlayers();
        int totalCount = 0;
        String[] serverNames = serverList.keySet().toArray(new String[0]);
        Arrays.sort(serverNames, (a, b) -> Integer.compare(serverList.get(b).size(), serverList.get(a).size()));
        if (sender instanceof Player) {
            Player player = (Player)sender;
            Msg.msg(player, "&9%s Player List (&f%d&9)", Connect.getInstance().getServerName(), totalCount);
            for (String serverName: serverNames) {
                OnlinePlayer[] playerArray = serverList.get(serverName).toArray(new OnlinePlayer[0]);
                String perm = "starbook.server." + serverName.toLowerCase();
                if (playerArray.length == 0 && !player.hasPermission(perm)) continue;
                Arrays.sort(playerArray, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.getName(), b.getName()));
                List<Object> json = new ArrayList<>();
                json.add(" ");
                if (player.isPermissionSet(perm) && player.hasPermission(perm)) {
                    json.add(Msg.button(ChatColor.BLUE,
                                        Msg.format("&7%s(&f%d&7)", serverName, playerArray.length),
                                        serverName + " Server\n&7&o/" + serverName.toLowerCase(),
                                        "/" + serverName.toLowerCase() + " "));
                } else {
                    json.add(Msg.button(ChatColor.BLUE,
                                        Msg.format("&7%s(&f%d&7)", serverName, playerArray.length),
                                        serverName + " Server",
                                        null));
                }
                for (OnlinePlayer online: playerArray) {
                    json.add(" ");
                    if (isStaff(online.getUuid())) {
                        json.add(Msg.button(ChatColor.GOLD,
                                            online.getName(),
                                            "&6" + online.getName() + " &r&oStaff\n&7&o/msg " + online.getName(),
                                            "/msg " + online.getName() + " "));
                    } else {
                        json.add(Msg.button(ChatColor.WHITE,
                                            online.getName(),
                                            online.getName() + "\n&7&o/msg " + online.getName(),
                                            "/msg " + online.getName() + " "));
                    }
                }
                Msg.raw(player, json);
            }
        } else {
            for (String serverName: serverNames) {
                OnlinePlayer[] playerArray = serverList.get(serverName).toArray(new OnlinePlayer[0]);
                StringBuilder sb = new StringBuilder(serverName).append("(").append(playerArray.length).append(")");
                for (OnlinePlayer p: playerArray) sb.append(" ").append(p.getName());
                sender.sendMessage(sb.toString());
            }
        }
    }
}
