package sebe3012.servercontroller.addon.api;

/**
 * Created by Sebe3012 on 28.02.2017.
 * Basic type for all addons
 */
public abstract class Addon {

	private AddonInfo addonInfo;
	private boolean loaded;

	protected Addon() {
	}


	public final void loadAddon() {
		if (!loaded) {
			load();
			loaded = true;
		} else {
			throw new RuntimeException("Addon is already loaded!");
		}
	}

	public final void unloadAddon() {
		if (loaded) {
			unload();
			loaded = false;
		} else {
			throw new RuntimeException("Addon is already unloaded");
		}
	}

	public final boolean isLoaded() {
		return loaded;
	}

	protected abstract void load();

	protected abstract void unload();

	public final void setAddonInfo(AddonInfo addonInfo) {
		this.addonInfo = addonInfo;
	}

	public final AddonInfo getAddonInfo() {
		return addonInfo;
	}
}
