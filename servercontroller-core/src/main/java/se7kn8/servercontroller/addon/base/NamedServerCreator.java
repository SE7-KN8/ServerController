package se7kn8.servercontroller.addon.base;

import se7kn8.servercontroller.api.gui.server.DialogRow;
import se7kn8.servercontroller.api.gui.server.ServerCreator;
import se7kn8.servercontroller.api.server.BasicServer;
import se7kn8.servercontroller.api.server.NamedServer;
import se7kn8.servercontroller.api.util.StringPredicates;
import se7kn8.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class NamedServerCreator implements ServerCreator {

	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {
		DialogRow nameRow = new DialogRow();

		nameRow.setName(I18N.translate("dialog_create_server_name"));
		nameRow.setPropertyName("name");
		nameRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		parentRows.add(nameRow);

		if(useProperties && properties != null){
			nameRow.setDefaultValue(properties.get("name"));
		}

		return parentRows;
	}

	@NotNull
	@Override
	public Class<? extends BasicServer> getServerClass() {
		return NamedServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "named-server";
	}

	@Override
	public boolean isStandaloneServer() {
		return false;
	}
}
