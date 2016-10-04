package com.AndroidExplorer;

import java.util.ArrayList;

import util.Constantes;
import util.Util;

import business.Controlador;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Consulta extends ListActivity {
	
	MySimpleArrayAdapter enderecoList;
	ArrayList<String> listStatusImoveis;
	Spinner spinnerMetodoBusca;
	Spinner spinnerFiltro;
	String filterCondition = null;
	String searchCondition = null;
	static int metodoBusca = Constantes.METODO_BUSCA_TODOS;
	static int filtroBusca = Constantes.FILTRO_BUSCA_TODOS;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.consulta);
 
    	metodoBusca = 0;
    	filtroBusca = 0;
    	
		// Spinner Metodo Busca
        spinnerMetodoBusca = (Spinner) findViewById(R.id.spinnerMetodoBusca);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.consulta, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodoBusca.setAdapter(adapter);
        metodoBuscaOnItemSelectedListener(spinnerMetodoBusca);

		// Spinner Filtrar por.
        spinnerFiltro = (Spinner) findViewById(R.id.spinnerFiltro);
        adapter = ArrayAdapter.createFromResource(this, R.array.filtro, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);
        filtroBuscaOnItemSelectedListener(spinnerFiltro);
       
        // Button Consulta 
        final Button buttonConsulta = (Button)findViewById(R.id.buttonConsulta);
        buttonConsulta.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	loadEnderecoImoveis();
            }
        });
    }
    
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);//must store the new intent unless getIntent() will return the old one.
//		loadEnderecoImoveis();
	}

    private void loadEnderecoImoveis(){
    	enderecoList = null;
    	setListAdapter(enderecoList);

    	if (Controlador.getInstancia() != null){
    		if (Controlador.getInstancia().getCadastroDataManipulator() != null){
    			
    			filterCondition = null;
    			String filterPreCondition = null;
    			searchCondition = null;

    			String valorBusca = "\"" + ((EditText)findViewById(R.id.consulta)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", "") + "\"";
    			
    			//Verifica filtro de busca
    			if (filtroBusca == Constantes.FILTRO_BUSCA_TODOS){
    				searchCondition = "";
    			
    			}else if (filtroBusca == Constantes.FILTRO_BUSCA_VISITADOS_SUCESSO){
    				searchCondition = "(imovel_status = " + Constantes.IMOVEL_SALVO + ")";
    				
    			}else if (filtroBusca == Constantes.FILTRO_BUSCA_VISITADOS_ANORMALIDADE){
    				searchCondition = "(imovel_status = " + Constantes.IMOVEL_SALVO_COM_ANORMALIDADE + ")";
    				
    			}else if (filtroBusca == Constantes.FILTRO_BUSCA_NAO_VISITADOS){
    				searchCondition = "(imovel_status = " + Constantes.IMOVEL_A_SALVAR + ")";
    				
    			}else if (filtroBusca == Constantes.FILTRO_BUSCA_NOVOS){
    				searchCondition = "(imovel_status = " + Constantes.IMOVEL_NOVO + ")";
    				
    			}else if (filtroBusca == Constantes.FILTRO_BUSCA_TRANSMITIDOS){
    				searchCondition = "(imovel_enviado = " + Constantes.SIM + ")";
    				
    			}else if (filtroBusca == Constantes.FILTRO_BUSCA_NAO_TRANSMITIDOS){
    				searchCondition = "(imovel_enviado = " + Constantes.NAO + ")";   				
    			}
    			
    			// Verifica Método de Busca				
	    		if (metodoBusca == Constantes.METODO_BUSCA_TODOS){
    				filterCondition = searchCondition;	    				    			
	    			
	    		}else if (metodoBusca == Constantes.METODO_BUSCA_MATRICULA){
	    			
	    			if (searchCondition.length() > 0){
	        			
	    				filterCondition = searchCondition + " AND ";	    			
	    				filterCondition += "(matricula = " + valorBusca + ")";

	    			}else{
	    				filterCondition = "(matricula = " + valorBusca + ")";	    				
	    			}	    			

    			}else if (metodoBusca == Constantes.METODO_BUSCA_CPF){

	    			filterPreCondition = "((cpf_cnpj_usuario = " + valorBusca + " AND tipo_pessoa_usuario = " + Constantes.TIPO_PESSOA_FISICA + ")";
	        	    filterPreCondition += " OR (cpf_cnpj_proprietario = " + valorBusca + " AND tipo_pessoa_proprietario = " + Constantes.TIPO_PESSOA_FISICA + ")";
	        	    filterPreCondition += " OR (cpf_cnpj_responsavel = " + valorBusca + " AND tipo_pessoa_responsavel = " + Constantes.TIPO_PESSOA_FISICA + "))";
        	    		
        	    	ArrayList<String> idList = (ArrayList)Controlador.getInstancia().getCadastroDataManipulator().selectIdClientes(filterPreCondition);
        	    		 
	    			if (searchCondition.length() > 0){
	        			filterCondition = searchCondition + " AND ";

	        	    	if (idList != null && idList.size() > 0){
            	    		
	        	    		filterCondition += " (id = " + idList.get(0);
	            	    		
	            	    	for (int i = 1; i < idList.size(); i++){
	            	    		filterCondition += " OR id = " + idList.get(i);
	            	    	}
	            	    	
	            	    	filterCondition += ")";
	        	    	}    	    				

	    			}else{
	        	    	if (idList != null && idList.size() > 0){
            	    		
	        	    		filterCondition = " (id = " + idList.get(0);
	            	    		
	            	    	for (int i = 1; i < idList.size(); i++){
	            	    		filterCondition += " OR id = " + idList.get(i);
	            	    	}
	            	    	
	            	    	filterCondition += ")";
	        	    	}    	    				
	    			}

    			}else if (metodoBusca == Constantes.METODO_BUSCA_CNPJ){
        	    	
	    			filterPreCondition = "((cpf_cnpj_usuario = " + valorBusca + " AND tipo_pessoa_usuario = " + Constantes.TIPO_PESSOA_JURIDICA + ")";
	        	    filterPreCondition += " OR (cpf_cnpj_proprietario = " + valorBusca + " AND tipo_pessoa_proprietario = " + Constantes.TIPO_PESSOA_JURIDICA + ")";
	        	    filterPreCondition += " OR (cpf_cnpj_responsavel = " + valorBusca + " AND tipo_pessoa_responsavel = " + Constantes.TIPO_PESSOA_JURIDICA + "))";

        	    	ArrayList<String> idList = (ArrayList)Controlador.getInstancia().getCadastroDataManipulator().selectIdClientes(filterPreCondition);
        	    		
	    			if (searchCondition.length() > 0){
	        			filterCondition = searchCondition + " AND ";

	        	    	if (idList != null && idList.size() > 0){
            	    		
	        	    		filterCondition += " (id = " + idList.get(0);
	            	    		
	            	    	for (int i = 1; i < idList.size(); i++){
	            	    		filterCondition += " OR id = " + idList.get(i);
	            	    	}
	            	    	
	            	    	filterCondition += ")";
	        	    	}    	    				

	    			}else{
	        	    	if (idList != null && idList.size() > 0){
            	    		
	        	    		filterCondition = " (id = " + idList.get(0);
	            	    		
	            	    	for (int i = 1; i < idList.size(); i++){
	            	    		filterCondition += " OR id = " + idList.get(i);
	            	    	}
	            	    	
	            	    	filterCondition += ")";
	        	    	}    	    				
	    			}
    			}else if(metodoBusca == Constantes.METODO_BUSCA_NUMERO_RESIDENCIA){
    				String complemento = "(numero_imovel = \"" + Util.adicionarCharDireita(5, valorBusca.replaceAll("\"", ""), ' ') + "\")" ;
    				
    				if (searchCondition.length() > 0){
	    				filterCondition = searchCondition + " AND ";	    			
	    				filterCondition += complemento;
	    			}else{
	    				filterCondition = complemento;		
	    			}	
    			}
    	    	
    			// Aplica condicoes de filtro
    	    	listStatusImoveis = (ArrayList)Controlador.getInstancia().getCadastroDataManipulator().selectStatusImoveis(filterCondition);
    	    	ArrayList<String> listEnderecoImoveis = (ArrayList)Controlador.getInstancia().getCadastroDataManipulator().selectEnderecoImoveis(filterCondition);

    	    	if(listEnderecoImoveis != null && listEnderecoImoveis.size() > 0){
    	        	enderecoList = new MySimpleArrayAdapter(this, listEnderecoImoveis);
    	        	setListAdapter(enderecoList);
    	    	}
    		}
    	}
    }
    
	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		// user clicked a list item, make it "selected"
		enderecoList.setSelectedPosition(position);

		Controlador.getInstancia().setCadastroSelecionadoByListPositionInConsulta(position, filterCondition);
		Intent myIntent = new Intent(getApplicationContext(), MainTab.class);
		startActivityForResult(myIntent, 0);
	}
	
	
	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final ArrayList<String> names;

		// used to keep selected position in ListView
		private int selectedPos = -1;

		public MySimpleArrayAdapter(Activity context, ArrayList<String> names) {
			super(context, R.layout.rowimovel, names);
			this.context = context;
			this.names = names;
		}

		public void setSelectedPosition(int pos){
			selectedPos = pos;
			// inform the view of this change
			notifyDataSetChanged();
		}

		public int getSelectedPosition(){
			return selectedPos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.rowimovel, null, true);

	        // change the row color based on selected state
	        if(selectedPos == position){
	        	rowView.setBackgroundColor(Color.argb(70, 255, 255, 255));
	        }else{
	        	rowView.setBackgroundColor(Color.TRANSPARENT);
	        }
	        
	        ((TextView)rowView.findViewById(R.id.nomerota)).setText(names.get(position));

			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
			
			if ( Integer.parseInt(listStatusImoveis.get(position)) == Constantes.IMOVEL_A_SALVAR ){
				imageView.setImageResource(R.drawable.todo);
			
			} else if ( Integer.parseInt(listStatusImoveis.get(position)) == Constantes.IMOVEL_SALVO){
				imageView.setImageResource(R.drawable.done);
			
			} else if ( Integer.parseInt(listStatusImoveis.get(position)) == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE ){
				imageView.setImageResource(R.drawable.done_anormal);
			}

			return rowView;
		}
		
		public String getListElementName(int element){
			return names.get(element);
		}
	}
	
	public void metodoBuscaOnItemSelectedListener (Spinner spinnerMetodoBusca){

		spinnerMetodoBusca.setOnItemSelectedListener(new OnItemSelectedListener () {
        	
    		public void onItemSelected(AdapterView parent, View v, int position, long id){
        		metodoBusca = position;
        		
        		if (metodoBusca == Constantes.METODO_BUSCA_CPF || metodoBusca == Constantes.METODO_BUSCA_CNPJ){
        			Util.addTextChangedListenerConsultaVerifierAndMask((EditText)findViewById(R.id.consulta), metodoBusca);
        		}
        	}
    		
    		public void onNothingSelected(AdapterView<?> arg0) {}
    	});
	}	
	
	public void filtroBuscaOnItemSelectedListener (Spinner spinnerFiltro){

		spinnerFiltro.setOnItemSelectedListener(new OnItemSelectedListener () {
        	
    		public void onItemSelected(AdapterView parent, View v, int position, long id){
        		filtroBusca = position;
        	}
    		
    		public void onNothingSelected(AdapterView<?> arg0) {}
    	});
	}	
	
}