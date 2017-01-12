package sebe3012.servercontroller.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import org.jdom2.JDOMException;

import com.google.common.eventbus.Subscribe;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.ServerControllerPreferences;
import sebe3012.servercontroller.event.ChangeControlsEvent;
import sebe3012.servercontroller.event.ServerEditEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.gui.dialog.ServerDialog;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.save.ServerSave;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.server.monitoring.ChartsUpdater;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.NumberField;

public class FrameHandler implements IEventHandler {

	public static TabPane mainPane;
	public static ListView<BasicServer> list;
	public static VBox buttonList;
	public static String currentDesign;
	public static HashMap<String, String> designs;

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
		FrameHandler.mainPane.getTabs().forEach(tab -> {
			if (tab instanceof ServerTab) {
				((ServerTab) tab).getTabContent().getContentHandler().getServerHandler().onRestartClicked();
			}
		});
	}

	@FXML
	void onStartAllClicked() {
		FrameHandler.mainPane.getTabs().forEach(tab -> {
			if (tab instanceof ServerTab) {
				((ServerTab) tab).getTabContent().getContentHandler().getServerHandler().onStartClicked();
			}
		});
	}

	@FXML
	void onStopAllClicked() {
		FrameHandler.mainPane.getTabs().forEach(tab -> {
			if (tab instanceof ServerTab) {
				((ServerTab) tab).getTabContent().getContentHandler().getServerHandler().onEndClicked();
			}
		});
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
		fc.getExtensionFilters().add(new ExtensionFilter(".xml", "*.xml"));
		File f = fc.showSaveDialog(Frame.primaryStage);
		if (f == null) {
			return;
		}
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
		fc.getExtensionFilters().add(new ExtensionFilter(".xml", "*.xml"));
		File f = fc.showOpenDialog(Frame.primaryStage);
		if (f == null) {
			return;
		}
		try {
			ServerSave.loadServerController(f.getAbsolutePath());

		} catch (IllegalStateException e) {
			e.printStackTrace();
			showSaveStateErrorDialog();
		} catch (JDOMException | IOException | IllegalArgumentException | ReflectiveOperationException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}

	}

	@FXML
	void onServerEditItemClicked(ActionEvent event) {
		BasicServer server = Tabs.getCurrentServer();
		if (server != null) {
			if (server.isRunning()) {
				showServerIsRunningDialog();
			} else {
				EventHandler.EVENT_BUS.post(new ServerEditEvent(server.getPluginName(), server));
			}
		}
	}

	@FXML
	void onServerRemoveItemClicked(ActionEvent event) {
		Tabs.removeCurrentTab();
	}

	@FXML
	void onLicenseClicked(ActionEvent event) {
		showLicense();
	}

	@FXML
	void onDesignClicked(ActionEvent event) {

		ChoiceDialog<String> cd = new ChoiceDialog<>();
		cd.setGraphic(new ImageView(this.getClass().getResource("icon.png").toExternalForm()));
		cd.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);
		cd.setTitle("Design w‰hlen");
		cd.setHeaderText("Design des ServerController anpassen");
		cd.getItems().setAll(FrameHandler.designs.keySet());

		Optional<String> result = cd.showAndWait();

		if (result.isPresent()) {

			String name = result.get();

			ServerControllerPreferences.saveSetting(ServerControllerPreferences.Constants.KEY_DESIGN, name);

			Frame.primaryStage.getScene().getStylesheets().clear();
			Frame.primaryStage.getScene().getStylesheets().add(FrameHandler.designs.get(name));
			FrameHandler.currentDesign = FrameHandler.designs.get(name);

		}

	}

	@FXML
	void onRConClicked(ActionEvent event) {

		Dialog<Pair<String, Pair<Integer, char[]>>> loginDialog = new Dialog<>();

		loginDialog.setTitle("RCon Verbindung");
		loginDialog.setHeaderText("RCon Verbindungsinformation");
		loginDialog.setGraphic(new ImageView(this.getClass().getResource("icon.png").toExternalForm()));

		DialogPane dp = loginDialog.getDialogPane();
		ButtonType bt = new ButtonType("Login", ButtonData.OK_DONE);
		dp.getButtonTypes().add(bt);
		dp.getStylesheets().add(FrameHandler.currentDesign);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField ip = new TextField();
		ip.setPromptText("IP");
		NumberField port = new NumberField();
		port.setPromptText("Port");
		PasswordField password = new PasswordField();
		password.setPromptText("Passwort");

		grid.add(new Label("IP:"), 0, 0);
		grid.add(ip, 1, 0);
		grid.add(new Label("Port:"), 0, 1);
		grid.add(port, 1, 1);
		grid.add(new Label("Passwort:"), 0, 2);
		grid.add(password, 1, 2);
		dp.setContent(grid);
		loginDialog.setResultConverter(dialogButton -> {
			try {
				if (dialogButton == bt) {
					return new Pair<>(ip.getText(),
							new Pair<>(Integer.valueOf(port.getText()), password.getText().toCharArray()));
				}
			} catch (Exception e) {
				return null;
			}
			return null;
		});

		Optional<Pair<String, Pair<Integer, char[]>>> result = loginDialog.showAndWait();

		if (result.isPresent()) {

			if (result.get().getKey() != null && result.get().getValue() != null) {
				new RConConsole(result.get().getKey(), result.get().getValue().getKey(),
						result.get().getValue().getValue());
			}

		}
	}

	private void showCredits() {
		DialogUtil.showInformationAlert("‹ber", "",
				"ServerController by Sebastian Knackstedt (Sebe3012)\n© 2016 Germany");
	}

	private void showLicense() {

		Stage stage = new Stage(StageStyle.UTILITY);
		stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toExternalForm()));
		stage.setTitle("Lizenz");

		VBox root = new VBox();

		WebView wv = new WebView();
		WebEngine engine = wv.getEngine();

		engine.loadContent(ServerController.loadStringContent("sebe3012/servercontroller/gui/license.html"));

		root.getChildren().add(wv);

		Scene scene = new Scene(root);
		scene.getStylesheets().add(FrameHandler.currentDesign);

		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.setScene(scene);
		stage.showAndWait();
	}

	private void init() {

		designs = new HashMap<>();

		designs.put("Hell", this.getClass().getResource("style_bright.css").toExternalForm());
		designs.put("Dunkel", this.getClass().getResource("style_dark.css").toExternalForm());

		currentDesign = designs.get(ServerControllerPreferences
				.loadSetting(ServerControllerPreferences.Constants.KEY_DESIGN, designs.keySet().iterator().next()));

		EventHandler.EVENT_BUS.registerEventListener(this);

		vBox.getStyleClass().add("buttonlist");
		credits.setText(ServerController.VERSION);

		monitoringThread.setName("server-monitoring-thread-1");

		lView.getSelectionModel().selectedItemProperty().addListener((oberservable, oldValue, newValue) -> {
			FrameHandler.mainPane.getSelectionModel().select(lView.getSelectionModel().getSelectedIndex());
		});
		main.getSelectionModel().selectedItemProperty().addListener((oberservable, oldValue, newValue) -> {
			FrameHandler.list.getSelectionModel().select(main.getSelectionModel().getSelectedIndex());
			if (newValue instanceof ServerTab) {
				TabServerHandler handler = ((ServerTab) newValue).getTabContent().getContentHandler()
						.getServerHandler();
				if (handler.hasServer()) {
					EventHandler.EVENT_BUS.post(new ChangeControlsEvent(handler.getServer().getExtraControls()));
				}
			}
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

	private static void showServerIsRunningDialog() {
		DialogUtil.showWaringAlert("Warnung", "", "Der Server muﬂ erst gestoppt werden");
	}

	private static void showSaveErrorDialog() {
		DialogUtil.showErrorAlert("Fehler", "", "Es ist ein Fehler beim Laden/Speichern aufgetreten");
	}

	private static void showSaveStateErrorDialog() {
		DialogUtil.showErrorAlert("Fehler", "",
				"Die Datei ist nicht mit dieser Version des ServerControllers kompatibel");
	}

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
	public void changeExtraButton(ChangeControlsEvent event) {

		Platform.runLater(() -> {

			for (int i = 3; i < vBox.getChildren().size(); i++) {
				vBox.getChildren().remove(i);
			}

			event.getNewControls().forEach(control -> {
				control.setPrefWidth(1000);
				vBox.getChildren().add(control);
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

		ramUsed.setTitle("Genutzer RAM / Zugewiese\n(Ungenau)");
		ramTotal.setTitle("Genutzer RAM / Gesamt\n(Ungenau)");
		cpu.setTitle("Genutze CPU / 100%\n(Ungenau)");

		ramUsed.getData().add(ramUsed1);
		ramUsed.getData().add(assignedRam);
		ramTotal.getData().add(ramUsed2);
		ramTotal.getData().add(totalRam);
		cpu.getData().add(usedCpu);
		cpu.getData().add(totelCpu);
	}
}
