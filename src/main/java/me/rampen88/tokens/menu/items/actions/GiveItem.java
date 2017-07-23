package me.rampen88.tokens.menu.items.actions;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.util.ItemBuilder;
import me.rampen88.tokens.util.ItemEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

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
		// TODO: Drop item if full inventory
		p.getInventory().addItem(itemBuilder.buildItem(mat, 1, name, damage, enchants, lore, flags, p));
	}


}
