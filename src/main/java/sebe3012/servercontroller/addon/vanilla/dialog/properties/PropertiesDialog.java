package sebe3012.servercontroller.addon.vanilla.dialog.properties;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.util.I18N;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PropertiesDialog {

	public PropertiesDialog(Stage stage, PropertiesHandler handler, BasicServer server) {
		try {

			stage.setTitle(I18N.format("addon_vanilla_properties_title", server.getName()));

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/PropertiesDialog.fxml"), I18N.getBundle());
			loader.setController(new PropertiesDialogHandler(handler));

			GridPane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(FrameHandler.currentDesign.getStylesheet());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
