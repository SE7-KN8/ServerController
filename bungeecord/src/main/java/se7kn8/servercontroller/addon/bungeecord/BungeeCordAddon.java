package se7kn8.servercontroller.addon.bungeecord;

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
public class BungeeCordAddon extends Addon implements ServerCreator {

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
		return BungeeCordServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "bungeecord";
	}

	@Nullable
	@Override
	public String getParent() {
		return "servercontroller-base:jar-server";
	}

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow configRow = new DialogRow();
		configRow.setName("Config-Pfad");
		configRow.setUsingFileChooser(true);
		configRow.setFileExtension("*.yml");
		configRow.setFileType("YML");
		configRow.setPropertyName("bungeeCordConfig");
		configRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties && properties != null) {
			configRow.setDefaultValue(properties.get("bungeeCordConfig"));
		}

		parentRows.add(configRow);

		return parentRows;
	}
}