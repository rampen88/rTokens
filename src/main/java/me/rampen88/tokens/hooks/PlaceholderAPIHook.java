package me.rampen88.tokens.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaceholderAPIHook {

	public String applyPlaceholders(Player p, String s){
		return PlaceholderAPI.setPlaceholders(p, s);
	}

	public List<String> applyPlaceholders(Player p, List<String> s){
		return PlaceholderAPI.setPlaceholders(p, s);
	}

}
