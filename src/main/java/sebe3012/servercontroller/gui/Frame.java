package sebe3012.servercontroller.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.server.monitoring.ChartsUpdater;
import sebe3012.servercontroller.server.monitoring.ServerMonitoring;

import java.io.IOException;
import java.util.Optional;

/**
 * The class contruct the basic frame and starts the splashscreen
 *
 * @author Sebastian Knackstedt
 */
public class Frame extends Application {

	/**
	 * The basic frame for the programm
	 */
	public static Stage primaryStage;

	private Stage splash;

	private int splashTimeInMillis = 5000;

	@Override
	public void start(Stage primaryStage) throws Exception {

		if (ServerController.DEBUG) {
			splashTimeInMillis = 0;
		}

		Frame.primaryStage = primaryStage;

		createSplashScreen();
		createPrimaryStage();

		Platform.runLater(() -> {
			try {
				Thread.sleep(splashTimeInMillis);
			} catch (Exception e) {
				e.printStackTrace();
			}
			splash.close();
			primaryStage.show();
		});

	}

	private void createSplashScreen() {
		Stage splash = new Stage(StageStyle.UNDECORATED);
		this.splash = splash;
		Group root = new Group();

		root.resize(800, 600);

		Rectangle image = new Rectangle(800, 600);

		image.setFill(new ImagePattern(new Image(ClassLoader.getSystemResourceAsStream("png/splash.png"))));

		root.getChildren().add(image);

		splash.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
		splash.setScene(new Scene(root));
		splash.show();
	}

	private void createPrimaryStage() throws IOException {
		BorderPane root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/BaseFrame.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(FrameHandler.currentDesign);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Minecraft Servercontroller by Sebe3012          " + ServerController.VERSION);
		primaryStage.setMaximized(true);
		primaryStage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
		primaryStage.setOnCloseRequest(event -> {

			// Close Dialog
			Alert dialog = new Alert(AlertType.CONFIRMATION, "Wollen sie wirklich beenden?", ButtonType.OK,
					ButtonType.CANCEL);
			dialog.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);
			dialog.setHeaderText("Beenden?");
			dialog.setTitle("");
			Optional<ButtonType> result = dialog.showAndWait();

			if (result.isPresent()) {
				if (result.get().equals(ButtonType.OK)) {
					System.out.println("Stop Servercontroller");
					FrameHandler.mainPane.getTabs().forEach(tab -> {
						if (tab instanceof ServerTab) {

							TabServerHandler handler = ((ServerTab) tab).getTabContent().getContentHandler()
									.getServerHandler();

							if (handler.getServer().isRunning()) {
								handler.onEndClicked();
							}
						}
					});
					ChartsUpdater.stopUpdate();
					ServerMonitoring.stopMonitoring();
					Platform.exit();
				} else {
					event.consume();
				}
			}
		});
	}

	/**
	 * This method load the frame and starts the splashscreen
	 *
	 * @param args The start arguments
	 */
	public static void load(String... args) {
		launch(args);
	}

}
