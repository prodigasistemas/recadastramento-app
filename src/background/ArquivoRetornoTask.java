package background;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import model.AnormalidadeImovel;
import model.Cliente;
import model.Imovel;
import model.Medidor;
import model.Servicos;
import ui.ArquivoRetorno;
import util.Constantes;
import util.LogUtil;
import util.Util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import business.Controlador;

import com.AndroidExplorer.R;

import dataBase.DataManipulator;

public class ArquivoRetornoTask extends AsyncTask<Integer, Integer, StringBuffer> {

	private Activity activity;
	private DataManipulator manipulator;
	private ProgressDialog dialog;
	
	private List<String> imoveis;

	public ArquivoRetornoTask(Activity activity) {
		this.activity = activity;
		this.manipulator = Controlador.getInstancia().getCadastroDataManipulator();
		this.dialog = new ProgressDialog(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		imoveis = (List<String>) manipulator.selectIdImoveis("imovel_status NOT IN (" + Constantes.IMOVEL_A_SALVAR + "," + Constantes.IMOVEL_INFORMATIVO + ")");

		if (imoveis.isEmpty()) {
			Util.criarDialog(activity, null, "Atenção", "Não há nenhum imóvel finalizado para geração do arquivo de retorno", R.drawable.aviso, null, null).show();
		} else {
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(false);
			dialog.setIndeterminate(false);
			dialog.setMax(imoveis.size());
			dialog.setMessage("Por favor, aguarde enquanto o arquivo de retorno está sendo gerado...");
			dialog.show();
		}
	}

	@Override
	protected StringBuffer doInBackground(Integer... params) {
		StringBuffer arquivo = null;

		try {
			ArquivoRetorno arquivoRetorno = new ArquivoRetorno();

			if (!imoveis.isEmpty()) {
				criarDiretorio();

				arquivo = new StringBuffer(arquivoRetorno.gerarHeader(activity));

				for (int i = 0; i < imoveis.size(); i++) {
					long idImovel = Long.parseLong(imoveis.get(i));

					manipulator.selectCliente(idImovel);
					manipulator.selectImovel(idImovel);

					if (cadastroInvalido())
						continue;

					manipulator.selectServico(idImovel);
					manipulator.selectMedidor(idImovel);
					manipulator.selectAnormalidadeImovel(String.valueOf(getImovel().getMatricula()));

					arquivo.append(arquivoRetorno.gerarLinhaCliente(getCliente()));
					arquivo.append(arquivoRetorno.gerarLinhaImovel(getImovel()));
					arquivo.append(arquivoRetorno.gerarLinhasRamoAtividade(getImovel()));
					arquivo.append(arquivoRetorno.gerarLinhaServico(getImovel(), getServicos()));
					arquivo.append(arquivoRetorno.gerarLinhaMedidor(getImovel(), getMedidor()));
					arquivo.append(arquivoRetorno.gerarLinhaAnormalidade(getImovel(), getAnormalidade()));

					publishProgress(i + 1);
				}

				gerarZip(arquivo);
			}
		} catch (Exception e) {
			arquivo = null;
			LogUtil.salvar(ArquivoRetornoTask.class, "Erro ao gerar arquivo de retorno", e);
		}

		return arquivo;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		dialog.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(StringBuffer arquivo) {
		super.onPostExecute(arquivo);

		if (dialog.isShowing()) {
			dialog.dismiss();
			
			if (arquivo != null) {
				Util.criarDialog(activity, null, "Sucesso", "Arquivo de retorno gerado com sucesso", R.drawable.save, null, null).show();
			} else {
				Util.criarDialog(activity, null, "Atenção", "Não foi possível gerar o arquivo de retorno", R.drawable.aviso, null, null).show();
			}
		}

	}

	private OutputStreamWriter getOutput() throws FileNotFoundException {
		File file = new File(Util.getRetornoRotaDirectory(), Util.getRotaFileName() + ".txt");
		return new OutputStreamWriter(new FileOutputStream(file));
	}

	private void criarDiretorio() {
		File diretorio = new File(Util.getExternalStorageDirectory() + Constantes.DIRETORIO_RETORNO);
		if (!diretorio.exists()) {
			diretorio.mkdirs();
		}
	}

	private boolean cadastroInvalido() {
		return getCliente().matriculaUsuarioInvalida() &&
			   getCliente().getUsuario().getNome().trim().equals("") &&
			   getImovel().getEnderecoImovel().getTipoLogradouro() == 0 &&
			   getImovel().getImovelStatus() != Constantes.IMOVEL_NOVO_COM_ANORMALIDADE;
	}

	private void gerarZip(StringBuffer arquivo) {
		try {
			OutputStreamWriter out = getOutput();
			out.write(arquivo.toString());
			out.close();

			Util.gerarZipRetorno();
		} catch (FileNotFoundException e) {
			LogUtil.salvar(ArquivoRetornoTask.class, "Arquivo de retorno não encontrado", e);
		} catch (IOException e) {
			LogUtil.salvar(ArquivoRetornoTask.class, "Erro ao gerar zip do arquivo de retorno", e);
		}
	}
	
	private Cliente getCliente() {
		return Controlador.getInstancia().getClienteSelecionado();
	}

	private Imovel getImovel() {
		return Controlador.getInstancia().getImovelSelecionado();
	}

	private Medidor getMedidor() {
		return Controlador.getInstancia().getMedidorSelecionado();
	}

	private Servicos getServicos() {
		return Controlador.getInstancia().getServicosSelecionado();
	}

	private AnormalidadeImovel getAnormalidade() {
		return Controlador.getInstancia().getAnormalidadeImovelSelecionado();
	}
}