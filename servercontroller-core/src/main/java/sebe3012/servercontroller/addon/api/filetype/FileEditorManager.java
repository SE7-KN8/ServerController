package sebe3012.servercontroller.addon.api.filetype;

import sebe3012.servercontroller.api.gui.fileeditor.FileEditor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class FileEditorManager {

	public static class FileEditorEntry {
		private Class<? extends FileEditor> editorClass;
		private ResourceBundle resourceBundle;
		private String name;
		private Node fileGraphic;

		public FileEditorEntry(@NotNull Class<? extends FileEditor> editorClass, @NotNull ResourceBundle resourceBundle, @NotNull String name, @Nullable Node fileGraphic) {
			this.editorClass = editorClass;
			this.resourceBundle = resourceBundle;
			this.name = name;
			this.fileGraphic = fileGraphic;
		}

		@NotNull
		public Class<? extends FileEditor> getEditorClass() {
			return editorClass;
		}

		@Nullable
		public Node getFileGraphic() {
			return fileGraphic;
		}

		@NotNull
		public String getName() {
			return name;
		}

		@NotNull
		public String getLocalizedName(){
			return resourceBundle.getString(name);
		}
	}

	private Map<String, List<FileEditorEntry>> fileEditors;

	public FileEditorManager() {
		fileEditors = new HashMap<>();
	}

	public void registerFileEditor(@NotNull List<String> fileType, @NotNull Class<? extends FileEditor> editor, @NotNull String name, @NotNull ResourceBundle translateBundle, @Nullable Node graphic) {
		fileType.forEach(type -> {
			if (fileEditors.get(type) == null) {
				List<FileEditorEntry> editors = new ArrayList<>();
				editors.add(new FileEditorEntry(editor, translateBundle, name, graphic));
				fileEditors.put(type, editors);
			} else {
				fileEditors.get(type).add(new FileEditorEntry(editor, translateBundle, name, graphic));
			}
		});
	}

	@NotNull
	public List<FileEditorEntry> getFileEditors(@NotNull String fileType) {
		fileEditors.computeIfAbsent(fileType, t -> new ArrayList<>());
		return fileEditors.get(fileType);
	}

}
