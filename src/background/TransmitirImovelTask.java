package background;

import model.Imovel;
import ui.ArquivoRetorno;
import util.Constantes;
import util.Util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.AndroidExplorer.R;

public class TransmitirImovelTask extends AsyncTask<Imovel, Integer, Void> {

	private Activity activity;
	private ProgressDialog dialog;

	private Imovel imovel;

	public TransmitirImovelTask(Activity activity) {
		this.activity = activity;
		this.dialog = new ProgressDialog(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.setIndeterminate(false);
		dialog.setMax(1);
		dialog.setMessage("Por favor, aguarde enquanto o imóvel é transmitido...");
		dialog.show();
	}

	@Override
	protected Void doInBackground(Imovel... params) {
		imovel = params[0];
		new ArquivoRetorno().gerarPorImovel(imovel, activity);
		publishProgress(1);

		return null;
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

		exibirMensagem();
	}

	private void exibirMensagem() {
		String mensagem = "";
		String transmitido = "";

		if (imovel.getImovelEnviado() == Constantes.SIM) {
			transmitido = " e transmitido";
		}

		switch (imovel.getImovelStatus()) {

		case Constantes.IMOVEL_SALVO:
			mensagem = "Imóvel finalizado" + transmitido + " com sucesso.";
			break;

		case Constantes.IMOVEL_SALVO_COM_ANORMALIDADE:
			mensagem = "Imóvel com anormalidade finalizado" + transmitido + " com sucesso.";
			break;

		case Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA:
			mensagem = "Imóvel finalizado e transmitido com inconsistências. Verifique a lista na aba de Anormalidade.";
			break;

		case Constantes.IMOVEL_NOVO:
			mensagem = "Imóvel novo criado" + transmitido + " com sucesso.";
			break;

		case Constantes.IMOVEL_NOVO_COM_ANORMALIDADE:
			mensagem = "Imóvel novo criado com anormalidade" + transmitido + " com sucesso.";
			break;

		default:
			break;
		}

		Util.exibirMensagem(activity, "Sucesso", mensagem, R.drawable.save, null, null);
	}
}
