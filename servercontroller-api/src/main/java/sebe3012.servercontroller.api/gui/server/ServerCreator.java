package sebe3012.servercontroller.api.gui.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by Sebe3012 on 14.05.2017.
 * A interface for all addons that create a new server type
 */
public interface ServerCreator {

	@NotNull
	List<DialogRow> createServerDialogRows(@NotNull Map<String, StringProperty> properties, @NotNull List<DialogRow> parentRows, boolean useProperties);


	@Nullable
	String getParent();
}
