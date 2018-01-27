package se7kn8.servercontroller.rest.authentication.permission;

import io.javalin.security.Role;

public class PermissionRole implements Role {

	private String permission;

	public PermissionRole(String permission){
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
