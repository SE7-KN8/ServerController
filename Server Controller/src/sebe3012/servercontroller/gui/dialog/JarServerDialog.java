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
	public static String ram;
	public static boolean useDefault = true;
	public static boolean isOpen = false;

	public JarServerDialog(Stage stage) {
		if (!isOpen) {
			JarServerDialog.stage = stage;

			try {
				Pane root = FXMLLoader.load(this.getClass().getResource("Dialog.fxml"));
				root.getStyleClass().add("pane");
				Scene scene = new Scene(root);
				scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
				stage.setOnCloseRequest(e -> {
					isOpen = false;
				});
				stage.setScene(scene);
				stage.setTitle("Server erstellen");
				stage.getIcons().add(new Image(Frame.class.getResource("icon.png").toExternalForm()));
				stage.setMinWidth(600);
				stage.setMinHeight(500);
				JarServerDialog.isOpen = true;
				stage.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JarServerDialog.stage.requestFocus();
		}

	}

	public JarServerDialog(Stage stage, String jar, String properties, String name, String ram) {
		if (!isOpen) {
			JarServerDialog.useDefault = false;
			JarServerDialog.jarPath = jar;
			JarServerDialog.propertiesPath = properties;
			JarServerDialog.name = name;
			JarServerDialog.ram = ram;
			new JarServerDialog(stage);
		} else {
			JarServerDialog.stage.requestFocus();
		}
	}

}
