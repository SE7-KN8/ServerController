package sebe3012.servercontroller.api.gui.server;

import sebe3012.servercontroller.api.util.StringPredicates;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.function.Predicate;

/**
 * Created by Sebe3012 on 15.01.2017.
 * This class is a wrapper for parameters that can used
 */
public class DialogRow {
	private StringProperty name;
	private StringProperty promptText;
	private StringProperty fileType;
	private StringProperty fileExtension;
	private StringProperty propertyName;
	private StringProperty defaultValue;
	private BooleanProperty usingFileChooser;
	private ObjectProperty<Predicate<String>> stringPredicate;
	private IntegerProperty prefHeight;
	private IntegerProperty prefWidth;

	public DialogRow() {
		stringPredicate = new SimpleObjectProperty<>(StringPredicates.DO_NOTHING);
		promptText = new SimpleStringProperty();
		name = new SimpleStringProperty();
		defaultValue = new SimpleStringProperty();
		prefWidth = new SimpleIntegerProperty(253);
		prefHeight = new SimpleIntegerProperty(53);
		propertyName = new SimpleStringProperty();
		fileType = new SimpleStringProperty();
		fileExtension = new SimpleStringProperty();
		usingFileChooser = new SimpleBooleanProperty();
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}


	public String getPromptText() {
		return promptText.get();
	}

	public StringProperty promptTextProperty() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText.set(promptText);
	}


	public String getFileType() {
		return fileType.get();
	}

	public StringProperty fileTypeProperty() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType.set(fileType);
	}


	public String getFileExtension() {
		return fileExtension.get();
	}

	public StringProperty fileExtensionProperty() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension.set(fileExtension);
	}


	public String getPropertyName() {
		return propertyName.get();
	}

	public StringProperty propertyNameProperty() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName.set(propertyName);
	}


	public String getDefaultValue() {
		return defaultValue.get();
	}

	public StringProperty defaultValueProperty() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue.set(defaultValue);
	}


	public boolean isUsingFileChooser() {
		return usingFileChooser.get();
	}

	public BooleanProperty usingFileChooserProperty() {
		return usingFileChooser;
	}

	public void setUsingFileChooser(boolean usingFileChooser) {
		this.usingFileChooser.set(usingFileChooser);
	}


	public Predicate<String> getStringPredicate() {
		return stringPredicate.get();
	}

	public ObjectProperty<Predicate<String>> stringPredicateProperty() {
		return stringPredicate;
	}

	public void setStringPredicate(Predicate<String> stringPredicate) {
		this.stringPredicate.set(stringPredicate);
	}


	public int getPrefHeight() {
		return prefHeight.get();
	}

	public IntegerProperty prefHeightProperty() {
		return prefHeight;
	}

	public void setPrefHeight(int prefHeight) {
		this.prefHeight.set(prefHeight);
	}


	public int getPrefWidth() {
		return prefWidth.get();
	}

	public IntegerProperty prefWidthProperty() {
		return prefWidth;
	}

	public void setPrefWidth(int prefWidth) {
		this.prefWidth.set(prefWidth);
	}
}
