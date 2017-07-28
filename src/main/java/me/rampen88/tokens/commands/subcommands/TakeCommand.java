package me.rampen88.tokens.commands.subcommands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeCommand extends SubCommand {

	public TakeCommand(Tokens plugin) {
		super(plugin, "tokens.command.take", "take", "remove");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if(!hasPermission(commandSender, false)) return;

		if(args.length != 3){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Take.Usage"));
			return;
		}

		Integer amount = getAndValidateInput(commandSender, args[2]);
		if(amount == null)
			return;

		Player p = getPlayerCheckOnline(commandSender, args[1]);
		if(p == null) return;

		plugin.getStorage().takeTokens(p.getUniqueId().toString(), amount, value -> takenTokens(p, commandSender, amount, value == 1));
	}

	private void takenTokens(Player p, CommandSender sender, Integer amount, boolean hadEnough){
		if(hadEnough) {
			sender.sendMessage(messageUtil.getMessage("Commands.Take.Removed").replace("%player%", p.getName()).replace("%amount%", amount.toString()));
			p.sendMessage(messageUtil.getMessage("Commands.Take.Target").replace("%amount%", amount.toString()));

			plugin.getLogger().info(sender.getName() + " took " + amount + " tokens from " + p.getName() + " (" + p.getUniqueId().toString() + ")");
		}else
			sender.sendMessage(messageUtil.getMessage("Commands.Take.Error").replace("%player%", p.getName()));

	}

}
