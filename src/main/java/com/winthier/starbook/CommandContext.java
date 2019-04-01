package com.winthier.starbook;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

final class CommandContext {
    final CommandSender sender;
    final Command command;
    final Player player;
    final String label;
    final String[] args;

    CommandContext(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.player = sender instanceof Player ? (Player)sender : null;
        this.command = command;
        this.label = label;
        this.args = args;
    }
}
