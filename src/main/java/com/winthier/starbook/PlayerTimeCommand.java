package com.winthier.starbook;

class PlayerTimeCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
        if (c.args.length != 1) StarBookCommandException.usage(c);
        String arg = c.args[0];
        long time = 0L;
        if ("reset".equalsIgnoreCase(arg) || "-r".equals(arg)) {
            c.player.resetPlayerTime();
            msg(c.player, "&aPlayer time was reset to server time.");
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
            if (arr.length != 2) throw new StarBookCommandException("&cTime expected: %s", arg);
            long hours;
            long minutes;
            try {
                hours = Long.parseLong(arr[0]);
                minutes = Long.parseLong(arr[1]);
            } catch (NumberFormatException nfe) {
                throw new StarBookCommandException("&cTime expected: %s", arg);
            }
            time = (hours * 1000) + (minutes * 1000 / 60) - 6000;
            if (time < 0) time = 24000 + time;
        } else {
            try {
                time = Long.parseLong(arg);
            } catch (NumberFormatException nfe) {
                throw new StarBookCommandException("&cTime expected: %s", arg);
            }
        }
        c.player.setPlayerTime(time, false);
        msg(c.sender, "&aPlayer time was set to %d.", time);
    }
}
