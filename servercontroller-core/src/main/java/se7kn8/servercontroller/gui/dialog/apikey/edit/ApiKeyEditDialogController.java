package se7kn8.servercontroller.gui.dialog.apikey.edit;

import se7kn8.servercontroller.rest.RestServer;
import se7kn8.servercontroller.rest.authentication.permission.Permission;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApiKeyEditDialogController {
	private RestServer server;
	private Stage stage;

	public ApiKeyEditDialogController(RestServer server, Stage stage) {
		this.server = server;
		this.stage = stage;
	}

	private boolean isFinished;

	@FXML
	private Button button;

	@FXML
	private ListView<String> listView;

	private List<String> checkedPermissions = new ArrayList<>();
	private List<String> permissions = new ArrayList<>();

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions.stream().map(Permission::getPermission).collect(Collectors.toList());
	}

	@FXML
	private void initialize() {
		listView.setItems(FXCollections.observableArrayList(server.getPermissions()));
		listView.setCellFactory(CheckBoxListCell.forListView(item -> {
			BooleanProperty observable = new SimpleBooleanProperty();
			observable.addListener((observable1, oldValue, newValue) -> {
				if (newValue) {
					checkedPermissions.add(item);
				} else {
					checkedPermissions.remove(item);
				}
			});
			if (permissions.contains(item)) {
				observable.set(true);
			}
			return observable;
		}));
	}

	@FXML
	private void onButtonClicked() {
		isFinished = true;
		stage.close();
	}

	public List<String> getCheckedPermissions() {
		return isFinished ? checkedPermissions : new ArrayList<>();
	}
}
