package se7kn8.servercontroller.app.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se7kn8.servercontroller.api.rest.ServerControllerPermissions;
import se7kn8.servercontroller.app.R;

import java.util.List;

public class PermissionListAdapter extends RecyclerView.Adapter<PermissionListAdapter.StringViewHolder>{

	private List<ServerControllerPermissions.ServerControllerPermission> mPermissions;

	public PermissionListAdapter(List<ServerControllerPermissions.ServerControllerPermission> permissions) {
		this.mPermissions = permissions;
	}

	class StringViewHolder extends RecyclerView.ViewHolder {

		private TextView mString;

		StringViewHolder(View itemView) {
			super(itemView);
			mString = itemView.findViewById(R.id.text_view_string);
		}

		private void onBind(ServerControllerPermissions.ServerControllerPermission permission){
			mString.setText(permission.getName());
		}
	}

	@Override
	public int getItemCount() {
		return mPermissions.size();
	}

	@Override
	@NonNull
	public PermissionListAdapter.StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new StringViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_string, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
		holder.onBind(mPermissions.get(position));
	}
}
