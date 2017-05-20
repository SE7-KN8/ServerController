package sebe3012.servercontroller.addon.spigot;

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

public class SpigotAddon extends Addon implements IEventHandler, ServerCreator {

	public void load() {
		AddonUtil.registerServerType(this, SpigotServer.class, this);
	}

	@Override
	public void unload() {
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