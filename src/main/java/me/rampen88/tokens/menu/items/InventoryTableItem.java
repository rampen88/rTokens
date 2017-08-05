package me.rampen88.tokens.menu.items;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.menu.items.actions.ItemAction;
import me.rampen88.tokens.util.ItemEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class InventoryTableItem extends InventoryRefreshItem{

	private String tableName;

	public InventoryTableItem(ItemAction clickAction, boolean closeInv, Material m, String name, int damage, List<ItemEnchant> enchants, List<String> lore, List<ItemFlag> flags, Integer cost, String tableName) {
		super(clickAction, closeInv, m, name, damage, enchants, lore, flags, cost);
		this.tableName = tableName;
	}

	@Override
	public boolean executeClick(Player p, Tokens plugin) {
		if(tableName != null){

			plugin.getStorage().checkTable(p.getUniqueId().toString(), tableName, value -> {

				if(value >= 1){
					p.sendMessage(plugin.getMessageUtil().getMessage("AlreadyBought"));
				}else{
					InventoryTableItem.super.executeClick(p, plugin);
				}

			});

		}else{
			return super.executeClick(p, plugin);
		}

		return closeInv;
	}

	@Override
	protected void doTheThing(Player p, Tokens plugin) {
		if(tableName != null)
			plugin.getStorage().addToTable(p.getUniqueId().toString(), tableName);
		super.doTheThing(p, plugin);
	}

	@Override
	public long getExtraDelay() {
		return 1500;
	}
}
