package sebe3012.servercontroller.gui.editor;

import sebe3012.servercontroller.api.gui.fileeditor.FileEditor;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.net.MalformedURLException;
import java.nio.file.Path;

public class BasicImageViewer implements FileEditor {

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
