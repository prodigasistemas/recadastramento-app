package com.AndroidExplorer;

import java.util.ArrayList;

import util.Constantes;
import background.CarregarRotaThread;
import background.GerarArquivoCompletoThread;
import background.GerarArquivoParcialThread;
import business.Controlador;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
public class MenuPrincipal extends FragmentActivity {
	
	static final int MENU_LISTA_CADASTROS = 0;
	static final int MENU_INFO = 1;
	static final int MENU_CONSULTA = 2;
	static final int MENU_ARQUIVO_COMPLETO = 3;
	static final int MENU_CADASTROS_CONCLUIDOS = 4;
	static final int MENU_FINALIZAR = 5;
	static final int MENU_RELATORIO = 6;
	static final int MENU_NOVO_ROTEIRO = 7;

	private ProgressDialog progDialog;
	private GerarArquivoCompletoThread progThread;
	private GerarArquivoParcialThread parcialThread;
	private String dialogMessage = null;
	private static int increment= 0;
	private int numeroImoveis;
	
    //---the images to display---
    Integer[] imageIDs = {
            R.drawable.menu_cadastros,
            R.drawable.menu_info,
            R.drawable.menu_consulta,
            R.drawable.menu_arquivo_completo,
            R.drawable.menu_cadastros_concluidos,
            R.drawable.menu_finalizar,
            R.drawable.menu_relatorio,
            R.drawable.menu_novo_roteiro
    };

    //---the texts to display---
    Integer[] TextIDs = {
            R.string.menu_cadastros,
            R.string.menu_info,
            R.string.menu_consulta,
            R.string.menu_arquivo_completo,
            R.string.menu_cadastros_concluidos,
            R.string.menu_finalizar,
            R.string.menu_relatorio,
            R.string.menu_novo_roteiro
    };

    @Override    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 
        instanciate();
	}
    
