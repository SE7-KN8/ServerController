package se7kn8.servercontroller.app.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se7kn8.servercontroller.api.rest.ServerControllerAddons;
import se7kn8.servercontroller.app.R;

import java.util.List;

public class AddonListAdapter extends RecyclerView.Adapter<AddonListAdapter.ServerControllerAddonViewHolder> {

	private List<ServerControllerAddons.ServerControllerAddonInfo> mAddons;

	class ServerControllerAddonViewHolder extends RecyclerView.ViewHolder {

		private TextView mName;
		private TextView mVersion;
		private TextView mAuthors;

		ServerControllerAddonViewHolder(View itemView) {
			super(itemView);

			mName = itemView.findViewById(R.id.text_view_name);
			mVersion = itemView.findViewById(R.id.text_view_version);
			mAuthors = itemView.findViewById(R.id.text_view_authors);
		}

		private void onBind(ServerControllerAddons.ServerControllerAddonInfo info) {
			mName.setText(mName.getContext().getString(R.string.addon_name, info.getName(), info.getId()));
			mVersion.setText(mVersion.getContext().getString(R.string.servercontroller_version, info.getVersion()));

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < info.getAuthors().size() - 1; i++) {
				sb.append(info.getAuthors().get(i));
				sb.append(", ");
			}
			sb.append(info.getAuthors().get(info.getAuthors().size() - 1));

			mAuthors.setText(mAuthors.getContext().getString(R.string.authors, sb.toString()));

		}
	}

	public AddonListAdapter(List<ServerControllerAddons.ServerControllerAddonInfo> addons) {
		this.mAddons = addons;
	}

	@Override
	public int getItemCount() {
		return mAddons.size();
	}

	@Override
	@NonNull
	public ServerControllerAddonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ServerControllerAddonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_servercontroller_addon, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ServerControllerAddonViewHolder holder, int position) {
		holder.onBind(mAddons.get(position));
	}
}
