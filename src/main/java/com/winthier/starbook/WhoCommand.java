package com.winthier.starbook;

import com.winthier.connect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
final class WhoCommand extends AbstractCommand {
    private final StarBookPlugin plugin;
    private Permission permission;

    @Override
    void onCommand(CommandContext context) {
        if (context.args.length != 0) StarBookCommandException.usage(context);
        if (context.player == null) StarBookCommandException.playerExpected();
        showOnlineList(context.player);
    }

    private Permission getPermission() {
        if (permission == null) {
            RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        }
        return permission;
    }

    private boolean isStaff(UUID uuid) {
        Player bukkitPlayer = plugin.getServer().getPlayer(uuid);
        if (bukkitPlayer != null) {
            return bukkitPlayer.hasPermission("onlinelist.staff");
        } else {
            Permission permission = getPermission();
            if (permission == null) return false;
            OfflinePlayer off = plugin.getServer().getOfflinePlayer(uuid);
            return permission.playerHas((String)null, off, "onlinelist.staff");
        }
    }

    void showOnlineList(Player player) {
        Map<String, List<OnlinePlayer>> serverList = new HashMap<>();
        int totalCount = 0;
        for (ServerConnection con: new ArrayList<>(Connect.getInstance().getServer().getConnections())) {
            List<OnlinePlayer> conList = new ArrayList<>(con.getOnlinePlayers());
            String displayName = con.getName();
            Client client = Connect.getInstance().getClient(displayName);
            if (client != null) displayName = client.getDisplayName();
            List<OnlinePlayer> playerList = serverList.get(displayName);
            if (playerList == null) {
                playerList = new ArrayList<>();
                serverList.put(displayName, playerList);
            }
            playerList.addAll(conList);
            totalCount += conList.size();
        }
        String[] serverNames = serverList.keySet().toArray(new String[0]);
        Arrays.sort(serverNames, (a, b) -> Integer.compare(serverList.get(b).size(), serverList.get(a).size()));
        Msg.msg(player, "&9%s Player List (&f%d&9)", Connect.getInstance().getServer().getDisplayName(), totalCount);
        for (String serverName: serverNames) {
            OnlinePlayer[] playerArray = serverList.get(serverName).toArray(new OnlinePlayer[0]);
            String perm = "starbook.server." + serverName.toLowerCase();
            if (playerArray.length == 0 && !player.hasPermission(perm)) continue;
            Arrays.sort(playerArray, new Comparator<OnlinePlayer>() {
                    @Override public int compare(OnlinePlayer a, OnlinePlayer b) { return String.CASE_INSENSITIVE_ORDER.compare(a.getName(), b.getName()); }
                    @Override public boolean equals(Object o) { return this == o; }
                });
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
    }
}
