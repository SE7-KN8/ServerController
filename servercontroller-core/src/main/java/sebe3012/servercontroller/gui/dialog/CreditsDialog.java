package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.util.I18N;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by Sebe3012 on 05.03.2017.
 * The credits dialog
 */
public class CreditsDialog extends AlertDialog {

	private Dialog addonDialog = new AddonDialog();

	public CreditsDialog() {
		super(I18N.translate("menu_item_credits"), I18N.translate("menu_item_credits"), "", Alert.AlertType.INFORMATION);
	}

	@Override
	public DialogPane createDialog(DialogPane dialogPane) {

		try {
			VBox content = FXMLLoader.load(ClassLoader.getSystemResource("fxml/CreditsDialog.fxml"), I18N.getDefaultBundle());

			((Label) content.getChildren().get(0)).setText(I18N.format("credits", ServerController.VERSION));
			((Hyperlink) ((HBox) content.getChildren().get(1)).getChildren().get(0)).setOnAction(event -> addonDialog.showDialog());
			dialogPane.setContent(content);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return dialogPane;
	}

}
