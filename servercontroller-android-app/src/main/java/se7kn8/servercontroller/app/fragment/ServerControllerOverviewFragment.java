package se7kn8.servercontroller.app.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.util.ServerControllerConnection;

public class ServerControllerOverviewFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
	private static final String STATE_CONNECTION = "connection";
	private ServerControllerConnection connection;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState == null){
			if(getArguments() != null){
				connection = (ServerControllerConnection) getArguments().getSerializable("connection");
			}
		}else {
			connection = (ServerControllerConnection) savedInstanceState.getSerializable(STATE_CONNECTION);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		createFragment(new ServerListFragment());

		View view = inflater.inflate(R.layout.fragment_servercontroller_overview, container, false);
		BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(this);

		return view;
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()){
			case R.id.item_servers:
				createFragment(new ServerListFragment());
				return true;
			case R.id.item_console:
				createFragment(new ServerConsoleFragment());
				return true;
			case R.id.item_info:
				createFragment(new ServerControllerInfoFragment());
				return true;
			default:
				return false;
		}
	}

	private void createFragment(Fragment fragment){
		Bundle args = new Bundle();
		args.putSerializable("connection", connection);
		fragment.setArguments(args);
		getChildFragmentManager().beginTransaction().replace(R.id.overview_fragment_container, fragment).commit();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATE_CONNECTION, connection);
	}
}
