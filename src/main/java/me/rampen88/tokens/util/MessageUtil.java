package me.rampen88.tokens.util;

import me.rampen88.tokens.Tokens;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {

	private Tokens plugin;

	public MessageUtil(Tokens plugin) {
		this.plugin = plugin;
	}

	public String getMessage(String path){
		return ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("Messages." + path, "&4Error: Failed to find message. Please inform staff."));
	}

	private String getPermissionMessage(){
		return getMessage("NoPermission");
	}

	public boolean hasPerm(CommandSender sender, String perm, boolean needsPlayer){
		if(sender.hasPermission(perm)){
			if(needsPlayer){
				if(sender instanceof Player)
					return true;
				else{
					sender.sendMessage(getPermissionMessage());
					return false;
				}
			}else return true;
		}else{
			sender.sendMessage(getPermissionMessage());
			return false;
		}
	}

	public List<String> translateColors(List<String> lore){
		List<String> list = new ArrayList<>();
		for(String s : lore){
			list.add(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s));
		}
		return list;
	}

}
