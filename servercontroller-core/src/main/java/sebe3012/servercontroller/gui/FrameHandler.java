package sebe3012.servercontroller.gui;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.Addons;
import sebe3012.servercontroller.api.gui.dialog.Dialog;
import sebe3012.servercontroller.api.gui.tab.TabEntry;
import sebe3012.servercontroller.api.gui.tab.TabHandler;
import sebe3012.servercontroller.api.gui.tree.TreeEntry;
import sebe3012.servercontroller.api.gui.tree.TreeHandler;
import sebe3012.servercontroller.api.preferences.ServerControllerPreferences;
import sebe3012.servercontroller.api.server.BasicServerHandler;
import sebe3012.servercontroller.api.util.design.Design;
import sebe3012.servercontroller.api.util.design.Designs;
import sebe3012.servercontroller.gui.dialog.AddonInstallDialog;
import sebe3012.servercontroller.gui.dialog.CreditsDialog;
import sebe3012.servercontroller.gui.dialog.LicenseDialog;
import sebe3012.servercontroller.gui.dialog.ServerDialog;
import sebe3012.servercontroller.gui.dialog.SettingsDialog;
import sebe3012.servercontroller.gui.handler.ProgramExitHandler;
import sebe3012.servercontroller.gui.tree.RootTreeEntry;
import sebe3012.servercontroller.save.ServerSave;
import sebe3012.servercontroller.server.ServerManager;
import sebe3012.servercontroller.util.GUIUtil;
import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FrameHandler {

	private Dialog addonInstallDialog;
	private Dialog creditsDialog;
	private Dialog licenseDialog;
	private Dialog settingsDialog;
	private Dialog serverDialog;

	public static ProgressBar currentProgress;
	public static VBox buttonList;
	//FIXME public static Thread monitoringThread;

	private TabHandler<TabEntry<BasicServerHandler>> handler;
	private TreeHandler<TreeEntry<?>> treeHandler;

	private ServerManager serverManager;

	private static final Logger log = LogManager.getLogger();

	private Stage primaryStage;

	public FrameHandler(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private MenuBar mBar;

	@FXML
	private TreeView<TreeEntry<?>> lView;

	@FXML
	private Label credits;

	@FXML
	private VBox vBox;

	@FXML
	private TabPane rootTabPane;

	@FXML
	private ToolBar toolbar;

	@FXML
	private ProgressBar progressBar;

	@FXML
	void initialize() {
		init();
	}

	@FXML
	void onSettingsClicked() {
		settingsDialog.showDialog();
	}

	@FXML
	void onCreditsItemClicked() {
		creditsDialog.showDialog();
	}

	@FXML
	void onAddServerItemClicked() {
		serverDialog.showDialog();
	}

	@FXML
	void onSaveItemClicked() {
		ServerSave.saveServerController(serverManager);
	}

	@FXML
	void onOpenItemClicked() {
		ServerSave.loadServerController(serverManager);
	}

	@FXML
	void onServerEditItemClicked() {
		serverManager.editSelectedServer();
	}

	@FXML
	void onServerRemoveItemClicked() {
		serverManager.removeSelectedServer();
	}

	@FXML
	void onDesignClicked() {
		Designs.showDesignDialog(primaryStage);
	}

	@FXML
	void onAddonInstallClicked() {
		addonInstallDialog.showDialog();
	}

	private void init() {
		Designs.registerDesign(new Design("css/bright", "bright", I18N.getDefaultBundle()));
		Designs.registerDesign(new Design("css/dark", "dark", I18N.getDefaultBundle()));
		Designs.registerDesign(new Design("css/bright-rework", "bright-rework", I18N.getDefaultBundle()));

		handler = new TabHandler<>("RootHandler", rootTabPane);

		TreeItem<TreeEntry<?>> rootItem = new TreeItem<>(new RootTreeEntry(I18N.translate("tree_servers")));

		treeHandler = new TreeHandler<>(lView, rootItem, false);

		serverManager = new ServerManager(handler, treeHandler, Addons.getAddonLoader().getRegistryHelper());

		this.primaryStage.setOnCloseRequest(new ProgramExitHandler(handler));

		String designID = ServerControllerPreferences.loadSetting(Designs.SAVE_KEY, Designs.getDefaultDesign().getId());

		Designs.setCurrentDesign(designID);

		vBox.getStyleClass().add("button-tree");
		credits.setText(ServerController.VERSION);

		currentProgress = progressBar;
		FrameHandler.hideBar();

		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/add.png").toExternalForm(), e -> serverDialog.showDialog(), I18N.translate("tooltip_add_server"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/remove.png").toExternalForm(), e -> serverManager.removeSelectedServer(), I18N.translate("tooltip_remove_server"));
		GUIUtil.addSeparatorToToolbar(toolbar);
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/start_all.png").toExternalForm(), e -> serverManager.startAllServers(), I18N.translate("tooltip_start_all_servers"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/restart_all.png").toExternalForm(), e -> serverManager.restartAllServers(), I18N.translate("tooltip_restart_all_servers"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/stop_all.png").toExternalForm(), e -> serverManager.stopAllServers(), I18N.translate("tooltip_stop_all_servers"));
		GUIUtil.addSeparatorToToolbar(toolbar);
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/start.png").toExternalForm(), e -> serverManager.startSelectedServer(), I18N.translate("tooltip_start_server"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/restart.png").toExternalForm(), e -> serverManager.restartSelectedServer(), I18N.translate("tooltip_restart_server"));
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/stop.png").toExternalForm(), e -> serverManager.stopSelectedServer(), I18N.translate("tooltip_stop_server"));
		GUIUtil.addSeparatorToToolbar(toolbar);
		GUIUtil.addButtonToToolbar(toolbar, ClassLoader.getSystemResource("png/toolbar/edit.png").toExternalForm(), e -> serverManager.editSelectedServer(), I18N.translate("tooltip_edit_server"));

		buttonList = vBox;

		/*FIXME initCharts();
		monitoringThread = new Thread(new ServerWatcher());
		monitoringThread.setName("Server Monitoring Thread");
		monitoringThread.start();*/


		serverDialog = new ServerDialog(serverManager);
		settingsDialog = new SettingsDialog();
		licenseDialog = new LicenseDialog();
		creditsDialog = new CreditsDialog();
		addonInstallDialog = new AddonInstallDialog();

		log.info("FXML initialized");
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	public static void showBar() {
		log.debug("Showing the progress bar");
		Platform.runLater(() -> FrameHandler.currentProgress.setVisible(true));
	}

	public static void hideBar() {
		log.debug("Hiding the progress bar");
		Platform.runLater(() -> FrameHandler.currentProgress.setVisible(false));
	}
/*FIXME Too many bugs
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
	}*/
}
