package sebe3012.servercontroller.addon.vanilla.dialog;

import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.addon.vanilla.VanillaServer;
import sebe3012.servercontroller.util.DialogUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.HashMap;

public class VanillaDialogController {

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

		String text = AddonUtil.openFileChooser("*.jar", "Java-Archiv");

		fillValues(text);

		jarPathTextfield.setText(text);
	}

	@FXML
	void onPropertiesClicked(ActionEvent event) {
		propertiesTextfield.setText(AddonUtil.openFileChooser("*.properties", "PROPERTIES"));
	}

	@FXML
	void onConfirmClicked(ActionEvent event) {

		if (AddonUtil.checkUserInput(idTextfield.getText())) {
			if (AddonUtil.checkUserInput(jarPathTextfield.getText())) {
				if (AddonUtil.checkUserInput(propertiesTextfield.getText())) {
					dialog.close();

					VanillaServer server = new VanillaServer(idTextfield.getText(), jarPathTextfield.getText(),
							propertiesTextfield.getText(), argsTextfield.getText());

					AddonUtil.addServer(server, isEdit);

				} else {
					DialogUtil.showErrorAlert("Fehler", "", "Properties-Datei ist nicht ausgewählt");
				}
			} else {
				DialogUtil.showErrorAlert("Fehler", "", "Java-Datei ist nicht ausgewählt");
			}
		} else {
			DialogUtil.showErrorAlert("Fehler", "", "Server-ID ist nicht vorhanden");
		}
	}

	private void fillValues(String baseFile) {

		File baseDir = new File(baseFile).getParentFile();

		propertiesTextfield.setText(new File(baseDir, "server.properties").getAbsolutePath());

	}

	@FXML
	void initialize() {
		if (extraValues != null) {
			isEdit = true;
			idTextfield.setText((String) extraValues.get("name"));
			jarPathTextfield.setText((String) extraValues.get("jarfile"));
			argsTextfield.setText((String) extraValues.get("args"));
			propertiesTextfield.setText((String) extraValues.get("properties"));
		}
	}

	public VanillaDialogController(Alert dialog, HashMap<String, Object> extraValues) {
		this.dialog = dialog;
		this.extraValues = extraValues;
	}

}