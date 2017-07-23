package me.rampen88.tokens.menu;

import me.rampen88.tokens.menu.items.InventoryItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Menu {

	private String title;
	private InventoryItem[] items;

	public Menu(String title, int rows){
		this.title = ChatColor.translateAlternateColorCodes('&', title);
		items = new InventoryItem[rows * 9];
	}
	
	void addItem(int position, InventoryItem item){
		if(position <= items.length && position >= 0){
			items[position] = item;
		}
	}
	
	public InventoryItem getItemAtPosition(int pos){
		if(pos >= 0 && pos < items.length){
			return items[pos];
		}else{
			return null;
		}
	}
	
	public void open(Player p){
		Inventory inv = Bukkit.createInventory(new TokensMenuHolder(this), items.length, title);
		for(int x = 0; x < items.length; x++){
			if(items[x] != null){
				ItemStack item = items[x].getItem(p);
				if(item != null) inv.setItem(x, item);
			}
		}
		p.openInventory(inv);
	}
	
}
