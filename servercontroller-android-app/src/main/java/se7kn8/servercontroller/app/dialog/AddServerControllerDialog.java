package se7kn8.servercontroller.app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.util.ServerControllerConnection;

public class AddServerControllerDialog extends DialogFragment {

	public interface AddServerControllerDialogListener {
		void addServerController(ServerControllerConnection connection);
	}

	private AddServerControllerDialogListener mListener;

	public void setListener(AddServerControllerDialogListener listener) {
		this.mListener = listener;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_add_servercontroller, null);
		final EditText name = dialogView.findViewById(R.id.edit_text_name);
		final EditText ip = dialogView.findViewById(R.id.edit_text_ip);
		final EditText port = dialogView.findViewById(R.id.edit_text_port);
		final EditText apiKey = dialogView.findViewById(R.id.edit_text_api_key);

		builder.setView(dialogView)
				.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							mListener.addServerController(new ServerControllerConnection(name.getText().toString(), ip.getText().toString(), Integer.valueOf(port.getText().toString()), apiKey.getText().toString()));
						} catch (NumberFormatException e) {
							//Empty number field
						}
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AddServerControllerDialog.this.getDialog().cancel();
					}
				})
				.setTitle(R.string.add_connection);

		return builder.create();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
