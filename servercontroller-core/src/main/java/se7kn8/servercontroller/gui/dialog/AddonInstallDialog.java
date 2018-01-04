package se7kn8.servercontroller.gui.dialog;

import se7kn8.servercontroller.addon.AddonLoader;
import se7kn8.servercontroller.api.gui.dialog.StageDialog;
import se7kn8.servercontroller.api.util.DialogUtil;
import se7kn8.servercontroller.api.util.FileUtil;
import se7kn8.servercontroller.api.util.design.Designs;
import se7kn8.servercontroller.util.I18N;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AddonInstallDialog extends StageDialog {

	public AddonInstallDialog() {
		super(I18N.translate("menu_item_addon_install"));
	}

	@Override
	public Scene createDialog() {
		BorderPane root = new BorderPane();

		Button openBtn = new Button(I18N.translate("file_choose"));
		openBtn.setOnAction(event -> {
			String file = FileUtil.openFileChooser("*.jar", "JAR");

			if (file != null && file.endsWith(".jar")) {

				Path addonPath = Paths.get(file);
				try {
					Files.createDirectories(AddonLoader.ADDON_TEMP_PATH);
					Files.copy(addonPath, AddonLoader.ADDON_TEMP_PATH.resolve(addonPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					DialogUtil.showInformationAlert("", I18N.format("successful_addon_copy", addonPath.getFileName().toString()));
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
							Files.createDirectories(AddonLoader.ADDON_TEMP_PATH);
							Files.copy(addonPath, AddonLoader.ADDON_TEMP_PATH.resolve(addonPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							DialogUtil.showInformationAlert("", I18N.format("successful_addon_copy", addonPath.getFileName().toString()));
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

		return scene;
	}
}
