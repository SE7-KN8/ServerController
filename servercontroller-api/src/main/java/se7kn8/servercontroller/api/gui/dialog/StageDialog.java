package se7kn8.servercontroller.api.gui.dialog;

import se7kn8.servercontroller.api.util.design.Designs;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class StageDialog implements Dialog {

	private String title;

	public StageDialog(String title){
		this.title = title;
	}

	public abstract Scene createDialog(Stage stage);

	@Override
	public final void showDialog() {
		Stage stage = new Stage(StageStyle.UTILITY);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
		stage.setTitle(title);
		Scene scene = createDialog(stage);
		Designs.applyCurrentDesign(scene);
		stage.setScene(scene);
		stage.showAndWait();
	}
}
