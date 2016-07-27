package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class OpsDialogController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ListView<String> mainList;

	@FXML
	private Button saveBtn;

	@FXML
	private ListView<String> leftList;

	@FXML
	private ListView<String> rightList;

	private OpsHandler handler;

	private ObservableList<String> main;
	private ObservableList<String> left;
	private ObservableList<String> right;

	@FXML
	void onLeftClicked() {
		rightList.getSelectionModel().select(leftList.getSelectionModel().getSelectedIndex());
	}

	@FXML
	void onRightClicked() {
		leftList.getSelectionModel().select(rightList.getSelectionModel().getSelectedIndex());
	}

	@FXML
	void onMiddleClicked() {
		left.clear();
		right.clear();

		Map<String, ?> map = handler.getAllValues().get(mainList.getSelectionModel().getSelectedIndex());

		map.keySet().forEach(key -> {
			left.add(key);
		});

		map.values().forEach(o -> {
			right.add(o.toString());
		});

		leftList.setItems(null);
		rightList.setItems(null);

		leftList.setItems(left);
		rightList.setItems(right);

	}

	public OpsDialogController(OpsHandler handler) {
		this.handler = handler;
	}

	@FXML
	void initialize() {

		main = FXCollections.observableArrayList();
		left = FXCollections.observableArrayList();
		right = FXCollections.observableArrayList();

		for (Map<String, ?> map : handler.getAllValues()) {
			main.add(map.get("name").toString());
		}
		mainList.setItems(main);

	}
}
