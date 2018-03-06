package se7kn8.servercontroller.app.adapter;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.util.SelectableRecyclerViewAdapter;
import se7kn8.servercontroller.app.util.ServerControllerConnection;
import se7kn8.servercontroller.app.util.Util;

import java.util.List;

public class ConnectionListAdapter extends SelectableRecyclerViewAdapter<ConnectionListAdapter.ServerControllerConnectionViewHolder> {

	class ServerControllerConnectionViewHolder extends RecyclerView.ViewHolder {
		private TextView mName;
		private TextView mUrl;

		ServerControllerConnectionViewHolder(View itemView) {
			super(itemView);
			mName = itemView.findViewById(R.id.text_view_title);
			mUrl = itemView.findViewById(R.id.text_view_url);
		}

		private void bind(ServerControllerConnection connection) {
			mName.setText(connection.getName());
			mUrl.setText(mUrl.getContext().getString(R.string.ip_and_port, connection.getIp(), connection.getPort()));
		}

	}

	private List<ServerControllerConnection> mConnections;

	public ConnectionListAdapter(List<ServerControllerConnection> connections) {
		super(new SparseBooleanArray());
		this.mConnections = connections;
	}

	@Override
	public int getItemCount() {
		return mConnections.size();
	}

	@Override
	@NonNull
	public ServerControllerConnectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ServerControllerConnectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_servercontroller_connection, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ServerControllerConnectionViewHolder holder, int position) {
		holder.bind(mConnections.get(position));
		CardView view = ((CardView) holder.itemView);
		float startValue = view.getCardElevation();
		float endValue = getSelectedIds().get(position) ?
				Util.convertDpToPixel(8.0f, holder.mName.getContext()) :
				Util.convertDpToPixel(2.0f, holder.mName.getContext());

		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "cardElevation", startValue, endValue);
		animator.start();
	}

}
