package com.winthier.starbook;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class ServerCommand extends AbstractCommand {
    private final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(c.command.getName());
        } catch (IOException ex) {
            // Impossible(?)
        }
        c.player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
