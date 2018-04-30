package com.winthier.starbook;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;

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
        } else if (cmd.equals("dump") && arg != null) {
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(arg);
            if (plugin == null) {
                c.sender.sendMessage("Plugin not found: " + arg);
            } else {
                count = 1;
                PluginDescriptionFile desc = plugin.getDescription();
                c.sender.sendMessage("Name: " + desc.getName());
                c.sender.sendMessage("Version: " + desc.getVersion());
                c.sender.sendMessage("Main: " + desc.getMain());
                c.sender.sendMessage("Authors: " + desc.getAuthors());
                c.sender.sendMessage("Description: " + desc.getDescription());
                c.sender.sendMessage("Website: " + desc.getWebsite());
                c.sender.sendMessage("Prefix: " + desc.getPrefix());
                c.sender.sendMessage("Load: " + desc.getLoad());
                c.sender.sendMessage("Depend: " + desc.getDepend());
                c.sender.sendMessage("SoftDepend: " + desc.getSoftDepend());
                c.sender.sendMessage("LoadBefore: " + desc.getLoadBefore());
                Map commands = desc.getCommands();
                c.sender.sendMessage("Commands: " + (commands == null ? "[]" : commands.keySet()));
                Collection<Permission> permissions = desc.getPermissions();
                c.sender.sendMessage("Permissions: " + (permissions == null ? "[]" : permissions.stream().map(a -> a.getName()).collect(Collectors.toList())));
                c.sender.sendMessage("PermissionDefault: " + desc.getPermissionDefault());
                c.sender.sendMessage("Awareness: " + desc.getAwareness());
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
        } else if (cmd.equals("listen") && arg != null) {
            HandlerList handlers;
            try {
                Class clazz = Class.forName(arg);
                handlers = (HandlerList)clazz.getMethod("getHandlerList").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
                c.sender.sendMessage("Event class not found: " + arg + ". See console.");
                return;
            }
            c.sender.sendMessage("Plugins not being listening to " + arg + ":");
            for (RegisteredListener listener: handlers.getRegisteredListeners()) {
                c.sender.sendMessage("- " + listener.getPlugin().getName() + " (" + listener.getPriority() + ")");
                count += 1;
            }
        } else if ((cmd.equals("permission") || cmd.equals("perm")) && arg != null) {
            Permission perm = Bukkit.getServer().getPluginManager().getPermission(arg);
            if (perm == null) {
                c.sender.sendMessage("Permission not found: " + arg);
            } else {
                count = 1;
                c.sender.sendMessage("Name: " + perm.getName());
                c.sender.sendMessage("Default: " + perm.getDefault());
                c.sender.sendMessage("Description: " + perm.getDescription());
                c.sender.sendMessage("Children:");
                for (Map.Entry<String, Boolean> child: perm.getChildren().entrySet()) {
                    c.sender.sendMessage("- " + child.getKey() + ": " + child.getValue());
                }
                Set ps = perm.getPermissibles();
                c.sender.sendMessage("Permissibles: " + (ps == null ? 0 : ps.size()));
            }
        } else {
            StarBookCommandException.usage(c);
        }
        c.sender.sendMessage("Total " + count);
    }
}
