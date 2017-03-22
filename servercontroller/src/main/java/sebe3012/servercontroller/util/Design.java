package sebe3012.servercontroller.util;

/**
 * Created by Sebe3012 on 18.02.2017.
 * A design for the gui
 */
public class Design {

	private String stylesheet;
	private String id;

	public Design(String stylesheet, String id) {
		this.stylesheet = stylesheet;
		this.id = id;
	}

	public String getStylesheet() {
		return stylesheet;
	}

	public String getId() {
		return id;
	}

	public String getLocalizedName() {
		return I18N.translate("design_" + id);
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}
}
