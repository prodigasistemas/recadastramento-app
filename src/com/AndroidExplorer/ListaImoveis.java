package com.AndroidExplorer;

import java.util.ArrayList;
import java.util.List;

import model.Imovel;
import util.Constantes;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import business.Controlador;
import dataBase.DataManipulator;

public class ListaImoveis extends ListActivity {

	private Controlador controlador;
	private DataManipulator manipulator;

	private ListaImoveisAdapter adapter;
	private List<Imovel> imoveis;
	private AlertDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.controlador = Controlador.getInstancia();
		this.manipulator = controlador.getCadastroDataManipulator();

		setContentView(R.layout.lista_imoveis);

		ListView view = getListView();
		int[] colors = { 0x12121212, 0xFFFFFFFF, 0x12121212 };
		view.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		view.setDividerHeight(1);
		view.setCacheColorHint(Color.TRANSPARENT);
		view.setClickable(true);
		view.setOnItemClickListener(click);
		view.setLongClickable(true);
		view.setOnItemLongClickListener(longClick);
	}
	
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);

		carregarEnderecos();
	}

	@Override
	protected void onResume() {
		super.onResume();

		carregarEnderecos();
	}

	public void chamarMainTab(View view) {
		controlador.setCadastroSelecionadoByListPosition(adapter.getPosicaoSelecionada());
		controlador.setMenuSelecionado(view.getId());
		dialog.dismiss();
		startActivityForResult(new Intent(getApplicationContext(), MainTab.class), 0);
	}
	
	private void carregarEnderecos() {

		if (manipulator != null) {

			imoveis = (List<Imovel>) manipulator.selectStatusImoveis(null);
			List<String> enderecos = (ArrayList<String>) manipulator.selectEnderecoImoveis(null);

			if (enderecos != null && enderecos.size() > 0) {
				adapter = new ListaImoveisAdapter(this, enderecos);
				setListAdapter(adapter);

				if (controlador.getPosicaoListaImoveis() > -1) {
					this.setSelection(controlador.getPosicaoListaImoveis());
					adapter.setPosicaoSelecionada(controlador.getPosicaoListaImoveis());
				}
			}
		}
	}

	private OnItemClickListener click = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			if (permiteCadastro(position)) {
				adapter.setPosicaoSelecionada(position);
				controlador.setCadastroSelecionadoByListPosition(position);
				startActivityForResult(new Intent(getApplicationContext(), MainTab.class), 0);
			}
		}
	};
	
	private OnItemLongClickListener longClick = new OnItemLongClickListener() {
		
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

			if (permiteCadastro(position)) {
				adapter.setPosicaoSelecionada(position);
				final View layout = getLayoutInflater().inflate(R.layout.menu_lista_imoveis, (ViewGroup) findViewById(R.id.layout_menu_lista_imoveis));
				dialog = Util.criarDialog(layout.getContext(), layout, "Selecione uma Ação", null, -1, null, cancelar);
				dialog.show();
			}
			
			return true;
		}
	};
	
	private boolean permiteCadastro(int posicao) {
		if (imoveis.get(posicao).isInformativo()) {
			Toast.makeText(ListaImoveis.this, "Não é possível selecionar imóvel Informativo", Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}
	}

	private OnClickListener cancelar = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {}
	};
	
	private class ListaImoveisAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final List<String> enderecos;

		private int posicaoSelecionada = -1;

		public ListaImoveisAdapter(Activity context, List<String> enderecos) {
			super(context, R.layout.rowimovel, enderecos);
			this.context = context;
			this.enderecos = enderecos;
		}

		public int getPosicaoSelecionada() {
			return posicaoSelecionada;
		}

		public void setPosicaoSelecionada(int posicao) {
			posicaoSelecionada = posicao;
			notifyDataSetChanged();
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = context.getLayoutInflater().inflate(R.layout.rowimovel, null, true);

			configurarSelecionado(position, rowView);
			configurarStatus(position, rowView);

			return rowView;
		}

		private void configurarSelecionado(int position, View rowView) {
			if (posicaoSelecionada == position) {
				rowView.setBackgroundColor(Color.argb(70, 255, 255, 255));
			} else {
				rowView.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		private void configurarStatus(int position, View rowView) {
			((TextView) rowView.findViewById(R.id.nomerota)).setText(enderecos.get(position));

			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

			Imovel imovel = imoveis.get(position);
			int status = imovel.getImovelStatus();

			switch (status) {
			case Constantes.IMOVEL_A_SALVAR:
				imageView.setImageResource(R.drawable.a_salvar);
				break;

			case Constantes.IMOVEL_SALVO:

				if (imovel.isEnviado()) {
					imageView.setImageResource(R.drawable.salvo_enviado);
				} else if (imovel.isExcluido()) {
					imageView.setImageResource(R.drawable.excluido);
				} else {
					imageView.setImageResource(R.drawable.salvo);
				}

				break;

			case Constantes.IMOVEL_SALVO_COM_ANORMALIDADE:

				if (imovel.isEnviado()) {
					imageView.setImageResource(R.drawable.salvo_anormalidade_enviado);
				} else {
					imageView.setImageResource(R.drawable.salvo_anormalidade);
				}

				break;

			case Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA:
				imageView.setImageResource(R.drawable.salvo_inconsistencia);
				break;

			case Constantes.IMOVEL_NOVO:
				imageView.setImageResource(R.drawable.novo);
				break;

			case Constantes.IMOVEL_NOVO_COM_ANORMALIDADE:
				imageView.setImageResource(R.drawable.novo_anormalidade);
				break;

			case Constantes.IMOVEL_INFORMATIVO:
				imageView.setImageResource(R.drawable.informativo);
				break;

			default:
				break;
			}
		}
	}
}