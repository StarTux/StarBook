package com.winthier.starbook;

import org.bukkit.Sound;

class SoundCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 1) StarBookCommandException.usage(c);
        Sound sound;
        try {
            sound = Sound.valueOf(c.args[0].toUpperCase());
        } catch (IllegalArgumentException iae) {
            sound = null;
        }
        if (sound == null) throw new StarBookCommandException("Sound not found: %s", c.args[0]);
        c.player.playSound(c.player.getEyeLocation(), sound, 1.0f, 1.0f);
        msg(c.sender, "Playing sound: %s", sound.name());
    }
}
