package com.winthier.starbook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

final class PluginInfoCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length == 0) StarBookCommandException.usage(c);
        String cmd = c.args[0].toLowerCase();
        String arg;
        if (c.args.length > 1) {
            StringBuilder sb = new StringBuilder(c.args[1]);
            for (int i = 2; i < c.args.length; i += 1) sb.append(" ").append(c.args[i]);
            arg = sb.toString();
        } else {
            arg = null;
        }
        int count = 0;
        if (cmd.equals("list") && arg == null) {
            c.sender.sendMessage("Plugins loaded:");
            for (Plugin plugin: Bukkit.getServer().getPluginManager().getPlugins()) {
                c.sender.sendMessage("- " + plugin.getName());
                count += 1;
            }
        } else if (cmd.equals("database") && arg == null) {
            c.sender.sendMessage("Plugins with database enabled:");
            for (Plugin plugin: Bukkit.getServer().getPluginManager().getPlugins()) {
                if (plugin.getDescription().isDatabaseEnabled()) {
                    c.sender.sendMessage("- " + plugin.getName());
                    count += 1;
                }
            }
        } else if ((cmd.equals("depend") || cmd.equals("depends")) && arg != null) {
            c.sender.sendMessage("Plugins depending on " + arg + ":");
            for (Plugin plugin: Bukkit.getServer().getPluginManager().getPlugins()) {
                if (plugin.getDescription().getDepend().contains(arg)) {
                    c.sender.sendMessage("- " + plugin.getName());
                    count += 1;
                }
                if (plugin.getDescription().getSoftDepend().contains(arg)) {
                    c.sender.sendMessage("- " + plugin.getName() + " (soft)");
                    count += 1;
                }
            }
        } else if (cmd.equals("author") && arg != null) {
            c.sender.sendMessage("Plugins being (co)authored by " + arg + ":");
            for (Plugin plugin: Bukkit.getServer().getPluginManager().getPlugins()) {
                if (plugin.getDescription().getAuthors().contains(arg)) {
                    c.sender.sendMessage("- " + plugin.getName());
                    count += 1;
                }
            }
        } else if (cmd.equals("nauthor") && arg != null) {
            c.sender.sendMessage("Plugins not being (co)authored by " + arg + ":");
            for (Plugin plugin: Bukkit.getServer().getPluginManager().getPlugins()) {
                if (!plugin.getDescription().getAuthors().contains(arg)) {
                    c.sender.sendMessage("- " + plugin.getName());
                    count += 1;
                }
            }
        } else {
            StarBookCommandException.usage(c);
        }
        c.sender.sendMessage("Total " + count);
    }
}
