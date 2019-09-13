package com.AndroidExplorer;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import model.Usuario;
import util.Constantes;
import util.Criptografia;
import util.Util;
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
import business.Controlador;
import business.ControladorAcessoOnline;
import dataBase.DataManipulator;

public class Fachada extends FragmentActivity {

	private Controlador controlador;
	private DataManipulator manipulator;

	private AlertDialog.Builder builder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		controlador = Controlador.getInstancia();
		controlador.initiateDataManipulator(getBaseContext());
		manipulator = controlador.getCadastroDataManipulator();

		setContentView(R.layout.welcome);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		final Animation animation = configurarAnimacaoBotaoIniciar();
		final Button botaoIniciar = (Button) findViewById(R.id.buttonStart);
		botaoIniciar.startAnimation(animation);

		botaoIniciar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.clearAnimation();

				configurarUrlServidor();

				if (permiteLogin()) {
					configurarDialogLogin(findViewById(R.id.buttonStart));
				} else {
					startActivityForResult(new Intent(getBaseContext(), ListaRotas.class), 1);
				}
			}
		});
	}

	private Animation configurarAnimacaoBotaoIniciar() {
		final Animation animation = new AlphaAnimation(1, (float) 0.3);
		animation.setDuration(1000);
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		return animation;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (permiteLogin()) {
			configurarDialogLogin(findViewById(R.id.buttonStart));
		}
	}

	private boolean permiteLogin() {
		return controlador.databaseExists() && controlador.rotaCarregada();
	}

	@SuppressWarnings("deprecation")
	public void configurarDialogLogin(View view) {
		if (!controlador.isPermissionGranted() && validar()) {
			manipulator.selectGeral();
			showDialog(Constantes.DIALOG_ID_PASSWORD);
		} else {
			Intent intent = new Intent(view.getContext(), MenuPrincipal.class);
			startActivity(intent);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case Constantes.DIALOG_ID_PASSWORD:
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final View layout = inflater.inflate(R.layout.login, (ViewGroup) findViewById(R.id.root));
			final EditText campoLogin = (EditText) layout.findViewById(R.id.EditText_User);
			final EditText campoSenha = (EditText) layout.findViewById(R.id.EditText_Password);

			builder = new AlertDialog.Builder(this);
			builder.setTitle("Autenticação");
			builder.setView(layout);

			cancelarLogin();

			confirmarLogin(layout, campoLogin, campoSenha);

			return builder.create();

		default:
			return null;
		}
	}

	private void confirmarLogin(final View layout, final EditText campoLogin, final EditText campoSenha) {

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String login = campoLogin.getText().toString();
				String senha = campoSenha.getText().toString();

				Usuario usuario = manipulator.selectUsuario(login);

				if (usuario != null) {
					if (Criptografia.encode(senha).equals(usuario.getSenha())) {
						efetuarLogin(layout);
					} else {
						Util.exibirMensagem(Fachada.this, "Alerta", "Senha inválida", R.drawable.aviso, null, null);
					}
				} else {
					Util.exibirMensagem(Fachada.this, "Alerta", "Login inválido", R.drawable.aviso, null, null);
				}

				campoLogin.setFocusable(true);
				campoLogin.getText().clear();
				campoSenha.getText().clear();
			}
		});
	}

	private void cancelarLogin() {
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(DialogInterface dialog, int whichButton) {
				removeDialog(Constantes.DIALOG_ID_PASSWORD);
				controlador.setPermissionGranted(false);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void efetuarLogin(final View layout) {
		controlador.setPermissionGranted(true);
		removeDialog(Constantes.DIALOG_ID_PASSWORD);

		Intent intent = new Intent(layout.getContext(), MenuPrincipal.class);
		startActivity(intent);
	}

	private boolean validar() {
		List<String> informacoes = manipulator.selectInformacoesRota();

		String tipoArquivo = informacoes.get(4).trim();
		if (tipoArquivo.equals("") || tipoArquivo.equals("R") || tipoArquivo.equals("V")) {
			return true;
		} else {
			return false;
		}
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