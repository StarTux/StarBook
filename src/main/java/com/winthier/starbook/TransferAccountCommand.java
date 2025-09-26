package com.winthier.starbook;

import com.cavetale.core.connect.ServerCategory;
import com.cavetale.core.playercache.PlayerCache;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

class TransferAccountCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (!(c.sender instanceof ConsoleCommandSender)) {
            throw new StarBookCommandException("Console required");
        }
        if (c.args.length != 2) return;
        PlayerCache from = PlayerCache.forArg(c.args[0]);
        if (from == null) throw new StarBookCommandException("Player not found: " + c.args[0]);
        PlayerCache to = PlayerCache.forArg(c.args[1]);
        if (to == null) throw new StarBookCommandException("Player not found: " + c.args[1]);
        if (from.equals(to)) throw new StarBookCommandException("Players are identical: " + from.getName());
        ServerCategory category = ServerCategory.current();
        String suffix = " " + from.uuid + " " + to.uuid;
        final List<String> commands;
        switch (category) {
        case SURVIVAL:
            commands = List.of(new String[] {
                    "money:moneyadmin transfer",
                    "home:homeadmin transfer",
                    "home:claimadmin transferall",
                    "massstorage:massstorageadmin player transfer",
                    "inventory:inventory store transfer",
                    "inventory:inventory stash transfer",
                    "wardrobe:wardrobeadmin transfer",
                    "title:titles transfer",
                    "photos:photoadmin transferall",
                    "shop:shopadmin transferall",
                    "tutor:tutoradmin transfer",
                    "fam:fam transfer",
                    "playerinfo:playerinfo migrate",
                    "perm:perm transfer",
                });
            break;
        case CREATIVE:
            commands = List.of(new String[] {
                    "creative:creativeadmin transferall",
                });
            break;
        default: throw new StarBookCommandException("Invalid server: " + category);
        }
        Bukkit.getScheduler().runTask(StarBookPlugin.instance, () -> {
                for (String part : commands) {
                    String command = part + suffix;
                    StarBookPlugin.instance.getLogger().info("Running command: " + command);
                    Bukkit.dispatchCommand(c.sender, command);
                }
            });
    }
}
