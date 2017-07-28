package me.rampen88.tokens.commands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.commands.subcommands.*;
import me.rampen88.tokens.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class TokensCommandHandler implements CommandExecutor{

	private Set<SimpleSubCommand> subCommands = new HashSet<>();
	private MessageUtil messageUtil;

	public TokensCommandHandler(Tokens plugin) {
		messageUtil = plugin.getMessageUtil();

		subCommands.add(new HelpCommand(plugin));
		subCommands.add(new AddCommand(plugin));
		subCommands.add(new TakeCommand(plugin));
		subCommands.add(new SendCommand(plugin));
		subCommands.add(new ViewCommand(plugin));
		subCommands.add(new ReloadCommand(plugin));
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
		SimpleSubCommand subCommand = getSubCommand(args.length == 0 ? "help" : args[0]);
		if(subCommand == null){
			commandSender.sendMessage(messageUtil.getMessage("Commands.UnknownCommand"));
			return true;
		}

		subCommand.execute(commandSender, args);
		return true;
	}

	private SimpleSubCommand getSubCommand(String input){
		for (SimpleSubCommand subCommand : subCommands) {
			if(subCommand.isAlias(input)) return subCommand;
		}
		return null;
	}
}
