package se7kn8.servercontroller.gui.editor;

import se7kn8.servercontroller.api.gui.fileeditor.FileEditor;
import se7kn8.servercontroller.api.gui.fileeditor.FileEditorCreator;
import se7kn8.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BasicImageViewer implements FileEditor {

	public static class BasicImageViewerCreator implements FileEditorCreator {

		@NotNull
		@Override
		public Class<? extends FileEditor> getFileEditorClass() {
			return BasicImageViewer.class;
		}

		@NotNull
		@Override
		public List<String> getFileTypes() {
			List<String> imageFileTypes = new ArrayList<>();
			imageFileTypes.add("png");
			imageFileTypes.add("jpg");
			imageFileTypes.add("jpeg");
			imageFileTypes.add("gif");
			return imageFileTypes;
		}

		@NotNull
		@Override
		public String getID() {
			return "basic_image_viewer";
		}

		@NotNull
		@Override
		public ResourceBundle getBundleToTranslate() {
			return I18N.getDefaultBundle();
		}
	}

	private Path filePath;
	private StackPane root;

	@Override
	public void openFile(@NotNull Path file) {
		this.filePath = file;
		root = new StackPane();
		try {
			root.getChildren().add(new ImageView(new Image(file.toUri().toURL().toExternalForm())));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
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
		return root;
	}

	@Override
	public boolean isCloseable() {
		return true;
	}
}
