package sebe3012.servercontroller.addon.vanilla.dialog.properties;

import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.design.Designs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PropertiesDialog {

	public PropertiesDialog(Stage stage, PropertiesHandler handler, BasicServer server) {
		try {

			stage.setTitle(I18N.format("addon_vanilla_properties_title", server.getName()));

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/PropertiesDialog.fxml"), I18N.getDefaultBundle());
			loader.setController(new PropertiesDialogHandler(handler));

			GridPane root = loader.load();
			Scene scene = new Scene(root);
			Designs.applyCurrentDesign(scene);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
