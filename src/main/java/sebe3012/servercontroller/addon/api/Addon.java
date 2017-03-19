package sebe3012.servercontroller.addon.api;

/**
 * Created by Sebe3012 on 28.02.2017.
 * Basic type for all addons
 */
public abstract class Addon {

	private AddonInfo addonInfo;

	protected Addon(){}

	public abstract void load();
	public abstract void unload();

	public final void setAddonInfo(AddonInfo addonInfo) {
		this.addonInfo = addonInfo;
	}
	public final AddonInfo getAddonInfo(){
		return addonInfo;
	}


}
