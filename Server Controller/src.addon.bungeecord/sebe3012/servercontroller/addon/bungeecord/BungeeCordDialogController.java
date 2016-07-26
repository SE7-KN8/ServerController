package sebe3012.servercontroller.addon.bungeecord;

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

public class BungeeCordDialogController {

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

	private Alert dialog;
	private HashMap<String, Object> extraValues;
	private boolean isEdit = false;

	@FXML
	void onJarClicked(ActionEvent event) {
		
		String path = AddonUtil.openFileChooser("*.jar", "Java-Archiv");
		
		fillValues(path);
		
		jarPathTextfield.setText(path);
	}

	@FXML
	void onPropertiesClicked(ActionEvent event) {
		propertiesTextfield.setText(AddonUtil.openFileChooser("*.yml", "BungeeCord-Config"));
	}

	@FXML
	void onConfirmClicked(ActionEvent event) {

		if (AddonUtil.checkUserInput(idTextfield.getText())) {
			if (AddonUtil.checkUserInput(jarPathTextfield.getText())) {
				if (AddonUtil.checkUserInput(propertiesTextfield.getText())) {
					dialog.close();

					BungeeCordServer server = new BungeeCordServer(idTextfield.getText(), jarPathTextfield.getText(),
							propertiesTextfield.getText(), argsTextfield.getText());

					AddonUtil.addServer(server, isEdit);

				} else {
					AddonUtil.openAlert("Fehler", "Config-Datei ist nicht ausgewählt", AlertType.WARNING);
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

		propertiesTextfield.setText(new File(baseDir, "config.yml").getAbsolutePath());

	}

	@FXML
	void initialize() {
		if (extraValues != null) {
			isEdit = true;
			idTextfield.setText((String) extraValues.get("name"));
			jarPathTextfield.setText((String) extraValues.get("jarfile"));
			argsTextfield.setText((String) extraValues.get("args"));
			propertiesTextfield.setText((String) extraValues.get("bungeecord"));
		}
	}

	public BungeeCordDialogController(Alert dialog, HashMap<String, Object> extraValues) {
		this.dialog = dialog;
		this.extraValues = extraValues;
	}

}