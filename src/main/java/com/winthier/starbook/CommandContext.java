package com.winthier.starbook;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

class CommandContext {
    CommandSender sender;
    Command command;
    Player player;
    String label;
    String[] args;
    List<String> tabCompletions = null;

    CommandContext(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.player = sender instanceof Player ? (Player)sender : null;
        this.command = command;
        this.label = label;
        this.args = args;
    }
}
