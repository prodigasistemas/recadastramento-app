package background;

import java.util.List;

import model.Imovel;
import ui.ArquivoRetorno;
import util.Constantes;
import util.Util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import business.Controlador;

import com.AndroidExplorer.R;

public class TransmitirFinalizadosTask extends AsyncTask<Imovel, Integer, Void> {

	private Activity activity;
	private ProgressDialog dialog;
	private List<Imovel> imoveis;

	public TransmitirFinalizadosTask(Activity activity) {
		this.activity = activity;
		this.dialog = new ProgressDialog(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		imoveis = (List<Imovel>) Controlador.getInstancia().getCadastroDataManipulator().pesquisarImoveisFinalizados();

		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.setIndeterminate(false);
		dialog.setMax(imoveis.size());
		dialog.setMessage("Por favor, aguarde enquanto os imóveis são transmitidos...");
		dialog.show();
	}

	@Override
	protected Void doInBackground(Imovel... params) {
		for (int i = 0; i < imoveis.size(); i++) {
			new ArquivoRetorno().gerarPorImovel(imoveis.get(i), activity);
			publishProgress(i + 1);
		}

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

		Util.showNotifyDialog((FragmentActivity) activity, R.drawable.save, "Sucesso", "Imóveis transmitidos com sucesso.",
				Constantes.DIALOG_ID_SUCESSO);
	}
}
