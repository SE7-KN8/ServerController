package sebe3012.servercontroller.addon;

import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.fileeditor.FileEditor;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.server.BasicServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;

import java.util.List;
import java.util.ResourceBundle;

public class AddonRegistryHelper implements AddonRegistry {

	private Addon addon;

	public void setCurrentAddon(Addon addon) {
		this.addon = addon;
	}

	@Override
	public void registerServerType(@NotNull Class<? extends BasicServer> serverClass, @NotNull ServerCreator serverCreator) {
		AddonUtil.registerServerType(addon, serverClass, serverCreator);
	}

	@Override
	public void registerFileEditor(@NotNull List<String> fileTypes, @NotNull Class<? extends FileEditor> editor, @NotNull String name, @NotNull ResourceBundle bundle, @Nullable Node graphic) {
		AddonUtil.registerFileEditor(fileTypes, editor, name, bundle, graphic);
	}

	@Override
	public void unregisterServerType(@NotNull Class<? extends BasicServer> serverClass) {
		//TODO implement
	}

	@Override
	public void unregisterFileEditor(@NotNull List<String> fileTypes) {
		//TODO implement
	}
}