//	protected void onNewIntent(Intent intent) {
//		  super.onNewIntent(intent);
//		  setIntent(intent);//must store the new intent unless getIntent() will return the old one.
//		  instanciate();
//		}
//
	public void instanciate(){

		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(this));
   	 
		gridView.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView parent, View v, int position, long id){        
        		String text = parent.getItemAtPosition(position).toString();
				
            	if (position == MENU_LISTA_CADASTROS){
					Intent myIntent = new Intent(getApplicationContext(),ListaImoveis.class);
	        		startActivity(myIntent);
				
            	}else if (position == MENU_INFO){
					Intent myIntent = new Intent(getApplicationContext(),TelaInformacoes.class);
	        		startActivity(myIntent);

            	}else if (position == MENU_CONSULTA){
					Intent myIntent = new Intent(getApplicationContext(),Consulta.class);
	        		startActivity(myIntent);            		
					
            	}else if (position == MENU_ARQUIVO_COMPLETO){
                    if (statusOk()){
                		showDialog(Constantes.DIALOG_ID_GERAR_ARQUIVO_COMPLETO + increment);
        	    	
        	    	}else{
            		
        	    		dialogMessage = "Roteiro ainda não concluído. Não foi possível gerar arquivo de retorno Completo.";
            	        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO);
        	    	}
            		
            	}else if (position == MENU_CADASTROS_CONCLUIDOS){
					
            	}else if (position == MENU_FINALIZAR){
            		if (statusOk()){
                		showDialog(Constantes.DIALOG_ID_GERAR_ARQUIVO_PARCIAL + increment);
        	    	
        	    	}else{
            		
        	    		dialogMessage = "Roteiro ainda não concluído. Não foi possível gerar arquivo de retorno parcial.";
            	        showNotifyDialog(R.drawable.aviso, "Alerta!", dialogMessage, Constantes.DIALOG_ID_ERRO);
        	    	}
            	}else if (position == MENU_RELATORIO){
					Intent myIntent = new Intent(getApplicationContext(),TelaRelatorio.class);
	        		startActivity(myIntent);
            		
            	}else if (position == MENU_NOVO_ROTEIRO){
        	    	showDialog(Constantes.DIALOG_ID_CLEAN_DB);
				}
            }

			private boolean statusOk() {
				boolean statusOk = false;
    	    	
                // 	Verifica se todos os imoveis já foram visitados.
    	    	ArrayList<String> listStatusImoveis = (ArrayList)Controlador.getInstancia().getCadastroDataManipulator().selectStatusImoveis(null);
    	    	
    	    	for (int i=0; i < listStatusImoveis.size(); i++){
    	    		if (Integer.parseInt(listStatusImoveis.get(i)) != Constantes.IMOVEL_A_SALVAR ){
    	    			statusOk = true;
    	    			numeroImoveis++;
    				}
    	    	}
				return statusOk;
			}
        });        
	}
	
    // Handler on the main (UI) thread that will receive messages from the second thread and update the progress.
	final Handler handlerArquivoComleto = new Handler() {
		public void handleMessage(Message msg) {
			
			// Get the current value of the variable total from the message data and update the progress bar.
			int totalArquivoCompleto = msg.getData().getInt("arquivoCompleto" + String.valueOf(increment));
			progDialog.setProgress(totalArquivoCompleto);
			
			if (totalArquivoCompleto >= numeroImoveis || progThread.getCustomizedState() == CarregarRotaThread.DONE) {
				
				dismissDialog(Constantes.DIALOG_ID_GERAR_ARQUIVO_COMPLETO + increment);
				
				dialogMessage = "Arquivo de retorno COMPLETO gerado com sucesso!";
				showNotifyDialog(R.drawable.save, "", dialogMessage, Constantes.DIALOG_ID_SUCESSO);
				increment = increment + 5;
			}
		}
	};
	
	final Handler handlerArquivoParcial = new Handler() {
        public void handleMessage(Message msg) {
            
        	// Get the current value of the variable total from the message data and update the progress bar.
        	int totalArquivoParcial = msg.getData().getInt("arquivoParcial" + String.valueOf(increment));
            progDialog.setProgress(totalArquivoParcial);
            
			if (totalArquivoParcial >= numeroImoveis || parcialThread.getCustomizedState() == CarregarRotaThread.DONE) {

				dismissDialog(Constantes.DIALOG_ID_GERAR_ARQUIVO_PARCIAL + increment);

				dialogMessage = "Arquivo de retorno PARCIAL gerado com sucesso!";
				showNotifyDialog(R.drawable.save, "", dialogMessage, Constantes.DIALOG_ID_SUCESSO);
				increment = increment + 5;
			}
         }
    };


	@Override
	protected Dialog onCreateDialog(final int id) {
		LayoutInflater inflater;
		AlertDialog.Builder builder;
		
	    if (id == Constantes.DIALOG_ID_CLEAN_DB){
	        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        final View layoutConfirmationDialog = inflater.inflate(R.layout.remove_data, (ViewGroup) findViewById(R.id.root));
	  
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
	        		EditText senha = (EditText) layoutConfirmationDialog.findViewById(R.id.txtSenha);
	        		if (senha.getText().toString().equals("apagar")) {
	
		        		removeDialog(id);
		        		Controlador.getInstancia().finalizeDataManipulator();
		        		Controlador.getInstancia().deleteDatabase();
		        		Controlador.getInstancia().setPermissionGranted(false);
		        		Controlador.getInstancia().initiateDataManipulator(layoutConfirmationDialog.getContext());
		        		
		        	    Toast.makeText(getBaseContext(),"Todas as informações foram apagadas com sucesso!",Toast.LENGTH_LONG).show();
	
	        		    Intent myIntent = new Intent(layoutConfirmationDialog.getContext(), Fachada.class);
	        	        startActivity(myIntent);
	        		} else {
	        			AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipal.this);
	        	        builder.setTitle("Erro");
	        	        builder.setMessage("Senha inválida");

	        	        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
						});
	        	        
	        	        builder.show();

	        		}
	        	}
	        });
	        
	        AlertDialog passwordDialog = builder.create();
	        return passwordDialog;
	    
	    }else if (id == Constantes.DIALOG_ID_GERAR_ARQUIVO_COMPLETO + increment) {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

		    	progDialog = new ProgressDialog(this);
	            progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            progDialog.setCancelable(false);
	            progDialog.setMessage("Por favor, espere enquanto o Arquivo de Retorno Completo está sendo gerado...");
	            progDialog.setMax(numeroImoveis);
	            progThread = new GerarArquivoCompletoThread(handlerArquivoComleto, this, increment);
	            progThread.start();
	            return progDialog;

            }else{
                Toast.makeText(getBaseContext(), "Cartão de memória não está disponível!", Toast.LENGTH_SHORT).show();
            }
	    }else if (id == Constantes.DIALOG_ID_GERAR_ARQUIVO_PARCIAL + increment) {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

		    	progDialog = new ProgressDialog(this);
	            progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            progDialog.setCancelable(false);
	            progDialog.setMessage("Por favor, espere enquanto o Arquivo de Retorno Parcial está sendo gerado...");
	            progDialog.setMax(numeroImoveis);
	            parcialThread = new GerarArquivoParcialThread(handlerArquivoParcial, this, increment);
	            parcialThread.start();
	            return progDialog;

            }else{
                Toast.makeText(getBaseContext(), "Cartão de memória não está disponível!", Toast.LENGTH_SHORT).show();
            }
	    }
	    return null;
	}
 
	void showNotifyDialog(int iconId, String title, String message, int messageType) {
		NotifyAlertDialogFragment newFragment = NotifyAlertDialogFragment.newInstance(iconId, title, message, messageType);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    public class ImageAdapter extends BaseAdapter 
    {
        private Context context;
		public static final int ACTIVITY_CREATE = 10;

        public ImageAdapter(Context c){
            context = c;
        }
 
        //returns the number of images
        public int getCount() {
            return imageIDs.length;
        }
 
        //returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
 
        public long getItemId(int position) {
            return position;
        }
 
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if(convertView==null){
				
				LayoutInflater inflator = getLayoutInflater();
				view = inflator.inflate(R.layout.icon, null);
							
			}else{
				view = convertView;
			}
			
			TextView textView = (TextView)view.findViewById(R.id.icon_text);
			textView.setText(TextIDs[position]);
			ImageView imageView = (ImageView)view.findViewById(R.id.icon_image);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(5, 5, 5, 5);
			imageView.setImageResource(imageIDs[position]);

			return view;
		}
    }    
}