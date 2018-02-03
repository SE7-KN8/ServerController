package se7kn8.servercontroller.api.gui.server;

import se7kn8.servercontroller.api.server.BasicServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;

import java.util.List;
import java.util.Map;

/**
 * Created by se7kn8 on 14.05.2017.
 * A interface for all addons that create a new server type
 */
public interface ServerCreator {

	@NotNull
	List<DialogRow> createServerDialogRows(@Nullable Map<String, String> properties, @NotNull List<DialogRow> parentRows, boolean useProperties);

	@NotNull
	Class<? extends BasicServer> getServerClass();

	@NotNull
	String getID();

	@Nullable
	default String getParent() {
		//No parent
		return null;
	}

	@Nullable
	default Node getGraphic() {
		//Do nothing
		return null;
	}

	default boolean isStandaloneServer(){
		return true;
	}

}
