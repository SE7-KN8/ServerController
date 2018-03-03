package se7kn8.servercontroller.api.rest;

import java.io.Serializable;
import java.util.List;

public class ServerControllerServers implements Serializable {

	private List<ServerControllerServer> serverList;

	public void setServerList(List<ServerControllerServer> serverList) {
		this.serverList = serverList;
	}

	public List<ServerControllerServer> getServerList() {
		return serverList;
	}

	public static class ServerControllerServer implements Serializable{
		private String name;
		private String serverCreatorInfo;
		private List<String> serverInformation;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setServerInformation(List<String> serverInformation) {
			this.serverInformation = serverInformation;
		}

		public void setServerCreatorInfo(String serverCreatorInfo) {
			this.serverCreatorInfo = serverCreatorInfo;
		}

		public String getServerCreatorInfo() {
			return serverCreatorInfo;
		}

		public List<String> getServerInformation() {
			return serverInformation;
		}
	}
}
