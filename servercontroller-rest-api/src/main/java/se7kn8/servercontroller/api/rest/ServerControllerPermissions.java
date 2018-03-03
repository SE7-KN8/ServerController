package se7kn8.servercontroller.api.rest;

import java.io.Serializable;
import java.util.List;

public class ServerControllerPermissions implements Serializable {

	private List<ServerControllerPermission> permissionList;

	public void setPermissionList(List<ServerControllerPermission> permissionList) {
		this.permissionList = permissionList;
	}

	public List<ServerControllerPermission> getPermissionList() {
		return permissionList;
	}

	public static class ServerControllerPermission implements Serializable{
		private String name;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

}
