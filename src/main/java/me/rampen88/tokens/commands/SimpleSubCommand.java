package me.rampen88.tokens.commands;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.util.MessageUtil;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SimpleSubCommand {

	protected MessageUtil messageUtil;
	protected Tokens plugin;

	private Set<String> aliases = new HashSet<>();
	private String permission;

	public SimpleSubCommand(Tokens plugin, String permission, String... alises) {
		this.plugin = plugin;
		this.permission = permission;
		this.aliases.addAll(Arrays.asList(alises));
		messageUtil = plugin.getMessageUtil();
	}

	public abstract void execute(CommandSender commandSender, String[] args);

	boolean isAlias(String string){
		return aliases.contains(string);
	}

	protected boolean hasPermission(CommandSender target, boolean needsPlayer){
		return hasPermission(target, permission, needsPlayer);
	}

	protected boolean hasPermission(CommandSender target, String permission, boolean needsPlayer){
		return permission == null || messageUtil.hasPerm(target, permission, needsPlayer);
	}

}
