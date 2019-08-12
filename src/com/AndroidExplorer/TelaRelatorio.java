package com.AndroidExplorer;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import business.Controlador;

public class TelaRelatorio extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roteirorelatorio);

		int total = Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis();
		ArrayList<Integer> lista = (ArrayList<Integer>) Controlador.getInstancia().getCadastroDataManipulator().selectNumeroTodosStatusImoveis();

		((ProgressBar) findViewById(R.id.progressVisitados)).setMax(total);
		((ProgressBar) findViewById(R.id.progressVisitados)).setProgress(lista.get(0));
		((TextView) findViewById(R.id.txtNumeroVisitados)).setText(String.valueOf(lista.get(0)));

		((ProgressBar) findViewById(R.id.progressNaoVisitados)).setMax(total);
		((ProgressBar) findViewById(R.id.progressNaoVisitados)).setProgress(lista.get(1));
		((TextView) findViewById(R.id.txtNumeroNaoVisitados)).setText(String.valueOf(lista.get(1)));

		((ProgressBar) findViewById(R.id.progressVisitadosAnormalidade)).setMax(total);
		((ProgressBar) findViewById(R.id.progressVisitadosAnormalidade)).setProgress(lista.get(2));
		((TextView) findViewById(R.id.txtNumeroVisitadosAnormalidade)).setText(String.valueOf(lista.get(2)));

		((ProgressBar) findViewById(R.id.progressNovos)).setMax(total);
		((ProgressBar) findViewById(R.id.progressNovos)).setProgress(lista.get(3));
		((TextView) findViewById(R.id.txtNumeroNovos)).setText(String.valueOf(lista.get(3)));

		((ProgressBar) findViewById(R.id.progressTransmitidos)).setMax(total);
		((ProgressBar) findViewById(R.id.progressTransmitidos)).setProgress(lista.get(4));
		((TextView) findViewById(R.id.txtNumeroTransmitidos)).setText(String.valueOf(lista.get(4)));
		
		((ProgressBar) findViewById(R.id.progressInconsistencias)).setMax(total);
		((ProgressBar) findViewById(R.id.progressInconsistencias)).setProgress(lista.get(5));
		((TextView) findViewById(R.id.txtNumeroInconsistencias)).setText(String.valueOf(lista.get(5)));

		((ProgressBar) findViewById(R.id.progressNaoTransmitidos)).setMax(total);
		((ProgressBar) findViewById(R.id.progressNaoTransmitidos)).setProgress(lista.get(6));
		((TextView) findViewById(R.id.txtNumeroNaoTransmitidos)).setText(String.valueOf(lista.get(6)));

	}
}