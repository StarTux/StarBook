package com.winthier.starbook;

class OpMeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 0) StarBookCommandException.usage(c);
        boolean val = c.player.isOp();
        c.player.setOp(!val);
        if (val) {
            c.player.sendMessage("De-opped yourself");
        } else {
            c.player.sendMessage("Opped yourself");
        }
    }
}
