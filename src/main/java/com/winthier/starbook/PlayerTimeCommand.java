package com.winthier.starbook;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

class PlayerTimeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 1) StarBookCommandException.usage(c);
        String arg = c.args[0];
        long time = 0L;
        if ("reset".equalsIgnoreCase(arg) || "-r".equals(arg)) {
            c.player.resetPlayerTime();
            c.player.sendMessage(text("Player time was reset to server time", GREEN));
            return;
        } else if ("day".equalsIgnoreCase(arg)) {
            time = 1000;
        } else if ("night".equalsIgnoreCase(arg)) {
            time = 13000;
        } else if ("noon".equalsIgnoreCase(arg)) {
            time = 6000;
        } else if ("midnight".equalsIgnoreCase(arg)) {
            time = 18000;
        } else if (arg.contains(":")) {
            String[] arr = arg.split(":");
            if (arr.length != 2) throw new StarBookCommandException("Time expected: " + arg);
            long hours;
            long minutes;
            try {
                hours = Long.parseLong(arr[0]);
                minutes = Long.parseLong(arr[1]);
            } catch (NumberFormatException nfe) {
                throw new StarBookCommandException("Time expected: " + arg);
            }
            time = (hours * 1000) + (minutes * 1000 / 60) - 6000;
            if (time < 0) time = 24000 + time;
        } else {
            try {
                time = Long.parseLong(arg);
            } catch (NumberFormatException nfe) {
                throw new StarBookCommandException("Time expected: " + arg);
            }
        }
        c.player.setPlayerTime(time, false);
        c.player.sendMessage(text("Player time was set to " + time, GREEN));
    }
}
