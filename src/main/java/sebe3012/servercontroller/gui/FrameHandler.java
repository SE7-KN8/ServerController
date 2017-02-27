package sebe3012.servercontroller.gui;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.event.ChangeControlsEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.gui.dialog.ServerDialog;
import sebe3012.servercontroller.gui.dialog.SettingsDialog;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.preferences.PreferencesConstants;
import sebe3012.servercontroller.preferences.ServerControllerPreferences;
import sebe3012.servercontroller.save.ServerSave;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.ServerState;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.server.monitoring.ServerWatcher;
import sebe3012.servercontroller.settings.SettingsConstants;
import sebe3012.servercontroller.util.Design;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.GUIUtil;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.NumberField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;

import com.google.common.eventbus.Subscribe;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FrameHandler implements IEventHandler {

	public static TabPane mainPane;
	public static ListView<BasicServer> list;
	public static VBox buttonList;
	public static Design currentDesign;
	public static List<Design> designs;
	public static Thread monitoringThread;

	private static final Logger log = LogManager.getLogger();

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
	private ToolBar toolbar;

	@FXML
	void initialize() {
		init();
	}

	@FXML
	void onRestartAllClicked() {
		Servers.restartAllServers();
	}

	@FXML
	void onStartAllClicked() {
		Servers.startAllServers();
	}

	@FXML
	void onStopAllClicked() {
		Servers.stopAllServers();
	}

	@FXML
	void onSettingsClicked() {
		new SettingsDialog().show();
	}

	@FXML
	void onCreditsItemClicked(ActionEvent event) {
		DialogUtil.showInformationAlert(I18N.translate("menu_item_credits"), "", I18N.format("credits", ServerController.VERSION));
	}

	@FXML
	void onAddServerItemClicked(ActionEvent event) {
		ServerDialog.loadDialog();
	}

	@FXML
	void onSaveItemClicked(ActionEvent event) {
		String file = AddonUtil.openFileChooser("*.xml", ".xml");

		try {
			ServerSave.saveServerController(file, true);
		} catch (IOException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}
	}

	@FXML
	void onOpenItemClicked(ActionEvent event) {
		String file = AddonUtil.openFileChooser("*.xml", ".xml");

		try {
			ServerSave.loadServerController(file, true);

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
		Servers.editCurrentServer();
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
		ChoiceDialog<Design> cd = new ChoiceDialog<>();
		cd.setGraphic(new ImageView(ClassLoader.getSystemResource("png/icon.png").toExternalForm()));
		cd.getDialogPane().getStylesheets().add(FrameHandler.currentDesign.getStylesheet());
		cd.setTitle(I18N.translate("dialog_choose_design"));
		cd.setHeaderText(I18N.translate("dialog_choose_design_desc"));
		cd.getItems().setAll(FrameHandler.designs);

		Optional<Design> result = cd.showAndWait();

		if (result.isPresent()) {

			Design design = result.get();

			ServerControllerPreferences.saveSetting(PreferencesConstants.KEY_DESIGN, design.getId());

			Frame.primaryStage.getScene().getStylesheets().clear();
			Frame.primaryStage.getScene().getStylesheets().add(design.getStylesheet());
			FrameHandler.currentDesign = design;

		}

	}

	@FXML
	void onRConClicked(ActionEvent event) {

		Dialog<Pair<String, Pair<Integer, char[]>>> loginDialog = new Dialog<>();

		loginDialog.setTitle(I18N.translate("dialog_rcon"));
		loginDialog.setHeaderText(I18N.translate("dialog_rcon_desc"));
		loginDialog.setGraphic(new ImageView(ClassLoader.getSystemClassLoader().getResource("png/icon.png").toExternalForm()));

		DialogPane dp = loginDialog.getDialogPane();
		ButtonType bt = new ButtonType(I18N.translate("dialog_rcon_login"), ButtonData.OK_DONE);
		dp.getButtonTypes().add(bt);
		dp.getStylesheets().add(FrameHandler.currentDesign.getStylesheet());
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField ip = new TextField();
		ip.setPromptText(I18N.translate("dialog_rcon_ip"));
		NumberField port = new NumberField();
		port.setPromptText(I18N.translate("dialog_rcon_port"));
		PasswordField password = new PasswordField();
		password.setPromptText(I18N.translate("dialog_rcon_password"));

		grid.add(new Label(I18N.translate("dialog_rcon_ip")), 0, 0);
		grid.add(ip, 1, 0);
		grid.add(new Label(I18N.translate("dialog_rcon_port")), 0, 1);
		grid.add(port, 1, 1);
		grid.add(new Label(I18N.translate("dialog_rcon_password")), 0, 2);
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

	private void showLicense() {

		Stage stage = new Stage(StageStyle.UTILITY);
		stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
		stage.setTitle(I18N.translate("menu_item_license"));

		VBox root = new VBox();

		WebView wv = new WebView();
		WebEngine engine = wv.getEngine();

		engine.loadContent(ServerController.loadStringContent("html/license.html"));

		root.getChildren().add(wv);

		Scene scene = new Scene(root);
		scene.getStylesheets().add(FrameHandler.currentDesign.getStylesheet());

		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.setScene(scene);
		stage.showAndWait();
	}

	private void init() {

		designs = new ArrayList<>();

		designs.add(new Design(ClassLoader.getSystemResource("css/style_bright.css").toExternalForm(), "bright"));
		designs.add(new Design(ClassLoader.getSystemResource("css/style_dark.css").toExternalForm(), "dark"));

		lView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> mainPane.getSelectionModel().select(newValue.intValue()));
		main.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> lView.getSelectionModel().select(newValue.intValue()));

		String designID = ServerControllerPreferences.loadSetting(PreferencesConstants.KEY_DESIGN, designs.iterator().next().getId());

		for (Design d : designs) {
			if (d.getId().equals(designID)) {
				currentDesign = d;
				break;
			}
		}

		EventHandler.EVENT_BUS.registerEventListener(this);

		vBox.getStyleClass().add("button-list");
		credits.setText(ServerController.VERSION);

		main.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			FrameHandler.this.dataCounterRam.set(0);
			FrameHandler.this.dataCounterCpu.set(0);

			if (newValue instanceof ServerTab) {
				TabServerHandler handler = ((ServerTab) newValue).getTabContent().getContentHandler()
						.getServerHandler();
				if (handler.hasServer()) {
					EventHandler.EVENT_BUS.post(new ChangeControlsEvent(handler.getServer().getExtraControls()));
				}
			}
		});

		lView.setCellFactory(e -> new ServerCell());

		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/add.png").toExternalForm(), e -> ServerDialog.loadDialog(), I18N.translate("tooltip_add_server"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/remove.png").toExternalForm(), e -> Tabs.removeCurrentTab(), I18N.translate("tooltip_remove_server"));
		GUIUtil.addSeparatorToToolbar(toolbar);
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/start_all.png").toExternalForm(), e-> Servers.startAllServers(), I18N.translate("tooltip_start_all_servers"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/restart_all.png").toExternalForm(), e-> Servers.startAllServers(), I18N.translate("tooltip_restart_all_servers"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/stop_all.png").toExternalForm(), e-> Servers.startAllServers(), I18N.translate("tooltip_stop_all_servers"));
		GUIUtil.addSeparatorToToolbar(toolbar);
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/start.png").toExternalForm(), e -> Servers.startCurrentServer(), I18N.translate("tooltip_start_server"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/restart.png").toExternalForm(), e -> Servers.restartCurrentServer(), I18N.translate("tooltip_restart_server"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/stop.png").toExternalForm(), e -> Servers.stopCurrentServer(), I18N.translate("tooltip_stop_server"));
		GUIUtil.addSeparatorToToolbar(toolbar);
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/edit.png").toExternalForm(), e -> Servers.editCurrentServer(), I18N.translate("tooltip_edit_server"));

		mainPane = main;
		list = lView;
		buttonList = vBox;
		lView.setItems(Servers.serversList);
		initCharts();

		monitoringThread = new Thread(new ServerWatcher());
		monitoringThread.setName("Server Monitoring Thread");
		monitoringThread.start();


		log.info("FXML initialized");


		if (ServerController.settings.get(SettingsConstants.AUTO_LOAD_SERVERS).getValue()) {
			new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Platform.runLater(() -> {
					try {
						ServerSave.loadServerController(ServerControllerPreferences.loadSetting(PreferencesConstants.LAST_SERVERS, null), false);
					} catch (IllegalStateException e) {
						e.printStackTrace();
						showSaveStateErrorDialog();
					} catch (JDOMException | IOException | IllegalArgumentException | ReflectiveOperationException e) {
						e.printStackTrace();
						showSaveErrorDialog();
					}
				});
			}).start();
		}
	}

	private static void showSaveErrorDialog() {
		DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_save_error"));
	}

	private static void showSaveStateErrorDialog() {
		DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_wrong_save_version"));
	}

	private class ServerCell extends ListCell<BasicServer> {

		@Override
		protected void updateItem(BasicServer item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null || empty) {
				setText("");
				setGraphic(null);
			} else {

				Circle c = new Circle(10, ServerState.getColor(item.getState()));

				setGraphic(c);
				setText(item.getName());
			}

		}

	}

	@Subscribe
	public void changeExtraButton(ChangeControlsEvent event) {
		Platform.runLater(() -> {

			vBox.getChildren().clear();

			event.getNewControls().forEach(control -> {
				control.setPrefWidth(1000);
				vBox.getChildren().add(control);
			});

		});

	}

	@FXML
	private VBox leftBox;

	private NumberAxis ramAxis;
	private NumberAxis cpuAxis;

	private AreaChart.Series<Number, Number> ramSeries;
	private AreaChart.Series<Number, Number> cpuSeries;

	private int maxValues = 10;
	private IntegerProperty dataCounterRam = new SimpleIntegerProperty(0);
	private IntegerProperty dataCounterCpu = new SimpleIntegerProperty(0);

	public static ConcurrentLinkedQueue<Number> ramData = new ConcurrentLinkedQueue<>();
	public static ConcurrentLinkedQueue<Number> cpuData = new ConcurrentLinkedQueue<>();

	private void initCharts() {

		ramAxis = new NumberAxis(0, maxValues, 1);
		ramAxis.setForceZeroInRange(false);
		ramAxis.setAutoRanging(false);

		cpuAxis = new NumberAxis(0, maxValues, 1);
		cpuAxis.setForceZeroInRange(false);
		cpuAxis.setAutoRanging(false);

		NumberAxis ramAxisY = new NumberAxis();
		ramAxisY.setAutoRanging(true);
		ramAxisY.setLowerBound(0);
		ramAxisY.setUpperBound(100);
		ramAxisY.setTickUnit(20);

		NumberAxis cpuAxisY = new NumberAxis();
		cpuAxisY.setAutoRanging(true);
		cpuAxisY.setLowerBound(0);
		cpuAxisY.setUpperBound(100);
		cpuAxisY.setTickUnit(20);

		AreaChart<Number, Number> ram = new AreaChart<Number, Number>(ramAxis, ramAxisY) {
			@Override
			protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
			}
		};

		AreaChart<Number, Number> cpu = new AreaChart<Number, Number>(cpuAxis, cpuAxisY) {
			@Override
			protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
			}
		};

		ram.setAnimated(false);
		cpu.setAnimated(false);

		ram.setTitle("RAM");
		cpu.setTitle("CPU");

		ramSeries = new AreaChart.Series<>();
		cpuSeries = new AreaChart.Series<>();

		ramSeries.setName(I18N.translate("ram_usage"));
		cpuSeries.setName(I18N.translate("cpu_usage"));

		ram.getData().add(ramSeries);
		cpu.getData().add(cpuSeries);

		leftBox.getChildren().add(cpu);
		leftBox.getChildren().add(ram);

		new AnimationTimer() {

			@Override
			public void handle(long now) {
				addDataToSeries(ramData, ramSeries, ramAxis, dataCounterRam);
			}
		}.start();

		new AnimationTimer() {

			@Override
			public void handle(long now) {
				addDataToSeries(cpuData, cpuSeries, cpuAxis, dataCounterCpu);
			}
		}.start();
	}

	private void addDataToSeries(ConcurrentLinkedQueue<Number> queue, AreaChart.Series<Number, Number> series, NumberAxis axis, IntegerProperty dataCounter) {
		for (int i = 0; i < 20; i++) {
			if (queue.isEmpty()) break;
			series.getData().add(new AreaChart.Data<>(dataCounter.get(), queue.remove()));
			dataCounter.set(dataCounter.get() + 1);
		}
		if (series.getData().size() > maxValues) {
			series.getData().remove(0, series.getData().size() - maxValues);
		}

		axis.setLowerBound(dataCounter.get() - maxValues);
		axis.setUpperBound(dataCounter.get() - 1);
	}
}
