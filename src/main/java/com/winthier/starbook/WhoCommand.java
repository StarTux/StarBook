package com.winthier.starbook;

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

    void showOnlineList(CommandSender sender) {
        StringBuilder sb = new StringBuilder("" + plugin.getServer().getOnlinePlayers().size() + " Online Players:");
        for (Player player: plugin.getServer().getOnlinePlayers()) sb.append(" ").append(player.getName());
        sender.sendMessage(sb.toString());
    }
}
