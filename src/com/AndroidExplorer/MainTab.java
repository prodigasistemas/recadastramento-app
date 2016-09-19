package com.AndroidExplorer;

import java.util.Calendar;
import java.util.List;

import dataBase.DataManipulator;

import model.Imovel;
import util.Constantes;
import util.Util;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.CellLocation;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import background.EnviarCadastroOnlineThread;
import business.Controlador;
 
public class MainTab extends FragmentActivity implements TabHost.OnTabChangeListener, OnItemClickListener, LocationListener {

	private static TabHost tabHost;
	public static Integer indiceNovoImovel;
	public boolean numeroLoteInsuficiente = false;
	private static final int IMOVEL_ANTERIOR = 0; 
	private static final int IMOVEL_POSTERIOR = 1; 
	private String dialogMessage = null;
	private static EnviarCadastroOnlineThread progThread;
	private static int increment= 0;
	Fragment clienteFragment;
	Fragment imovelFragment;
	Fragment servicosFragment;
	Fragment medidorFragment;
	Fragment anormalidadeFragment;
	ClienteTab clienteTab = new ClienteTab();
	ImovelTab imovelTab = new ImovelTab();
	ServicosTab servicosTab = new ServicosTab();
	MedidorTab medidorTab = new MedidorTab();
	AnormalidadeTab anormalidadeTab = new AnormalidadeTab();

	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maintab);
	    
	    initializeTabs();
	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	if ((keyCode == KeyEvent.KEYCODE_BACK)){
    		dialogMessage = " Deseja voltar para a lista de cadastros? ";
    		showCompleteDialog(R.drawable.aviso, "Atenção!", dialogMessage, Constantes.DIALOG_ID_CONFIRMA_VOLTAR);
            return true;

        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initializeTabs(){
	    
        /* Use the LocationManager class to obtain GPS locations */
        mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        	mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        boolean enabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabled){
	        dialogMessage = " GPS está desligado. Por favor, ligue-o para continuar o cadastro. ";
	        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
        }	    

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		provider = mLocManager.getBestProvider(criteria, false);
		lastKnownLocation = mLocManager.getLastKnownLocation(provider);
    	CellLocation.requestLocationUpdate();

	    tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    tabHost.setup();
	    tabHost.setOnTabChangedListener(this);
	    
	    // Define a imagem de fundo de acordo com a orientacao do dispositivo
	    if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_PORTRAIT){
	    	tabHost.setBackgroundResource(R.drawable.fundocadastro);
	    }else{
	    	tabHost.setBackgroundResource(R.drawable.fundocadastro);
	    }
	    
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		ft.add(R.id.tabCliente, clienteTab);
    	ft.add(R.id.tabImovel, imovelTab);
    	ft.add(R.id.tabServicos, servicosTab);
    	ft.add(R.id.tabMedidor, medidorTab);
    	ft.add(R.id.tabAnormalidade, anormalidadeTab);
    	
    	clienteFragment = fm.findFragmentById(R.id.tabCliente);
    	imovelFragment = fm.findFragmentById(R.id.tabImovel);
    	servicosFragment = fm.findFragmentById(R.id.tabServicos);
    	medidorFragment = fm.findFragmentById(R.id.tabMedidor);
    	anormalidadeFragment = fm.findFragmentById(R.id.tabAnormalidade);

    	ft.show(clienteFragment);
    	ft.hide(imovelFragment);
    	ft.hide(servicosFragment);
    	ft.hide(medidorFragment);
    	ft.hide(anormalidadeFragment);
    	ft.commit();

	    addTab("cliente", "Cliente", R.drawable.tab_cliente, R.layout.clientetab);
	    addTab("imovel", "Imóvel", R.drawable.tab_imovel, R.layout.imoveltab);
	    addTab("servico", "Serviço", R.drawable.tab_servico, R.layout.servicotab);
	    addTab("medidor", "Medidor", R.drawable.tab_medidor, R.layout.medidortab);
	    addTab("anormalidade", "Anormalidade", R.drawable.tab_anormalidade, R.layout.anormalidadetab);

	    setTabColor();
	    tabHost.setCurrentTab(0);
    }
        
	// Instancia novas tabs
	public void addTab(String tag, String titulo, int imagem, final int view) {
		TabHost.TabSpec tabSpec;
	    Resources res = getResources();
	    		
	    tabSpec = tabHost.newTabSpec(tag).setIndicator(titulo, res.getDrawable(imagem)).setContent(new TabContentFactory() {

            public View createTabContent(String tag) {
            	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	View layout = inflater.inflate(view, (ViewGroup) findViewById(R.layout.maintab));
                return layout;
            }
        });
	    
	    tabHost.addTab(tabSpec);
	    
	    setTabColor();
	}
	
    public void setTabColor() {
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++){
            
        	if (getCadastroDataManipulator().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_SALVO){
        		tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_custom_green);
            
            }else if (getCadastroDataManipulator().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE){
            	tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_custom_red);
            }
            else if(getCadastroDataManipulator().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_A_SALVAR){
            	tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_custom_white);            	
            }
        }
    }
    
	public boolean onCreateOptionsMenu(Menu menu) {
	    if (indiceNovoImovel == null) {
	    	MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.layout.menuoptions, menu);
	    }
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		
		final int posicao = Controlador.getInstancia().getCadastroListPosition();

		// Handle item selection
	    switch (item.getItemId()) {
	    case R.id.proximoImovel:

	    	Controlador.getInstancia().isCadastroAlterado();
	    	
	    	if(Controlador.getInstancia().getCadastroListPosition() == (getCadastroDataManipulator().getNumeroImoveis())-1){
				Controlador.getInstancia().setCadastroSelecionadoByListPosition(0);

			}else{
		    	Controlador.getInstancia().setCadastroSelecionadoByListPosition(Controlador.getInstancia().getCadastroListPosition()+1);
			}
	    	finish();
			Intent myIntent = new Intent(getApplicationContext(), MainTab.class);
			startActivity(myIntent);
	    	return true;

	    case R.id.imovelAnterior:

	    	Controlador.getInstancia().isCadastroAlterado();
	    	
	    	if(Controlador.getInstancia().getCadastroListPosition() <= 0){
				Controlador.getInstancia().setCadastroSelecionadoByListPosition((int)getCadastroDataManipulator().getNumeroImoveis()-1);
			}else{
		    	Controlador.getInstancia().setCadastroSelecionadoByListPosition(Controlador.getInstancia().getCadastroListPosition()-1);
			}
	    	finish();
	    	
			myIntent = new Intent(getApplicationContext(), MainTab.class);
			startActivity(myIntent);
	        return true;
	    
	    case R.id.adicionarNovo:
	    	
	    	
	    	List<Imovel> imoveis = getCadastroDataManipulator().selectEnderecoImovel(null);
			
	    	Imovel proximo = null;
	    	
	    	Imovel anterior = null;
	    	
	    	String imovelAnterior = "";
	    	String imovelPosterior = "";
	    	
	    	if (!isInicioLista(posicao)) {
	    		anterior = imoveis.get(posicao-1);
	    		imovelAnterior = Util.capitalizarString(anterior.getEnderecoImovel().getLogradouro() + ", nº " + anterior.getEnderecoImovel().getNumero() + 
	    				" " + anterior.getEnderecoImovel().getComplemento());
	    	}
	    	
	    	if (!isFimLista(posicao)) {
	    		proximo = imoveis.get(posicao+1);
	    		imovelPosterior = Util.capitalizarString(proximo.getEnderecoImovel().getLogradouro() + ", nº " + proximo.getEnderecoImovel().getNumero() + 
	    				" " + proximo.getEnderecoImovel().getComplemento());
	    	}
	    	
	    	String imovelAtual = Util.capitalizarString(getImovelSelecionado().getEnderecoImovel().getLogradouro() + ", nº " + 
	    						 getImovelSelecionado().getEnderecoImovel().getNumero() + 
	    						 " " + getImovelSelecionado().getEnderecoImovel().getComplemento());
	    	
	    	
	    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	final View view = (View) inflater.inflate(R.layout.add_lote_dialog, (ViewGroup) findViewById(R.layout.maintab));
	    	
	    	final AlertDialog dialog = new AlertDialog.Builder(this).create();
	    	dialog.setTitle("Por favor, escolha a posição do novo imóvel");
	    	dialog.setView(view);
	    	dialog.show();
	    	
	    	if (posicao >= 1) {
	    		((TextView) view.findViewById(R.id.txtImovelAnterior)).setText(imovelAnterior);
	    	}
	    	
	    	if (posicao <= getCadastroDataManipulator().getNumeroImoveis()) {
	    		((TextView) view.findViewById(R.id.txtImovelPosterior)).setText(imovelPosterior);
	    	}
	    	
	    	((TextView) view.findViewById(R.id.txtImovelAtual)).setText(imovelAtual); 
	    	 
	    	// Primeiro botao para adicionar imovel
	    	Button inserirImoveAntes = (Button) view.findViewById(R.id.txtInserirImovelAntes);
	    	final Imovel ant = anterior;
	    	final Imovel prox = proximo;
	    	inserirImoveAntes.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					dialog.dismiss();
					Controlador.getInstancia().setCadastroListPosition(posicao);
					if (isInicioLista(posicao)) {
					
						indiceNovoImovel = posicao + 1;
						int lote = (int)(Integer.parseInt(getImovelSelecionado().getLote())/2);
						if (lote < 1) {
							numeroLoteInsuficiente = true;;
						}
						preencheNovoImovel(getImovelSelecionado(), montarLote(""+lote));
						
					} else if (isFimLista(posicao)) {
						
						indiceNovoImovel = 0;
						
						int lote = (Integer.parseInt(getImovelSelecionado().getLote()) + Integer.parseInt(ant.getLote()))/2;
						if (getImovelSelecionado().getLote().equals(""+lote) || ant.getLote().equals(""+lote)) {
							numeroLoteInsuficiente = true;;
						}
						preencheNovoImovel(getImovelSelecionado(), montarLote(""+lote));
					
					} else if (isMesmoEndereco(ant.getEnderecoImovel().getLogradouro(), getImovelSelecionado().getEnderecoImovel().getLogradouro())) {
						
						indiceNovoImovel = posicao + 1;
						
						int lote = (Integer.parseInt(ant.getLote()) + Integer.parseInt(getImovelSelecionado().getLote()))/2;
						if (getImovelSelecionado().getLote().equals(""+lote) || ant.getLote().equals(""+lote)) {
							numeroLoteInsuficiente = true;;
						}
	                    preencheNovoImovel(ant, montarLote(""+lote));
	                    
					} else if (!isMesmoEndereco(ant.getEnderecoImovel().getLogradouro(), Controlador.getInstancia()
							.getImovelSelecionado().getEnderecoImovel().getLogradouro())) {
						
						indiceNovoImovel = posicao + 1;
						showDialogSelecionarFace(ant, IMOVEL_ANTERIOR);
	                    
					} 
				}
			});
	    	
	    	// Segundo botao para adicionar imovel
	    	Button inserirImoveDepois = (Button) view.findViewById(R.id.txtInserirImovelDepois);
	    	inserirImoveDepois.setOnClickListener(new OnClickListener() {
	    		
	    		public void onClick(View v) {
	    			dialog.dismiss();
	    			Controlador.getInstancia().setCadastroListPosition(posicao);
	    			int qtdImoveisRota = getCadastroDataManipulator().getNumeroImoveis();

	    			if (qtdImoveisRota == 1) {
	    				indiceNovoImovel = 0;
						int lote = (Integer.parseInt(getImovelSelecionado().getLote())+4);
						preencheNovoImovel(getImovelSelecionado(), montarLote(""+lote));
						
	    			} else if (isInicioLista(posicao)) {
						
	    				indiceNovoImovel = posicao+2;
	    				int lote = (Integer.parseInt(getImovelSelecionado().getLote()) + Integer.parseInt(prox.getLote()))/2;
	    				if (getImovelSelecionado().getLote().equals(""+lote) || prox.getLote().equals(""+lote)) {
							numeroLoteInsuficiente = true;;
						}
						preencheNovoImovel(getImovelSelecionado(), montarLote(""+lote));
						
					} else if (isFimLista(posicao)) {
						
						indiceNovoImovel = 0;
						int lote = (Integer.parseInt(getImovelSelecionado().getLote())+4);
						preencheNovoImovel(getImovelSelecionado(), montarLote(""+lote));
					
					} else if (isMesmoEndereco(getImovelSelecionado().getEnderecoImovel().getLogradouro(), prox.getEnderecoImovel().getLogradouro())) {
	                
						indiceNovoImovel = posicao+2;
						int lote = (Integer.parseInt(getImovelSelecionado().getLote()) + Integer.parseInt(prox.getLote()))/2;
						if (getImovelSelecionado().getLote().equals(""+lote) || prox.getLote().equals(""+lote)) {
							numeroLoteInsuficiente = true;;
						}
	                    preencheNovoImovel(prox, montarLote(""+lote));
					
	    			} else if (!isMesmoEndereco(prox.getEnderecoImovel().getLogradouro(), getImovelSelecionado().getEnderecoImovel().getLogradouro())) {
						
						indiceNovoImovel = posicao+2;
						showDialogSelecionarFace(prox, IMOVEL_POSTERIOR);
	                    
					}
	    		}
	    	});
	    	

	        return true;
	        
	    case R.id.novoSublote:

	    	Controlador.getInstancia().setCadastroListPosition(posicao);
			
			indiceNovoImovel = posicao + 1;
			preencheSubLote(getImovelSelecionado());
			
	    	return true;
	        
	    case R.id.imovelExcluir:
    		dialogMessage = "Confirma exclusão deste imóvel?";
    		showCompleteDialog(R.drawable.aviso, "Atenção!", dialogMessage, Constantes.DIALOG_ID_CONFIRMA_EXCLUSAO);
            return true;
	    		        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onResume() {
//		initializeTabs();
		super.onResume();
	}

    public boolean ImovelExcluidoDialog(){

    	// setando dados do imóvel excluído.
    	getImovelSelecionado().setOperacoTipo(Constantes.OPERACAO_CADASTRO_EXCLUIDO);
    	getImovelSelecionado().setImovelStatus(String.valueOf(Constantes.IMOVEL_SALVO));
    	getImovelSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));

    	Controlador.getInstancia().getClienteSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));
    	Controlador.getInstancia().getServicosSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));
    	Controlador.getInstancia().getMedidorSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));
    	Controlador.getInstancia().getAnormalidadeImovelSelecionado().setData(Util.formatarData(Calendar.getInstance().getTime()));
    	
    	// Cadastro configurado como Nao Transmitido
    	Controlador.getInstancia().getImovelSelecionado().setImovelEnviado(String.valueOf(Constantes.NAO));
    	
    	Controlador.getInstancia().getCadastroDataManipulator().salvarCliente();
    	Controlador.getInstancia().getCadastroDataManipulator().salvarServico();
    	Controlador.getInstancia().getCadastroDataManipulator().salvarImovel();
    	Controlador.getInstancia().getCadastroDataManipulator().salvarMedidor();
    	Controlador.getInstancia().getCadastroDataManipulator().salvarAnormalidadeImovel();
        this.setTabColor();
   		dialogMessage = " Imovel excluído com sucesso!";
        showNotifyDialog(R.drawable.save, "", dialogMessage, Constantes.DIALOG_ID_CONFIRMA_EXCLUSAO);
        return true;
    }
    
	public void preencheNovoImovel(Imovel imovelReferencia, String lote) {
        Controlador.getInstancia().setCadastroSelecionadoNovoImovel();
        
        int qtdImoveisNovos = getCadastroDataManipulator().getQtdImoveisNovo(); 
        
		Imovel imovel = new Imovel();
		imovel.setMatricula(""+(++qtdImoveisNovos));
		imovel.getEnderecoImovel().setLogradouro(imovelReferencia.getEnderecoImovel().getLogradouro());
		imovel.getEnderecoImovel().setBairro(imovelReferencia.getEnderecoImovel().getBairro());
		imovel.getEnderecoImovel().setCep(imovelReferencia.getEnderecoImovel().getCep());
		imovel.getEnderecoImovel().setMunicipio(imovelReferencia.getEnderecoImovel().getMunicipio());
		imovel.setRota(imovelReferencia.getRota());
		imovel.setFace(imovelReferencia.getFace());
		imovel.setCodigoMunicipio(""+imovelReferencia.getCodigoMunicipio());
		imovel.setSubLote("000");
		imovel.setLote(lote);
		imovel.setCodigoLogradouro(""+imovelReferencia.getCodigoLogradouro());
		imovel.setLocalidade(imovelReferencia.getLocalidade());
		imovel.setSetor(imovelReferencia.getSetor());
		imovel.setQuadra(imovelReferencia.getQuadra());
		imovel.setImovelStatus(""+Constantes.IMOVEL_NOVO);
		imovel.setOperacoTipo(Constantes.OPERACAO_CADASTRO_NOVO);
		imovel.setNovoRegistro(true);
		
		Controlador.getInstancia().setImovelSelecionado(imovel);
		Controlador.getInstancia().getClienteSelecionado().setNovoRegistro(true);
		Controlador.getInstancia().getServicosSelecionado().setNovoRegistro(true);
		Controlador.getInstancia().getMedidorSelecionado().setNovoRegistro(true);
		Controlador.getInstancia().getAnormalidadeImovelSelecionado().setNovoRegistro(true);
		
		if (numeroLoteInsuficiente) {
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
			        if ( (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) && 
				         (event.getRepeatCount() == 0)) {
				            
				        return true; // Pretend we processed it
			        }
			        return false; // Any other keys are still processed as normal
			    }
			});

			
			dialog.show();
		} else {
			finish();
			Intent myIntent = new Intent(getApplicationContext(), MainTab.class);
	        startActivity(myIntent);
		}
	}
	
	public void preencheSubLote(Imovel imovelReferencia) {
        Controlador.getInstancia().setCadastroSelecionadoNovoImovel();
        
        // Verifica se existem outros sublotes neste mesmo lote.
        List<String> listaInscricao = getCadastroDataManipulator().selectSubLotesImovel(imovelReferencia.getLocalidade() + 
        																				imovelReferencia.getSetor() + 
        																				imovelReferencia.getQuadra() + 
        																				imovelReferencia.getLote());
        // Maior Sublote deste Lote
        String ultimaInscricao = listaInscricao.get(listaInscricao.size()-1);
        String ultimoSubLote = null;
        
        if (ultimaInscricao.trim().length() == 16){
        	ultimoSubLote = ultimaInscricao.substring(13, 16);
        }else{
        	ultimoSubLote = ultimaInscricao.substring(14, 17);        	
        }        
        
        int qtdImoveisNovos = getCadastroDataManipulator().getQtdImoveisNovo(); 
        
		Imovel imovel = new Imovel();
		imovel.setMatricula(""+(++qtdImoveisNovos));
		imovel.setLocalidade(imovelReferencia.getLocalidade());
		imovel.setSetor(imovelReferencia.getSetor());
		imovel.setQuadra(imovelReferencia.getQuadra());
		imovel.setLote(imovelReferencia.getLote());
		imovel.setSubLote(Util.adicionarZerosEsquerdaNumero(3, ""+(Integer.valueOf(ultimoSubLote)+1)));
		imovel.setRota(imovelReferencia.getRota());
		imovel.setCodigoLogradouro(""+imovelReferencia.getCodigoLogradouro());
		imovel.setFace(imovelReferencia.getFace());
		imovel.getEnderecoImovel().setTipoLogradouro(""+imovelReferencia.getEnderecoImovel().getTipoLogradouro());
		imovel.getEnderecoImovel().setLogradouro(imovelReferencia.getEnderecoImovel().getLogradouro());
		imovel.getEnderecoImovel().setBairro(imovelReferencia.getEnderecoImovel().getBairro());
		imovel.getEnderecoImovel().setCep(imovelReferencia.getEnderecoImovel().getCep());
		imovel.getEnderecoImovel().setMunicipio(imovelReferencia.getEnderecoImovel().getMunicipio());
		imovel.setCodigoMunicipio(""+imovelReferencia.getCodigoMunicipio());
		imovel.setCodigoLogradouro(""+imovelReferencia.getCodigoLogradouro());
		imovel.setImovelStatus(""+Constantes.IMOVEL_NOVO);
		imovel.setOperacoTipo(Constantes.OPERACAO_CADASTRO_NOVO);

		Controlador.getInstancia().setImovelSelecionado(imovel);
		
		finish();
		Intent myIntent = new Intent(getApplicationContext(), MainTab.class);
        startActivity(myIntent);
	}	
	
	/**
	 * 
	 * @param imovelReferencia
	 * @param posicaoImovel - 0: anterior, 1: posterior
	 */
	public void showDialogSelecionarFace(final Imovel imovelReferencia, final int posicaoImovel) {
		ListView lista = new ListView(MainTab.this);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainTab.this, android.R.layout.simple_list_item_1);
		
		final AlertDialog dialog = new AlertDialog.Builder(MainTab.this).create();
		dialog.setTitle("Por favor, selecione a rua ao qual o imóvel faz frente");
		dialog.setView(lista);
		dialog.show();
		
		if (posicaoImovel == IMOVEL_ANTERIOR) {
			arrayAdapter.add(Util.capitalizarString(imovelReferencia.getEnderecoImovel().getLogradouro() + ", nº " + imovelReferencia.getEnderecoImovel().getNumero() + 
					" " + imovelReferencia.getEnderecoImovel().getComplemento()));
			arrayAdapter.add(Util.capitalizarString(getImovelSelecionado().getEnderecoImovel().getLogradouro() + ", nº " + 
					 getImovelSelecionado().getEnderecoImovel().getNumero() + 
					 " " + getImovelSelecionado().getEnderecoImovel().getComplemento()));
		
		} else if (posicaoImovel == IMOVEL_POSTERIOR) {
			arrayAdapter.add(Util.capitalizarString(getImovelSelecionado().getEnderecoImovel().getLogradouro() + ", nº " + 
					getImovelSelecionado().getEnderecoImovel().getNumero() + 
					" " + getImovelSelecionado().getEnderecoImovel().getComplemento()));
			arrayAdapter.add(Util.capitalizarString(imovelReferencia.getEnderecoImovel().getLogradouro() + ", nº " + imovelReferencia.getEnderecoImovel().getNumero() + 
					" " + imovelReferencia.getEnderecoImovel().getComplemento()));
		}
		
		lista.setAdapter(arrayAdapter);
		lista.setBackgroundColor(Color.parseColor("#6e6e6e"));
		lista.setCacheColorHint(0);
		lista.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				dialog.dismiss();
				
				// Monta o novo imovel com os dados do imovel selecionado
				if ((position == 0 && posicaoImovel == IMOVEL_POSTERIOR)) {
					preencheNovoImovel(getImovelSelecionado(), montarLote(""+(Integer.parseInt(getImovelSelecionado().getLote()) + 4)));
					return;
				
				} else if (position == 1 && posicaoImovel == IMOVEL_ANTERIOR) {
					int lote = Integer.parseInt(getImovelSelecionado().getLote())/2;
					if (lote < 1) {
						numeroLoteInsuficiente = true;
					}
					preencheNovoImovel(getImovelSelecionado(), montarLote(""+lote));
					return;
				}
				
				if (posicaoImovel == IMOVEL_ANTERIOR) {
					int lote = (Integer.parseInt(imovelReferencia.getLote()) + 4);
					preencheNovoImovel(getImovelSelecionado(), montarLote(""+lote));
				
				} else if (posicaoImovel == IMOVEL_POSTERIOR) {
					int lote = Integer.parseInt(imovelReferencia.getLote())/2;
					if (lote < 1) {
						numeroLoteInsuficiente = true;
					}
					preencheNovoImovel(imovelReferencia, montarLote(""+lote));
				}
				
			}
		});

	}

	private void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
	
	private void showCompleteDialog(int iconId, String title, String message, int messageType) {
		CompleteAlertDialogFragment newFragment = CompleteAlertDialogFragment.newInstance(iconId, title, message, messageType);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

	public int getCodigoAnormalidade(){
		return anormalidadeTab.getCodigoAnormalidade();
	}

	public void doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!");
    }

	public void doNegativeClick() {
		// Do stuff here.
		Log.i("FragmentAlertDialog", "Negative click!");
	}
	
	public void doGpsDesligado() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
    }
    
	public void chamaProximoImovel() {
		Controlador.getInstancia().isCadastroAlterado();
		
		// Thread para obter dados do cadastro finalizado e transmiti-lo ao servidor.        			
		progThread = new EnviarCadastroOnlineThread(handler,this, increment);
		progThread.start();
		
		if (indiceNovoImovel != null) {
			Controlador.getInstancia().setCadastroSelecionadoByListPosition(indiceNovoImovel);
			indiceNovoImovel = null;
		} else if(Controlador.getInstancia().getCadastroListPosition() == (Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis())-1){
			Controlador.getInstancia().setCadastroSelecionadoByListPosition(0);
			
		}else{
			Controlador.getInstancia().setCadastroSelecionadoByListPosition(Controlador.getInstancia().getCadastroListPosition()+1);
		}
		finish();
		Intent myIntent = new Intent( this, MainTab.class);
		startActivity(myIntent);
    }

    // Handler on the main (UI) thread that will receive messages.
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            
        	// Get the current value of the variable total from the message data and update the progress bar.
        	int cadastroOnline = msg.getData().getInt("envioCadastroOnline" + String.valueOf(increment));

            if (progThread.getCustomizedState() == EnviarCadastroOnlineThread.DONE_OK){

            	// SETAR CADASTRO PARA TRANSMITIDO
			    increment++;
            
            }else if (progThread.getCustomizedState() == EnviarCadastroOnlineThread.DONE_ERROR){
			    increment++;
            }
         }
    };
    
	public String montarLote(String lote) {
		return Util.adicionarZerosEsquerdaNumero(4, lote);
	}
	
	public boolean isInicioLista(long id) {
		return id == 0;
	}
	
	public boolean isFimLista(long id) {
		return id == getCadastroDataManipulator().getNumeroImoveis()-1;
	}
	
	public boolean isMesmoEndereco(String e1, String e2) {
		return e1.trim().equals(e2.trim());
	}
	
	public Imovel getImovelSelecionado() {
		return Controlador.getInstancia().getImovelSelecionado();
	}
	
	public int getPosicaoImovelLista(Imovel imovel) {
		return getCadastroDataManipulator().getPosicaoImovelLista(imovel);
	}

	public static DataManipulator getCadastroDataManipulator(){
		return Controlador.getInstancia().getCadastroDataManipulator();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onTabChanged(String tabId) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		if (tabId.equals("cliente")){
	    	ft.show(clienteFragment);
	    	ft.hide(imovelFragment);
	    	ft.hide(servicosFragment);
	    	ft.hide(medidorFragment);
	    	ft.hide(anormalidadeFragment);
		
		}else if (tabId.equals("imovel")){
	    	ft.show(imovelFragment);
	    	ft.hide(clienteFragment);
	    	ft.hide(servicosFragment);
	    	ft.hide(medidorFragment);
	    	ft.hide(anormalidadeFragment);
		
		}else if (tabId.equals("servico")){
	    	ft.show(servicosFragment);
	    	ft.hide(clienteFragment);
	    	ft.hide(imovelFragment);
	    	ft.hide(medidorFragment);
	    	ft.hide(anormalidadeFragment);
		
		}else if (tabId.equals("medidor")){
	    	ft.show(medidorFragment);
	    	ft.hide(clienteFragment);
	    	ft.hide(imovelFragment);
	    	ft.hide(servicosFragment);
	    	ft.hide(anormalidadeFragment);
		
		}else if (tabId.equals("anormalidade")){
	    	ft.show(anormalidadeFragment);
	    	ft.hide(clienteFragment);
	    	ft.hide(imovelFragment);
	    	ft.hide(servicosFragment);
	    	ft.hide(medidorFragment);
		}
		
    	ft.commit();
	}

	public void onLocationChanged(Location location) {
		((TextView)findViewById(R.id.txtValorLatitude)).setText(String.valueOf(location.getLatitude()));
		((TextView)findViewById(R.id.txtValorLongitude)).setText(String.valueOf(location.getLongitude()));
	}
	
	public void onProviderDisabled(String provider) {
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
		dialogMessage = " GPS está desligado. Por favor, ligue-o para continuar o cadastro. ";
        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
	}
	
	public void onProviderEnabled(String provider) {
		Toast.makeText(this,"GPS ligado",Toast.LENGTH_SHORT).show();
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {}

}