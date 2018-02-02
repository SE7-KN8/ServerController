package se7kn8.servercontroller.gui.dialog;

import se7kn8.servercontroller.api.gui.dialog.StageDialog;
import se7kn8.servercontroller.api.util.FileUtil;
import se7kn8.servercontroller.util.I18N;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class LicenseDialog extends StageDialog {

	public LicenseDialog() {
		super(I18N.translate("menu_item_license"));
	}

	@Override
	public Scene createDialog(Stage stage) {
		VBox root = new VBox();

		WebView wv = new WebView();
		WebEngine engine = wv.getEngine();

		engine.loadContent(FileUtil.loadStringContent("html/license.html"));

		root.getChildren().add(wv);

		return new Scene(root);
	}
}
