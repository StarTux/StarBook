package com.winthier.starbook;

class StarBookCommandException extends RuntimeException {
    StarBookCommandException(String msg, Object... args) {
        super(AbstractCommand.format(msg, args));
    }

    StarBookCommandException(CommandContext context) {
        super(context.command.getUsage());
    }

    static void playerExpected() {
        throw new StarBookCommandException("Player expected");
    }
}
