package me.charles.antilopackage me.charles.antilog;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class AntiLog extends JavaPlugin implements Listener {
    private final HashMap<UUID, Long> combatTagged = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("AntiLog Enabled");
    }

    @Override
    public void onDisable() {
        combatTagged.clear();
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            long expireTime = System.currentTimeMillis() + 15_000;
            combatTagged.put(victim.getUniqueId(), expireTime);
            combatTagged.put(attacker.getUniqueId(), expireTime);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Long tagEnd = combatTagged.get(uuid);

        if (tagEnd != null && System.currentTimeMillis() < tagEnd) {
            double health = player.getHealth();

            if (health <= 12.0) {
                Location loc = player.getLocation();
                Bukkit.broadcastMessage("§c" + player.getName() + " combat logged under 6 hearts! Dropping their items.");

                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null) {
                        loc.getWorld().dropItemNaturally(loc, item);
                    }
                }

                player.getInventory().clear();
            }
        }

        combatTagged.remove(uuid);
    }
}
