package com.winthier.starbook;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

class TimeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length == 0) {
            long time = c.player.getWorld().getTime();
            c.sender.sendMessage(join(noSeparators(),
                                      text("World time "),
                                      text(String.format("%02d", hours(time)), GREEN),
                                      text(":"),
                                      text(String.format("%02d", minutes(time)), GREEN),
                                      text(" ("),
                                      text(time, GREEN),
                                      text(")")));
            return;
        }
        if (c.args.length != 1) StarBookCommandException.usage(c);
        String arg = c.args[0];
        long time;
        if ("day".equalsIgnoreCase(arg)) {
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
            time = raw(hours, minutes);
        } else {
            try {
                time = Long.parseLong(arg);
            } catch (NumberFormatException nfe) {
                throw new StarBookCommandException("Time expected: " + arg);
            }
        }
        c.player.getWorld().setTime(time);
            c.sender.sendMessage(join(noSeparators(),
                                      text("World time was set to "),
                                      text(String.format("%02d", hours(time)), GREEN),
                                      text(":"),
                                      text(String.format("%02d", minutes(time)), GREEN),
                                      text(" ("),
                                      text(time, GREEN),
                                      text(")")));
    }

    long hours(long raw) {
        long time = raw + 6000;
        long hours = time / 1000;
        if (hours >= 24) hours -= 24;
        return hours;
    }

    long minutes(long raw) {
        long time = raw + 6000;
        long minutes = ((time % 1000) * 60) / 1000;
        return minutes;
    }

    long raw(long hours, long minutes) {
        long time = (hours * 1000) + (minutes * 1000 / 60) - 6000;
        if (time < 0) time = 24000 + time;
        return time;
    }
}
