package com.winthier.starbook;

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
        long time = c.player.getLevel().getCurrentTick();
        long day = time / 24000;
        int index = (int)day % phases.length;
        msg(c.player, "Moon phase: &a%s&r.", phases[index]);
    }
}
