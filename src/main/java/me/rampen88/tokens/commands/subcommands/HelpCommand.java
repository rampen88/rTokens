package me.rampen88.tokens.commands.subcommands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.commands.SimpleSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends SimpleSubCommand {

	public HelpCommand(Tokens plugin) {
		super(plugin, null, "help", "h");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		List<String> help = plugin.getConfig().getStringList("Messages.Commands.Help.Top");

		if(sender.hasPermission("tokens.command.help"))
			help.addAll(plugin.getConfig().getStringList("Messages.Commands.Help.User"));

		if(sender.hasPermission("tokens.command.help.admin"))
			help.addAll(plugin.getConfig().getStringList("Messages.Commands.Help.Admin"));

		help.add("&7" + plugin.getDescription().getName() + " version "+ plugin.getDescription().getVersion() + " made by rampen88");

		help = plugin.getMessageUtil().translateColors(help);
		help.forEach(sender::sendMessage);
	}
}
