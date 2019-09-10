package com.AndroidExplorer;

import java.util.ArrayList;
import java.util.List;

import model.Imovel;
import util.Constantes;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import business.Controlador;

public class ListaImoveis extends ListActivity {

	private ListaImoveisAdapter adapter;
	private List<Imovel> imoveis;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imoveislist);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);

		int[] colors = { 0x12121212, 0xFFFFFFFF, 0x12121212 };
		this.getListView().setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		this.getListView().setDividerHeight(1);
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		carregarEnderecos();
	}

	private void carregarEnderecos() {

		if (Controlador.getInstancia().getCadastroDataManipulator() != null) {

			imoveis = (List<Imovel>) Controlador.getInstancia().getCadastroDataManipulator().selectStatusImoveis(null);
			List<String> enderecos = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectEnderecoImoveis(null);

			if (enderecos != null && enderecos.size() > 0) {
				adapter = new ListaImoveisAdapter(this, enderecos);
				setListAdapter(adapter);

				if (Controlador.getInstancia().getPosicaoListaImoveis() > -1) {
					this.setSelection(Controlador.getInstancia().getPosicaoListaImoveis());
					adapter.setSelectedPosition(Controlador.getInstancia().getPosicaoListaImoveis());
				}
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		adapter.setSelectedPosition(position);

		Imovel imovel = imoveis.get(position);
		int status = imovel.getImovelStatus();

		if (status != Constantes.IMOVEL_INFORMATIVO) {
			Controlador.getInstancia().setCadastroSelecionadoByListPosition(position);
			Intent myIntent = new Intent(getApplicationContext(), MainTab.class);
			startActivityForResult(myIntent, 0);
		}
	}

	public class ListaImoveisAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final List<String> enderecos;

		private int selectedPosition = -1;

		public ListaImoveisAdapter(Activity context, List<String> enderecos) {
			super(context, R.layout.rowimovel, enderecos);
			this.context = context;
			this.enderecos = enderecos;
		}

		public void setSelectedPosition(int pos) {
			selectedPosition = pos;
			notifyDataSetChanged();
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = context.getLayoutInflater().inflate(R.layout.rowimovel, null, true);

			configurarSelecionado(position, rowView);
			configurarStatus(position, rowView);

			return rowView;
		}

		public String getListElementName(int element) {
			return enderecos.get(element);
		}

		private void configurarSelecionado(int position, View rowView) {
			if (selectedPosition == position) {
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

	@Override
	protected void onResume() {
		carregarEnderecos();
		super.onResume();
	}
}