package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpsHandler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7045929347464449020L;
	private String path;
	private List<Map<String, String>> allValues;

	public OpsHandler(String path) {
		this.path = path;
	}

	public OpsHandler() {
	}

	public List<Map<String, String>> getAllValues() {
		return allValues;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void readOps() {
		
		try (JsonParser parser = Json.createParser(Files.newInputStream(Paths.get(path)))) {

			allValues = null;
			
			int counter = 0;
			String currentKey = null;

			while (parser.hasNext()) {

				Event e = parser.next();

				if (e == Event.START_ARRAY) {
					allValues = new ArrayList<>();
				} else if (e == Event.START_OBJECT) {
					allValues.add(new HashMap<>());
				} else if (e == Event.KEY_NAME) {
					currentKey = parser.getString();
				} else if (e == Event.VALUE_STRING || e == Event.VALUE_NUMBER) {
					allValues.get(counter).put(currentKey, parser.getString());
				} else if (e == Event.VALUE_TRUE) {
					allValues.get(counter).put(currentKey, "true");
				} else if (e == Event.VALUE_FALSE) {
					allValues.get(counter).put(currentKey, "false");
				} else if (e == Event.END_OBJECT) {
					counter++;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
