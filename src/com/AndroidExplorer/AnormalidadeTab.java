package com.AndroidExplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.AnormalidadeImovel;
import util.Constantes;
import util.Util;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.telephony.CellLocation;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import background.EnviarCadastroOnlineThread;
import business.Controlador;
 
public class AnormalidadeTab extends Fragment implements LocationListener {

	private View view;
	static boolean consideraEventoItemSelectedListenerCodigoAnormalidade;
	List<String> listAnormalidades;
    static final int TAKE_PICTURE_1 = 1;
    static final int TAKE_PICTURE_2 = 2;
    static Bitmap bPicture1;
    static Bitmap bPicture2;
    EditText codigoAnormalidade;
	private String dialogMessage = null;
	static boolean foto1Taken = false;
	static boolean foto2Taken = false;

	public LocationManager mLocManager;
	Location lastKnownLocation;
	private String provider;

	private static EnviarCadastroOnlineThread progThread;
	private static int increment= 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.anormalidadetab, container, false);
		
		// Define a imagem de fundo de acordo com a orientacao do dispositivo
	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
	    	view.setBackgroundResource(R.drawable.fundocadastro);
	    else
	    	view.setBackgroundResource(R.drawable.fundocadastro);

        instanciate();
        return view;
	}

	public void instanciate(){
        
		foto1Taken = false;
        foto2Taken = false;

        /* Use the LocationManager class to obtain GPS locations */
        mLocManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        	mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        
        boolean enabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabled){
	        dialogMessage = " GPS está desligado. Por favor, ligue-o para continuar o cadastro. ";
	        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
        }
        
        if(mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        	mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		provider = mLocManager.getBestProvider(criteria, false);
		lastKnownLocation = mLocManager.getLastKnownLocation(provider);
    	CellLocation.requestLocationUpdate();

        // Button Atualizar 
        final Button buttonAtualizar = (Button)view.findViewById(R.id.buttonAtualizar);
        buttonAtualizar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	CellLocation.requestLocationUpdate();
                lastKnownLocation = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null){
        			((TextView)view.findViewById(R.id.txtValorLatitude)).setText(String.valueOf(lastKnownLocation.getLatitude()));
        			((TextView)view.findViewById(R.id.txtValorLongitude)).setText(String.valueOf(lastKnownLocation.getLongitude()));

                }else if (getAnormalidadeImovel().getLatitude() != Constantes.NULO_DOUBLE && getAnormalidadeImovel().getLatitude() != 0 &&
                		getAnormalidadeImovel().getLongitude() != 0 && getAnormalidadeImovel().getLongitude() != Constantes.NULO_DOUBLE ){
        			
                	((TextView)view.findViewById(R.id.txtValorLatitude)).setText(String.valueOf(getAnormalidadeImovel().getLatitude()));
        			((TextView)view.findViewById(R.id.txtValorLongitude)).setText(String.valueOf(getAnormalidadeImovel().getLongitude()));

                } else{
        			((TextView)view.findViewById(R.id.txtValorLatitude)).setText("----");
        			((TextView)view.findViewById(R.id.txtValorLongitude)).setText("----");
                	
                }
            }
        });
        
        if (getAnormalidadeImovel().getLatitude() != Constantes.NULO_DOUBLE && getAnormalidadeImovel().getLatitude() != 0 &&
        		getAnormalidadeImovel().getLongitude() != 0 && getAnormalidadeImovel().getLongitude() != Constantes.NULO_DOUBLE ){

        	((TextView)view.findViewById(R.id.txtValorLatitude)).setText(String.valueOf(getAnormalidadeImovel().getLatitude()));
			((TextView)view.findViewById(R.id.txtValorLongitude)).setText(String.valueOf(getAnormalidadeImovel().getLongitude()));
    		

        }else if (lastKnownLocation != null){
			((TextView)view.findViewById(R.id.txtValorLatitude)).setText(String.valueOf(lastKnownLocation.getLatitude()));
			((TextView)view.findViewById(R.id.txtValorLongitude)).setText(String.valueOf(lastKnownLocation.getLongitude()));

        }else{
			((TextView)view.findViewById(R.id.txtValorLatitude)).setText("----");
			((TextView)view.findViewById(R.id.txtValorLongitude)).setText("----");
        	
        }        
       
		// Spinner Tipo de Anormalidade
        Spinner spinnerTipoAnormalidade = (Spinner) view.findViewById(R.id.spinnerTipoAnormalidade);
        
        listAnormalidades = new ArrayList<String>();
        listAnormalidades = Controlador.getInstancia().getCadastroDataManipulator().selectAnormalidades();
        
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listAnormalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAnormalidade.setAdapter(adapter);
        spinnerTipoAnormalidade.setOnItemSelectedListener(new OnItemSelectedListener () {

        	
			public void onItemSelected(AdapterView parent, View v, int position, long id){
 				String codigo = Controlador.getInstancia().getCadastroDataManipulator().selectCodigoByDescricaoFromTable(Constantes.TABLE_ANORMALIDADE, ((Spinner)view.findViewById(R.id.spinnerTipoAnormalidade)).getSelectedItem().toString());
 
 				if (codigo.compareTo(((EditText)view.findViewById(R.id.codigoAnormalidade)).getText().toString()) != 0 &&
 					((Spinner)(view.findViewById(R.id.spinnerTipoAnormalidade))).getSelectedItemPosition() != 0){
 					
 					consideraEventoItemSelectedListenerCodigoAnormalidade = true;  
 					codigoAnormalidade.setText(codigo);
	        	}
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		// Codigo de Anormalidade
        codigoAnormalidade = (EditText)view.findViewById(R.id.codigoAnormalidade);
        codigoAnormalidade.addTextChangedListener(new TextWatcher() {

    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}  
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	      
    			// Quando o texto é alterado o onTextChange é chamado. Essa flag evita a chamada infinita desse método  
    			if (consideraEventoItemSelectedListenerCodigoAnormalidade){
    				consideraEventoItemSelectedListenerCodigoAnormalidade = false;  
    				return;  
    			}  
    	      
 				String descricaoAnormalidade = Controlador.getInstancia().getCadastroDataManipulator().selectDescricaoByCodigoFromTable(Constantes.TABLE_ANORMALIDADE, s.toString());
 				if (descricaoAnormalidade != null){
 					for (int i = 0; i < listAnormalidades.size(); i++){
 			        	if (listAnormalidades.get(i).equalsIgnoreCase(descricaoAnormalidade)){
 			                ((Spinner)(view.findViewById(R.id.spinnerTipoAnormalidade))).setSelection(i);
 			        		break;
 			        	}else{
 			                ((Spinner)(view.findViewById(R.id.spinnerTipoAnormalidade))).setSelection(0); 			        		
 			        	}
 			        }
 				}
    		}  
    		
    	    public void afterTextChanged(Editable s) {}  
		});
        
        // Button Picture 1 
        final Button buttonPicture1 = (Button)view.findViewById(R.id.buttonPicture1);
        buttonPicture1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
    		    
                startCamera(TAKE_PICTURE_1);
            }
        });
        
        // Button Picture 2 
        final Button buttonPicture2 = (Button)view.findViewById(R.id.buttonPicture2);
        buttonPicture2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
    		    
                startCamera(TAKE_PICTURE_2);
            }
        });
        
        // Button Save 
        final Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	updateAnormalidadeSelecionada();
            	
            	// Verificar se pode salvar!!!!!!
             	// Verificar os campos obrigatórios
            	
            	if (areOtherTabsOk()){
            		        	    	
            		if(isLocationValid()){
            			// verificar se nao está criando imóvel duplicado.
            			
            			if (((Spinner)(view.findViewById(R.id.spinnerTipoAnormalidade))).getSelectedItemPosition() > 0) {
            				if (Controlador.getInstancia().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_NOVO) {
            					Controlador.getInstancia().getImovelSelecionado().setOperacoTipo(Constantes.OPERACAO_CADASTRO_NOVO);
            					Controlador.getInstancia().getImovelSelecionado().setImovelStatus(String.valueOf(Constantes.IMOVEL_NOVO_COM_ANORMALIDADE));
            				} else { 
            					Controlador.getInstancia().getImovelSelecionado().setOperacoTipo(Constantes.OPERACAO_CADASTRO_ALTERADO);
            					Controlador.getInstancia().getImovelSelecionado().setImovelStatus(String.valueOf(Constantes.IMOVEL_SALVO_COM_ANORMALIDADE));
            				}
            				
            			} else if (Controlador.getInstancia().getImovelSelecionado().getImovelStatus() != Constantes.IMOVEL_NOVO) {
            				Controlador.getInstancia().getImovelSelecionado().setOperacoTipo(Constantes.OPERACAO_CADASTRO_ALTERADO);
            				Controlador.getInstancia().getImovelSelecionado().setImovelStatus(String.valueOf(Constantes.IMOVEL_SALVO));
            			}
            			
            			// Cadastro configurado como Nao Transmitido
            			Controlador.getInstancia().getImovelSelecionado().setImovelEnviado(String.valueOf(Constantes.NAO));
            			
            			Controlador.getInstancia().getCadastroDataManipulator().salvarCliente();
            			Controlador.getInstancia().getCadastroDataManipulator().salvarServico();
            			Controlador.getInstancia().getCadastroDataManipulator().salvarImovel();
            			Controlador.getInstancia().getCadastroDataManipulator().salvarMedidor();
            			Controlador.getInstancia().getCadastroDataManipulator().salvarAnormalidadeImovel();
            			((MainTab)getActivity()).setTabColor();
            			
            			getAnormalidadeImovel().setTabSaved(true);
            			dialogMessage = " Todas as informações do cadastro foram atualizadas com sucesso. ";
            			showNotifyDialog(R.drawable.save, "", dialogMessage, Constantes.DIALOG_ID_CONFIRMA_IMOVEL_SALVO);
            		}
            	}else{
            		
            		if (!Controlador.getInstancia().getClienteSelecionado().isTabSaved()){
               			dialogMessage = " Atualize os dados do cliente antes de finalizar. ";
    			        showNotifyDialog(R.drawable.aviso, "Mensagem:", dialogMessage, Constantes.DIALOG_ID_ERRO);
           			
            		}else if (!Controlador.getInstancia().getImovelSelecionado().isTabSaved()){
               			dialogMessage = " Atualize os dados do imóvel antes de finalizar. ";
    			        showNotifyDialog(R.drawable.aviso, "Mensagem:", dialogMessage, Constantes.DIALOG_ID_ERRO);
            			
            		}else if (!Controlador.getInstancia().getServicosSelecionado().isTabSaved()){
               			dialogMessage = " Atualize os dados de serviço antes de finalizar. ";
    			        showNotifyDialog(R.drawable.aviso, "Mensagem:", dialogMessage, Constantes.DIALOG_ID_ERRO);
            			
            		}else if (!Controlador.getInstancia().getMedidorSelecionado().isTabSaved()){
               			dialogMessage = " Atualize os dados do medidor antes de finalizar. ";
    			        showNotifyDialog(R.drawable.aviso, "Mensagem:", dialogMessage, Constantes.DIALOG_ID_ERRO);
            		}
            	}
            }
        });
        
        // Populate
        // Anormalidade
        codigoAnormalidade.setText(String.valueOf(getAnormalidadeImovel().getCodigoAnormalidade()));
        
        // Comentario
        ((EditText)view.findViewById(R.id.editComentario)).setText(getAnormalidadeImovel().getComentario());
        
        
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        	
	        // Foto 1
	        if (getAnormalidadeImovel().getFoto1().trim().compareTo("") != 0  && getAnormalidadeImovel().getFoto1().trim().length() > 0){
	        	
	   		
	        	try {
		    		bPicture1 = Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory() + "/" + getAnormalidadeImovel().getFoto1())) );
		    	} catch (FileNotFoundException e) {
		    		e.printStackTrace();
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	}  
	
	//    		((ImageView)view.findViewById(R.id.imageView1)).setImageBitmap(Bitmap.createScaledBitmap(bPicture1, 80, 80, false));
	        	((ImageView)view.findViewById(R.id.imageView1)).setImageBitmap(bPicture1);
	        	((ImageView)view.findViewById(R.id.imageView1)).invalidate(); 
	        
	        }else if (getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_1.jpg").exists()){
	    		
	        	try {
	    			bPicture1 = Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_1.jpg")) );
	    		} catch (FileNotFoundException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}  
	
	//    		((ImageView)view.findViewById(R.id.imageView1)).setImageBitmap(Bitmap.createScaledBitmap(bPicture1, 80, 80, false));
	        	((ImageView)view.findViewById(R.id.imageView1)).setImageBitmap(bPicture1);
	        	((ImageView)view.findViewById(R.id.imageView1)).invalidate(); 
	        	
	        }
	        
	        // Foto 2
	        if (getAnormalidadeImovel().getFoto2().trim().compareTo("") != 0 && getAnormalidadeImovel().getFoto2().trim().length() > 0 ){
	
	    		try {
	    			bPicture2 = Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory() + "/" + getAnormalidadeImovel().getFoto2())) );
	    		} catch (FileNotFoundException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}  
	
	        	((ImageView)view.findViewById(R.id.imageView2)).setImageBitmap(bPicture2);
	        	((ImageView)view.findViewById(R.id.imageView2)).invalidate(); 
	        
	        }else if (getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg").exists()){
	        	
	    		try {
	    			bPicture2 = Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg")) );
	    		} catch (FileNotFoundException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}  
	
	        	((ImageView)view.findViewById(R.id.imageView2)).setImageBitmap(bPicture2);
	        	((ImageView)view.findViewById(R.id.imageView2)).invalidate(); 
	        }
        
        }else {
        	Toast.makeText(getActivity(), " Cartão de memória não está disponível ", Toast.LENGTH_SHORT).show();;
        }
	}
	
    public void startCamera(int fotoNumber) {
        Log.d("CAMERA", "Starting camera on the phone...");
//        String fileName = "testphoto.jpg";
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, fileName);
//        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        imageUri = getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        if (fotoNumber == TAKE_PICTURE_1){
        	
        	if (getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_1.jpg").exists()){
        		getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_1.jpg").delete();
        	}
        	
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_1.jpg")));
 
        }else{
        	if (getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg").exists()){
        		getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg").delete();
        	}
        	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg")));
        }

        startActivityForResult(intent, fotoNumber);
    }

    private File getFotoFile(String rotaDirectory, String fileName){  

    	final File fotoPath = new File(rotaDirectory);  
    	if(!fotoPath.exists()){  
    		fotoPath.mkdir();  
    	}  
    	return new File(fotoPath, fileName);  
	}  

    private File getFotoFile(String filePath){  

    	final File fotoFile = new File(filePath);  
    	if(!fotoFile.exists()){  
    		return null;  
    	}  
    	return fotoFile;  
	}  

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        
//    	if (data != null){
//    		Log.i("DATA NOT NULL", "DATA NOT NULL!!!");
//    	}
//    	
//    	if (resultCode == RESULT_OK) {  
//
//	    	switch(requestCode) {
//
//	    	case TAKE_PICTURE_1: 
//
//	             try {  
//	             
//	            	 bPicture1 = Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() +"_1.jpg")) );  
//	                 foto1Taken = true;
//	            	 
//	             } catch (FileNotFoundException e) {  
//	               e.printStackTrace();  
//	             } catch (IOException e) {  
//	               e.printStackTrace();  
//	             }  
//
//	             ((ImageView)view.findViewById(R.id.imageView1)).setImageBitmap(bPicture1);
//	        	((ImageView)view.findViewById(R.id.imageView1)).invalidate(); 
//	    		break;
//
//	    	case TAKE_PICTURE_2:
//
//	             try {  
//	             
//	            	 bPicture2 = Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg")) );  
//	                 foto2Taken = true;
//
//	             } catch (FileNotFoundException e) {  
//	               e.printStackTrace();  
//	             } catch (IOException e) {  
//	               e.printStackTrace();  
//	             }  
//
//	    		((ImageView)view.findViewById(R.id.imageView2)).setImageBitmap(bPicture2);
//	        	((ImageView)view.findViewById(R.id.imageView2)).invalidate(); 
//	    		break;
//	    	}
//    	}
//    	
//    	removeImage(getLastImageId());
//    }
    
    private int getLastImageId(){
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        Cursor imageCursor = getActivity().managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        if(imageCursor.moveToFirst()){
            int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            Log.d(TAG, "getLastImageId::id " + id);
//            Log.d(TAG, "getLastImageId::path " + fullPath);
            imageCursor.close();
            return id;
        }else{
            return 0;
        }
    }
    
    private void removeImage(int id) {
    	   ContentResolver cr = getActivity().getContentResolver();
    	   cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{ Long.toString(id) } );
    	}

    public long getImovelSelecionadoId(){
    	return Controlador.getInstancia().getImovelSelecionado().getImovelId();
    }

    public void updateAnormalidadeSelecionada(){

		if (codigoAnormalidade.getText().toString().length() > 0){
			getAnormalidadeImovel().setCodigoAnormalidade(Integer.parseInt(codigoAnormalidade.getText().toString()));
		}else{
			getAnormalidadeImovel().setCodigoAnormalidade(0);			
		}
		
		getAnormalidadeImovel().setComentario(((EditText)view.findViewById(R.id.editComentario)).getText().toString());
		
		if (foto1Taken && getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_1.jpg").exists()){
			getAnormalidadeImovel().setFoto1(Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_1.jpg");
		}
		
		if (foto2Taken && getFotoFile(Util.getRetornoRotaDirectory(), Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg").exists()){
			getAnormalidadeImovel().setFoto2(Controlador.getInstancia().getImovelSelecionado().getMatricula() + "_2.jpg");	
		}

		if (!((TextView)view.findViewById(R.id.txtValorLatitude)).getText().toString().equalsIgnoreCase("----")){
			getAnormalidadeImovel().setLatitude(((TextView)view.findViewById(R.id.txtValorLatitude)).getText().toString());
		}

		if (!((TextView)view.findViewById(R.id.txtValorLongitude)).getText().toString().equalsIgnoreCase("----")){
			getAnormalidadeImovel().setLongitude(((TextView)view.findViewById(R.id.txtValorLongitude)).getText().toString());
		}

		getAnormalidadeImovel().setData(Util.formatarData(Calendar.getInstance().getTime()));
    }
	
	public AnormalidadeImovel getAnormalidadeImovel(){
		return Controlador.getInstancia().getAnormalidadeImovelSelecionado();
	}

	
	public boolean areOtherTabsOk(){
		boolean result = true;
    	
    	// Somente verifica as outras tabs se não houver nenhuma anormalidade.
		if (((Spinner)(view.findViewById(R.id.spinnerTipoAnormalidade))).getSelectedItemPosition() == 0) {
    	
			if ( Controlador.getInstancia().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_A_SALVAR ||
				 Controlador.getInstancia().getImovelSelecionado().getImovelStatus() == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE ){
				
				if ( !Controlador.getInstancia().getClienteSelecionado().isTabSaved() ||
		        	 !Controlador.getInstancia().getImovelSelecionado().isTabSaved()  ||
		        	 !Controlador.getInstancia().getClienteSelecionado().isTabSaved() ||
		        	 !Controlador.getInstancia().getClienteSelecionado().isTabSaved() ){
		    	
		    		result = false;
		    	}
			}
    	}
		return result;
	}
	
	public boolean isLocationValid(){
		boolean result = true;

        boolean enabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled){
	        dialogMessage = " GPS está desligado. Por favor, ligue-o para finalizar o cadastro";
	        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO_GPS_DESLIGADO);
	        result = false;
        }	    
		
		// Descartar validacao para Emulador Android.
		if (!Build.BRAND.startsWith("generic") || !Build.DEVICE.startsWith("generic")){
		
	    	if (  ((TextView)view.findViewById(R.id.txtValorLongitude)).getText().toString().equalsIgnoreCase("----")
	    		|| ((TextView)view.findViewById(R.id.txtValorLatitude)).getText().toString().equalsIgnoreCase("----")){
				
	    		dialogMessage = "Por favor, atualize a Localização Geográfica antes de salvar.";
		        showNotifyDialog(R.drawable.aviso, "Mensagem:", dialogMessage, Constantes.DIALOG_ID_ERRO);
		    	result = false;
	    	}
		}
		return result;
	}
	
	public int getCodigoAnormalidade(){
        return Integer.valueOf(((EditText)view.findViewById(R.id.codigoAnormalidade)).getText().toString());
	}

	void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
		newFragment.setTargetFragment(this, Constantes.FRAGMENT_ID_ANORMALIDADE);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

	/* Request updates at startup */
	@Override
	public void onResume() {
		super.onResume();
		mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
	}

	public void onLocationChanged(Location location) {}

	public void onProviderDisabled(String provider) {}

	public void onProviderEnabled(String provider) {}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
}