package me.rampen88.tokens.menu;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.config.MenuConfig;
import me.rampen88.tokens.menu.items.InventoryItem;
import me.rampen88.tokens.menu.items.InventoryRefreshItem;
import me.rampen88.tokens.menu.items.InventoryTableItem;
import me.rampen88.tokens.menu.items.actions.*;
import me.rampen88.tokens.storage.Storage;
import me.rampen88.tokens.util.ItemEnchant;

import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.util.*;

public class MenuHandler {

	private Tokens plugin;
	
	private Map<String, Menu> all = new HashMap<>();
	private Map<String, Menu> commands = new HashMap<>();
	private Set<String> tables = new HashSet<>();

	private File menuFilesFolder;
	
	public MenuHandler(Tokens plugin) {
		this.plugin = plugin;
		load();
	}
	
	public void openInv(String name, Player p){
		Menu m = all.get(name.toLowerCase());
		if(m != null){
			m.open(p);
		}
	}

	public Menu getMenuFromCommand(String command){
		return commands.get(command.toLowerCase());
	}
	
	public void load(){
		clearMaps();

		menuFilesFolder = getFile("Menu");
		createFolders(menuFilesFolder);

		Collection<File> menuFiles = getAllMenuFiles();
		loadAllMenus(menuFiles);

		Storage storage = plugin.getStorage();
		tables.forEach(storage::setupTable);
	}

	private void clearMaps(){
		all.clear();
		commands.clear();
		tables.clear();
	}

	private File getFile(String name){
		return new File(plugin.getDataFolder() + File.separator + name);
	}

	private void createFolders(File folder){
		if(!folder.exists())
			folder.mkdirs();
	}

	private Collection<File> getAllMenuFiles(){
		Collection<File> menuFiles = FileUtils.listFiles(menuFilesFolder, new String[]{"yml"}, true);
		if(menuFiles.isEmpty())
			saveAndAddDefaultMenuFiles(menuFiles);
		return menuFiles;
	}

	private void saveAndAddDefaultMenuFiles(Collection<File> files){
		plugin.saveResource("Menu" + File.separator + "shop.yml", false);
		files.addAll(FileUtils.listFiles(menuFilesFolder, new String[]{"yml"}, true));
	}

	private void loadAllMenus(Collection<File> menuFiles){
		for(File f : menuFiles){
			MenuConfig menuConfig = new MenuConfig(f);

			Menu menu = loadMenu(menuConfig);
			if(menu == null) continue;

			all.put(f.getName().toLowerCase(), menu);
			registerMenuCommands(menuConfig, menu);
		}
	}

	private Menu loadMenu(MenuConfig config){
		String title = config.getString("Title");
		int rows = config.getInt("Rows");

		Menu menu = new Menu(title, rows);
		loadAllItems(config, menu);

		return menu;
	}

	private void registerMenuCommands(MenuConfig menuConfig, Menu menu){
		for(String s : getMenuCommands(menuConfig)){
			if(s.isEmpty()) continue;

			if(!commands.containsKey(s.toLowerCase()))
				commands.put(s.toLowerCase(), menu);
			else
				plugin.getLogger().info("Command " + s + " from menu " + menuConfig.getName() + " is already in use!");

		}
	}

	private String[] getMenuCommands(MenuConfig menuConfig){
		return menuConfig.getString("Commands", "").split(";");
	}

	private void loadAllItems(MenuConfig config, Menu menu){
		ConfigurationSection items = config.getConfigurationSection("Items");
		for(String s : items.getKeys(false)){

			InventoryItem item = loadItem(items.getConfigurationSection(s), config);
			if(item == null) continue;

			int position = (items.getInt(s + ".Position-X") - 1) + ((items.getInt(s + ".Position-Y") - 1) * 9);
			menu.addItem(position, item);

		}
	}

