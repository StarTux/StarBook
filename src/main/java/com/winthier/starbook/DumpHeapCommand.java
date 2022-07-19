package com.winthier.starbook;

import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandWarn;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.management.MBeanServer;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class DumpHeapCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.args.length != 1) {
            throw StarBookCommandException.usage(c);
        }
        boolean live;
        try {
            live = CommandArgCompleter.requireBoolean(c.args[0]);
        } catch (CommandWarn warn) {
            throw new StarBookCommandException("Boolean expected: " + c.args[0]);
        }
        final File folder = new File("hprof");
        folder.mkdirs();
        final String path = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".hprof";
        final File file = new File(folder, path);
        try {
            dumpHeap(file, live);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new StarBookCommandException("IOException: " + ioe.getMessage());
        }
        c.sender.sendMessage(text("Heap dumped: " + file + " live=" + live, AQUA));
    }

    private static void dumpHeap(File file, boolean live) throws IOException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        HotSpotDiagnosticMXBean mxBean = ManagementFactory
            .newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
        mxBean.dumpHeap(file.toString(), live);
    }
}
