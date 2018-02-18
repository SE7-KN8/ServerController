package se7kn8.servercontroller.app.util;

public interface ActionModeFragment {
	void setNullToActionMode();
	boolean handleAction(int id);
	int getMenuID();
}
