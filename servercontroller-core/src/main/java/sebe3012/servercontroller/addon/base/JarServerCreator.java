package sebe3012.servercontroller.addon.base;

import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.server.JarServer;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class JarServerCreator implements ServerCreator {

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow jarRow = new DialogRow();
		jarRow.setName("Jar");
		jarRow.setUsingFileChooser(true);
		jarRow.setFileExtension("*.jar");
		jarRow.setFileType("JAR-ARCHIVE");
		jarRow.setPropertyName("jarPath");
		jarRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (useProperties && properties != null) {
			jarRow.setDefaultValue(properties.get("jarPath"));
		}

		parentRows.add(jarRow);

		return parentRows;
	}

	@NotNull
	@Override
	public Class<? extends BasicServer> getServerClass() {
		return JarServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "jar-server";
	}

	@Nullable
	@Override
	public String getParent() {
		return "servercontroller-base:cli-server";
	}

	@Override
	public boolean isStandaloneServer() {
		return false;
	}
}
