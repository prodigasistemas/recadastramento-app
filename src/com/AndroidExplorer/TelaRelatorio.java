package com.AndroidExplorer;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import business.Controlador;
import dataBase.DataManipulator;

public class TelaRelatorio extends FragmentActivity {

	private static final int POSICAO_TOTAL = 0;
	private static final int POSICAO_INFORMATIVOS = 1;
	private static final int POSICAO_PENDENTES = 2;

	private static final int POSICAO_TOTAL_FINALIZADOS = 3;
	private static final int POSICAO_FINALIZADOS = 4;
	private static final int POSICAO_FINALIZADOS_ANORMALIDADE = 5;
	private static final int POSICAO_NOVOS = 6;
	private static final int POSICAO_EXCLUIDOS = 7;

	private static final int POSICAO_NAO_TRANSMITIDOS = 8;
	private static final int POSICAO_TRANSMITIDOS = 9;
	private static final int POSICAO_TRANSMITIDOS_INCONSISTENCIA = 10;

	private List<Integer> dados;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tela_relatorio);
		DataManipulator manipulator = Controlador.getInstancia().getCadastroDataManipulator();

		dados = (List<Integer>) manipulator.obterDadosRelatorio();

		configurarItem(R.id.progressTotal, R.id.valorTotal, POSICAO_TOTAL);
		configurarItem(R.id.progressInformativos, R.id.valorInformativos, POSICAO_INFORMATIVOS);
		configurarItem(R.id.progressPendentes, R.id.valorPendentes, POSICAO_PENDENTES);

		int totalFinalizados = dados.get(POSICAO_TOTAL_FINALIZADOS);

		configurarItem(R.id.progressTotalFinalizados, R.id.valorTotalFinalizados, totalFinalizados, POSICAO_TOTAL_FINALIZADOS);
		configurarItem(R.id.progressFinalizados, R.id.valorFinalizados, totalFinalizados, POSICAO_FINALIZADOS);
		configurarItem(R.id.progressFinalizadosAnormalidade, R.id.valorFinalizadosAnormalidade, totalFinalizados, POSICAO_FINALIZADOS_ANORMALIDADE);
		configurarItem(R.id.progressNovos, R.id.valorNovos, totalFinalizados, POSICAO_NOVOS);
		configurarItem(R.id.progressExcluidos, R.id.valorExcluidos, totalFinalizados, POSICAO_EXCLUIDOS);

		configurarItem(R.id.progressNaoTransmitidos, R.id.valorNaoTransmitidos, totalFinalizados, POSICAO_NAO_TRANSMITIDOS);
		configurarItem(R.id.progressTransmitidos, R.id.valorTransmitidos, totalFinalizados, POSICAO_TRANSMITIDOS);
		configurarItem(R.id.progressTransmitidosInconsistencia, R.id.valorTransmitidosInconsistencia, totalFinalizados, POSICAO_TRANSMITIDOS_INCONSISTENCIA);
	}

	private void configurarItem(int progressBar, int textView, int total, int posicao) {
		((ProgressBar) findViewById(progressBar)).setMax(total);
		((ProgressBar) findViewById(progressBar)).setProgress(dados.get(posicao));
		((TextView) findViewById(textView)).setText(String.valueOf(dados.get(posicao)));
	}

	private void configurarItem(int progressBar, int textView, int posicao) {
		configurarItem(progressBar, textView, dados.get(POSICAO_TOTAL), posicao);
	}
}