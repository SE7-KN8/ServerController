package se7kn8.servercontroller.app.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se7kn8.servercontroller.api.rest.ServerControllerAddons;
import se7kn8.servercontroller.api.rest.ServerControllerPermissions;
import se7kn8.servercontroller.api.rest.ServerControllerVersion;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.adapter.AddonListAdapter;
import se7kn8.servercontroller.app.adapter.StringListAdapter;
import se7kn8.servercontroller.app.util.GsonRequest;
import se7kn8.servercontroller.app.util.ServerControllerConnection;
import se7kn8.servercontroller.app.util.VolleyRequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

public class ServerControllerInfoFragment extends Fragment {

	private static final String STATE_CONNECTION = "connection";
	private static final String STATE_ADDONS = "addons";
	private static final String STATE_PERMISSIONS = "permissions";

	private ServerControllerConnection mConnection;

	private TextView mVersion;
	private TextView mApiVersion;

	private ArrayList<ServerControllerAddons.ServerControllerAddonInfo> mAddons;
	private AddonListAdapter mAddonAdapter;

	private ArrayList<String> mPermissions;
	private StringListAdapter mPermissionsAdapter;

	private class ServerControllerVersionReceiver implements Response.Listener<ServerControllerVersion>, Response.ErrorListener {

		@Override
		public void onResponse(ServerControllerVersion response) {
			mVersion.setText(getString(R.string.servercontroller_version, response.getVersion()));
			mApiVersion.setText(getString(R.string.servercontroller_api_version, response.getApiVersion()));
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
		}
	}

	private class ServerControllerAddonsReceiver implements Response.Listener<ServerControllerAddons>, Response.ErrorListener {

		@Override
		public void onResponse(ServerControllerAddons response) {
			mAddons.clear();
			mAddons.addAll(response.getAddons());
			mAddonAdapter.notifyDataSetChanged();
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
		}
	}

	private class ServerControllerPermissionsReceiver implements Response.Listener<ServerControllerPermissions>, Response.ErrorListener {

		@Override
		public void onResponse(ServerControllerPermissions response) {
			mPermissions.clear();

			List<String> permissions = new ArrayList<>();
			for (ServerControllerPermissions.ServerControllerPermission serverControllerPermission : response.getPermissionList()) {
				permissions.add(serverControllerPermission.getName());
			}
			mPermissions.addAll(permissions);
			mPermissionsAdapter.notifyDataSetChanged();
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			if (getArguments() != null) {
				mConnection = (ServerControllerConnection) getArguments().getSerializable("connection");
				mAddons = new ArrayList<>();
				mPermissions = new ArrayList<>();
			}
		} else {
			mConnection = (ServerControllerConnection) savedInstanceState.getSerializable(STATE_CONNECTION);
			mAddons = (ArrayList<ServerControllerAddons.ServerControllerAddonInfo>) savedInstanceState.getSerializable(STATE_ADDONS);
			mPermissions = (ArrayList<String>) savedInstanceState.getSerializable(STATE_PERMISSIONS);
		}

	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_servercontroller_info, container, false);

		mVersion = layout.findViewById(R.id.text_view_version);
		mApiVersion = layout.findViewById(R.id.text_view_api_version);

		ServerControllerVersionReceiver versionReceiver = new ServerControllerVersionReceiver();
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(mConnection.toURL() + "version/", versionReceiver, ServerControllerVersion.class, mConnection.getApiKey(), versionReceiver), getContext());

		mAddonAdapter = new AddonListAdapter(mAddons);
		RecyclerView recyclerViewAddons = layout.findViewById(R.id.info_addon_recycler);
		LinearLayoutManager manager = new LinearLayoutManager(getContext()){
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		recyclerViewAddons.setLayoutManager(manager);
		DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), manager.getOrientation());
		recyclerViewAddons.addItemDecoration(itemDecoration);
		recyclerViewAddons.setAdapter(mAddonAdapter);

		ServerControllerAddonsReceiver addonsReceiver = new ServerControllerAddonsReceiver();
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(mConnection.toURL() + "addons/", addonsReceiver, ServerControllerAddons.class, mConnection.getApiKey(), addonsReceiver), getContext());

		mPermissionsAdapter = new StringListAdapter(mPermissions);
		RecyclerView recyclerViewPermissions = layout.findViewById(R.id.info_permissions_recycler);
		manager = new LinearLayoutManager(getContext()){
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		recyclerViewPermissions.setLayoutManager(manager);
		itemDecoration = new DividerItemDecoration(getContext(), manager.getOrientation());
		recyclerViewPermissions.addItemDecoration(itemDecoration);
		recyclerViewPermissions.setAdapter(mPermissionsAdapter);

		ServerControllerPermissionsReceiver permissionsReceiver = new ServerControllerPermissionsReceiver();
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(mConnection.toURL() + "user/permissions/", permissionsReceiver, ServerControllerPermissions.class, mConnection.getApiKey(), permissionsReceiver), getContext());

		return layout;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATE_CONNECTION, mConnection);
		outState.putSerializable(STATE_ADDONS, mAddons);
		outState.putSerializable(STATE_PERMISSIONS, mPermissions);
	}
}
