package com.AndroidExplorer;

import java.util.ArrayList;

import util.Constantes;

import business.Controlador;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TelaRelatorio extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.roteirorelatorio);
    	
    	int numeroCadastros = Controlador.getInstancia().getCadastroDataManipulator().getNumeroCadastros();
    	ArrayList<Integer> listStatus = (ArrayList<Integer>) Controlador.getInstancia().getCadastroDataManipulator().selectNumeroTodosStatusImoveis();
    	
    	((ProgressBar)findViewById(R.id.progressVisitados)).setMax(numeroCadastros);
    	((ProgressBar)findViewById(R.id.progressVisitados)).setProgress(listStatus.get(Constantes.IMOVEL_SALVO));
    	((TextView)findViewById(R.id.txtNumeroVisitados)).setText(String.valueOf(listStatus.get(Constantes.IMOVEL_SALVO)));
    	
    	((ProgressBar)findViewById(R.id.progressNaoVisitados)).setMax(numeroCadastros);
    	((ProgressBar)findViewById(R.id.progressNaoVisitados)).setProgress(listStatus.get(Constantes.IMOVEL_A_SALVAR));
    	((TextView)findViewById(R.id.txtNumeroNaoVisitados)).setText(String.valueOf(listStatus.get(Constantes.IMOVEL_A_SALVAR)));

    	((ProgressBar)findViewById(R.id.progressVisitadosAnormalidade)).setMax(numeroCadastros);
    	((ProgressBar)findViewById(R.id.progressVisitadosAnormalidade)).setProgress(listStatus.get(Constantes.IMOVEL_SALVO_COM_ANORMALIDADE));
    	((TextView)findViewById(R.id.txtNumeroVisitadosAnormalidade)).setText(String.valueOf(listStatus.get(Constantes.IMOVEL_SALVO_COM_ANORMALIDADE)));
    
    	((ProgressBar)findViewById(R.id.progressNovos)).setMax(numeroCadastros);
    	((ProgressBar)findViewById(R.id.progressNovos)).setProgress(listStatus.get(Constantes.IMOVEL_NOVO));
    	((TextView)findViewById(R.id.txtNumeroNovos)).setText(String.valueOf(listStatus.get(Constantes.IMOVEL_NOVO)));
    
    	((ProgressBar)findViewById(R.id.progressTransmitidos)).setMax(numeroCadastros);
    	((ProgressBar)findViewById(R.id.progressTransmitidos)).setProgress(listStatus.get(Constantes.IMOVEL_TRANSMITIDO));
    	((TextView)findViewById(R.id.txtNumeroTransmitidos)).setText(String.valueOf(listStatus.get(Constantes.IMOVEL_TRANSMITIDO)));
    
    	((ProgressBar)findViewById(R.id.progressNaoTransmitidos)).setMax(numeroCadastros);
    	((ProgressBar)findViewById(R.id.progressNaoTransmitidos)).setProgress(listStatus.get(Constantes.IMOVEL_NAO_TRANSMITIDO));
    	((TextView)findViewById(R.id.txtNumeroNaoTransmitidos)).setText(String.valueOf(listStatus.get(Constantes.IMOVEL_NAO_TRANSMITIDO)));
    
    }
}