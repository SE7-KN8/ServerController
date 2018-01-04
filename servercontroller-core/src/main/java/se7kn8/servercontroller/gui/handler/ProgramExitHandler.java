package se7kn8.servercontroller.gui.handler;

import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.api.gui.tab.TabEntry;
import se7kn8.servercontroller.api.gui.tab.TabHandler;
import se7kn8.servercontroller.api.server.BasicServerHandler;
import se7kn8.servercontroller.api.util.DialogUtil;
import se7kn8.servercontroller.util.I18N;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class ProgramExitHandler implements EventHandler<WindowEvent> {

	private TabHandler<TabEntry<BasicServerHandler>> rootHandler;

	public ProgramExitHandler(TabHandler<TabEntry<BasicServerHandler>> rootHandler) {
		this.rootHandler = rootHandler;
	}

	@Override
	public void handle(WindowEvent event) {
		if (ServerController.DEBUG) {
			rootHandler.getTabEntries().forEach(TabEntry::onClose);
			ServerController.stop();
			Platform.exit();
		} else {
			// Close Dialog
			Optional<Boolean> shouldExit = DialogUtil.showRequestAlert(I18N.translate("dialog_close"), I18N.translate("dialog_close_desc"));

			if (shouldExit.isPresent()) {
				if (shouldExit.get()) {
					rootHandler.getTabEntries().forEach(TabEntry::onClose);
					ServerController.stop();
					Platform.exit();
				} else {
					event.consume();
				}
			}
		}
	}
}
