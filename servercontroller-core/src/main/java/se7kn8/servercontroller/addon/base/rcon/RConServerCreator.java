package se7kn8.servercontroller.addon.base.rcon;

import se7kn8.servercontroller.api.gui.server.DialogRow;
import se7kn8.servercontroller.api.gui.server.ServerCreator;
import se7kn8.servercontroller.api.server.BasicServer;
import se7kn8.servercontroller.api.util.StringPredicates;
import se7kn8.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RConServerCreator implements ServerCreator {
	@NotNull
	@Override
	public List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties) {

		DialogRow ipRow = new DialogRow();
		ipRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);
		ipRow.setName(I18N.translate("dialog_rcon_ip"));
		ipRow.setPropertyName("rconIP");

		DialogRow portRow = new DialogRow();
		portRow.setStringPredicate(StringPredicates.IS_NUMERIC);
		portRow.setName(I18N.translate("dialog_rcon_port"));
		portRow.setPropertyName("rconPort");

		DialogRow passwordRow = new DialogRow();
		passwordRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);
		passwordRow.setName(I18N.translate("dialog_rcon_password"));
		passwordRow.setPropertyName("rconPassword");

		Collections.addAll(parentRows, ipRow, portRow, passwordRow);

		return parentRows;
	}

	@NotNull
	@Override
	public Class<? extends BasicServer> getServerClass() {
		return RConServer.class;
	}

	@NotNull
	@Override
	public String getID() {
		return "rcon-server";
	}

	@Nullable
	@Override
	public String getParent() {
		return "servercontroller-base:named-server";
	}
}
