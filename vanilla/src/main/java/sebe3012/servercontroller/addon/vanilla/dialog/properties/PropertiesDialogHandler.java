package sebe3012.servercontroller.addon.vanilla.dialog.properties;

import sebe3012.servercontroller.addon.vanilla.VanillaAddon;
import sebe3012.servercontroller.api.util.DialogUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PropertiesDialogHandler {

	private PropertiesHandler handler;

	public PropertiesDialogHandler(PropertiesHandler handler) {
		this.handler = handler;
	}

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ListView<String> lProperties;

	@FXML
	private ListView<String> rProperties;

	@FXML
	private Button btnSave;

	@FXML
	void initialize() {
		lProperties.setEditable(true);
		rProperties.setEditable(true);
		rProperties.setCellFactory(e -> new TextFieldListCell<>(new StringConverter<String>() {
					@Override
					public String toString(String object) {
						return object;
					}

					@Override
					public String fromString(String string) {
						return string;
					}
				})
		);
		ObservableList<String> keys = FXCollections.observableArrayList(handler.getAllValues().keySet());
		ObservableList<String> values = FXCollections.observableArrayList(handler.getAllValues().values());
		lProperties.setItems(keys);
		rProperties.setItems(values);
	}

	@FXML
	void onLViewClicked() {
		rProperties.getSelectionModel().select(lProperties.getSelectionModel().getSelectedIndex());
	}

	@FXML
	void onRViewClicked() {
		lProperties.getSelectionModel().select(rProperties.getSelectionModel().getSelectedIndex());
	}

	@FXML
	void onSaveClicked() {
		try {
			Path file = Paths.get(handler.getProperitesFile().toURI());
			List<String> lines = new ArrayList<>();
			for (int i = 0; i < lProperties.getItems().size(); i++) {
				lines.add(lProperties.getItems().get(i) + "=" + rProperties.getItems().get(i));
			}
			Files.write(file, lines, Charset.forName("UTF-8"));

			DialogUtil.showInformationAlert("", VanillaAddon.bundle.getString("addon_vanilla_properties_save_information"));
		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.showErrorAlert("", VanillaAddon.bundle.getString("addon_vanilla_properties_save_error"));
		}

	}
}