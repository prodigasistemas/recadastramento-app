package com.AndroidExplorer;

import java.util.Calendar;
import java.util.List;

import util.Constantes;
import util.Util;
import model.Servicos;
import business.Controlador;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
 
public class ServicosTab extends Activity implements LocationListener{
    
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
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicotab);
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

        
        // Spinner Tipo de Ligação de água
        spinnerLigacaoAgua = (Spinner) findViewById(R.id.spinnerLigacaoAgua);
        listLigacaoAgua = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_SITUACAO_LIGACAO_AGUA);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, listLigacaoAgua);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLigacaoAgua.setAdapter(adapter);

        // populate Tipo de Ligação de água
		String descricaoLigacaoAgua = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_AGUA, String.valueOf(getServicos().getTipoLigacaoAgua()));
		if (descricaoLigacaoAgua != null){
			for (int i = 0; i < listLigacaoAgua.size(); i++){
	        	if (listLigacaoAgua.get(i).equalsIgnoreCase(descricaoLigacaoAgua)){
	        		spinnerLigacaoAgua.setSelection(i);
	        		break;
	        	}
	        }
		}

        // Spinner Tipo de Ligação de Esgoto
        spinnerLigacaoEsgoto = (Spinner) findViewById(R.id.spinnerLigacaoEsgoto);
        listLigacaoEsgoto = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO);
        adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO));
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
        spinnerLocalInstalacaoRamal = (Spinner) findViewById(R.id.spinnerLocalizacaoPontoServico);
        listLocalInstalacaoRamal = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_LOCAL_INSTALACAO_RAMAL);
        adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, listLocalInstalacaoRamal);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalInstalacaoRamal.setAdapter(adapter);

        // populate Local de Instalação do Ramal
		String descricaoLocalInstalacaoRamal = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_LOCAL_INSTALACAO_RAMAL, String.valueOf(getServicos().getLocalInstalacaoRamal()));
		if (descricaoLocalInstalacaoRamal != null){
			for (int i = 0; i < listLocalInstalacaoRamal.size(); i++){
	        	if (listLocalInstalacaoRamal.get(i).equalsIgnoreCase(descricaoLocalInstalacaoRamal)){
	        		spinnerLocalInstalacaoRamal.setSelection(i);
	        		break;
	        	}
	        }
		}

		// Button Save 
        final Button buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	updateServicoSelecionado();
            	
//            	if (!((TextView)findViewById(R.id.txtValorLatitude)).getText().toString().equalsIgnoreCase("----")){
            		getServicos().setTabSaved(true);
        			dialogMessage = " Dados do Serviço atualizados com sucesso. ";
        	    	showDialog(Constantes.DIALOG_ID_SUCESSO);
            		
//            	}else{
//        			dialogMessage = "Atualize a Localização Geográfica antes de salvar.";
//        	    	showDialog(Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
//            	}
            }
        });
	}
	
	public void updateServicoSelecionado(){
		
		String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_AGUA, ((Spinner)findViewById(R.id.spinnerLigacaoAgua)).getSelectedItem().toString());
		getServicos().setTipoLigacaoAgua(codigo);

		codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO, ((Spinner)findViewById(R.id.spinnerLigacaoEsgoto)).getSelectedItem().toString());
		getServicos().setTipoLigacaoEsgoto(codigo);
		
        if (lastKnownLocation != null) {
        	getServicos().setLatitude(String.valueOf(lastKnownLocation.getLatitude()));
        	getServicos().setLongitude(String.valueOf(lastKnownLocation.getLongitude()));
        }

        getServicos().setData(Util.formatarData(Calendar.getInstance().getTime()));
	}
	
	public Servicos getServicos(){
		return Controlador.getInstancia().getServicosSelecionado();
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
	
	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		mLocManager.removeUpdates(this);
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
}