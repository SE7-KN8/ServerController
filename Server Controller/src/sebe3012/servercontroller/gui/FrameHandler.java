package sebe3012.servercontroller.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import sebe3012.servercontroller.gui.dialog.JarServerDialog;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.save.ServerSave;
import sebe3012.servercontroller.server.JarServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.server.monitoring.ChartsUpdater;

public class FrameHandler {

	public static TabPane mainPane;
	public static ListView<JarServer> list;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private MenuBar mBar;

	@FXML
	private ListView<JarServer> lView;

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
	private MenuItem saveItem;

	@FXML
	private MenuItem openItem;

	public static Thread monitoringThread = new Thread(new ChartsUpdater());

	@FXML
	void initialize() {
		init();
	}

	@FXML
	void onOverItemClicked(ActionEvent event) {
		showCredits();
	}

	@FXML
	void onAddServerItemClicked(ActionEvent event) {
		new JarServerDialog(new Stage());
	}

	@FXML
	void onSaveItemClicked(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter(".serversave", "*.serversave"));
		File f = fc.showSaveDialog(Frame.primaryStage);
		try {
			ServerSave.saveServerController(f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}
	}

	@FXML
	void onOpenItemClicked(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter(".serversave", "*.serversave"));
		File f = fc.showOpenDialog(Frame.primaryStage);
		try {
			ServerSave.loadServerController(f.getAbsolutePath());
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}
	}

	@FXML
	void onServerEditItemClicked(ActionEvent event) {
		ServerTab tab = (ServerTab) mainPane.getSelectionModel().getSelectedItem();
		JarServer js = Tabs.servers.get(tab.getTabContent().getId()).getServer();
		if (js.isRunning()) {
			showServerIsRunningDialog();
		} else {
			new JarServerDialog(new Stage(), js.getJarFile().getAbsolutePath(),
					js.getPropertiesFile().getAbsolutePath(), js.getName());
		}
	}

	@FXML
	void onServerRemoveItemClicked(ActionEvent event) {
		ServerTab tab = (ServerTab) mainPane.getSelectionModel().getSelectedItem();
		JarServer bs = Tabs.servers.get(tab.getTabContent().getId()).getServer();
		if (bs.isRunning()) {
			showServerIsRunningDialog();
		} else {
			Servers.servers.remove(bs);
			mainPane.getTabs().remove(mainPane.getSelectionModel().getSelectedItem());
		}

	}

	private void showCredits() {
		Alert credits = new Alert(AlertType.INFORMATION,
				"ServerController by Sebastian Knackstedt (Sebe3012)\n� 2016 Germany", ButtonType.OK);
		credits.setTitle("�ber");
		credits.setHeaderText("");
		credits.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		credits.showAndWait();
	}

	private void init() {
		monitoringThread.setName("server-monitoring-thread-1");
		lView.setCellFactory(e -> {
			return new ServerCell();
		});
		lView.setOnMouseClicked(event -> {
			JarServer bs = lView.getSelectionModel().getSelectedItem();
			Tabs.servers.forEach((id, server) -> {
				if (server.getServer().equals(bs)) {
					main.getSelectionModel().select(main.getTabs().get(id));
				}
			});
		});
		mainPane = main;
		list = lView;
		lView.setItems(Servers.servers);
		initCharts();
		System.out.println("[GUI] FXML intitialize");
		monitoringThread.start();
	}

	private void showServerIsRunningDialog() {
		Alert dialog = new Alert(AlertType.WARNING, "Der Server mu� erst gestoppt werden", ButtonType.OK);
		dialog.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		dialog.setTitle("Fehler");
		dialog.setHeaderText("");
		dialog.showAndWait();
	}

	private void showSaveErrorDialog() {
		Alert dialog = new Alert(AlertType.ERROR, "Es ist ein Fehler bei der Eingabe/Ausgabe aufgetreten",
				ButtonType.OK);
		dialog.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		dialog.setTitle("Fehler");
		dialog.setHeaderText("");
		dialog.showAndWait();
	}

	private class ServerCell extends ListCell<JarServer> {

		@Override
		protected void updateItem(JarServer item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
			} else {
				setText(item.getName());
			}
		}

	}

	@FXML
	private PieChart ramUsed;

	@FXML
	private PieChart ramTotal;

	@FXML
	private PieChart cpu;

	public static PieChart cpuChart;
	public static PieChart ramTotelChart;
	public static PieChart ramUsedChart;

	public static PieChart.Data ramUsed1 = new PieChart.Data("Genutzt", 1.0);
	public static PieChart.Data ramUsed2 = new PieChart.Data("Genutzt", 1.0);
	public static PieChart.Data assignedRam = new PieChart.Data("Zugewiesen", 1.0);
	public static PieChart.Data totalRam = new PieChart.Data("Gesamt", 1.0);
	public static PieChart.Data totelCpu = new PieChart.Data("100%", 1.0);
	public static PieChart.Data usedCpu = new PieChart.Data("Genutzt", 1.0);

	private void initCharts() {

		FrameHandler.cpuChart = cpu;
		FrameHandler.ramTotelChart = ramTotal;
		FrameHandler.ramUsedChart = ramUsed;

		ramUsed.setTitle("Genutzer RAM / Zugewiesen\n(Ungenutzt)");
		ramTotal.setTitle("Genutzer RAM / Gesamt");
		cpu.setTitle("Genutze CPU / 100%");

		ramUsed.getData().add(ramUsed1);
		ramUsed.getData().add(assignedRam);
		ramTotal.getData().add(ramUsed2);
		ramTotal.getData().add(totalRam);
		cpu.getData().add(usedCpu);
		cpu.getData().add(totelCpu);
	}
}
