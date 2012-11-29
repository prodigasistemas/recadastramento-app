package com.AndroidExplorer;

import java.util.Calendar;
import java.util.List;

import business.Controlador;
import model.Medidor;
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
import android.widget.RadioGroup.OnCheckedChangeListener;
 
public class MedidorTab extends Activity implements LocationListener {
	
	private String dialogMessage = null;
	private List<String> listCaixaProtecao;
	private List<String> listMarcaHidrometro;
	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medidortab);
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

    	// RadioGroup proprietario reside no imovel.
    	RadioGroup radioGroupPossuiHidrometro = (RadioGroup) findViewById(R.id.radioGroupPossuiHidrometro);
		possuiHidrometroOnCheckedChangeListener(radioGroupPossuiHidrometro);
        
	      // Popula RadioButton - Possui Hidrometro
        if (getMedidor().getPossuiMedidor() == Constantes.NAO){
        	((RadioButton)(findViewById(R.id.tipoMedicaoRadioNao))).setChecked(true);
       
        }else if (getMedidor().getPossuiMedidor() == Constantes.SIM){
            ((RadioButton)(findViewById(R.id.tipoMedicaoRadioSim))).setChecked(true);
        }
        
        // Button Save 
        final Button buttonSave = (Button)findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	updateMedidorSelecionado();
            	
            	// Verificar se pode salvar!!!!!!
             	// Verificar os campos obrigatórios
            	
         		getMedidor().setTabSaved(true);
         		Toast.makeText(MedidorTab.this, "Dados do Medidor atualizados com sucesso.", 5).show();
//    			dialogMessage = " Dados do Medidor atualizados com sucesso. ";
//    	    	showDialog(Constantes.DIALOG_ID_SUCESSO);
            }
        });
	}
	
	public void updateMedidorSelecionado(){
		
		if ( ((RadioGroup)findViewById(R.id.radioGroupPossuiHidrometro)).getCheckedRadioButtonId() == R.id.tipoMedicaoRadioNao){
			getMedidor().setPossuiMedidor(String.valueOf(Constantes.NAO));
		}else{
			getMedidor().setPossuiMedidor(String.valueOf(Constantes.SIM));
			getMedidor().setNumeroHidrometro(((EditText)findViewById(R.id.numeroHidrometro)).getText().toString());
			getMedidor().setCapacidade(((EditText)findViewById(R.id.capacidadeHidrometro)).getText().toString());

			String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_MARCA_HIDROMETRO, ((Spinner)findViewById(R.id.spinnerMarcaHidrometro)).getSelectedItem().toString());
			getMedidor().setMarca(codigo);
			
			codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_PROTECAO_HIDROMETRO, ((Spinner)findViewById(R.id.spinnerCaixaProtecao)).getSelectedItem().toString());
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
 		        LinearLayout submitAdditionalLayout = (LinearLayout)findViewById(R.id.linearLayoutDadosHidrometro);
	            
		        if (submitAdditionalLayout != null){
	            	submitAdditionalLayout.removeAllViews();
	            }
	            
            	if (checkedId == R.id.tipoMedicaoRadioSim){
		            LayoutInflater inflater = getLayoutInflater();
		            submitAdditionalLayout.addView(inflater.inflate(R.layout.dadoshidrometro, null));

		        	// popula o endereço do hidrometro, caso exista
		            populateDadosHidrometro();

		            // Spinner Marca Hidrômetro
		            Spinner spinnerMarcaHidrometro = (Spinner) findViewById(R.id.spinnerMarcaHidrometro);
		            listMarcaHidrometro = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_MARCA_HIDROMETRO);
		            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, listMarcaHidrometro);
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
		            Spinner spinnerCaixaProtecao = (Spinner) findViewById(R.id.spinnerCaixaProtecao);
		            listCaixaProtecao = Controlador.getInstancia().getCadastroDataManipulator().selectDescricoesFromTable(Constantes.TABLE_PROTECAO_HIDROMETRO);
		            adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, listCaixaProtecao);
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
            ((EditText)(findViewById(R.id.numeroHidrometro))).setText(String.valueOf(getMedidor().getNumeroHidrometro()));
        }
        
		// Capacidade do Hidrometro
        if ( getMedidor().getCapacidade() != Constantes.NULO_DOUBLE){
            ((EditText)(findViewById(R.id.capacidadeHidrometro))).setText(String.valueOf(getMedidor().getCapacidade()));
        }
	}

	public Medidor getMedidor(){
		return Controlador.getInstancia().getMedidorSelecionado();
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
	        		MainTab.indiceNovoImovel = null;
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
		lastKnownLocation = location;
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