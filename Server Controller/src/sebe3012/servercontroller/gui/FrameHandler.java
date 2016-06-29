package sebe3012.servercontroller.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
import sebe3012.servercontroller.event.ChangeButtonsEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.gui.dialog.ServerDialog;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.save.ServerSave;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.server.monitoring.ChartsUpdater;

public class FrameHandler implements IEventHandler {

	public static TabPane mainPane;
	public static ListView<BasicServer> list;
	public static VBox buttonList;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private MenuBar mBar;

	@FXML
	private ListView<BasicServer> lView;

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

	@FXML
	private Button btnRestartAll;

	@FXML
	private Button btnStartAll;

	@FXML
	private Button btnStopAll;

	public static Thread monitoringThread = new Thread(new ChartsUpdater());

	@FXML
	void initialize() {
		init();
	}

	@FXML
	void onRestartAllClicked() {
		FrameHandler.mainPane.getTabs().forEach(tab->{
			if(tab instanceof ServerTab){
				((ServerTab)tab).getTabContent().getContentHandler().getServerHandler().onRestartClicked();
			}
		});
	}

	@FXML
	void onStartAllClicked() {
		FrameHandler.mainPane.getTabs().forEach(tab->{
			if(tab instanceof ServerTab){
				((ServerTab)tab).getTabContent().getContentHandler().getServerHandler().onStartClicked();
			}
		});
	}

	@FXML
	void onStopAllClicked() {
		FrameHandler.mainPane.getTabs().forEach(tab->{
			if(tab instanceof ServerTab){
				((ServerTab)tab).getTabContent().getContentHandler().getServerHandler().onEndClicked();
			}
		});;
	}

	@FXML
	void onOverItemClicked(ActionEvent event) {
		showCredits();
	}

	@FXML
	void onAddServerItemClicked(ActionEvent event) {
		ServerDialog.loadDialog();
	}

	@FXML
	void onSaveItemClicked(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter(".serversave", "*.serversave"));
		File f = fc.showSaveDialog(Frame.primaryStage);
		if (f == null) {
			return;
		}
		/*try {*/
			ServerSave.saveServerController(f.getAbsolutePath());
		/*} catch (IOException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}*/
	}

	@FXML
	void onOpenItemClicked(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter(".serversave", "*.serversave"));
		File f = fc.showOpenDialog(Frame.primaryStage);
		if (f == null) {
			return;
		}
		/*try {*/
			ServerSave.loadServerController(f.getAbsolutePath());
		/*} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}*/
	}

	@FXML
	void onServerEditItemClicked(ActionEvent event) {
		BasicServer server = Tabs.getCurrentServer();
		if (server.isRunning()) {
			showServerIsRunningDialog();
		} else {
			// TODO Use new dialog
		}
	}

	@FXML
	void onServerRemoveItemClicked(ActionEvent event) {
		BasicServer server = Tabs.getCurrentServer();
		if (server.isRunning()) {
			showServerIsRunningDialog();
		} else {
			
			Servers.serversList.remove(Tabs.getCurrentServer());
			
			lView.setItems(null);
			lView.setItems(Servers.serversList);
			main.getTabs().remove(Tabs.getCurrentTab());
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

		EventHandler.EVENT_BUS.registerEventListener(this);

		monitoringThread.setName("server-monitoring-thread-1");

		lView.getSelectionModel().selectedItemProperty().addListener((oberservable, oldValue, newValue) -> {
			FrameHandler.mainPane.getSelectionModel().select(lView.getSelectionModel().getSelectedIndex());
		});
		main.getSelectionModel().selectedItemProperty().addListener((oberservable, oldValue, newValue) -> {
			FrameHandler.list.getSelectionModel().select(main.getSelectionModel().getSelectedIndex());
		});

		lView.setCellFactory(e -> {
			return new ServerCell();
		});

		mainPane = main;
		list = lView;
		buttonList = vBox;
		lView.setItems(Servers.serversList);
		initCharts();
		System.out.println("FXML intitialized");
		monitoringThread.start();
	}

	private void showServerIsRunningDialog() {
		Alert dialog = new Alert(AlertType.WARNING, "Der Server mu� erst gestoppt werden", ButtonType.OK);
		dialog.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		dialog.setTitle("Fehler");
		dialog.setHeaderText("");
		dialog.showAndWait();
	}

	/*private void showSaveErrorDialog() {
		Alert dialog = new Alert(AlertType.ERROR, "Es ist ein Fehler bei der Eingabe/Ausgabe aufgetreten",
				ButtonType.OK);
		dialog.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		dialog.setTitle("Fehler");
		dialog.setHeaderText("");
		dialog.showAndWait();
	}*/

	private class ServerCell extends ListCell<BasicServer> {

		@Override
		protected void updateItem(BasicServer item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
				setText("");
				setGraphic(null);
			} else {
				setGraphic(null);
				setText(item.getName());
			}
		}

	}

	@Subscribe
	public void changeExtraButton(ChangeButtonsEvent event) {

		Platform.runLater(() -> {

			for (int i = 3; i < vBox.getChildren().size(); i++) {
				vBox.getChildren().remove(i);
			}

			event.getNewButtons().forEach((name, action) -> {

				Button newButton = new Button(name);
				newButton.setOnAction(e -> {
					action.run();
				});

				vBox.getChildren().add(newButton);
			});

		});

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

		ramUsed.setTitle("Genutzer RAM / Zugewiesen");
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
