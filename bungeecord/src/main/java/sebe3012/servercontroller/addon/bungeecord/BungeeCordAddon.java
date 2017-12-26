package sebe3012.servercontroller.addon.bungeecord;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BungeeCordAddon extends Addon implements ServerCreator {

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
		return BungeeCordServer.class;
	}

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@NotNull Map<String, StringProperty> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow jarRow = new DialogRow();
		jarRow.setName("Jar-Pfad");
		jarRow.setUsingFileChooser(true);
		jarRow.setFileExtension("*.jar");
		jarRow.setFileType("JAR-ARCHIVE");
		jarRow.setPropertyName("jar");
		jarRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		DialogRow configRow = new DialogRow();
		configRow.setName("Config-Pfad");
		configRow.setUsingFileChooser(true);
		configRow.setFileExtension("*.yml");
		configRow.setFileType("YML");
		configRow.setPropertyName("bungeecord");
		configRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties) {
			jarRow.setDefaultValue(properties.get("jar").get());
			configRow.setDefaultValue(properties.get("bungeecord").get());
		}

		Collections.addAll(parentRows, jarRow, configRow);

		return parentRows;
	}

	@Nullable
	@Override
	public String getParent() {
		return null;
	}
}