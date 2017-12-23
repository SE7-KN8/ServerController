package sebe3012.servercontroller.addon.vanilla;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class VanillaAddon extends Addon implements ServerCreator {

	public static ResourceBundle bundle = ResourceBundle.getBundle("lang/addon_vanilla_lang");

	@Override
	public void load(AddonRegistry registry) {
		registry.registerServerType(VanillaServer.class, this);
	}

	@Override
	public void unload(AddonRegistry registry) {
		registry.unregisterServerType(VanillaServer.class);
	}

	@Override
	@NotNull
	public List<DialogRow> createServerDialogRows(@NotNull Map<String, StringProperty> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow jarRow = new DialogRow();
		jarRow.setName("Jar-Pfad");
		jarRow.setUsingFileChooser(true);
		jarRow.setFileExtension("*.jar");
		jarRow.setFileType("JAR-ARCHIVE");
		jarRow.setPropertyName("jarfile");
		jarRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		DialogRow propertiesRow = new DialogRow();
		propertiesRow.setName("Properties-Pfad");
		propertiesRow.setUsingFileChooser(true);
		propertiesRow.setFileExtension("*.properties");
		propertiesRow.setFileType("PROPERTIES");
		propertiesRow.setPropertyName("properties");
		propertiesRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties) {
			jarRow.setDefaultValue(properties.get("jarfile").get());
			propertiesRow.setDefaultValue(properties.get("properties").get());
		}

		Collections.addAll(parentRows, jarRow, propertiesRow);

		return parentRows;
	}

	@Override
	@Nullable
	public String getParent() {
		return null;
	}
}