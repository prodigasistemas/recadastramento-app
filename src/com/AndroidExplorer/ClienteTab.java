package com.AndroidExplorer;

import java.util.Calendar;

import model.Cliente;
import model.Endereco;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
 
public class ClienteTab extends Activity implements LocationListener {

	boolean consideraEventoItemSelectedListenerCpfCnpjResponsavel = false;
	boolean consideraEventoItemSelectedListenerCpfCnpjUsuario = false;
	boolean consideraEventoItemSelectedListenerCpfCnpjProprietario = false;
	boolean consideraEventoItemSelectedListenerUsuario = false;
	boolean consideraEventoItemSelectedListenerResponsavel = false;
	static boolean isProprietarioCpfSelected = true;
	static boolean isUsuarioCpfSelected = true;
	static boolean isResponsavelCpfSelected = true;
	private String dialogMessage = null;
	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientetab);
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
	        dialogMessage = "GPS está desligado. Por favor, ligue-o para continuar o cadastro.";
	    	showDialog(Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
        }
        
		Criteria criteria = new Criteria();
		provider = mLocManager.getBestProvider(criteria, false);
		Location location = mLocManager.getLastKnownLocation(provider);

        lastKnownLocation = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	CellLocation.requestLocationUpdate();

		Util.addTextChangedListenerPhoneMask((EditText)findViewById(R.id.foneUsuario));
    	Util.addTextChangedListenerPhoneMask((EditText)findViewById(R.id.celularUsuario));
    	Util.addTextChangedListenerCpfCnpjVerifierAndMask((EditText)findViewById(R.id.cpfCnpjUsuario), Constantes.PESSOA_USUARIO);
        
    	// Verifica após preencher o campo se está válido
    	((EditText)findViewById(R.id.cpfCnpjUsuario)).setOnFocusChangeListener(new OnFocusChangeListener() {          
    		public void onFocusChange(View v, boolean hasFocus) {
    			
    			if(!hasFocus){
    				
    				if (isTipoCpf(Constantes.PESSOA_USUARIO)){
    					
    					if ( (((EditText)findViewById(R.id.cpfCnpjUsuario)).getText().toString().trim().compareTo("") != 0) && 
    						 (!Util.getCpfUsuarioOk()) ){
    						dialogMessage = "CPF do usuário inválido.";
    				    	showDialog(Constantes.DIALOG_ID_ERRO);
    					}
    				
    				}else{
    					
    					if ( (((EditText)findViewById(R.id.cpfCnpjUsuario)).getText().toString().trim().compareTo("") != 0) && 
    						 (!Util.getCnpjUsuarioOk()) ){
    						
    						dialogMessage = "CNPJ do usuário inválido.";
    				    	showDialog(Constantes.DIALOG_ID_ERRO);
    					}
    				}
    			}
    		}
    	});

		// Usuario
		// Spinner Usuario tipo de pessoa
        Spinner spinnerTipoPessoaUsuario = (Spinner) findViewById(R.id.spinnerTipoPessoaUsuario);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipoPessoa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPessoaUsuario.setAdapter(adapter);
        // Populate Tipo de Pessoa do Proprietario
        spinnerTipoPessoaUsuario.setSelection(getCliente().getUsuario().getTipoPessoa() - 1);
        tipoPessoaUsuarioOnItemSelectedListener(spinnerTipoPessoaUsuario);

		// Spinner Usuario Sexo
        Spinner spinnerSexoUsuario = (Spinner) findViewById(R.id.spinnerSexoUsuario);
        adapter = ArrayAdapter.createFromResource(this, R.array.sexo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSexoUsuario.setAdapter(adapter);
        
        // Proprietario
    	// RadioGroup usuário é o proprietario.
		usuarioEProprietarioOnCheckedChangeListener((RadioGroup)findViewById(R.id.groupUsuarioEProprietario));

        // Popula RadioButton Usuário é o Proprietário
        if (getCliente().isUsuarioProprietario() == Constantes.NAO){
        	((RadioButton)(findViewById(R.id.radioNao))).setChecked(true);
       
        }else if (getCliente().isUsuarioProprietario() == Constantes.SIM){
            ((RadioButton)(findViewById(R.id.radioSim))).setChecked(true);
        }
        
        if (getCliente().isUsuarioProprietario() == Constantes.NAO){
        	inflateDadosProprietario();
        }

        populateUsuario();

        //Responsavel
        // Spinner Define Responsavel
        Spinner spinnerDefineResponsavel = (Spinner) findViewById(R.id.spinnerDefineResponsavel);
        adapter = ArrayAdapter.createFromResource(this, R.array.listaResponsavel, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDefineResponsavel.setAdapter(adapter);
		
        // Popula Spinner
        spinnerDefineResponsavel.setSelection(getCliente().getTipoResponsavel());
        defineResponsavelOnItemSelectedListener(spinnerDefineResponsavel);
        
        if (getCliente().getTipoResponsavel() == Constantes.TIPO_RESPONSAVEL_OUTRO){
        	inflateDadosResponsavel();
        }
        
        // Button Save 
        final Button buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	updateClienteSelecionado();
            	
            	if (areUFsValidos()){
	             	// Verificar os campos obrigatórios
	            	if (areCamposObrigatoriosOk()){
	
	                	// Verificar se pode salvar!!!!!!
	                	
	            		getCliente().setTabSaved(true);
	        			dialogMessage = "Dados do Cliente atualizados com sucesso.";
	        	    	showDialog(Constantes.DIALOG_ID_SUCESSO);
	            	}
            	}
        	}
        });
	}

	
	public boolean areUFsValidos(){
		boolean result = true;
		String[] lista = getResources().getStringArray(R.array.listaUF);

		if (((EditText)findViewById(R.id.ufUsuario)).getText().toString().length() > 0 ){
			result = false;

			for (int i = 0; i < lista.length; i++){
	        	if (lista[i].equalsIgnoreCase(((EditText)findViewById(R.id.ufUsuario)).getText().toString())){
	                result = true;
	                break;
	        	}
	        }
			if (!result){
				dialogMessage = "UF do Usuário inválido. ";
		    	showDialog(Constantes.DIALOG_ID_ERRO);
			}
		}
		
		if (getCliente().isUsuarioProprietario() == Constantes.NAO && result){
			if (((EditText)findViewById(R.id.ufProprietario)).getText().toString().length() > 0 ){
				result = false;

				for (int i = 0; i < lista.length; i++){
		        	if (lista[i].equalsIgnoreCase(((EditText)findViewById(R.id.ufProprietario)).getText().toString())){
		                result = true;
		                break;
		        	}
		        }
				if (!result){
					dialogMessage = "UF do Proprietário inválido. ";
			    	showDialog(Constantes.DIALOG_ID_ERRO);
				}
			}
		}

		if (((Spinner)findViewById(R.id.spinnerDefineResponsavel)).getSelectedItemPosition() > 1 && result){
			if (((EditText)findViewById(R.id.ufResponsavel)).getText().toString().length() > 0 ){
				result = false;

				for (int i = 0; i < lista.length; i++){
		        	if (lista[i].equalsIgnoreCase(((EditText)findViewById(R.id.ufResponsavel)).getText().toString())){
		                result = true;
		                break;
		        	}
		        }
				if (!result){
					dialogMessage = "UF do Responsável inválido. ";
			    	showDialog(Constantes.DIALOG_ID_ERRO);
				}
			}
		}
		
		return result;
	}
	
	public boolean areCamposObrigatoriosOk(){
		boolean result = true;
		
		// Nome do Usuario
		if (((EditText)findViewById(R.id.nomeUsuario)).getText().toString().trim().compareTo("") == 0){
			dialogMessage = "Nome do usuário inválido.";
	    	showDialog(Constantes.DIALOG_ID_ERRO);
			return false;
		}
		// CPF ou CNPJ do usuario
		if (isUsuarioCpfSelected){
			
			if( (((EditText)findViewById(R.id.cpfCnpjUsuario)).getText().toString().trim().compareTo("") != 0) && 
				(!Util.getCpfUsuarioOk() || !Util.validateCpf(((EditText)findViewById(R.id.cpfCnpjUsuario)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", ""))) ){
				
				dialogMessage = "CPF do usuário inválido.";
		    	showDialog(Constantes.DIALOG_ID_ERRO);
				return false;
			}
		}else{
			
			if( (((EditText)findViewById(R.id.cpfCnpjUsuario)).getText().toString().trim().compareTo("") != 0) && 
				(!Util.getCnpjUsuarioOk() || !Util.validateCnpj(((EditText)findViewById(R.id.cpfCnpjUsuario)).getText().toString())) ){
				
				dialogMessage = "CNPJ do usuário inválido.";
		    	showDialog(Constantes.DIALOG_ID_ERRO);
				return false;
			}
		}
		
		// Dados do Responsavel, se existir.
		if ( ((Spinner)findViewById(R.id.spinnerDefineResponsavel)).getSelectedItemPosition() > 1){
			
			// Nome do Responsavel
			if (((EditText)findViewById(R.id.nomeResponsavel)).getText().toString().trim().compareTo("") == 0){
				
				dialogMessage = "Nome do responsável inválido.";
		    	showDialog(Constantes.DIALOG_ID_ERRO);
				return false;
			}
			
			// CPF ou CNPJ do Responsavel
			if (isResponsavelCpfSelected){
				if( (((EditText)findViewById(R.id.cpfCnpjResponsavel)).getText().toString().trim().compareTo("") != 0) && 
					(!Util.getCpfResponsavelOk() || !Util.validateCpf(((EditText)findViewById(R.id.cpfCnpjResponsavel)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", ""))) ){
					
					dialogMessage = "CPF do responsável inválido.";
			    	showDialog(Constantes.DIALOG_ID_ERRO);
					return false;
				}
			
			}else{
				if( (((EditText)findViewById(R.id.cpfCnpjResponsavel)).getText().toString().trim().compareTo("") != 0) &&
					(!Util.getCnpjResponsavelOk() || !Util.validateCnpj(((EditText)findViewById(R.id.cpfCnpjResponsavel)).getText().toString())) ){
					
					dialogMessage = "CNPJ do responsável inválido.";
			    	showDialog(Constantes.DIALOG_ID_ERRO);
					return false;
				}
			}
		}
		// Dados do Proprietario, se existir.
		if ( ((RadioGroup)findViewById(R.id.groupUsuarioEProprietario)).getCheckedRadioButtonId() == R.id.radioNao){
			
			// Nome do Proprietario
			if (((EditText)findViewById(R.id.nomeProprietario)).getText().toString().trim().compareTo("") == 0){
				
				dialogMessage = "Nome do proprietário inválido.";
		    	showDialog(Constantes.DIALOG_ID_ERRO);
				return false;
			}
			
			// CPF ou CNPJ do Proprietario
			if (isProprietarioCpfSelected){
				if( (((EditText)findViewById(R.id.cpfCnpjProprietario)).getText().toString().trim().compareTo("") != 0) && 
					(!Util.getCpfProprietarioOk() || !Util.validateCpf(((EditText)findViewById(R.id.cpfCnpjProprietario)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", ""))) ){
					
					dialogMessage = "CPF do proprietário inválido.";
			    	showDialog(Constantes.DIALOG_ID_ERRO);
					return false;
				}
			
			}else{
				if( (((EditText)findViewById(R.id.cpfCnpjProprietario)).getText().toString().trim().compareTo("") != 0) &&
					(!Util.getCnpjProprietarioOk() || !Util.validateCnpj(((EditText)findViewById(R.id.cpfCnpjProprietario)).getText().toString())) ){
					
					dialogMessage = "CNPJ do proprietário inválido.";
			    	showDialog(Constantes.DIALOG_ID_ERRO);
					return false;
				}
			}
		}
		return result;
	}
	
	public void updateClienteSelecionado(){
		
//		getCliente().setNomeGerenciaRegional(((EditText)(findViewById(R.id.))).getText().toString());
		   
		if ( ((RadioGroup)findViewById(R.id.groupUsuarioEProprietario)).getCheckedRadioButtonId() == R.id.radioSim){
			getCliente().setUsuarioEProprietario(String.valueOf(Constantes.SIM));
		}else{
			getCliente().setUsuarioEProprietario(String.valueOf(Constantes.NAO));

			// Caso o responsavel nao seja o usuário.
			if ( ((RadioGroup)findViewById(R.id.radioGroupTipoEnderecoProprietario)).getCheckedRadioButtonId() == R.id.radioResidencialProprietario){
				getCliente().setTipoEnderecoProprietario(String.valueOf(Constantes.IMOVEL_PROPRIETARIO_RESIDENCIAL));
			}else{
				getCliente().setTipoEnderecoProprietario(String.valueOf(Constantes.IMOVEL_PROPRIETARIO_COMERCIAL));
			}

			getCliente().getProprietario().setNome(((EditText)findViewById(R.id.nomeProprietario)).getText().toString());
			getCliente().getProprietario().setTipoPessoa(String.valueOf(((Spinner)findViewById(R.id.spinnerTipoPessoaProprietario)).getSelectedItemPosition() + 1));
			getCliente().getProprietario().setCpfCnpj(((EditText)findViewById(R.id.cpfCnpjProprietario)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""));
			getCliente().getProprietario().setRg(((EditText)findViewById(R.id.rgProprietario)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", ""));
			getCliente().getProprietario().setUf(((EditText)findViewById(R.id.ufProprietario)).getText().toString());
			getCliente().getProprietario().setTipoSexo(String.valueOf(((Spinner)findViewById(R.id.spinnerSexoProprietario)).getSelectedItemPosition()));
			getCliente().getProprietario().setTelefone(((EditText)findViewById(R.id.foneProprietario)).getText().toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""));
			getCliente().getProprietario().setCelular(((EditText)findViewById(R.id.celularProprietario)).getText().toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""));
			getCliente().getProprietario().setEmail(((EditText)findViewById(R.id.emailProprietario)).getText().toString());
			getCliente().getEnderecoProprietario().setLogradouro(((EditText)findViewById(R.id.logradouroProprietario)).getText().toString());
			getCliente().getEnderecoProprietario().setNumero(((EditText)findViewById(R.id.numeroProprietario)).getText().toString());
			getCliente().getEnderecoProprietario().setComplemento(((EditText)findViewById(R.id.complementoProprietario)).getText().toString());
			getCliente().getEnderecoProprietario().setBairro(((EditText)findViewById(R.id.bairroProprietario)).getText().toString());
			getCliente().getEnderecoProprietario().setCep(((EditText)findViewById(R.id.cepProprietario)).getText().toString().replaceAll("[-]", ""));
			getCliente().getEnderecoProprietario().setMunicipio(((EditText)findViewById(R.id.municipioProprietario)).getText().toString());
		}

		getCliente().setTipoResponsavel(String.valueOf(((Spinner)findViewById(R.id.spinnerDefineResponsavel)).getSelectedItemPosition()));
		if ( ((Spinner)findViewById(R.id.spinnerDefineResponsavel)).getSelectedItemPosition() > 1){
			
			// Caso o responsavel nao seja o usuário.
			if ( ((RadioGroup)findViewById(R.id.radioGroupTipoEnderecoResponsavel)).getCheckedRadioButtonId() == R.id.radioResidencialResponsavel){
				getCliente().setTipoEnderecoResponsavel(String.valueOf(Constantes.IMOVEL_RESPONSAVEL_RESIDENCIAL));
			}else{
				getCliente().setTipoEnderecoResponsavel(String.valueOf(Constantes.IMOVEL_RESPONSAVEL_COMERCIAL));
			}

			getCliente().getResponsavel().setNome(((EditText)findViewById(R.id.nomeResponsavel)).getText().toString());
			getCliente().getResponsavel().setTipoPessoa(String.valueOf(((Spinner)findViewById(R.id.spinnerTipoPessoaResponsavel)).getSelectedItemPosition()+1));
			getCliente().getResponsavel().setCpfCnpj(((EditText)findViewById(R.id.cpfCnpjResponsavel)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""));
			getCliente().getResponsavel().setRg(((EditText)findViewById(R.id.rgResponsavel)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", ""));
			getCliente().getResponsavel().setUf(((EditText)findViewById(R.id.ufResponsavel)).getText().toString());
			getCliente().getResponsavel().setTipoSexo(String.valueOf(((Spinner)findViewById(R.id.spinnerSexoResponsavel)).getSelectedItemPosition()));
			getCliente().getResponsavel().setTelefone(((EditText)findViewById(R.id.foneResponsavel)).getText().toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""));
			getCliente().getResponsavel().setCelular(((EditText)findViewById(R.id.celularResponsavel)).getText().toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""));
			getCliente().getResponsavel().setEmail(((EditText)findViewById(R.id.emailResponsavel)).getText().toString());
			getCliente().getEnderecoResponsavel().setLogradouro(((EditText)findViewById(R.id.logradouroResponsavel)).getText().toString());
			getCliente().getEnderecoResponsavel().setNumero(((EditText)findViewById(R.id.numeroResponsavel)).getText().toString());
			getCliente().getEnderecoResponsavel().setComplemento(((EditText)findViewById(R.id.complementoResponsavel)).getText().toString());
			getCliente().getEnderecoResponsavel().setBairro(((EditText)findViewById(R.id.bairroResponsavel)).getText().toString());
			getCliente().getEnderecoResponsavel().setCep(((EditText)findViewById(R.id.cepResponsavel)).getText().toString().replaceAll("[-]", ""));
			getCliente().getEnderecoResponsavel().setMunicipio(((EditText)findViewById(R.id.municipioResponsavel)).getText().toString());
		}
		
		getCliente().getUsuario().setNome(((EditText)findViewById(R.id.nomeUsuario)).getText().toString());
		getCliente().getUsuario().setTipoPessoa(String.valueOf(((Spinner)findViewById(R.id.spinnerTipoPessoaUsuario)).getSelectedItemPosition()+1));
		getCliente().getUsuario().setCpfCnpj(((EditText)findViewById(R.id.cpfCnpjUsuario)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", "").replaceAll("[/]", ""));
		getCliente().getUsuario().setRg(((EditText)findViewById(R.id.rgUsuario)).getText().toString().replaceAll("[-]", "").replaceAll("[.]", ""));
		getCliente().getUsuario().setUf(((EditText)findViewById(R.id.ufUsuario)).getText().toString());
		getCliente().getUsuario().setTipoSexo(String.valueOf(((Spinner)findViewById(R.id.spinnerSexoUsuario)).getSelectedItemPosition()));
		getCliente().getUsuario().setTelefone(((EditText)findViewById(R.id.foneUsuario)).getText().toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""));
		getCliente().getUsuario().setCelular(((EditText)findViewById(R.id.celularUsuario)).getText().toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[)]", ""));
		getCliente().getUsuario().setEmail(((EditText)findViewById(R.id.emailUsuario)).getText().toString());
				   
        if (lastKnownLocation != null){
            getCliente().setLatitude(String.valueOf(lastKnownLocation.getLatitude()));
    		getCliente().setLongitude(String.valueOf(lastKnownLocation.getLongitude()));
        }
		getCliente().setData(Util.formatarData(Calendar.getInstance().getTime()));
	}
	
	public void populateProprietario(){

//        if (getCliente().isUsuarioProprietario() == Constantes.NAO){
	       
        	// Nome do Usuario
			((EditText)(findViewById(R.id.nomeProprietario))).setText(getCliente().getProprietario().getNome());
	        
			// Tipo de Pessoa do Proprietario
	        if (getCliente().getProprietario().getTipoPessoa() != Constantes.NULO_INT){
	        	((Spinner)(findViewById(R.id.spinnerTipoPessoaProprietario))).setSelection(getCliente().getProprietario().getTipoPessoa() - 1);
	        }	        
	        
	        // UF do Proprietario
	        if (getCliente().getProprietario().getUf() != Constantes.NULO_STRING){
				((EditText)(findViewById(R.id.ufProprietario))).setText(getCliente().getProprietario().getUf());
	        }
	        
	        // Sexo do Proprietario
			String[] lista = getResources().getStringArray(R.array.sexo);
			for (int i = 0; i < lista.length; i++){
	        	if (lista[i].equalsIgnoreCase(getCliente().getProprietario().getTipoSexo())){
	                ((Spinner)(findViewById(R.id.spinnerSexoProprietario))).setSelection(i);
	        		break;
	        	}
	        }
	        
			// CPF / CNPJ do Proprietario
	        if (getCliente().getProprietario().getCpfCnpj() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.cpfCnpjProprietario))).setText(String.valueOf(getCliente().getProprietario().getCpfCnpj()));
	        }
	        
	        // RG do Proprietario
	        if (getCliente().getProprietario().getRg() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.rgProprietario))).setText(String.valueOf(getCliente().getProprietario().getRg()));
	        }
	        
	        // Telefone do Proprietario
	        if (getCliente().getProprietario().getTelefone() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.foneProprietario))).setText(String.valueOf(getCliente().getProprietario().getTelefone()));
	        }
	        
	        // Celular do Proprietario
	        if (getCliente().getProprietario().getCelular() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.celularProprietario))).setText(String.valueOf(getCliente().getProprietario().getCelular()));
	        }
	        
	        // Email do Proprietario
	        if (getCliente().getProprietario().getEmail() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.emailProprietario))).setText(String.valueOf(getCliente().getProprietario().getEmail()));
	        }
	        
			// Logradouro do Proprietario
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getLogradouro()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.logradouroProprietario))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getLogradouro()));
	        }
	        
	    	// Numero do Proprietario
	        if ( ((Endereco)(getCliente().getEnderecoProprietario())).getNumero() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.numeroProprietario))).setText(((Endereco)(getCliente().getEnderecoProprietario())).getNumero());
	        }
	        
	    	// Municipio do Proprietario
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getMunicipio()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.municipioProprietario))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getMunicipio()));
	        }
	        
	    	// Complemento do Proprietario
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getComplemento()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.complementoProprietario))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getComplemento()));
	        }
	        
	    	// Bairro do Proprietario
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getBairro()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.bairroProprietario))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getBairro()));
	        }
	        
	    	// CEP do Proprietario
	        if ( ((Endereco)(getCliente().getEnderecoProprietario())).getCep() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.cepProprietario))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoProprietario())).getCep()));
	        }
	        
	    	// Tipo de Endereço do Proprietario
	        if (getCliente().getTipoEnderecoProprietario() == Constantes.IMOVEL_PROPRIETARIO_RESIDENCIAL){
	        	((RadioButton)(findViewById(R.id.radioResidencialProprietario))).setChecked(true);
	       
	        }else {
	            ((RadioButton)(findViewById(R.id.radioComercialProprietario))).setChecked(true);
	        }
