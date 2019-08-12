package business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import ui.MessageDispatcher;
import util.Util;

public class ControladorAcessoOnline {

	private static final byte ATUALIZAR_CADASTRO = 1;

	private boolean requestOK = false;

	private static ControladorAcessoOnline instance;

	private static MessageDispatcher dispatcher;

	private ControladorAcessoOnline() {
		ControladorAcessoOnline.dispatcher = MessageDispatcher.getInstancia();
	}

	/**
	 * Retorna a instância de Online Access.
	 * @return A instância da fachada de rede.
	 */
	public static ControladorAcessoOnline getInstancia() {
		if (ControladorAcessoOnline.instance == null) {
			ControladorAcessoOnline.instance = new ControladorAcessoOnline();
		}
		return ControladorAcessoOnline.instance;
	}

	/**
	 * Repassa as requisições ao servidor.
	 * 
	 * @param parametros Vetor de parâmetros da operação.
	 * @param recebeResposta Boolean que diz se recebe ou não um InputStream do servidor
	 */
	public void enviar(Vector<Object> parametros) {
		dispatcher.setMensagem(Util.empacotarParametros(parametros));
		dispatcher.enviarMensagem();
		requestOK = MessageDispatcher.getRespostaServidor().equals(MessageDispatcher.RESPOSTA_SUCESSO);
	}

	public void setURL(String url) {
		dispatcher.setUrlServidor(url);
	}


	/**
	 * Envia o arquivo de retorno para o servidor
	 * 
	 * @param arquivo Array de bytes do arquivo de retorno
	 * @throws IOException
	 */
	public void atualizarCadastro(byte[] arquivo) throws IOException {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		bais.write(ATUALIZAR_CADASTRO);
		bais.write(arquivo);

		Vector<Object> parametros = new Vector<Object>();
		parametros.addElement(bais.toByteArray());
		parametros.trimToSize();

		this.enviar(parametros);
	}

	public boolean isRequestOK() {
		return requestOK;
	}
}