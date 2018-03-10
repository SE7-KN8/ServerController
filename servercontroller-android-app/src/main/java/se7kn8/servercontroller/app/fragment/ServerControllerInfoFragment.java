package se7kn8.servercontroller.app.fragment;

import android.arch.lifecycle.ViewModelProviders;
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
import se7kn8.servercontroller.app.adapter.PermissionListAdapter;
import se7kn8.servercontroller.app.util.GsonRequest;
import se7kn8.servercontroller.app.util.ServerControllerConnection;
import se7kn8.servercontroller.app.util.VolleyRequestQueue;
import se7kn8.servercontroller.app.viewmodel.ConnectionViewModel;
import se7kn8.servercontroller.app.viewmodel.InformationViewModel;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ServerControllerInfoFragment extends Fragment {

	private TextView mVersion;
	private TextView mApiVersion;

	private AddonListAdapter mAddonAdapter;

	private PermissionListAdapter mPermissionsAdapter;

	private InformationViewModel mInformationViewModel;

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
			mInformationViewModel.getAddons().clear();
			mInformationViewModel.getAddons().addAll(response.getAddons());
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
			mInformationViewModel.getPermissions().clear();
			mInformationViewModel.getPermissions().addAll(response.getPermissionList());
			mPermissionsAdapter.notifyDataSetChanged();
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInformationViewModel = ViewModelProviders.of(requireActivity()).get(InformationViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_servercontroller_info, container, false);

		mVersion = layout.findViewById(R.id.text_view_version);
		mApiVersion = layout.findViewById(R.id.text_view_api_version);

		ServerControllerConnection currentConnection = ViewModelProviders.of(requireActivity()).get(ConnectionViewModel.class).getCurrentConnection();

		ServerControllerVersionReceiver versionReceiver = new ServerControllerVersionReceiver();
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(currentConnection.toURL() + "version/", versionReceiver, ServerControllerVersion.class, currentConnection.getApiKey(), versionReceiver), requireContext());

		mAddonAdapter = new AddonListAdapter(mInformationViewModel.getAddons());
		RecyclerView recyclerViewAddons = layout.findViewById(R.id.info_addon_recycler);
		LinearLayoutManager manager = new LinearLayoutManager(requireContext()) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		recyclerViewAddons.setLayoutManager(manager);
		DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), manager.getOrientation());
		recyclerViewAddons.addItemDecoration(itemDecoration);
		recyclerViewAddons.setAdapter(mAddonAdapter);

		ServerControllerAddonsReceiver addonsReceiver = new ServerControllerAddonsReceiver();
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(currentConnection.toURL() + "addons/", addonsReceiver, ServerControllerAddons.class, currentConnection.getApiKey(), addonsReceiver), requireContext());

		mPermissionsAdapter = new PermissionListAdapter(mInformationViewModel.getPermissions());
		RecyclerView recyclerViewPermissions = layout.findViewById(R.id.info_permissions_recycler);
		manager = new LinearLayoutManager(requireContext()) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		recyclerViewPermissions.setLayoutManager(manager);
		itemDecoration = new DividerItemDecoration(requireContext(), manager.getOrientation());
		recyclerViewPermissions.addItemDecoration(itemDecoration);
		recyclerViewPermissions.setAdapter(mPermissionsAdapter);

		ServerControllerPermissionsReceiver permissionsReceiver = new ServerControllerPermissionsReceiver();
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(currentConnection.toURL() + "user/permissions/", permissionsReceiver, ServerControllerPermissions.class, currentConnection.getApiKey(), permissionsReceiver), requireContext());

		return layout;
	}
}
