package me.rampen88.tokens.menu.items.actions;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.util.ItemBuilder;
import me.rampen88.tokens.util.ItemEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveItem implements ItemAction{

	private static ItemBuilder itemBuilder = Tokens.getItemBuilder();

	private Material mat;
	private String name;

	private int damage;
	private int amount;

	private List<String> lore;
	private List<ItemFlag> flags;
	private List<ItemEnchant> enchants;

	public GiveItem(Material m, String name, int damage, int amount, List<ItemEnchant> enchants, List<String> lore, List<ItemFlag> flags) {
		this.mat = m;
		this.name = name;
		this.damage = damage;
		this.amount = amount;
		this.enchants = enchants;
		this.lore = lore;
		this.flags = flags;
	}

	@Override
	public void executeAction(Player p, Tokens plugin) {

		ItemStack item = itemBuilder.buildItem(mat, amount, name, damage, enchants, lore, flags, p);

		int free = getFreeSlots(p.getInventory().getStorageContents(), item);
		if(free >= item.getAmount()){

			p.getInventory().addItem(item);

		}else{
			int amountToDrop = item.getAmount() - free;

			if (free > 0) {
				item.setAmount(free);
				p.getInventory().addItem(item);
			}

			while(amountToDrop > 0){

				ItemStack drop = new ItemStack(item);
				int toDrop;

				if(amountToDrop > item.getMaxStackSize()){
					amountToDrop -= item.getMaxStackSize();
					toDrop = item.getMaxStackSize();
				}else{
					toDrop = amountToDrop;
					amountToDrop = 0;
				}

				drop.setAmount(toDrop);
				p.getWorld().dropItem(p.getLocation(), drop);
			}
		}
	}

	private int getFreeSlots(ItemStack[] contents, ItemStack item){
		int free = 0;
		for (ItemStack i : contents) {
			if (i == null || i.getType() == Material.AIR)
				free += item.getMaxStackSize();
		}
		return free;
	}


}
