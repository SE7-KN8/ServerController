package se7kn8.servercontroller.gui.dialog;

import se7kn8.servercontroller.addon.AddonLoader;
import se7kn8.servercontroller.api.addon.AddonInfo;
import se7kn8.servercontroller.api.gui.dialog.AlertDialog;
import se7kn8.servercontroller.util.I18N;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Created by se7kn8 on 05.03.2017.
 * The addon dialog
 */
public class AddonDialog extends AlertDialog {

	@SuppressWarnings("unused")//Things are used by reflection
	public static class AddonInfoWrapper {

		private StringProperty id = new SimpleStringProperty();
		private StringProperty name = new SimpleStringProperty();
		private StringProperty version = new SimpleStringProperty();
		private StringProperty mainClass = new SimpleStringProperty();
		private StringProperty jarPath = new SimpleStringProperty();
		private ObjectProperty<List<String>> authors = new SimpleObjectProperty<>();
		private ObjectProperty<List<String>> dependencies = new SimpleObjectProperty<>();

		public AddonInfoWrapper(AddonInfo info) {
			id.set(info.getId());
			name.set(info.getName());
			version.set(info.getVersion().toString());
			mainClass.set(info.getMainClass());
			jarPath.set(info.getJarPath().toString());
			authors.set(info.getAuthors());
			dependencies.set(info.getDependencies());
		}

		public String getId() {
			return id.get();
		}

		public String getName() {
			return name.get();
		}

		public String getVersion() {
			return version.get();
		}

		public String getMainClass() {
			return mainClass.get();
		}

		public String getJarPath() {
			return jarPath.get();
		}

		public List<String> getAuthors() {
			return authors.get();
		}

		public List<String> getDependencies() {
			return dependencies.get();
		}

		public void setJarPath(String jarPath) {
			this.jarPath.set(jarPath);
		}

		public void setName(String name) {
			this.name.set(name);
		}

		public void setAuthors(List<String> authors) {
			this.authors.set(authors);
		}

		public void setId(String id) {
			this.id.set(id);
		}

		public void setDependencies(List<String> dependencies) {
			this.dependencies.set(dependencies);
		}

		public void setMainClass(String mainClass) {
			this.mainClass.set(mainClass);
		}

		public void setVersion(String version) {
			this.version.set(version);
		}

		public StringProperty idProperty() {
			return id;
		}

		public StringProperty nameProperty() {
			return name;
		}

		public StringProperty jarPathProperty() {
			return jarPath;
		}

		public StringProperty mainClassProperty() {
			return mainClass;
		}

		public StringProperty versionProperty() {
			return version;
		}

		public ObjectProperty<List<String>> authorsProperty() {
			return authors;
		}

		public ObjectProperty<List<String>> dependenciesProperty() {
			return dependencies;
		}
	}

	public AddonDialog() {
		super(I18N.translate("dialog_addons"), I18N.translate("dialog_addons"), "", Alert.AlertType.INFORMATION);
	}

	@Override
	public DialogPane createDialog(DialogPane dialogPane) {

		ObservableList<AddonInfoWrapper> infoWrappers = FXCollections.observableArrayList();

		AddonLoader.ADDONS.values().forEach(addon -> infoWrappers.add(new AddonInfoWrapper(addon.getAddonInfo())));

		TableView<AddonInfoWrapper> table = new TableView<>();
		table.setItems(infoWrappers);

		TableColumn<AddonInfoWrapper, String> id = new TableColumn<>(I18N.translate("table_id"));
		TableColumn<AddonInfoWrapper, String> name = new TableColumn<>(I18N.translate("table_name"));
		TableColumn<AddonInfoWrapper, String> version = new TableColumn<>(I18N.translate("table_version"));
		TableColumn<AddonInfoWrapper, String> mainClass = new TableColumn<>(I18N.translate("table_main_class"));
		TableColumn<AddonInfoWrapper, String> jarPath = new TableColumn<>(I18N.translate("table_jar_path"));
		TableColumn<AddonInfoWrapper, List<String>> authors = new TableColumn<>(I18N.translate("table_authors"));
		TableColumn<AddonInfoWrapper, List<String>> dependencies = new TableColumn<>(I18N.translate("table_dependencies"));

		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		version.setCellValueFactory(new PropertyValueFactory<>("version"));
		mainClass.setCellValueFactory(new PropertyValueFactory<>("mainClass"));
		jarPath.setCellValueFactory(new PropertyValueFactory<>("jarPath"));
		authors.setCellValueFactory(new PropertyValueFactory<>("authors"));
		dependencies.setCellValueFactory(new PropertyValueFactory<>("dependencies"));

		table.getColumns().add(id);
		table.getColumns().add(name);
		table.getColumns().add(authors);
		table.getColumns().add(version);
		table.getColumns().add(jarPath);
		table.getColumns().add(dependencies);
		table.getColumns().add(mainClass);

		dialogPane.setContent(table);
		dialogPane.setPrefWidth(800);

		return dialogPane;
	}
}
