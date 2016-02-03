package sebe3012.servercontroller.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import sebe3012.servercontroller.gui.dialog.BatchServerDialog;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BatchServer;
import sebe3012.servercontroller.server.Servers;

public class FrameHandler {

	public static TabPane mainPane;
	public static ListView<BatchServer> list;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private MenuBar mBar;

	@FXML
	private ListView<BatchServer> lView;

	@FXML
	private Label credits;

	@FXML
	private VBox vBox;

	@FXML
	private TabPane main;

	@FXML
	private MenuItem over;

	@FXML
	private MenuItem addServer;

	@FXML
	void initialize() {
		lView.setCellFactory(new Callback<ListView<BatchServer>, ListCell<BatchServer>>() {

			@Override
			public ListCell<BatchServer> call(ListView<BatchServer> param) {
				return new ServerCell();
			}
		});
		lView.setOnMouseClicked(event -> {
			BatchServer bs = lView.getSelectionModel().getSelectedItem();
			Tabs.servers.forEach((id, server) -> {
				if (server.getServer().equals(bs)) {
					main.getSelectionModel().select(main.getTabs().get(id));
				}
			});
		});
		mainPane = main;
		list = lView;
		lView.setItems(Servers.servers);
		System.out.println("FXML intitialize");
	}

	@FXML
	void onOverItemClicked(ActionEvent event) {
		Alert credits = new Alert(AlertType.INFORMATION,
				"ServerController by Sebastian Knackstedt (Sebe3012)\n© 2016 Germany", ButtonType.OK);
		credits.setTitle("Über");
		credits.setHeaderText("");
		credits.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		credits.showAndWait();
	}

	@FXML
	void onAddServerItemClicked(ActionEvent event) {
		BatchServerDialog bd = new BatchServerDialog(new Stage());
		System.out.println(bd);
	}

	private class ServerCell extends ListCell<BatchServer> {
		@Override
		protected void updateItem(BatchServer item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
			} else {
				setText(item.getName());
			}
		}
	}
}
