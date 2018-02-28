package se7kn8.servercontroller.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se7kn8.servercontroller.api.rest.ServerControllerServers;
import se7kn8.servercontroller.app.R;

import java.util.List;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerControllerServerViewHolder> {

	private List<ServerControllerServers.ServerControllerServer> mServers;

	public ServerListAdapter(List<ServerControllerServers.ServerControllerServer> servers) {
		this.mServers = servers;
	}

	class ServerControllerServerViewHolder extends RecyclerView.ViewHolder {
		private TextView mName;
		private TextView mCreatorInfo;
		private TextView mServerInfo;

		ServerControllerServerViewHolder(View itemView) {
			super(itemView);

			mName = itemView.findViewById(R.id.text_view_name);
			mCreatorInfo = itemView.findViewById(R.id.text_view_creator_info);
			mServerInfo = itemView.findViewById(R.id.text_view_server_info);
		}

		private void bind(ServerControllerServers.ServerControllerServer server) {
			mName.setText(server.getName());
			mCreatorInfo.setText(server.getServerCreatorInfo());

			StringBuilder sb = new StringBuilder();
			for (String info : server.getServerInformation()) {
				sb.append(info);
				sb.append("\n");
			}

			mServerInfo.setText(sb.toString());
		}
	}

	@Override
	public int getItemCount() {
		return mServers.size();
	}

	@Override
	public ServerControllerServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ServerControllerServerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_server, parent, false));
	}

	@Override
	public void onBindViewHolder(ServerControllerServerViewHolder holder, int position) {
		holder.bind(mServers.get(position));
	}
}
