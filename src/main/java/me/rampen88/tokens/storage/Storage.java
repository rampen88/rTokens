package me.rampen88.tokens.storage;

public interface Storage {

	void addTokens(String uuid, int amount);

	void takeTokens(String uuid, Integer amount, StorageCallback toRunSync);

	void getTokens(String uuid, StorageCallback toRunSync);

	void checkTable(String uuid, String tableName, StorageCallback toRunSync);

	void setupTable(String tableName);

	void addToTable(String uuid, String tableName);

	void close();

}
