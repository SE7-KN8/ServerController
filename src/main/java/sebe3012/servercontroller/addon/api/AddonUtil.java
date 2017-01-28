package sebe3012.servercontroller.addon.api;

import sebe3012.servercontroller.event.ChangeControlsEvent;
import sebe3012.servercontroller.event.ServerCreateEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabContent;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.GUIUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AddonUtil {

	private static final Logger log = LogManager.getLogger();

	public static void addServer(BasicServer server, boolean isEdit) {

		log.debug("Added server {}", server);

		TabContent content = new TabContent();
		ServerTab tab = new ServerTab(server.getName(), content);
		tab.setContent(content.getTabContent());

		if (isEdit) {
			int index = Tabs.getCurrentIndex();
			FrameHandler.mainPane.getTabs().set(index, tab);
			Servers.serversList.set(index, server);
		} else {
			FrameHandler.mainPane.getTabs().add(tab);
			Servers.serversList.add(server);
		}

		FrameHandler.mainPane.getSelectionModel().select(tab);

		if (!isEdit) {
			EventHandler.EVENT_BUS.post(new ChangeControlsEvent(server.getExtraControls()));
		}

		EventHandler.EVENT_BUS.post(new ServerCreateEvent(server));
	}

	public static String openFileChooser(String fileType, String fileName) {

		FileChooser fc = new FileChooser();

		//fc.setInitialDirectory(new File(ServerControllerPreferences
		//		.loadSetting(ServerControllerPreferences.Constants.FILE_ADDON_UTIL, System.getProperty("user.home"))));

		fc.getExtensionFilters().add(new ExtensionFilter(fileName, fileType));

		File f = fc.showOpenDialog(null);

		if (f != null) {

			//ServerControllerPreferences.saveSetting(ServerControllerPreferences.Constants.FILE_ADDON_UTIL,
			//		f.getAbsolutePath());

			return f.getAbsolutePath();
		}

		return "";

	}

	public static boolean checkUserInput(String input) {
		return input != null && input.trim().length() >= 1;
	}

	/**
	 * Creates an dialog to create Addon-Specified server
	 *
	 * @param addon          The addon name
	 * @param values         The rows to be used
	 * @param parent         A parent server if the dialog is used to edit. Can be null.
	 * @param serverConsumer The function to create the server
	 */
	public static void openCreateDialog(@NotNull String addon, @NotNull List<DialogRow> values, @Nullable BasicServer parent, @NotNull Consumer<Map<String, StringProperty>> serverConsumer) {
		Alert dialog = new Alert(Alert.AlertType.NONE);

		GridPane root = getDialogLayout(addon, values, serverConsumer, parent, v -> dialog.close());

		dialog.getDialogPane().setContent(root);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		dialog.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);
		dialog.setTitle(addon + "-Server erstellen");
		dialog.show();
	}


	private static GridPane getDialogLayout(String addon, List<DialogRow> values, Consumer<Map<String, StringProperty>> serverConsumer, BasicServer parent, Consumer<Void> closeCallback) {
		DialogRow idRow = new DialogRow().setName("Server-ID").setPropertyName("name").setStringPredicate(StringPredicates.DEFAULT_CHECK);
		DialogRow argsRow = new DialogRow().setName("Start-Argumente").setPropertyName("args").setStringPredicate(StringPredicates.DO_NOTHING);

		if (parent != null) {
			idRow.setDefaultValue(parent.getName());
			argsRow.setDefaultValue(parent.getArgs());
		}

		Map<String, StringProperty> properties = new HashMap<>();
		Map<String, DialogRow> rows = new HashMap<>();

		//put the rows list in the rows map
		values.forEach(row -> rows.put(row.getPropertyName(), row));
		rows.put(idRow.getPropertyName(), idRow);
		rows.put(argsRow.getPropertyName(), argsRow);

		//Create layout
		GridPane layout = new GridPane();
		layout.setMinWidth(1000);
		layout.setMinHeight(500);

		//Size of the list + header + id + args
		int size = values.size() + 3;

		//Add rows to layout
		for (int i = 0; i < size; i++) {
			GUIUtil.addRowConstraints(layout, 10, 30, -1, Priority.SOMETIMES);
		}

		//Add columns to layout
		GUIUtil.addColumnConstraints(layout, 10, 100, -1, Priority.SOMETIMES);
		GUIUtil.addColumnConstraints(layout, 10, 100, -1, Priority.SOMETIMES);
		GUIUtil.addColumnConstraints(layout, 10, 134, 227, Priority.SOMETIMES);
		GUIUtil.addColumnConstraints(layout, 10, 238, 253, Priority.SOMETIMES);

		//Add header text
		Label header = new Label(addon + "-Server erstellen");
		header.setFont(new Font(50));
		header.setPrefWidth(750);
		header.setPrefHeight(73);
		GridPane.setColumnSpan(header, 3);
		layout.getChildren().add(header);


		//Add default rows
		addRow(idRow, 1, layout, properties);
		addRow(argsRow, 2, layout, properties);


		//Add addon-specified rows
		int lastRow = 0;
		for (int i = 0; i < values.size(); i++) {
			DialogRow row = values.get(i);
			addRow(row, i + 3, layout, properties);
			lastRow = i + 3;
		}

		//Add confirm button
		Button confirm = new Button("Fertig");
		confirm.setPrefWidth(100);
		confirm.setPrefHeight(50);
		confirm.setOnAction(e -> {

			//No primitive boolean because it must be effective-final
			BooleanProperty flag = new SimpleBooleanProperty(true);

			log.debug("Start property analysis");
			properties.forEach((k, v) -> {
				DialogRow controlRow = rows.get(k);

				//Non-Null check
				if (controlRow == null) {
					throw new IllegalStateException("Row is null");
				}

				//Test the values
				if (!controlRow.getStringPredicate().test(v.get())) {
					log.warn("Value '{}' do not match!", controlRow.getName());
					DialogUtil.showWaringAlert("Warnung", "Falscher Inhalt", "Der Server kann nicht erstellt werden, da '" + controlRow.getName() + "' nicht gesetzt wurde");
					flag.set(false);
					return;
				}
			});

			log.debug("End property analysis");
			log.debug("Load server callback");

			if (flag.get()) {
				serverConsumer.accept(properties);
				closeCallback.accept(null);
			}
			log.debug("Server created");
		});

		GridPane.setColumnIndex(confirm, 3);
		GridPane.setRowIndex(confirm, lastRow);
		GridPane.setHalignment(confirm, HPos.CENTER);
		GridPane.setValignment(confirm, VPos.CENTER);
		layout.getChildren().add(confirm);

		return layout;
	}

	private static void addRow(DialogRow dialogRow, int row, GridPane layout, Map<String, StringProperty> properties) {
		log.debug("Add row: width:{}, height: {}", dialogRow.getPrefWidth(), dialogRow.getPrefHeight());

		Label label = new Label(dialogRow.getName());
		label.setFont(new Font(37));
		GridPane.setColumnIndex(label, 0);
		GridPane.setRowIndex(label, row);
		layout.getChildren().add(label);

		TextField field = new TextField();
		field.setPrefWidth(dialogRow.getPrefWidth());
		field.setPrefHeight(dialogRow.getPrefHeight());
		field.setPromptText(dialogRow.getPromptText());
		field.setText(dialogRow.getDefaultValue());
		GridPane.setColumnIndex(field, 1);
		GridPane.setRowIndex(field, row);
		layout.getChildren().add(field);
		properties.put(dialogRow.getPropertyName(), field.textProperty());

		if (dialogRow.isUsingFileChooser()) {
			Button b = new Button("Auswählen...");
			b.setPrefWidth(dialogRow.getPrefWidth());
			b.setPrefHeight(dialogRow.getPrefHeight());
			b.setOnAction(e -> field.setText(AddonUtil.openFileChooser(dialogRow.getFileExtension(), dialogRow.getFileType())));

			GridPane.setColumnIndex(b, 2);
			GridPane.setRowIndex(b, row);
			layout.getChildren().add(b);
		}

	}

}
