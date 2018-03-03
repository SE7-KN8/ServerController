package se7kn8.servercontroller.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import se7kn8.servercontroller.api.rest.ServerControllerMessage;
import se7kn8.servercontroller.api.rest.ServerControllerServers;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.util.GsonPostRequest;
import se7kn8.servercontroller.app.util.ServerControllerConnection;
import se7kn8.servercontroller.app.util.VolleyRequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerControllerServerViewHolder> {

	private List<ServerControllerServers.ServerControllerServer> mServers;
	private ServerControllerConnection mConnection;

	public ServerListAdapter(List<ServerControllerServers.ServerControllerServer> servers, ServerControllerConnection connection) {
		this.mServers = servers;
		this.mConnection = connection;
	}

	class ServerControllerServerViewHolder extends RecyclerView.ViewHolder {
		private TextView mName;
		private TextView mCreatorInfo;
		private TextView mServerInfo;

		private Button mStart;
		private Button mStop;
		private Button mRestart;

		ServerControllerServerViewHolder(View itemView) {
			super(itemView);

			mName = itemView.findViewById(R.id.text_view_name);
			mCreatorInfo = itemView.findViewById(R.id.text_view_creator_info);
			mServerInfo = itemView.findViewById(R.id.text_view_server_info);

			mStart = itemView.findViewById(R.id.button_start);
			mStop = itemView.findViewById(R.id.button_stop);
			mRestart = itemView.findViewById(R.id.button_restart);
		}

		private void bind(final ServerControllerServers.ServerControllerServer server) {
			mName.setText(server.getName());
			mCreatorInfo.setText(server.getServerCreatorInfo());

			StringBuilder sb = new StringBuilder();
			for (String info : server.getServerInformation()) {
				sb.append(info);
				sb.append("\n");
			}

			mServerInfo.setText(sb.toString());

			final Response.Listener<ServerControllerMessage> listener = new Response.Listener<ServerControllerMessage>() {
				@Override
				public void onResponse(ServerControllerMessage response) {
					System.out.println(response.getMessage());
				}
			};

			final Response.ErrorListener errorListener = new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					error.printStackTrace();
				}
			};

			mStart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = mConnection.toURL() + "server/"+ server.getName() + "/start/";
					VolleyRequestQueue.getInstance().addToRequestQueue(new GsonPostRequest<>(url, mConnection.getApiKey(), ServerControllerMessage.class, "", listener, errorListener), mStart.getContext());
				}
			});

			mStop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = mConnection.toURL() + "server/"+ server.getName() + "/stop/";
					VolleyRequestQueue.getInstance().addToRequestQueue(new GsonPostRequest<>(url, mConnection.getApiKey(), ServerControllerMessage.class, "", listener, errorListener), mStop.getContext());
				}
			});

			mRestart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = mConnection.toURL() + "server/"+ server.getName() + "/restart/";
					VolleyRequestQueue.getInstance().addToRequestQueue(new GsonPostRequest<>(url, mConnection.getApiKey(), ServerControllerMessage.class, "", listener, errorListener), mRestart.getContext());
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mServers.size();
	}

	@Override
	public ServerControllerServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ServerControllerServerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_servercontroller_server, parent, false));
	}

	@Override
	public void onBindViewHolder(ServerControllerServerViewHolder holder, int position) {
		holder.bind(mServers.get(position));
	}
}
