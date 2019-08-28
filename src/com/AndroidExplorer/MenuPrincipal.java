package com.AndroidExplorer;

import java.util.List;

import model.Imovel;
import util.Constantes;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import background.GerarArquivoRetornoThread;
import business.Controlador;

public class MenuPrincipal extends FragmentActivity {

	private static final int MENU_LISTA_CADASTROS = 0;
	private static final int MENU_INFO = 1;
	private static final int MENU_CONSULTA = 2;
	private static final int MENU_ARQUIVO_RETORNO = 3;
	private static final int MENU_CADASTROS_CONCLUIDOS = 4;
	private static final int MENU_RELATORIO = 5;
	private static final int MENU_NOVO_ROTEIRO = 6;
	private static final int MENU_EXPORTAR_BD = 7;

	private ProgressDialog progressDialog;
	private GerarArquivoRetornoThread arquivoRetornoThread;
	private int quantidade;

	Integer[] imageIDs = { 
			R.drawable.menu_cadastros, 
			R.drawable.menu_info, 
			R.drawable.menu_consulta, 
			R.drawable.menu_arquivo_retorno,
			R.drawable.menu_cadastros_concluidos, 
			R.drawable.menu_relatorio, 
			R.drawable.menu_novo_roteiro,
			R.drawable.menu_exportar_banco };

	Integer[] textIDs = { 
			R.string.menu_cadastros, 
			R.string.menu_info, 
			R.string.menu_consulta, 
			R.string.menu_arquivo_retorno,
			R.string.menu_cadastros_concluidos, 
			R.string.menu_relatorio, 
			R.string.menu_novo_roteiro,
			R.string.menu_exportar_banco };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		instanciate();
	}

	@SuppressWarnings("deprecation")
	public void instanciate() {
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(this));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (position == MENU_LISTA_CADASTROS) {
					Intent myIntent = new Intent(getApplicationContext(), ListaImoveis.class);
					startActivity(myIntent);

				} else if (position == MENU_INFO) {
					Intent myIntent = new Intent(getApplicationContext(), TelaInformacoes.class);
					startActivity(myIntent);

				} else if (position == MENU_CONSULTA) {
					Intent myIntent = new Intent(getApplicationContext(), Consulta.class);
					startActivity(myIntent);

				} else if (position == MENU_ARQUIVO_RETORNO) {
					if (permiteGerarArquivoRetorno()) {
						showDialog(Constantes.DIALOG_ID_GERAR_ARQUIVO_RETORNO);
					} else {
						showNotifyDialog(R.drawable.aviso, "Alerta", "Roteiro sem imóveis finalizados. Não foi possível gerar o arquivo de retorno.", 
								Constantes.DIALOG_ID_ERRO);
					}
					
				} else if (position == MENU_CADASTROS_CONCLUIDOS) {

				} else if (position == MENU_RELATORIO) {
					Intent myIntent = new Intent(getApplicationContext(), TelaRelatorio.class);
					startActivity(myIntent);

				} else if (position == MENU_NOVO_ROTEIRO) {
					showDialog(Constantes.DIALOG_ID_CLEAN_DB);

				} else if (position == MENU_EXPORTAR_BD) {
					configurarExportarBD();
				}
			}
		});
	}
	
	private boolean permiteGerarArquivoRetorno() {
		boolean gerar = false;

		List<Imovel> imoveis = (List<Imovel>) Controlador.getInstancia().getCadastroDataManipulator().selectStatusImoveis(null);

		for (Imovel imovel : imoveis) {
			if (imovel.getImovelStatus() != Constantes.IMOVEL_A_SALVAR) {
				gerar = true;
				quantidade++;
			}
		}

		return gerar;
	}

	@SuppressLint("HandlerLeak")
	final Handler handlerArquivoRetorno = new Handler() {

		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			int processados = msg.getData().getInt("progressoArquivoRetorno");
			progressDialog.setProgress(processados);

			if (processados >= quantidade) {
				dismissDialog(Constantes.DIALOG_ID_GERAR_ARQUIVO_RETORNO);
				showNotifyDialog(R.drawable.save, "", "Arquivo de retorno gerado com sucesso!", Constantes.DIALOG_ID_SUCESSO);
			}
		}
	};

	@Override
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(final int id) {

		if (id == Constantes.DIALOG_ID_CLEAN_DB) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layoutConfirmationDialog = inflater.inflate(R.layout.remove_data, (ViewGroup) findViewById(R.id.root));

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Atenção");
			builder.setView(layoutConfirmationDialog);

			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					removeDialog(id);
				}
			});

			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					EditText senha = (EditText) layoutConfirmationDialog.findViewById(R.id.txtSenha);
					if (senha.getText().toString().equals("apagar")) {

						removeDialog(id);
						Controlador.getInstancia().finalizeDataManipulator();
						Controlador.getInstancia().deleteDatabase();
						Controlador.getInstancia().setPermissionGranted(false);
						Controlador.getInstancia().initiateDataManipulator(layoutConfirmationDialog.getContext());

						Toast.makeText(getBaseContext(), "Todas as informações foram apagadas com sucesso!", Toast.LENGTH_LONG).show();

						Intent myIntent = new Intent(layoutConfirmationDialog.getContext(), Fachada.class);
						startActivity(myIntent);
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipal.this);
						builder.setTitle("Erro");
						builder.setMessage("Senha inválida");

						builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});

						builder.show();

					}
				}
			});

			AlertDialog passwordDialog = builder.create();
			return passwordDialog;

		} else if (id == Constantes.DIALOG_ID_GERAR_ARQUIVO_RETORNO) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

				progressDialog = new ProgressDialog(this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setCancelable(false);
				progressDialog.setMessage("Por favor, aguarde enquanto o Arquivo de Retorno está sendo gerado...");
				progressDialog.setMax(quantidade);
				arquivoRetornoThread = new GerarArquivoRetornoThread(handlerArquivoRetorno, this);
				arquivoRetornoThread.start();
				return progressDialog;

			} else {
				Toast.makeText(getBaseContext(), "Cartão de memória não está disponível!", Toast.LENGTH_SHORT).show();
			}
		}
		
		return null;
	}

	private void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}

	@SuppressLint("InflateParams")
	public class ImageAdapter extends BaseAdapter {

		Context context;

		public ImageAdapter(Context c) {
			this.context = c;
		}

		public int getCount() {
			return imageIDs.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				LayoutInflater inflator = getLayoutInflater();
				view = inflator.inflate(R.layout.icon, null);

			} else {
				view = convertView;
			}

			TextView textView = (TextView) view.findViewById(R.id.icon_text);
			textView.setText(textIDs[position]);
			ImageView imageView = (ImageView) view.findViewById(R.id.icon_image);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(5, 5, 5, 5);
			imageView.setImageResource(imageIDs[position]);

			return view;
		}
	}

	@SuppressLint("InflateParams")
	private void configurarExportarBD() {
		LayoutInflater inflater = getLayoutInflater();

		final View viewExport = inflater.inflate(R.layout.confirmation_database_export, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exportar Banco de Dados");
		builder.setView(viewExport);

		builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				EditText senha = (EditText) viewExport.findViewById(R.id.exportSenha);
				if (senha.getText().toString().equalsIgnoreCase("exportar")) {
					Controlador.getInstancia().exportDB(MenuPrincipal.this);
				} else {
					Toast.makeText(MenuPrincipal.this, "Senha incorreta", Toast.LENGTH_SHORT).show();
				}
			}
		});

		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		builder.show();
	}
}