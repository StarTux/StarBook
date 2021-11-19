package com.winthier.starbook;

import com.cavetale.core.font.Emoji;
import com.cavetale.core.font.GlyphPolicy;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

class MakeBookCommand extends AbstractCommand {
    @Override
    public void onCommand(CommandContext c) {
        if (c.player == null) throw new StarBookCommandException("[starbook:makebook] player expected");
        if (c.args.length == 0) throw new StarBookCommandException(c);
        String string = String.join(" ", c.args);
        string = ChatColor.translateAlternateColorCodes('&', string);
        Component component = Emoji.replaceText(string, GlyphPolicy.HIDDEN, true).asComponent();
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        book.editMeta(m -> {
                BookMeta meta = (BookMeta) m;
                meta.setAuthor("StarBook");
                meta.title(Component.text("StarBook"));
                meta.pages(List.of(component));
            });
        c.player.openBook(book);
    }

    @Override
    public List<String> onTabComplete(CommandContext c) {
        return Emoji.tabComplete(c.args[c.args.length - 1], GlyphPolicy.HIDDEN);
    }
}
