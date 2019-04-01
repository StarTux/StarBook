package com.winthier.starbook;

class TimeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length == 0) {
            long time = c.player.getLevel().getTime();
            msg(c.sender, "World time &a%02d&r:&a%02d&r (&2%d&r)", hours(time), minutes(time), time);
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
            if (arr.length != 2) throw new StarBookCommandException("&cTime expected: %s", arg);
            long hours, minutes;
            try {
                hours = Long.parseLong(arr[0]);
                minutes = Long.parseLong(arr[1]);
            } catch (NumberFormatException nfe) {
                throw new StarBookCommandException("&cTime expected: %s", arg);
            }
            time = raw(hours, minutes);
        } else {
            try {
                time = Long.parseLong(arg);
            } catch (NumberFormatException nfe) {
                throw new StarBookCommandException("&cTime expected: %s", arg);
            }
        }
        c.player.getLevel().setTime((int)time);
        msg(c.sender, "World time was set to &a%02d&r:&a%02d&r (&2%d&r).", hours(time), minutes(time), time);
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
