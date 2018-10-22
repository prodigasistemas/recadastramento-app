package com.AndroidExplorer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.Imovel;
import util.Constantes;
import util.Util;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.CellLocation;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import business.Controlador;
 
public class ImovelTab extends Fragment implements LocationListener {

	static boolean isUpdatingRamoAtividade;
	private static View view;
	private static CheckBox cbResidencial;;
	private static CheckBox cbComercial;
	private static CheckBox cbPublica;
	private static CheckBox cbIndustrial;
	private MySimpleArrayAdapter ramoAtividadeList;
	private ArrayList<String> ramosAtividadeImovel;
	private List<String> listRamosAtividade;
	private List<String> listFonteAbastecimento;
	private List<String> listPercentualAbastecimento;
	private List<String> listClasseSocial;
	private List<String> listTipo;
	private List<String> listAcessoHidrometro;
	private List<String> listTiposLogradouroImovel;
	private String dialogMessage = null;
	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;
	
	private Spinner spinnerClasseSocial;
	private Spinner spinnerTipoUso;
	private Spinner spinnerAcessoHidrometro;
	
	private static boolean categResidencialOk = false;
	private static boolean categComercialOk = false;
	private static boolean categPublicaOk = false;
	private static boolean categIndustrialOk = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		categResidencialOk = false;
		categComercialOk = false;
		categPublicaOk = false;
		categIndustrialOk = false;

		view = inflater.inflate(R.layout.imoveltab, container, false);
		
		// Define a imagem de fundo de acordo com a orientacao do dispositivo
	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
	    	view.setBackgroundResource(R.drawable.fundocadastro);
	    else
	    	view.setBackgroundResource(R.drawable.fundocadastro);

