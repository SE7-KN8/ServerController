package sebe3012.servercontroller.addon.craftbukkit;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.server.CLIServer;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CraftBukkitAddon extends Addon implements ServerCreator {

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
		return CraftBukkitServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "craftbukkit";
	}

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {

		DialogRow bukkitConfigRow = new DialogRow();
		bukkitConfigRow.setName("CraftBukkit-Config");
		bukkitConfigRow.setUsingFileChooser(true);
		bukkitConfigRow.setFileExtension("*.yml");
		bukkitConfigRow.setFileType("YML");
		bukkitConfigRow.setPropertyName("craftBukkitConfig");
		bukkitConfigRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties && properties != null) {
			bukkitConfigRow.setDefaultValue(properties.get("craftBukkitConfig"));
		}
		parentRows.add(bukkitConfigRow);

		return parentRows;
	}

	@Nullable
	@Override
	public String getParent() {
		return "vanilla:vanilla";
	}
}