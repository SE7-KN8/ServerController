package se7kn8.servercontroller.api.gui.dialog;

import se7kn8.servercontroller.api.util.design.Designs;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public abstract class AlertDialog implements Dialog {

	private String title;
	private String header;
	private String content;
	private Alert.AlertType type;
	private ButtonType[] buttonTypes;

	public AlertDialog(@NotNull String title, @NotNull String header, @NotNull String content, @NotNull Alert.AlertType type, @NotNull ButtonType... types){
		this.title = title;
		this.header = header;
		this.content = content;
		this.type = type;
		this.buttonTypes = types;
	}

	public abstract DialogPane createDialog(DialogPane dialogPane);

	@Override
	public final void showDialog() {
		Alert alert = new Alert(type, content, buttonTypes);
		Designs.applyCurrentDesign(alert.getDialogPane());
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setResizable(true);
		alert.setDialogPane(createDialog(alert.getDialogPane()));

		alert.showAndWait();
	}
}
