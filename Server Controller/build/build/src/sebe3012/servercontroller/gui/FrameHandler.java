package sebe3012.servercontroller.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class FrameHandler {

	public static TabPane mainPane;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private MenuBar mBar;

	@FXML
	private ListView<?> lView;

	@FXML
	private Label credits;

	@FXML
	private VBox vBox;

	@FXML
	private TabPane main;

	@FXML
	private MenuItem over;

	@FXML
	void initialize() {
		mainPane = main;
		System.out.println("FXML intitialize");
	}

	@FXML
	void onOverItemClicked(ActionEvent event) {
		Alert credits = new Alert(AlertType.INFORMATION,
				"ServerController by Sebastian Knackstedt (Sebe3012)\n© 31.01.2015 Germany", ButtonType.OK);
		credits.setTitle("Über");
		credits.setHeaderText("");
		credits.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		credits.showAndWait();
	}
}
