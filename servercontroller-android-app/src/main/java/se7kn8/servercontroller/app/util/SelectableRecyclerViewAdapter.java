package se7kn8.servercontroller.app.util;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

public abstract class SelectableRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

	private SparseBooleanArray mSelectedItems;

	public SelectableRecyclerViewAdapter(SparseBooleanArray array) {
		this.mSelectedItems = array;
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItems.get(position));
	}

	public void selectView(int position, boolean value) {
		if (value) {
			mSelectedItems.put(position, value);
		} else {
			mSelectedItems.delete(position);
		}
		notifyDataSetChanged();
	}

	public void removeSelections() {
		mSelectedItems = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public int getSelectedItemCount() {
		return mSelectedItems.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItems;
	}

}
