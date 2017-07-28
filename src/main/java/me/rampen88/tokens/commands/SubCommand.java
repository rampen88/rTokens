package me.rampen88.tokens.commands;

import me.rampen88.tokens.Tokens;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand extends SimpleSubCommand {


	public SubCommand(Tokens plugin, String permission, String... alises) {
		super(plugin, permission, alises);
	}

	protected Player getPlayerCheckOnline(CommandSender sender, String name){
		Player p = Bukkit.getPlayer(name);
		if(p == null)
			sender.sendMessage(messageUtil.getMessage("Commands.PlayerNotOnline").replace("%player%", name));

		return p;
	}

	protected Integer getAndValidateInput(CommandSender commandSender, String input){
		Integer amount = getIntegerFromInput(input);
		if(amount == null)
			commandSender.sendMessage(messageUtil.getMessage("Commands.Send.Usage"));
		else if(amount <= 0){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Amount"));
			return null;
		}
		return amount;
	}

	private Integer getIntegerFromInput(String input){
		try{
			return Integer.valueOf(input);
		}catch(NumberFormatException ignored){}
		return null;
	}

}
