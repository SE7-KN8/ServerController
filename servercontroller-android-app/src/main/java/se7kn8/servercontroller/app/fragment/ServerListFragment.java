package se7kn8.servercontroller.app.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se7kn8.servercontroller.api.rest.ServerControllerServers;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.adapter.ServerListAdapter;
import se7kn8.servercontroller.app.util.GsonRequest;
import se7kn8.servercontroller.app.util.ServerControllerConnection;
import se7kn8.servercontroller.app.util.VolleyRequestQueue;
import se7kn8.servercontroller.app.viewmodel.ConnectionViewModel;
import se7kn8.servercontroller.app.viewmodel.ServerViewModel;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ServerListFragment extends Fragment implements Response.Listener<ServerControllerServers>, Response.ErrorListener, SwipeRefreshLayout.OnRefreshListener {
	private ServerListAdapter mAdapter;
	private ConnectionViewModel mConnectionViewModel;
	private ServerViewModel mServerViewModel;
	private SwipeRefreshLayout mSwipeRefresh;

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mConnectionViewModel = ViewModelProviders.of(requireActivity()).get(ConnectionViewModel.class);
		mServerViewModel = ViewModelProviders.of(requireActivity()).get(ServerViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_server_list, container, false);

		mSwipeRefresh = view.findViewById(R.id.server_list_swipe);
		mSwipeRefresh.setOnRefreshListener(this);

		RecyclerView recyclerView = view.findViewById(R.id.server_list_recycler);
		mAdapter = new ServerListAdapter(mServerViewModel.getServers(), mConnectionViewModel.getCurrentConnection());
		recyclerView.setAdapter(mAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		loadData();

		return view;
	}

	private void loadData(){
		ServerControllerConnection connection = mConnectionViewModel.getCurrentConnection();
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(connection.toURL() + "servers/", this, ServerControllerServers.class, connection.getApiKey(), this), this.getContext());
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
	}

	@Override
	public void onResponse(ServerControllerServers response) {
		mServerViewModel.getServers().clear();
		mServerViewModel.getServers().addAll(response.getServerList());
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		loadData();
		mSwipeRefresh.setRefreshing(false);
	}
}
