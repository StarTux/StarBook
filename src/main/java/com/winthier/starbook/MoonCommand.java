package com.winthier.starbook;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class MoonCommand extends AbstractCommand {
    final String[] phases = {
        "Full Moon",
        "Waning Gibbous",
        "Last Quarter",
        "Waning Crescent",
        "New Moon",
        "Waxing Crescent",
        "First Quarter",
        "Waxing Gibbous"
    };

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 0) StarBookCommandException.usage(c);
        long time = c.player.getWorld().getFullTime();
        long day = time / 24000;
        int index = (int) day % phases.length;
        c.player.sendMessage(join(noSeparators(),
                                  text("Moon phase: "),
                                  text(phases[index], GREEN)));
    }
}
