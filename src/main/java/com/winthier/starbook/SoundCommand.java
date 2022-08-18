package com.winthier.starbook;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
final class SoundCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length < 1) StarBookCommandException.usage(c);
        Sound sound;
        try {
            sound = Sound.valueOf(c.args[0].toUpperCase());
        } catch (IllegalArgumentException iae) {
            sound = null;
        }
        if (sound == null) throw new StarBookCommandException("Sound not found: " + c.args[0]);
        float volume = 1.0f;
        if (c.args.length >= 2) {
            try {
                volume = Float.parseFloat(c.args[1]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad volume arg: " + c.args[1]);
            }
            if (volume <= 0.0f) throw new StarBookCommandException("Bad volume arg: " + volume);
        }
        float pitch = 1.0f;
        if (c.args.length >= 3) {
            try {
                pitch = Float.parseFloat(c.args[2]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad pitch arg: " + c.args[2]);
            }
            if (pitch <= 0.0f || pitch > 2.0f) throw new StarBookCommandException("Bad pitch arg: " + pitch);
        }
        Player target = c.player;
        boolean everyone = false;
        if (c.args.length >= 4) {
            if ("*".equals(c.args[3])) {
                everyone = true;
            } else {
                target = plugin.getServer().getPlayerExact(c.args[3]);
                if (target == null) throw new StarBookCommandException("Player not found: " + c.args[3]);
            }
        } else {
            if (c.player == null) StarBookCommandException.playerExpected();
        }
        if (everyone) {
            int count = 0;
            for (Player target2 : plugin.getServer().getOnlinePlayers()) {
                target2.playSound(target2.getLocation(), sound, volume, pitch);
                count += 1;
            }
            c.sender.sendMessage(join(noSeparators(),
                                      text("Playing "),
                                      text(sound.name(), GREEN),
                                      text(" for "),
                                      text(count, GREEN),
                                      text(" players:"),
                                      text(" volume"),
                                      text(":", GRAY),
                                      text(String.format("%.2f", volume), GREEN),
                                      text(" pitch"),
                                      text(":", GRAY),
                                      text(String.format("%.2f", pitch), GREEN)));
        } else {
            target.playSound(target.getLocation(), sound, volume, pitch);
            c.sender.sendMessage(join(noSeparators(),
                                      text("Playing "),
                                      text(sound.name(), GREEN),
                                      text(" for "),
                                      text(target.getName()),
                                      text(":"),
                                      text(" volume"),
                                      text(":", GRAY),
                                      text(String.format("%.2f", volume), GREEN),
                                      text(" pitch"),
                                      text(":", GRAY),
                                      text(String.format("%.2f", pitch), GREEN)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        if (c.args.length != 1) return null;
        List<String> result = new ArrayList<>();
        String cmd = c.args[0].toLowerCase();
        for (Sound sound: Sound.values()) {
            if (sound.name().toLowerCase().contains(cmd)) {
                result.add(sound.name());
            }
        }
        return result;
    }
}
