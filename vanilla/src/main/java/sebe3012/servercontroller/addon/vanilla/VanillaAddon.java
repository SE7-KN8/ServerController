package sebe3012.servercontroller.addon.vanilla;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.addon.api.DialogRow;
import sebe3012.servercontroller.addon.api.ServerCreator;
import sebe3012.servercontroller.addon.api.StringPredicates;
import sebe3012.servercontroller.eventbus.IEventHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VanillaAddon extends Addon implements IEventHandler, ServerCreator {

	@Override
	public void load() {
		AddonUtil.registerServerType(this, VanillaServer.class, this);
	}

	@Override
	public void unload() {
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