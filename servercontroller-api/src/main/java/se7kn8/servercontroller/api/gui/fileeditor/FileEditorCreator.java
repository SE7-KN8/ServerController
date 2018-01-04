package se7kn8.servercontroller.api.gui.fileeditor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;

import java.util.List;
import java.util.ResourceBundle;

public interface FileEditorCreator {

	@NotNull
	Class<? extends FileEditor> getFileEditorClass();

	@NotNull
	List<String> getFileTypes();

	@NotNull
	String getID();

	@NotNull
	ResourceBundle getBundleToTranslate();

	@Nullable
	default Node getFileGraphic() {
		//Do nothing
		return null;
	}
}
