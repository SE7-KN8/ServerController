package sebe3012.servercontroller.gui.dialog;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sebe3012.servercontroller.gui.Frame;

public class JarServerDialog {

	public static Stage stage;
	public static String jarPath;
	public static String propertiesPath;
	public static String name;
	public static boolean useDefault = true;

	public JarServerDialog(Stage stage) {

		JarServerDialog.stage = stage;

		try {
			Pane root = FXMLLoader.load(this.getClass().getResource("Dialog.fxml"));
			root.getStyleClass().add("pane");
			Scene scene = new Scene(root);
			scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Server erstellen");
			stage.getIcons().add(new Image(Frame.class.getResource("icon.png").toExternalForm()));
			stage.setResizable(false);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public JarServerDialog(Stage stage, String jar, String properties, String name) {
		JarServerDialog.useDefault = false;
		JarServerDialog.jarPath = jar;
		JarServerDialog.propertiesPath = properties;
		JarServerDialog.name = name;
		new JarServerDialog(stage);
	}

}
