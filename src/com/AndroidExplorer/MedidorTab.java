package com.AndroidExplorer;

import java.util.Calendar;
import java.util.List;

import business.Controlador;
import model.Medidor;
import util.Constantes;
import util.Util;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
 
public class MedidorTab extends Fragment {
	
	private static View view;
	private String dialogMessage = null;
	private List<String> listCaixaProtecao;
	private List<String> listMarcaHidrometro;
	private List<String> listCapacidadeHidrometro;
	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;
	private static boolean numeroMedidorOk = false;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		numeroMedidorOk = false;
		view = inflater.inflate(R.layout.medidortab, container, false);
		
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

    	// RadioGroup - Possui Hidrômetro.
    	RadioGroup radioGroupPossuiHidrometro = (RadioGroup) view.findViewById(R.id.radioGroupPossuiHidrometro);
		possuiHidrometroOnCheckedChangeListener(radioGroupPossuiHidrometro);
        
	      // Popula RadioButton - Possui Hidrometro
        if (getMedidor().getPossuiMedidor() == Constantes.NAO){
        	((RadioButton)(view.findViewById(R.id.tipoMedicaoRadioNao))).setChecked(true);
       
        }else if (getMedidor().getPossuiMedidor() == Constantes.SIM){
            ((RadioButton)(view.findViewById(R.id.tipoMedicaoRadioSim))).setChecked(true);
        }
        
