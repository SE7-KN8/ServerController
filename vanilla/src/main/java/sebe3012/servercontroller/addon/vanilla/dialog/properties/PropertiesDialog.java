package sebe3012.servercontroller.addon.vanilla.dialog.properties;

import sebe3012.servercontroller.addon.vanilla.VanillaAddon;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.util.design.Designs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PropertiesDialog {

	public PropertiesDialog(Stage stage, PropertiesHandler handler, BasicServer server) {
		try {

			stage.setTitle(String.format(VanillaAddon.bundle.getString("addon_vanilla_properties_title"), server.getName()));

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/PropertiesDialog.fxml"), VanillaAddon.bundle);
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
