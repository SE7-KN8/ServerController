package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.util.I18N;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.io.IOException;

public class OpsDialog {

	public OpsDialog(Stage stage, OpsHandler handler, BasicServer server) {
		try {
			stage.setTitle(I18N.format("addon_vanilla_operators_title", server.getName()));

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/OpsDialog.fxml"), I18N.getDefaultBundle());
			loader.setController(new OpsDialogController(handler, server));

			SplitPane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(FrameHandler.currentDesign.getStylesheet());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
