package com.winthier.starbook;

import lombok.Getter;

@Getter
final class StarBookCommandException extends RuntimeException {
    private boolean usage;

    StarBookCommandException(String msg, Object... args) {
        super(AbstractCommand.format(msg, args));
    }

    StarBookCommandException(CommandContext context) {
        super(context.command.getUsage());
        this.usage = true;
    }

    static void playerExpected() {
        throw new StarBookCommandException("Player expected");
    }

    static void usage(CommandContext context) {
        throw new StarBookCommandException(context);
    }
}
