package me.rampen88.tokens.menu.items;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.menu.items.actions.ItemAction;
import me.rampen88.tokens.util.ItemBuilder;
import me.rampen88.tokens.util.ItemEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryRefreshItem extends InventoryItem{

	private static ItemBuilder ib = Tokens.getItemBuilder();

	private Material mat;
	private String name;
	private int damage;
	private List<String> lore;
	private List<ItemFlag> flags;
	private List<ItemEnchant> enchants;

	public InventoryRefreshItem(ItemAction clickAction, boolean closeInv, Material m, String name, int damage, List<ItemEnchant> enchants, List<String> lore, List<ItemFlag> flags, Integer cost) {
		super(null, clickAction, closeInv, cost);
		this.mat = m;
		this.name = name;
		this.damage = damage;
		this.enchants = enchants;
		this.lore = lore;
		this.flags = flags;
	}

	@Override
	public ItemStack getItem(Player p){
		return ib.buildItem(mat, 1, name, damage, enchants, lore, flags, p);
	}

}
