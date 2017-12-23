package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.api.gui.dialog.StageDialog;
import sebe3012.servercontroller.api.util.FileUtil;
import sebe3012.servercontroller.util.I18N;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class LicenseDialog extends StageDialog {

	public LicenseDialog() {
		super(I18N.translate("menu_item_license"));
	}

	@Override
	public Scene createDialog() {
		VBox root = new VBox();

		WebView wv = new WebView();
		WebEngine engine = wv.getEngine();

		engine.loadContent(FileUtil.loadStringContent("html/license.html"));

		root.getChildren().add(wv);

		return new Scene(root);
	}
}
