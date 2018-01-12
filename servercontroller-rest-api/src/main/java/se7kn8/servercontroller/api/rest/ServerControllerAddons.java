package se7kn8.servercontroller.api.rest;

import java.util.List;

public class ServerControllerAddons {

	private List<ServerControllerAddonInfo> addons;

	public List<ServerControllerAddonInfo> getAddons() {
		return addons;
	}

	public void setAddons(List<ServerControllerAddonInfo> addons) {
		this.addons = addons;
	}

	public static class ServerControllerAddonInfo{
		private String name;
		private String id;
		private String version;
		private List<String> authors;

		public String getVersion() {
			return version;
		}

		public String getName() {
			return name;
		}

		public List<String> getAuthors() {
			return authors;
		}

		public String getId() {
			return id;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public void setAuthors(List<String> authors) {
			this.authors = authors;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
