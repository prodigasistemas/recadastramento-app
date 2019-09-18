package com.AndroidExplorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

public class CustomDialog {

	public static OnClickListener DEFAULT_LISTENER = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {}
	};
	
	public static AlertDialog criar(Context context, View layout, String titulo, String mensagem, int icone, OnClickListener confirmar, OnClickListener cancelar) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);

		if (titulo != null && !titulo.equals("")) {
			dialog.setTitle(titulo);
		}

		if (mensagem != null && !mensagem.equals("")) {
			dialog.setMessage(mensagem);
		}

		if (icone != -1) {
			dialog.setIcon(icone);
		}

		if (layout != null) {
			dialog.setView(layout);
		}

		if (confirmar != null) {
			dialog.setPositiveButton(android.R.string.ok, confirmar);
		}

		if (cancelar == null) {
			dialog.setCancelable(false);
		} else {
			dialog.setNegativeButton(android.R.string.cancel, cancelar);
		}

		return dialog.create();
	}

	public static AlertDialog criar(Context context, View layout, String titulo, String mensagem, int icone, OnClickListener confirmar) {
		return criar(context, layout, titulo, mensagem, icone, confirmar, DEFAULT_LISTENER);
	}

	public static AlertDialog criar(Context context, String titulo, String mensagem, int icone) {
		return criar(context, null, titulo, mensagem, icone, DEFAULT_LISTENER, null);
	}

	public static AlertDialog criar(Context context, String titulo, String mensagem, int icone, OnClickListener confirmar) {
		return criar(context, null, titulo, mensagem, icone, confirmar, null);
	}

	public static AlertDialog criar(Context context, String titulo, String mensagem, int icone, OnClickListener confirmar, boolean cancelar) {
		return criar(context, null, titulo, mensagem, icone, confirmar, DEFAULT_LISTENER);
	}
}