//        }
	}
	
	public void populateUsuario(){
		
        // Nome do Usuario
		((EditText)(findViewById(R.id.nomeUsuario))).setText(getCliente().getUsuario().getNome());
        
		// Tipo de Pessoa do Usuario
		((Spinner)(findViewById(R.id.spinnerTipoPessoaUsuario))).setSelection(getCliente().getUsuario().getTipoPessoa() - 1);
        
        // UF do Usuario
        if (getCliente().getUsuario().getUf() != Constantes.NULO_STRING){
			((EditText)(findViewById(R.id.ufUsuario))).setText(getCliente().getUsuario().getUf());
        }
        
        // Sexo do Usuario
		String[] lista = getResources().getStringArray(R.array.sexo);
		for (int i = 0; i < lista.length; i++){
        	if (lista[i].equalsIgnoreCase(getCliente().getUsuario().getTipoSexo())){
                ((Spinner)(findViewById(R.id.spinnerSexoUsuario))).setSelection(i);
        		break;
        	}
        }
        
		// CPF / CNPJ do Usuario
        if (getCliente().getUsuario().getCpfCnpj() != Constantes.NULO_STRING){
            ((EditText)(findViewById(R.id.cpfCnpjUsuario))).setText(String.valueOf(getCliente().getUsuario().getCpfCnpj()));
        }
        
        // RG do Usuario
        if (getCliente().getUsuario().getRg() != Constantes.NULO_STRING){
            ((EditText)(findViewById(R.id.rgUsuario))).setText(String.valueOf(getCliente().getUsuario().getRg()));
        }
        
        // Telefone do Usuario
        if (getCliente().getUsuario().getTelefone() != Constantes.NULO_STRING){
            ((EditText)(findViewById(R.id.foneUsuario))).setText(String.valueOf(getCliente().getUsuario().getTelefone()));
        }
        
        // Celular do Usuario
        if (getCliente().getUsuario().getCelular() != Constantes.NULO_STRING){
            ((EditText)(findViewById(R.id.celularUsuario))).setText(String.valueOf(getCliente().getUsuario().getCelular()));
        }
        
        // Email do Usuario
        if (getCliente().getUsuario().getEmail() != Constantes.NULO_STRING){
            ((EditText)(findViewById(R.id.emailUsuario))).setText(String.valueOf(getCliente().getUsuario().getEmail()));
        }
	}
		
	public void populateResponsavel(){
		
        if (getCliente().getTipoResponsavel() == Constantes.TIPO_RESPONSAVEL_OUTRO){

			// Nome do Responsavel
			((EditText)(findViewById(R.id.nomeResponsavel))).setText(getCliente().getResponsavel().getNome());
	        
			// Tipo de Pessoa do Responsavel
	        if (getCliente().getResponsavel().getTipoPessoa() != Constantes.NULO_INT){
	        	((Spinner)(findViewById(R.id.spinnerTipoPessoaResponsavel))).setSelection(getCliente().getResponsavel().getTipoPessoa() - 1);
	        }	        
	        // UF do Responsavel
	        if (getCliente().getResponsavel().getUf() != Constantes.NULO_STRING){
				((EditText)(findViewById(R.id.ufResponsavel))).setText(getCliente().getResponsavel().getUf());
	        }
	        
	        // Sexo do Responsavel
			String[] lista = getResources().getStringArray(R.array.sexo);
			for (int i = 0; i < lista.length; i++){
	        	if (lista[i].equalsIgnoreCase(getCliente().getResponsavel().getTipoSexo())){
	                ((Spinner)(findViewById(R.id.spinnerSexoResponsavel))).setSelection(i);
	        		break;
	        	}
	        }
	        
			// CPF / CNPJ do Responsavel
	        if (getCliente().getResponsavel().getCpfCnpj() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.cpfCnpjResponsavel))).setText(String.valueOf(getCliente().getResponsavel().getCpfCnpj()));
	        }
	        
	        // RG do Responsavel
	        if (getCliente().getResponsavel().getRg() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.rgResponsavel))).setText(String.valueOf(getCliente().getResponsavel().getRg()));
	        }
	        
	        // Telefone do Responsavel
	        if (getCliente().getResponsavel().getTelefone() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.foneResponsavel))).setText(String.valueOf(getCliente().getResponsavel().getTelefone()));
	        }
	        
	        // Celular do Responsavel
	        if (getCliente().getResponsavel().getCelular() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.celularResponsavel))).setText(String.valueOf(getCliente().getResponsavel().getCelular()));
	        }
	        
	        // Email do Responsavel
	        if (getCliente().getResponsavel().getEmail() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.emailResponsavel))).setText(String.valueOf(getCliente().getResponsavel().getEmail()));
	        }
	        
			// Logradouro do Responsavel
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getLogradouro()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.logradouroResponsavel))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getLogradouro()));
	        }
	        
	    	// Numero do Responsavel
	        if ( ((Endereco)(getCliente().getEnderecoResponsavel())).getNumero() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.numeroResponsavel))).setText(((Endereco)(getCliente().getEnderecoResponsavel())).getNumero());
	        }
	        
	    	// Municipio do Responsavel
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getMunicipio()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.municipioResponsavel))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getMunicipio()));
	        }
	        
	    	// Complemento do Responsavel
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getComplemento()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.complementoResponsavel))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getComplemento()));
	        }
	        
	    	// Bairro do Responsavel
	        if ( String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getBairro()) != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.bairroResponsavel))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getBairro()));
	        }
	        
	    	// CEP do Responsavel
	        if ( ((Endereco)(getCliente().getEnderecoResponsavel())).getCep() != Constantes.NULO_STRING){
	            ((EditText)(findViewById(R.id.cepResponsavel))).setText(String.valueOf(((Endereco)(getCliente().getEnderecoResponsavel())).getCep()));
	        }
	        
	    	// Tipo de Endereço do Responsavel
	        if (getCliente().getTipoEnderecoResponsavel() == Constantes.IMOVEL_RESPONSAVEL_RESIDENCIAL){
	        	((RadioButton)(findViewById(R.id.radioResidencialResponsavel))).setChecked(true);
	       
	        }else {
	            ((RadioButton)(findViewById(R.id.radioComercialResponsavel))).setChecked(true);
	        }
		}
	}
	
	public void usuarioEProprietarioOnCheckedChangeListener (RadioGroup mRadioGroup){
        
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
 		        LinearLayout submitAdditionalLayout = (LinearLayout)findViewById(R.id.linearLayoutUsuarioEProprietario);
	            
		        if (submitAdditionalLayout != null){
	            	submitAdditionalLayout.removeAllViews();
	            }
	            
            	if (checkedId == R.id.radioNao){
		        	inflateDadosProprietario();
            	}
            }
        });
	}
	
	public void tipoPessoaResponsavelOnItemSelectedListener (Spinner spinnerTipoPessoaResponsavel){

		spinnerTipoPessoaResponsavel.setOnItemSelectedListener(new OnItemSelectedListener () {
        	
    		public void onItemSelected(AdapterView parent, View v, int position, long id){
        		setResponsavelCpfOrCnpjTextField(position + 1);
        	}
    		
    		public void onNothingSelected(AdapterView<?> arg0) {}
    	});
       	setResponsavelCpfOrCnpjTextField(getCliente().getResponsavel().getTipoPessoa());

	}	
	
	public void tipoPessoaUsuarioOnItemSelectedListener (Spinner spinnerTipoPessoaUsuario){

		spinnerTipoPessoaUsuario.setOnItemSelectedListener(new OnItemSelectedListener () {
        	
    		public void onItemSelected(AdapterView parent, View v, int position, long id){
        		setUsuarioCpfOrCnpjTextField(position + 1);
        	}
    		
    		public void onNothingSelected(AdapterView<?> arg0) {}
    	});
       	setUsuarioCpfOrCnpjTextField(getCliente().getUsuario().getTipoPessoa());

	}	
	
	public void tipoPessoaProprietarioOnItemSelectedListener (Spinner spinnerTipoPessoaProprietario){
	   
		spinnerTipoPessoaProprietario.setOnItemSelectedListener(new OnItemSelectedListener () {
			
			public void onItemSelected(AdapterView parent, View v, int position, long id){
				setProprietarioCpfOrCnpjTextField(position + 1);
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		setProprietarioCpfOrCnpjTextField(getCliente().getProprietario().getTipoPessoa());
	}
	
	public void defineResponsavelOnItemSelectedListener (Spinner spinnerDefineResponsavel){
	
		spinnerDefineResponsavel.setOnItemSelectedListener(new OnItemSelectedListener () {
    		
    	    public void onItemSelected(AdapterView parent, View v, int position, long id){
    			if(consideraEventoItemSelectedListenerResponsavel){
    			
    				if (position == Constantes.TIPO_RESPONSAVEL_OUTRO){
    					inflateDadosResponsavel();
    				}else{
    			        LinearLayout submitScoreLayout = (LinearLayout)findViewById(R.id.linearLayoutResponsavelPagamento);
    		            if (submitScoreLayout != null){
    		            	submitScoreLayout.removeAllViews();
    		            }
    				}
    	    
    			}else{
    				consideraEventoItemSelectedListenerResponsavel = true;
    			}
    		}
    		
    		public void onNothingSelected(AdapterView<?> arg0) {
    		}
    	});
	}
	
	public void inflateDadosProprietario(){

		LinearLayout submitScoreLayout = (LinearLayout)findViewById(R.id.linearLayoutUsuarioEProprietario);
        if (submitScoreLayout != null){
        	submitScoreLayout.removeAllViews();
        }

		LayoutInflater inflater = getLayoutInflater();
        submitScoreLayout.addView(inflater.inflate(R.layout.dadosproprietario, null));
    
    	Util.addTextChangedListenerCepMask((EditText)findViewById(R.id.cepProprietario));
    	Util.addTextChangedListenerPhoneMask((EditText)findViewById(R.id.foneProprietario));
    	Util.addTextChangedListenerPhoneMask((EditText)findViewById(R.id.celularProprietario));
    	Util.addTextChangedListenerCpfCnpjVerifierAndMask((EditText)findViewById(R.id.cpfCnpjProprietario), Constantes.PESSOA_PROPRIETARIO);

    	// Verifica após preencher o campo se esta válido
    	((EditText)findViewById(R.id.cpfCnpjProprietario)).setOnFocusChangeListener(new OnFocusChangeListener() {          
    		public void onFocusChange(View v, boolean hasFocus) {
    			
    			if(!hasFocus){
    				
    				if (isTipoCpf(Constantes.PESSOA_PROPRIETARIO)){
    					
    					if ( (((EditText)findViewById(R.id.cpfCnpjProprietario)).getText().toString().trim().compareTo("") != 0) && 
    						 (!Util.getCpfProprietarioOk()) ){
    						
    						dialogMessage = " CPF do proprietário inválido. ";
    				    	showDialog(Constantes.DIALOG_ID_ERRO);
    					}
    				
    				}else{
    					
    					if ( (((EditText)findViewById(R.id.cpfCnpjProprietario)).getText().toString().trim().compareTo("") != 0) && 
    						 (!Util.getCnpjProprietarioOk()) ){
    						
    						dialogMessage = " CNPJ do proprietário inválido. ";
    				    	showDialog(Constantes.DIALOG_ID_ERRO);
    					}
    				}
    			}
    		}
    	});
        // Spinner Tipo Pessoa Proprietario
        Spinner spinnerTipoPessoaProprietario = (Spinner) findViewById(R.id.spinnerTipoPessoaProprietario);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.tipoPessoa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoPessoaProprietario.setAdapter(adapter);
        // Populate Tipo de Pessoa do Proprietario
        if (getCliente().getProprietario().getTipoPessoa() != Constantes.NULO_INT){
            spinnerTipoPessoaProprietario.setSelection(getCliente().getProprietario().getTipoPessoa() - 1);
        }

        tipoPessoaProprietarioOnItemSelectedListener(spinnerTipoPessoaProprietario);
        
        // Spinner Sexo Proprietario
        Spinner spinnerSexoProprietario = (Spinner) findViewById(R.id.spinnerSexoProprietario);
        adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.sexo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSexoProprietario.setAdapter(adapter);
        
		populateProprietario();

	}

	public void inflateDadosResponsavel(){
        LinearLayout submitScoreLayout = (LinearLayout)findViewById(R.id.linearLayoutResponsavelPagamento);
        if (submitScoreLayout != null){
        	submitScoreLayout.removeAllViews();
        }
        
		// Display a messagebox.
//	    Toast.makeText(getBaseContext(),"Preencha os dados do responsável pelo pagamento.",Toast.LENGTH_SHORT).show();

	    LayoutInflater inflater = getLayoutInflater();
	    submitScoreLayout.addView(inflater.inflate(R.layout.dadosresponsavel, null));

		Util.addTextChangedListenerPhoneMask((EditText)findViewById(R.id.foneResponsavel));
		Util.addTextChangedListenerPhoneMask((EditText)findViewById(R.id.celularResponsavel));
		Util.addTextChangedListenerCpfCnpjVerifierAndMask((EditText)findViewById(R.id.cpfCnpjResponsavel), Constantes.PESSOA_RESPONSAVEL);

    	// Verifica após preencher o campo se esta válido
    	((EditText)findViewById(R.id.cpfCnpjResponsavel)).setOnFocusChangeListener(new OnFocusChangeListener() {          
    		public void onFocusChange(View v, boolean hasFocus) {
    			if(!hasFocus){
    				
    				if (isTipoCpf(Constantes.PESSOA_RESPONSAVEL)){
    					
    					if ( (((EditText)findViewById(R.id.cpfCnpjResponsavel)).getText().toString().trim().compareTo("") != 0) && 
    						 (!Util.getCpfResponsavelOk()) ){
    						
    						dialogMessage = " CPF do responsável inválido. ";
    				    	showDialog(Constantes.DIALOG_ID_ERRO);
    					}
    				
    				}else{
    					
    					if ( (((EditText)findViewById(R.id.cpfCnpjResponsavel)).getText().toString().trim().compareTo("") != 0) && 
    						 (!Util.getCnpjResponsavelOk()) ){
    						
    						dialogMessage = " CNPJ do responsável inválido. ";
    				    	showDialog(Constantes.DIALOG_ID_ERRO);
    					}
    				}
    			}
    		}
    	});
	    // Spinner Tipo Pessoa Responsavel
	    Spinner spinnerTipoPessoaResponsavel = (Spinner) findViewById(R.id.spinnerTipoPessoaResponsavel);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.tipoPessoa, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinnerTipoPessoaResponsavel.setAdapter(adapter);
        // Populate Tipo de Pessoa do Responsavel
        if (getCliente().getResponsavel().getTipoPessoa() != Constantes.NULO_INT){
        	spinnerTipoPessoaResponsavel.setSelection(getCliente().getResponsavel().getTipoPessoa() - 1);
        }
	    tipoPessoaResponsavelOnItemSelectedListener(spinnerTipoPessoaResponsavel);
	    
        // Spinner Sexo Responsavel
	    Spinner spinnerSexoResponsavel = (Spinner) findViewById(R.id.spinnerSexoResponsavel);
	    adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.sexo, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinnerSexoResponsavel.setAdapter(adapter);

		populateResponsavel();
	}
	
	public void setProprietarioCpfOrCnpjTextField(int position){
		if(consideraEventoItemSelectedListenerCpfCnpjProprietario){
			
			TextView txtView = (TextView)findViewById(R.id.txtCpfCnpjProprietario);
			if (position == Constantes.TIPO_PESSOA_FISICA){
	            if (txtView != null){
	            	txtView.setText("CPF:");
	            	isProprietarioCpfSelected = true;
	            }
			
			}else {    
	            if (txtView != null){
	            	txtView.setText("CNPJ:");
	            	isProprietarioCpfSelected = false;
	            }
			}

			if (position == getCliente().getProprietario().getTipoPessoa()){
                ((EditText)(findViewById(R.id.cpfCnpjProprietario))).setText(String.valueOf(getCliente().getProprietario().getCpfCnpj()));
    		}else{
            	((EditText)findViewById(R.id.cpfCnpjProprietario)).setText("");
    		}
    
		}else{
			consideraEventoItemSelectedListenerCpfCnpjProprietario = true;
		}
	}
	
	public void setUsuarioCpfOrCnpjTextField(int position){
		if(consideraEventoItemSelectedListenerCpfCnpjUsuario){
			
			TextView txtView = (TextView)findViewById(R.id.txtCpfCnpjUsuario);
			if (position == Constantes.TIPO_PESSOA_FISICA){
	            if (txtView != null){
	            	txtView.setText("CPF:");
	            	isUsuarioCpfSelected = true;
	            }
			
			}else {    
	            if (txtView != null){
	            	txtView.setText("CNPJ:");
	            	isUsuarioCpfSelected = false;
	            }
			}

			if (position == getCliente().getUsuario().getTipoPessoa()){
                ((EditText)(findViewById(R.id.cpfCnpjUsuario))).setText(String.valueOf(getCliente().getUsuario().getCpfCnpj()));
    		}else{
            	((EditText)findViewById(R.id.cpfCnpjUsuario)).setText("");
    		}
    
		}else{
			consideraEventoItemSelectedListenerCpfCnpjUsuario = true;
		}
	}
	
	public void setResponsavelCpfOrCnpjTextField(int position){
		if(consideraEventoItemSelectedListenerCpfCnpjResponsavel){
			
			TextView txtView = (TextView)findViewById(R.id.txtCpfCnpjResponsavel);
			if (position == Constantes.TIPO_PESSOA_FISICA){
	            if (txtView != null){
	            	txtView.setText("CPF:");
	            	isResponsavelCpfSelected = true;

	            }
			
			}else {    
	            if (txtView != null){
	            	txtView.setText("CNPJ:");
	            	isResponsavelCpfSelected = false;
	            }
			}
			
    		if (position == getCliente().getResponsavel().getTipoPessoa()){
                ((EditText)(findViewById(R.id.cpfCnpjResponsavel))).setText(String.valueOf(getCliente().getResponsavel().getCpfCnpj()));
    		}else{
            	((EditText)findViewById(R.id.cpfCnpjResponsavel)).setText("");
    		}
    
		}else{
			consideraEventoItemSelectedListenerCpfCnpjResponsavel = true;
		}
	}
	
	public static boolean isTipoCpf(int pessoa){
    	boolean result = false;
		if (pessoa == Constantes.PESSOA_PROPRIETARIO){
			result = isProprietarioCpfSelected;
    	
    	}else if (pessoa == Constantes.PESSOA_USUARIO){
    		result = isUsuarioCpfSelected;
    	
    	}else if (pessoa == Constantes.PESSOA_RESPONSAVEL){
    		result = isResponsavelCpfSelected;
    	}
		return result;
	}
	
	public Cliente getCliente(){
		return Controlador.getInstancia().getClienteSelecionado();
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) {
	        
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AlertDialog.Builder builder;
	  
		if (id == Constantes.DIALOG_ID_SUCESSO || id == Constantes.DIALOG_ID_ERRO || id == Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO){
			
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
			
		}else if (id == Constantes.DIALOG_ID_CONFIRM_BACK){
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
	    			finish();
	        	}
	        });
	        
	        AlertDialog passwordDialog = builder.create();
	        return passwordDialog;
		    
		}
        return null;
	}

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
		((TextView)findViewById(R.id.txtValorLatitude)).setText(String.valueOf(location.getLatitude()));
		((TextView)findViewById(R.id.txtValorLongitude)).setText(String.valueOf(location.getLongitude()));
	}

	
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