
package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import sebe3012.servercontroller.api.util.design.Designs;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.util.I18N;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class OpsDialogController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ListView<String> mainList;

	@FXML
	private ListView<String> leftList;

	@FXML
	private ListView<String> rightList;

	private OpsHandler handler;
	private BasicServer server;

	private ObservableList<String> main;
	private ObservableList<String> left;
	private ObservableList<String> right;

	public void changedMain(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		left.clear();
		right.clear();

		int index = newValue.intValue();

		if (index >= handler.getAllValues().size() || index < 0) {
			return;
		}

		Operator op = handler.getAllValues().get(index);

		left.addAll("uuid", "name", "level", "bypassesPlayerLimit");
		right.addAll(op.getUuid(), op.getName(), String.valueOf(op.getLevel()), String.valueOf(op.isBypassesPlayerLimit()));

		leftList.setItems(null);
		rightList.setItems(null);

		leftList.setItems(left);
		rightList.setItems(right);
	}

	public void changedLeft(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		rightList.getSelectionModel().select(leftList.getSelectionModel().getSelectedIndex());
	}

	public void changedRight(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		leftList.getSelectionModel().select(rightList.getSelectionModel().getSelectedIndex());
	}

	public OpsDialogController(OpsHandler handler, BasicServer server) {
		this.handler = handler;
		this.server = server;
	}

	@FXML
	void initialize() {

		handler.readOps();

		mainList.setItems(null);

		main = FXCollections.observableArrayList();
		left = FXCollections.observableArrayList();
		right = FXCollections.observableArrayList();

		for (Operator op : handler.getAllValues()) {
			main.add(op.getName());
		}
		mainList.setItems(main);
		mainList.getSelectionModel().selectedIndexProperty().addListener(this::changedMain);
		leftList.getSelectionModel().selectedIndexProperty().addListener(this::changedLeft);
		rightList.getSelectionModel().selectedIndexProperty().addListener(this::changedRight);

		MenuItem addOp = new MenuItem(I18N.translate("addon_vanilla_add_operator"));

		MenuItem removedOp = new MenuItem(I18N.translate("addon_vanilla_remove_operator"));

		addOp.setOnAction(e -> {

			TextInputDialog dialog = new TextInputDialog();
			dialog.setHeaderText(I18N.translate("addon_vanilla_add_operator"));
			dialog.setContentText(I18N.translate("addon_vanilla_player_name"));

			Designs.applyCurrentDesign(dialog);
			dialog.getDialogPane().setMaxSize(300, 45);
			dialog.getDialogPane().setPrefSize(300, 45);
			Optional<String> result = dialog.showAndWait();

			if (result.isPresent()) {
				server.sendCommand("op " + result.get());
				new Thread(() -> {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					handler.readOps();

					Platform.runLater(() -> {
						mainList.setItems(null);
						main.clear();
						for (Operator op : handler.getAllValues()) {
							main.add(op.getName());
						}
						mainList.setItems(main);
					});
				}).start();
			}

		});

		removedOp.setOnAction(e -> {
			String player = mainList.getSelectionModel().getSelectedItem();
			server.sendCommand("deop " + player);
			new Thread(() -> {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				handler.readOps();

				Platform.runLater(() -> {
					mainList.setItems(null);
					main.clear();
					for (Operator op : handler.getAllValues()) {
						main.add(op.getName());
					}
					mainList.setItems(main);
				});
			}).start();
		});

		ContextMenu menu = new ContextMenu(addOp, removedOp);
		mainList.setContextMenu(menu);

	}
}
