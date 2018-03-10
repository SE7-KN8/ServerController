package se7kn8.servercontroller.app.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.ServerControllerActivity;
import se7kn8.servercontroller.app.adapter.ConnectionListAdapter;
import se7kn8.servercontroller.app.dialog.AddServerControllerDialog;
import se7kn8.servercontroller.app.util.ActionModeFragment;
import se7kn8.servercontroller.app.util.RecyclerViewTouchListener;
import se7kn8.servercontroller.app.util.ServerControllerConnection;
import se7kn8.servercontroller.app.util.ToolbarActionModeCallback;
import se7kn8.servercontroller.app.viewmodel.ConnectionViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by sebkn on 11.02.2018.
 */

public class ConnectionListFragment extends Fragment implements AddServerControllerDialog.AddServerControllerDialogListener, ActionModeFragment {

	private ConnectionListAdapter mAdapter;
	private ActionMode mActionMode;
	private ConnectionViewModel mConnectionViewModel;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mConnectionViewModel = ViewModelProviders.of(requireActivity()).get(ConnectionViewModel.class);

		if (savedInstanceState == null) {
			mConnectionViewModel.getConnections().addAll(loadList());
		}
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		CoordinatorLayout layout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_connection_list, container, false);
		RecyclerView view = layout.findViewById(R.id.connection_overview_recycler);
		mAdapter = new ConnectionListAdapter(mConnectionViewModel.getConnections());
		view.setAdapter(mAdapter);
		view.setLayoutManager(new LinearLayoutManager(requireActivity()));
		view.addOnItemTouchListener(new RecyclerViewTouchListener(requireActivity(), view, new RecyclerViewTouchListener.RecyclerViewClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				if (mActionMode != null) {
					onItemSelected(position);
				} else {
					mConnectionViewModel.setCurrentConnection(mConnectionViewModel.getConnections().get(position));
					requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new ServerControllerOverviewFragment()).addToBackStack(ServerControllerActivity.FRAGMENT_TAG_SERVERCONTROLLER_OVERVIEW).commit();
				}
			}

			@Override
			public void onItemLongClick(View view, int position) {
				onItemSelected(position);
			}
		}));

		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.toolbar_options, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_add_servercontroller:
				showAddServerControllerDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void addServerController(ServerControllerConnection connection) {
		mConnectionViewModel.addConnection(connection);
		saveList();
		mAdapter.notifyDataSetChanged();
	}

	private void onItemSelected(int position) {
		mAdapter.toggleSelection(position);

		boolean hasCheckedItems = mAdapter.getSelectedItemCount() > 0;
		if (hasCheckedItems && mActionMode == null) {
			mActionMode = ((AppCompatActivity) requireActivity()).startSupportActionMode(new ToolbarActionModeCallback(this, mAdapter));
		} else if (!hasCheckedItems && mActionMode != null) {
			mActionMode.finish();
		}
		if (mActionMode != null) {
			mActionMode.setTitle(getString(R.string.selected, mAdapter.getSelectedItemCount()));
		}
	}

	@Override
	public boolean handleAction(int id) {
		switch (id) {
			case R.id.item_delete:
				SparseBooleanArray array = mAdapter.getSelectedIds();
				for (int i = (array.size()); i >= 0; i--) {
					if (array.valueAt(i)) {
						mConnectionViewModel.removeConnection(i);
						mAdapter.notifyDataSetChanged();
					}
				}
				saveList();
				mActionMode.finish();
				return true;
			default:
				return false;
		}
	}

	@Override
	public void setNullToActionMode() {
		if (mActionMode != null) {
			mActionMode = null;
		}
	}

	@Override
	public int getMenuID() {
		return R.menu.action_mode_delete;
	}

	private void showAddServerControllerDialog() {
		AddServerControllerDialog fragment = new AddServerControllerDialog();
		fragment.setListener(this);
		fragment.show(getChildFragmentManager(), "addServerController");
	}

	private static final String FILE_NAME = "connections.obj";

	private void saveList() {
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(requireContext().getFilesDir(), FILE_NAME)));
			objectOutputStream.writeObject(mConnectionViewModel.getConnections());
			objectOutputStream.close();
		} catch (Exception e) {
			throw new RuntimeException("Can't save mConnections list", e);
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<ServerControllerConnection> loadList() {
		try {
			File inputFile = new File(requireContext().getFilesDir(), FILE_NAME);
			if (inputFile.exists()) {
				ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(inputFile));
				Object list = objectInputStream.readObject();
				objectInputStream.close();

				return (ArrayList) list;
			}
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}