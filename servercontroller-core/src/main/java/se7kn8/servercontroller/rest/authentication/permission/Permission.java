package se7kn8.servercontroller.rest.authentication.permission;

import se7kn8.servercontroller.rest.authentication.permission.node.GlobNode;
import se7kn8.servercontroller.rest.authentication.permission.node.PermissionNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permission {

	private String permission;
	private List<PermissionNode> nodes;

	public Permission(PermissionNode... nodes) {
		this.nodes = new ArrayList<>(Arrays.asList(nodes));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nodes.length; i++) {
			PermissionNode node = nodes[i];
			sb.append(node.getPath());
			if (i != (nodes.length - 1)) {
				sb.append(".");
			}
		}
		permission = sb.toString();
	}

	public String getPermission() {
		return permission;
	}

	public List<PermissionNode> getNodes() {
		return nodes;
	}

	public boolean isPermissionValid(String permissionToTest) {
		if (permission.equals("") || permissionToTest.equals("")) {
			return false;
		}

		String[] splitPermission = permissionToTest.split("\\.");

		if (splitPermission.length < nodes.size()) {
			return false;
		}

		for (int i = 0; i < splitPermission.length; i++) {
			String path = splitPermission[i];
			if (nodes.get(i) instanceof GlobNode) {
				return true;
			}
			if (!nodes.get(i).isPathValid(path)) {
				return false;
			}
		}

		return true;
	}

}
