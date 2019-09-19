package util;

public class Constantes {

	public static final int NULO_INT = Integer.MIN_VALUE;
    public static final short NULO_SHORT = Short.MIN_VALUE;
    public static final String NULO_STRING = "";
    public static final double NULO_DOUBLE = Double.MIN_VALUE;

    public static final int DIALOG_ID_PASSWORD = 0;
    public static final int DIALOG_ID_CARREGAR_ROTA = 1;
    public static final int DIALOG_ID_LIMPAR_TUDO = 2;
    public static final int DIALOG_ID_SUCESSO = 3;
    public static final int DIALOG_ID_AVISO = 4;
    public static final int DIALOG_ID_ERRO = 5;
    public static final int DIALOG_ID_ERRO_GPS_DESLIGADO = 6;
    public static final int DIALOG_ID_ADD_NOVO_IMOVEL = 7;
    public static final int DIALOG_ID_ADD_NOVO_IMOVEL_ANTES_PRIMEIRO = 8;
    public static final int DIALOG_ID_ADD_NOVO_IMOVEL_APOS_ULTIMO = 9;
    public static final int DIALOG_ID_CONFIRMA_VOLTAR = 10;
    public static final int DIALOG_ID_CONFIRMA_MUDANCA = 11;
    public static final int DIALOG_ID_CONFIRMA_EXCLUSAO = 12;
    public static final int DIALOG_ID_CONFIRMA_IMOVEL_SALVO = 13;
    public static final int DIALOG_ID_EXPORTAR_BANCO = 14;
    
    public static final int FRAGMENT_ID_CLIENTE = 0;
    public static final int FRAGMENT_ID_IMOVEL = 1;
    public static final int FRAGMENT_ID_SERVICOS = 2;
    public static final int FRAGMENT_ID_MEDIDOR = 3;
    public static final int FRAGMENT_ID_ANORMALIDADE = 4;

    public static final String DIRETORIO_ROTAS = "/external_sd/Cadastro/Roteiros";
    public static final String DIRETORIO_RETORNO = "/external_sd/Cadastro/Retorno";
    public static final String DIRETORIO_LOGS = "/external_sd/Cadastro/Logs";
    public static final String DIRETORIO_EXPORTACAO_BANCO = "/external_sd/Cadastro/Banco";
    
    public static final int REGISTRO_TIPO_CLIENTE = 1;
    public static final int REGISTRO_TIPO_IMOVEL = 2;
    public static final int REGISTRO_TIPO_RAMOS_ATIVIDADE_IMOVEL = 3;
    public static final int REGISTRO_TIPO_SERVICO = 4;
    public static final int REGISTRO_TIPO_HIDROMETRO = 5;
    public static final int REGISTRO_TIPO_ANORMALIDADE_IMOVEL = 6;
    public static final int REGISTRO_TIPO_GERAL = 7;
    public static final int REGISTRO_TIPO_ANORMALIDADE = 8;
    public static final int REGISTRO_TIPO_RAMO_ATIVIDADE = 9;
    public static final int REGISTRO_TIPO_SITUACAO_AGUA = 10;
    public static final int REGISTRO_TIPO_SITUACAO_ESGOTO = 11;
    public static final int REGISTRO_TIPO_PROTECAO_HIDROMETRO = 12;
    public static final int REGISTRO_TIPO_FONTE_ABASTECIMENTO = 13;
    public static final int REGISTRO_TIPO_MARCA_HIDROMETRO = 14;
    public static final int REGISTRO_TIPO_LOCAl_INSTALACAO_RAMAL= 15;
    public static final int REGISTRO_TIPO_CAPACIDADE_HIDROMETRO = 16;
    public static final int REGISTRO_TIPO_LOGRADOURO = 17;
    public static final int REGISTRO_TIPO_CLASSE_SOCIAL = 18;
    public static final int REGISTRO_TIPO_USO = 19;
    public static final int REGISTRO_TIPO_ACESSO_HIDROMETRO = 20;
    public static final int REGISTRO_TIPO_ACESSO_USUARIO = 21;
    
    public static final int SIM = 1;
    public static final int NAO = 2;
   
    public static final int TIPO_USUARIO_PROPRIETARIO = 0;
    public static final int TIPO_USUARIO_OUTRO = 1;
    
    public static final int TIPO_RESPONSAVEL_PROPRIETARIO = 0;
    public static final int TIPO_RESPONSAVEL_USUARIO = 1;
    public static final int TIPO_RESPONSAVEL_OUTRO = 2;

    public static final int IMOVEL_PROPRIETARIO_RESIDENCIAL = 1;
    public static final int IMOVEL_PROPRIETARIO_COMERCIAL = 2;
    
    public static final int IMOVEL_RESPONSAVEL_RESIDENCIAL = 1;
    public static final int IMOVEL_RESPONSAVEL_COMERCIAL = 2;
    
