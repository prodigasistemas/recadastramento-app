package background;

import java.io.BufferedReader;
import java.io.IOException;

import ui.FileManager;
import util.Constantes;
import util.LogUtil;
import util.Util;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import business.Controlador;

import com.AndroidExplorer.Fachada;
import com.AndroidExplorer.R;

import dataBase.DataManipulator;

public class CarregarRotaTask extends AsyncTask<Void, Integer, Void> {

	private Activity activity;
	private Controlador controlador;
	private DataManipulator manipulator;
	private ProgressDialog dialog;
	private int total;

	private String nomeArquivo;
	private BufferedReader reader;
	private String linha = "";

	public CarregarRotaTask(Activity activity, String nomeArquivo) {
		this.activity = activity;
		this.controlador = Controlador.getInstancia();
		this.manipulator = controlador.getCadastroDataManipulator();
		this.dialog = new ProgressDialog(activity);
		this.nomeArquivo = nomeArquivo;
		this.total = FileManager.getQtdLinhas(nomeArquivo);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.setIndeterminate(false);
		dialog.setMax(total);
		dialog.setMessage("Por favor, aguarde enquanto o arquivo de rota está sendo carregado...");
		dialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		configurar();
		carregar();

		return null;
	}

	private void configurar() {
		if (nomeArquivo.endsWith(".txt")) {
			reader = FileManager.readFile(nomeArquivo);
		} else {
			reader = FileManager.readCompressedFile(nomeArquivo);
		}
	}

	private void carregar() {
		if (reader != null) {
			try {
				int contador = 0;
				while ((linha = reader.readLine()) != null) {
					linha = Util.removerCaractereEspecial(linha);
					lerLinha();
					publishProgress(++contador);
				}

				manipulator.updateConfiguracao("rota_carregada", Constantes.SIM);
			} catch (IOException e) {
				LogUtil.salvar(CarregarRotaTask.class, "Erro ao ler arquivo de rota", e);
			}
		}
	}

	private void lerLinha() {
		int tipo = Integer.parseInt(linha.substring(0, 2));

		switch (tipo) {
		case Constantes.REGISTRO_TIPO_CLIENTE:
			manipulator.insertCliente(linha);
			break;

		case Constantes.REGISTRO_TIPO_IMOVEL:
			manipulator.insertImovel(linha);
			break;

		case Constantes.REGISTRO_TIPO_RAMOS_ATIVIDADE_IMOVEL:
			manipulator.insertRamosAtividadeImovel(linha);
			break;

		case Constantes.REGISTRO_TIPO_SERVICO:
			manipulator.insertServico(linha);
			break;

		case Constantes.REGISTRO_TIPO_HIDROMETRO:
			manipulator.insertMedidor(linha);
			break;

		case Constantes.REGISTRO_TIPO_ANORMALIDADE_IMOVEL:
			manipulator.insertAnormalidadeImovel(linha);
			break;

		case Constantes.REGISTRO_TIPO_GERAL:
			manipulator.insertDadosGerais(linha, nomeArquivo);
			break;

		case Constantes.REGISTRO_TIPO_ANORMALIDADE:
			manipulator.insertAnormalidade(linha);
			break;

		case Constantes.REGISTRO_TIPO_RAMO_ATIVIDADE:
			manipulator.insertRamoAtividade(linha);
			break;

		case Constantes.REGISTRO_TIPO_SITUACAO_AGUA:
			manipulator.insertSituacaoLigacaoAgua(linha);
			break;

		case Constantes.REGISTRO_TIPO_SITUACAO_ESGOTO:
			manipulator.insertSituacaoLigacaoEsgoto(linha);
			break;

		case Constantes.REGISTRO_TIPO_PROTECAO_HIDROMETRO:
			manipulator.insertProtecaoHidrometro(linha);
			break;

		case Constantes.REGISTRO_TIPO_FONTE_ABASTECIMENTO:
			manipulator.insertFonteAbastecimento(linha);
			break;

		case Constantes.REGISTRO_TIPO_MARCA_HIDROMETRO:
			manipulator.insertMarcaHidrometro(linha);
			break;

		case Constantes.REGISTRO_TIPO_LOCAl_INSTALACAO_RAMAL:
			manipulator.insertLocalInstalacaoRamal(linha);
			break;

		case Constantes.REGISTRO_TIPO_CAPACIDADE_HIDROMETRO:
			manipulator.insertCapacidadeHidrometro(linha);
			break;

		case Constantes.REGISTRO_TIPO_LOGRADOURO:
			manipulator.insertLogradouro(linha);
			break;

		case Constantes.REGISTRO_TIPO_CLASSE_SOCIAL:
			manipulator.insertClasseSocial(linha);
			break;

		case Constantes.REGISTRO_TIPO_USO:
			manipulator.insertTipoUso(linha);
			break;

		case Constantes.REGISTRO_TIPO_ACESSO_HIDROMETRO:
			manipulator.insertAcessoHidrometro(linha);
			break;

		case Constantes.REGISTRO_TIPO_ACESSO_USUARIO:
			manipulator.insertUsuario(linha);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		dialog.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		if (versoesCompativeis()) {
			Toast.makeText(activity.getBaseContext(), "Arquivo de rota carregado com sucesso", Toast.LENGTH_LONG).show();

			activity.finish();
			activity.startActivity(new Intent(activity, Fachada.class));
		} else {
			apagarBancoDeDados();
		}
	}

	private boolean versoesCompativeis() {
		manipulator.selectGeral();

		int versaoAplicativo = Integer.parseInt(activity.getString(R.string.app_versao).replace(".", ""));
		int versaoArquivo = Integer.parseInt(manipulator.getDadosGerais().getVersaoArquivo().replace(".", ""));

		return versaoAplicativo >= versaoArquivo;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void apagarBancoDeDados() {
		controlador.finalizeDataManipulator();
		controlador.deleteDatabase();

		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.recreate();
			}
		};

		Util.exibirMensagem(activity, "Alerta", "As versões do aplicativo e arquivo são incompatíveis", R.drawable.aviso, listener, null);
	}
}
