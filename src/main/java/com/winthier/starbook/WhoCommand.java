package com.winthier.starbook;

import com.winthier.connect.Connect;
import com.winthier.connect.OnlinePlayer;
import com.winthier.perm.Perm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
        return Perm.has(uuid, "onlinelist.staff");
    }

    void showOnlineList(CommandSender sender) {
        if (plugin.getServer().getPluginManager().getPlugin("Connect") == null) {
            StringBuilder sb = new StringBuilder("" + plugin.getServer().getOnlinePlayers().size() + " Online Players:");
            for (Player player : plugin.getServer().getOnlinePlayers()) sb.append(" ").append(player.getName());
            sender.sendMessage(sb.toString());
            return;
        }
        Map<String, List<OnlinePlayer>> serverList = Connect.getInstance().listPlayers();
        List<String> serverNames = new ArrayList<>(serverList.keySet());
        Collections.sort(serverNames, (a, b) -> Integer.compare(serverList.get(b).size(), serverList.get(a).size()));
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int totalCount = 0;
            List<List<Object>> msgs = new ArrayList<>();
            for (String serverName : serverNames) {
                List<OnlinePlayer> playerList = new ArrayList<>(serverList.get(serverName));
                playerList.removeIf(it -> {
                        Player online = Bukkit.getPlayer(it.getUuid());
                        return online != null && ((online.getGameMode() == GameMode.SPECTATOR && online.hasPermission("chat.invisible")) || !player.canSee(online));
                    });
                serverList.put(serverName, playerList);
                String perm = "starbook.server." + serverName.toLowerCase();
                if (playerList.size() == 0) continue;
                Collections.sort(playerList, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.getName(), b.getName()));
                List<Object> json = new ArrayList<>();
                msgs.add(json);
                totalCount += playerList.size();
                json.add(" ");
                if (player.isPermissionSet(perm) && player.hasPermission(perm)) {
                    json.add(Msg.button(ChatColor.BLUE,
                                        Msg.format("&7%s(&f%d&7)", serverName, playerList.size()),
                                        serverName + " Server\n&7&o/" + serverName.toLowerCase(),
                                        "/" + serverName.toLowerCase() + " "));
                } else {
                    json.add(Msg.button(ChatColor.BLUE,
                                        Msg.format("&7%s(&f%d&7)", serverName, playerList.size()),
                                        serverName + " Server",
                                        null));
                }
                for (OnlinePlayer online : playerList) {
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
            }
            Msg.msg(player, "&9%s Player List (&f%d&9)", Connect.getInstance().getServerName(), totalCount);
            for (List<Object> json : msgs) Msg.raw(player, json);
        } else {
            for (String serverName : serverNames) {
                OnlinePlayer[] playerArray = serverList.get(serverName).toArray(new OnlinePlayer[0]);
                StringBuilder sb = new StringBuilder(serverName).append("(").append(playerArray.length).append(")");
                for (OnlinePlayer p : playerArray) sb.append(" ").append(p.getName());
                sender.sendMessage(sb.toString());
            }
        }
    }
}
