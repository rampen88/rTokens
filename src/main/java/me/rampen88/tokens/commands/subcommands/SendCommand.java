package me.rampen88.tokens.commands.subcommands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.commands.SubCommand;
import me.rampen88.tokens.storage.Storage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendCommand extends SubCommand {

	public SendCommand(Tokens plugin) {
		super(plugin, "tokens.command.send", "send");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if(!hasPermission(commandSender, true)) return;

		if(args.length != 3){
			commandSender.sendMessage(messageUtil.getMessage("Commands.Send.Usage"));
			return;
		}

		Player p = (Player) commandSender;

		Integer amount = getAndValidateInput(commandSender, args[2]);
		if(amount == null) return;

		Player target = getPlayerCheckOnline(commandSender, args[1]);
		if(target == null) return;

		// Make sure you cant sent tokens to yourself.
		if(target.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString())){
			p.sendMessage(messageUtil.getMessage("Commands.Send.NotSelf"));
			return;
		}

		Storage storage = plugin.getStorage();

		storage.takeTokens(p.getUniqueId().toString(), amount, value -> {
			if(value == 0){
				p.sendMessage(messageUtil.getMessage("Commands.Send.NotEnough"));
			}else{
				storage.addTokens(target.getUniqueId().toString(), amount);

				p.sendMessage(messageUtil.getMessage("Commands.Send.Sent").replace("%player%", target.getName()).replace("%amount%", amount.toString()));
				target.sendMessage(messageUtil.getMessage("Commands.Send.Received").replace("%player%", p.getName()).replace("%amount%", amount.toString()));
			}

		});
	}
}
