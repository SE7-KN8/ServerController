package sebe3012.servercontroller.addon.craftbukkit;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.addon.api.DialogRow;
import sebe3012.servercontroller.addon.api.ServerCreator;
import sebe3012.servercontroller.addon.api.StringPredicates;
import sebe3012.servercontroller.eventbus.IEventHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Map;

public class CraftbukkitAddon extends Addon implements IEventHandler, ServerCreator {

	@Override
	public void load() {
		AddonUtil.registerServerType(this, CraftbukkitServer.class, this);
	}

	@Override
	public void unload() {

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