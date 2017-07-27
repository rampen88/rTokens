package me.rampen88.tokens.storage;

import me.rampen88.tokens.Tokens;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncStorage implements Storage{

	private Storage storage;
	private Tokens plugin;

	public AsyncStorage(Storage storage, Tokens plugin) {
		this.storage = storage;
		this.plugin = plugin;
	}

	@Override
	public void addTokens(String uuid, int amount) {
		new BukkitRunnable() {
			@Override
			public void run() {
				storage.addTokens(uuid, amount);
			}
		}.runTaskAsynchronously(plugin);
	}

	@Override
	public void takeTokens(String uuid, Integer amount, StorageCallback toRunSync) {
		new BukkitRunnable() {
			@Override
			public void run() {
				storage.takeTokens(uuid, amount, toRunSync);
			}
		}.runTaskAsynchronously(plugin);
	}

	@Override
	public void getTokens(String uuid, StorageCallback toRunSync) {
		new BukkitRunnable() {
			@Override
			public void run() {
				storage.getTokens(uuid, toRunSync);
			}
		}.runTaskAsynchronously(plugin);
	}

	@Override
	public void checkTable(String uuid, String tableName, StorageCallback toRunSync) {
		new BukkitRunnable() {
			@Override
			public void run() {
				storage.checkTable(uuid, tableName, toRunSync);
			}
		}.runTaskAsynchronously(plugin);
	}

	@Override
	public void setupTable(String tableName) {
		new BukkitRunnable() {
			@Override
			public void run() {
				storage.setupTable(tableName);
			}
		}.runTaskAsynchronously(plugin);
	}

	@Override
	public void addToTable(String uuid, String tableName) {
		new BukkitRunnable() {
			@Override
			public void run() {
				storage.addToTable(uuid, tableName);
			}
		}.runTaskAsynchronously(plugin);
	}

	@Override
	public void close() {
		storage.close();
	}
}
