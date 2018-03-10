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

public class ServerControllerOverviewFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

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
		switch (item.getItemId()) {
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

	private void createFragment(Fragment fragment) {
		getChildFragmentManager().beginTransaction().replace(R.id.overview_fragment_container, fragment).commit();
	}
}
