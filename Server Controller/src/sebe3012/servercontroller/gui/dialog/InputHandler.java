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
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabContent;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.JarServer;
import sebe3012.servercontroller.server.Servers;

/***
 *
 * @author Sebe3012
 *
 *         This class handle the input of a JarServerDialog
 *
 * @see JarServerDialog
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
	private TextField txfJar;
	/**
	 * The textfield with the properties file
	 */
	@FXML
	private TextField txfPro;
	/**
	 * The button to open the batch
	 */
	@FXML
	private Button btnOpenJar;
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
	private TextField txfRam;

	@FXML
	void onAction(ActionEvent event) {

		boolean successful = true;

		if (JarServerDialog.useDefault) {
			System.out.println("[ServerCreateDialog] ID= " + txfID.getText() + " START= " + txfJar.getText()
					+ " PROPERTIES= " + txfPro.getText() + " RAM=" + txfRam.getText());
			successful = createNewTab();
		} else {
			int id = ((ServerTab) FrameHandler.mainPane.getSelectionModel().getSelectedItem()).getTabContent().getId();
			JarServer js = Tabs.servers.get(id).getServer();
			int tab = FrameHandler.mainPane.getSelectionModel().getSelectedIndex();
			Servers.servers.remove(js);
			if (!js.isRunning()) {
				Tabs.servers.get(id).initServer(txfJar.getText(), txfPro.getText(), txfID.getText(), txfRam.getText(),
						true);
				FrameHandler.mainPane.getSelectionModel().select(tab);
				FrameHandler.mainPane.getSelectionModel().getSelectedItem().setText(txfID.getText());
			}
		}
		if (successful) {
			JarServerDialog.stage.close();
			JarServerDialog.isOpen = false;
		}
		JarServerDialog.useDefault = true;
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
			TabContent content = new TabContent();
			ServerTab tab = new ServerTab(txfID.getText(), content);
			tab.setContent(content.getTabContent());
			tab.setClosable(false);
			FrameHandler.mainPane.getTabs().add(tab);
			init = false;
			Tabs.servers.forEach((id, server) -> {
				if (!init) {
					if (!server.hasServer()) {
						server.initServer(txfJar.getText(), txfPro.getText(), txfID.getText(), txfRam.getText(), true);
						init = true;
					}
				}
			});
		});
		return true;
	}

	/**
	 *
	 * This method is called when the user selects the jar file
	 *
	 * @param event
	 *            The action event
	 */
	@FXML
	void onOpenJar(ActionEvent event) {
		FileChooser fc = new FileChooser();
		ExtensionFilter shellScripte = new ExtensionFilter("Javafile, Jar", "*.jar");
		fc.getExtensionFilters().add(shellScripte);
		fc.setTitle("Datei öffnen");
		File f = fc.showOpenDialog(null);
		if (f == null) {
			return;
		}
		if (f.exists()) {
			txfJar.setText(f.getAbsolutePath());
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
		init();
	}

	private void init() {
		if (!JarServerDialog.useDefault) {
			txfID.setText(JarServerDialog.name);
			txfJar.setText(JarServerDialog.jarPath);
			txfPro.setText(JarServerDialog.propertiesPath);
			txfRam.setText(JarServerDialog.ram);
		}
		lbl.setEffect(new DropShadow(10, 10, 10, Color.GREY));
	}
}