package com.winthier.starbook;

import lombok.Getter;

@Getter
final class StarBookCommandException extends RuntimeException {
    private boolean usage;

    StarBookCommandException(final String msg) {
        super(msg);
    }

    StarBookCommandException(final CommandContext context) {
        super(context.command.getUsage());
        this.usage = true;
    }

    static StarBookCommandException playerExpected() {
        return new StarBookCommandException("Player expected");
    }

    static StarBookCommandException usage(CommandContext context) {
        return new StarBookCommandException(context);
    }
}
