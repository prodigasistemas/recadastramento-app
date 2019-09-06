package ui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import util.LogUtil;

/**
 * Classe reponsável por enviar as mensagens de requisição de serviço ao
 * servidor.
 */
public class MessageDispatcher {

	public static final String RESPOSTA_SUCESSO = "*";
	public static final String RESPOSTA_ERRO = "#";
	public static final String RESPOSTA_INCONSISTENCIA = "!";

	private static final String REQUISICAO_GSAN_ACTION_URL = "processarRequisicaoDispositivoMovelRecadastramentoAction.do";

	private static MessageDispatcher instancia;
	private static String respostaServidor = RESPOSTA_ERRO;

	private HttpURLConnection conexao;
	private String urlServidor;
	private byte[] mensagem;

	protected MessageDispatcher() {
		super();
	}

	public static MessageDispatcher getInstancia() {
		if (instancia == null) {
			instancia = new MessageDispatcher();
		}

		return instancia;
	}

	public void enviarMensagem() {

		synchronized (mensagem) {
			try {
				configurarConexao();
				enviarRequisicao();

				int response = conexao.getResponseCode();

				if (response == HttpURLConnection.HTTP_OK) {
					InputStream resposta = conexao.getInputStream();

					String valor = obterResposta(resposta);

					if (respostaValida(valor)) {
						respostaServidor = valor;
					}
				}
			} catch (IOException e) {
				LogUtil.salvar(MessageDispatcher.class, "Erro ao estabelecer conexão com o servidor", e);
			} finally {
				desconectar();
			}
		}
	}

	/**
	 * Define a mensagem de requisição a ser enviada ao servidor.
	 * 
	 * @param mensagem
	 *            Mensagem empacotada.
	 */
	public void setMensagem(byte[] mensagem) {
		this.mensagem = mensagem;
	}

	public static String getRespostaServidor() {
		return respostaServidor;
	}

	public void setUrlServidor(String url) {
		this.urlServidor = url;
	}

	public static boolean isRespostaInconsistencia() {
		return respostaServidor.startsWith(MessageDispatcher.RESPOSTA_INCONSISTENCIA);
	}

	public static String getInconsistencias() {
		return respostaServidor.substring(1);
	}

	private boolean respostaValida(String valor) {
		return valor.equals(RESPOSTA_SUCESSO) || valor.startsWith(RESPOSTA_INCONSISTENCIA);
	}

	private String obterResposta(InputStream resposta) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		int length;
		byte[] buffer = new byte[1024];

		while ((length = resposta.read(buffer)) != -1) {
			output.write(buffer, 0, length);
		}

		return output.toString();
	}

	private void desconectar() {
		if (conexao != null) {
			conexao.disconnect();
		}
	}

	private void enviarRequisicao() throws IOException {
		DataOutputStream output = new DataOutputStream(conexao.getOutputStream());
		output.write(this.mensagem);
		output.flush();
		output.close();
	}

	private void configurarConexao() throws IOException {
		this.conexao = (HttpURLConnection) new URL(urlServidor.concat(REQUISICAO_GSAN_ACTION_URL)).openConnection();
		this.conexao.setDoOutput(true);
		this.conexao.setDoInput(true);
		this.conexao.setUseCaches(false);
		this.conexao.setConnectTimeout(2000);
		this.conexao.setRequestProperty("Content-Type", "application/octet-stream");
		this.conexao.setRequestProperty("Content-Length", Integer.toString(this.mensagem.length));
		this.conexao.setRequestMethod("POST");
		this.conexao.setRequestProperty("User-Agent", "Profile/Android Apache");
	}
}
