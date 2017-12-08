package sebe3012.servercontroller.gui.handler;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.gui.tab.TabEntry;
import sebe3012.servercontroller.gui.tab.TabHandler;
import sebe3012.servercontroller.server.BasicServerHandler;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.I18N;

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
			Optional<Boolean> shouldExit = DialogUtil.showRequestAlert("", I18N.translate("dialog_close"), I18N.translate("dialog_close_desc"));

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
