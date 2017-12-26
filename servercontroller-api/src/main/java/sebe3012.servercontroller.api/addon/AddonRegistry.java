package sebe3012.servercontroller.api.addon;

import sebe3012.servercontroller.api.gui.fileeditor.FileEditorCreator;
import sebe3012.servercontroller.api.gui.server.ServerCreator;

import org.jetbrains.annotations.NotNull;

public interface AddonRegistry {

	void registerServerType(@NotNull ServerCreator serverCreator);

	void unregisterServerType(@NotNull ServerCreator serverCreator);

	void registerFileEditor(@NotNull FileEditorCreator editor);

	void unregisterFileEditor(@NotNull FileEditorCreator editor);
}