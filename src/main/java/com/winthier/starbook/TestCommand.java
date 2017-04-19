package com.winthier.starbook;

import com.winthier.playercache.PlayerCache;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

class TestCommand extends AbstractCommand {
    private class Row implements Comparable<Row> {
        @Override public int compareTo(Row other) {
            return Integer.compare(other.score, score);
        }
        UUID uuid;
        String name;
        int score;
    }

    @Override
    public void onCommand(CommandContext c) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(Bukkit.getServer().getPluginManager().getPlugin("Easter").getDataFolder(), "scores.yml"));
            ArrayList<Row> rows = new ArrayList<>();
            for (String key: config.getKeys(false)) {
                Row row = new Row();
                row.uuid = UUID.fromString(key);
                row.name = PlayerCache.nameForUuid(row.uuid);
                row.score = config.getInt(key);
                rows.add(row);
            }
            Collections.sort(rows);
            PrintWriter out =  new PrintWriter(new File(Bukkit.getServer().getPluginManager().getPlugin("Easter").getDataFolder(), "highscore.txt"));
            for (Row row: rows) {
                out.printf("[color=#6cdd23]%d[/color] [b]%s[/b]%n", row.score, row.name);
            }
            out.close();
            c.sender.sendMessage("Highscore generated.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
