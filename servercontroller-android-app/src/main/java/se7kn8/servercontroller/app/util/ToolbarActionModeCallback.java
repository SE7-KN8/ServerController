package se7kn8.servercontroller.app.util;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

public class ToolbarActionModeCallback implements ActionMode.Callback {

	private ActionModeFragment mFragment;
	private SelectableRecyclerViewAdapter<?> mRecyclerViewAdapter;

	public ToolbarActionModeCallback(ActionModeFragment fragment, SelectableRecyclerViewAdapter<?> recyclerViewAdapter) {
		this.mFragment = fragment;
		this.mRecyclerViewAdapter = recyclerViewAdapter;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(mFragment.getMenuID(), menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		return mFragment.handleAction(item.getItemId());
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mRecyclerViewAdapter.removeSelections();
		mFragment.setNullToActionMode();
	}
}
