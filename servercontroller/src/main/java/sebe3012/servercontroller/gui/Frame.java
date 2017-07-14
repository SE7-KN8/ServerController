package sebe3012.servercontroller.gui;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.Addons;
import sebe3012.servercontroller.preferences.PreferencesConstants;
import sebe3012.servercontroller.preferences.ServerControllerPreferences;
import sebe3012.servercontroller.save.ServerSave;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.design.Designs;
import sebe3012.servercontroller.util.settings.Settings;

import org.controlsfx.control.Notifications;
import org.jdom2.JDOMException;
import org.scenicview.ScenicView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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


	@Override
	public void start(Stage primaryStage) throws Exception {
		Addons.loadAddons();

		Frame.primaryStage = primaryStage;

		createSplashScreen();
		createPrimaryStage();

		Platform.runLater(() -> {

			Addons.waitForLoadingComplete();

			splash.close();
			primaryStage.show();
			if ((boolean) Settings.readSetting(Settings.Constants.AUTO_LOAD_SERVERS)) {
				try {
					ServerSave.loadServerController(ServerControllerPreferences.loadSetting(PreferencesConstants.LAST_SERVERS, null), false);
				} catch (IllegalStateException e) {
					e.printStackTrace();
					FrameHandler.showSaveStateErrorDialog();
				} catch (JDOMException | IOException | IllegalArgumentException | ReflectiveOperationException e) {
					e.printStackTrace();
					FrameHandler.showSaveErrorDialog();
				}
			}
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
		scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			System.out.println(event.getCode());
					switch (event.getCode()) {
						case F1:
							break;
						case F2:
							break;
						case F3:
							break;
						case F4:
							break;
						case F5:
							break;
						case F6:
							break;
						case F7:
							break;
						case F8:
							break;
						case F9:
							break;
						case F10:
							break;
						case F11:
							Notifications.create().darkStyle().hideAfter(Duration.seconds(10)).owner(Frame.primaryStage).title("Test notification").onAction(e -> DialogUtil.showInformationAlert("Some Dialog", "", "Some information")).text("Some text with some information").showInformation();
							break;
						case F12:
							ScenicView.show(scene);
							break;
					}
				}
		);
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
