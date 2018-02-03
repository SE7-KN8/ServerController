package se7kn8.servercontroller.rest.authentication.permission.node;

import org.jetbrains.annotations.NotNull;

public interface PermissionNode {

	@NotNull
	String getPath();

	boolean isPathValid(@NotNull String path);

}