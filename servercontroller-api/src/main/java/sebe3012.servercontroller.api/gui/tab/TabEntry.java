package sebe3012.servercontroller.api.gui.tab;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

public interface TabEntry<T> {

	@Deprecated //Currently no use
	@NotNull
	T getItem();


	@Deprecated //Currently no use
	void setItem(@NotNull T item);

	@NotNull
	String getTitle();

	@NotNull
	Node getContent();

	boolean isCloseable();

	@Nullable
	default Node getGraphic(){
		return null;
	}

	@Nullable
	default ContextMenu getContextMenu(){
		return null;
	}

	default void onSelect(){
		//Do nothing
	}

	default boolean onClose(){
		//Do nothing
		return true;
	}

	default void refresh(){
		//Do nothing
	}

}
