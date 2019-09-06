package business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import ui.MessageDispatcher;
import util.LogUtil;
import util.Util;

public class ControladorAcessoOnline {

	private static final byte TRANSMITIR_IMOVEL = 1;

	private static ControladorAcessoOnline instance;
	private static MessageDispatcher dispatcher;
	
	private boolean imovelTransmitido = false;

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
		
		imovelTransmitido = MessageDispatcher.getRespostaServidor().equals(MessageDispatcher.RESPOSTA_SUCESSO);
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
	public void transmitirImovel(byte[] arquivo) {
		try {
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			bais.write(TRANSMITIR_IMOVEL);
			bais.write(arquivo);
			Vector<Object> parametros = new Vector<Object>();
			parametros.addElement(bais.toByteArray());
			parametros.trimToSize();

			this.enviar(parametros);
		} catch (IOException e) {
			LogUtil.salvar(ControladorAcessoOnline.class, "Erro ao escrever ByteArrayOutputStream.", e);
		}
	}

	public boolean isImovelTransmitido() {
		return imovelTransmitido;
	}
}