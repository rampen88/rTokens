package me.rampen88.tokens.menu.items.actions;

import me.rampen88.tokens.Tokens;
import org.bukkit.entity.Player;

public class OpenInventory implements ItemAction{

	private String inventoryName;

	public OpenInventory(String inventoryName) {
		this.inventoryName = inventoryName;
	}

	@Override
	public void executeAction(Player p, Tokens plugin) {
		plugin.getInventoryMaster().openInv(inventoryName, p);
	}
}
