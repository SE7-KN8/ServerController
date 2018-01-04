package se7kn8.servercontroller.addon.spigot;

import se7kn8.servercontroller.api.addon.Addon;
import se7kn8.servercontroller.api.addon.AddonRegistry;
import se7kn8.servercontroller.api.gui.server.DialogRow;
import se7kn8.servercontroller.api.gui.server.ServerCreator;
import se7kn8.servercontroller.api.server.CLIServer;
import se7kn8.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") //Loaded by AddonLoader
public class SpigotAddon extends Addon implements ServerCreator {

	@Override
	public void load(@NotNull AddonRegistry registry) {
		registry.registerServerType(this);
	}

	@Override
	public void unload(@NotNull AddonRegistry registry) {
		registry.unregisterServerType(this);
	}

	@NotNull
	@Override
	public Class<? extends CLIServer> getServerClass() {
		return SpigotServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "spigot";
	}

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {

		DialogRow spigotConfigRow = new DialogRow();
		spigotConfigRow.setName("Spigot-Config");
		spigotConfigRow.setUsingFileChooser(true);
		spigotConfigRow.setFileExtension("*.yml");
		spigotConfigRow.setFileType("YML");
		spigotConfigRow.setPropertyName("spigotConfig");
		spigotConfigRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties && properties != null) {
			spigotConfigRow.setDefaultValue(properties.get("spigotConfig"));
		}
		parentRows.add(spigotConfigRow);

		return parentRows;
	}

	@Nullable
	@Override
	public String getParent() {
		return "craftbukkit:craftbukkit";
	}
}