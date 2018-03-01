package se7kn8.servercontroller.app.fragment;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

public class ServerListFragment extends Fragment implements Response.Listener<ServerControllerServers>, Response.ErrorListener, SwipeRefreshLayout.OnRefreshListener {
	private static final String STATE_CONNECTION = "connection";
	private static final String STATE_SERVERS = "servers";

	private ServerControllerConnection mConnection;
	private ArrayList<ServerControllerServers.ServerControllerServer> mServers;
	private ServerListAdapter mAdapter;

	private SwipeRefreshLayout mSwipeRefresh;

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			if (getArguments() != null) {
				mConnection = (ServerControllerConnection) getArguments().getSerializable("connection");
			}
			mServers = new ArrayList<>();
		} else {
			mConnection = (ServerControllerConnection) savedInstanceState.getSerializable(STATE_CONNECTION);
			mServers = (ArrayList<ServerControllerServers.ServerControllerServer>) savedInstanceState.getSerializable(STATE_SERVERS);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_server_list, container, false);

		mSwipeRefresh = view.findViewById(R.id.server_list_swipe);
		mSwipeRefresh.setOnRefreshListener(this);

		RecyclerView recyclerView = view.findViewById(R.id.server_list_recycler);
		mAdapter = new ServerListAdapter(mServers);
		recyclerView.setAdapter(mAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		loadData();

		return view;
	}

	private void loadData(){
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(mConnection.toURL() + "servers/", this, ServerControllerServers.class, mConnection.getApiKey(), this), this.getContext());
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
	}

	@Override
	public void onResponse(ServerControllerServers response) {
		mServers.clear();
		mServers.addAll(response.getServerList());
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATE_CONNECTION, mConnection);
		outState.putSerializable(STATE_SERVERS, mServers);
	}

	@Override
	public void onRefresh() {
		loadData();
		mSwipeRefresh.setRefreshing(false);
	}
}
