package com.winthier.starbook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

final class ListDependsCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length != 1) StarBookCommandException.usage(c);
        String cmd = c.args[0];
        int count = 0;
        if (cmd.equals("database")) {
            c.sender.sendMessage("Plugins with database enabled:");
            for (Plugin plugin: Bukkit.getServer().getPluginManager().getPlugins()) {
                if (plugin.getDescription().isDatabaseEnabled()) {
                    c.sender.sendMessage("- " + plugin.getName());
                    count += 1;
                }
            }
        } else {
            c.sender.sendMessage("Plugins depending on " + cmd + ":");
            for (Plugin plugin: Bukkit.getServer().getPluginManager().getPlugins()) {
                if (plugin.getDescription().getDepend().contains(cmd)) {
                    c.sender.sendMessage("- " + plugin.getName());
                    count += 1;
                }
                if (plugin.getDescription().getSoftDepend().contains(cmd)) {
                    c.sender.sendMessage("- " + plugin.getName() + " (soft)");
                    count += 1;
                }
            }
        }
        c.sender.sendMessage("Total " + count);
    }
}
