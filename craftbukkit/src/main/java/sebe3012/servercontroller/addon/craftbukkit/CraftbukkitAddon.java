package sebe3012.servercontroller.addon.craftbukkit;

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

public class CraftbukkitAddon extends Addon implements ServerCreator {

	@Override
	public void load(AddonRegistry registry) {
		registry.registerServerType(CraftbukkitServer.class, this);
	}

	@Override
	public void unload(AddonRegistry registry) {
		registry.unregisterServerType(CraftbukkitServer.class);
	}

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@NotNull Map<String, StringProperty> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {

		DialogRow bukkitConfigRow = new DialogRow();
		bukkitConfigRow.setName("Bukkit-Config");
		bukkitConfigRow.setUsingFileChooser(true);
		bukkitConfigRow.setFileExtension("*.yml");
		bukkitConfigRow.setFileType("YML");
		bukkitConfigRow.setPropertyName("bukkit");
		bukkitConfigRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties) {
			bukkitConfigRow.setDefaultValue(properties.get("bukkit").get());
		}
		parentRows.add(bukkitConfigRow);

		return parentRows;
	}

	@Nullable
	@Override
	public String getParent() {
		return "vanilla";
	}
}