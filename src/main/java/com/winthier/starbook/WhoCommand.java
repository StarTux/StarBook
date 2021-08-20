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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
        Map<String, List<OnlinePlayer>> serverList = Connect.getInstance().listPlayers();
        List<String> serverNames = new ArrayList<>(serverList.keySet());
        Collections.sort(serverNames, (a, b) -> Integer.compare(serverList.get(b).size(), serverList.get(a).size()));
        int totalCount = 0;
        List<Component> lines = new ArrayList<>();
        for (String serverName : serverNames) {
            List<OnlinePlayer> playerList = new ArrayList<>(serverList.get(serverName));
            if (sender instanceof Player) {
                playerList.removeIf(it -> {
                        Player online = Bukkit.getPlayer(it.getUuid());
                        return online != null && online.getGameMode() == GameMode.SPECTATOR && online.hasPermission("chat.invisible");
                    });
            }
            serverList.put(serverName, playerList);
            if (playerList.size() == 0) continue;
            Collections.sort(playerList, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.getName(), b.getName()));
            totalCount += playerList.size();
            Component nameComponent = TextComponent.ofChildren(new Component[] {
                    Component.text(serverName, NamedTextColor.GRAY),
                    Component.text(" (", NamedTextColor.GRAY),
                    Component.text("" + playerList.size(), NamedTextColor.WHITE),
                    Component.text(")", NamedTextColor.GRAY),
                })
                .clickEvent(ClickEvent.suggestCommand("/" + serverName.toLowerCase()))
                .hoverEvent(HoverEvent.showText(Component.text("/" + serverName.toLowerCase(), NamedTextColor.GREEN)));
            List<Component> playerNames = new ArrayList<>();
            for (OnlinePlayer online : playerList) {
                boolean staff = isStaff(online.getUuid());
                playerNames.add(Component.text(online.getName(), staff ? NamedTextColor.GOLD : NamedTextColor.WHITE)
                                .clickEvent(ClickEvent.suggestCommand("/msg " + online.getName()))
                                .hoverEvent(HoverEvent.showText(TextComponent.ofChildren(new Component[] {
                                                Component.text(online.getName(), (staff ? NamedTextColor.GOLD : NamedTextColor.WHITE)),
                                                Component.newline(),
                                                Component.text((staff ? "Staff member" : "Player"), NamedTextColor.DARK_GRAY),
                                                Component.newline(),
                                                Component.text("/msg " + online.getName(), NamedTextColor.GRAY),
                                            }))));
            }
            lines.add(TextComponent.ofChildren(new Component[] {
                        nameComponent,
                        Component.space(),
                        Component.join(Component.text(", ", NamedTextColor.GRAY), playerNames),
                    }));
        }
        sender.sendMessage(Component.join(Component.newline(), lines));
    }
}
