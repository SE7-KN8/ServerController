package se7kn8.servercontroller.app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import se7kn8.servercontroller.app.R;
import se7kn8.servercontroller.app.util.ServerControllerConnection;

public class AddServerControllerDialog extends DialogFragment {

	private EditText mEditTextName;
	private EditText mEditTextIP;
	private EditText mEditTextPort;
	private EditText mEditTextApiKey;

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
		View dialogView = View.inflate(getActivity(), R.layout.dialog_add_servercontroller, null);
		mEditTextName = dialogView.findViewById(R.id.edit_text_name);
		mEditTextIP = dialogView.findViewById(R.id.edit_text_ip);
		mEditTextPort = dialogView.findViewById(R.id.edit_text_port);
		mEditTextApiKey = dialogView.findViewById(R.id.edit_text_api_key);

		builder.setView(dialogView)
				.setPositiveButton(R.string.add, null)
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
	public void onResume() {
		super.onResume();

		final AlertDialog dialog = (AlertDialog) getDialog();
		if (dialog != null) {
			Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean successful = true;

					String name = mEditTextName.getText().toString();

					if (name.trim().equals("")) {
						mEditTextName.requestFocus();
						mEditTextName.setError(getString(R.string.invalid_name));
						successful = false;
					}

					String ip = mEditTextIP.getText().toString();

					if (ip.trim().equals("")) {
						mEditTextIP.requestFocus();
						mEditTextIP.setError(getString(R.string.invalid_ip));
						successful = false;
					}

					String apiKey = mEditTextApiKey.getText().toString();
					int port = 0;

					try {
						port = Integer.valueOf(mEditTextPort.getText().toString());
					} catch (NumberFormatException e) {
						mEditTextPort.requestFocus();
						mEditTextPort.setError(getString(R.string.invalid_port));
						successful = false;
					}

					if (successful) {
						mListener.addServerController(new ServerControllerConnection(name, ip, port, apiKey));
						dialog.cancel();
					}

				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
