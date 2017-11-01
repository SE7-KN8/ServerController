package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.addon.AddonLoader;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.FileUtil;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.design.Designs;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AddonInstallDialog {

	public static void showDialog(){
		Stage stage = new Stage(StageStyle.UTILITY);
		stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
		stage.setTitle(I18N.translate("menu_item_addon_install"));

		BorderPane root = new BorderPane();

		Button openBtn = new Button(I18N.translate("file_choose"));
		openBtn.setOnAction(event -> {
			String file = FileUtil.openFileChooser("*.jar", "JAR");

			if (file != null && file.endsWith(".jar")) {

				Path addonPath = Paths.get(file);
				try {
					Files.copy(addonPath, AddonLoader.ADDON_PATH.resolve(addonPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					DialogUtil.showInformationAlert(I18N.translate("dialog_information"), "", I18N.format("successful_file_copy", addonPath.getFileName().toString()));
				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}

		});

		root.setCenter(new Label(I18N.translate("dialog_addon_install_drag_and_drop"), openBtn));

		Scene scene = new Scene(root);
		Designs.applyCurrentDesign(scene);

		scene.setOnDragOver(event -> {
			if (event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		});

		scene.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();

			if (dragboard.hasFiles()) {
				event.setDropCompleted(true);

				for (File f : dragboard.getFiles()) {

					if (f.getAbsolutePath().endsWith(".jar")) {

						Path addonPath = f.toPath();
						try {
							Files.copy(addonPath, AddonLoader.ADDON_PATH.resolve(addonPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							DialogUtil.showInformationAlert(I18N.translate("dialog_information"), "", I18N.format("successful_file_copy", addonPath.getFileName().toString()));
						} catch (IOException ex) {
							ex.printStackTrace();
						}

					}

				}

			} else {
				event.setDropCompleted(false);
			}

			event.consume();

		});

		root.setPrefWidth(600);
		root.setPrefHeight(400);

		stage.setAlwaysOnTop(true);
		stage.setScene(scene);
		stage.showAndWait();
	}

}
