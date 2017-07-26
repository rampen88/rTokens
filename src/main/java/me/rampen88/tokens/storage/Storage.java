package me.rampen88.tokens.storage;

import org.bukkit.entity.Player;

public interface Storage {

	void addTokens(Player p, int amount);

	void takeTokens(Player p, Integer amount, StorageCallback toRunSync);

	void getTokens(Player p, StorageCallback toRunSync);

	void checkTable(String uuid, String tableName, StorageCallback toRunSync);

	void setupTable(String tableName);

	void addToTable(String uuid, String tableName);

}
