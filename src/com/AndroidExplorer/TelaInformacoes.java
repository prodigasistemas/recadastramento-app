package com.AndroidExplorer;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import business.Controlador;

public class TelaInformacoes extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roteiroinfo);

		List<String> infoList = Controlador.getInstancia().getCadastroDataManipulator().selectInformacoesRota();
		String anoMesReferencia = infoList.get(4).substring(4, 6) + "/" + infoList.get(4).substring(0, 4);

		((TextView) findViewById(R.id.valorGrupo)).setText(infoList.get(0));
		((TextView) findViewById(R.id.valorLocalidade)).setText(infoList.get(1));
		((TextView) findViewById(R.id.valorSetor)).setText(infoList.get(2));
		((TextView) findViewById(R.id.valorRota)).setText(infoList.get(3));
		((TextView) findViewById(R.id.valorAnoMesReferencia)).setText(anoMesReferencia);
		((TextView) findViewById(R.id.valorTotalImoveis)).setText(String.valueOf(Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis()));
		((TextView) findViewById(R.id.valorUsuario)).setText(infoList.get(5));
		((TextView) findViewById(R.id.valorNomeArquivo)).setText(getNomeArquivo(infoList.get(6)));
		((TextView) findViewById(R.id.valorTipoArquivo)).setText(getTipoArquivo(infoList.get(7).trim()));
	}

	private String getNomeArquivo(String nome) {
		return nome != null && !nome.trim().equals("") ? nome + ".txt" : "";
	}

	private String getTipoArquivo(String tipo) {
		if (tipo.equals("")) {
			return "TRANSMISSÃO";
		} else if (tipo.equals("R")) {
			return "REVISÃO";
		} else if (tipo.equals("F")) {
			return "FISCALIZAÇÃO";
		} else if (tipo.equals("V")) {
			return "REVISITA";
		} else {
			return "";
		}
	}
}