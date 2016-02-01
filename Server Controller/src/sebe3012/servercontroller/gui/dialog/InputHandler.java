package sebe3012.servercontroller.gui.dialog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.TabContent;
import sebe3012.servercontroller.gui.tab.Tabs;

/***
 *
 * @author Sebe3012
 *
 *         This class handle the input of a BatchServerDialog
 *
 * @see BatchServerDialog
 *
 */
public class InputHandler {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	/**
	 * The OK button
	 */
	@FXML
	private Button btnOK;
	/**
	 * The textfield with the server id
	 */
	@FXML
	private TextField txfID;
	/**
	 * The textfield with the batch file
	 */
	@FXML
	private TextField txfStart;
	/**
	 * The textfield with the properties file
	 */
	@FXML
	private TextField txfPro;
	/**
	 * The button to open the batch
	 */
	@FXML
	private Button btnOpenBatch;
	/**
	 * The button to open the properties
	 */
	@FXML
	private Button btnPro;
	/**
	 * The imageview for the server-icon
	 */
	@FXML
	private ImageView image;
	/**
	 * The label for the headline
	 */
	@FXML
	private Label lbl;

	@FXML
	void onAction(ActionEvent event) {

		System.out.println("[ServerCreateDialog] ID= " + txfID.getText() + " START= " + txfStart.getText()
				+ " PROPERTIES= " + txfPro.getText());
		boolean successful = createNewTab();
		if(successful){
			BatchServerDialog.stage.close();
		}
	}

	/**
	 *
	 */
	private boolean init;

	/**
	 * This method create a new tab and setting the server on the tab
	 */
	private boolean createNewTab() {
		Platform.runLater(() -> {
			Tab tab = new Tab(txfID.getText());
			tab.setContent(new TabContent().getTabContent());
			tab.setClosable(false);
			FrameHandler.mainPane.getTabs().add(tab);
			init = false;
			Tabs.servers.forEach((id, server) -> {
				if (!init) {
					if (!server.hasServer()) {
						server.initServer(txfStart.getText(), txfPro.getText(), txfID.getText());
						init = true;
					}
				}
			});
		});
		return true;
	}

	/**
	 *
	 * This method is called when the user selects the batch file
	 *
	 * @param event
	 *            	The action event
	 */
	@FXML
	void onOpenBatch(ActionEvent event) {
		FileChooser fc = new FileChooser();
		ExtensionFilter shellScripte = new ExtensionFilter("CMD/BAT/SH", "*.bat", "*.cmd", "*.sh");
		fc.getExtensionFilters().add(shellScripte);
		fc.setTitle("Datei öffnen");
		File f = fc.showOpenDialog(null);
		if (f == null) {
			return;
		}
		if (f.exists()) {
			txfStart.setText(f.getAbsolutePath());
		}
		File icon = new File(f.getParentFile().getAbsoluteFile() + "\\server-icon.png");
		System.out.println("[ServerCreateDialog] " + icon.getAbsolutePath());
		if (icon.exists()) {
			BufferedImage i = null;
			try {
				i = ImageIO.read(icon);
			} catch (IOException e) {
				e.printStackTrace();
			}
			image.setSmooth(false);
			image.setImage(SwingFXUtils.toFXImage(i, null));
		}
	}

	/**
	 *
	 * This method is called when the user selects the properties file
	 *
	 * @param event
	 *            The action event
	 */
	@FXML
	void onOpenPro(ActionEvent event) {
		FileChooser fc = new FileChooser();
		ExtensionFilter shellScripte = new ExtensionFilter("PROPERTIES", "*.properties");
		fc.getExtensionFilters().add(shellScripte);
		fc.setTitle("Datei öffnen");
		File f = fc.showOpenDialog(null);
		if (f == null) {
			return;
		}
		if (f.exists()) {
			txfPro.setText(f.getAbsolutePath());
		}
		File icon = new File(f.getParentFile().getAbsoluteFile() + "\\server-icon.png");
		System.out.println("[ServerCreateDialog] " + icon.getAbsolutePath());
		if (icon.exists()) {
			BufferedImage i = null;
			try {
				i = ImageIO.read(icon);
			} catch (IOException e) {
				e.printStackTrace();
			}
			image.setSmooth(false);
			image.setImage(SwingFXUtils.toFXImage(i, null));
		}
	}

	/**
	 * This method is called by the FXMLLoader
	 *
	 * @see FXMLLoader
	 *
	 */
	@FXML
	void initialize() {
		lbl.setEffect(new DropShadow(10, 10, 10, Color.GREY));
	}
}