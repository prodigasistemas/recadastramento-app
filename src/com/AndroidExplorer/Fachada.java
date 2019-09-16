package com.AndroidExplorer;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import model.Usuario;
import util.Criptografia;
import util.Util;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
					configurarLogin(findViewById(R.id.buttonStart));
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
			configurarLogin(findViewById(R.id.buttonStart));
		}
	}

	private boolean permiteLogin() {
		return controlador.databaseExists() && controlador.rotaCarregada();
	}

	public void configurarLogin(View view) {
		if (!controlador.isPermissionGranted() && validar()) {
			manipulator.selectGeral();
			
			final View layout = getLayoutInflater().inflate(R.layout.login, (ViewGroup) findViewById(R.id.root));
			final EditText campoLogin = (EditText) layout.findViewById(R.id.EditText_User);
			final EditText campoSenha = (EditText) layout.findViewById(R.id.EditText_Password);

			DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					String login = campoLogin.getText().toString();
					String senha = campoSenha.getText().toString();

					Usuario usuario = manipulator.selectUsuario(login);

					if (usuario != null) {
						if (Criptografia.encode(senha).equals(usuario.getSenha())) {
							efetuarLogin();
						} else {
							Util.criarDialog(Fachada.this, null, "Alerta", "Senha inválida", R.drawable.aviso, null, null).show();
						}
					} else {
						Util.criarDialog(Fachada.this, null, "Alerta", "Login inválido", R.drawable.aviso, null, null).show();
					}

					campoLogin.setFocusable(true);
					campoLogin.getText().clear();
					campoSenha.getText().clear();
				}
			};
			
			DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			};
			
			Util.criarDialog(layout.getContext(), layout, "Autenticação", null, -1, positiveListener, negativeListener).show();
			
		} else {
			startActivity(new Intent(view.getContext(), MenuPrincipal.class));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void efetuarLogin() {
		controlador.setPermissionGranted(true);
		startActivity(new Intent(Fachada.this, MenuPrincipal.class));
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