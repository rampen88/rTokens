package me.rampen88.tokens.commands.subcommands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCommand extends SubCommand {

	public AddCommand(Tokens plugin) {
		super(plugin, "tokens.command.add", "add", "give");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!hasPermission(commandSender, false)) return;

		if (args.length != 3) {
			commandSender.sendMessage(messageUtil.getMessage("Commands.Add.Usage"));
			return;
		}

		Integer amount = getAndValidateInput(commandSender, args[2]);
		if (amount == null) return;

		// Check that target player is online
		Player p = getPlayerCheckOnline(commandSender, args[1]);
		if (p == null) return;

		plugin.getStorage().addTokens(p.getUniqueId().toString(), amount);

		// Inform both the target and the sender
		p.sendMessage(messageUtil.getMessage("Commands.Add.Target").replace("%amount%", amount.toString()));
		commandSender.sendMessage(messageUtil.getMessage("Commands.Add.Added").replace("%amount%", amount.toString()).replace("%player%", p.getName()));

		// Log to console.
		plugin.getLogger().info(commandSender.getName() + " added " + amount + " tokens to " + p.getName() + " (" + p.getUniqueId().toString() + ")");
	}
}
