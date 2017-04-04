package sebe3012.servercontroller.util;

import sebe3012.servercontroller.gui.Frame;
import sebe3012.servercontroller.preferences.PreferencesConstants;
import sebe3012.servercontroller.preferences.ServerControllerPreferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Sebe3012 on 01.04.2017.
 * Util class for all designs
 */
public class Designs {

	private static Logger log = LogManager.getLogger();

	private static Design currentDesign;

	private static HashMap<String, Design> designs = new HashMap<>();

	public static Design getCurrentDesign() {
		return currentDesign;
	}

	public static void registerDesign(Design design) {
		designs.put(design.getId(), design);
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
			scene.getStylesheets().add(getCurrentDesign().getStylesheet());
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
			dialog.getStylesheets().add(getCurrentDesign().getStylesheet());
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
			dialog.getDialogPane().getStylesheets().add(getCurrentDesign().getStylesheet());
		}
	}


	public static void setCurrentDesign(String designID) {
		currentDesign = designs.get(designID);
	}

	public static void showDesignDialog() {
		ChoiceDialog<Design> cd = new ChoiceDialog<>();
		cd.setGraphic(new ImageView(ClassLoader.getSystemResource("png/icon.png").toExternalForm()));
		Designs.applyCurrentDesign(cd.getDialogPane());
		cd.setTitle(I18N.translate("dialog_choose_design"));
		cd.setHeaderText(I18N.translate("dialog_choose_design_desc"));
		cd.getItems().setAll(Designs.getDesigns());

		Optional<Design> result = cd.showAndWait();

		if (result.isPresent()) {

			Design design = result.get();

			ServerControllerPreferences.saveSetting(PreferencesConstants.KEY_DESIGN, design.getId());

			Designs.setCurrentDesign(design.getId());
			Designs.applyCurrentDesign(Frame.primaryStage.getScene());
		}
	}

}

