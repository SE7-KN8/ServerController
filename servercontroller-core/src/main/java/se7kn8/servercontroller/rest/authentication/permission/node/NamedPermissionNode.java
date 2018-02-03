package se7kn8.servercontroller.rest.authentication.permission.node;

import org.jetbrains.annotations.NotNull;

public class NamedPermissionNode implements PermissionNode {

	private String name;

	public NamedPermissionNode(String name) {
		this.name = name;
	}

	@Override
	public boolean isPathValid(@NotNull String path) {
		return path.equals(name);
	}

	@NotNull
	@Override
	public String getPath() {
		return name;
	}
}
