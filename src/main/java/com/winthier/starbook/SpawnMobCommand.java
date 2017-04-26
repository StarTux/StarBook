package com.winthier.starbook;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

@RequiredArgsConstructor
final class SpawnMobCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    Map<String, String> getMobs() {
        Map<String, String> mobs = new HashMap<>();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "spawnmob.yml"));
        for (String key: config.getKeys(false)) {
            mobs.put(key, config.getString(key));
        }
        return mobs;
    }

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        String mobName = null;
        Integer amount = null;
        for (String arg: c.args) {
            if (mobName == null) {
                mobName = getMobs().get(arg);
                if (mobName == null) mobName = arg;
            } else if (amount == null) {
                try {
                    amount = Integer.parseInt(arg);
                } catch (NumberFormatException nfe) {
                    throw new StarBookCommandException(c);
                }
                if (amount < 1) throw new StarBookCommandException(c);
            } else {
                throw new StarBookCommandException(c);
            }
        }
        if (mobName == null) throw new StarBookCommandException(c);
        if (amount == null) amount = 1;
        String[] tokens = mobName.split(" ", 2);
        String command;
        if (tokens.length == 2) {
            command = "summon " + tokens[0] + " ~0 ~0 ~0 " + tokens[1];
        } else if (tokens.length == 1) {
            command = "summon " + tokens[0];
        } else {
            throw new StarBookCommandException(c);
        }
        int count = 0;
        for (int i = 0; i < amount; ++i) {
            boolean result = c.player.performCommand(command);
            if (!result) break;
            count += 1;
        }
        msg(c.player, "&eSpawned %d times!", count);
    }
}
