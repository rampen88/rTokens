package me.rampen88.tokens.commands.subcommands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewCommand extends SubCommand {

	public ViewCommand(Tokens plugin) {
		super(plugin, "tokens.command.view", "view", "look");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if(args.length < 2){
			if(!hasPermission(commandSender, true)) return;

			viewTokens((Player) commandSender, commandSender, "Self");
			return;
		}
		if(!hasPermission(commandSender, "tokens.command.view.other", false)) return;

		Player p = getPlayerCheckOnline(commandSender, args[1]);
		if(p == null) return;

		viewTokens(p, commandSender, "Other");
	}

	private void viewTokens(Player p, CommandSender sender, String extraMsgPath){
		plugin.getStorage().getTokens(p.getUniqueId().toString(), value -> sender.sendMessage(messageUtil.getMessage("Commands.View." + extraMsgPath).replace("%player%", p.getName()).replace("%amount%", Integer.toString(value))));
	}

}
