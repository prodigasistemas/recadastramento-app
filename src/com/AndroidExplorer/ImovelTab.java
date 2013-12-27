package com.AndroidExplorer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.Imovel;
import business.Controlador;
import util.Constantes;
import util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
 
public class ImovelTab extends Activity implements LocationListener {

	static boolean isUpdatingRamoAtividade;
	private CheckBox cbResidencial;;
	private CheckBox cbComercial;
	private CheckBox cbPublica;
	private CheckBox cbIndustrial;
	private MySimpleArrayAdapter ramoAtividadeList;
	private ArrayList<String> ramosAtividadeImovel;
	private List<String> listRamosAtividade;
	private List<String> listFonteAbastecimento;
	private List<String> listTiposLogradouroImovel;
	private String dialogMessage = null;
	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;

	private boolean categResidencialOk = false;
	private boolean categComercialOk = false;
	private boolean categPublicaOk = false;
	private boolean categIndustrialOk = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
        setContentView(R.layout.imoveltab);
        instanciate();
 	}
	
	protected void onNewIntent(Intent intent) {
		  super.onNewIntent(intent);
		  setIntent(intent);//must store the new intent unless getIntent() will return the old one.
		  instanciate();
		}

	public void instanciate(){
	
        /* Use the LocationManager class to obtain GPS locations */
        mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        boolean enabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabled){
	        dialogMessage = " GPS está desligado. Por favor, ligue-o para continuar o cadastro. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
        }
        
		Criteria criteria = new Criteria();
		provider = mLocManager.getBestProvider(criteria, false);
		Location location = mLocManager.getLastKnownLocation(provider);

        lastKnownLocation = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	CellLocation.requestLocationUpdate();

    	cbResidencial = (CheckBox) findViewById(R.id.checkBoxResidencial);;
    	cbComercial = (CheckBox) findViewById(R.id.checkBoxComercial);
    	cbPublica = (CheckBox) findViewById(R.id.checkBoxPublica);
    	cbIndustrial = (CheckBox) findViewById(R.id.checkBoxIndustrial);
    	ramosAtividadeImovel = new ArrayList<String>();
        
    	Util.addTextChangedListenerCepMask((EditText)findViewById(R.id.cepImovel));
    	
    	// Spinner Tipo Logradouro
        Spinner spinnerTipoLogradouro = (Spinner) findViewById(R.id.spinnerTipoLogradouroImovel);
        listTiposLogradouroImovel = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_TIPO_LOGRADOURO);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listTiposLogradouroImovel);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoLogradouro.setAdapter(arrayAdapter);
		
        // populate Tipo Logradouro
		String descricaoTipoLogradouro = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_TIPO_LOGRADOURO, String.valueOf(getImovel().getEnderecoImovel().getTipoLogradouro()));
		if (descricaoTipoLogradouro != null){
			for (int i = 0; i < listTiposLogradouroImovel.size(); i++){
	        	if (listTiposLogradouroImovel.get(i).equalsIgnoreCase(descricaoTipoLogradouro)){
	        		spinnerTipoLogradouro.setSelection(i);
	        		break;
	        	}
	        }
		}

    	//CheckBox Categoria Residencial
    	enableEconominasResidencial(false);
    	cbResidencial.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    	public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
		    	if (buttonView.isChecked()) {
		        	enableEconominasResidencial(true);
		        	if (Util.allowPopulateDados()){
		        		populateSubCategoriasResidenciais();
		        	}
		    	}else {
		        	enableEconominasResidencial(false);
		    	}
	    	}
    	}); 

    	//CheckBox Categoria Comercial
       	enableEconominasComercial(false);
    	cbComercial.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    	public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
		    	if (buttonView.isChecked()) {
		        	enableEconominasComercial(true);
		        	if (Util.allowPopulateDados()){
		        		populateSubCategoriasComerciais();
		        	}
		    	}else {
		        	enableEconominasComercial(false);
		    	}
	    	}
    	}); 

    	//CheckBox Categoria Publica
       	enableEconominasPublica(false);
    	cbPublica.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    	public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
		    	if (buttonView.isChecked()) {
		        	enableEconominasPublica(true);
		        	if (Util.allowPopulateDados()){
		        		populateSubCategoriasPublicas();
		        	}
		    	}else {
		        	enableEconominasPublica(false);
		    	}
	    	}
    	}); 

    	//CheckBox Categoria Industrial
       	enableEconominasIndustrial(false);
    	cbIndustrial.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    	public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
		    	if (buttonView.isChecked()) {
		        	enableEconominasIndustrial(true);
		        	if (Util.allowPopulateDados()){
		        		populateSubCategoriasIndustriais();
		        	}
		    	}else {
		        	enableEconominasIndustrial(false);
		    	}
	    	}
    	}); 

		// Descrição do Ramo de Atividade
        Spinner spinnerDescricaoRamoAtividade = (Spinner) findViewById(R.id.spinnerDescricaoRamoAtividade);
        
        listRamosAtividade = new ArrayList<String>();
        listRamosAtividade = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_RAMO_ATIVIDADE);
        listRamosAtividade.add(0, "");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listRamosAtividade);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDescricaoRamoAtividade.setAdapter(adapter);
        spinnerDescricaoRamoAtividade.setOnItemSelectedListener(new OnItemSelectedListener () {

        	
			public void onItemSelected(AdapterView parent, View v, int position, long id){
 				String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_RAMO_ATIVIDADE, ((Spinner)findViewById(R.id.spinnerDescricaoRamoAtividade)).getSelectedItem().toString());
 				
 				if (codigo.compareTo(((EditText)findViewById(R.id.codigoRamoAtividade)).getText().toString()) != 0 &&
 					((Spinner)(findViewById(R.id.spinnerDescricaoRamoAtividade))).getSelectedItemPosition() != 0){
 	 				
 					isUpdatingRamoAtividade = true;  
 					((EditText)findViewById(R.id.codigoRamoAtividade)).setText(codigo);
 					
	        	}else if (((Spinner)(findViewById(R.id.spinnerDescricaoRamoAtividade))).getSelectedItemPosition() == 0){
	        		((EditText)findViewById(R.id.codigoRamoAtividade)).setText("");
	        	}
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		// Codigo do Ramo de Atividade
        EditText codigoRamoAtividade = (EditText)findViewById(R.id.codigoRamoAtividade);
        codigoRamoAtividade.addTextChangedListener(new TextWatcher() {

    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}  
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	      
    			// Quando o texto é alterado o onTextChange é chamado. Essa flag evita a chamada infinita desse método  
    			if (isUpdatingRamoAtividade){
    				isUpdatingRamoAtividade = false;  
    				return;  
    			}  
    	      
 				String descricaoRamoAtividade = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_RAMO_ATIVIDADE, s.toString());
 				if (descricaoRamoAtividade != null){
 					for (int i = 0; i < listRamosAtividade.size(); i++){
 			        	if (listRamosAtividade.get(i).equalsIgnoreCase(descricaoRamoAtividade)){
 			                ((Spinner)(findViewById(R.id.spinnerDescricaoRamoAtividade))).setSelection(i);
 			        		break;
 			        	}else{
 			                ((Spinner)(findViewById(R.id.spinnerDescricaoRamoAtividade))).setSelection(0);
 			        	}
 			        }
 				}
    		}  
    		
    	    public void afterTextChanged(Editable s) {}  
		});

        // Fonte de Abastecimento
        Spinner spinnerFonteAbastecimento = (Spinner) findViewById(R.id.spinnerFonteAbastecimento);

        listFonteAbastecimento = new ArrayList<String>();
        listFonteAbastecimento = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_FONTE_ABASTECIMENTO);
        listFonteAbastecimento.add(0, "");

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listFonteAbastecimento);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFonteAbastecimento.setAdapter(adapter);
		
        // populate Tipo Fonte de Abastecimento
		String descricaoFonteAbastecimento = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_FONTE_ABASTECIMENTO, String.valueOf(getImovel().getTipoFonteAbastecimento()));
		if (descricaoFonteAbastecimento != null){
			for (int i = 0; i < listFonteAbastecimento.size(); i++){
	        	if (listFonteAbastecimento.get(i).equalsIgnoreCase(descricaoFonteAbastecimento)){
	        		spinnerFonteAbastecimento.setSelection(i);
	        		break;
	        	}else{
	        		spinnerFonteAbastecimento.setSelection(0);	        		
	        	}
	        }
		}
		
    	populateImovel();
    	if (Util.allowPopulateDados()){
    		populateCategorias();
    	}   
    	
        // Button Add Ramo Atividade 
        final Button buttonAddRamoAtividade = (Button)findViewById(R.id.buttonAddRamoAtividade);
        buttonAddRamoAtividade.setOnClickListener(new OnClickListener() {

        	public void onClick(View v) {
        		boolean isRamoAtividadeJaAdicionado = false;
        		
        		// Verifica se Ramo de Atividade já foi adicionado neste Imovel.
        		if (ramosAtividadeImovel != null){
	        		for (int i=0; i< ramosAtividadeImovel.size(); i++){
	        			if ((ramosAtividadeImovel.get(i)).equalsIgnoreCase(((EditText)(findViewById(R.id.codigoRamoAtividade))).getText().toString())){
	        				
	        				isRamoAtividadeJaAdicionado = true;
	        			}
	        		}
        		}
        		
        		if (!isRamoAtividadeJaAdicionado){
	        	    
        			if (((EditText)(findViewById(R.id.codigoRamoAtividade))).getText().toString().length() > 0){

//                		if (isRamoAtividadeOk()){

	        				ramosAtividadeImovel.add(((EditText)(findViewById(R.id.codigoRamoAtividade))).getText().toString());
	     	        	    ListView listRamosAtividade = (ListView)findViewById(R.id.listRamosAtividade);
	    	        	    ramoAtividadeList = new MySimpleArrayAdapter(getBaseContext());
	    	        	    listRamosAtividade.setAdapter(ramoAtividadeList);
	    	       	    
	    	        	    // Hide txtEmpty se lista de ramos de atividade for maior que ZERO.
	    	        	    if (ramosAtividadeImovel.size() > 0){
	    		        		((TextView)findViewById(R.id.txtEmpty)).setVisibility(TextView.GONE);
	    	        	    }else{
	    		        		((TextView)findViewById(R.id.txtEmpty)).setVisibility(TextView.VISIBLE);	        	    	
	    	        	    }
	
	    	        	    ViewGroup.LayoutParams params = listRamosAtividade.getLayoutParams();
	    	        	    params.height = (int) (33*(ramosAtividadeImovel.size())*(getResources().getDisplayMetrics().density));
	    	                listRamosAtividade.setLayoutParams(params);
	    	                listRamosAtividade.requestLayout();
//                		}
        			}else{
            			dialogMessage = " Ramo de Atividade inválido! ";
            	    	showDialog(Constantes.DIALOG_ID_ERRO);
        			}
        		}else{
        			dialogMessage = " Ramo de Atividade já existente! ";
        	    	showDialog(Constantes.DIALOG_ID_ERRO);
        		}
            }
        });

        // Button Save 
        final Button buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
    			if ( !Util.allowPopulateDados()){
    					
	            	if (!checkChangesSubCategoriasResidenciais()){
	            		categResidencialOk = true;
	            	}
	
	            	if (!checkChangesSubCategoriasComerciais()){
	            		categComercialOk = true;
	            	}
	
	            	if (!checkChangesSubCategoriasPublicas()){
	            		categPublicaOk = true;
	            	}
	
	            	if (!checkChangesSubCategoriasIndustriais()){
	            		categIndustrialOk = true;
	            	}
	            	
	            	if (!categResidencialOk || !categComercialOk || !categPublicaOk || !categIndustrialOk){
	        	    	showDialog(Constantes.DIALOG_ID_CONFIRM_CHANGES);	            		
	            	}
	            		            	
    			}else{
            		categResidencialOk = true;
            		categComercialOk = true;
            		categPublicaOk = true;
            		categIndustrialOk = true;
    			}

    			if (categResidencialOk && 
    				categComercialOk && 
    				categPublicaOk && 
    				categIndustrialOk ){
            	
	            	updateImovelSelecionado();
	            	
	             	// Verifica os campos obrigatórios
	            	if (areCamposObrigatoriosOk()){
	            		
	                	// Ramo de atividade somente para imóvel com economia Comercial, publica ou industrial!!!
	            		if (isRamoAtividadeOk()){
	            			
	                    	// Se tipo de categoria estiver selecionado deve possuir pelo menos uma economia.
	                		if (isDadosCategoriaOk()){
	
	                			getImovel().setTabSaved(true);
		            			Toast.makeText(ImovelTab.this, "Dados do Imóvel atualizados com sucesso.", 5).show();
	                		}
	            		}
	            	}
            	}
            }
        });
	}
	
	public boolean isRamoAtividadeOk(){
		boolean result = true;

		if ( !cbComercial.isChecked() && !cbPublica.isChecked() && !cbIndustrial.isChecked() && ramosAtividadeImovel.size() > 0){
			dialogMessage = " Ramo de Atividade só deve existir para economia Comercial, Pública ou Industrial. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			result = false;

		}else if ( (cbComercial.isChecked() || cbPublica.isChecked() || cbIndustrial.isChecked()) && ramosAtividadeImovel.size() == 0){
			dialogMessage = " É necessário informar o Ramo de Atividade para economia Comercial, Pública ou Industrial. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			result = false;
		}
		return result;
	}

	public boolean isDadosCategoriaOk(){
		boolean result = true;

		if ( cbComercial.isChecked() &&
				((((EditText)findViewById(R.id.economiasC1)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)findViewById(R.id.economiasC2)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)findViewById(R.id.economiasC3)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)findViewById(R.id.economiasC4)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias comerciais inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
	    	result = false;
		}

		if ( cbIndustrial.isChecked() &&
				((((EditText)findViewById(R.id.economiasI1)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)findViewById(R.id.economiasI2)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)findViewById(R.id.economiasI3)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)findViewById(R.id.economiasI4)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias industriais inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
	    	result = false;
		}

		if ( cbPublica.isChecked() &&
				((((EditText)findViewById(R.id.economiasP1)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)findViewById(R.id.economiasP2)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)findViewById(R.id.economiasP3)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)findViewById(R.id.economiasP4)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias públicas inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
	    	result = false;
		}

		return result;
	}
	
	public boolean areCamposObrigatoriosOk(){
		boolean result = true;
		
		//localidade
		if ( (((EditText)findViewById(R.id.localidade)).getText().toString().trim().length() == 0) ||
			 ( ((EditText)findViewById(R.id.localidade)).getText().toString().trim().length() >0 &&
				(Integer.parseInt(((EditText)findViewById(R.id.localidade)).getText().toString().trim()) == 0 ) ) ){
		    
			dialogMessage = " Localidade inválida. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}

		//setor
		if ( (((EditText)findViewById(R.id.setor)).getText().toString().trim().length() == 0) ||
				 ( ((EditText)findViewById(R.id.setor)).getText().toString().trim().length() >0 &&
					(Integer.parseInt(((EditText)findViewById(R.id.setor)).getText().toString().trim()) == 0 ) ) ){
		    
			dialogMessage = " Setor inválido ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// rota
		if ( (((EditText)findViewById(R.id.rota)).getText().toString().trim().length() == 0) ||
				 ( ((EditText)findViewById(R.id.rota)).getText().toString().trim().length() >0 &&
					(Integer.parseInt(((EditText)findViewById(R.id.rota)).getText().toString().trim()) == 0 ) ) ){
		    
			dialogMessage = " Rota inválida. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// logradouro
		if (((EditText)findViewById(R.id.logradouro)).getText().toString().trim().compareTo("") == 0){
		    
			dialogMessage = " Logradouro inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// Bairro
		if (((EditText)findViewById(R.id.bairro)).getText().toString().trim().compareTo("") == 0){
		    
			dialogMessage = " Bairro inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// Municipio
		if (((EditText)findViewById(R.id.municipio)).getText().toString().trim().compareTo("") == 0){
		    
			dialogMessage = " Município inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// Verificar Categorias - pelo menos 1 categoria
		if (!cbResidencial.isChecked() && !cbComercial.isChecked() && !cbIndustrial.isChecked() && !cbPublica.isChecked()){
		    
			dialogMessage = " Imóvel deve possuir pelo menos 1 economia. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// Verificar Econominas. pelo menos 1 economia
		if ( cbResidencial.isChecked() &&
				((((EditText)findViewById(R.id.economiasR1)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasR1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasR1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)findViewById(R.id.economiasR2)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasR2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasR2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)findViewById(R.id.economiasR3)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasR3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasR3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)findViewById(R.id.economiasR4)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasR4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasR4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias residenciais inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}

		if ( cbComercial.isChecked() &&
				((((EditText)findViewById(R.id.economiasC1)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)findViewById(R.id.economiasC2)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)findViewById(R.id.economiasC3)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)findViewById(R.id.economiasC4)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasC4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasC4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias comerciais inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}

		if ( cbIndustrial.isChecked() &&
				((((EditText)findViewById(R.id.economiasI1)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)findViewById(R.id.economiasI2)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)findViewById(R.id.economiasI3)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)findViewById(R.id.economiasI4)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasI4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasI4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias industriais inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}

		if ( cbPublica.isChecked() &&
				((((EditText)findViewById(R.id.economiasP1)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)findViewById(R.id.economiasP2)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)findViewById(R.id.economiasP3)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)findViewById(R.id.economiasP4)).getText().toString().trim().length() == 0) ||
				( ((EditText)findViewById(R.id.economiasP4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)findViewById(R.id.economiasP4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias públicas inválido. ";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}

		return result;
	}
	
	public void updateImovelSelecionado(){
		
		if (getImovel().getImovelStatus() == Constantes.IMOVEL_NOVO) {
			ArrayList<Integer> listStatus = (ArrayList<Integer>) Controlador.getInstancia().getCadastroDataManipulator().selectNumeroTodosStatusImoveis();
	        int qtdImoveisNovos = listStatus.get(Constantes.IMOVEL_NOVO);
			getImovel().setMatricula(""+(qtdImoveisNovos == 0 ? 1 : qtdImoveisNovos+1));
			
		} else {
			getImovel().setMatricula(((EditText)findViewById(R.id.matricula)).getText().toString());
		}
		
		getImovel().setCodigoCliente(((EditText)findViewById(R.id.codCliente)).getText().toString());
		getImovel().setLocalidade(Util.adicionarZerosEsquerdaNumero(3, ((EditText)findViewById(R.id.localidade)).getText().toString()));
		getImovel().setSetor(Util.adicionarZerosEsquerdaNumero(3, ((EditText)findViewById(R.id.setor)).getText().toString()));
		getImovel().setQuadra(Util.adicionarZerosEsquerdaNumero(4, ((EditText)findViewById(R.id.quadra)).getText().toString()));
		getImovel().setLote(Util.adicionarZerosEsquerdaNumero(4, ((EditText)findViewById(R.id.lote)).getText().toString()));
		getImovel().setSubLote(Util.adicionarZerosEsquerdaNumero(3, ((EditText)findViewById(R.id.subLote)).getText().toString()));
		getImovel().getInscricao();
		
		getImovel().setRota(Util.adicionarZerosEsquerdaNumero(2, ((EditText)findViewById(R.id.rota)).getText().toString()));
		getImovel().setFace(Util.adicionarZerosEsquerdaNumero(2, ((EditText)findViewById(R.id.face)).getText().toString()));
		getImovel().setCodigoMunicipio(((EditText)findViewById(R.id.codMunicipio)).getText().toString());
		getImovel().setListaRamoAtividade(ramosAtividadeImovel);
		getImovel().setIptu(((EditText)findViewById(R.id.iptu)).getText().toString());
		getImovel().setNumeroCelpa(((EditText)findViewById(R.id.numeroCelpa)).getText().toString());
		getImovel().setNumeroPontosUteis(((EditText)findViewById(R.id.numeroPontosUteis)).getText().toString());
		getImovel().setNumeroOcupantes(((EditText)findViewById(R.id.numeroOcupantes)).getText().toString());
		
		
		String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_TIPO_LOGRADOURO, ((Spinner)findViewById(R.id.spinnerTipoLogradouroImovel)).getSelectedItem().toString());
    	getImovel().getEnderecoImovel().setTipoLogradouro(codigo);
		getImovel().getEnderecoImovel().setLogradouro(((EditText)findViewById(R.id.logradouro)).getText().toString());
		getImovel().getEnderecoImovel().setNumero(((EditText)findViewById(R.id.numero)).getText().toString());
		getImovel().getEnderecoImovel().setComplemento(((EditText)findViewById(R.id.complemento)).getText().toString());
		getImovel().getEnderecoImovel().setBairro(((EditText)findViewById(R.id.bairro)).getText().toString());
		getImovel().getEnderecoImovel().setCep(((EditText)findViewById(R.id.cepImovel)).getText().toString());
		getImovel().getEnderecoImovel().setMunicipio(((EditText)findViewById(R.id.municipio)).getText().toString());
		
		getImovel().setCodigoLogradouro(((EditText)findViewById(R.id.codLogradouro)).getText().toString());

		if (cbResidencial.isChecked()){
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria1(((EditText)findViewById(R.id.economiasR1)).getText().toString());
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria2(((EditText)findViewById(R.id.economiasR2)).getText().toString());
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria3(((EditText)findViewById(R.id.economiasR3)).getText().toString());
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria4(((EditText)findViewById(R.id.economiasR4)).getText().toString());
    	}else{
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}
		
    	if (cbComercial.isChecked()){
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria1(((EditText)findViewById(R.id.economiasC1)).getText().toString());
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria2(((EditText)findViewById(R.id.economiasC2)).getText().toString());
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria3(((EditText)findViewById(R.id.economiasC3)).getText().toString());
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria4(((EditText)findViewById(R.id.economiasC4)).getText().toString());
    	}else{
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}
		
    	if (cbPublica.isChecked()){
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria1(((EditText)findViewById(R.id.economiasP1)).getText().toString());
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria2(((EditText)findViewById(R.id.economiasP2)).getText().toString());
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria3(((EditText)findViewById(R.id.economiasP3)).getText().toString());
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria4(((EditText)findViewById(R.id.economiasP4)).getText().toString());
    	}else{
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}
		
    	if (cbIndustrial.isChecked()){
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria1(((EditText)findViewById(R.id.economiasI1)).getText().toString());
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria2(((EditText)findViewById(R.id.economiasI2)).getText().toString());
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria3(((EditText)findViewById(R.id.economiasI3)).getText().toString());
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria4(((EditText)findViewById(R.id.economiasI4)).getText().toString());
    	}else{
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}

		codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_FONTE_ABASTECIMENTO, ((Spinner)findViewById(R.id.spinnerFonteAbastecimento)).getSelectedItem().toString());
    	getImovel().setTipoFonteAbastecimento(codigo);

        if (lastKnownLocation != null) {
	    	getImovel().setLatitude(String.valueOf(lastKnownLocation.getLatitude()));
	    	getImovel().setLongitude(String.valueOf(lastKnownLocation.getLongitude()));
        }
    	getImovel().setData(Util.formatarData(Calendar.getInstance().getTime()));

	}
	
	public void enableEconominasResidencial(boolean enable){
    	((EditText)(findViewById(R.id.economiasR1))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasR2))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasR3))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasR4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(findViewById(R.id.economiasR1))).setText("");
        	((EditText)(findViewById(R.id.economiasR2))).setText("");
        	((EditText)(findViewById(R.id.economiasR3))).setText("");
        	((EditText)(findViewById(R.id.economiasR4))).setText("");

    	}
	}
	
	public void enableEconominasComercial(boolean enable){
    	((EditText)(findViewById(R.id.economiasC1))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasC2))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasC3))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasC4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(findViewById(R.id.economiasC1))).setText("");
        	((EditText)(findViewById(R.id.economiasC2))).setText("");
        	((EditText)(findViewById(R.id.economiasC3))).setText("");
        	((EditText)(findViewById(R.id.economiasC4))).setText("");

    	}
	}

	public void enableEconominasPublica(boolean enable){
    	((EditText)(findViewById(R.id.economiasP1))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasP2))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasP3))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasP4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(findViewById(R.id.economiasP1))).setText("");
        	((EditText)(findViewById(R.id.economiasP2))).setText("");
        	((EditText)(findViewById(R.id.economiasP3))).setText("");
        	((EditText)(findViewById(R.id.economiasP4))).setText("");

    	}
	}

	public void enableEconominasIndustrial(boolean enable){
    	((EditText)(findViewById(R.id.economiasI1))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasI2))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasI3))).setEnabled(enable);
    	((EditText)(findViewById(R.id.economiasI4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(findViewById(R.id.economiasI1))).setText("");
        	((EditText)(findViewById(R.id.economiasI2))).setText("");
        	((EditText)(findViewById(R.id.economiasI3))).setText("");
        	((EditText)(findViewById(R.id.economiasI4))).setText("");

    	}
	}
	
	public void populateImovel(){
		
        // Código do Cliente
        if (getImovel().getCodigoCliente() != Constantes.NULO_INT){
        	((EditText)findViewById(R.id.codCliente)).setText(String.valueOf(getImovel().getCodigoCliente()));
        }
        
		// Matricula
        if (getImovel().getMatricula() != Constantes.NULO_INT){
        	((EditText)findViewById(R.id.matricula)).setText(String.valueOf(getImovel().getMatricula()));
        }
        
		// Localidade
        if (getImovel().getLocalidade() != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.localidade)).setText(String.valueOf(getImovel().getLocalidade()));
        }
        
		// Setor
        if (getImovel().getSetor() != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.setor)).setText(String.valueOf(getImovel().getSetor()));
        }
        
		// Quadra
        if (getImovel().getQuadra() != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.quadra)).setText(String.valueOf(getImovel().getQuadra()));
        }
       
		// Lote
        if (getImovel().getLote() != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.lote)).setText(String.valueOf(getImovel().getLote()));
        }
        
		// Sub Lote
        if (getImovel().getSubLote() != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.subLote)).setText(String.valueOf(getImovel().getSubLote()));
        }
		
		// Rota
        if (getImovel().getRota() != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.rota)).setText(String.valueOf(getImovel().getRota()));
        }
		
		// Face
        if (getImovel().getFace() != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.face)).setText(String.valueOf(getImovel().getFace()));
        }
		
		// Logradouro
        if ( String.valueOf(getImovel().getEnderecoImovel().getLogradouro()) != Constantes.NULO_STRING){
        	((EditText)findViewById(R.id.logradouro)).setText(String.valueOf(getImovel().getEnderecoImovel().getLogradouro()));
        }
        
		// Numero
		((EditText)findViewById(R.id.numero)).setText(getImovel().getEnderecoImovel().getNumero());
		
		// Codigo do Logradouro
        if (getImovel().getCodigoLogradouro() != Constantes.NULO_INT){
        	((EditText)findViewById(R.id.codLogradouro)).setText(String.valueOf(getImovel().getCodigoLogradouro()));
        }
        
		// Complemento
		((EditText)findViewById(R.id.complemento)).setText(getImovel().getEnderecoImovel().getComplemento());
		
		// Bairro
		((EditText)findViewById(R.id.bairro)).setText(getImovel().getEnderecoImovel().getBairro());
		
		// Cep
		((EditText)findViewById(R.id.cepImovel)).setText(String.valueOf(getImovel().getEnderecoImovel().getCep()));
		
		// Municipio
		((EditText)findViewById(R.id.municipio)).setText(getImovel().getEnderecoImovel().getMunicipio());
		
		// Código do Municipio
		((EditText)findViewById(R.id.codMunicipio)).setText(String.valueOf(getImovel().getCodigoMunicipio()));
		
		// Codigo do Ramo de Atividade
        // popula lista dos Ramos de Atividade do Imovel
        for (int i =0; i < getImovel().getListaRamoAtividade().size(); i++){
    	    ramosAtividadeImovel.add(getImovel().getListaRamoAtividade().get(i));
		}
        
 	    ListView listRamosAtividade = (ListView)findViewById(R.id.listRamosAtividade);
 	    ramoAtividadeList = new MySimpleArrayAdapter(getBaseContext());
	    listRamosAtividade.setAdapter(ramoAtividadeList);
	    
	    // Hide txtEmpty se lista de ramos de atividade for maior que ZERO.
	    if (ramosAtividadeImovel.size() > 0){
    		((TextView)findViewById(R.id.txtEmpty)).setVisibility(TextView.GONE);
	    }else{
    		((TextView)findViewById(R.id.txtEmpty)).setVisibility(TextView.VISIBLE);	        	    	
	    }

	    ViewGroup.LayoutParams params = listRamosAtividade.getLayoutParams();
	    params.height = (int) (33*(ramosAtividadeImovel.size())*(getResources().getDisplayMetrics().density));
        listRamosAtividade.setLayoutParams(params);
        listRamosAtividade.requestLayout();
        
		// IPTU
		((EditText)(findViewById(R.id.iptu))).setText(getImovel().getIptu());
		
		// Numero UC CELPA
        if (getImovel().getNumeroCelpa() != Constantes.NULO_STRING){
        	((EditText)(findViewById(R.id.numeroCelpa))).setText(String.valueOf(getImovel().getNumeroCelpa()));
        }		

        // Numero Pontos Úteis
        if (getImovel().getNumeroPontosUteis() != Constantes.NULO_INT){
        	((EditText)(findViewById(R.id.numeroPontosUteis))).setText(String.valueOf(getImovel().getNumeroPontosUteis()));
        }		
        
        // Numero Ocupantes
        if (getImovel().getNumeroOcupantes() != Constantes.NULO_INT){
        	((EditText)(findViewById(R.id.numeroOcupantes))).setText(String.valueOf(getImovel().getNumeroOcupantes()));
        }		
	}

	public void populateSubCategoriasResidenciais(){
		
		if (getImovel().hasCategoria(getImovel().getCategoriaResidencial())){
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasR1))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria1()));
			}
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasR2))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasR3))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria3()));
			}
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasR4))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria4()));
			}
		}
	}
	
	public void populateSubCategoriasComerciais(){

		if (getImovel().hasCategoria(getImovel().getCategoriaComercial())){
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasC1))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria1()));
			}
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasC2))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasC3))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria3()));
			}
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasC4))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria4()));
			}
		}
	}

	public void populateSubCategoriasPublicas(){

		if (getImovel().hasCategoria(getImovel().getCategoriaPublica())){
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasP1))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria1()));
			}
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasP2))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasP3))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria3()));
			}
			
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasP4))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria4()));
			}
		}
	}

	public void populateSubCategoriasIndustriais(){

		if (getImovel().hasCategoria(getImovel().getCategoriaIndustrial())){
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasI1))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria1()));
			}
			
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasI2))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasI3))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria3()));
			}
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(findViewById(R.id.economiasI4))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria4()));
			}
		}
	}
	
	public boolean checkChangesSubCategoriasResidenciais(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaResidencial()) == ((EditText)(findViewById(R.id.economiasR1))).isEnabled()){
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria1() != (((EditText)findViewById(R.id.economiasR1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasR1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria2() != (((EditText)findViewById(R.id.economiasR2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasR2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria3() != (((EditText)findViewById(R.id.economiasR3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasR3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria4() != (((EditText)findViewById(R.id.economiasR4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasR4)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}

	public boolean checkChangesSubCategoriasComerciais(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaComercial()) == ((EditText)(findViewById(R.id.economiasC1))).isEnabled()){
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria1() != (((EditText)findViewById(R.id.economiasC1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasC1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaComercial().getEconomiasSubCategoria2() != (((EditText)findViewById(R.id.economiasC2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasC2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaComercial().getEconomiasSubCategoria3() != (((EditText)findViewById(R.id.economiasC3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasC3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaComercial().getEconomiasSubCategoria4() != (((EditText)findViewById(R.id.economiasC4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasC4)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}

	public boolean checkChangesSubCategoriasPublicas(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaPublica()) == ((EditText)(findViewById(R.id.economiasP1))).isEnabled()){
			
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria1() != (((EditText)findViewById(R.id.economiasP1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasP1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaPublica().getEconomiasSubCategoria2() != (((EditText)findViewById(R.id.economiasP2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasP2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaPublica().getEconomiasSubCategoria3() != (((EditText)findViewById(R.id.economiasP3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasP3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaPublica().getEconomiasSubCategoria4() != (((EditText)findViewById(R.id.economiasP4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasP4)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}
	
	public boolean checkChangesSubCategoriasIndustriais(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaIndustrial()) == ((EditText)(findViewById(R.id.economiasI1))).isEnabled()){
			
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria1() != (((EditText)findViewById(R.id.economiasI1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasI1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria2() != (((EditText)findViewById(R.id.economiasI2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasI2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria3() != (((EditText)findViewById(R.id.economiasI3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasI3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria4() != (((EditText)findViewById(R.id.economiasI4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)findViewById(R.id.economiasI4)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}

	public Imovel getImovel(){
		return Controlador.getInstancia().getImovelSelecionado();
	}
	
	public void populateCategorias(){
		if (getImovel().hasCategoria(getImovel().getCategoriaResidencial())){
			cbResidencial.setChecked(true);
		}

		if (getImovel().hasCategoria(getImovel().getCategoriaComercial())){
			cbComercial.setChecked(true);
		}

		if (getImovel().hasCategoria(getImovel().getCategoriaPublica())){
			cbPublica.setChecked(true);
		}

		if (getImovel().hasCategoria(getImovel().getCategoriaIndustrial())){
			cbIndustrial.setChecked(true);
		}
	}
	
	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Context context;

		public MySimpleArrayAdapter(Context context) {
			super(context, R.layout.ramo_atividade, ramosAtividadeImovel);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.ramo_atividade, null, true);
			TextView txtCodigoRamoAtividade = (TextView) rowView.findViewById(R.id.txtCodigoRamoAtividade);
			TextView txtDescricaoRamoAtividade = (TextView) rowView.findViewById(R.id.txtDescricaoRamoAtividade);

			txtCodigoRamoAtividade.setText(ramosAtividadeImovel.get(position));
			txtDescricaoRamoAtividade.setText(Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_RAMO_ATIVIDADE, ramosAtividadeImovel.get(position)));

	        // Button Remove Ramo Atividade 
	        final Button buttonRemoveRamoAtividade = (Button)rowView.findViewById(R.id.buttonRemoveRamoAtividade);
	        buttonRemoveRamoAtividade.setOnClickListener(new OnClickListener() {

	        	public void onClick(View v) {
	        	    ramosAtividadeImovel.remove(position);
	        	    
	        	    // Hide txtEmpty se lista de ramos de atividade for maior que ZERO.
	        	    if (ramosAtividadeImovel.size() > 0){
		        		((TextView)findViewById(R.id.txtEmpty)).setVisibility(TextView.GONE);
	        	    }else{
		        		((TextView)findViewById(R.id.txtEmpty)).setVisibility(TextView.VISIBLE);	        	    	
	        	    }
	        	    
	        	    ViewGroup.LayoutParams params = parent.getLayoutParams();
	        	    params.height = (int) (33*(ramosAtividadeImovel.size())*(getResources().getDisplayMetrics().density));
	        	    parent.setLayoutParams(params);
	        	    parent.requestLayout();
	        	}
	        });
			return rowView;
		}
		
		public String getListRamoAtividadeElement(int element){
			return ramosAtividadeImovel.get(element);
		}
	}	

	@Override
	protected Dialog onCreateDialog(final int id) {
	        
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AlertDialog.Builder builder;
	  
		switch (id){
		case Constantes.DIALOG_ID_SUCESSO:
		case Constantes.DIALOG_ID_ERRO:
		case Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO:
	        View layout = inflater.inflate(R.layout.custon_dialog, (ViewGroup) findViewById(R.id.layout_root));
	        ((TextView)layout.findViewById(R.id.messageDialog)).setText(dialogMessage);
	        
	        if (id == Constantes.DIALOG_ID_SUCESSO){
		        ((ImageView)layout.findViewById(R.id.imageDialog)).setImageResource(R.drawable.save);
	
	        }else if (id == Constantes.DIALOG_ID_ERRO || id == Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO){
		        ((ImageView)layout.findViewById(R.id.imageDialog)).setImageResource(R.drawable.aviso);
	        }
	        
	        builder = new AlertDialog.Builder(this);
	        builder.setView(layout);
	        builder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        	
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		removeDialog(id);

	        		if (id == Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO){
	        			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	        			startActivity(intent);
	        		}
	        	}
	        });
	
	        AlertDialog messageDialog = builder.create();
	        return messageDialog;
	        
		case Constantes.DIALOG_ID_CONFIRM_BACK:
	        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        final View layoutConfirmationDialog = inflater.inflate(R.layout.confirmationdialog, (ViewGroup) findViewById(R.id.root));
			((TextView)layoutConfirmationDialog.findViewById(R.id.textViewUser)).setText(dialogMessage);
	
	        builder = new AlertDialog.Builder(this);
	        builder.setTitle("Atenção!");
	        builder.setView(layoutConfirmationDialog);
	        
	        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	        	
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		removeDialog(id);
	        	}
	        });
	        	 
	        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		removeDialog(id);
	        		MainTab.indiceNovoImovel = null;
	    			finish();
	        	}
	        });
	        
	        AlertDialog confirmDialog = builder.create();
	        return confirmDialog;
		    	        
		case Constantes.DIALOG_ID_CONFIRM_CHANGES:
			
	        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        final View layoutChangeDialog = inflater.inflate(R.layout.confirmationdialog, (ViewGroup) findViewById(R.id.root));
	        String categorias = "";
	                	
        	if (!categResidencialOk){
        		categorias = " residencial";
        	}

        	if (!categComercialOk){
        		categorias += " comercial";
        	}

        	if (!categPublicaOk){
        		categorias += " pública";
        	}

        	if (!categIndustrialOk){
        		categorias += " industrial";
        	}
        	
        	String[] split = categorias.split( " " );        	
	        
        	if (split.length > 1){
    	        dialogMessage = "Houve alteração nos dados das categorias" + categorias + ". Por favor informe os dados novamente.";
        	}else{
    	        dialogMessage = "Houve alteração nos dados da categoria" + categorias + ". Por favor informe os dados novamente.";
        	}
	        
			((TextView)layoutChangeDialog.findViewById(R.id.textViewUser)).setText(dialogMessage);
	
	        builder = new AlertDialog.Builder(this);
	        builder.setTitle("Confirmação");
	        builder.setView(layoutChangeDialog);
	        
	        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		removeDialog(id);
	        	}
	        });
	        	 
	        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		removeDialog(id);
	        		
	            	if (!categResidencialOk){
	            		categResidencialOk = true;
		        		enableEconominasResidencial(false);
		        		cbResidencial.setChecked(false);
	            	}

	            	if (!categComercialOk){
	            		categComercialOk = true;
		        		enableEconominasComercial(false);
		        		cbComercial.setChecked(false);
	            	}

	            	if (!categPublicaOk){
	            		categPublicaOk = true;
		        		enableEconominasPublica(false);
		        		cbPublica.setChecked(false);
	            	}

	            	if (!categIndustrialOk){
	            		categIndustrialOk = true;
		        		enableEconominasIndustrial(false);
		        		cbIndustrial.setChecked(false);
	            	}
	        	}
	        });
	        
	        AlertDialog changeDialog = builder.create();
	        return changeDialog;
		    
		}
	    return null;
	}

    @SuppressWarnings("deprecation")
	public boolean onKeyDown(int keyCode, KeyEvent event){
        
    	if ((keyCode == KeyEvent.KEYCODE_BACK)){
    		dialogMessage = " Deseja voltar para a lista de cadastros? ";
	    	showDialog(Constantes.DIALOG_ID_CONFIRM_BACK);
            return true;

        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

	public void onLocationChanged(Location location) {
		lastKnownLocation = location;
	}
	
	@SuppressWarnings("deprecation")
	public void onProviderDisabled(String provider) {
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
		dialogMessage = " GPS está desligado. Por favor, ligue-o para continuar o cadastro. ";
    	showDialog(Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
	}
	
	public void onProviderEnabled(String provider) {
		Toast.makeText( getApplicationContext(),"GPS ligado",Toast.LENGTH_SHORT).show();
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}