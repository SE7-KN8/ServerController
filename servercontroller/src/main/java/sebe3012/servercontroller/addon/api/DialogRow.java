package sebe3012.servercontroller.addon.api;

import java.util.function.Predicate;

/**
 * Created by Sebe3012 on 15.01.2017.
 * This class is a wrapper for parameters that can used
 */
public class DialogRow {

	private String name;
	private String promptText;
	private String fileType;
	private String fileExtension;
	private String propertyName;
	private String defaultValue;
	private boolean usingFileChooser;
	private Predicate<String> stringPredicate;
	private int prefHeight;
	private int prefWidth;

	public String getName() {
		return name;
	}

	public String getPromptText() {
		return promptText;
	}

	public Predicate<String> getStringPredicate() {
		return stringPredicate;
	}

	public boolean isUsingFileChooser() {
		return usingFileChooser;
	}

	public int getPrefHeight() {
		return prefHeight;
	}

	public int getPrefWidth() {
		return prefWidth;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getFileType() {
		return fileType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public DialogRow() {
		stringPredicate = StringPredicates.DO_NOTHING;
		promptText = "";
		name = "";
		defaultValue = "";
		prefWidth = 235;
		prefHeight = 53;
		propertyName = "";
	}

	public DialogRow setName(String name) {
		this.name = name;
		return this;
	}

	public DialogRow setPromptText(String promptText) {
		this.promptText = promptText;
		return this;
	}

	public DialogRow setStringPredicate(Predicate<String> stringPredicate) {
		this.stringPredicate = stringPredicate;
		return this;
	}

	public DialogRow setUsingFileChooser(boolean usingFileChooser) {
		this.usingFileChooser = usingFileChooser;
		return this;
	}

	public DialogRow setPrefHeight(int prefHeight) {
		this.prefHeight = prefHeight;
		return this;
	}

	public DialogRow setPrefWidth(int prefWidth) {
		this.prefWidth = prefWidth;
		return this;
	}

	public DialogRow setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
		return this;
	}

	public DialogRow setFileType(String fileType) {
		this.fileType = fileType;
		return this;
	}

	public DialogRow setPropertyName(String propertyName) {
		this.propertyName = propertyName;
		return this;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
