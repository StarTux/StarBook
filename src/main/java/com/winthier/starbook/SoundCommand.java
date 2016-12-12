package com.winthier.starbook;

import org.bukkit.Sound;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
class SoundCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
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
        if (c.args.length >= 4) {
            target = plugin.getServer().getPlayerExact(c.args[3]);
            if (target == null) throw new StarBookCommandException("Player not found: %s", c.args[3]);
        }
        target.playSound(target.getEyeLocation(), sound, volume, pitch);
        msg(c.sender, "Playing sound for %s: %s volume=%.2f pitch=%.2f", target.getName(), sound.name(), volume, pitch);
    }

    @Override
    public void onTabComplete(CommandContext c) {
        if (c.args.length != 1) return;
        c.tabCompletions = emptyTabList();
        String cmd = c.args[0].toLowerCase();
        for (Sound sound: Sound.values()) {
            if (sound.name().toLowerCase().startsWith(cmd)) {
                c.tabCompletions.add(sound.name());
            }
        }
    }
}
