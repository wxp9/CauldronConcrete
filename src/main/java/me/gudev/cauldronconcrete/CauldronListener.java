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
        if (block == null) return;

        // Cross-version check for water level
        // In 1.17+ WATER_CAULDRON implements Levelled.
        // In 1.16.5 CAULDRON implements Levelled.
        // If it doesn't have levels, it's not a water-filled cauldron.
        if (!(block.getBlockData() instanceof Levelled)) return;

        // Security check: make sure it's actually water, not lava or powder snow (for 1.17+)
        Material blockType = block.getType();
        String typeName = blockType.name();
        if (!typeName.equals("WATER_CAULDRON") && !typeName.equals("CAULDRON")) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        Material itemType = item.getType();
        String itemTypeName = itemType.name();
        
        if (!itemTypeName.endsWith("_CONCRETE_POWDER")) return;

        Material concreteType = Material.getMaterial(itemTypeName.replace("_POWDER", ""));
        if (concreteType == null) return;

        Levelled levelled = (Levelled) block.getBlockData();
        int level = levelled.getLevel();

        if (level > 0) {
            event.setCancelled(true);

            // Handle water level
            if (level > 1) {
                levelled.setLevel(level - 1);
                block.setBlockData(levelled);
            } else {
                // Return to empty cauldron
                block.setType(Material.CAULDRON);
            }

            player.getWorld().playSound(block.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);
            
            // Batch convert
            item.setType(concreteType);
            player.updateInventory();
        }
    }
}
