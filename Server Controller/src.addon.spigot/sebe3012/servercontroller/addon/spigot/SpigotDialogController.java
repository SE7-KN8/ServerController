package sebe3012.servercontroller.addon.spigot;

import java.io.File;
import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

import sebe3012.servercontroller.addon.AddonUtil;

public class SpigotDialogController {

	@FXML
	private GridPane IdLabel;

	@FXML
	private Label headerLabel;

	@FXML
	private Label idLabel;

	@FXML
	private TextField idTextfield;

	@FXML
	private TextField jarPathTextfield;

	@FXML
	private TextField propertiesTextfield;

	@FXML
	private TextField argsTextfield;

	@FXML
	private Label idJar;

	@FXML
	private Label idProperties;

	@FXML
	private Label idArgs;

	@FXML
	private Button jarButton;

	@FXML
	private Button propertiesButton;

	@FXML
	private Button confirmButton;

	@FXML
	private TextField idBukkit;

	@FXML
	private Button buttonBukkit;

	@FXML
	private TextField idSpigot;

	@FXML
	private Button spigotButton;

	private Alert dialog;
	private HashMap<String, Object> extraValues;
	private boolean isEdit = false;

	@FXML
	void onJarClicked(ActionEvent event) {
		
		String path = AddonUtil.openFileChooser("*.jar", "Java-Archiv");
		
		jarPathTextfield.setText(path);
		
		fillValues(path);
	}

	@FXML
	void onPropertiesClicked(ActionEvent event) {
		propertiesTextfield.setText(AddonUtil.openFileChooser("*.properties", "PROPERTIES"));
	}

	@FXML
	void onBukkitClicked(ActionEvent event) {
		idBukkit.setText(AddonUtil.openFileChooser("*.yml", "Bukkit-Config"));
	}

	@FXML
	void onSpigotClicked(ActionEvent event) {
		idSpigot.setText(AddonUtil.openFileChooser("*.yml", "Spigot-Config"));
	}

	@FXML
	void onConfirmClicked(ActionEvent event) {
		if (AddonUtil.checkUserInput(idTextfield.getText())) {
			if (AddonUtil.checkUserInput(jarPathTextfield.getText())) {
				if (AddonUtil.checkUserInput(propertiesTextfield.getText())) {
					if (AddonUtil.checkUserInput(idBukkit.getText())) {
						if (AddonUtil.checkUserInput(idSpigot.getText())) {
							dialog.close();

							SpigotServer server = new SpigotServer(idTextfield.getText(), jarPathTextfield.getText(),
									propertiesTextfield.getText(), argsTextfield.getText(), idBukkit.getText(),
									idSpigot.getText());

							AddonUtil.addServer(server, isEdit);
						} else {
							AddonUtil.openAlert("Fehler", "Spigot-Datei ist nicht ausgewählt", AlertType.WARNING);
						}
					} else {
						AddonUtil.openAlert("Fehler", "Bukkit-Datei ist nicht ausgewählt", AlertType.WARNING);
					}
				} else {
					AddonUtil.openAlert("Fehler", "Properties-Datei ist nicht ausgewählt", AlertType.WARNING);
				}
			} else {
				AddonUtil.openAlert("Fehler", "Java-Datei ist nicht ausgewählt", AlertType.WARNING);
			}
		} else {
			AddonUtil.openAlert("Fehler", "Server-ID ist nicht vorhanden", AlertType.WARNING);
		}
	}

	private void fillValues(String baseFile) {

		File baseDir = new File(baseFile).getParentFile();

		propertiesTextfield.setText(new File(baseDir, "server.properties").getAbsolutePath());
		idBukkit.setText(new File(baseDir, "bukkit.yml").getAbsolutePath());
		idSpigot.setText(new File(baseFile, "spigot.yml").getAbsolutePath());

	}

	@FXML
	void initialize() {
		if (extraValues != null) {
			isEdit = true;
			idTextfield.setText((String) extraValues.get("name"));
			jarPathTextfield.setText((String) extraValues.get("jarfile"));
			argsTextfield.setText((String) extraValues.get("args"));
			propertiesTextfield.setText((String) extraValues.get("properties"));
			idBukkit.setText((String) extraValues.get("bukkit"));
			idSpigot.setText((String) extraValues.get("spigot"));
		}
	}

	public SpigotDialogController(Alert dialog, HashMap<String, Object> extraValues) {
		this.dialog = dialog;
		this.extraValues = extraValues;
	}

}