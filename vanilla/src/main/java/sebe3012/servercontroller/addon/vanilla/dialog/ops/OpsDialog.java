package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import sebe3012.servercontroller.addon.vanilla.VanillaAddon;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.util.design.Designs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.io.IOException;

public class OpsDialog {

	public OpsDialog(Stage stage, OpsHandler handler, BasicServer server) {
		try {
			stage.setTitle(String.format(VanillaAddon.bundle.getString("addon_vanilla_operators_title"), server.getName()));

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/OpsDialog.fxml"), VanillaAddon.bundle);
			loader.setController(new OpsDialogController(handler, server));

			SplitPane root = loader.load();
			Scene scene = new Scene(root);
			Designs.applyCurrentDesign(scene);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
