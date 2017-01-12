package sebe3012.servercontroller.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

public class WebAPI {

	private static Map<String, String> knownPlayers = new HashMap<>();
	private static Map<String, BufferedImage> knownSkins = new HashMap<>();
	private static Map<String, BufferedImage> knownHeads = new HashMap<>();

	public static String getUUID(String name) {

		if (!knownPlayers.containsKey(name)) {
			HttpsURLConnection connection = null;
			try {
				URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
				connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setDoOutput(true);

				InputStream is = connection.getInputStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = r.readLine()) != null) {
					response.append(line);
					response.append("\n");
				}
				r.close();
				if (response.toString().split("\"").length >= 3) {
					String uuid = response.toString().split("\"")[3];
					knownPlayers.put(name, uuid);
					return uuid;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "---";
		}

		return knownPlayers.get(name);

	}

	public static BufferedImage getSkin(String uuid) {

		if (!knownSkins.containsKey(uuid)) {
			HttpsURLConnection connection = null;
			try {
				URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
				connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setDoOutput(true);

				InputStream is = connection.getInputStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = r.readLine()) != null) {
					response.append(line);
					response.append("\n");
				}
				JsonReader reader = Json.createReader(new StringReader(response.toString()));

				String textureInfo = ((JsonObject) reader.read()).getJsonArray("properties").getJsonObject(0)
						.getString("value");

				textureInfo = new String(Base64.getDecoder().decode(textureInfo));

				reader = Json.createReader(new StringReader(textureInfo));

				String texturePath = ((JsonObject) reader.read()).getJsonObject("textures").getJsonObject("SKIN")
						.getString("url");

				BufferedImage image = ImageIO.read(new URI(texturePath).toURL());

				knownSkins.put(uuid, image);

				return image;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		return knownSkins.get(uuid);

	}

	public static BufferedImage getPlayerHead(String uuid) {
		if (!knownHeads.containsKey(uuid)) {

			BufferedImage head = getSkin(uuid).getSubimage(8, 8, 8, 8);

			knownHeads.put(uuid, head);

			return head;
		}

		return knownHeads.get(uuid);
	}

}
