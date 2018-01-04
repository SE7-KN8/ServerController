package se7kn8.servercontroller.gui.editor;

import se7kn8.servercontroller.api.gui.fileeditor.FileEditor;
import se7kn8.servercontroller.api.gui.fileeditor.FileEditorCreator;
import se7kn8.servercontroller.api.util.DialogUtil;
import se7kn8.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BasicTextEditor implements FileEditor {

	public static class BasicTextEditorCreator implements FileEditorCreator {

		@NotNull
		@Override
		public Class<? extends FileEditor> getFileEditorClass() {
			return BasicTextEditor.class;
		}

		@NotNull
		@Override
		public List<String> getFileTypes() {
			List<String> textFileTypes = new ArrayList<>();
			textFileTypes.add("txt");
			textFileTypes.add("properties");
			textFileTypes.add("log");
			textFileTypes.add("json");
			textFileTypes.add("xml");
			textFileTypes.add("yml");
			return textFileTypes;
		}

		@NotNull
		@Override
		public String getID() {
			return "basic_text_editor";
		}

		@NotNull
		@Override
		public ResourceBundle getBundleToTranslate() {
			return I18N.getDefaultBundle();
		}
	}

	private class ReadFileTask extends Task<String> {

		private Path path;

		ReadFileTask(Path path) {
			this.path = path;
		}

		@NotNull
		@Override
		protected String call() throws Exception {
			if (Files.isDirectory(path)) {
				return "";
			}

			StringBuilder sb = new StringBuilder();

			try {
				String line;
				BufferedReader reader = Files.newBufferedReader(path);

				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}

				reader.close();
			} catch (MalformedInputException e) {
				Platform.runLater(() -> DialogUtil.showErrorAlert(I18N.translate("dialog_file_cannot_load_header"), I18N.format("dialog_file_cannot_load", path.toFile())));
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}

			return sb.toString();
		}
	}

	private class SaveFileTask extends Task<Void> {

		private Path filePath;
		private String fileContent;

		SaveFileTask(Path filePath, String fileContent) {
			this.filePath = filePath;
			this.fileContent = fileContent;
		}

		@Override
		protected Void call() {
			try {
				PrintWriter ps = new PrintWriter(Files.newBufferedWriter(filePath));
				ps.write(fileContent);
				ps.close();
			} catch (Exception e) {
				Platform.runLater(() -> DialogUtil.showErrorAlert(I18N.translate("dialog_file_cannot_save_header"), I18N.format("dialog_file_cannot_save", filePath.toFile())));
			}

			return null;
		}
	}

	private Path filePath;

	@FXML
	private VBox rootPane;

	@FXML
	private TextArea textArea;

	@FXML
	private void onSaveClicked() {
		saveFile();
	}

	@FXML
	private void onKeyPressed(KeyEvent event) {
		if (event.isControlDown() && event.getCode() == KeyCode.S) {
			saveFile();
		}
	}

	private void saveFile() {
		Task<Void> saveFileTask = new SaveFileTask(filePath, textArea.getText());
		saveFileTask.setOnSucceeded(event -> DialogUtil.showInformationAlert(I18N.translate("dialog_file_saved_header"), I18N.format("dialog_file_saved", filePath.toString())));
		new Thread(saveFileTask).start();
	}

	@FXML
	void initialize() {
		Task<String> readFileTask = new ReadFileTask(filePath);
		readFileTask.setOnSucceeded(event -> textArea.setText(readFileTask.getValue()));
		new Thread(readFileTask).start();
	}

	@NotNull
	@Override
	public Path getItem() {
		return filePath;
	}

	@Override
	public void setItem(@NotNull Path item) {
		this.filePath = item;
	}

	@NotNull
	@Override
	public String getTitle() {
		return filePath.getFileName().toString();
	}

	@NotNull
	@Override
	public Node getContent() {
		return rootPane;
	}

	@Override
	public boolean isCloseable() {
		return true;
	}

	@Override
	public void openFile(@NotNull Path file) {
		this.filePath = file;

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setResources(I18N.getDefaultBundle());
			loader.setLocation(ClassLoader.getSystemResource("fxml/tab/BasicTextEditorTab.fxml"));
			loader.load();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
