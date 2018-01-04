package sebe3012.servercontroller.addon.vanilla;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.server.CLIServer;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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

	@Override
	@NotNull
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow jarRow = new DialogRow();
		jarRow.setName("Jar-Pfad");
		jarRow.setUsingFileChooser(true);
		jarRow.setFileExtension("*.jar");
		jarRow.setFileType("JAR-ARCHIVE");
		jarRow.setPropertyName("jarPath");
		jarRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		DialogRow propertiesRow = new DialogRow();
		propertiesRow.setName("Properties-Pfad");
		propertiesRow.setUsingFileChooser(true);
		propertiesRow.setFileExtension("*.properties");
		propertiesRow.setFileType("PROPERTIES");
		propertiesRow.setPropertyName("propertiesPath");
		propertiesRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties && properties != null) {
			jarRow.setDefaultValue(properties.get("jarPath"));
			propertiesRow.setDefaultValue(properties.get("propertiesPath"));
		}

		Collections.addAll(parentRows, jarRow, propertiesRow);

		return parentRows;
	}
}