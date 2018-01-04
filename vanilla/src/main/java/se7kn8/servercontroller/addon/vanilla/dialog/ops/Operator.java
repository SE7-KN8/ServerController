package se7kn8.servercontroller.addon.vanilla.dialog.ops;

/**
 * Created by Sebe3012 on 28.02.2017.
 * A wrapper class for the operators of a server
 */
public class Operator {
	private String uuid;
	private String name;
	private int level;
	private boolean bypassesPlayerLimit;

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public String getUuid() {
		return uuid;
	}

	public boolean isBypassesPlayerLimit() {
		return bypassesPlayerLimit;
	}

	@Override
	public String toString() {
		return "Operator{" +
				"uuid='" + uuid + '\'' +
				", name='" + name + '\'' +
				", level=" + level +
				", bypassesPlayerLimit=" + bypassesPlayerLimit +
				'}';
	}
}
