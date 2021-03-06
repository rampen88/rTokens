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

	private Map<UUID, Long> playerCooldown = new HashMap<>();
	private Tokens plugin;
	private long minDelay;

	public MenuListener(Tokens plugin) {
		this.plugin = plugin;
		reload();
	}

	public void reload(){
		minDelay = plugin.getConfig().getLong("InventoryClickDelay", 1000);
	}

	@EventHandler
	public void preCommand(PlayerCommandPreprocessEvent e){
		// substring to remove the /
		Menu m = plugin.getMenuHandler().getMenuFromCommand(e.getMessage().substring(1));
		if(m != null){
			e.setCancelled(true);
			m.open(e.getPlayer());
		}
	}

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

				if (isOnCooldown(p.getUniqueId()))
					return;
				else
					playerCooldown.put(p.getUniqueId(), System.currentTimeMillis() + minDelay + item.getExtraDelay());

				if(item.executeClick(p, plugin)){
					delayClose(p);
				}
			}
		}
	}

	private boolean isOnCooldown(UUID uuid){
		if (minDelay > 0) {
			Long last = playerCooldown.get(uuid);
			long now = System.currentTimeMillis();

			return last != null && last > now;
		}
		return false;
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

	void removePlayer(UUID id){
		playerCooldown.remove(id);
	}


}
