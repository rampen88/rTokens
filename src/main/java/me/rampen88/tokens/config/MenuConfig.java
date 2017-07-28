package me.rampen88.tokens.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MenuConfig extends YamlConfiguration {

	private File file;
	
	public MenuConfig(File f){
		super();
		file = f;

		try {
			load();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

	}
	
	private void load() throws IOException, InvalidConfigurationException {
		this.getKeys(false).forEach(s -> set(s, null));
		load(file);
	}
	
	public String getName(){
		return file.getName();
	}
	
}
