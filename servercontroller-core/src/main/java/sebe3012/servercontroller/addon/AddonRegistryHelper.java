package sebe3012.servercontroller.addon;

import sebe3012.servercontroller.addon.registry.Registry;
import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.fileeditor.FileEditorCreator;
import sebe3012.servercontroller.api.gui.server.ServerCreator;

import org.jetbrains.annotations.NotNull;

public class AddonRegistryHelper implements AddonRegistry {

	private Addon addon;
	private Registry<ServerCreator> serverCreatorRegistry;
	private Registry<FileEditorCreator> fileEditorRegistry;

	public AddonRegistryHelper(){
		serverCreatorRegistry = new Registry<>();
		fileEditorRegistry = new Registry<>();
	}

	public void setCurrentAddon(Addon addon) {
		this.addon = addon;
	}

	@Override
	public void registerServerType(@NotNull ServerCreator serverCreator) {
		serverCreatorRegistry.registerEntry(addon, serverCreator);
	}

	@Override
	public void unregisterServerType(@NotNull ServerCreator serverCreator) {
		serverCreatorRegistry.unregisterEntry(addon, serverCreator);
	}

	public Registry<ServerCreator> getServerCreatorRegistry() {
		return serverCreatorRegistry;
	}


	@Override
	public void registerFileEditor(@NotNull FileEditorCreator editorCreator) {
		fileEditorRegistry.registerEntry(addon, editorCreator);
	}

	@Override
	public void unregisterFileEditor(@NotNull FileEditorCreator editorCreator) {
		fileEditorRegistry.unregisterEntry(addon, editorCreator);
	}

	public Registry<FileEditorCreator> getFileEditorRegistry() {
		return fileEditorRegistry;
	}
}
