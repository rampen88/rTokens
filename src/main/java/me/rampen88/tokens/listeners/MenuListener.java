package me.rampen88.tokens.listeners;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.menu.Menu;

import me.rampen88.tokens.menu.TokensMenuHolder;
import me.rampen88.tokens.menu.items.InventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener{

	private Tokens plugin;
	private long minDelay;

	public MenuListener(Tokens plugin) {
		this.plugin = plugin;
		reload();
	}

	public void reload(){
		// Get minimum delay, with 1000 as default.
		minDelay = plugin.getConfig().getLong("InventoryClickDelay", 1000);
	}

	private Map<UUID, Long> playerCooldown = new HashMap<>();

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e){
		if(e.getInventory().getHolder() instanceof TokensMenuHolder){
			e.setCancelled(true);

			if(e.getClickedInventory() == null){
				return;
			}

			Menu menu = ((TokensMenuHolder)e.getInventory().getHolder()).getMenu();
			InventoryItem item = menu.getItemAtPosition(e.getRawSlot());

			if(item != null){
				Player p = (Player) e.getWhoClicked();

				// Make sure player cant spam click items in inventory.
				if (minDelay > 0) {
					Long last = playerCooldown.get(p.getUniqueId());
					long now = System.currentTimeMillis();

					if(last != null && last > now) return;

					// Add extra delay for certain items.
					playerCooldown.put(p.getUniqueId(), now + minDelay + item.getExtraDelay());
				}

				if(item.executeClick(p, plugin)){
					delayClose(p);
				}
			}
		}
	}

	void removePlayer(UUID id){
		playerCooldown.remove(id);
	}

	private void delayClose(Player p){
		new BukkitRunnable(){

			@Override
			public void run() {
				if(p.isOnline()){
					p.closeInventory();
				}
			}

		}.runTaskLater(plugin, 0L);
	}

	@EventHandler
	public void preCommand(PlayerCommandPreprocessEvent e){
		// substring to remove the /
		Menu m = plugin.getInventoryMaster().getMenuFromCommand(e.getMessage().substring(1));
		if(m != null){
			e.setCancelled(true);
			m.open(e.getPlayer());
		}
	}

}
