package se7kn8.servercontroller.api.rest;

import java.util.List;

public class ServerControllerPermissions {

	private List<ServerControllerPermission> permissionList;

	public void setPermissionList(List<ServerControllerPermission> permissionList) {
		this.permissionList = permissionList;
	}

	public List<ServerControllerPermission> getPermissionList() {
		return permissionList;
	}

	public static class ServerControllerPermission {
		private String name;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

}
