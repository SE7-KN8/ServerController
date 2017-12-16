package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.gui.RConConsole;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.NumberField;
import sebe3012.servercontroller.util.design.Designs;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

public class RConDialog implements Dialog {

	public void showDialog(){
		javafx.scene.control.Dialog<Pair<String, Pair<Integer, char[]>>> loginDialog = new javafx.scene.control.Dialog<>();

		loginDialog.setTitle(I18N.translate("dialog_rcon"));
		loginDialog.setHeaderText(I18N.translate("dialog_rcon_desc"));
		loginDialog.setGraphic(new ImageView(ClassLoader.getSystemClassLoader().getResource("png/icon.png").toExternalForm()));

		DialogPane dp = loginDialog.getDialogPane();
		ButtonType bt = new ButtonType(I18N.translate("dialog_rcon_login"), ButtonBar.ButtonData.OK_DONE);
		dp.getButtonTypes().add(bt);
		Designs.applyCurrentDesign(dp);
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

}
