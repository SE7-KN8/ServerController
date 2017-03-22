package sebe3012.servercontroller.settings;

import sebe3012.servercontroller.preferences.PreferencesConstants;
import sebe3012.servercontroller.preferences.ServerControllerPreferences;
import sebe3012.servercontroller.util.I18N;

/**
 * Created by Sebe3012 on 22.02.2017.
 * A row for the settings dialog to control the settings
 */
public class SettingsRow {

	private String id;
	private boolean defaultValue;
	private boolean value;

	public SettingsRow(String id, boolean defaultValue) {
		this.id = id;
		this.defaultValue = defaultValue;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public String getLocalizedName() {
		return I18N.translate("settings_" + id);
	}

	public void load() {
		this.value = ServerControllerPreferences.loadBooleanSetting(PreferencesConstants.SETTINGS_PREFIX + id, defaultValue);
	}

	public void save() {
		ServerControllerPreferences.saveBooleanSetting(PreferencesConstants.SETTINGS_PREFIX + id, value);
	}

}
