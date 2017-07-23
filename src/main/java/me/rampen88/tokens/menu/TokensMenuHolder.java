package me.rampen88.tokens.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TokensMenuHolder implements InventoryHolder {

	private Menu menu;
	
	TokensMenuHolder(Menu menu){
		this.menu = menu;
	}
	
	@Override
	public Inventory getInventory() {
		return Bukkit.createInventory(null, 54);
	}
	
	public Menu getMenu(){
		return menu;
	}

}
