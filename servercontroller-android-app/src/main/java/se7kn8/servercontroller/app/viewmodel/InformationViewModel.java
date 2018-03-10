package se7kn8.servercontroller.app.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import se7kn8.servercontroller.api.rest.ServerControllerAddons;
import se7kn8.servercontroller.api.rest.ServerControllerPermissions;

import java.util.ArrayList;
import java.util.List;

public class InformationViewModel extends ViewModel {

	private List<ServerControllerAddons.ServerControllerAddonInfo> mAddons = new ArrayList<>();
	private List<ServerControllerPermissions.ServerControllerPermission> mPermissions = new ArrayList<>();

	@NonNull
	public List<ServerControllerAddons.ServerControllerAddonInfo> getAddons() {
		return mAddons;
	}

	@NonNull
	public List<ServerControllerPermissions.ServerControllerPermission> getPermissions() {
		return mPermissions;
	}
}
