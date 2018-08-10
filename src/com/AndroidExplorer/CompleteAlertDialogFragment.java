package com.AndroidExplorer;

import util.Constantes;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CompleteAlertDialogFragment extends DialogFragment {
	
	public static CompleteAlertDialogFragment newInstance(int iconId, String title, String message, int messageType) {
		CompleteAlertDialogFragment frag = new CompleteAlertDialogFragment();
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
		.setIcon(iconId)
		.setMessage(message)
		.setTitle(title)
		.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						switch (messageType){
						case Constantes.DIALOG_ID_CONFIRMA_VOLTAR:
			        		MainTab.indiceNovoImovel = null;
			        		getActivity().finish();
							break;
						case Constantes.DIALOG_ID_CONFIRMA_MUDANCA:
							if (getTargetRequestCode() == Constantes.FRAGMENT_ID_SERVICOS){
								ServicosTab.setChangesConfirmed();
							}else if(getTargetRequestCode() == Constantes.FRAGMENT_ID_MEDIDOR){
								MedidorTab.setChangesConfirmed();
							}else if(getTargetRequestCode() == Constantes.FRAGMENT_ID_IMOVEL){
								ImovelTab.setChangesConfirmed();
							}
							break;
						case Constantes.DIALOG_ID_CONFIRMA_EXCLUSAO:
							((MainTab)getActivity()).ImovelExcluidoDialog();
							break;
						}
					}
				})
		.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((MainTab)getActivity()).doNegativeClick();
					}
		}).create();
	}
}