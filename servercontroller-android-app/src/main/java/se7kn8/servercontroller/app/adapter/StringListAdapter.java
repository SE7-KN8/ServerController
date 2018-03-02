package se7kn8.servercontroller.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se7kn8.servercontroller.app.R;

import java.util.List;

public class StringListAdapter extends RecyclerView.Adapter<StringListAdapter.StringViewHolder>{

	private List<String> strings;

	public StringListAdapter(List<String> strings) {
		this.strings = strings;
	}

	class StringViewHolder extends RecyclerView.ViewHolder {

		private TextView mString;

		StringViewHolder(View itemView) {
			super(itemView);
			mString = itemView.findViewById(R.id.text_view_string);
		}

		private void onBind(String string){
			mString.setText(string);
		}
	}

	@Override
	public int getItemCount() {
		return strings.size();
	}

	@Override
	public StringListAdapter.StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new StringViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_string, parent, false));
	}

	@Override
	public void onBindViewHolder(StringViewHolder holder, int position) {
		holder.onBind(strings.get(position));
	}
}
