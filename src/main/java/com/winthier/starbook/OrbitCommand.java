package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandWarn;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class OrbitCommand extends AbstractCommand<StarBookPlugin> implements Listener {
    @RequiredArgsConstructor
    private final class Orbit {
        private final UUID viewer;
        private boolean started;
        private Location center;
        private int ticks;
        private double distance;
        private double height;
        private double speed;
        private float pitch;
        private double startAngle = 0;
        private BukkitTask task;

        public Player getPlayer() {
            return Bukkit.getPlayer(viewer);
        }

        public void start() {
            final Player player = getPlayer();
            started = true;
            ticks = 0;
            final Location loc = player.getLocation();
            double dx = loc.getX() - center.getX();
            double dz = loc.getZ() - center.getZ();
            distance = Math.sqrt(dx * dx + dz * dz);
            pitch = loc.getPitch();
            height = loc.getY();
            task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
        }

        private void tick() {
            final Player player = getPlayer();
            if (player == null || !player.getWorld().equals(center.getWorld())) {
                stop();
                return;
            }
            final double angle = (double) ticks * speed + startAngle * Math.PI;
            final double x = Math.cos(angle) * distance;
            final double z = Math.sin(angle) * distance;
            final Location location = new Location(player.getWorld(), center.getX() + x, height, center.getZ() + z);
            final org.bukkit.util.Vector v = center.toVector().subtract(location.toVector()).normalize();
            location.setDirection(v);
            location.setPitch(pitch);
            ticks += 1;
            player.teleport(location);
        }

        public void stop() {
            if (task != null) {
                task.cancel();
                task = null;
            }
            started = false;
        }
    }

    private Map<UUID, Orbit> orbits = new HashMap<>();

    public OrbitCommand(final StarBookPlugin plugin) {
        super(plugin, "orbit");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("center").denyTabCompletion()
            .description("Set the center")
            .playerCaller(this::center);
        rootNode.addChild("start").arguments("<speed> <startangle>")
            .description("Start spinning")
            .playerCaller(this::start);
        rootNode.addChild("stop").denyTabCompletion()
            .description("Stop spinning")
            .playerCaller(this::stop);
        rootNode.addChild("clear").denyTabCompletion()
            .description("Clear orbit")
            .playerCaller(this::clear);
    }

    private Orbit orbit(Player player) {
        return orbits.computeIfAbsent(player.getUniqueId(), u -> new Orbit(u));
    }

    private void center(Player player) {
        final Orbit orbit = orbit(player);
        orbit.center = player.getLocation();
        player.sendMessage(text("Center is set", YELLOW));
    }

    private boolean start(Player player, String[] args) {
        if (args.length != 1 && args.length != 2) return false;
        final Orbit orbit = orbit(player);
        final double speed = CommandArgCompleter.requireDouble(args[0]);
        final double startAngle = args.length >= 2
            ? CommandArgCompleter.requireDouble(args[1])
            : 0.0;
        orbit.speed = speed;
        orbit.startAngle = startAngle;
        orbit.start();
        player.sendMessage(text("Orbit started", YELLOW));
        return true;
    }

    private void stop(Player player) {
        if (!orbit(player).started) {
            throw new CommandWarn("No Orbit started");
        }
        orbit(player).stop();
        player.sendMessage(text("Orbit stopped", YELLOW));
    }

    private void clear(Player player) {
        if (orbits.remove(player.getUniqueId()) == null) {
            throw new CommandWarn("No orbit loaded");
        }
        player.sendMessage(text("Orbit cleared"));
    }
}
