package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Map;

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
	public Class<? extends BasicServer> getServerClass() {
		return SpigotServer.class;
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