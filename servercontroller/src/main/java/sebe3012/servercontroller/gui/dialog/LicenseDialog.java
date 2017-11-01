package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.util.FileUtil;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.design.Designs;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LicenseDialog {

	public static void showDialog(){
		Stage stage = new Stage(StageStyle.UTILITY);
		stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("png/icon.png")));
		stage.setTitle(I18N.translate("menu_item_license"));

		VBox root = new VBox();

		WebView wv = new WebView();
		WebEngine engine = wv.getEngine();

		engine.loadContent(FileUtil.loadStringContent("html/license.html"));

		root.getChildren().add(wv);

		Scene scene = new Scene(root);
		Designs.applyCurrentDesign(scene);

		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.setScene(scene);
		stage.showAndWait();
	}

}
