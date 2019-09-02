package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import business.Controlador;

public class LogUtil {

	private static String EXTENSAO = ".log";
	
	public static void salvarExceptionLog(Throwable erro) {
		try {
			File arquivo = criarArquivo(getNome());
			FileWriter fw = new FileWriter(arquivo, true);
			fw.write(DateUtil.getData() + " - [ERRO]\n");
			fw.write(escreverErro(erro) + "\n");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void salvarLog(String conteudo) {
		String linha = DateUtil.getData() + " - " + conteudo;
		gerarLog(linha);
	}

	public static void salvarLog(String tipo, String conteudo) {
		String linha = DateUtil.getData() + " - [" + tipo + "] - " + conteudo;
		gerarLog(linha);
	}

	private static String escreverErro(Throwable erro) {
		Writer w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		erro.printStackTrace(p);
		return w.toString();
	}
	
	private static void gerarLog(String linha) {
		try {
			File arquivo = criarArquivo(getNome());
			BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true));
			bw.append(linha);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			salvarExceptionLog(e);
		} catch (Exception e) {
			e.printStackTrace();
			salvarExceptionLog(e);
		}
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
		List<String> informacoes = Controlador.getInstancia().getCadastroDataManipulator().selectInformacoesRota();
		String nome = informacoes.get(3).trim() + "_" + DateUtil.getData("yyyyMMdd_HH:mm:ss");
		return nome + EXTENSAO;
	}

	private static File getDiretorio() {
		return new File(Util.getExternalStorageDirectory() + Constantes.DIRETORIO_LOGS);
	}
}
