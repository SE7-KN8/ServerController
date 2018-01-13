package se7kn8.servercontroller.api.util.design;

import se7kn8.servercontroller.api.preferences.ServerControllerPreferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by se7kn8 on 01.04.2017.
 * Util class for all designs
 */
//TODO don't use static methods
public class Designs {

	private static Logger log = LogManager.getLogger();
	private static ResourceBundle bundle = ResourceBundle.getBundle("lang/designs/lang");
	public static final String SAVE_KEY = "design";

	private static Design currentDesign;

	private static HashMap<String, Design> designs = new HashMap<>();

	public static Design getDefaultDesign(){
		return designs.values().iterator().next();
	}

	public static Design getCurrentDesign() {
		return currentDesign;
	}

	public static void registerDesign(Design design) {
		designs.put(design.getId(), design);
		log.debug("Register design with id '{}'", design.getId());
	}

	public static Collection<Design> getDesigns() {
		return designs.values();
	}

	/**
	 * Apply the current design to the scene <br>
	 * All other designs will be cleared
	 *
	 * @param scene The {@link Scene}
	 */
	public static void applyCurrentDesign(Scene scene) {
		if (scene != null) {
			scene.getStylesheets().clear();
			scene.getStylesheets().addAll(getCurrentDesign().getStylesheets());
		}
	}

	/**
	 * Apply the current design to the scene <br>
	 * All other designs will be cleared
	 *
	 * @param dialog The {@link DialogPane}
	 */
	public static void applyCurrentDesign(DialogPane dialog) {
		if (dialog != null) {
			dialog.getStylesheets().clear();
			dialog.getStylesheets().addAll(getCurrentDesign().getStylesheets());
		}
	}

	/**
	 * Apply the current design to the scene <br>
	 * All other designs will be cleared
	 *
	 * @param dialog The {@link Dialog}
	 */
	public static void applyCurrentDesign(Dialog dialog){
		if (dialog != null) {
			dialog.getDialogPane().getStylesheets().clear();
			dialog.getDialogPane().getStylesheets().addAll(getCurrentDesign().getStylesheets());
		}
	}


	public static void setCurrentDesign(String designID) {
		currentDesign = designs.getOrDefault(designID, designs.values().stream().findFirst().get());
	}

	public static void showDesignDialog(Stage primaryStage) {
		ChoiceDialog<Design> cd = new ChoiceDialog<>();
		cd.setGraphic(new ImageView(ClassLoader.getSystemResource("png/icon.png").toExternalForm()));
		Designs.applyCurrentDesign(cd.getDialogPane());
		cd.setTitle(bundle.getString("dialog_choose_design"));
		cd.setHeaderText(bundle.getString("dialog_choose_design_desc"));
		cd.getItems().setAll(Designs.getDesigns());

		Optional<Design> result = cd.showAndWait();

		if (result.isPresent()) {

			Design design = result.get();

			ServerControllerPreferences.saveSetting(Designs.SAVE_KEY, design.getId());

			Designs.setCurrentDesign(design.getId());
			Designs.applyCurrentDesign(primaryStage.getScene());
		}
	}

}

