package se7kn8.servercontroller.app.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import se7kn8.servercontroller.api.rest.ServerControllerServerLog;
import se7kn8.servercontroller.api.rest.ServerControllerServers;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.util.GsonRequest;
import se7kn8.servercontroller.app.util.ServerControllerConnection;
import se7kn8.servercontroller.app.util.VolleyRequestQueue;
import se7kn8.servercontroller.app.viewmodel.ConnectionViewModel;
import se7kn8.servercontroller.app.viewmodel.ServerViewModel;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

public class ServerConsoleFragment extends Fragment implements AdapterView.OnItemSelectedListener, Response.ErrorListener, Response.Listener<ServerControllerServerLog> {

	private ArrayAdapter<String> mServerAdapter;
	private TextView mConsoleLog;
	private ScrollView mConsoleScrollView;

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
	}

	@Override
	public void onResponse(ServerControllerServerLog response) {
		List<String> lines = response.getLines();
		mConsoleLog.setText("");
		for (int i = (lines.size() - 1); i >= 0; i--) {
			mConsoleLog.append(lines.get(i));
			mConsoleLog.append("\n");
		}

		mConsoleScrollView.post(new Runnable() {
			@Override
			public void run() {
				mConsoleScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	private ServerControllerConnection mConnection;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragement_server_console, container, false);

		mConsoleLog = layout.findViewById(R.id.text_view_console);
		mConsoleScrollView = layout.findViewById(R.id.scroll_view_console);

		mConnection = ViewModelProviders.of(requireActivity()).get(ConnectionViewModel.class).getCurrentConnection();

		List<String> serversList = new ArrayList<>();
		for (ServerControllerServers.ServerControllerServer server : ViewModelProviders.of(requireActivity()).get(ServerViewModel.class).getServers()) {
			serversList.add(server.getName());
		}
		mServerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, serversList);
		mServerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = layout.findViewById(R.id.spinner_servers);
		spinner.setAdapter(mServerAdapter);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(this);

		return layout;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		VolleyRequestQueue.getInstance().addToRequestQueue(new GsonRequest<>(mConnection.toURL() + "server/" + mServerAdapter.getItem(position) + "/log/", this, ServerControllerServerLog.class, mConnection.getApiKey(), this), requireContext());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
