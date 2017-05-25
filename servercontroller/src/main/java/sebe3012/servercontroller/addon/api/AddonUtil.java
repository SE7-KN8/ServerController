package sebe3012.servercontroller.addon.api;

import sebe3012.servercontroller.addon.Addons;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.FileUtil;
import sebe3012.servercontroller.util.GUIUtil;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.design.Designs;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AddonUtil {

	private static final Logger log = LogManager.getLogger();

	private static final HashMap<Addon, Class<? extends BasicServer>> serverTypes = new HashMap<>();
	private static final HashMap<Addon, ServerCreator> serverCreators = new HashMap<>();

	public static void registerServerType(Addon addon, Class<? extends BasicServer> serverClass, ServerCreator serverCreator) {
		serverTypes.put(addon, serverClass);
		serverCreators.put(addon, serverCreator);
	}

	public static ServerCreator getServerCreator(Addon addon) {
		return serverCreators.get(addon);
	}

	public static HashMap<Addon, Class<? extends BasicServer>> getServerTypes() {
		return serverTypes;
	}

	public static void loadServerCreateDialog(Addon addon, BasicServer parent) {
		List<DialogRow> rows = new ArrayList<>();

		createRows(AddonUtil.getServerCreator(addon), rows, Servers.getServerProperties(parent), parent != null);

		openCreateDialog(addon, rows, parent, e -> {
			try {

				if (parent == null) {
					Servers.addServer(Servers.createBasicServer(e, serverTypes.get(addon)), addon);
				} else {
					Map<String, StringProperty> oldServerMap = Servers.getServerProperties(parent);
					oldServerMap.forEach((key, value) -> value.set(e.get(key).get()));
					FrameHandler.tree.refresh();
				}
			} catch (Exception ex) {
				log.error("Could not create the server, because: ", ex);
			}
		});

	}

	private static void createRows(ServerCreator creator, List<DialogRow> parentRows, Map<String, StringProperty> properties, boolean useProperties) {
		Addon parentCreatorAddon = Addons.addonForID(creator.getParent());
		ServerCreator parentCreator = AddonUtil.getServerCreator(parentCreatorAddon);

		if (parentCreator != null) {
			createRows(parentCreator, parentRows, properties, useProperties);
		}

		creator.createServerDialogRows(properties, parentRows, useProperties);
	}

	/**
	 * Creates an dialog to create Addon-Specified server
	 *
	 * @param addon          The addon name
	 * @param values         The rows to be used
	 * @param parent         A parent server if the dialog is used to edit. Can be null.
	 * @param serverConsumer The function to create the server
	 */
	public static void openCreateDialog(@NotNull Addon addon, @NotNull List<DialogRow> values, @Nullable BasicServer parent, @NotNull Consumer<Map<String, StringProperty>> serverConsumer) {
		Alert dialog = new Alert(Alert.AlertType.NONE);

		GridPane root = getDialogLayout(addon, values, serverConsumer, parent, v -> dialog.close());

		dialog.getDialogPane().setContent(root);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		Designs.applyCurrentDesign(dialog);
		dialog.setTitle(I18N.format("create_server", addon.getAddonInfo().getName()));
		dialog.show();
	}


	private static GridPane getDialogLayout(Addon addon, List<DialogRow> values, Consumer<Map<String, StringProperty>> serverConsumer, BasicServer parent, Consumer<Void> closeCallback) {
		DialogRow idRow = new DialogRow();

		idRow.setName(I18N.translate("dialog_create_server_name"));
		idRow.setPropertyName("name");
		idRow.setStringPredicate(StringPredicates.DEFAULT_CHECK);


		DialogRow argsRow = new DialogRow();
		argsRow.setName(I18N.translate("dialog_create_server_args"));
		argsRow.setPropertyName("args");
		argsRow.setStringPredicate(StringPredicates.DO_NOTHING);

		if (parent != null) {
			idRow.setDefaultValue(parent.getName());
			argsRow.setDefaultValue(parent.getArgs());
		}

		Map<String, StringProperty> properties = new HashMap<>();
		Map<String, DialogRow> rows = new HashMap<>();

		//put the rows tree in the rows map
		values.forEach(row -> rows.put(row.getPropertyName(), row));
		rows.put(idRow.getPropertyName(), idRow);
		rows.put(argsRow.getPropertyName(), argsRow);

		//Create layout
		GridPane layout = new GridPane();
		layout.setMinWidth(1000);
		layout.setMinHeight(500);

		//Size of the tree + header + id + args
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
		Label header = new Label(I18N.format("create_server", addon.getAddonInfo().getName()));
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
		Button confirm = new Button(I18N.translate("dialog_finish"));
		confirm.setPrefWidth(100);
		confirm.setPrefHeight(50);
		confirm.setOnAction(e -> {
			//No primitive boolean because it have to be effective-final
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
					DialogUtil.showWaringAlert(I18N.translate("dialog_warning"), I18N.translate("dialog_wrong_content"), I18N.format("dialog_wrong_content_desc", controlRow.getName()));
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
			Button b = new Button(I18N.format("file_choose"));
			b.setPrefWidth(dialogRow.getPrefWidth());
			b.setPrefHeight(dialogRow.getPrefHeight());
			b.setOnAction(e -> field.setText(FileUtil.openFileChooser(dialogRow.getFileExtension(), dialogRow.getFileType())));

			GridPane.setColumnIndex(b, 2);
			GridPane.setRowIndex(b, row);
			layout.getChildren().add(b);
		}
	}

}
