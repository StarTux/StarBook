package com.winthier.starbook;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class OpMeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 0) StarBookCommandException.usage(c);
        boolean val = c.player.isOp();
        c.player.setOp(!val);
        if (val) {
            c.player.sendMessage(text("De-opped yourself", YELLOW));
        } else {
            c.player.sendMessage(text("Opped yourself", AQUA));
        }
    }
}
