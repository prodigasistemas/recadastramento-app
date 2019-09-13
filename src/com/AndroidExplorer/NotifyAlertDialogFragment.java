package com.AndroidExplorer;

import util.Constantes;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NotifyAlertDialogFragment extends DialogFragment {

	public static NotifyAlertDialogFragment newInstance(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment frag = new NotifyAlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("iconId", iconId);
		args.putString("title", title);
		args.putString("message", message);
		args.putInt("messageType", messageType);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String title = getArguments().getString("title");
		final String message = getArguments().getString("message");
		final int messageType = getArguments().getInt("messageType");
		final int iconId = getArguments().getInt("iconId");

		return new AlertDialog.Builder(getActivity())
				.setIcon(iconId).setMessage(message)
				.setTitle(title)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int whichButton) {

						switch (messageType) {
						
						case Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO:
							((MainTab) getActivity()).doGpsDesligado();
							break;

						case Constantes.DIALOG_ID_CONFIRMA_IMOVEL_SALVO:
							((MainTab) getActivity()).chamaProximoImovel();
							break;

						default:
							break;
						}
					}
				}).create();
	}
}