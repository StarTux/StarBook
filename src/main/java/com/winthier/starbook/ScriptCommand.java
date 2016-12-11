package com.winthier.starbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

@RequiredArgsConstructor
class ScriptCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        String arg = c.args.length >= 1 ? c.args[0].toLowerCase() : null;
        if (arg.equals("test")) {
            for (String line: compileCommands()) {
                c.sender.sendMessage(line);
            }
        } else if (arg.equals("run")) {
            for (String line: compileCommands()) {
                plugin.getServer().dispatchCommand(c.sender, line);
            }
        }
    }

    List<String> compileCommands() {
        List<String> list = new ArrayList<>();
        File file = new File(plugin.getDataFolder(), "script.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String line: config.getStringList("commands")) {
            for (String name: config.getStringList("players")) {
                list.add(line.replace("%player%", name));
            }
        }
        return list;
    }
}
