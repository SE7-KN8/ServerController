package sebe3012.servercontroller.addon.vanilla.dialog.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class PropertiesHandler {
	private File properitesFile;

	public PropertiesHandler(File propertiesFile) {
		this.properitesFile = propertiesFile;
	}

	public void readProperties() throws FileNotFoundException {
		Scanner s = new Scanner(properitesFile);

		while (s.hasNextLine()) {
			String l = s.nextLine();
			String[] lArr = l.split("=");
			String lValue;
			if (lArr.length == 1) {
				lValue = " ";
			} else {
				lValue = l.split("=")[1];
			}
			if (l.startsWith("#")) {
				continue;
			}
			allValues.put(l.split("=")[0], lValue);
			if (l.contains("generator-settings")) {
				generatorSettings = lValue;
			} else if (l.contains("op-permisson-level")) {
				opPermissonLevel = lValue;
			} else if (l.contains("resource-pack-hash")) {
				resourcePackHash = lValue;
			} else if (l.contains("level-name")) {
				levelName = lValue;
			} else if (l.contains("allow-flight")) {
				allowFlight = lValue;
			} else if (l.contains("announce-player-achievements")) {
				announcePlayerAchievements = lValue;
			} else if (l.contains("server-port")) {
				serverPort = lValue;
			} else if (l.contains("max-world-size")) {
				maxWorldSize = lValue;
			} else if (l.contains("level-type")) {
				levelType = lValue;
			} else if (l.contains("enable-rcon")) {
				enableRcon = lValue;
			} else if (l.contains("level-seed")) {
				levelSeed = lValue;
			} else if (l.contains("force-gamemode")) {
				forceGamemode = lValue;
			} else if (l.contains("server-ip")) {
				serverIp = lValue;
			} else if (l.contains("network-compression-threshold")) {
				networkCompressionThreshold = lValue;
			} else if (l.contains("max-build-height")) {
				maxBuildHeight = lValue;
			} else if (l.contains("spawn-npcs")) {
				spawnNpcs = lValue;
			} else if (l.contains("white-list")) {
				whiteList = lValue;
			} else if (l.contains("spawn-animales")) {
				spawnAnimales = lValue;
			} else if (l.contains("snooper-enable")) {
				snooperEnabled = lValue;
			} else if (l.contains("online-mode")) {
				onlineMode = lValue;
			} else if (l.contains("resource-pack")) {
				resourcePack = lValue;
			} else if (l.contains("pvp")) {
				pvp = lValue;
			} else if (l.contains("difficulty")) {
				difficulty = lValue;
			} else if (l.contains("enable-command-block")) {
				enableCommandBlock = lValue;
			} else if (l.contains("gamemode")) {
				gamemode = lValue;
			} else if (l.contains("player-idle-timeout")) {
				playerIdleTimeout = lValue;
			} else if (l.contains("max-players")) {
				maxPlayers = lValue;
			} else if (l.contains("max-tick-time")) {
				maxTickTime = lValue;
			} else if (l.contains("spawn-monsters")) {
				spawnMonsters = lValue;
			} else if (l.contains("generate-structures")) {
				generateStructures = lValue;
			} else if (l.contains("view-distance")) {
				viewDistance = lValue;
			} else if (l.contains("motd")) {
				motd = lValue;
			}
		}

		s.close();
	}

	public HashMap<String, String> getAllValues() {
		return allValues;
	}

	private HashMap<String, String> allValues = new HashMap<>();
	private String generatorSettings;
	private String opPermissonLevel;
	private String resourcePackHash;
	private String levelName;
	private String allowFlight;
	private String announcePlayerAchievements;
	private String serverPort;
	private String maxWorldSize;
	private String levelType;
	private String enableRcon;
	private String levelSeed;
	private String forceGamemode;
	private String serverIp;
	private String networkCompressionThreshold;
	private String maxBuildHeight;
	private String spawnNpcs;
	private String whiteList;
	private String spawnAnimales;
	private String snooperEnabled;
	private String onlineMode;
	private String resourcePack;
	private String pvp;
	private String difficulty;
	private String enableCommandBlock;
	private String gamemode;
	private String playerIdleTimeout;
	private String maxPlayers;
	private String maxTickTime;
	private String spawnMonsters;
	private String generateStructures;
	private String viewDistance;
	private String motd;

	public File getProperitesFile() {
		return properitesFile;
	}

	public String getGeneratorSettings() {
		return generatorSettings;
	}

	public String getOpPermissonLevel() {
		return opPermissonLevel;
	}

	public String getResourcePackHash() {
		return resourcePackHash;
	}

	public String getLevelName() {
		return levelName;
	}

	public String getAllowFlight() {
		return allowFlight;
	}

	public String getAnnouncePlayerAchievements() {
		return announcePlayerAchievements;
	}

	public String getServerPort() {
		return serverPort;
	}

	public String getMaxWorldSize() {
		return maxWorldSize;
	}

	public String getLevelType() {
		return levelType;
	}

	public String getEnableRcon() {
		return enableRcon;
	}

	public String getLevelSeed() {
		return levelSeed;
	}

	public String getForceGamemode() {
		return forceGamemode;
	}

	public String getServerIp() {
		return serverIp;
	}

	public String getNetworkCompressionThreshold() {
		return networkCompressionThreshold;
	}

	public String getMaxBuildHeight() {
		return maxBuildHeight;
	}

	public String getSpawnNpcs() {
		return spawnNpcs;
	}

	public String getWhiteList() {
		return whiteList;
	}

	public String getSpawnAnimales() {
		return spawnAnimales;
	}

	public String getSnooperEnabled() {
		return snooperEnabled;
	}

	public String getOnlineMode() {
		return onlineMode;
	}

	public String getResourcePack() {
		return resourcePack;
	}

	public String getPvp() {
		return pvp;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public String getEnableCommandBlock() {
		return enableCommandBlock;
	}

	public String getGamemode() {
		return gamemode;
	}

	public String getPlayerIdleTimeout() {
		return playerIdleTimeout;
	}

	public String getMaxPlayers() {
		return maxPlayers;
	}

	public String getMaxTickTime() {
		return maxTickTime;
	}

	public String getSpawnMonsters() {
		return spawnMonsters;
	}

	public String getGenerateStructures() {
		return generateStructures;
	}

	public String getViewDistance() {
		return viewDistance;
	}

	public String getMotd() {
		return motd;
	}
}
