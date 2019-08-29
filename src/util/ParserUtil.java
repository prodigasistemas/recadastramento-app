package util;

public class ParserUtil {

	private int contador = 0;
	private String fonte;

	private String conteudo;

	public ParserUtil(String fonte) {
		this.fonte = fonte;
	}

	public String obterDadoParser(int tamanho) {
		String retorno = null;

		try {
			int posicaoInicial = contador;
			contador += tamanho;

			retorno = fonte.substring(posicaoInicial, contador);
		} catch (IndexOutOfBoundsException e) {
			retorno = "";
		}

		this.setConteudo(retorno);

		return retorno;
	}

	public String getFonte() {
		return fonte;
	}

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}

	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
}
