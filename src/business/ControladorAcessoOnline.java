package business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import ui.MessageDispatcher;
import util.Util;

public class ControladorAcessoOnline {

	public static boolean indcConfirmacaRecebimento = false;

	// Identificador da requisição Cliente -> Servidor para confirmar
	// recebimento do roteiro.
	public static final byte CS_CONFIRMAR_RECEBIMENTO = 3;

	// Identificadores das requisições
	private static final byte PACOTE_BAIXAR_ROTEIRO = 0;
	private static final byte PACOTE_ATUALIZAR_MOVIMENTO = 1;
	private static final byte PACOTE_FINALIZAR_CADASTRAMENTO = 2;
	private static final byte PACOTE_CONFIRMAR_ARQUIVO_RECEBIDO = 3;
	private static final byte BAIXAR_NOVA_VERSAO = 4;

	private boolean requestOK = false;

	private long imei;

	private static ControladorAcessoOnline instance;

	private static MessageDispatcher dispatcher;

	private ControladorAcessoOnline() {
		ControladorAcessoOnline.dispatcher = MessageDispatcher.getInstancia();
	}

	public static ControladorAcessoOnline getInstancia() {
		if (ControladorAcessoOnline.instance == null) {
			ControladorAcessoOnline.instance = new ControladorAcessoOnline();
		}
		return ControladorAcessoOnline.instance;
	}

	public void iniciarServicoRede(Vector parametros, boolean enviarIMEI) {
		byte[] serverMsg = null;

		if (enviarIMEI) {
			parametros.insertElementAt(new Long(this.getIMEI()), 1);
		}

		serverMsg = Util.empacotarParametros(parametros);

		dispatcher.setMensagem(serverMsg);
		dispatcher.enviarMensagem();
		requestOK = MessageDispatcher.getRespostaServidor() == MessageDispatcher.RESPOSTA_OK;
	}

	public void setURL(String url) {
		dispatcher.setUrlServidor(url);
	}

	public long getIMEI() {
		return this.imei;
	}

	public void setIMEI(String imei) {
		if (imei != null) {
			this.imei = Long.parseLong(imei);

		} else {
			this.imei = Long.parseLong("356837024186111");
		}
	}

	public void confirmarRecebimentoArquivo() {
		Vector param = new Vector();
		param.addElement(new Byte(PACOTE_CONFIRMAR_ARQUIVO_RECEBIDO));

		param.trimToSize();
		this.iniciarServicoRede(param, true);
		requestOK = MessageDispatcher.getRespostaServidor() == MessageDispatcher.RESPOSTA_OK;
	}

	public void baixarRoteiro() {
		Vector param = new Vector();
		param.addElement(new Byte(PACOTE_BAIXAR_ROTEIRO));

		param.trimToSize();
		this.iniciarServicoRede(param, true);
		requestOK = MessageDispatcher.getRespostaServidor() == MessageDispatcher.RESPOSTA_OK;
	}

	public void enviarCadastro(byte[] cadastro) throws IOException {
		Vector param = new Vector();
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		bais.write(PACOTE_ATUALIZAR_MOVIMENTO);
		bais.write(cadastro);
		param.addElement(bais.toByteArray());
		param.trimToSize();
		this.iniciarServicoRede(param, false);
		requestOK = MessageDispatcher.getRespostaServidor() == MessageDispatcher.RESPOSTA_OK;
	}

	public void finalizarCadastramento(byte[] arquivoRetorno, short tipoFinalizacao) throws IOException {
		Vector param = new Vector();
		param.addElement(new Byte(PACOTE_FINALIZAR_CADASTRAMENTO));
		param.addElement(arquivoRetorno);
		param.trimToSize();
		this.iniciarServicoRede(param, true);
		requestOK = MessageDispatcher.getRespostaServidor() == MessageDispatcher.RESPOSTA_OK;
	}

	public boolean isRequestOK() {
		return requestOK;
	}
}