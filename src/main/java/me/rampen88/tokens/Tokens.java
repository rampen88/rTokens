package me.rampen88.tokens;

import me.rampen88.tokens.commands.TokenCommand;
import me.rampen88.tokens.hooks.PlaceholderAPIHook;
import me.rampen88.tokens.listeners.MenuListener;
import me.rampen88.tokens.listeners.PlayerListener;
import me.rampen88.tokens.menu.InventoryMaster;
import me.rampen88.tokens.storage.AsyncStorage;
import me.rampen88.tokens.storage.Storage;
import me.rampen88.tokens.storage.MySqlStorageImpl;
import me.rampen88.tokens.util.ItemBuilder;
import me.rampen88.tokens.util.MessageUtil;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Tokens extends JavaPlugin{

	private Storage storage;
	private MessageUtil messageUtil;
	private static ItemBuilder itemBuilder;
	private InventoryMaster inventoryMaster;
	private MenuListener menuListener;
	private PlaceholderAPIHook placeholderAPIHook;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		messageUtil = new MessageUtil(this);
		itemBuilder = new ItemBuilder(this);

		storage = setupStorage();
		if(storage == null){
			setEnabled(false);
			return;
		}

		if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && getConfig().getBoolean("UsePlaceholderAPI")){
			placeholderAPIHook = new PlaceholderAPIHook();
		}

		inventoryMaster = new InventoryMaster(this);

		registerCommands();
		registerListeners();
	}

	private void registerCommands(){
		PluginCommand rTokens = getCommand("rtokens");

		rTokens.setExecutor(new TokenCommand(this));
		rTokens.setAliases(Arrays.asList("token","tokens","rtoken"));
	}

	private void registerListeners(){
		PluginManager pluginManager = getServer().getPluginManager();

		menuListener = new MenuListener(this);
		pluginManager.registerEvents(menuListener, this);
		pluginManager.registerEvents(new PlayerListener(this), this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		storage.close();
	}

	private Storage setupStorage(){
		switch (getConfig().getString("StorageType").toLowerCase()){
			case "mysql":
				return new AsyncStorage(new MySqlStorageImpl(this), this);
			default:
				getLogger().info("I said the only supported storage type was MySQL. Now you broke the plugin. great job.");
				return null;
		}
	}

	public void reload(){
		reloadConfig();
		inventoryMaster.load();
		menuListener.reload();
	}

	// Apply placeholders if placeholderAPIHook is not null.
	public String applyPlaceholders(Player p, String s){
		return placeholderAPIHook != null ? placeholderAPIHook.applyPlaceholders(p, s) : s;
	}

	public List<String> applyPlaceholders(Player p, List<String> s){
		return placeholderAPIHook != null ? placeholderAPIHook.applyPlaceholders(p, s) : s;
	}

	public Storage getStorage() {
		return storage;
	}

	public MessageUtil getMessageUtil() {
		return messageUtil;
	}

	public static ItemBuilder getItemBuilder() {
		return itemBuilder;
	}

	public InventoryMaster getInventoryMaster() {
		return inventoryMaster;
	}

	public MenuListener getMenuListener() {
		return menuListener;
	}
}
