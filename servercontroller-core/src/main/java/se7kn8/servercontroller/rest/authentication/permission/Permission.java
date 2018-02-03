package se7kn8.servercontroller.rest.authentication.permission;

import se7kn8.servercontroller.rest.authentication.permission.node.GlobNode;
import se7kn8.servercontroller.rest.authentication.permission.node.NamedPermissionNode;
import se7kn8.servercontroller.rest.authentication.permission.node.PermissionNode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permission {

	private static class PermissionTypeAdapter extends TypeAdapter<Permission> {

		@Override
		public void write(JsonWriter out, Permission value) throws IOException {
			out.beginObject().name("nodes").beginArray();
			for (PermissionNode node : value.getNodes()) {
				out.beginObject();
				if (node instanceof GlobNode) {
					out.name("type").value("GLOB");
				} else if (node instanceof NamedPermissionNode) {
					out.name("type").value("NAMED");
				}
				out.name("path").value(node.getPath());
				out.endObject();
			}
			out.endArray().endObject();
		}

		@Override
		public Permission read(JsonReader in) throws IOException {
			in.beginObject();
			in.nextName();
			in.beginArray();
			List<PermissionNode> nodes = new ArrayList<>();
			while (in.hasNext()) {
				in.beginObject();
				in.nextName();
				String type = in.nextString();
				in.nextName();
				String path = in.nextString();
				switch (type) {
					case "NAMED":
						nodes.add(new NamedPermissionNode(path));
						break;
					case "GLOB":
						nodes.add(new GlobNode());
						break;
				}
				in.endObject();
			}
			in.endArray();
			in.endObject();
			return new Permission(nodes.toArray(new PermissionNode[nodes.size()]));
		}
	}

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

	public static JsonElement toJson(Permission permission) {
		Gson gson = new GsonBuilder()
				.registerTypeHierarchyAdapter(Permission.class, new PermissionTypeAdapter())
				.create();
		return gson.toJsonTree(permission);
	}

	public static Permission fromJson(JsonElement element) {
		Gson gson = new GsonBuilder()
				.registerTypeHierarchyAdapter(Permission.class, new PermissionTypeAdapter())
				.create();
		return gson.fromJson(element, Permission.class);
	}

}
