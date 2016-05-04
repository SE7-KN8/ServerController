package sebe3012.servercontroller.server;

public enum ServerTypes {
	VANILLA, SPIGOT, CRAFTBUKKIT, BUNGEE;

	public String getType() {
		switch (this) {
		case VANILLA:
			return "vanilla";
		case SPIGOT:
			return "spigot";
		case CRAFTBUKKIT:
			return "craftbukkit";
		case BUNGEE:
			return "bungee";
		default:
			return "unsupported";
		}
	}
}