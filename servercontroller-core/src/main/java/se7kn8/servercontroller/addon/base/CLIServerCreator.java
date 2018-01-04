package se7kn8.servercontroller.addon.base;

import se7kn8.servercontroller.api.gui.server.DialogRow;
import se7kn8.servercontroller.api.gui.server.ServerCreator;
import se7kn8.servercontroller.api.server.BasicServer;
import se7kn8.servercontroller.api.server.CLIServer;
import se7kn8.servercontroller.api.util.StringPredicates;
import se7kn8.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CLIServerCreator implements ServerCreator {
	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow argsRow = new DialogRow();
		argsRow.setName(I18N.translate("dialog_create_server_args"));
		argsRow.setPropertyName("args");
		argsRow.setStringPredicate(StringPredicates.DO_NOTHING);

		if (useProperties && properties != null){
			argsRow.setDefaultValue(properties.get("args"));
		}

		parentRows.add(argsRow);

		return parentRows;
	}

	@NotNull
	@Override
	public Class<? extends BasicServer> getServerClass() {
		return CLIServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "cli-server";
	}

	@Nullable
	@Override
	public String getParent() {
		return "servercontroller-base:named-server";
	}

	@Override
	public boolean isStandaloneServer() {
		return false;
	}
}
