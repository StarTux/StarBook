package com.winthier.starbook;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

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
        if (sound == null) throw new StarBookCommandException("Sound not found: %s", c.args[0]);
        float volume = 1.0f;
        if (c.args.length >= 2) {
            try {
                volume = Float.parseFloat(c.args[1]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad volume arg: %s", c.args[1]);
            }
            if (volume <= 0.0f) throw new StarBookCommandException("Bad volume arg: %.2f", volume);
        }
        float pitch = 1.0f;
        if (c.args.length >= 3) {
            try {
                pitch = Float.parseFloat(c.args[2]);
            } catch (IllegalArgumentException iae) {
                throw new StarBookCommandException("Bad pitch arg: %s", c.args[2]);
            }
            if (pitch <= 0.0f || pitch > 2.0f) throw new StarBookCommandException("Bad pitch arg: %.2f", pitch);
        }
        Player target = c.player;
        boolean everyone = false;
        if (c.args.length >= 4) {
            if ("*".equals(c.args[3])) {
                everyone = true;
            } else {
                target = plugin.getServer().getPlayerExact(c.args[3]);
                if (target == null) throw new StarBookCommandException("Player not found: %s", c.args[3]);
            }
        } else {
            if (c.player == null) StarBookCommandException.playerExpected();
        }
        if (everyone) {
            for (Player target2: plugin.getServer().getOnlinePlayers()) {
                target2.playSound(target2.getEyeLocation(), sound, volume, pitch);
                msg(c.sender, "Playing sound for %s: %s volume=%.2f pitch=%.2f", target2.getName(), sound.name(), volume, pitch);
            }
        } else {
            target.playSound(target.getEyeLocation(), sound, volume, pitch);
            msg(c.sender, "Playing sound for %s: %s volume=%.2f pitch=%.2f", target.getName(), sound.name(), volume, pitch);
        }
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        if (c.args.length != 1) return null;
        List<String> result = emptyTabList();
        String cmd = c.args[0].toLowerCase();
        for (Sound sound: Sound.values()) {
            if (sound.name().toLowerCase().startsWith(cmd)) {
                result.add(sound.name());
            }
        }
        return result;
    }
}
