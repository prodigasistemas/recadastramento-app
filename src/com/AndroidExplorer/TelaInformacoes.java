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

		List<String> informacoes = Controlador.getInstancia().getCadastroDataManipulator().selectInformacoesRota();
		((TextView) findViewById(R.id.valorLocalidade)).setText(informacoes.get(0));
		((TextView) findViewById(R.id.valorSetor)).setText(informacoes.get(1));
		((TextView) findViewById(R.id.valorRota)).setText(informacoes.get(2));
		((TextView) findViewById(R.id.valorTotalImoveis)).setText(String.valueOf(Controlador.getInstancia().getCadastroDataManipulator().getNumeroImoveis()));
		((TextView) findViewById(R.id.valorUsuario)).setText(Controlador.getInstancia().getCadastroDataManipulator().getUsuario().getNome());
		((TextView) findViewById(R.id.valorNomeArquivo)).setText(getNomeArquivo(informacoes.get(3).trim()));
		((TextView) findViewById(R.id.valorTipoArquivo)).setText(getTipoArquivo(informacoes.get(4).trim()));
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