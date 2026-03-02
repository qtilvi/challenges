package io.github.qtilvi.challenges.challenge;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BlockDropRandomizerChallenge extends AbstractChallenge {
    private BlockDropItemEvent blockDropItemEvent = null;
    private final Map<Material, Material> blockMap = new HashMap<>();
    private Random random;

    public BlockDropRandomizerChallenge(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public String getName() {
        return "blockrandomizer";
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
                .filter(Material::isBlock)
                .filter(mat -> !mat.isAir())
                .toList();

        List<Material> shuffled = new ArrayList<>(validBlocks);
        Collections.shuffle(shuffled, random);

        for (int i = 0; i < validBlocks.size(); i++) {
            blockMap.put(validBlocks.get(i), shuffled.get(i));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void blockDropRandomizerChallenge(BlockDropItemEvent blockDropItemEvent) {
        // this is the BlockDropItemEvent which has the randomized block, so ignore
        if (this.blockDropItemEvent != null) {
            blockDropItemEvent = null;
            return;
        }

        this.blockDropItemEvent = blockDropItemEvent;
        Block block = this.blockDropItemEvent.getBlock();
        BlockState blockState = this.blockDropItemEvent.getBlockState();
        Player player = this.blockDropItemEvent.getPlayer();
        List<Item> items = this.blockDropItemEvent.getItems();

        Material blockMaterial = block.getType();
        Material newBlockMaterial = blockMap.get(blockMaterial);
        block.setType(newBlockMaterial);


        new BlockDropItemEvent(block, blockState, player, items);

        blockDropItemEvent.setCancelled(true);


        /*
         * [X] copy this event
         * [X] cancel this event
         * [] make new event with randomized (but consistent) block
         */
    }
}
