package com.AndroidExplorer;

import java.util.Calendar;
import java.util.List;

import model.Endereco;
import model.Imovel;
import util.Constantes;
import util.Util;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.CellLocation;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import business.Controlador;
import dataBase.DataManipulator;

public class MainTab extends FragmentActivity implements TabHost.OnTabChangeListener, OnItemClickListener, LocationListener {

	private static final int IMOVEL_ANTERIOR = 0;
	private static final int IMOVEL_POSTERIOR = 1;
	
	public static Integer indiceNovoImovel;
	
	private Controlador controlador;
	private DataManipulator manipulator;
	
	private static TabHost tabHost;

	private Fragment clienteFragment;
	private Fragment imovelFragment;
	private Fragment servicosFragment;
	private Fragment medidorFragment;
	private Fragment anormalidadeFragment;

	private ClienteTab clienteTab = new ClienteTab();
	private ImovelTab imovelTab = new ImovelTab();
	private ServicosTab servicosTab = new ServicosTab();
	private MedidorTab medidorTab = new MedidorTab();
	private AnormalidadeTab anormalidadeTab = new AnormalidadeTab();
	
	private boolean loteInvalido = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.maintab);

		this.controlador = Controlador.getInstancia();
		this.manipulator = controlador.getCadastroDataManipulator();

		if (controlador.getImovelSelecionado().isInformativo()) {
			chamarProximoImovel();
		}
		
		verificarGPS();
		configurarTabHost();
		configurarTabs();
		adicionarTabs();
		configurarCor();
		tabHost.setCurrentTab(0);
		
		configurarMenu();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			showCompleteDialog(R.drawable.aviso, "Atenção.", "Deseja voltar para a lista de cadastros?", Constantes.DIALOG_ID_CONFIRMA_VOLTAR);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void configurarCor() {
		Imovel imovel = manipulator.getImovelSelecionado();
		int status = imovel.getImovelStatus();

		TabWidget tabWidget = tabHost.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			View child = tabWidget.getChildAt(i);

			if (status == Constantes.IMOVEL_SALVO && !imovel.isExcluido()) {
				child.setBackgroundResource(R.drawable.tab_custom_green);

			} else if (status == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE) {
				child.setBackgroundResource(R.drawable.tab_custom_red);

			} else if (status == Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA) {
				child.setBackgroundResource(R.drawable.tab_custom_yellow);

			} else {
				child.setBackgroundResource(R.drawable.tab_custom_white);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public int getCodigoAnormalidade() {
		return anormalidadeTab.getCodigoAnormalidade();
	}

	public void doGpsDesligado() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	public void chamarProximoImovel() {
		controlador.isCadastroAlterado();

		if (controlador.getPosicaoListaImoveis() == (manipulator.getNumeroImoveis()) - 1) {
			controlador.setCadastroSelecionadoByListPosition(0);
		} else {
			controlador.setCadastroSelecionadoByListPosition(controlador.getPosicaoListaImoveis() + 1);
		}

		finish();
		Intent intent = new Intent(this, MainTab.class);
		startActivity(intent);
	}

	public void onTabChanged(String tabId) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		if (tabId.equals("cliente")) {
			transaction.show(clienteFragment);
			transaction.hide(imovelFragment);
			transaction.hide(servicosFragment);
			transaction.hide(medidorFragment);
			transaction.hide(anormalidadeFragment);

		} else if (tabId.equals("imovel")) {
			transaction.show(imovelFragment);
			transaction.hide(clienteFragment);
			transaction.hide(servicosFragment);
			transaction.hide(medidorFragment);
			transaction.hide(anormalidadeFragment);

		} else if (tabId.equals("servico")) {
			transaction.show(servicosFragment);
			transaction.hide(clienteFragment);
			transaction.hide(imovelFragment);
			transaction.hide(medidorFragment);
			transaction.hide(anormalidadeFragment);

		} else if (tabId.equals("medidor")) {
			transaction.show(medidorFragment);
			transaction.hide(clienteFragment);
			transaction.hide(imovelFragment);
			transaction.hide(servicosFragment);
			transaction.hide(anormalidadeFragment);

		} else if (tabId.equals("anormalidade")) {
			transaction.show(anormalidadeFragment);
			transaction.hide(clienteFragment);
			transaction.hide(imovelFragment);
			transaction.hide(servicosFragment);
			transaction.hide(medidorFragment);
		}

		transaction.commit();
	}

	public void onLocationChanged(Location location) {
		((TextView) findViewById(R.id.txtValorLatitude)).setText(String.valueOf(location.getLatitude()));
		((TextView) findViewById(R.id.txtValorLongitude)).setText(String.valueOf(location.getLongitude()));
	}

	public void onProviderDisabled(String provider) {
		showNotifyDialog(R.drawable.aviso, "Alerta.", "O GPS está desligado. Por favor, ligue-o para continuar o cadastro.",
				Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "GPS ligado", Toast.LENGTH_SHORT).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	private void verificarGPS() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}

		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			showNotifyDialog(R.drawable.aviso, "Alerta", "O GPS está desligado. Por favor, ligue-o para continuar o cadastro.",
					Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
		}

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);

		CellLocation.requestLocationUpdate();
	}

	private void configurarTabHost() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.setOnTabChangedListener(this);

		getResources().getConfiguration();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			tabHost.setBackgroundResource(R.drawable.fundocadastro);
		} else {
			tabHost.setBackgroundResource(R.drawable.fundocadastro);
		}
	}

	private void configurarTabs() {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		transaction.add(R.id.tabCliente, clienteTab);
		transaction.add(R.id.tabImovel, imovelTab);
		transaction.add(R.id.tabServicos, servicosTab);
		transaction.add(R.id.tabMedidor, medidorTab);
		transaction.add(R.id.tabAnormalidade, anormalidadeTab);

		clienteFragment = manager.findFragmentById(R.id.tabCliente);
		imovelFragment = manager.findFragmentById(R.id.tabImovel);
		servicosFragment = manager.findFragmentById(R.id.tabServicos);
		medidorFragment = manager.findFragmentById(R.id.tabMedidor);
		anormalidadeFragment = manager.findFragmentById(R.id.tabAnormalidade);

		transaction.show(clienteFragment);
		transaction.hide(imovelFragment);
		transaction.hide(servicosFragment);
		transaction.hide(medidorFragment);
		transaction.hide(anormalidadeFragment);
		transaction.commit();
	}

	private void adicionarTabs() {
		addTab("cliente", "Cliente", R.drawable.tab_cliente, R.layout.clientetab);
		addTab("imovel", "Imóvel", R.drawable.tab_imovel, R.layout.imoveltab);
		addTab("servico", "Serviço", R.drawable.tab_servico, R.layout.servicotab);
		addTab("medidor", "Medidor", R.drawable.tab_medidor, R.layout.medidortab);
		addTab("anormalidade", "Anormalidade", R.drawable.tab_anormalidade, R.layout.anormalidadetab);
	}

	private void addTab(String tag, String titulo, int imagem, final int view) {
		Resources resources = getResources();

		TabSpec tabSpec = tabHost.newTabSpec(tag).setIndicator(titulo, resources.getDrawable(imagem)).setContent(new TabContentFactory() {

			public View createTabContent(String tag) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(view, (ViewGroup) findViewById(R.layout.maintab));
				return layout;
			}
		});

		tabHost.addTab(tabSpec);

		configurarCor();
	}

	private void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment dialog = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
		dialog.show(getSupportFragmentManager(), "dialog");
	}

	private void showCompleteDialog(int iconId, String title, String message, int messageType) {
		CompleteAlertDialogFragment dialog = CompleteAlertDialogFragment.newInstance(iconId, title, message, messageType);
		dialog.show(getSupportFragmentManager(), "dialog");
	}
	
	private void configurarMenu() {

		final int menu = controlador.getMenuSelecionado();
		final int posicao = controlador.getPosicaoListaImoveis();
		
		switch (menu) {
		case R.id.botaoNovoImovel:
			
			final Imovel imovelSelecionado = controlador.getImovelSelecionado();
			List<Imovel> imoveis = manipulator.selectEnderecoImovel(null);

			final Imovel imovelAnterior = imoveis.get(posicao - 1);
			final Imovel imovelPosterior = imoveis.get(posicao + 1);
			
			String enderecoAnterior = "";
			String enderecoPosterior = "";

			if (!isInicioLista(posicao)) {
				enderecoAnterior = montarEndereco(imovelAnterior);
			}

			if (!isFimLista(posicao)) {
				enderecoPosterior = montarEndereco(imovelPosterior);
			}

			Imovel imovelAtual = imoveis.get(posicao);
			String enderecoAtual = montarEndereco(imovelAtual);

			final View view = getViewDialogImovelNovo();
			final AlertDialog dialog = configurarDialogImovelNovo(view, posicao, enderecoAnterior, enderecoPosterior, enderecoAtual);

			configurarBotaoInserirImovelNovoAntes(posicao, imovelSelecionado, imovelAnterior, view, dialog);
			configurarBotaoInserirImovelNovoDepois(posicao, imovelSelecionado, imovelPosterior, view, dialog);
			
			break;

		case R.id.botaoAdicionarSublote:
			indiceNovoImovel = posicao + 1;
			montarImovelNovoSublote();
			
			break;

		case R.id.botaoExcluirImovel:

			showCompleteDialog(R.drawable.aviso, "Atenção", "Confirma exclusão deste imóvel?", Constantes.DIALOG_ID_CONFIRMA_EXCLUSAO);

			break;

		default:
			break;
		}
		
		controlador.setMenuSelecionado(-1);
	}

	private boolean isInicioLista(long id) {
		return id == 0;
	}

	private boolean isFimLista(long id) {
		return id == manipulator.getNumeroImoveis() - 1;
	}
	
	private String montarEndereco(Imovel imovel) {
		return Util.capitalizarString(
				imovel.getEnderecoImovel().getLogradouro() + ", nº " + 
		        imovel.getEnderecoImovel().getNumero() + " " + 
				imovel.getEnderecoImovel().getComplemento());
	}
	
	private View getViewDialogImovelNovo() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = (View) inflater.inflate(R.layout.dialog_imovel_novo, (ViewGroup) findViewById(R.layout.maintab));
		return view;
	}
	
	private AlertDialog configurarDialogImovelNovo(final View view, int posicao, String enderecoAnterior, String enderecoPosterior, String enderecoAtual) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Por favor, escolha a posição do imóvel novo");
		dialog.setView(view);
		dialog.show();

		if (posicao >= 1) {
			((TextView) view.findViewById(R.id.txtImovelAnterior)).setText(enderecoAnterior);
		}

		if (posicao <= manipulator.getNumeroImoveis()) {
			((TextView) view.findViewById(R.id.txtImovelPosterior)).setText(enderecoPosterior);
		}

		((TextView) view.findViewById(R.id.txtImovelAtual)).setText(enderecoAtual);

		return dialog;
	}
	
	private void configurarBotaoInserirImovelNovoAntes(final int posicao, final Imovel imovelSelecionado, final Imovel imovelAnterior, 
			final View view, final AlertDialog dialog) {

		Button botao = (Button) view.findViewById(R.id.txtInserirImovelAntes);
		botao.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();

				Controlador.getInstancia().setPosicaoListaImoveis(posicao); // TODO - Necessário?
				
				if (isInicioLista(posicao)) {

					indiceNovoImovel = posicao + 1;

					int lote = Integer.parseInt(imovelSelecionado.getLote()) / 2;

					if (lote < 1) {
						loteInvalido = true;
					}

					montarImovelNovo(imovelSelecionado, lote);

				} else if (isFimLista(posicao)) {

					indiceNovoImovel = 0;

					int lote = verificarLoteInvalido(imovelSelecionado, imovelAnterior);

					montarImovelNovo(imovelSelecionado, lote);

				} else if (isMesmoEndereco(imovelAnterior, imovelSelecionado)) {

					indiceNovoImovel = posicao + 1;

					int lote = verificarLoteInvalido(imovelSelecionado, imovelAnterior);

					montarImovelNovo(imovelAnterior, lote);

				} else if (!isMesmoEndereco(imovelAnterior, imovelSelecionado)) {

					indiceNovoImovel = posicao + 1;
					
					configurarDialogFace(imovelSelecionado, imovelAnterior, IMOVEL_ANTERIOR);
				}
			}
		});
	}

	private void configurarBotaoInserirImovelNovoDepois(final int posicao, final Imovel imovelSelecionado, final Imovel imovelPosterior,
			final View view, final AlertDialog dialog) {

		Button botao = (Button) view.findViewById(R.id.txtInserirImovelDepois);
		botao.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();

				Controlador.getInstancia().setPosicaoListaImoveis(posicao); // TODO - Necessário???

				if (manipulator.getNumeroImoveis() == 1) {

					indiceNovoImovel = 0;

					int lote = Integer.parseInt(imovelSelecionado.getLote()) + 4;

					montarImovelNovo(imovelSelecionado, lote);

				} else if (isInicioLista(posicao)) {

					indiceNovoImovel = posicao + 2;

					int lote = verificarLoteInvalido(imovelSelecionado, imovelPosterior);

					montarImovelNovo(imovelSelecionado, lote);

				} else if (isFimLista(posicao)) {

					indiceNovoImovel = 0;

					int lote = Integer.parseInt(imovelSelecionado.getLote()) + 4;

					montarImovelNovo(imovelSelecionado, lote);

				} else if (isMesmoEndereco(imovelSelecionado, imovelPosterior)) {

					indiceNovoImovel = posicao + 2;

					int lote = verificarLoteInvalido(imovelSelecionado, imovelPosterior);

					montarImovelNovo(imovelPosterior, lote);

				} else if (!isMesmoEndereco(imovelPosterior, imovelSelecionado)) {

					indiceNovoImovel = posicao + 2;
					
					configurarDialogFace(imovelSelecionado, imovelPosterior, IMOVEL_POSTERIOR);
				}
			}
		});
	}
	
	private int verificarLoteInvalido(final Imovel imovelSelecionado, final Imovel imovelAnteriorOuPosterior) {
		int loteSelecionado = Integer.parseInt(imovelSelecionado.getLote());
		int loteAnteriorOuPosterior = Integer.parseInt(imovelAnteriorOuPosterior.getLote());

		int lote = (loteSelecionado + loteAnteriorOuPosterior) / 2;

		if (loteSelecionado == lote || loteAnteriorOuPosterior == lote) {
			loteInvalido = true;
		}

		return lote;
	}
	
	private void montarImovelNovo(Imovel imovelReferencia, int lote) {
		controlador.setCadastroSelecionadoNovoImovel();

		int qtdImoveisNovos = manipulator.getQtdImoveisNovo();

		Imovel imovel = new Imovel();
		imovel.setMatricula("" + (++qtdImoveisNovos));
		imovel.getEnderecoImovel().setLogradouro(imovelReferencia.getEnderecoImovel().getLogradouro());
		imovel.getEnderecoImovel().setBairro(imovelReferencia.getEnderecoImovel().getBairro());
		imovel.getEnderecoImovel().setCep(imovelReferencia.getEnderecoImovel().getCep());
		imovel.getEnderecoImovel().setMunicipio(imovelReferencia.getEnderecoImovel().getMunicipio());
		imovel.setRota(imovelReferencia.getRota());
		imovel.setFace(imovelReferencia.getFace());
		imovel.setCodigoMunicipio("" + imovelReferencia.getCodigoMunicipio());
		imovel.setSubLote("000");
		imovel.setLote(Util.adicionarZerosEsquerdaNumero(4, String.valueOf(lote)));
		imovel.setCodigoLogradouro("" + imovelReferencia.getCodigoLogradouro());
		imovel.setLocalidade(imovelReferencia.getLocalidade());
		imovel.setSetor(imovelReferencia.getSetor());
		imovel.setQuadra(imovelReferencia.getQuadra());
		imovel.setImovelStatus("" + Constantes.IMOVEL_NOVO);
		imovel.setOperacoTipo("" + Constantes.OPERACAO_CADASTRO_NOVO);
		imovel.setNovoRegistro(true);

		controlador.setImovelSelecionado(imovel);
		controlador.getClienteSelecionado().setNovoRegistro(true);
		controlador.getServicosSelecionado().setNovoRegistro(true);
		controlador.getMedidorSelecionado().setNovoRegistro(true);
		controlador.getAnormalidadeImovelSelecionado().setNovoRegistro(true);

		if (loteInvalido) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Erro");
			dialog.setMessage("Não há mais lotes disponíveis");
			dialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
					Intent myIntent = new Intent(getApplicationContext(), MainTab.class);
					startActivity(myIntent);
				}
			});

			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if ((keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK)
							&& (event.getRepeatCount() == 0)) {

						return true; // Pretend we processed it
					}
					return false; // Any other keys are still processed as
									// normal
				}
			});

			dialog.show();
		} else {
			finish();
			startActivity(new Intent(getApplicationContext(), MainTab.class));
		}
	}

	private boolean isMesmoEndereco(Imovel imovelAnterior, Imovel imovelSelecionado) {
		return imovelAnterior.getEnderecoImovel().getLogradouro().trim().equals(imovelSelecionado.getEnderecoImovel().getLogradouro().trim());
	}
	
	private void configurarDialogFace(final Imovel imovelSelecionado, final Imovel imovelReferencia, final int posicao) {
		ListView lista = new ListView(MainTab.this);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainTab.this, android.R.layout.simple_list_item_1);

		final AlertDialog dialog = new AlertDialog.Builder(MainTab.this).create();
		dialog.setTitle("Por favor, selecione a rua ao qual o imóvel faz frente");
		dialog.setView(lista);
		dialog.show();

		if (posicao == IMOVEL_ANTERIOR) {
			arrayAdapter.add(Util.capitalizarString(imovelReferencia.getEnderecoImovel().getLogradouro() + ", nº "
					+ imovelReferencia.getEnderecoImovel().getNumero() + " " + imovelReferencia.getEnderecoImovel().getComplemento()));
			arrayAdapter.add(Util.capitalizarString(imovelSelecionado.getEnderecoImovel().getLogradouro() + ", nº "
					+ imovelSelecionado.getEnderecoImovel().getNumero() + " " + imovelSelecionado.getEnderecoImovel().getComplemento()));

		} else if (posicao == IMOVEL_POSTERIOR) {
			arrayAdapter.add(Util.capitalizarString(imovelSelecionado.getEnderecoImovel().getLogradouro() + ", nº "
					+ imovelSelecionado.getEnderecoImovel().getNumero() + " " + imovelSelecionado.getEnderecoImovel().getComplemento()));
			arrayAdapter.add(Util.capitalizarString(imovelReferencia.getEnderecoImovel().getLogradouro() + ", nº "
					+ imovelReferencia.getEnderecoImovel().getNumero() + " " + imovelReferencia.getEnderecoImovel().getComplemento()));
		}

		lista.setAdapter(arrayAdapter);
		lista.setBackgroundColor(Color.parseColor("#6e6e6e"));
		lista.setCacheColorHint(0);
		lista.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				dialog.dismiss();

				// Monta o novo imovel com os dados do imovel selecionado
				if ((position == 0 && posicao == IMOVEL_POSTERIOR)) {
					montarImovelNovo(imovelSelecionado, (Integer.parseInt(imovelSelecionado.getLote()) + 4));
					return;

				} else if (position == 1 && posicao == IMOVEL_ANTERIOR) {
					int lote = Integer.parseInt(imovelSelecionado.getLote()) / 2;
					if (lote < 1) {
						loteInvalido = true;
					}
					montarImovelNovo(imovelSelecionado, lote);
					return;
				}

				if (posicao == IMOVEL_ANTERIOR) {
					int lote = (Integer.parseInt(imovelReferencia.getLote()) + 4);
					montarImovelNovo(imovelSelecionado, lote);

				} else if (posicao == IMOVEL_POSTERIOR) {
					int lote = Integer.parseInt(imovelReferencia.getLote()) / 2;
					if (lote < 1) {
						loteInvalido = true;
					}
					montarImovelNovo(imovelReferencia, lote);
				}

			}
		});
	}
	
	private void montarImovelNovoSublote() {
		Imovel imovelReferencia = controlador.getImovelSelecionado();
		
		controlador.setCadastroSelecionadoNovoImovel();

		List<String> inscricoes = manipulator.selectSubLotesImovel(imovelReferencia.getLocalidade() + imovelReferencia.getSetor()
				+ imovelReferencia.getQuadra() + imovelReferencia.getLote());

		int quantidadeImoveisNovos = manipulator.getQtdImoveisNovo() + 1;

		Imovel imovel = new Imovel();
		imovel.setSubLote(Util.adicionarZerosEsquerdaNumero(3, getNovoSublote(inscricoes)));
		imovel.setMatricula(String.valueOf((quantidadeImoveisNovos)));
		imovel.setLocalidade(imovelReferencia.getLocalidade());
		imovel.setSetor(imovelReferencia.getSetor());
		imovel.setQuadra(imovelReferencia.getQuadra());
		imovel.setLote(imovelReferencia.getLote());
		imovel.setRota(imovelReferencia.getRota());
		imovel.setCodigoLogradouro(String.valueOf(imovelReferencia.getCodigoLogradouro()));
		imovel.setFace(imovelReferencia.getFace());
		
		Endereco endereco = new Endereco();
		endereco.setTipoLogradouro(String.valueOf(imovelReferencia.getEnderecoImovel().getTipoLogradouro()));
		endereco.setLogradouro(imovelReferencia.getEnderecoImovel().getLogradouro());
		endereco.setBairro(imovelReferencia.getEnderecoImovel().getBairro());
		endereco.setCep(imovelReferencia.getEnderecoImovel().getCep());
		endereco.setMunicipio(imovelReferencia.getEnderecoImovel().getMunicipio());
		imovel.setEnderecoImovel(endereco);
		
		imovel.setCodigoMunicipio(String.valueOf(imovelReferencia.getCodigoMunicipio()));
		imovel.setCodigoLogradouro(String.valueOf(imovelReferencia.getCodigoLogradouro()));
		imovel.setImovelStatus(String.valueOf(Constantes.IMOVEL_NOVO));
		imovel.setOperacoTipo(String.valueOf(Constantes.OPERACAO_CADASTRO_NOVO));

		controlador.setImovelSelecionado(imovel);

		finish();
		startActivity(new Intent(getApplicationContext(), MainTab.class));
	}

	private String getNovoSublote(List<String> inscricoes) {
		String ultimaInscricao = inscricoes.get(inscricoes.size() - 1);

		String ultimoSublote = null;
		if (ultimaInscricao.trim().length() == 16) {
			ultimoSublote = ultimaInscricao.substring(13, 16);
		} else {
			ultimoSublote = ultimaInscricao.substring(14, 17);
		}

		return String.valueOf(Integer.parseInt(ultimoSublote) + 1);
	}
	
	public void excluirImovel() {
		Imovel imovelSelecionado = controlador.getImovelSelecionado();
		imovelSelecionado.setOperacoTipo(String.valueOf(Constantes.OPERACAO_CADASTRO_EXCLUIDO));
		imovelSelecionado.setImovelStatus(String.valueOf(Constantes.IMOVEL_SALVO));
		imovelSelecionado.setData(Util.formatarData(Calendar.getInstance().getTime()));
		imovelSelecionado.setImovelEnviado(String.valueOf(Constantes.NAO)); // TODO - TRANSMISTIR?

		manipulator.getClienteSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));
		manipulator.getServicosSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));
		manipulator.getMedidorSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));
		manipulator.getAnormalidadeImovelSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));

		manipulator.salvarCliente();
		manipulator.salvarServico();
		manipulator.salvarImovel();
		manipulator.salvarMedidor();
		manipulator.salvarAnormalidadeImovel();

		configurarCor();
		
		showNotifyDialog(R.drawable.save, "Sucesso", "Imovel excluído com sucesso", Constantes.DIALOG_ID_CONFIRMA_EXCLUSAO);
	}
}