package sebe3012.servercontroller.addon.base;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.addon.AddonRegistry;
import sebe3012.servercontroller.api.gui.fileeditor.FileEditorCreator;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.gui.editor.BasicImageViewer;
import sebe3012.servercontroller.gui.editor.BasicTextEditor;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")//Loaded by addon loader
public class BaseAddon extends Addon {

	private FileEditorCreator basicTextEditorCreator = new BasicTextEditor.BasicTextEditorCreator();
	private FileEditorCreator basicImageViewerCreator = new BasicImageViewer.BasicImageViewerCreator();

	private ServerCreator namedServerCreator = new NamedServerCreator();
	private ServerCreator cliServerCreator = new CLIServerCreator();
	private ServerCreator jarServerCreator = new JarServerCreator();

	@Override
	protected void load(@NotNull AddonRegistry registry) {
		registry.registerFileEditor(basicTextEditorCreator);
		registry.registerFileEditor(basicImageViewerCreator);

		registry.registerServerType(namedServerCreator);
		registry.registerServerType(cliServerCreator);
		registry.registerServerType(jarServerCreator);
	}

	@Override
	protected void unload(@NotNull AddonRegistry registry) {
		registry.unregisterFileEditor(basicTextEditorCreator);
		registry.unregisterFileEditor(basicImageViewerCreator);

		registry.unregisterServerType(jarServerCreator);
		registry.unregisterServerType(cliServerCreator);
		registry.unregisterServerType(namedServerCreator);
	}

}
