package me.rampen88.tokens.menu;

import me.rampen88.tokens.Tokens;
import me.rampen88.tokens.config.MenuConfig;
import me.rampen88.tokens.menu.items.InventoryItem;
import me.rampen88.tokens.menu.items.InventoryRefreshItem;
import me.rampen88.tokens.menu.items.InventoryTableItem;
import me.rampen88.tokens.menu.items.actions.*;
import me.rampen88.tokens.util.ItemEnchant;

import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.util.*;

public class InventoryMaster {

	private Tokens plugin;
	
	private Map<String, Menu> all = new HashMap<>();
	private Map<String, Menu> commands = new HashMap<>();
	private HashSet<String> tables = new HashSet<>();
	
	public InventoryMaster(Tokens plugin) {
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
		// Clear the maps, so reloading works fine.
		all.clear();
		commands.clear();
		tables.clear();

		// Create a folder called Menu if it does not exist, which is where we will be loading all the Menus from.
		File file = new File(plugin.getDataFolder() + File.separator + "Menu");
		if(!file.exists()){
			file.mkdirs();
		}

		// Get all the files in the menu folder, if there are none, save a default one.
		Collection<File> menuFiles = FileUtils.listFiles(file, new String[]{"yml"}, true);
		if(menuFiles.isEmpty()){
			plugin.saveResource("Menu" + File.separator + "shop.yml", false);
			menuFiles = FileUtils.listFiles(file, new String[]{"yml"}, true);
		}

		// Create a menu for each of the file in the Menu folder.
		for(File f : menuFiles){
			MenuConfig mc = new MenuConfig(f);

			Menu menu = loadMenu(mc);
			if(menu == null) continue;

			all.put(f.getName().toLowerCase(), menu);
			// Add all the commands to the command map.
			for(String s : mc.getString("Commands","").split(";")){
				if(s.isEmpty()) continue;

				if(!commands.containsKey(s.toLowerCase())){
					commands.put(s.toLowerCase(), menu);
				}else{
					plugin.getLogger().info("Command " + s + " from menu " + f.getName() + " is already in use!");
				}

			}
		}

		tables.forEach(s -> plugin.getStorage().setupTable(s));
	}

	private Menu loadMenu(MenuConfig config){

		String title = config.getString("Title");
		int rows = config.getInt("Rows");

		Menu menu = new Menu(title, rows);

		ConfigurationSection items = config.getConfigurationSection("Items");
		for(String s : items.getKeys(false)){

			InventoryItem item = loadItem(items.getConfigurationSection(s), config);
			int pos = (items.getInt(s + ".Position-X") - 1) + ((items.getInt(s + ".Position-Y") - 1) * 9);

			menu.addItem(pos, item);
		}

		return menu;
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
		if(flags != null) {

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
		return null;
	}

	private List<ItemEnchant> getItemEnchants(List<String> ench){
		if(ench != null){

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
		return null;
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
