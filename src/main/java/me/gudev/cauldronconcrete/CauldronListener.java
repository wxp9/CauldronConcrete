package me.gudev.cauldronconcrete;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CauldronListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCauldronInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        Player player = event.getPlayer();
        if (!player.hasPermission("cauldronconcrete.use")) return;
        if (!player.isSneaking()) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.WATER_CAULDRON) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        Material type = item.getType();
        String typeName = type.name();
        
        if (!typeName.endsWith("_CONCRETE_POWDER")) return;

        Material concreteType = Material.getMaterial(typeName.replace("_POWDER", ""));
        if (concreteType == null) return;

        Levelled levelled = (Levelled) block.getBlockData();
        int level = levelled.getLevel();

        if (level > 0) {
            // Cancel placement
            event.setCancelled(true);

            // Consume one water level
            if (level > 1) {
                levelled.setLevel(level - 1);
                block.setBlockData(levelled);
            } else {
                block.setType(Material.CAULDRON);
            }

            // Effects
            player.getWorld().playSound(block.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);

            // BATCH CONVERSION: Convert the entire stack in hand
            // We use a small delay or direct modification. Direct is fine since we cancelled the event.
            item.setType(concreteType);
            
            // Force update to prevent client-side desync (ghost items)
            player.updateInventory();
        }
    }
}