    public static final int FONTE_ABASTECIMENTO_COSANPA = 0;
    public static final int FONTE_ABASTECIMENTO_PROPRIO = 1;
    public static final int FONTE_ABASTECIMENTO_MISTO = 2;
    public static final int FONTE_ABASTECIMENTO_OUTRO = 3;
    
    public static final int TIPO_PESSOA_FISICA = 1;
    public static final int TIPO_PESSOA_JURIDICA = 2;
    
    public static final int METODO_BUSCA_TODOS = 0;
    public static final int METODO_BUSCA_MATRICULA = 1;
    public static final int METODO_BUSCA_CPF = 2;
    public static final int METODO_BUSCA_CNPJ = 3;
    public static final int METODO_BUSCA_NUMERO_RESIDENCIA = 4;
    
    public static final int FILTRO_BUSCA_TODOS = 0;
    public static final int FILTRO_BUSCA_VISITADOS_SUCESSO = 1;
    public static final int FILTRO_BUSCA_VISITADOS_ANORMALIDADE = 2;
    public static final int FILTRO_BUSCA_NAO_VISITADOS = 3;
    public static final int FILTRO_BUSCA_NOVOS = 4;
    public static final int FILTRO_BUSCA_TRANSMITIDOS = 5;
    public static final int FILTRO_BUSCA_NAO_TRANSMITIDOS = 6;
    
    public static final int PESSOA_PROPRIETARIO = 0;
    public static final int PESSOA_USUARIO = 1;
    public static final int PESSOA_RESPONSAVEL = 2;
  
    public static final String TABLE_CLIENTE = "cliente";
    public static final String TABLE_IMOVEL = "imovel";
    public static final String TABLE_RAMO_ATIVIDADE_IMOVEL = "ramo_atividade_imovel";
    public static final String TABLE_SERVICO = "servico";
    public static final String TABLE_MEDIDOR = "medidor";
    public static final String TABLE_ANORMALIDADE = "anormalidade";
    public static final String TABLE_ANORMALIDADE_IMOVEL = "anormalidade_imovel";
    public static final String TABLE_GERAL = "geral";
    public static final String TABLE_RAMO_ATIVIDADE = "ramo_atividade";
    public static final String TABLE_SITUACAO_LIGACAO_AGUA = "ligacao_agua";
    public static final String TABLE_SITUACAO_LIGACAO_ESGOTO = "ligacao_esgoto";
    public static final String TABLE_LOCAL_INSTALACAO_RAMAL = "local_instalacao_ramal";
    public static final String TABLE_PROTECAO_HIDROMETRO = "protecao_hidrometro";
    public static final String TABLE_FONTE_ABASTECIMENTO = "fonte_abastecimento";
    public static final String TABLE_MARCA_HIDROMETRO = "marca_hidrometro";
    public static final String TABLE_CAPACIDADE_HIDROMETRO = "capacidade_hidrometro";
    public static final String TABLE_CONFIGURACAO = "configuracao";
    public static final String TABLE_TIPO_LOGRADOURO = "tipo_logradouro";
    public static final String TABLE_CLASSE_SOCIAL = "classe_social";
    public static final String TABLE_TIPO_USO = "tipo_uso";
    public static final String TABLE_ACESSO_HIDROMETRO = "acesso_hidrometro";
    public static final String TABLE_USUARIO = "usuario";
    public static final String TABLE_INCONSISTENCIA_IMOVEL = "inconsistencia_imovel";
    
    public static final String DATABASE_NAME = "cadastro.db";
	public static final String DATABASE_PATH = "/data/data/com.AndroidExplorer/databases/";
	
    public static final int IMOVEL_SALVO = 0;
    public static final int IMOVEL_A_SALVAR = 1;
    public static final int IMOVEL_SALVO_COM_ANORMALIDADE = 2;
    public static final int IMOVEL_SALVO_COM_INCONSISTENCIA = 3;
    public static final int IMOVEL_NOVO = 4;
    public static final int IMOVEL_NOVO_COM_ANORMALIDADE = 5;
    public static final int IMOVEL_EXCLUIDO = 6;
    public static final int IMOVEL_INFORMATIVO = 7;
    
    
    public static final int OPERACAO_CADASTRO_ALTERADO = 1;
    public static final int OPERACAO_CADASTRO_NOVO = 2;
    public static final int OPERACAO_CADASTRO_EXCLUIDO = 3;

    public static final int SEM_OCORRENCIA = 1;
    public static final int ANORMALIDADE_HIDR_NAO_LOCALIZADO = 34;
    public static final int ANORMALIDADE_HIDR_SEM_IDENTIFICACAO = 35;
    
    public static final int PARCIAL_CPF_CNPJ = 7;
}
