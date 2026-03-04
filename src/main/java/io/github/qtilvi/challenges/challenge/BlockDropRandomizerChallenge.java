package io.github.qtilvi.challenges.challenge;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BlockDropRandomizerChallenge extends AbstractChallenge {
    private final Map<Material, Material> blockMap = new HashMap<>();
    private Random random;

    public BlockDropRandomizerChallenge(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public String getName() {
        return "blockdroprandomizer";
    }

    @Override
    public void register() {
        super.register();
        this.random = new Random();
        generateMapping();
    }

    @Override
    public void disable() {
        super.disable();
        blockMap.clear();
    }

    private void generateMapping() {
        List<Material> validBlocks = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(mat -> !mat.isAir())
                .filter(mat -> mat.getMaxStackSize() > 0)
                .toList();

        List<Material> shuffled = new ArrayList<>(validBlocks);
        Collections.shuffle(shuffled, random);

        for (int i = 0; i < validBlocks.size(); i++) {
            blockMap.put(validBlocks.get(i), shuffled.get(i));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void blockDropRandomizerChallenge(BlockDropItemEvent blockDropItemEvent) {
        Block block = blockDropItemEvent.getBlock();
        BlockState blockState = blockDropItemEvent.getBlockState();
        World world = block.getWorld();

        Material blockMaterial = blockState.getType();
        Material newBlockMaterial = blockMap.get(blockMaterial);

        blockDropItemEvent.getItems().clear();

        world.dropItemNaturally(block.getLocation(), new ItemStack(newBlockMaterial));
    }
}
