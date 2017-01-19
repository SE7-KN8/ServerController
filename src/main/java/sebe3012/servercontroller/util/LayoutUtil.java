package sebe3012.servercontroller.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Created by Sebe3012 on 16.01.2017.
 * This class contains static methods for better layout handling
 */
public class LayoutUtil {

	private static Logger log = LogManager.getLogger();

	/**
	 *-1 will disabling a number setting
	 *
	 * @param layout The GridPane
	 * @param minWidth The minimal width of the column
	 * @param prefWidth The prefers width of the column
	 * @param maxWidth The max width of the column
	 * @param horizontalGrow The horizontal grow of the column
	 */
	public static void addColumnConstraints(GridPane layout, int minWidth, int prefWidth, int maxWidth, Priority horizontalGrow) {
		if (layout != null) {
			ColumnConstraints columnConstraints = new ColumnConstraints();
			if (horizontalGrow != null) {
				columnConstraints.setHgrow(horizontalGrow);
			}
			if (minWidth != -1) {
				columnConstraints.setMinWidth(minWidth);
			}
			if (prefWidth != -1) {
				columnConstraints.setPrefWidth(prefWidth);
			}
			if (maxWidth != -1) {
				columnConstraints.setMaxWidth(maxWidth);
			}
			layout.getColumnConstraints().add(columnConstraints);
			log.debug("Added column constraints {}", columnConstraints);
		} else {
			log.warn("Could not add column constraints! Layout is null!");
		}

	}

	/**
	 *-1 will disabling a number setting
	 *
	 * @param layout The GridPane
	 * @param minHeight The minimal height of the row
	 * @param prefHeight The prefers height of the row
	 * @param maxHeight The max height of the row
	 * @param verticalGrow The vertical grow of the row
	 */
	public static void addRowConstraints(@Nullable GridPane layout, int minHeight, int prefHeight, int maxHeight, @Nullable Priority verticalGrow) {
		if (layout != null) {
			RowConstraints rowConstraints = new RowConstraints();

			if (verticalGrow != null) {
				rowConstraints.setVgrow(verticalGrow);
			}
			if (minHeight != -1) {
				rowConstraints.setMinHeight(minHeight);
			}
			if (prefHeight != -1) {
				rowConstraints.setPrefHeight(prefHeight);
			}
			if (maxHeight != -1) {
				rowConstraints.setMaxHeight(maxHeight);
			}
			layout.getRowConstraints().add(rowConstraints);
			log.debug("Added row constraints {}", rowConstraints);
		} else {
			log.warn("Could not add row constraints! Layout is null!");
		}
	}
}
