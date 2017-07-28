package me.rampen88.tokens.util;

import org.bukkit.enchantments.Enchantment;

public class ItemEnchant {

	private Enchantment enchantment;
	private int level;

	public ItemEnchant(Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.level = level;
	}

	Enchantment getEnchantment() {
		return enchantment;
	}

	int getLevel() {
		return level;
	}

}
