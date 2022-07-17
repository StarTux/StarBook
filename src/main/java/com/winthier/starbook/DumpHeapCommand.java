package com.winthier.starbook;

import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandWarn;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class DumpHeapCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length != 2) {
            StarBookCommandException.usage(c);
            return;
        }
        String file = c.args[0];
        boolean live;
        try {
            live = CommandArgCompleter.requireBoolean(c.args[1]);
        } catch (CommandWarn warn) {
            throw new StarBookCommandException("Boolean expected: " + c.args[1]);
        }
        try {
            dumpHeap(file, live);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new StarBookCommandException("IOException: " + ioe.getMessage());
        }
        c.sender.sendMessage(text("Heap dumped: " + file + " live=" + live, AQUA));
    }

    private static void dumpHeap(String filePath, boolean live) throws IOException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        HotSpotDiagnosticMXBean mxBean = ManagementFactory
            .newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
        mxBean.dumpHeap(filePath, live);
    }
}
