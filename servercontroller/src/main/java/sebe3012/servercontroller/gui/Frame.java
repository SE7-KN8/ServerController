package sebe3012.servercontroller.gui;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.AddonLoader;
import sebe3012.servercontroller.addon.Addons;
import sebe3012.servercontroller.gui.handler.DebugKeyHandler;
import sebe3012.servercontroller.prelaunch.AddonInstallerTask;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.design.Designs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * The class construct the basic frame and starts the splash-screen
 *
 * @author Sebastian Knackstedt
 */
public class Frame extends Application {

	private Stage splash;


	@Override
	public void start(Stage primaryStage) throws Exception {

		createSplashScreen();
		createPrimaryStage(primaryStage);

		new AddonInstallerTask(AddonLoader.ADDON_TEMP_PATH, AddonLoader.ADDON_PATH, AddonLoader.JAR_FILE_MATCHER).installAddons();
		Addons.loadAddons();

		new Thread(new Task<Void>(){
			@Override
			protected Void call() throws Exception {
				Addons.waitForLoadingComplete();

				Platform.runLater(() -> {
					splash.close();
					primaryStage.show();
					//TODO use new system
					// ServerSave.loadServerControllerFromLastFile();
				});

				return null;
			}
		}).start();
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

	private void createPrimaryStage(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(I18N.getDefaultBundle());
		loader.setController(new FrameHandler(primaryStage));
		loader.setLocation(ClassLoader.getSystemResource("fxml/BaseFrame.fxml"));
		BorderPane root = loader.load();
		Scene scene = new Scene(root);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, new DebugKeyHandler(primaryStage));
		Designs.applyCurrentDesign(scene);
		primaryStage.setScene(scene);
		primaryStage.setTitle(I18N.format("window_title", ServerController.VERSION));
		primaryStage.setMaximized(true);
		primaryStage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
	}

	/**
	 * This method load the frame and starts the splash-screen
	 *
	 * @param args The start arguments
	 */
	public static void load(String... args) {
		launch(args);
	}

}
