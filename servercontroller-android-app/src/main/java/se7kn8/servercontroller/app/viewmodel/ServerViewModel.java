package se7kn8.servercontroller.app.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import se7kn8.servercontroller.api.rest.ServerControllerServers;

import java.util.ArrayList;
import java.util.List;

public class ServerViewModel extends ViewModel {
	private List<ServerControllerServers.ServerControllerServer> mServers = new ArrayList<>();

	@NonNull
	public List<ServerControllerServers.ServerControllerServer> getServers() {
		return mServers;
	}
}
