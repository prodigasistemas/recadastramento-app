package com.AndroidExplorer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.Constantes;
import util.Util;
import model.Imovel;
import model.Servicos;
import business.Controlador;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
 
public class ServicosTab extends Fragment{
    
	private static View view;
	Spinner spinnerLigacaoAgua;
	Spinner spinnerLigacaoEsgoto;
	Spinner spinnerLocalInstalacaoRamal;
	private String dialogMessage = null;
	private List<String> listLigacaoAgua;
	private List<String> listLigacaoEsgoto;
	private List<String> listLocalInstalacaoRamal;
	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;
	private static boolean ligacaoAguaOk = false;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		ligacaoAguaOk = false;
		
		view = inflater.inflate(R.layout.servicotab, container, false);
		
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

        
        // Spinner Tipo de Ligação de água
        spinnerLigacaoAgua = (Spinner) view.findViewById(R.id.spinnerLigacaoAgua);
        listLigacaoAgua = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_SITUACAO_LIGACAO_AGUA);
        listLigacaoAgua.add(0, "");
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listLigacaoAgua);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLigacaoAgua.setAdapter(adapter);

        // populate Tipo de Ligação de água
//		String descricaoLigacaoAgua = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_AGUA, String.valueOf(getServicos().getTipoLigacaoAgua()));
//		if (descricaoLigacaoAgua != null){
//			for (int i = 0; i < listLigacaoAgua.size(); i++){
//	        	if (listLigacaoAgua.get(i).equalsIgnoreCase(descricaoLigacaoAgua)){
//	        		spinnerLigacaoAgua.setSelection(i);
//	        		break;
//	        	}
//	        }
//		}

        // Spinner Tipo de Ligação de Esgoto
        spinnerLigacaoEsgoto = (Spinner) view.findViewById(R.id.spinnerLigacaoEsgoto);
        listLigacaoEsgoto = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLigacaoEsgoto.setAdapter(adapter);
        spinnerLigacaoEsgoto.setSelection(getServicos().getTipoLigacaoEsgoto());
        
        // populate Tipo de Ligação de Esgoto
		String descricaoLigacaoEsgoto = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO, String.valueOf(getServicos().getTipoLigacaoEsgoto()));
		if (descricaoLigacaoEsgoto != null){
			for (int i = 0; i < listLigacaoEsgoto.size(); i++){
	        	if (listLigacaoEsgoto.get(i).equalsIgnoreCase(descricaoLigacaoEsgoto)){
	        		spinnerLigacaoEsgoto.setSelection(i);
	        		break;
	        	}
	        }
		}
		
        // Spinner Local de Instalação do Ramal
        spinnerLocalInstalacaoRamal = (Spinner) view.findViewById(R.id.spinnerLocalizacaoPontoServico);

        listLocalInstalacaoRamal = new ArrayList<String>();
        listLocalInstalacaoRamal = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_LOCAL_INSTALACAO_RAMAL);
        listLocalInstalacaoRamal.add(0, "");
        
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listLocalInstalacaoRamal);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalInstalacaoRamal.setAdapter(adapter);

        // populate Local de Instalação do Ramal
		String descricaoLocalInstalacaoRamal = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_LOCAL_INSTALACAO_RAMAL, String.valueOf(getServicos().getLocalInstalacaoRamal()));
		if (descricaoLocalInstalacaoRamal != null){
			for (int i = 0; i < listLocalInstalacaoRamal.size(); i++){
	        	if (listLocalInstalacaoRamal.get(i).equalsIgnoreCase(descricaoLocalInstalacaoRamal)){
	        		spinnerLocalInstalacaoRamal.setSelection(i);
	        		break;
	        	}else{
	        		spinnerLocalInstalacaoRamal.setSelection(0);
	        	}
	        }
		}

		// Button Save 
        final Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

				if (((Spinner)view.findViewById(R.id.spinnerLigacaoAgua)).getSelectedItemPosition() == 0){
		        	dialogMessage = "Por favor, informe tipo de ligação de água.";
			        showNotifyDialog(R.drawable.aviso, "Mensagem:", dialogMessage, Constantes.DIALOG_ID_ERRO);

				}else{					

	    			if ( !Util.allowPopulateDados()){
						
		            	if (!checkChangeLigacaoAgua()){
		            		ligacaoAguaOk = true;
		            	}
		
		            	if (!ligacaoAguaOk){
		            	    dialogMessage = "Houve alteração nos dados de ligação de água. Por favor informe novamente para confirmação.";
        					showCompleteDialog(R.drawable.aviso, "Confirmação:", dialogMessage, Constantes.DIALOG_ID_CONFIRMA_MUDANCA);
		            	}
		            		            	
	    			}else{
	            		ligacaoAguaOk = true;
	    			}
	
	            }
				
				if (((Spinner)view.findViewById(R.id.spinnerLocalizacaoPontoServico)).getSelectedItemPosition() == 0) {
                    dialogMessage = "Por favor, informe a localização do ponto de serviço.";
                    showNotifyDialog(R.drawable.aviso, "Mensagem:", dialogMessage, Constantes.DIALOG_ID_ERRO);
                    ligacaoAguaOk = false;
                }

                if (ligacaoAguaOk){
                    updateServicoSelecionado();
                    getServicos().setTabSaved(true);
                    Toast.makeText(getActivity(), "Dados do Serviço atualizados com sucesso.", 5).show();
                }
            }
        });
	}
	
	public boolean checkChangeLigacaoAgua(){
		boolean result = false;
		String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_AGUA, ((Spinner)view.findViewById(R.id.spinnerLigacaoAgua)).getSelectedItem().toString());

		if (codigo.length() > 0 && getServicos().getTipoLigacaoAgua() != Integer.parseInt(codigo)){
			result = true;
		}
		return result;
	}

	public void updateServicoSelecionado(){
		
		String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_AGUA, ((Spinner)view.findViewById(R.id.spinnerLigacaoAgua)).getSelectedItem().toString());
		getServicos().setTipoLigacaoAgua(codigo);

		codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO, ((Spinner)view.findViewById(R.id.spinnerLigacaoEsgoto)).getSelectedItem().toString());
		getServicos().setTipoLigacaoEsgoto(codigo);
		
		codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_LOCAL_INSTALACAO_RAMAL, ((Spinner)view.findViewById(R.id.spinnerLocalizacaoPontoServico)).getSelectedItem().toString());
		getServicos().setLocalInstalacaoRamal(codigo);
		
        if (lastKnownLocation != null) {
        	getServicos().setLatitude(String.valueOf(lastKnownLocation.getLatitude()));
        	getServicos().setLongitude(String.valueOf(lastKnownLocation.getLongitude()));
        }

        getServicos().setData(Util.formatarData(Calendar.getInstance().getTime()));
	}
	
	public Servicos getServicos(){
		return Controlador.getInstancia().getServicosSelecionado();
	}
	
	public Imovel getImovel(){
		return Controlador.getInstancia().getImovelSelecionado();
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
    	if (!ligacaoAguaOk){
    		ligacaoAguaOk = true;
    		((Spinner)view.findViewById(R.id.spinnerLigacaoAgua)).setSelection(0);
    	}
	}
	
	void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.setTargetFragment(this, Constantes.FRAGMENT_ID_SERVICOS);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
	
	void showCompleteDialog(int iconId, String title, String message, int messageType) {
		CompleteAlertDialogFragment newFragment = CompleteAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.setTargetFragment(this, Constantes.FRAGMENT_ID_SERVICOS);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

}