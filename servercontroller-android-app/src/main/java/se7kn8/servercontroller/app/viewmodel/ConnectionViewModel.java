package se7kn8.servercontroller.app.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import se7kn8.servercontroller.app.util.ServerControllerConnection;

import java.util.ArrayList;
import java.util.List;

public class ConnectionViewModel extends ViewModel {
	private List<ServerControllerConnection> mConnections = new ArrayList<>();

	public void addConnection(@NonNull ServerControllerConnection connection) {
		mConnections.add(connection);
	}

	public void removeConnection(@NonNull ServerControllerConnection connection) {
		mConnections.remove(connection);
	}

	public void removeConnection(int index) {
		mConnections.remove(index);
	}

	@NonNull
	public List<ServerControllerConnection> getConnections() {
		return mConnections;
	}

	private ServerControllerConnection mCurrentConnection;

	public void setCurrentConnection(@NonNull ServerControllerConnection currentConnection) {
		this.mCurrentConnection = currentConnection;
	}

	@NonNull
	public ServerControllerConnection getCurrentConnection() {
		if (mCurrentConnection == null) {
			throw new IllegalStateException("No connection set!");
		} else {
			return mCurrentConnection;
		}
	}
}
