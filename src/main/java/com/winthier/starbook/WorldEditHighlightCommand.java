package com.winthier.starbook;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
class WorldEditHighlightCommand extends AbstractCommand {
    final StarBookPlugin plugin;

    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) StarBookCommandException.playerExpected();
	WorldEditPlugin we = getWorldEdit();
        if (we == null) throw new StarBookCommandException("WorldEdit not foundx!");
	Selection sel = we.getSelection(c.player);
        if (sel == null) throw new StarBookCommandException("Make a selection first!");
	Block a = sel.getMinimumPoint().getBlock();
	Block b = sel.getMaximumPoint().getBlock();
	new HighlightTask(c.player, a, b).runTaskTimer(plugin, 1, 1);
    }

    WorldEditPlugin getWorldEdit() {
        return (WorldEditPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    @RequiredArgsConstructor
    class HighlightTask extends BukkitRunnable {
	final Player player;
	final Block a, b;
	int iter = 0;
	int count = 0;
	@Override
	public void run() {
	    if (!player.isValid()) {
		cancel();
		return;
	    }
	    boolean active = false;
	    if (iter <= b.getX() - a.getX()) {
		highlight(a.getX(), a.getY(), a.getZ(), 0);
		highlight(a.getX(), a.getY(), b.getZ(), 0);
		highlight(a.getX(), b.getY(), a.getZ(), 0);
		highlight(a.getX(), b.getY(), b.getZ(), 0);
		active = true;
	    }
	    if (iter <= b.getY() - a.getY()) {
		highlight(a.getX(), a.getY(), a.getZ(), 1);
		highlight(a.getX(), a.getY(), b.getZ(), 1);
		highlight(b.getX(), a.getY(), a.getZ(), 1);
		highlight(b.getX(), a.getY(), b.getZ(), 1);
		active = true;
	    }
	    if (iter <= b.getZ() - a.getZ()) {
		highlight(a.getX(), a.getY(), a.getZ(), 2);
		highlight(a.getX(), b.getY(), a.getZ(), 2);
		highlight(b.getX(), a.getY(), a.getZ(), 2);
		highlight(b.getX(), b.getY(), a.getZ(), 2);
		active = true;
	    }
	    if (!active) {
		iter = 0;
		count += 1;
		if (count > 2) cancel();
	    } else {
		iter += 1;
	    }
	}

	void highlight(int x, int y, int z, int axis) {
	    switch (axis) {
	    case 0: x += iter; break;
	    case 1: y += iter; break;
	    case 2: z += iter; break;
	    }
	    Block block = a.getWorld().getBlockAt(x, y, z);
	    player.spawnParticle(Particle.CRIT, block.getLocation().add(.5, .5, .5), 3, .0, .0, .0, .0);
	}
    }
}
