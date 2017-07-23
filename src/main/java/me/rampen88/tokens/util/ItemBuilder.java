package me.rampen88.tokens.util;

import me.rampen88.tokens.Tokens;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

	private Tokens pl;
	private MessageUtil messageUtil;

	public ItemBuilder(Tokens pl){
		this.pl = pl;
		messageUtil = pl.getMessageUtil();
	}

	public ItemStack buildItem(Material m, int amount, String name, int damage, List<ItemEnchant> enchants, List<String> lore, List<ItemFlag> flags, Player p){

		ItemStack item = new ItemStack(m, amount, (short)damage);

		ItemMeta meta = item.getItemMeta();

		// Attempt to add placeholders color to ItemName and lore if they are not null.
		if(name != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pl.applyPlaceholders(p, name)));
		if(lore != null) meta.setLore(messageUtil.translateColors(pl.applyPlaceholders(p, lore)));


		if(enchants != null){
			enchants.forEach(e -> meta.addEnchant(e.getEnchantment(), e.getLevel(), true));
		}

		if(flags != null){
			flags.forEach(meta::addItemFlags);
		}

		item.setItemMeta(meta);
		return item;
	}

}
