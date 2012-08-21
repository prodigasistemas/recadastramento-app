package com.AndroidExplorer;

import util.Constantes;
import business.Controlador;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost.TabContentFactory;
 
public class MainTab extends TabActivity {

	private static TabHost tabHost;
	
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
    
//  private LinearLayout makeTabIndicator(String text, int tabIconId){
    //
//        	int tabHeight = 60;
//    		LayoutInflater inflater = this.getLayoutInflater();
//    		LinearLayout tabView = (LinearLayout)inflater.inflate(R.layout.rowtab, null, true);
//        	
//            ((TextView)tabView.findViewById(R.id.tabText)).setText(text);
//    		((ImageView)tabView.findViewById(R.id.tabIcon)).setImageResource(tabIconId);
//        	LayoutParams lp3 = new LayoutParams(LayoutParams.WRAP_CONTENT, tabHeight, 1);
//        	lp3.setMargins(1, 0, 1, 0);
//        	tabView.setLayoutParams(lp3);
//        	tabView.setBackgroundDrawable( getResources().getDrawable(R.drawable.tab_custom_green));
//        	return tabView;
//        }

	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.menuoptions, menu);
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.proximoImovel:

	    	Controlador.getInstancia().isCadastroAlterado();
	    	
	    	if(Controlador.getInstancia().getCadastroListPosition() == (Controlador.getInstancia().getCadastroDataManipulator().getNumeroCadastros())-1){
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
				Controlador.getInstancia().setCadastroSelecionadoByListPosition((int)Controlador.getInstancia().getCadastroDataManipulator().getNumeroCadastros()-1);
			}else{
		    	Controlador.getInstancia().setCadastroSelecionadoByListPosition(Controlador.getInstancia().getCadastroListPosition()-1);
			}
	    	finish();
	    	
			myIntent = new Intent(getApplicationContext(), MainTab.class);
			startActivity(myIntent);
	        return true;
	    
	    case R.id.adicionarNovo:
			
			myIntent = new Intent(getApplicationContext(), ListaAddImovel.class);
			startActivity(myIntent);

//	    	Controlador.getInstancia().setCadastroSelecionadoByListPosition(-1);
//	    	Controlador.getInstancia().initCadastroTabs();
//	    	finish();
//	    	myIntent = new Intent(getApplicationContext(), MainTab.class);
//			startActivity(myIntent);
	        return true;
	        
	    case R.id.menuPrincipal:
			
	    	myIntent = new Intent(getApplicationContext(), MenuPrincipal.class);
			startActivity(myIntent);
	        return true;
	        
	    case R.id.listCadastros:
			
	    	myIntent = new Intent(getApplicationContext(), ListaImoveis.class);
			startActivity(myIntent);
	        return true;
	        
	    case R.id.sair:
			
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

}