package com.AndroidExplorer;

import java.util.List;

import model.Imovel;
import util.Constantes;
import util.Util;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import business.Controlador;
 
public class MainTab extends TabActivity {

	private static TabHost tabHost;
	public static Integer indiceNovoImovel;
	public boolean numeroLoteInsuficiente = false;
	private static final int IMOVEL_ANTERIOR = 0; 
	private static final int IMOVEL_POSTERIOR = 1; 
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maintab);
	    
	    initializeTabs();
	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event){
        
    	if ((keyCode == KeyEvent.KEYCODE_BACK)){
			finish();
            return true;

        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
	
    private void initializeTabs(){
	    
    	Resources res = getResources(); // Resource object to get Drawables
	    tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Reusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    intent = new Intent().setClass(this, ClienteTab.class);
	    spec = tabHost.newTabSpec("cliente").setIndicator("Cliente1", res.getDrawable(R.drawable.tab_cliente)).setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, ImovelTab.class);
	    spec = tabHost.newTabSpec("imovel").setIndicator("Imóvel", res.getDrawable(R.drawable.tab_imovel)).setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, ServicosTab.class);
	    spec = tabHost.newTabSpec("servico").setIndicator("Serviço", res.getDrawable(R.drawable.tab_servico)).setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, MedidorTab.class);
	    spec = tabHost.newTabSpec("medidor").setIndicator("Medidor", res.getDrawable(R.drawable.tab_medidor)).setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, AnormalidadeTab.class);
	    spec = tabHost.newTabSpec("anormalidade").setIndicator("Anormalidade", res.getDrawable(R.drawable.tab_anormalidade)).setContent(intent);
	    tabHost.addTab(spec);

	    // TODO guardar a ultima tab selecionada para restaurá-la
	    
	    setTabColor();
	    tabHost.setCurrentTab(0);
    }
        
    public static void setTabColor() {
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++){
            
        	if (Controlador.getInstancia().getCadastroDataManipulator().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_SALVO){
        		tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_custom_green);
            
            }else if (Controlador.getInstancia().getCadastroDataManipulator().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE){
            	tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_custom_red);
            }
            else if(Controlador.getInstancia().getCadastroDataManipulator().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_A_SALVAR){
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
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.proximoImovel:

	    	Controlador.getInstancia().isCadastroAlterado();
	    	
	    	if(Controlador.getInstancia().getCadastroListPosition() == (Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis())-1){
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
				Controlador.getInstancia().setCadastroSelecionadoByListPosition((int)Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis()-1);
			}else{
		    	Controlador.getInstancia().setCadastroSelecionadoByListPosition(Controlador.getInstancia().getCadastroListPosition()-1);
			}
	    	finish();
	    	
			myIntent = new Intent(getApplicationContext(), MainTab.class);
			startActivity(myIntent);
	        return true;
	    
	    case R.id.adicionarNovo:
	    	
	    	final int posicao = Controlador.getInstancia().getCadastroListPosition();
	    	
	    	List<Imovel> imoveis = Controlador.getInstancia().getCadastroDataManipulator().selectEnderecoImovel(null);
			
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
	    	
	    	if (posicao <= Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis()) {
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
	    			if (isInicioLista(posicao)) {
						
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

	
	public void preencheNovoImovel(Imovel imovelReferencia, String lote) {
        Controlador.getInstancia().setCadastroSelecionadoNovoImovel();
		
		Imovel imovel = new Imovel();
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
		imovel.setImovelStatus(""+Constantes.IMOVEL_A_SALVAR);
		
		Controlador.getInstancia().setImovelSelecionado(imovel);
		
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
	
	public String montarLote(String lote) {
		return Util.adicionarZerosEsquerdaNumero(4, lote);
	}
	
	public boolean isInicioLista(long id) {
		return id == 0;
	}
	
	public boolean isFimLista(long id) {
		return id == Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis()-1;
	}
	
	public boolean isMesmoEndereco(String e1, String e2) {
		return e1.trim().equals(e2.trim());
	}
	
	public Imovel getImovelSelecionado() {
		return Controlador.getInstancia().getImovelSelecionado();
	}
	
	public int getPosicaoImovelLista(Imovel imovel) {
		return Controlador.getInstancia().getCadastroDataManipulator().getPosicaoImovelLista(imovel);
	}

}