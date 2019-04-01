package com.winthier.starbook;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandContext context = new CommandContext(sender, command, label, args);
        try {
            onCommand(context);
        } catch (StarBookCommandException sbce) {
            if (sbce.isUsage()) {
                return false;
            } else {
                if (sender instanceof Player) {
                    msg(sender, "&c%s", sbce.getMessage());
                } else {
                    StarBookPlugin.getInstance().getLogger().info(String.format("&c%s", sbce.getMessage()));
                }
            }
        }
        return true;
    }

    abstract void onCommand(CommandContext context);

    protected final List<String> emptyTabList() {
        return new ArrayList<>();
    }

    static final String format(String msg, Object... args) {
        msg = TextFormat.colorize(msg);
        if (args.length > 0) msg = String.format(msg, args);
        return msg;
    }

    static final void msg(CommandSender sender, String msg, Object... args) {
        sender.sendMessage(format(msg, args));
    }
}

