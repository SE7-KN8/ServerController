package se7kn8.servercontroller.api.util;

import javafx.scene.control.TextField;

public class NumberField extends TextField {

	@Override
	public void replaceText(int start, int end, String text) {
		if (text.matches("[0-9]") || text.equals("")) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		if (text.matches("[0-9]") || text.equals("")) {
			super.replaceSelection(text);
		}
	}

}