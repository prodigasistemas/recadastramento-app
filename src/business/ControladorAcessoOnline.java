package business;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import ui.MessageDispatcher;
import util.LogUtil;

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
		dispatcher.setMensagem(empacotarParametros(parametros));
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
	
	/**
     * Método responsável por transformar um vetor de parâmetros em uma mensagem um array de bytes.
     * 
     * @param parameters Vetor de parâmetros.
     * @return O array de bytes com os parâmetros empacotados.
     */
	private byte[] empacotarParametros(Vector<?> parametros) {

		byte[] resposta = null;

		try {
			parametros.trimToSize();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			if (parametros != null) {
				int tamanho = parametros.size();

				for (int i = 0; i < tamanho; i++) {

					Object param = parametros.elementAt(i);

					if (param instanceof Byte) {
						dos.writeByte(((Byte) param).byteValue());
					} else if (param instanceof Integer) {
						dos.writeInt(((Integer) param).intValue());
					} else if (param instanceof Long) {
						dos.writeLong(((Long) param).longValue());
					} else if (param instanceof String) {
						dos.writeUTF((String) param);
					} else if (param instanceof byte[]) {
						dos.write((byte[]) param);
					}
				}
			}

			resposta = baos.toByteArray();

			if (dos != null) {
				dos.close();
				dos = null;
			}

			if (baos != null) {
				baos.close();
				baos = null;
			}
		} catch (IOException e) {
			LogUtil.salvar(getClass(), "Erro ao empacotar parametros", e);
		}

		return resposta;
	}
}