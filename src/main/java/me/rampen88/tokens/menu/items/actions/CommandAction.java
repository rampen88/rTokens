package me.rampen88.tokens.menu.items.actions;

import me.rampen88.tokens.Tokens;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandAction implements ItemAction{

	private String command;

	public CommandAction(String command) {
		this.command = command;
	}

	@Override
	public void executeAction(Player p, Tokens plugin) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
	}

}
