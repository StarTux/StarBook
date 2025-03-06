package com.winthier.starbook;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class MoonCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 0) StarBookCommandException.usage(c);
        c.player.sendMessage(join(noSeparators(),
                                  text("Moon phase: "),
                                  text(c.player.getWorld().getMoonPhase().name(), GREEN)));
    }
}
