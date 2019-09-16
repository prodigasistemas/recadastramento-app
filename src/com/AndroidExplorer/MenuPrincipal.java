package com.AndroidExplorer;

import util.Util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
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
import background.ArquivoRetornoTask;
import background.TransmitirFinalizadosTask;
import business.Controlador;

public class MenuPrincipal extends FragmentActivity {

	private static final int MENU_LISTA_CADASTROS = 0;
	private static final int MENU_INFO = 1;
	private static final int MENU_CONSULTA = 2;
	private static final int MENU_ARQUIVO_RETORNO = 3;
	private static final int MENU_TRANSMITIR_FINALIZADOS = 4;
	private static final int MENU_RELATORIO = 5;
	private static final int MENU_LIMPAR_TUDO = 6;
	private static final int MENU_EXPORTAR_BD = 7;

	private Controlador controlador;
	
	Integer[] imageIDs = { 
			R.drawable.menu_cadastros, 
			R.drawable.menu_info, 
			R.drawable.menu_consulta, 
			R.drawable.menu_arquivo_retorno,
			R.drawable.menu_transmitir_finalizados, 
			R.drawable.menu_relatorio, 
			R.drawable.menu_novo_roteiro,
			R.drawable.menu_exportar_banco };

	Integer[] textIDs = { 
			R.string.menu_cadastros, 
			R.string.menu_info, 
			R.string.menu_consulta, 
			R.string.menu_arquivo_retorno,
			R.string.menu_transmitir_finalizados, 
			R.string.menu_relatorio, 
			R.string.menu_novo_roteiro,
			R.string.menu_exportar_banco };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.controlador = Controlador.getInstancia();
		
		setContentView(R.layout.mainmenu);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		configurar();
	}

	public void configurar() {
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(this));

		gridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				switch (position) {
				case MENU_LISTA_CADASTROS:
					
					startActivity(new Intent(getApplicationContext(), ListaImoveis.class));
					
					break;

				case MENU_INFO:
					
					startActivity(new Intent(getApplicationContext(), TelaInformacoes.class));
					
					break;

				case MENU_CONSULTA:
					
					startActivity(new Intent(getApplicationContext(), Consulta.class));
					
					break;

				case MENU_ARQUIVO_RETORNO:
					
					new ArquivoRetornoTask(MenuPrincipal.this).execute();
					
					break;

				case MENU_TRANSMITIR_FINALIZADOS:
					
					new TransmitirFinalizadosTask(MenuPrincipal.this).execute();
					
					break;

				case MENU_RELATORIO:
					
					startActivity(new Intent(getApplicationContext(), TelaRelatorio.class));
					
					break;

				case MENU_LIMPAR_TUDO:
					
					limparTudo();
					
					break;

				case MENU_EXPORTAR_BD:
					
					exportarBanco();
					
					break;

				default:
					break;
				}
			}
		});
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

	private void limparTudo() {

		final View view = getLayoutInflater().inflate(R.layout.confirmation_dialog_limpar_tudo, (ViewGroup) findViewById(R.id.root));

		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				String senha = ((EditText) view.findViewById(R.id.txtSenha)).getText().toString();

				if (senha.equals("apagar")) {
					controlador.deleteDatabase();
					Toast.makeText(getBaseContext(), "Todas as informações foram apagadas com sucesso", Toast.LENGTH_LONG).show();
					startActivity(new Intent(view.getContext(), Fachada.class));
				} else {
					Util.exibirDialog(MenuPrincipal.this, null, "Alerta", "Senha inválida", R.drawable.aviso, null, null);
				}
			}
		};

		Util.exibirDialog(MenuPrincipal.this, view, "Limpar Tudo", null, R.drawable.aviso, listener, null);
	}
	
	private void exportarBanco() {

		final View view = getLayoutInflater().inflate(R.layout.confirmation_dialog_exportar_banco, (ViewGroup) findViewById(R.id.root));

		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {

				String senha = ((EditText) view.findViewById(R.id.exportSenha)).getText().toString();

				if (senha.equals("exportar")) {
					controlador.exportarBanco(MenuPrincipal.this);
				} else {
					Util.exibirDialog(MenuPrincipal.this, null, "Alerta", "Senha inválida", R.drawable.aviso, null, null);
				}
			}
		};

		Util.exibirDialog(MenuPrincipal.this, view, "Exportando Banco de Dados", null, R.drawable.aviso, listener, null);
	}
}