package me.rampen88.tokens.menu.items;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.menu.items.actions.ItemAction;
import me.rampen88.tokens.storage.StorageCallback;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryItem {

	private ItemStack item;
	private ItemAction clickAction;
	boolean closeInv;
	private Integer cost;
	
	InventoryItem(ItemStack item, ItemAction clickAction, boolean closeInv, Integer cost){
		this.item = item;
		this.clickAction = clickAction;
		this.closeInv = closeInv;
		this.cost = cost;
	}
	
	public ItemStack getItem(Player p){
		return item;
	}
	
	public boolean executeClick(Player p, Tokens plugin){
		if(cost != null){

			plugin.getStorage().takeTokens(p, cost, value -> {
				if(value >= 1){
					doTheThing(p, plugin);
				}else{
					p.sendMessage(plugin.getMessageUtil().getMessage("NotEnough"));
				}
			});

		}else{
			doTheThing(p, plugin);
		}
		return closeInv;
	}

	private void doTheThing(Player p, Tokens plugin){
		if(clickAction != null){
			clickAction.executeAction(p, plugin);
		}
	}

	public long getExtraDelay(){
		return 0;
	}
	
}
