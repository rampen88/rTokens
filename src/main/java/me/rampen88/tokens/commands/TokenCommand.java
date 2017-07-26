package me.rampen88.tokens.commands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.storage.Storage;
import me.rampen88.tokens.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenCommand implements CommandExecutor{

	private Tokens plugin;
	private MessageUtil messageUtil;

	public TokenCommand(Tokens plugin) {
		this.plugin = plugin;
		messageUtil = plugin.getMessageUtil();
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
		if(args.length == 0){
			helpCommand(commandSender);
			return true;
		}else{
			switch (args[0].toLowerCase()){
				case "add":
					addTokens(commandSender, args);
					return true;

				case "take":
					takeTokens(commandSender, args);
					return true;

				case "send":
					sendCredits(commandSender, args);
					return true;

				case "help":
					helpCommand(commandSender);
					return true;

				case "reload":
					if(!messageUtil.hasPerm(commandSender, "tokens.command.reload", false)) return true;

					plugin.reload();
					commandSender.sendMessage(messageUtil.getMessage("Commands.Reload"));

					return true;

				case "view":
					if(args.length < 2){
						if(!messageUtil.hasPerm(commandSender, "tokens.command.view", true)) return true;

						viewTokens((Player) commandSender, commandSender);
						return true;
					}
					if(!messageUtil.hasPerm(commandSender, "tokens.command.view.other", false)) return true;

					Player p = getPlayerCheckOnline(commandSender, args[1]);
					if(p == null) return true;

					viewTokens(p, commandSender);
					return true;

				default:
					// TODO: send unknown command message.
					helpCommand(commandSender);
					return true;
			}
		}
	}

	private void helpCommand(CommandSender sender){

		List<String> help = plugin.getConfig().getStringList("Messages.Commands.Help.Top");

		if(sender.hasPermission("tokens.command.help"))
			help.addAll(plugin.getConfig().getStringList("Messages.Commands.Help.User"));

		if(sender.hasPermission("tokens.command.help.admin"))
			help.addAll(plugin.getConfig().getStringList("Messages.Commands.Help.Admin"));


		help.add("&7" + plugin.getDescription().getName() + " version "+ plugin.getDescription().getVersion() + " made by rampen88");

		help = messageUtil.translateColors(help);
		help.forEach(sender::sendMessage);
	}

	private void viewTokens(Player p, CommandSender sender){
		plugin.getStorage().getTokens(p, value -> sender.sendMessage(messageUtil.getMessage("Commands.View.Other").replace("%player%", p.getName()).replace("%amount%", Integer.toString(value))));
	}

	private void addTokens(CommandSender commandSender, String[] args) {
		if (!messageUtil.hasPerm(commandSender, "tokens.command.add", false)) return;

		if (args.length != 3) {
			commandSender.sendMessage(messageUtil.getMessage("Commands.Add.Usage"));
			return;
		}

		Integer amount = getIntegerFromInput(args[2]);
		if (amount == null) {
			commandSender.sendMessage(messageUtil.getMessage("Commands.Add.Usage"));
			return;
		}else if(amount <= 0){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Amount"));
			return;
		}

		// Check that target player is online
		Player p = getPlayerCheckOnline(commandSender, args[1]);
		if (p == null) return;

		// Add the tokens
		plugin.getStorage().addTokens(p, amount);

		// Inform both the target and the sender
		p.sendMessage(messageUtil.getMessage("Commands.Add.Target").replace("%amount%", amount.toString()));
		commandSender.sendMessage(messageUtil.getMessage("Commands.Add.Added").replace("%amount%", amount.toString()).replace("%player%", p.getName()));

		// Log to console.
		plugin.getLogger().info(commandSender.getName() + " added " + amount + " tokens to " + p.getName() + " (" + p.getUniqueId().toString() + ")");
	}

	private void takeTokens(CommandSender commandSender, String[] args){
		if(!messageUtil.hasPerm(commandSender, "tokens.command.take", false)) return;

		if(args.length != 3){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Take.Usage"));
			return;
		}

		Integer amount = getIntegerFromInput(args[2]);
		if(amount == null){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Take.Usage"));
			return;
		}else if(amount <= 0){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Amount"));
			return;
		}

		// Make sure target player is online.
		Player p = getPlayerCheckOnline(commandSender, args[1]);
		if(p == null) return;

		// Take the credits from the player.
		plugin.getStorage().takeTokens(p, amount, value -> takenTokens(p, commandSender, amount, value == 1));

	}

	private void sendCredits(CommandSender commandSender, String[] args){
		if(!messageUtil.hasPerm(commandSender, "tokens.command.send", true)) return;

		if(args.length != 3){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Send.Usage"));
			return;
		}

		Player p = (Player) commandSender;

		// Integer instead of int to allow for null in case input is not valid.
		Integer amount = getIntegerFromInput(args[2]);
		if(amount == null){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Send.Usage"));
			return;
		}else if(amount <= 0){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Amount"));
			return;
		}

		// Check that target player is online
		Player target = getPlayerCheckOnline(commandSender, args[1]);
		if(target == null) return;

		// Make sure you cant sent credits to yourself.
		if(target.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString())){
			p.sendMessage(messageUtil.getMessage("Commands.Send.NotSelf"));
			return;
		}

		Storage storage = plugin.getStorage();

		storage.takeTokens(p, amount, value -> {

			if(value == 0){
				p.sendMessage(messageUtil.getMessage("Commands.Send.NotEnough"));
			}else{
				storage.addTokens(target, amount);

				p.sendMessage(messageUtil.getMessage("Commands.Send.Sent").replace("%player%", target.getName()).replace("%amount%", amount.toString()));
				target.sendMessage(messageUtil.getMessage("Commands.Send.Received").replace("%player%", p.getName()).replace("%amount%", amount.toString()));
			}

		});
	}

	private void takenTokens(Player p, CommandSender sender, Integer amount, boolean hadEnough){
		if(hadEnough) {
			sender.sendMessage(messageUtil.getMessage("Commands.Take.Removed").replace("%player%", p.getName()).replace("%amount%", amount.toString()));
			p.sendMessage(messageUtil.getMessage("Commands.Take.Target").replace("%amount%", amount.toString()));

			plugin.getLogger().info(sender.getName() + " took " + amount + " tokens from " + p.getName() + " (" + p.getUniqueId().toString() + ")");
		}else
			sender.sendMessage(messageUtil.getMessage("Commands.Take.Error").replace("%player%", p.getName()));

	}

	private Player getPlayerCheckOnline(CommandSender sender, String name){
		Player p = Bukkit.getPlayer(name);

		// Send message to the person executing the command if player is not online.
		if(p == null){
			sender.sendMessage(messageUtil.getMessage("Commands.PlayerNotOnline").replace("%player%", name));
			return null;
		}

		return p;
	}

	private Integer getIntegerFromInput(String input){
		try{

			return Integer.valueOf(input);

		}catch(NumberFormatException ignored){}
		return null;
	}

}
