package sebe3012.servercontroller.gui;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.util.Designs;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.I18N;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

/**
 * The class construct the basic frame and starts the splash-screen
 *
 * @author Sebastian Knackstedt
 */
public class Frame extends Application {

	/**
	 * The basic frame for the program
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
		BorderPane root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/BaseFrame.fxml"), I18N.getDefaultBundle());
		Scene scene = new Scene(root);
		Designs.applyCurrentDesign(scene);
		primaryStage.setScene(scene);
		primaryStage.setTitle(I18N.format("window_title", ServerController.VERSION));
		primaryStage.setMaximized(true);
		primaryStage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
		primaryStage.setOnCloseRequest(event -> {

			if (ServerController.DEBUG) {
				ServerController.stop();
				Platform.exit();
			} else {
				// Close Dialog
				Optional<ButtonType> result = DialogUtil.showAlert("", I18N.translate("dialog_close"), I18N.translate("dialog_close_desc"), AlertType.CONFIRMATION, ButtonType.OK, ButtonType.CANCEL);

				if (result.isPresent()) {
					if (result.get().equals(ButtonType.OK)) {
						ServerController.stop();
						Platform.exit();
					} else {
						event.consume();
					}
				}
			}

		});
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
