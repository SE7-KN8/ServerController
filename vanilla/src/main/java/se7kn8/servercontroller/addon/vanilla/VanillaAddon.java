package se7kn8.servercontroller.addon.vanilla;

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
import java.util.ResourceBundle;

@SuppressWarnings("unused") //Loaded by AddonLoader
public class VanillaAddon extends Addon implements ServerCreator {

	public static ResourceBundle bundle = ResourceBundle.getBundle("lang/addon_vanilla_lang");

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
		return VanillaServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "vanilla";
	}

	@Nullable
	@Override
	public String getParent() {
		return "servercontroller-base:jar-server";
	}

	@Override
	@NotNull
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow propertiesRow = new DialogRow();
		propertiesRow.setName("Properties-Pfad");
		propertiesRow.setUsingFileChooser(true);
		propertiesRow.setFileExtension("*.properties");
		propertiesRow.setFileType("PROPERTIES");
		propertiesRow.setPropertyName("propertiesPath");
		propertiesRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties && properties != null) {
			propertiesRow.setDefaultValue(properties.get("propertiesPath"));
		}

		parentRows.add(propertiesRow);

		return parentRows;
	}
}