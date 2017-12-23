package sebe3012.servercontroller.api.addon;

import sebe3012.servercontroller.api.gui.fileeditor.FileEditor;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.server.BasicServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;

import java.util.List;
import java.util.ResourceBundle;

public interface AddonRegistry {

	void registerServerType(@NotNull Class<? extends BasicServer> serverClass, @NotNull ServerCreator serverCreator);
	void unregisterServerType(@NotNull Class<? extends BasicServer> serverClass);

	void registerFileEditor(@NotNull List<String> fileTypes, @NotNull Class<? extends FileEditor> editor, @NotNull String name, @NotNull ResourceBundle bundle, @Nullable Node graphic);
	void unregisterFileEditor(@NotNull List<String> fileTypes);
}