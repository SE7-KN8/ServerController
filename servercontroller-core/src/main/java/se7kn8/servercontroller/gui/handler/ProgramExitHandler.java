package se7kn8.servercontroller.gui.handler;

import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.api.gui.tab.TabEntry;
import se7kn8.servercontroller.api.util.DialogUtil;
import se7kn8.servercontroller.rest.RestServer;
import se7kn8.servercontroller.server.ServerManager;
import se7kn8.servercontroller.util.I18N;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class ProgramExitHandler implements EventHandler<WindowEvent> {

	private ServerManager manager;
	private RestServer server;

	public ProgramExitHandler(ServerManager manager, RestServer server) {
		this.manager = manager;
		this.server = server;
	}

	@Override
	public void handle(WindowEvent event) {
		if (ServerController.DEBUG) {
			close();
		} else {
			// Close Dialog
			Optional<Boolean> shouldExit = DialogUtil.showRequestAlert(I18N.translate("dialog_close"), I18N.translate("dialog_close_desc"));

			if (shouldExit.isPresent()) {
				if (shouldExit.get()) {
					close();
				} else {
					event.consume();
				}
			}
		}
	}

	private void close(){
		server.stop();
		manager.getTabHandler().getTabEntries().forEach(TabEntry::onClose);
		ServerController.stop();
		Platform.exit();
	}
}
