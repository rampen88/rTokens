package me.rampen88.tokens.commands.subcommands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.commands.SimpleSubCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SimpleSubCommand{

	public ReloadCommand(Tokens plugin) {
		super(plugin, "tokens.command.reload", "reload");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if(!hasPermission(commandSender, false)) return;

		plugin.reload();
		commandSender.sendMessage(messageUtil.getMessage("Commands.Reload"));
	}
}
