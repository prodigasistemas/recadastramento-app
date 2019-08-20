package com.AndroidExplorer;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import model.Usuario;
import util.Constantes;
import util.Criptografia;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import business.Controlador;
import business.ControladorAcessoOnline;

public class Fachada extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		final Animation animation = new AlphaAnimation(1, (float) 0.3);
		animation.setDuration(1000);
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);

		final Button startButton = (Button) findViewById(R.id.buttonStart);
		startButton.startAnimation(animation);

		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.clearAnimation();
				
				configurarUrlServidor();

				if (Controlador.getInstancia().databaseExists(getBaseContext()) && Controlador.getInstancia().isDatabaseRotaCarregadaOk() == Constantes.SIM) {

					if (!Controlador.getInstancia().isPermissionGranted()) {
						Controlador.getInstancia().initiateDataManipulator(getBaseContext());
					}
					onPasswordDialogButtonClick(findViewById(R.id.buttonStart));

				} else {
					Intent myIntent = new Intent(getBaseContext(), ListaRotas.class);
					startActivityForResult(myIntent, 1);
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Controlador.getInstancia().databaseExists(getBaseContext()) && Controlador.getInstancia().isDatabaseRotaCarregadaOk() == Constantes.SIM) {

			if (!Controlador.getInstancia().isPermissionGranted()) {
				Controlador.getInstancia().initiateDataManipulator(getBaseContext());
			}
			onPasswordDialogButtonClick(findViewById(R.id.buttonStart));

		}
	}

	@SuppressWarnings("deprecation")
	public void carregaRotaDialogButtonClick(String fileName) {
		showDialog(Constantes.DIALOG_ID_CARREGAR_ROTA);
	}

	@SuppressWarnings("deprecation")
	public void onPasswordDialogButtonClick(View v) {
		if (!Controlador.getInstancia().isPermissionGranted() && validar()) {
			Controlador.getInstancia().getCadastroDataManipulator().selectGeral();

			showDialog(Constantes.DIALOG_ID_PASSWORD);

		} else {
			Intent myIntent = new Intent(v.getContext(), MenuPrincipal.class);
			startActivity(myIntent);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case Constantes.DIALOG_ID_PASSWORD:
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final View layout = inflater.inflate(R.layout.login, (ViewGroup) findViewById(R.id.root));
			final EditText user = (EditText) layout.findViewById(R.id.EditText_User);
			final EditText password = (EditText) layout.findViewById(R.id.EditText_Password);

			if (versoesCompativeis()) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Autenticação");
				builder.setView(layout);

				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int whichButton) {
						removeDialog(Constantes.DIALOG_ID_PASSWORD);
						Controlador.getInstancia().setPermissionGranted(false);
					}
				});

				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String strUsr = user.getText().toString();
						String strPass = password.getText().toString();

						Usuario usuario = Controlador.getInstancia().getCadastroDataManipulator().selectUsuario(strUsr);

						if (usuario != null) {
							if (Criptografia.encode(strPass).equals(usuario.getSenha())) {
								permitirAcesso(layout);
							} else {
								showNotifyDialog(R.drawable.aviso, "Alerta!", "Senha inválida.", Constantes.DIALOG_ID_ERRO);
							}
						} else {
							showNotifyDialog(R.drawable.aviso, "Alerta!", "Login inválido.", Constantes.DIALOG_ID_ERRO);
						}
						
						layout.findViewById(R.id.EditText_User).clearFocus();
						user.getText().clear();
						password.getText().clear();
					}
				});

				AlertDialog passwordDialog = builder.create();
				return passwordDialog;
				
			} else {
				limparDB();
			}
		}
		return null;
	}

	private boolean versoesCompativeis() {
		String versaoAplicativo = getString(R.string.app_versao);
		String versaoArquivo = Controlador.getInstancia().getDadosGerais().getVersaoArquivo();
		
		return versaoAplicativo.equals(versaoArquivo);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}

	@SuppressWarnings("deprecation")
	private void permitirAcesso(final View layout) {
		Controlador.getInstancia().setPermissionGranted(true);
		removeDialog(Constantes.DIALOG_ID_PASSWORD);

		Intent myIntent = new Intent(layout.getContext(), MenuPrincipal.class);
		startActivity(myIntent);
	}

	private boolean validar() {
		List<String> informacoes = Controlador.getInstancia().getCadastroDataManipulator().selectInformacoesRota();

		String tipoArquivo = informacoes.get(4).trim();
		if (tipoArquivo.equals("") || tipoArquivo.equals("R") || tipoArquivo.equals("V")) {
			return true;
		} else {
			return false;
		}
	}
	
	private void limparDB() {
		Controlador.getInstancia().finalizeDataManipulator();
		Controlador.getInstancia().deleteDatabase();

		Toast.makeText(getBaseContext(), "Versão do aplicativo e arquivo incompatíveis. Por favor, atualize sua versão.", Toast.LENGTH_LONG).show();

		Intent myIntent = new Intent(getBaseContext(), Fachada.class);
		startActivity(myIntent);
	}

	private void configurarUrlServidor() {
		Properties prop = new Properties();
		try {
			InputStream is = Fachada.this.getAssets().open("app.properties");
			prop.load(is);
			ControladorAcessoOnline.getInstancia().setURL(prop.getProperty("url"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}