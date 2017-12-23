package sebe3012.servercontroller.api.gui.fileeditor;

import sebe3012.servercontroller.api.gui.tab.TabEntry;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface FileEditor extends TabEntry<Path>{
	void openFile(@NotNull Path file);
}
