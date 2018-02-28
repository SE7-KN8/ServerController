package se7kn8.servercontroller.api.rest;

import java.io.Serializable;
import java.util.List;

public class ServerControllerServerLog implements Serializable {
	private List<String> lines;

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public List<String> getLines() {
		return lines;
	}
}