	private InventoryItem loadItem(ConfigurationSection section, MenuConfig menuConfig){
		Material mat = Material.getMaterial(section.getString("Material").toUpperCase());

		if(mat == null){
			plugin.getLogger().info("Invalid material: " + section.getString("Material") + ", Skipping item.");
			return null;
		}

		String name = section.getString("Name");
		List<String> lore = section.getStringList("Lore");
		int damage = section.getInt("Damage", 0);
		Integer cost = section.contains("Cost") ? section.getInt("Cost") : null;
		boolean closeInv = section.getBoolean("CloseInventory", false);

		ItemAction itemAction = getItemActions(section.getStringList("ItemAction"), menuConfig);

		List<ItemEnchant> itemEnchants = getItemEnchants(section.getStringList("Enchantments"));
		List<ItemFlag> itemFlags = getItemFlags(section.getStringList("ItemFlags"));

		String tableName = section.getString("ItemTable");
		if(tableName != null && !tableName.isEmpty()){
			tables.add(tableName);
			return new InventoryTableItem(itemAction, closeInv, mat, name, damage, itemEnchants, lore, itemFlags, cost, tableName);
		}else{
			return new InventoryRefreshItem(itemAction, closeInv, mat, name, damage, itemEnchants, lore, itemFlags, cost);
		}
	}

	private List<ItemFlag> getItemFlags(List<String> flags){
		if(flags == null) return null;

		ArrayList<ItemFlag> itemFlags = new ArrayList<>();
		for (String s : flags) {
			try {

				itemFlags.add(ItemFlag.valueOf(s));

			} catch (IllegalArgumentException | NullPointerException e) {
				plugin.getLogger().info("Invalid ItemFlag: " + s);
			}
		}
		return itemFlags;
	}

	private List<ItemEnchant> getItemEnchants(List<String> ench){
		if(ench == null) return null;

		ArrayList<ItemEnchant> itemEnchants = new ArrayList<>();
		for(String s : ench){

			String[] enchant = s.split(",");
			// TODO: log to console
			if(enchant.length != 2) continue;
			try{

				int level = Integer.valueOf(enchant[1]);
				Enchantment enchantment = Enchantment.getByName(enchant[0]);
				if(enchantment == null){
					plugin.getLogger().info("Invalid enchantment: " + enchant[0]);
					continue;
				}

				itemEnchants.add(new ItemEnchant(enchantment, level));

			}catch (NumberFormatException e){
				plugin.getLogger().info("Invalid enchantment level: " + enchant[1]);
			}
		}
		return itemEnchants;
	}

	private ItemAction getItemActions(List<String> actions, MenuConfig menuConfig){
		if(actions == null || actions.size() == 0) return null;

		ItemActionSet itemActionSet = new ItemActionSet();
		actions.forEach(s -> {

			String[] args = s.split("::");
			if(args.length < 2) return;

			switch (args[0]){
				case "GI":
					GiveItem item = loadGiveItem(menuConfig.getConfigurationSection("GiveItems." + args[1]));
					if(item != null) itemActionSet.addAction(item);
					break;
				case "CMD":
					CommandAction cmd = new CommandAction(args[1]);
					itemActionSet.addAction(cmd);
					break;
				case "OI":
					OpenInventory openInventory = new OpenInventory(args[1].toLowerCase());
					itemActionSet.addAction(openInventory);
					break;
				default:
			}
		});

		return itemActionSet;
	}

	private GiveItem loadGiveItem(ConfigurationSection section){
		if(section == null) return null;
		Material mat = Material.getMaterial(section.getString("Material").toUpperCase());

		if(mat == null){
			plugin.getLogger().info("Invalid material: " + section.getString("Material") + ", Skipping item.");
			return null;
		}

		String name = section.getString("Name");
		int damage = section.getInt("Damage", 0);
		int amount = section.getInt("Amount",1);

		List<String> lore = section.getStringList("Lore");
		List<ItemEnchant> itemEnchants = getItemEnchants(section.getStringList("Enchantments"));
		List<ItemFlag> itemFlags = getItemFlags(section.getStringList("ItemFlags"));

		return new GiveItem(mat, name, damage, amount, itemEnchants, lore, itemFlags);
	}

}
