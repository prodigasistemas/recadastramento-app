package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import business.Controlador;

public class LogUtil {

	public static void salvar(Class<?> classe, String mensagem, Throwable erro) {
		try {
			File arquivo = criarArquivo(getNome());

			FileWriter fw = new FileWriter(arquivo, true);
			fw.write(DateUtil.getData() + " - [" + classe.getName() + "] - " + mensagem + "\n");

			if (erro != null) {
				fw.write(escreverErro(erro) + "\n");
			}

			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void salvar(Class<?> classe, String mensagem) {
		salvar(classe, mensagem, null);
	}

	private static String escreverErro(Throwable erro) throws IOException {
		Writer w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		erro.printStackTrace(p);
		return w.toString();
	}

	private static File criarArquivo(String nome) throws IOException {
		File diretorio = getDiretorio();

		if (!diretorio.exists()) {
			diretorio.mkdir();
		}

		File arquivo = new File(diretorio, nome);
		if (!arquivo.exists()) {
			arquivo.createNewFile();
		}

		return arquivo;
	}

	public static String getNome() {
		String data = DateUtil.getData("yyyyMMdd") + ".log";
		Controlador controlador = Controlador.getInstancia();
		if (controlador.databaseExists()) {
			List<String> informacoes = Controlador.getInstancia().getCadastroDataManipulator().selectInformacoesRota();

			if (informacoes.isEmpty()) {
				return data;
			} else {
				return informacoes.get(3).trim() + "_" + data;
			}
		} else {
			return data;
		}
	}

	private static File getDiretorio() {
		return new File(Util.getExternalStorageDirectory() + Constantes.DIRETORIO_LOGS);
	}
}
