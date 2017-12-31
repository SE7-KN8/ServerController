package sebe3012.servercontroller.addon;

import sebe3012.servercontroller.api.gui.fileeditor.FileEditor;
import sebe3012.servercontroller.api.gui.fileeditor.FileEditorCreator;
import sebe3012.servercontroller.api.gui.server.DialogRow;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
import sebe3012.servercontroller.api.gui.tab.TabEntry;
import sebe3012.servercontroller.api.gui.tab.TabHandler;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.util.DialogUtil;
import sebe3012.servercontroller.api.util.FileUtil;
import sebe3012.servercontroller.api.util.StringPredicates;
import sebe3012.servercontroller.api.util.design.Designs;
import sebe3012.servercontroller.server.ServerManager;
import sebe3012.servercontroller.util.GUIUtil;
import sebe3012.servercontroller.util.I18N;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

//TODO don't use static methods
public class AddonUtil {

	private static final Logger log = LogManager.getLogger();

	public static List<FileEditorCreator> getEditorsForType(Path file, ServerManager manager) {
		if (!Files.exists(file) || Files.isDirectory(file)) {
			return new ArrayList<>();
		}

		String filePath = file.toString();
		String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);

		Collection<List<FileEditorCreator>> fileCreators = manager.getRegistryHelper().getFileEditorRegistry().getValues();

		List<FileEditorCreator> creatorList = new ArrayList<>();

		for (List<FileEditorCreator> creators : fileCreators) {
			for (FileEditorCreator creator : creators) {
				if (creator.getFileTypes().contains(fileExtension)) {
					creatorList.add(creator);
				}
			}
		}

		return creatorList;
	}

	public static void loadFileEditor(String className, TabHandler<TabEntry<?>> serverTabHandler, Path file) {
		try {
			Class<?> editorClass = Class.forName(className);
			Constructor<?> constructor = editorClass.getConstructor();
			Object o = constructor.newInstance();
			if (o instanceof FileEditor) {
				FileEditor editor = (FileEditor) o;
				editor.openFile(file);
				serverTabHandler.addTab(editor);
			}
		} catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
			log.error("Error while opening file editor ", e);
		}
	}

	public static void openFileEditor(TabHandler<TabEntry<?>> serverTabHandler, Path file, ServerManager manager) {
		List<FileEditorCreator> editors = AddonUtil.getEditorsForType(file, manager);

		Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.OK, ButtonType.CANCEL);
		dialog.setHeaderText(I18N.format("dialog_choose_editor_header", file.getFileName().toString()));
		Designs.applyCurrentDesign(dialog);
		VBox buttons = new VBox();
		buttons.setSpacing(20);
		ToggleGroup group = new ToggleGroup();

		for (FileEditorCreator editor : editors) {
			RadioButton button = new RadioButton(editor.getBundleToTranslate().getString(editor.getID()));
			button.setToggleGroup(group);
			button.setUserData(editor.getFileEditorClass().getName());
			buttons.getChildren().add(button);
		}

		RadioButton openWithSystem = new RadioButton(I18N.translate("context_menu_open_in_system"));
		openWithSystem.setUserData("useSystemExplorer");
		openWithSystem.setToggleGroup(group);
		buttons.getChildren().add(openWithSystem);
		group.selectToggle(openWithSystem);

		dialog.getDialogPane().setContent(buttons);
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			String className = (String) group.getSelectedToggle().getUserData();
			if (className.equals("useSystemExplorer")) {
				try {
					Desktop.getDesktop().open(file.toFile());
				} catch (IOException e) {
					log.error("Error while opening file", e);
				}
			} else {
				AddonUtil.loadFileEditor(className, serverTabHandler, file);
			}

		}
	}

	public static void loadServerCreateDialog(String addonID, String serverCreatorID, BasicServer parent, ServerManager serverManager) {

		List<DialogRow> rows = new ArrayList<>();

		findServerCreator(serverManager, addonID, serverCreatorID, serverCreator -> {
			if (parent != null) {
				createRows(serverCreator, rows, parent.getProperties(), true, serverManager);
			} else {
				createRows(serverCreator, rows, null, false, serverManager);
			}
		});

		openCreateDialog(addonID, serverCreatorID, serverManager, rows, parent, e -> {
			if (parent == null) {
				findServerCreator(serverManager, addonID, serverCreatorID, serverCreator -> {
					try {
						serverManager.createServerHandler(e, serverCreator.getServerClass(), true, addonID, serverCreatorID);
					} catch (Exception ex) {
						log.error("Could not create the server, because: ", ex);
					}
				});
			} else {
				Map<String, StringProperty> oldServerMap = parent.getProperties();
				oldServerMap.forEach((key, value) -> value.set(e.get(key).get()));
				serverManager.getTabHandler().refresh();
				serverManager.getTreeHandler().refresh();
			}
		});

	}

	public static void findServerCreator(ServerManager manager, String addonID, String id, Consumer<ServerCreator> consumer) {
		findServerCreator(manager, addonID, id).ifPresent(consumer);
	}

	public static Optional<ServerCreator> findServerCreator(ServerManager manager, String addonID, String id) {
		return manager.getRegistryHelper()
				.getServerCreatorRegistry()
				.getEntries(Addons.addonForID(addonID))
				.stream()
				.filter(serverCreator -> serverCreator.getID().equals(id))
				.findFirst();
	}

	private static void createRows(ServerCreator creator, List<DialogRow> parentRows, Map<String, StringProperty> properties, boolean useProperties, ServerManager manager) {
		if (creator.getParent() != null) {
			String parentAddonID = creator.getParent().split(":")[0];
			String parentCreatorID = creator.getParent().split(":")[1];

			findServerCreator(manager, parentAddonID, parentCreatorID, parentCreator -> createRows(parentCreator, parentRows, properties, useProperties, manager));
		}

		creator.createServerDialogRows(properties, parentRows, useProperties);

	}

	/**
	 * Creates an dialog to create Addon-Specified server
	 *
	 * @param addonID        The addon id
	 * @param values         The rows to be used
	 * @param parent         A parent server if the dialog is used to edit. Can be null.
	 * @param serverConsumer The function to create the server
	 */
	public static void openCreateDialog(@NotNull String addonID, String id, ServerManager manager, @NotNull List<DialogRow> values, @Nullable BasicServer parent, @NotNull Consumer<Map<String, StringProperty>> serverConsumer) {
		Alert dialog = new Alert(Alert.AlertType.NONE);

		GridPane root = getDialogLayout(addonID, values, serverConsumer, parent, v -> dialog.close());

		dialog.getDialogPane().setContent(root);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		Designs.applyCurrentDesign(dialog);
		findServerCreator(manager, addonID, id, serverCreator -> dialog.setTitle(I18N.format("create_server", serverCreator.getID())));
		dialog.show();
	}


	private static GridPane getDialogLayout(String addonID, List<DialogRow> values, Consumer<Map<String, StringProperty>> serverConsumer, BasicServer parent, Consumer<Void> closeCallback) {
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
		Label header = new Label(I18N.format("create_server", Addons.addonForID(addonID).getAddonInfo().getName()));
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

				if (v.get() == null) {
					v.setValue("");
				}

				//Test the values
				if (!controlRow.getStringPredicate().test(v.get())) {
					log.warn("Value '{}' do not match!", controlRow.getName());
					DialogUtil.showWaringAlert(I18N.translate("dialog_wrong_content"), I18N.format("dialog_wrong_content_desc", controlRow.getName()));
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