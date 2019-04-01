package com.winthier.starbook;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PoofCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.player.getGamemode() == 2) {
            c.player.setGamemode(0);
            msg(c.player, "&eYou reappeared!");
        } else {
            c.player.setGamemode(2);
            msg(c.player, "&eYou vanished!");
        }
    }
}
