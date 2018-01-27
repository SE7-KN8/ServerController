package se7kn8.servercontroller.rest.authentication.permission.node;

import org.jetbrains.annotations.NotNull;

public class GlobNode implements PermissionNode {

	@Override
	public boolean isPathValid(@NotNull String path) {
		return true;
	}

	@NotNull
	@Override
	public String getPath() {
		return "*";
	}
}
