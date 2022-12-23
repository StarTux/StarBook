package com.winthier.starbook;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.struct.Cuboid;
import com.cavetale.core.struct.Vec3i;
import com.cavetale.core.util.Json;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class MakeModelCommand extends AbstractCommand<StarBookPlugin> {
    public MakeModelCommand(final StarBookPlugin plugin) {
        super(plugin, "makemodel");
    }

    @Override
    protected void onEnable() {
        rootNode
            .description("Make a model file")
            .playerCaller(this::model);
    }

    private void model(Player player) {
        Cuboid cuboid = Cuboid.requireSelectionOf(player);
        Map<Vec3i, BlockData> blockMap = new HashMap<>();
        for (Vec3i vec : cuboid.enumerate()) {
            Block block = vec.toBlock(player.getWorld());
            if (block.isEmpty()) continue;
            BlockData blockData = block.getBlockData();
            blockMap.put(vec, blockData);
        }
        ModelJson modelJson = new ModelJson();
        modelJson.parent = "minecraft:block/block";
        modelJson.textures = new HashMap<>();
        modelJson.elements = new ArrayList<>();
        final Vec3i size = cuboid.getSize();
        for (Map.Entry<Vec3i, BlockData> entry : blockMap.entrySet()) {
            Vec3i vector = entry.getKey();
            BlockData blockData = entry.getValue();
            Vec3i from = vector.subtract(cuboid.getMin());
            // Center
            from = from.add(-size.x / 2 + 8, 0, -size.z / 2 + 8);
            Vec3i to = from.add(1, 1, 1);
            ElementJson elementJson = new ElementJson();
            elementJson.from = List.of(from.x, from.y, from.z);
            elementJson.to = List.of(to.x, to.y, to.z);
            elementJson.faces = new HashMap<>();
            for (BlockFace face : List.of(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN)) {
                if (blockMap.containsKey(vector.add(face.getModX(), face.getModY(), face.getModZ()))) {
                    continue;
                }
                FaceJson faceJson = new FaceJson();
                faceJson.uv = List.of(0, 0, 16, 16);
                String textureKey = blockData.getMaterial().getKey().getKey();
                faceJson.texture = "#" + textureKey;
                modelJson.textures.put(textureKey, "block/" + blockData.getMaterial().getKey().getKey());
                elementJson.faces.put(face.name().toLowerCase(), faceJson);
            }
            if (elementJson.faces.isEmpty()) continue;
            modelJson.elements.add(elementJson);
        }
        File file = new File(plugin.getDataFolder(), "model.json");
        Json.save(file, modelJson, true);
        player.sendMessage(text("Model saved to " + file, YELLOW));
    }

    private static final class ModelJson {
        protected String parent;
        protected Boolean ambientocclusion;
        @SuppressWarnings("MemberName")
        protected String gui_light;
        protected List<Object> elements;
        protected Map<String, Object> display;
        protected Map<String, String> textures;
        protected List<Map<String, Object>> overrides;
    }

    private static final class ElementJson {
        protected List<Integer> from;
        protected List<Integer> to;
        protected Map<String, Object> rotation;
        protected boolean shade;
        protected Map<String, FaceJson> faces;
    }

    private static final class FaceJson {
        protected List<Integer> uv;
        protected String texture;
        protected String cullface;
        protected Integer rotation;
        protected Integer tintindex;
    }
}
