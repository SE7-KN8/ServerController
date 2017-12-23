package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Map;

public class SpigotAddon extends Addon implements ServerCreator {

	public void load(AddonRegistry registry) {
		registry.registerServerType(SpigotServer.class, this);
	}

	@Override
	public void unload(AddonRegistry registry) {
		registry.unregisterServerType(SpigotServer.class);
	}

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@NotNull Map<String, StringProperty> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {

		DialogRow spigotConfigRow = new DialogRow();
		spigotConfigRow.setName("Spigot-Config");
		spigotConfigRow.setUsingFileChooser(true);
		spigotConfigRow.setFileExtension("*.yml");
		spigotConfigRow.setFileType("YML");
		spigotConfigRow.setPropertyName("spigot");
		spigotConfigRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties) {
			spigotConfigRow.setDefaultValue(properties.get("spigot").get());
		}
		parentRows.add(spigotConfigRow);

		return parentRows;
	}

	@Nullable
	@Override
	public String getParent() {
		return "craftbukkit";
	}
}