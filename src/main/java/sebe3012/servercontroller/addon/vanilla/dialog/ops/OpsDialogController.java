
package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.util.I18N;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

import java.net.URL;
import java.util.Map;
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
	private Button saveBtn;

	@FXML
	private ListView<String> leftList;

	@FXML
	private ListView<String> rightList;

	private OpsHandler handler;
	private BasicServer server;

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

		int index = mainList.getSelectionModel().getSelectedIndex();

		if (index >= handler.getAllValues().size() || index < 0) {
			return;
		}

		Map<String, ?> map = handler.getAllValues().get(index);

		map.keySet().forEach(key -> left.add(key));
		map.values().forEach(o -> right.add(o.toString()));

		leftList.setItems(null);
		rightList.setItems(null);

		leftList.setItems(left);
		rightList.setItems(right);

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

		for (Map<String, ?> map : handler.getAllValues()) {
			main.add(map.get("name").toString());
		}
		mainList.setItems(main);

		MenuItem addOp = new MenuItem(I18N.translate("addon_vanilla_add_operator"));

		MenuItem removedOp = new MenuItem(I18N.translate("addon_vanilla_remove_operator"));

		addOp.setOnAction(e -> {

			TextInputDialog dialog = new TextInputDialog();
			dialog.setHeaderText(I18N.translate("addon_vanilla_add_operator"));
			dialog.setContentText(I18N.translate("addon_vanilla_player_name"));

			dialog.getDialogPane().getStylesheets().add(FrameHandler.currentDesign.getStylesheet());
			dialog.getDialogPane().setMaxSize(300, 45);
			dialog.getDialogPane().setPrefSize(300, 45);
			Optional<String> result = dialog.showAndWait();

			if (result.isPresent()) {
				server.getServerHandler().sendCommand("op " + result.get());
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
						for (Map<String, ?> map : handler.getAllValues()) {
							main.add(map.get("name").toString());
						}
						mainList.setItems(main);
					});
				}).start();
			}

		});

		removedOp.setOnAction(e -> {
			String player = mainList.getSelectionModel().getSelectedItem();
			server.getServerHandler().sendCommand("deop " + player);
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
					for (Map<String, ?> map : handler.getAllValues()) {
						main.add(map.get("name").toString());
					}
					mainList.setItems(main);
				});
			}).start();
		});

		ContextMenu menu = new ContextMenu(addOp, removedOp);
		mainList.setContextMenu(menu);

	}
}
