package io.github.qtilvi.challenges.challenge;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoCraftingTableChallenge extends AbstractChallenge {
    public NoCraftingTableChallenge(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public String getName() {
        return "nocraftingtable";
    }

    @EventHandler(ignoreCancelled = true)
    public void noCraftingTable(PlayerInteractEvent playerInteractEvent) {
        Block clickedBlock = playerInteractEvent.getClickedBlock();
        if ((clickedBlock == null) || (clickedBlock.getType() != Material.CRAFTING_TABLE)) return;

        Action action = playerInteractEvent.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;

        playerInteractEvent.setCancelled(true);
    }
}
