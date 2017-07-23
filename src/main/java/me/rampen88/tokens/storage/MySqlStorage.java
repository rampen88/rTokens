package me.rampen88.tokens.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.rampen88.tokens.Tokens;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlStorage implements IStorage {

	private Tokens plugin;
	private HikariDataSource dataSource;
	private String tablePrefix;

	public MySqlStorage(Tokens plugin) {
		this.plugin = plugin;

		HikariConfig cpConfig = new HikariConfig();
		FileConfiguration config = plugin.getConfig();

		tablePrefix = config.getString("MySql.TablePrefix");

		String user = config.getString("MySql.Username");
		String pass = config.getString("MySql.Password");
		String database = config.getString("MySql.Database");
		String host = config.getString("MySql.Host");
		String port = config.getString("MySql.Port");

		boolean ssl = config.getBoolean("MySql.UseSSL");

		int maxPoolSize = config.getInt("MySql.MaxPoolSize", 10);
		long connectionTimeout = config.getLong("MySql.ConnectionTimeout", 30000);
		long idleTimeout = config.getLong("MySql.IdleTimeout", 600000);
		long maxLifetime = config.getLong("MySql.MaxLifetime", 1800000);

		cpConfig.setUsername(user);
		cpConfig.setPassword(pass);
		cpConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);

		cpConfig.addDataSourceProperty("useSSL", ssl);
		cpConfig.addDataSourceProperty("useAffectedRows", true);

		cpConfig.setMaximumPoolSize(maxPoolSize);
		cpConfig.setMaxLifetime(maxLifetime);
		cpConfig.setIdleTimeout(idleTimeout);
		cpConfig.setConnectionTimeout(connectionTimeout);

		dataSource = new HikariDataSource(cpConfig);
		setupTokensTable();
	}

	private void setupTokensTable(){
		new BukkitRunnable() {
			@Override
			public void run() {

				try(Connection connection = dataSource.getConnection()){

					PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "users (uuid VARCHAR(36) NOT NULL UNIQUE, tokens INT NOT NULL);");
					preparedStatement.execute();

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}.runTaskAsynchronously(plugin);
	}

	public void addTokens(Player p, int amount){
		new BukkitRunnable(){
			@Override
			public void run() {

				try(Connection connection = dataSource.getConnection()){

					PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tablePrefix + "users (uuid, tokens) VALUES (?,?) ON DUPLICATE KEY UPDATE tokens=tokens+?;");
					statement.setString(1, p.getUniqueId().toString());
					statement.setInt(2, amount);
					statement.setInt(3, amount);

					statement.execute();

				} catch (SQLException e) {
					e.printStackTrace();
				}


			}
		}.runTaskAsynchronously(plugin);

	}

	public void takeTokens(Player p, Integer amount, StorageCallback toRunSync){
		new BukkitRunnable(){

			@Override
			public void run() {

				try(Connection connection = dataSource.getConnection()){

					PreparedStatement statement = connection.prepareStatement("UPDATE " + tablePrefix + "users SET tokens=CASE WHEN tokens>=? THEN tokens-? ELSE tokens END WHERE uuid=?;");
					statement.setInt(1, amount);
					statement.setInt(2, amount);
					statement.setString(3, p.getUniqueId().toString());

					int rowsAffected = statement.executeUpdate();
					if(rowsAffected > 1)
						plugin.getLogger().warning("More than 1 row(s) got updated when taking credits from player: " + p.getName() + ". This should never happen!");


					new BukkitRunnable(){

						@Override
						public void run() {
							toRunSync.afterStorageCall(rowsAffected);
						}
					}.runTask(plugin);

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}.runTaskAsynchronously(plugin);

	}

	public void getTokens(Player p, StorageCallback toRunSync){
		new BukkitRunnable(){

			@Override
			public void run() {

				try(Connection connection = dataSource.getConnection()){

					PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "users WHERE uuid=?;");
					statement.setString(1, p.getUniqueId().toString());

					ResultSet resultSet = statement.executeQuery();


					int tokens = getInt(resultSet, "tokens");

					new BukkitRunnable(){

						@Override
						public void run() {
							toRunSync.afterStorageCall(tokens);
						}
					}.runTask(plugin);

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}.runTaskAsynchronously(plugin);
	}

	public void checkTable(String uuid, String tableName, StorageCallback toRunSync){
		new BukkitRunnable(){

			@Override
			public void run() {
				try(Connection connection = dataSource.getConnection()){

					PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS uuidCount FROM "+ tablePrefix + tableName + " WHERE uuid=?;");
					statement.setString(1, uuid);

					ResultSet resultSet = statement.executeQuery();

					int uuidCount = getInt(resultSet, "uuidCount");

					new BukkitRunnable(){

						@Override
						public void run() {
							toRunSync.afterStorageCall(uuidCount);
						}
					}.runTask(plugin);

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}


		}.runTaskAsynchronously(plugin);
	}

	public void addToTable(String uuid, String tableName){
		new BukkitRunnable(){

			@Override
			public void run() {
				try(Connection connection = dataSource.getConnection()){

					PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + tableName + " (uuid) VALUES(?);");
					statement.setString(1, uuid);

					statement.execute();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}


		}.runTaskAsynchronously(plugin);
	}

	public void setupTable(String tableName){
		new BukkitRunnable() {
			@Override
			public void run() {

				try(Connection connection = dataSource.getConnection()){

					PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + tableName + " (uuid VARCHAR(36) NOT NULL UNIQUE);");
					preparedStatement.execute();

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}.runTaskAsynchronously(plugin);
	}

	private int getInt(ResultSet rs, String columnLabel) throws SQLException {
		if(rs.getRow() != 0 || rs.next())
			return rs.getInt(columnLabel);
		else
			return 0;
	}
}