        // Button Save 
        final Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	if (!isNumeroMedidorValido()){
		        	dialogMessage = "Por favor, informe o número do hidrômetro ou selecione a anormalidade correspondente.";
                    showNotifyDialog(R.drawable.aviso, "Erro:", dialogMessage, Constantes.DIALOG_ID_ERRO);
		
            	}else{
            		
	    			if ( !Util.allowPopulateDados()){
						
		            	if (!checkChangeNumeroMedidor()){
		            		numeroMedidorOk = true;
		            	}
		
		            	if (!numeroMedidorOk){
		            	    dialogMessage = "Houve alteração no número do hidrômetro. Por favor informe novamente para confirmação.";
        					showCompleteDialog(R.drawable.aviso, "Confirmação:", dialogMessage, Constantes.DIALOG_ID_CONFIRMA_MUDANCA);
		            	}
		            		            	
	    			}else{
	            		numeroMedidorOk = true;
	    			}
	
	    			if (numeroMedidorOk){
		            	updateMedidorSelecionado();
		            	
		         		getMedidor().setTabSaved(true);
		         		Toast.makeText(getActivity(), "Dados do Medidor atualizados com sucesso.", 5).show();
		         		
		         		if(Controlador.getInstancia().getImovelSelecionado().getImovelStatus() != Constantes.IMOVEL_A_SALVAR){
        					Controlador.getInstancia().getCadastroDataManipulator().salvarMedidor();
        				}
	            	}
            	}
            }
        });
	}
	
	public boolean isNumeroMedidorValido(){
		boolean result = true;

		int codigoAnormalidade = ((MainTab)getActivity()).getCodigoAnormalidade();
		
		if (((RadioGroup)view.findViewById(R.id.radioGroupPossuiHidrometro)).getCheckedRadioButtonId() == R.id.tipoMedicaoRadioSim &&
		   ((EditText)view.findViewById(R.id.numeroHidrometro)).getText().toString().length() == 0 && 
		   codigoAnormalidade != Constantes.ANORMALIDADE_HIDR_NAO_LOCALIZADO &&
		   codigoAnormalidade != Constantes.ANORMALIDADE_HIDR_SEM_IDENTIFICACAO){

			Log.i("TESTE", String.valueOf(codigoAnormalidade));

			result = false;
		}
		return result;
	}
	
	public boolean checkChangeNumeroMedidor(){
		boolean result = false;

		if (((RadioGroup)view.findViewById(R.id.radioGroupPossuiHidrometro)).getCheckedRadioButtonId() == R.id.tipoMedicaoRadioSim &&
			 getMedidor().getNumeroHidrometro().compareTo(((EditText)view.findViewById(R.id.numeroHidrometro)).getText().toString()) != 0){
			
			result = true;
		}
		return result;
	}

	public void updateMedidorSelecionado(){
		
		if ( ((RadioGroup)view.findViewById(R.id.radioGroupPossuiHidrometro)).getCheckedRadioButtonId() == R.id.tipoMedicaoRadioNao){
			getMedidor().setPossuiMedidor(String.valueOf(Constantes.NAO));
		}else{
			getMedidor().setPossuiMedidor(String.valueOf(Constantes.SIM));
			getMedidor().setNumeroHidrometro(((EditText)view.findViewById(R.id.numeroHidrometro)).getText().toString());
			
			String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_CAPACIDADE_HIDROMETRO, ((Spinner)view.findViewById(R.id.spinnerCapacidadeHidrometro)).getSelectedItem().toString());
			getMedidor().setCapacidade(codigo);

			codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_MARCA_HIDROMETRO, ((Spinner)view.findViewById(R.id.spinnerMarcaHidrometro)).getSelectedItem().toString());
			getMedidor().setMarca(codigo);
			
			codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_PROTECAO_HIDROMETRO, ((Spinner)view.findViewById(R.id.spinnerCaixaProtecao)).getSelectedItem().toString());
			getMedidor().setTipoCaixaProtecao(codigo);

	        if (lastKnownLocation != null) {
				getMedidor().setLatitude(String.valueOf(lastKnownLocation.getLatitude()));
				getMedidor().setLongitude(String.valueOf(lastKnownLocation.getLongitude()));
	        }
			getMedidor().setData(Util.formatarData(Calendar.getInstance().getTime()));
		}
	}
	
	public void possuiHidrometroOnCheckedChangeListener (RadioGroup radioGroupPossuiHidrometro){
        
		radioGroupPossuiHidrometro.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
 		        LinearLayout submitAdditionalLayout = (LinearLayout)view.findViewById(R.id.linearLayoutDadosHidrometro);
	            
		        if (submitAdditionalLayout != null){
	            	submitAdditionalLayout.removeAllViews();
	            }
	            
            	if (checkedId == R.id.tipoMedicaoRadioSim){
		            LayoutInflater inflater = getActivity().getLayoutInflater();
		            submitAdditionalLayout.addView(inflater.inflate(R.layout.dadoshidrometro, null));

		        	// popula o endereço do hidrometro, caso exista
		            populateDadosHidrometro();
		            
		            
		            // Spinner Capacidade Hidrômetro
		            Spinner spinnerCapacidadeHidrometro = (Spinner) view.findViewById(R.id.spinnerCapacidadeHidrometro);
		            listCapacidadeHidrometro = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_CAPACIDADE_HIDROMETRO);
		            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listCapacidadeHidrometro);
		            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		            spinnerCapacidadeHidrometro.setAdapter(adapter);
		    		// Popula Spinner Capacidade Hidrômetro
		    		String descricaoCapacidadeHidrometro = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_CAPACIDADE_HIDROMETRO, String.valueOf(getMedidor().getCapacidade()));
		    		if (descricaoCapacidadeHidrometro != null){
		    			for (int i = 0; i < listCapacidadeHidrometro.size(); i++){
		    	        	if (listCapacidadeHidrometro.get(i).equalsIgnoreCase(descricaoCapacidadeHidrometro)){
		    	        		spinnerCapacidadeHidrometro.setSelection(i);
		    	        		break;
		    	        	}
		    	        }
		    		}

		            // Spinner Marca Hidrômetro
		            Spinner spinnerMarcaHidrometro = (Spinner) view.findViewById(R.id.spinnerMarcaHidrometro);
		            listMarcaHidrometro = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_MARCA_HIDROMETRO);
		            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listMarcaHidrometro);
		            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		            spinnerMarcaHidrometro.setAdapter(adapter);
		    		// Popula Spinner Marca Hidrômetro
		    		String descricaoMarcaHidrometro = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_MARCA_HIDROMETRO, String.valueOf(getMedidor().getMarca()));
		    		if (descricaoMarcaHidrometro != null){
		    			for (int i = 0; i < listMarcaHidrometro.size(); i++){
		    	        	if (listMarcaHidrometro.get(i).equalsIgnoreCase(descricaoMarcaHidrometro)){
		    	        		spinnerMarcaHidrometro.setSelection(i);
		    	        		break;
		    	        	}
		    	        }
		    		}

		            // Spinner Caixa de Proteção
		            Spinner spinnerCaixaProtecao = (Spinner) view.findViewById(R.id.spinnerCaixaProtecao);
		            listCaixaProtecao = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_PROTECAO_HIDROMETRO);
		            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listCaixaProtecao);
		            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		            spinnerCaixaProtecao.setAdapter(adapter);
		    		// Popula Spinner Caixa de Proteção
		    		String descricaoCaixaProtecao = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_PROTECAO_HIDROMETRO, String.valueOf(getMedidor().getTipoCaixaProtecao()));
		    		if (descricaoCaixaProtecao != null){
		    			for (int i = 0; i < listCaixaProtecao.size(); i++){
		    	        	if (listCaixaProtecao.get(i).equalsIgnoreCase(descricaoCaixaProtecao)){
		    	        		spinnerCaixaProtecao.setSelection(i);
		    	        		break;
		    	        	}
		    	        }
		    		}
            	}
            }
        });

	}
	
	public void populateDadosHidrometro(){

		// Número do Hidrometro
        if ( String.valueOf(getMedidor().getNumeroHidrometro()) != Constantes.NULO_STRING){
            ((EditText)(view.findViewById(R.id.numeroHidrometro))).setText(String.valueOf(getMedidor().getNumeroHidrometro()));
        }
	}

	public Medidor getMedidor(){
		return Controlador.getInstancia().getMedidorSelecionado();
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
		Toast.makeText( getActivity(),"GPS ligado",Toast.LENGTH_SHORT).show();
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	public static void setChangesConfirmed(){
    	if (!numeroMedidorOk){
    		numeroMedidorOk = true;
    		((EditText)(view.findViewById(R.id.numeroHidrometro))).setText("");
    	}
	}

	void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.setTargetFragment(this, Constantes.FRAGMENT_ID_MEDIDOR);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
	
	void showCompleteDialog(int iconId, String title, String message, int messageType) {
		CompleteAlertDialogFragment newFragment = CompleteAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.setTargetFragment(this, Constantes.FRAGMENT_ID_MEDIDOR);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

}