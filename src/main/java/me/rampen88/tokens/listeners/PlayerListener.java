package me.rampen88.tokens.listeners;

import me.rampen88.tokens.Tokens;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener{

	private Tokens plugin;

	public PlayerListener(Tokens plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void leaveEvent(PlayerQuitEvent e){
		// Remove player from inventory cooldown map upon leaving the server.
		UUID id = e.getPlayer().getUniqueId();
		plugin.getMenuListener().removePlayer(id);
	}

}
