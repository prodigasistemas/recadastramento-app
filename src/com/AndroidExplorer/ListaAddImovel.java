package com.AndroidExplorer;

import java.util.ArrayList;
import java.util.List;

import model.Imovel;
import util.Constantes;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import business.Controlador;

public class ListaAddImovel extends ListActivity {

	private ListaAddImovelAdapter adapter;
	private List<Imovel> imoveis;
	private String dialogMessage = null;

	ArrayList<String> posicao = new ArrayList<String>();

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

		if (Controlador.getInstancia() != null) {
			if (Controlador.getInstancia().getCadastroDataManipulator() != null) {

				imoveis = (List<Imovel>) Controlador.getInstancia().getCadastroDataManipulator().selectStatusImoveis(null);
				List<String> enderecos = (List<String>) Controlador.getInstancia().getCadastroDataManipulator().selectEnderecoImoveis(null);

				if (enderecos != null && enderecos.size() > 0) {
					adapter = new ListaAddImovelAdapter(this, enderecos);
					setListAdapter(adapter);
				}
			}
		}
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	public class ListaAddImovelAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final List<String> enderecos;

		public ListaAddImovelAdapter(Activity context, List<String> enderecos) {
			super(context, R.layout.rowimovel, enderecos);
			this.context = context;
			this.enderecos = enderecos;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.add_imovel_list, null, true);
			rowView.setBackgroundColor(Color.TRANSPARENT);
			CheckBox imovelChecked = (CheckBox) rowView.findViewById(R.id.list_checkbox);

			imovelChecked.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@SuppressWarnings("deprecation")
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						posicao.add(String.valueOf(position));

						if (posicao.size() == 1) {

							// verifica se é o primeiro da lista
							if (position == 0) {
								showDialog(Constantes.DIALOG_ID_ADD_NOVO_IMOVEL_ANTES_PRIMEIRO);

								// verifica se é o ultimo da lista
							} else if (position == imoveis.size() - 1) {
								showDialog(Constantes.DIALOG_ID_ADD_NOVO_IMOVEL_APOS_ULTIMO);
							}

						} else if (posicao.size() == 2) {
							// Dialog perguntando se confirma a posição do novo
							// imovel.
							showDialog(Constantes.DIALOG_ID_ADD_NOVO_IMOVEL);

						} else {
							dialogMessage = "Por favor, não selecione mais que 2 imóveis";
							showDialog(Constantes.DIALOG_ID_ERRO);
						}
					} else {
						posicao.remove(String.valueOf(position));
					}
				}
			});

			((TextView) rowView.findViewById(R.id.nomerota)).setText(enderecos.get(position));
			return rowView;

		}

		public String getListElementName(int element) {
			return enderecos.get(element);
		}
	}

	@Override
	protected void onResume() {
		carregarEnderecos();
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(final int id) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
		case Constantes.DIALOG_ID_SUCESSO:
			
		case Constantes.DIALOG_ID_ERRO:

			View layout = inflater.inflate(R.layout.custon_dialog, (ViewGroup) findViewById(R.id.layout_root));
			((TextView) layout.findViewById(R.id.messageDialog)).setText(dialogMessage);

			if (id == Constantes.DIALOG_ID_SUCESSO) {
				((ImageView) layout.findViewById(R.id.imageDialog)).setImageResource(R.drawable.save);

			} else if (id == Constantes.DIALOG_ID_ERRO) {
				((ImageView) layout.findViewById(R.id.imageDialog)).setImageResource(R.drawable.aviso);
			}

			builder.setView(layout);
			builder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					removeDialog(id);
				}
			});

			AlertDialog messageDialog = builder.create();
			return messageDialog;

		case Constantes.DIALOG_ID_ADD_NOVO_IMOVEL:
			
		case Constantes.DIALOG_ID_ADD_NOVO_IMOVEL_ANTES_PRIMEIRO:
			
		case Constantes.DIALOG_ID_ADD_NOVO_IMOVEL_APOS_ULTIMO:

			final View layoutConfirmationDialog = inflater.inflate(R.layout.confirmationdialog, (ViewGroup) findViewById(R.id.root));

			if (id == Constantes.DIALOG_ID_ADD_NOVO_IMOVEL) {

				// verifica se são 2 imoveis consecutivos.
				if (Integer.parseInt(posicao.get(0)) == (Integer.parseInt(posicao.get(1)) + 1) || 
					Integer.parseInt(posicao.get(0)) == (Integer.parseInt(posicao.get(1)) - 1)) {

					// permite escolher a posição do novo imovel
					((TextView) layoutConfirmationDialog.findViewById(R.id.textViewUser)).setText("Deseja criar o imóvel novo entre os dois imóveis selecionados?");

				} else {
					dialogMessage = "Por favor, selecione 2 imóveis consecutivos.";
					showDialog(Constantes.DIALOG_ID_ERRO);
					return null;
				}

			} else if (id == Constantes.DIALOG_ID_ADD_NOVO_IMOVEL_ANTES_PRIMEIRO) {
				((TextView) layoutConfirmationDialog.findViewById(R.id.textViewUser)).setText("Deseja criar o imóvel novo antes do primeiro imóvel da rota?");

			} else if (id == Constantes.DIALOG_ID_ADD_NOVO_IMOVEL_APOS_ULTIMO) {
				((TextView) layoutConfirmationDialog.findViewById(R.id.textViewUser)).setText("Deseja criar o imóvel novo após o último imóvel da rota?");
			}

			builder.setTitle("Atenção!");
			builder.setView(layoutConfirmationDialog);

			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					removeDialog(id);
					if (id == Constantes.DIALOG_ID_ADD_NOVO_IMOVEL) {
						// desselecionar o ultimo adicionado
					}
				}
			});

			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(id);
					Toast.makeText(getBaseContext(), "Novo imóvel adicionado!", Toast.LENGTH_LONG).show();
				}
			});
			
			messageDialog = builder.create();
			return messageDialog;
		}

		return null;
	}

}