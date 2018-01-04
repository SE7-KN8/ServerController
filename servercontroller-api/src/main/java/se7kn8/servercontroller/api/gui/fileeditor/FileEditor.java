package se7kn8.servercontroller.api.gui.fileeditor;

import se7kn8.servercontroller.api.gui.tab.TabEntry;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface FileEditor extends TabEntry<Path> {
	void openFile(@NotNull Path file);
}
