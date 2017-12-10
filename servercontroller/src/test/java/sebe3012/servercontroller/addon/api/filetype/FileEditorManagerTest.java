package sebe3012.servercontroller.addon.api.filetype;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import javafx.scene.Node;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileEditorManagerTest {

	private class TestFileHandler1 implements FileEditor {
		@Override
		public void openFile(@NotNull Path file) {

		}

		@NotNull
		@Override
		public Path getItem() {
			return null;
		}

		@Override
		public void setItem(@NotNull Path item) {

		}

		@NotNull
		@Override
		public String getTitle() {
			return null;
		}

		@NotNull
		@Override
		public Node getContent() {
			return null;
		}

		@Override
		public boolean isCloseable() {
			return false;
		}
	}

	private class TestFileHandler2 implements FileEditor {
		@Override
		public void openFile(@NotNull Path file) {

		}

		@NotNull
		@Override
		public Path getItem() {
			return null;
		}

		@Override
		public void setItem(@NotNull Path item) {

		}

		@NotNull
		@Override
		public String getTitle() {
			return null;
		}

		@NotNull
		@Override
		public Node getContent() {
			return null;
		}

		@Override
		public boolean isCloseable() {
			return false;
		}
	}

	private FileEditorManager manager;

	@Before
	public void before() {
		manager = new FileEditorManager();
	}

	@Test
	public void registerFileEditor() {
		List<String> fileTypes1 = new ArrayList<>();
		fileTypes1.add("txt");
		fileTypes1.add("png");
		List<String> fileTypes2 = new ArrayList<>();
		fileTypes2.add("cpp");
		fileTypes2.add("java");
		manager.registerFileEditor(fileTypes1, TestFileHandler1.class, "tfh1", null, null);
		manager.registerFileEditor(fileTypes2, TestFileHandler2.class, "tfh1", null, null);
		assertEquals(TestFileHandler1.class, manager.getFileEditors("txt").get(0).getEditorClass());
		assertEquals(TestFileHandler1.class, manager.getFileEditors("png").get(0).getEditorClass());
		assertEquals(TestFileHandler2.class, manager.getFileEditors("cpp").get(0).getEditorClass());
		assertEquals(TestFileHandler2.class, manager.getFileEditors("java").get(0).getEditorClass());
	}

	@Test
	public void getFileEditors() {
		assertTrue(manager.getFileEditors("notfound") != null);
	}

}