        instanciate();
        return view;
	}

	public void instanciate(){
        /* Use the LocationManager class to obtain GPS locations */
        mLocManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        
        boolean enabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabled){
	        dialogMessage = " GPS está desligado. Por favor, ligue-o para continuar o cadastro. ";
	        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
        }
        
		Criteria criteria = new Criteria();
		provider = mLocManager.getBestProvider(criteria, false);
		Location location = mLocManager.getLastKnownLocation(provider);

        lastKnownLocation = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	CellLocation.requestLocationUpdate();

		Util.addTextChangedListenerIPTUMask((EditText)view.findViewById(R.id.iptu));
    	// Verifica após preencher o campo se está válido
    	((EditText)view.findViewById(R.id.iptu)).setOnFocusChangeListener(new OnFocusChangeListener() {          
    		public void onFocusChange(View v, boolean hasFocus) {
    			
    			if(!hasFocus){
    				
    				if ( ((EditText)view.findViewById(R.id.iptu)).getText().toString().length() > 0 &&
    					 ((EditText)view.findViewById(R.id.iptu)).getText().toString().length() < 31){
    					
    						dialogMessage = "Número de IPTU inválido.";
                            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
    				}
    			}
    		}
    	});
		
    	cbResidencial = (CheckBox) view.findViewById(R.id.checkBoxResidencial);;
    	cbComercial = (CheckBox) view.findViewById(R.id.checkBoxComercial);
    	cbPublica = (CheckBox) view.findViewById(R.id.checkBoxPublica);
    	cbIndustrial = (CheckBox) view.findViewById(R.id.checkBoxIndustrial);
    	ramosAtividadeImovel = new ArrayList<String>();
        
    	Util.addTextChangedListenerCepMask((EditText)view.findViewById(R.id.cepImovel));
    	
    	// Spinner Tipo Logradouro
        Spinner spinnerTipoLogradouro = (Spinner) view.findViewById(R.id.spinnerTipoLogradouroImovel);
        listTiposLogradouroImovel = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_TIPO_LOGRADOURO);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listTiposLogradouroImovel);
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
        Spinner spinnerDescricaoRamoAtividade = (Spinner) view.findViewById(R.id.spinnerDescricaoRamoAtividade);
        
        listRamosAtividade = new ArrayList<String>();
        listRamosAtividade = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_RAMO_ATIVIDADE);
        listRamosAtividade.add(0, "");
        
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listRamosAtividade);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDescricaoRamoAtividade.setAdapter(adapter);
        spinnerDescricaoRamoAtividade.setOnItemSelectedListener(new OnItemSelectedListener () {

        	
			public void onItemSelected(AdapterView parent, View v, int position, long id){
 				String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_RAMO_ATIVIDADE, ((Spinner)view.findViewById(R.id.spinnerDescricaoRamoAtividade)).getSelectedItem().toString());
 				
 				if (codigo.compareTo(((EditText)view.findViewById(R.id.codigoRamoAtividade)).getText().toString()) != 0 &&
 					((Spinner)(view.findViewById(R.id.spinnerDescricaoRamoAtividade))).getSelectedItemPosition() != 0){
 	 				
 					isUpdatingRamoAtividade = true;  
 					((EditText)view.findViewById(R.id.codigoRamoAtividade)).setText(codigo);
 					
	        	}else if (((Spinner)(view.findViewById(R.id.spinnerDescricaoRamoAtividade))).getSelectedItemPosition() == 0){
	        		((EditText)view.findViewById(R.id.codigoRamoAtividade)).setText("0");
	        	}
 				
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
        
        
		// Codigo do Ramo de Atividade
        EditText codigoRamoAtividade = (EditText)view.findViewById(R.id.codigoRamoAtividade);
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
 			                ((Spinner)(view.findViewById(R.id.spinnerDescricaoRamoAtividade))).setSelection(i);
 			        		break;
 			        	}else{
 			                ((Spinner)(view.findViewById(R.id.spinnerDescricaoRamoAtividade))).setSelection(0);
 			        	}
 			        }
 				}
    		}  
    		
    	    public void afterTextChanged(Editable s) {}  
		});

        // Fonte de Abastecimento
        Spinner spinnerFonteAbastecimento = (Spinner) view.findViewById(R.id.spinnerFonteAbastecimento);

        listFonteAbastecimento = new ArrayList<String>();
        listFonteAbastecimento = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_FONTE_ABASTECIMENTO);
        listFonteAbastecimento.add(0, "");

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listFonteAbastecimento);
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
		
		
		// Percentual de Abastecimento
        Spinner spinnerPercentualAbastecimento = (Spinner) view.findViewById(R.id.spinnerPercentualAbastecimento);

        listPercentualAbastecimento = new ArrayList<String>();
        listPercentualAbastecimento.add(0, "");
        listPercentualAbastecimento.add(1,"25");
        listPercentualAbastecimento.add(2,"50");
        listPercentualAbastecimento.add(3,"75");
        listPercentualAbastecimento.add(4,"100");

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listPercentualAbastecimento);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPercentualAbastecimento.setAdapter(adapter);
		
        // populate Percentual de Abastecimento
		String descricaoPercentualAbastecimento = String.valueOf(getImovel().getPercentualAbastecimento());
		if (descricaoPercentualAbastecimento != null){
			for (int i = 0; i < listPercentualAbastecimento.size(); i++){
	        	if (listPercentualAbastecimento.get(i).equalsIgnoreCase(descricaoPercentualAbastecimento)){
	        		spinnerPercentualAbastecimento.setSelection(i);
	        		break;
	        	}else{
	        		spinnerPercentualAbastecimento.setSelection(0);	        		
	        	}
	        }
		}
		
		// Spinners de classe_social
        spinnerClasseSocial = (Spinner) view.findViewById(R.id.spinnerClasseSocial);
        listClasseSocial = new ArrayList<String>();
        listClasseSocial = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_CLASSE_SOCIAL);
        listClasseSocial.add(0, "");
        
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listClasseSocial);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClasseSocial.setAdapter(adapter);

        // populate classe social
		String descricaoClasseSocial = Controlador.getInstancia().getCadastroDataManipulator().
				selectDescricaoByCodigoFromTable(Constantes.TABLE_CLASSE_SOCIAL, String.valueOf(getImovel().getClasseSocial()));
		if (descricaoClasseSocial != null){
			for (int i = 0; i < listClasseSocial.size(); i++){
	        	if (listClasseSocial.get(i).equalsIgnoreCase(descricaoClasseSocial)){
	        		spinnerClasseSocial.setSelection(i);
	        		break;
	        	}else{
	        		spinnerClasseSocial.setSelection(0);
	        	}
	        }
		}
		
		spinnerTipoUso = (Spinner) view.findViewById(R.id.spinnerTipo);
		listTipo = new ArrayList<String>();
        listTipo = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_TIPO_USO);
        listTipo.add(0, "");
        
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listTipo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoUso.setAdapter(adapter);

        // populate tipo uso
		String descricaoTipoUso = Controlador.getInstancia().getCadastroDataManipulator().
				selectDescricaoByCodigoFromTable(Constantes.TABLE_TIPO_USO, String.valueOf(getImovel().getTipoUso()));
		if (descricaoTipoUso != null){
			for (int i = 0; i < listTipo.size(); i++){
	        	if (listTipo.get(i).equalsIgnoreCase(descricaoTipoUso)){
	        		spinnerTipoUso.setSelection(i);
	        		break;
	        	}else{
	        		spinnerTipoUso.setSelection(0);
	        	}
	        }
		}
        
		spinnerAcessoHidrometro = (Spinner) view.findViewById(R.id.spinnerAcessoHidrometro);
		listAcessoHidrometro = new ArrayList<String>();
		listAcessoHidrometro = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_ACESSO_HIDROMETRO);
		listAcessoHidrometro.add(0, "");
        
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listAcessoHidrometro);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAcessoHidrometro.setAdapter(adapter);

        // populate acesso hidrometro
		String descricaoAcessoHidrometro = Controlador.getInstancia().getCadastroDataManipulator().
				selectDescricaoByCodigoFromTable(Constantes.TABLE_ACESSO_HIDROMETRO, String.valueOf(getImovel().getAcessoHidrometro()));
		if (descricaoAcessoHidrometro != null){
			for (int i = 0; i < listAcessoHidrometro.size(); i++){
	        	if (listAcessoHidrometro.get(i).equalsIgnoreCase(descricaoAcessoHidrometro)){
	        		spinnerAcessoHidrometro.setSelection(i);
	        		break;
	        	}else{
	        		spinnerAcessoHidrometro.setSelection(0);
	        	}
	        }
		}
        
    	populateImovel();
    	if (Util.allowPopulateDados()){
    		populateCategorias();
    	}   
    	
        // Button Add Ramo Atividade 
        final Button buttonAddRamoAtividade = (Button)view.findViewById(R.id.buttonAddRamoAtividade);
        buttonAddRamoAtividade.setOnClickListener(new OnClickListener() {

        	public void onClick(View v) {
        		boolean isRamoAtividadeJaAdicionado = false;
        		
        		// Verifica se Ramo de Atividade já foi adicionado neste Imovel.
        		if (ramosAtividadeImovel != null){
	        		for (int i=0; i< ramosAtividadeImovel.size(); i++){
	        			if ((ramosAtividadeImovel.get(i)).equalsIgnoreCase(((EditText)(view.findViewById(R.id.codigoRamoAtividade))).getText().toString())) {
	        				
	        				isRamoAtividadeJaAdicionado = true;
	        			}
	        		}
        		}  
        		if (!isRamoAtividadeJaAdicionado){
        			if (((EditText)(view.findViewById(R.id.codigoRamoAtividade))).getText().toString().length() > 0){
        				if (((EditText)(view.findViewById(R.id.codigoRamoAtividade))).getText().toString().equals("0")){
                			
     
        					((Button)(view.findViewById(R.id.buttonAddRamoAtividade))).setClickable(false);
            				ramoAtividadeList.clear();
            				} 

        				ramosAtividadeImovel.add(((EditText)(view.findViewById(R.id.codigoRamoAtividade))).getText().toString());
        				ListView listRamosAtividade = (ListView)view.findViewById(R.id.listRamosAtividade);
        				ramoAtividadeList = new MySimpleArrayAdapter(getActivity().getBaseContext());
        				listRamosAtividade.setAdapter(ramoAtividadeList);
        				
        				// Hide txtEmpty se lista de ramos de atividade for maior que ZERO.
        				if (ramosAtividadeImovel.size() > 0){
        					((TextView)view.findViewById(R.id.txtEmpty)).setVisibility(TextView.GONE);
        				}else{
        					((TextView)view.findViewById(R.id.txtEmpty)).setVisibility(TextView.VISIBLE);	        	    	
        				}
        				
        				ViewGroup.LayoutParams params = listRamosAtividade.getLayoutParams();
        				params.height = (int) ((33*(ramosAtividadeImovel.size()))*(getResources().getDisplayMetrics().density));
        				listRamosAtividade.setLayoutParams(params);
        				listRamosAtividade.requestLayout();

        			}else{
            			dialogMessage = " Ramo de Atividade inválido! ";
                        showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
        			}
        		}else{
        			dialogMessage = " Ramo de Atividade já existente! ";
                    showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
        		}
        	}
        });

        // Button Save 
        final Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
             	// Verifica os campos obrigatórios
            	if (areCamposObrigatoriosOk()){
            		
                	// Ramo de atividade somente para imóvel com economia Comercial, publica ou industrial!!!
            		if (isRamoAtividadeOk()){
            			
                    	// Se tipo de categoria estiver selecionado deve possuir pelo menos uma economia.
                		if (isDadosCategoriaOk()){
            	
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
                					
                					showCompleteDialog(R.drawable.aviso, "Confirmação:", dialogMessage, Constantes.DIALOG_ID_CONFIRMA_MUDANCA);
                					
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
                				
                				getImovel().setTabSaved(true);
                				Toast.makeText(getActivity(), "Dados do Imovel atualizados com sucesso.", 5).show();
                				
                				if(getImovel().getImovelStatus() != Constantes.IMOVEL_A_SALVAR){
                					Controlador.getInstancia().getCadastroDataManipulator().salvarImovel();
                				}
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
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			result = false;

		}else if ( (cbComercial.isChecked() || cbPublica.isChecked() || cbIndustrial.isChecked()) && ramosAtividadeImovel.size() == 0){
			dialogMessage = " É necessário informar o Ramo de Atividade para economia Comercial, Pública ou Industrial. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			result = false;
		}
		return result;
	}

	public boolean isDadosCategoriaOk(){
		boolean result = true;

		if ( cbComercial.isChecked() &&
				((((EditText)view.findViewById(R.id.economiasC1)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)view.findViewById(R.id.economiasC2)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)view.findViewById(R.id.economiasC3)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)view.findViewById(R.id.economiasC4)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias comerciais inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
	    	result = false;
		}

		if ( cbIndustrial.isChecked() &&
				((((EditText)view.findViewById(R.id.economiasI1)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)view.findViewById(R.id.economiasI2)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)view.findViewById(R.id.economiasI3)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)view.findViewById(R.id.economiasI4)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias industriais inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
	    	result = false;
		}

		if ( cbPublica.isChecked() &&
				((((EditText)view.findViewById(R.id.economiasP1)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)view.findViewById(R.id.economiasP2)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)view.findViewById(R.id.economiasP3)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)view.findViewById(R.id.economiasP4)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias públicas inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
	    	result = false;
		}

		return result;
	}
	
	public boolean areCamposObrigatoriosOk(){
		boolean result = true;
		
		//localidade
		if ( (((EditText)view.findViewById(R.id.localidade)).getText().toString().trim().length() == 0) ||
			 ( ((EditText)view.findViewById(R.id.localidade)).getText().toString().trim().length() >0 &&
				(Integer.parseInt(((EditText)view.findViewById(R.id.localidade)).getText().toString().trim()) == 0 ) ) ){
		    
			dialogMessage = " Localidade inválida. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}

		//setor
		if ( (((EditText)view.findViewById(R.id.setor)).getText().toString().trim().length() == 0) ||
				 ( ((EditText)view.findViewById(R.id.setor)).getText().toString().trim().length() >0 &&
					(Integer.parseInt(((EditText)view.findViewById(R.id.setor)).getText().toString().trim()) == 0 ) ) ){
		    
			dialogMessage = " Setor inválido ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// rota
		if ( (((EditText)view.findViewById(R.id.rota)).getText().toString().trim().length() == 0) ||
				 ( ((EditText)view.findViewById(R.id.rota)).getText().toString().trim().length() >0 &&
					(Integer.parseInt(((EditText)view.findViewById(R.id.rota)).getText().toString().trim()) == 0 ) ) ){
		    
			dialogMessage = " Rota inválida. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// logradouro
		if (((EditText)view.findViewById(R.id.logradouro)).getText().toString().trim().compareTo("") == 0){
		    
			dialogMessage = " Logradouro inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// Bairro
		if (((EditText)view.findViewById(R.id.bairro)).getText().toString().trim().compareTo("") == 0){
		    
			dialogMessage = " Bairro inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// Municipio
		if (((EditText)view.findViewById(R.id.municipio)).getText().toString().trim().compareTo("") == 0){
		    
			dialogMessage = " Município inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		if ( ((EditText)view.findViewById(R.id.iptu)).getText().toString().length() > 0 &&
				 ((EditText)view.findViewById(R.id.iptu)).getText().toString().length() < 31){
				
					dialogMessage = "Número de IPTU inválido.";
                   showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
		}

		// Verificar Categorias - pelo menos 1 categoria
		if (!cbResidencial.isChecked() && !cbComercial.isChecked() && !cbIndustrial.isChecked() && !cbPublica.isChecked()){
		    
			dialogMessage = " Imóvel deve possuir pelo menos 1 economia. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}
		
		// Verificar Econominas. pelo menos 1 economia
		if ( cbResidencial.isChecked() &&
				((((EditText)view.findViewById(R.id.economiasR1)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasR1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasR1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)view.findViewById(R.id.economiasR2)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasR2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasR2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)view.findViewById(R.id.economiasR3)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasR3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasR3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)view.findViewById(R.id.economiasR4)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasR4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasR4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias residenciais inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}

		if ( cbComercial.isChecked() &&
				((((EditText)view.findViewById(R.id.economiasC1)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)view.findViewById(R.id.economiasC2)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)view.findViewById(R.id.economiasC3)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)view.findViewById(R.id.economiasC4)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasC4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasC4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias comerciais inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}

		if ( cbIndustrial.isChecked() &&
				((((EditText)view.findViewById(R.id.economiasI1)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)view.findViewById(R.id.economiasI2)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)view.findViewById(R.id.economiasI3)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)view.findViewById(R.id.economiasI4)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasI4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasI4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias industriais inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}

		if ( cbPublica.isChecked() &&
				((((EditText)view.findViewById(R.id.economiasP1)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP1)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP1)).getText().toString().trim()) == 0 ))) 
				&&
				
				((((EditText)view.findViewById(R.id.economiasP2)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP2)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP2)).getText().toString().trim()) == 0 ))) 
				&&
						
				((((EditText)view.findViewById(R.id.economiasP3)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP3)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP3)).getText().toString().trim()) == 0 ))) 
				&&
								
				((((EditText)view.findViewById(R.id.economiasP4)).getText().toString().trim().length() == 0) ||
				( ((EditText)view.findViewById(R.id.economiasP4)).getText().toString().trim().length() >0 &&
				   (Integer.parseInt(((EditText)view.findViewById(R.id.economiasP4)).getText().toString().trim()) == 0 )))){
			
			dialogMessage = " Número de economias públicas inválido. ";
            showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
			return false;
		}

		return result;
	}
	
	public void updateImovelSelecionado(){
		
		if (getImovel().getImovelStatus() == Constantes.IMOVEL_NOVO) {
		} else {
			getImovel().setMatricula(((EditText)view.findViewById(R.id.matricula)).getText().toString());
		}
		
		getImovel().setCodigoCliente(((EditText)view.findViewById(R.id.codCliente)).getText().toString());
		getImovel().setLocalidade(Util.adicionarZerosEsquerdaNumero(3, ((EditText)view.findViewById(R.id.localidade)).getText().toString()));
		getImovel().setSetor(Util.adicionarZerosEsquerdaNumero(3, ((EditText)view.findViewById(R.id.setor)).getText().toString()));
		getImovel().setQuadra(Util.adicionarZerosEsquerdaNumero(4, ((EditText)view.findViewById(R.id.quadra)).getText().toString()));
		getImovel().setLote(Util.adicionarZerosEsquerdaNumero(4, ((EditText)view.findViewById(R.id.lote)).getText().toString()));
		getImovel().setSubLote(Util.adicionarZerosEsquerdaNumero(3, ((EditText)view.findViewById(R.id.subLote)).getText().toString()));
		getImovel().getInscricao();
		
		getImovel().setRota(Util.adicionarZerosEsquerdaNumero(2, ((EditText)view.findViewById(R.id.rota)).getText().toString()));
		getImovel().setFace(Util.adicionarZerosEsquerdaNumero(2, ((EditText)view.findViewById(R.id.face)).getText().toString()));
		getImovel().setCodigoMunicipio(((EditText)view.findViewById(R.id.codMunicipio)).getText().toString());
		getImovel().setListaRamoAtividade(ramosAtividadeImovel);
		getImovel().setIptu(((EditText)view.findViewById(R.id.iptu)).getText().toString());
		getImovel().setNumeroCelpa(((EditText)view.findViewById(R.id.numeroCelpa)).getText().toString());
		getImovel().setNumeroPontosUteis(((EditText)view.findViewById(R.id.numeroPontosUteis)).getText().toString());
		getImovel().setNumeroOcupantes(((EditText)view.findViewById(R.id.numeroOcupantes)).getText().toString());
		
		String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_TIPO_LOGRADOURO, ((Spinner)view.findViewById(R.id.spinnerTipoLogradouroImovel)).getSelectedItem().toString());
    	getImovel().getEnderecoImovel().setTipoLogradouro(codigo);
		getImovel().getEnderecoImovel().setLogradouro(((EditText)view.findViewById(R.id.logradouro)).getText().toString());
		getImovel().getEnderecoImovel().setNumero(((EditText)view.findViewById(R.id.numero)).getText().toString());
		getImovel().getEnderecoImovel().setComplemento(Util.removerCaractereEspecial(((EditText)view.findViewById(R.id.complemento)).getText().toString()));
		getImovel().getEnderecoImovel().setBairro(((EditText)view.findViewById(R.id.bairro)).getText().toString());
		getImovel().getEnderecoImovel().setCep(((EditText)view.findViewById(R.id.cepImovel)).getText().toString());
		getImovel().getEnderecoImovel().setMunicipio(((EditText)view.findViewById(R.id.municipio)).getText().toString());
		
		getImovel().setCodigoLogradouro(((EditText)view.findViewById(R.id.codLogradouro)).getText().toString());

		if (cbResidencial.isChecked()){
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria1(((EditText)view.findViewById(R.id.economiasR1)).getText().toString());
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria2(((EditText)view.findViewById(R.id.economiasR2)).getText().toString());
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria3(((EditText)view.findViewById(R.id.economiasR3)).getText().toString());
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria4(((EditText)view.findViewById(R.id.economiasR4)).getText().toString());
    	}else{
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaResidencial().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}
		
    	if (cbComercial.isChecked()){
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria1(((EditText)view.findViewById(R.id.economiasC1)).getText().toString());
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria2(((EditText)view.findViewById(R.id.economiasC2)).getText().toString());
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria3(((EditText)view.findViewById(R.id.economiasC3)).getText().toString());
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria4(((EditText)view.findViewById(R.id.economiasC4)).getText().toString());
    	}else{
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaComercial().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}
		
    	if (cbPublica.isChecked()){
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria1(((EditText)view.findViewById(R.id.economiasP1)).getText().toString());
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria2(((EditText)view.findViewById(R.id.economiasP2)).getText().toString());
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria3(((EditText)view.findViewById(R.id.economiasP3)).getText().toString());
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria4(((EditText)view.findViewById(R.id.economiasP4)).getText().toString());
    	}else{
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaPublica().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}
		
    	if (cbIndustrial.isChecked()){
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria1(((EditText)view.findViewById(R.id.economiasI1)).getText().toString());
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria2(((EditText)view.findViewById(R.id.economiasI2)).getText().toString());
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria3(((EditText)view.findViewById(R.id.economiasI3)).getText().toString());
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria4(((EditText)view.findViewById(R.id.economiasI4)).getText().toString());
    	}else{
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria1(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria2(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria3(String.valueOf(Constantes.NULO_INT));
    		getImovel().getCategoriaIndustrial().setEconomiasSubCategoria4(String.valueOf(Constantes.NULO_INT));
    	}

		codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_FONTE_ABASTECIMENTO, ((Spinner)view.findViewById(R.id.spinnerFonteAbastecimento)).getSelectedItem().toString());
    	getImovel().setTipoFonteAbastecimento(codigo);

        if (lastKnownLocation != null) {
	    	getImovel().setLatitude(String.valueOf(lastKnownLocation.getLatitude()));
	    	getImovel().setLongitude(String.valueOf(lastKnownLocation.getLongitude()));
        }
    	getImovel().setData(Util.formatarData(Calendar.getInstance().getTime()));

    	getImovel().setAreaConstruida(Util.removeDecimalChar(((EditText)view.findViewById(R.id.areaConstruida)).getText().toString()));
    	getImovel().setNumeroAnimais(((EditText)view.findViewById(R.id.numeroAnimais)).getText().toString());
    	getImovel().setVolumePiscina(Util.removeDecimalChar(((EditText)view.findViewById(R.id.volumePiscina)).getText().toString()));
    	getImovel().setVolumeCisterna(Util.removeDecimalChar(((EditText)view.findViewById(R.id.volumeCisterna)).getText().toString()));
    	getImovel().setVolumeCaixaDagua(Util.removeDecimalChar(((EditText)view.findViewById(R.id.volumeCaixaDagua)).getText().toString()));
    	
    	getImovel().getOcupacaoImovel().setCriancas(((EditText)view.findViewById(R.id.numCriancas)).getText().toString());
    	getImovel().getOcupacaoImovel().setAdultos(((EditText)view.findViewById(R.id.numAdultos)).getText().toString());
    	getImovel().getOcupacaoImovel().setAlunos(((EditText)view.findViewById(R.id.numAlunos)).getText().toString());
    	getImovel().getOcupacaoImovel().setCaes(((EditText)view.findViewById(R.id.numCaes)).getText().toString());
    	getImovel().getOcupacaoImovel().setIdosos(((EditText)view.findViewById(R.id.numIdosos)).getText().toString());
    	getImovel().getOcupacaoImovel().setIdosos(((EditText)view.findViewById(R.id.numEmpregados)).getText().toString());
    	getImovel().getOcupacaoImovel().setOutros(((EditText)view.findViewById(R.id.numOutros)).getText().toString());
    	
    	getImovel().setClasseSocial(String.valueOf(((Spinner)view.findViewById(R.id.spinnerClasseSocial)).getSelectedItemId()));
    	getImovel().setTipoUso(String.valueOf(((Spinner)view.findViewById(R.id.spinnerTipo)).getSelectedItemId()));
    	getImovel().setAcessoHidrometro(String.valueOf(((Spinner)view.findViewById(R.id.spinnerAcessoHidrometro)).getSelectedItemId()));
    	
    	getImovel().setQuantidadeEconomiasSocial(((EditText)view.findViewById(R.id.quantidadeEconomiasSocial)).getText().toString());
    	getImovel().setQuantidadeEconomiasOutros(((EditText)view.findViewById(R.id.quantidadeEconomiasOutros)).getText().toString());
    	
    	getImovel().setPercentualAbastecimento(String.valueOf(((Spinner)view.findViewById(R.id.spinnerPercentualAbastecimento)).getSelectedItem()));
    	getImovel().setObservacao(((EditText)view.findViewById(R.id.editObservação)).getText().toString());
	}
	
	public static void enableEconominasResidencial(boolean enable){
    	((EditText)(view.findViewById(R.id.economiasR1))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasR2))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasR3))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasR4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(view.findViewById(R.id.economiasR1))).setText("");
        	((EditText)(view.findViewById(R.id.economiasR2))).setText("");
        	((EditText)(view.findViewById(R.id.economiasR3))).setText("");
        	((EditText)(view.findViewById(R.id.economiasR4))).setText("");

    	}
	}
	
	public static void enableEconominasComercial(boolean enable){
    	((EditText)(view.findViewById(R.id.economiasC1))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasC2))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasC3))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasC4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(view.findViewById(R.id.economiasC1))).setText("");
        	((EditText)(view.findViewById(R.id.economiasC2))).setText("");
        	((EditText)(view.findViewById(R.id.economiasC3))).setText("");
        	((EditText)(view.findViewById(R.id.economiasC4))).setText("");

    	}
	}

	public static void enableEconominasPublica(boolean enable){
    	((EditText)(view.findViewById(R.id.economiasP1))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasP2))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasP3))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasP4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(view.findViewById(R.id.economiasP1))).setText("");
        	((EditText)(view.findViewById(R.id.economiasP2))).setText("");
        	((EditText)(view.findViewById(R.id.economiasP3))).setText("");
        	((EditText)(view.findViewById(R.id.economiasP4))).setText("");

    	}
	}

	public static void enableEconominasIndustrial(boolean enable){
    	((EditText)(view.findViewById(R.id.economiasI1))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasI2))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasI3))).setEnabled(enable);
    	((EditText)(view.findViewById(R.id.economiasI4))).setEnabled(enable);
    	
    	// limpa textfields se os mesmos estiverem desabilitados
    	if (!enable){
        	((EditText)(view.findViewById(R.id.economiasI1))).setText("");
        	((EditText)(view.findViewById(R.id.economiasI2))).setText("");
        	((EditText)(view.findViewById(R.id.economiasI3))).setText("");
        	((EditText)(view.findViewById(R.id.economiasI4))).setText("");

    	}
	}
	
	public void populateImovel(){
		
        // Código do Cliente
        if (getImovel().getCodigoCliente() != Constantes.NULO_INT){
        	((EditText)view.findViewById(R.id.codCliente)).setText(String.valueOf(getImovel().getCodigoCliente()));
        }
        
		// Matricula
        if (getImovel().getMatricula() != Constantes.NULO_INT){
        	((EditText)view.findViewById(R.id.matricula)).setText(String.valueOf(getImovel().getMatricula()));
        }
        
		// Localidade
        if (getImovel().getLocalidade() != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.localidade)).setText(String.valueOf(getImovel().getLocalidade()));
        }
        
		// Setor
        if (getImovel().getSetor() != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.setor)).setText(String.valueOf(getImovel().getSetor()));
        }
        
		// Quadra
        if (getImovel().getQuadra() != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.quadra)).setText(String.valueOf(getImovel().getQuadra()));
        }
       
		// Lote
        if (getImovel().getLote() != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.lote)).setText(String.valueOf(getImovel().getLote()));
        }
        
		// Sub Lote
        if (getImovel().getSubLote() != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.subLote)).setText(String.valueOf(getImovel().getSubLote()));
        }
		
		// Rota
        if (getImovel().getRota() != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.rota)).setText(String.valueOf(getImovel().getRota()));
        }
		
		// Face
        if (getImovel().getFace() != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.face)).setText(String.valueOf(getImovel().getFace()));
        }
		
		// Logradouro
        if ( String.valueOf(getImovel().getEnderecoImovel().getLogradouro()) != Constantes.NULO_STRING){
        	((EditText)view.findViewById(R.id.logradouro)).setText(String.valueOf(getImovel().getEnderecoImovel().getLogradouro()));
        }
        
		// Numero
		((EditText)view.findViewById(R.id.numero)).setText(getImovel().getEnderecoImovel().getNumero());
		
		// Codigo do Logradouro
        if (getImovel().getCodigoLogradouro() != Constantes.NULO_INT){
        	((EditText)view.findViewById(R.id.codLogradouro)).setText(String.valueOf(getImovel().getCodigoLogradouro()));
        }
        
		// Complemento
		((EditText)view.findViewById(R.id.complemento)).setText(getImovel().getEnderecoImovel().getComplemento());
		
		// Bairro
		((EditText)view.findViewById(R.id.bairro)).setText(getImovel().getEnderecoImovel().getBairro());
		
		// Cep
		((EditText)view.findViewById(R.id.cepImovel)).setText(String.valueOf(getImovel().getEnderecoImovel().getCep()));
		
		// Municipio
		((EditText)view.findViewById(R.id.municipio)).setText(getImovel().getEnderecoImovel().getMunicipio());
		
		// Código do Municipio
		((EditText)view.findViewById(R.id.codMunicipio)).setText(String.valueOf(getImovel().getCodigoMunicipio()));
		
		// Codigo do Ramo de Atividade
        // popula lista dos Ramos de Atividade do Imovel
        for (int i =0; i < getImovel().getListaRamoAtividade().size(); i++){
    	    ramosAtividadeImovel.add(getImovel().getListaRamoAtividade().get(i));
		}
        
 	    ListView listRamosAtividade = (ListView)view.findViewById(R.id.listRamosAtividade);
 	    ramoAtividadeList = new MySimpleArrayAdapter(getActivity().getBaseContext());
	    listRamosAtividade.setAdapter(ramoAtividadeList);
	    
	    // Hide txtEmpty se lista de ramos de atividade for maior que ZERO.
	    if (ramosAtividadeImovel.size() > 0){
    		((TextView)view.findViewById(R.id.txtEmpty)).setVisibility(TextView.GONE);
	    }else{
    		((TextView)view.findViewById(R.id.txtEmpty)).setVisibility(TextView.VISIBLE);	        	    	
	    }

	    ViewGroup.LayoutParams params = listRamosAtividade.getLayoutParams();
	    params.height = (int) (33*(ramosAtividadeImovel.size())*(getResources().getDisplayMetrics().density));
        listRamosAtividade.setLayoutParams(params);
        listRamosAtividade.requestLayout();
        
		// IPTU
		((EditText)(view.findViewById(R.id.iptu))).setText(getImovel().getIptu());
		
		// Numero UC CELPA
        if (getImovel().getNumeroCelpa() != Constantes.NULO_STRING){
        	((EditText)(view.findViewById(R.id.numeroCelpa))).setText(String.valueOf(getImovel().getNumeroCelpa()));
        }		

        // Numero Pontos Úteis
        if (getImovel().getNumeroPontosUteis() != Constantes.NULO_INT){
        	((EditText)(view.findViewById(R.id.numeroPontosUteis))).setText(String.valueOf(getImovel().getNumeroPontosUteis()));
        }		
        
        // Numero Ocupantes
        if (getImovel().getNumeroOcupantes() != Constantes.NULO_INT){
        	((EditText)(view.findViewById(R.id.numeroOcupantes))).setText(String.valueOf(getImovel().getNumeroOcupantes()));
        }
        
        ((EditText)(view.findViewById(R.id.areaConstruida))).setText(Util.verificarNuloString(getImovel().getAreaConstruida()));
        
        // Numero de animais
        if (getImovel().getNumeroAnimais() != Constantes.NULO_INT){
        	((EditText)(view.findViewById(R.id.numeroAnimais))).setText(String.valueOf(getImovel().getNumeroAnimais()));
        }
        
        ((EditText)(view.findViewById(R.id.volumeCisterna))).setText(Util.verificarNuloString(getImovel().getVolumeCisterna()));
        ((EditText)(view.findViewById(R.id.volumePiscina))).setText(Util.verificarNuloString(getImovel().getVolumePiscina()));
        ((EditText)(view.findViewById(R.id.volumeCaixaDagua))).setText(Util.verificarNuloString(getImovel().getVolumeCaixaDagua()));
        
        // Numero de criancas
        if (getImovel().getOcupacaoImovel().getCriancas() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.numCriancas)).setText(String.valueOf(getImovel().getOcupacaoImovel().getCriancas()));
        }
        // Numero de adultos
        if (getImovel().getOcupacaoImovel().getAdultos() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.numAdultos)).setText(String.valueOf(getImovel().getOcupacaoImovel().getAdultos()));
        }
        // Numero de Alunos
        if (getImovel().getOcupacaoImovel().getAlunos() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.numAlunos)).setText(String.valueOf(getImovel().getOcupacaoImovel().getAlunos()));
        }
        // Numero de caes
        if (getImovel().getOcupacaoImovel().getCaes() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.numCaes)).setText(String.valueOf(getImovel().getOcupacaoImovel().getCaes()));
        }
        // Numero de Idosos
        if (getImovel().getOcupacaoImovel().getIdosos() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.numIdosos)).setText(String.valueOf(getImovel().getOcupacaoImovel().getIdosos()));
        }
        // Numero de Empregados
        if (getImovel().getOcupacaoImovel().getEmpregados() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.numEmpregados)).setText(String.valueOf(getImovel().getOcupacaoImovel().getEmpregados()));
        }
        // Numero de Outros
        if (getImovel().getOcupacaoImovel().getOutros() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.numOutros)).setText(String.valueOf(getImovel().getOcupacaoImovel().getOutros()));
        }
        // Quantidade de economias sociais
        if (getImovel().getQuantidadeEconomiasSocial() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.quantidadeEconomiasSocial)).setText(String.valueOf(getImovel().getQuantidadeEconomiasSocial()));
        }
        // Quantidade de economias outros
        if (getImovel().getQuantidadeEconomiasOutros() != Constantes.NULO_INT){
        	((EditText) view.findViewById(R.id.quantidadeEconomiasOutros)).setText(String.valueOf(getImovel().getQuantidadeEconomiasOutros()));      
        }
        
        //Observacao
        ((EditText)view.findViewById(R.id.editObservação)).setText(getImovel().getObservacao());

    }

	public void populateSubCategoriasResidenciais(){
		
		if (getImovel().hasCategoria(getImovel().getCategoriaResidencial())){
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasR1))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria1()));
			}
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasR2))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasR3))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria3()));
			}
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasR4))).setText(String.valueOf(getImovel().getCategoriaResidencial().getEconomiasSubCategoria4()));
			}
		}
	}
	
	public void populateSubCategoriasComerciais(){

		if (getImovel().hasCategoria(getImovel().getCategoriaComercial())){
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasC1))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria1()));
			}
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasC2))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasC3))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria3()));
			}
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasC4))).setText(String.valueOf(getImovel().getCategoriaComercial().getEconomiasSubCategoria4()));
			}
		}
	}

	public void populateSubCategoriasPublicas(){

		if (getImovel().hasCategoria(getImovel().getCategoriaPublica())){
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasP1))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria1()));
			}
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasP2))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasP3))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria3()));
			}
			
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasP4))).setText(String.valueOf(getImovel().getCategoriaPublica().getEconomiasSubCategoria4()));
			}
		}
	}

	public void populateSubCategoriasIndustriais(){

		if (getImovel().hasCategoria(getImovel().getCategoriaIndustrial())){
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria1() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasI1))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria1()));
			}
			
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria2() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasI2))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria2()));
			}
			
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria3() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasI3))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria3()));
			}
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria4() != Constantes.NULO_INT){
				((EditText)(view.findViewById(R.id.economiasI4))).setText(String.valueOf(getImovel().getCategoriaIndustrial().getEconomiasSubCategoria4()));
			}
		}
	}
	
	public boolean checkChangesSubCategoriasResidenciais(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaResidencial()) == ((EditText)(view.findViewById(R.id.economiasR1))).isEnabled()){
			
			if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria1() != (((EditText)view.findViewById(R.id.economiasR1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasR1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria2() != (((EditText)view.findViewById(R.id.economiasR2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasR2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria3() != (((EditText)view.findViewById(R.id.economiasR3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasR3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaResidencial().getEconomiasSubCategoria4() != (((EditText)view.findViewById(R.id.economiasR4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasR4)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}

	public boolean checkChangesSubCategoriasComerciais(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaComercial()) == ((EditText)(view.findViewById(R.id.economiasC1))).isEnabled()){
			
			if (getImovel().getCategoriaComercial().getEconomiasSubCategoria1() != (((EditText)view.findViewById(R.id.economiasC1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasC1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaComercial().getEconomiasSubCategoria2() != (((EditText)view.findViewById(R.id.economiasC2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasC2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaComercial().getEconomiasSubCategoria3() != (((EditText)view.findViewById(R.id.economiasC3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasC3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaComercial().getEconomiasSubCategoria4() != (((EditText)view.findViewById(R.id.economiasC4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasC4)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}

	public boolean checkChangesSubCategoriasPublicas(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaPublica()) == ((EditText)(view.findViewById(R.id.economiasP1))).isEnabled()){
			
			if (getImovel().getCategoriaPublica().getEconomiasSubCategoria1() != (((EditText)view.findViewById(R.id.economiasP1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasP1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaPublica().getEconomiasSubCategoria2() != (((EditText)view.findViewById(R.id.economiasP2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasP2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaPublica().getEconomiasSubCategoria3() != (((EditText)view.findViewById(R.id.economiasP3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasP3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaPublica().getEconomiasSubCategoria4() != (((EditText)view.findViewById(R.id.economiasP4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasP4)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}
	
	public boolean checkChangesSubCategoriasIndustriais(){
		boolean result = false;
		if (getImovel().hasCategoria(getImovel().getCategoriaIndustrial()) == ((EditText)(view.findViewById(R.id.economiasI1))).isEnabled()){
			
			if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria1() != (((EditText)view.findViewById(R.id.economiasI1)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasI1)).getText().toString()) : Constantes.NULO_INT)){
				result = true;
				
			}else if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria2() != (((EditText)view.findViewById(R.id.economiasI2)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasI2)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria3() != (((EditText)view.findViewById(R.id.economiasI3)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasI3)).getText().toString()) : Constantes.NULO_INT)){
				result = true;

			}else if (getImovel().getCategoriaIndustrial().getEconomiasSubCategoria4() != (((EditText)view.findViewById(R.id.economiasI4)).getText().toString().length() > 0 ? Integer.valueOf(((EditText)view.findViewById(R.id.economiasI4)).getText().toString()) : Constantes.NULO_INT)){
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
					((Button)(view.findViewById(R.id.buttonAddRamoAtividade))).setClickable(true);
	        	    ramosAtividadeImovel.remove(position);
	        	    
	        	    // Hide txtEmpty se lista de ramos de atividade for maior que ZERO.
	        	    if (ramosAtividadeImovel.size() > 0){
		        		((TextView)view.findViewById(R.id.txtEmpty)).setVisibility(TextView.GONE);
	        	    }else{
		        		((TextView)view.findViewById(R.id.txtEmpty)).setVisibility(TextView.VISIBLE);	        	    	
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

	public void onLocationChanged(Location location) {
		lastKnownLocation = location;
	}
	
	public void onProviderDisabled(String provider) {
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        dialogMessage = " GPS está desligado. Por favor, ligue-o para continuar o cadastro. ";
        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
	}
	
	public void onProviderEnabled(String provider) {
		Toast.makeText(getActivity().getApplicationContext(),"GPS ligado",Toast.LENGTH_SHORT).show();
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	public static void setChangesConfirmed(){
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

	void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.setTargetFragment(this, Constantes.FRAGMENT_ID_IMOVEL);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
	
	void showCompleteDialog(int iconId, String title, String message, int messageType) {
		CompleteAlertDialogFragment newFragment = CompleteAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.setTargetFragment(this, Constantes.FRAGMENT_ID_IMOVEL);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

}