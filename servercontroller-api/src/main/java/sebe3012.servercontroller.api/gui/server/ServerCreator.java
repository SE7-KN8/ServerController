package sebe3012.servercontroller.api.gui.server;

import sebe3012.servercontroller.api.server.BasicServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import java.util.List;
import java.util.Map;

/**
 * Created by Sebe3012 on 14.05.2017.
 * A interface for all addons that create a new server type
 */
public interface ServerCreator {

	@NotNull
	List<DialogRow> createServerDialogRows(@NotNull Map<String, StringProperty> properties, @NotNull List<DialogRow> parentRows, boolean useProperties);

	@NotNull
	Class<? extends BasicServer> getServerClass();

	@Nullable
	String getParent();

	@Nullable
	default Node getGraphic(){
		//Do nothing
		return null;
	}
}
