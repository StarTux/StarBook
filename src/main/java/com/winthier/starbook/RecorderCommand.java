package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandWarn;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class RecorderCommand extends AbstractCommand<StarBookPlugin> implements Listener {
    private BukkitTask recorderTask;
    private final List<Tick> ticks = new ArrayList<>();
    private int currentTick = -1;

    public RecorderCommand(final StarBookPlugin plugin) {
        super(plugin, "recorder");
    }

    private static record Tick(int tick, Instrument instrument, Note note) { }

    @Override
    protected void onEnable() {
        rootNode.addChild("start").denyTabCompletion()
            .description("Start recording")
            .playerCaller(this::start);
        rootNode.addChild("stop").denyTabCompletion()
            .description("Stop recording")
            .playerCaller(this::stop);
        rootNode.addChild("replay").denyTabCompletion()
            .description("Replay recording")
            .playerCaller(this::replay);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void start(Player player) {
        if (recorderTask != null) throw new CommandWarn("Already recording");
        ticks.clear();
        currentTick = -1;
        recorderTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (currentTick >= 0) currentTick++;
            }, 0L, 1L);
        player.sendMessage(text("Recorder started", YELLOW));
    }

    private void stop(Player player) {
        if (recorderTask == null) throw new CommandWarn("Not recording");
        recorderTask.cancel();
        recorderTask = null;
        player.sendMessage(text("Recorder stopped: " + ticks.size(), YELLOW));
    }

    private void replay(Player player) {
        new BukkitRunnable() {
            private int time = 0;
            private int index = 0;
            private final List<Tick> list = List.copyOf(ticks);
            @Override public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                if (index >= list.size()) {
                    cancel();
                    player.sendMessage(text("Replay finished", GREEN));
                    return;
                }
                boolean success = false;
                do {
                    success = false;
                    Tick tick = list.get(index);
                    if (tick.tick() == time) {
                        player.playNote(player.getLocation(), tick.instrument(), tick.note());
                        player.sendActionBar(text("" + index + " " + tick.tick() + " " + tick.instrument() + " " + tick.note(), GREEN));
                        index += 1;
                        success = index < list.size();
                    }
                } while (success);
                time += 1;
            }
        }.runTaskTimer(plugin, 1L, 1L);
        player.sendMessage(text("Replay started: " + ticks.size(), YELLOW));
    }

    @EventHandler
    private void onNotePlay(NotePlayEvent event) {
        if (recorderTask == null) return;
        if (currentTick < 0) currentTick = 0;
        ticks.add(new Tick(currentTick, event.getInstrument(), event.getNote()));
    }
}
