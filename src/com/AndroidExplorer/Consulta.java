package com.AndroidExplorer;

import java.util.ArrayList;
import java.util.List;

import model.Imovel;
import util.Constantes;
import util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import business.Controlador;

public class Consulta extends ListActivity {
	
	private static int metodoBusca = 0;
	private static int filtroBusca = 0;
	
	private ListaImoveisAdapter adapter;
	private List<Imovel> imoveis;
	
	private String filtroCondicoes = null;
	private String buscaCondicoes = null;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consulta);

		configurarSpinnerMetodoBusca();
		configurarSpinnerFiltroBusca();

		final Button buttonConsulta = (Button) findViewById(R.id.buttonConsulta);
		buttonConsulta.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				carregarEnderecos();
			}
		});
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		carregarEnderecos();
	}

	private void carregarEnderecos() {
		adapter = null;
		setListAdapter(adapter);

		if (Controlador.getInstancia() != null) {
			if (Controlador.getInstancia().getCadastroDataManipulator() != null) {

				filtroCondicoes = null;
				buscaCondicoes = null;
				String filtroPreCondicao = null;

				String valorBusca = "\"" + ((EditText) findViewById(R.id.consulta)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", "") + "\"";

				// Verifica filtro de busca
				if (filtroBusca == Constantes.FILTRO_BUSCA_TODOS) {
					buscaCondicoes = "";
				} else if (filtroBusca == Constantes.FILTRO_BUSCA_VISITADOS_SUCESSO) {
					buscaCondicoes = "(imovel_status = " + Constantes.IMOVEL_SALVO + ")";
				} else if (filtroBusca == Constantes.FILTRO_BUSCA_VISITADOS_ANORMALIDADE) {
					buscaCondicoes = "(imovel_status = " + Constantes.IMOVEL_SALVO_COM_ANORMALIDADE + ")";
				} else if (filtroBusca == Constantes.FILTRO_BUSCA_NAO_VISITADOS) {
					buscaCondicoes = "(imovel_status = " + Constantes.IMOVEL_A_SALVAR + ")";
				} else if (filtroBusca == Constantes.FILTRO_BUSCA_NOVOS) {
					buscaCondicoes = "(imovel_status = " + Constantes.IMOVEL_NOVO + ")";
				} else if (filtroBusca == Constantes.FILTRO_BUSCA_TRANSMITIDOS) {
					buscaCondicoes = "(imovel_transmitido = " + Constantes.SIM + ")";
				} else if (filtroBusca == Constantes.FILTRO_BUSCA_NAO_TRANSMITIDOS) {
					buscaCondicoes = "(imovel_transmitido = " + Constantes.NAO + ")";
				}

				// Verifica Método de Busca
				if (metodoBusca == Constantes.METODO_BUSCA_TODOS) {
					filtroCondicoes = buscaCondicoes;
				} else if (metodoBusca == Constantes.METODO_BUSCA_MATRICULA) {

					if (buscaCondicoes.length() > 0) {
						filtroCondicoes = buscaCondicoes + " AND ";
						filtroCondicoes += "(matricula = " + valorBusca + ")";

					} else {
						filtroCondicoes = "(matricula = " + valorBusca + ")";
					}

				} else if (metodoBusca == Constantes.METODO_BUSCA_CPF) {

					filtroPreCondicao = "((cpf_cnpj_usuario = " + valorBusca + " AND tipo_pessoa_usuario = " + Constantes.TIPO_PESSOA_FISICA + ")";
					filtroPreCondicao += " OR (cpf_cnpj_proprietario = " + valorBusca + " AND tipo_pessoa_proprietario = " + Constantes.TIPO_PESSOA_FISICA + ")";
					filtroPreCondicao += " OR (cpf_cnpj_responsavel = " + valorBusca + " AND tipo_pessoa_responsavel = " + Constantes.TIPO_PESSOA_FISICA + "))";

					ArrayList<String> idList = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdClientes(filtroPreCondicao);

					if (buscaCondicoes.length() > 0) {
						filtroCondicoes = buscaCondicoes + " AND ";

						if (idList != null && idList.size() > 0) {
							filtroCondicoes += " (id = " + idList.get(0);

							for (int i = 1; i < idList.size(); i++) {
								filtroCondicoes += " OR id = " + idList.get(i);
							}

							filtroCondicoes += ")";
						}

					} else {
						if (idList != null && idList.size() > 0) {
							filtroCondicoes = " (id = " + idList.get(0);

							for (int i = 1; i < idList.size(); i++) {
								filtroCondicoes += " OR id = " + idList.get(i);
							}

							filtroCondicoes += ")";
						}
					}

				} else if (metodoBusca == Constantes.METODO_BUSCA_CNPJ) {

					filtroPreCondicao = "((cpf_cnpj_usuario = " + valorBusca + " AND tipo_pessoa_usuario = " + Constantes.TIPO_PESSOA_JURIDICA + ")";
					filtroPreCondicao += " OR (cpf_cnpj_proprietario = " + valorBusca + " AND tipo_pessoa_proprietario = " + Constantes.TIPO_PESSOA_JURIDICA + ")";
					filtroPreCondicao += " OR (cpf_cnpj_responsavel = " + valorBusca + " AND tipo_pessoa_responsavel = " + Constantes.TIPO_PESSOA_JURIDICA + "))";

					ArrayList<String> idList = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectIdClientes(filtroPreCondicao);

					if (buscaCondicoes.length() > 0) {
						filtroCondicoes = buscaCondicoes + " AND ";

						if (idList != null && idList.size() > 0) {
							filtroCondicoes += " (id = " + idList.get(0);

							for (int i = 1; i < idList.size(); i++) {
								filtroCondicoes += " OR id = " + idList.get(i);
							}

							filtroCondicoes += ")";
						}

					} else {
						if (idList != null && idList.size() > 0) {
							filtroCondicoes = " (id = " + idList.get(0);

							for (int i = 1; i < idList.size(); i++) {
								filtroCondicoes += " OR id = " + idList.get(i);
							}

							filtroCondicoes += ")";
						}
					}
				} else if (metodoBusca == Constantes.METODO_BUSCA_NUMERO_RESIDENCIA) {
					String complemento = "(numero_imovel = \"" + Util.adicionarCharDireita(5, valorBusca.replaceAll("\"", ""), ' ') + "\")";

					if (buscaCondicoes.length() > 0) {
						filtroCondicoes = buscaCondicoes + " AND ";
						filtroCondicoes += complemento;
					} else {
						filtroCondicoes = complemento;
					}
				}

				imoveis = (List<Imovel>) Controlador.getInstancia().getCadastroDataManipulator().selectStatusImoveis(filtroCondicoes);
				List<String> enderecos = (ArrayList<String>) Controlador.getInstancia().getCadastroDataManipulator().selectEnderecoImoveis(filtroCondicoes);

				if (enderecos != null && enderecos.size() > 0) {
					adapter = new ListaImoveisAdapter(this, enderecos);
					setListAdapter(adapter);
				}
			}
		}
	}
    
	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		adapter.setSelectedPosition(position);

		if (!imoveis.get(position).isInformativo()) {
			Controlador.getInstancia().setSelecionadoPorPosicao(position, filtroCondicoes);
			startActivityForResult(new Intent(getApplicationContext(), MainTab.class), 0);
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
				
				imageView.setImageResource(R.drawable.status_a_salvar);
				
				break;

			case Constantes.IMOVEL_SALVO:

				if (imovel.isTransmitido()) {
					imageView.setImageResource(R.drawable.status_salvo_transmitido);
				} else {
					imageView.setImageResource(R.drawable.status_salvo);
				}

				break;

			case Constantes.IMOVEL_SALVO_COM_ANORMALIDADE:

				if (imovel.isTransmitido()) {
					imageView.setImageResource(R.drawable.status_salvo_anormalidade_transmitido);
				} else {
					imageView.setImageResource(R.drawable.status_salvo_anormalidade);
				}
				break;

			case Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA:
				
				imageView.setImageResource(R.drawable.status_salvo_inconsistencia);
				
				break;

			case Constantes.IMOVEL_NOVO:
				
				if (imovel.isTransmitido()) {
					imageView.setImageResource(R.drawable.status_novo_transmitido);
				} else {
					imageView.setImageResource(R.drawable.status_novo);
				}
				
				break;

			case Constantes.IMOVEL_NOVO_COM_ANORMALIDADE:
				
				if (imovel.isTransmitido()) {
					imageView.setImageResource(R.drawable.status_novo_anormalidade_transmitido);
				} else {
					imageView.setImageResource(R.drawable.status_novo_anormalidade);
				}
				
				break;
				
			case Constantes.IMOVEL_EXCLUIDO:
				
				imageView.setImageResource(R.drawable.status_excluido);
				
				break;

			case Constantes.IMOVEL_INFORMATIVO:
				
				imageView.setImageResource(R.drawable.status_informativo);
				
				break;

			default:
				break;
			}
		}
	}

	private void configurarSpinnerFiltroBusca() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerFiltro);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.filtro, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		configurarFiltroBusca(spinner);
	}

	public void configurarFiltroBusca(Spinner spinner) {
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				filtroBusca = position;
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	private void configurarSpinnerMetodoBusca() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerMetodoBusca);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.consulta, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		configurarMetodoBusca(spinner);
	}

	public void configurarMetodoBusca(Spinner spinner) {
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				metodoBusca = position;
				if (metodoBusca == Constantes.METODO_BUSCA_CPF || metodoBusca == Constantes.METODO_BUSCA_CNPJ) {
					Util.addTextChangedListenerConsultaVerifierAndMask((EditText) findViewById(R.id.consulta), metodoBusca);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
}