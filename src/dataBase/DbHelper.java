package dataBase;

import util.Constantes;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	
	private static final String[] UPDATE_IMOVEL_DATABASE = { 
			" alter table imovel add column area_construida TEXT; ",
			" alter table imovel add column classe_social TEXT; ", 
			" alter table imovel add column numero_animais INTEGER; ",
			" alter table imovel add column volume_piscina TEXT; ", 
			" alter table imovel add column volume_cisterna TEXT; ",
			" alter table imovel add column volume_caixa_dagua TEXT; ", 
			" alter table imovel add column tipo_uso TEXT; ",
			" alter table imovel add column acesso_hidrometro TEXT; ", 
			" alter table imovel add column quantidade_economias_social INTEGER; ",
			" alter table imovel add column numero_criancas INTEGER; ", 
			" alter table imovel add column numero_adultos INTEGER; ",
			" alter table imovel add column numero_alunos INTEGER; ", 
			" alter table imovel add column numero_caes INTEGER; ",
			" alter table imovel add column numero_idosos INTEGER; ", 
			" alter table imovel add column numero_empregados INTEGER; ",
			" alter table imovel add column numero_outros INTEGER; ", 
			" alter table imovel add column quantidade_economias_outros  INTEGER; ",
			" alter table imovel add column observacao TEXT;" };
	
	private static final String ADD_COLUMN_PERCENTUAL_ABASTECIMENTO_TO_IMOVEL = " alter table imovel add column percentual_abastecimento INTEGER; ";

    private static final String DATABASE_CLIENTE_QUERY =
    	"CREATE TABLE cliente (id INTEGER PRIMARY KEY autoincrement, matricula_usuario INTEGER, matricula_responsavel INTEGER, matricula_proprietario INTEGER, matricula TEXT not null, gerencia TEXT, tipo_endereco_proprietario TEXT, tipo_endereco_responsavel TEXT, usuario_proprietario TEXT, tipo_responsavel TEXT, " +
    	"nome_usuario TEXT, tipo_pessoa_usuario TEXT, cpf_cnpj_usuario TEXT, rg_usuario TEXT, uf_usuario TEXT, tipo_sexo_usuario TEXT, telefone_usuario TEXT, celular_usuario TEXT, email_usuario TEXT, " +
    	"nome_proprietario TEXT, tipo_pessoa_proprietario TEXT, cpf_cnpj_proprietario TEXT, rg_proprietario TEXT, uf_proprietario TEXT, tipo_sexo_proprietario TEXT, telefone_proprietario TEXT, celular_proprietario TEXT, email_proprietario TEXT, " +
     	"tipo_logradouro_proprietario INTEGER, logradouro_proprietario TEXT, numero_proprietario TEXT, complemento_proprietario TEXT, bairro_proprietario TEXT, cep_proprietario TEXT, municipio_proprietario TEXT, " +
    	"nome_responsavel TEXT, tipo_pessoa_responsavel TEXT, cpf_cnpj_responsavel TEXT, rg_responsavel TEXT, uf_responsavel TEXT, tipo_sexo_responsavel TEXT, telefone_responsavel TEXT, celular_responsavel TEXT, email_responsavel TEXT, " +
 		"tipo_logradouro_responsavel INTEGER, logradouro_responsavel TEXT, numero_responsavel TEXT, complemento_responsavel TEXT, bairro_responsavel TEXT, cep_responsavel TEXT, municipio_responsavel TEXT, latitude TEXT, longitude TEXT, data TEXT )";

    private static final String DATABASE_IMOVEL_QUERY =
    	"CREATE TABLE imovel (id INTEGER PRIMARY KEY autoincrement, matricula TEXT not null, codigo_cliente TEXT, inscricao TEXT, rota TEXT, face TEXT, codigo_municipio TEXT, numero_iptu TEXT, numero_celpa TEXT, numero_pontos_uteis TEXT, " +
    	"numero_ocupantes TEXT, tipo_logradouro_imovel INTEGER, logradouro_imovel TEXT, numero_imovel TEXT, complemento_imovel TEXT, bairro_imovel TEXT, cep_imovel TEXT, municipio_imovel TEXT, codigo_logradouro_imovel TEXT, " +
    	"sub_categoria_residencial_1 TEXT, sub_categoria_residencial_2 TEXT, sub_categoria_residencial_3 TEXT, sub_categoria_residencial_4 TEXT, " +
    	"sub_categoria_comercial_1 TEXT, sub_categoria_comercial_2 TEXT, sub_categoria_comercial_3 TEXT, sub_categoria_comercial_4 TEXT, " +
    	"sub_categoria_publica_1 TEXT, sub_categoria_publica_2 TEXT, sub_categoria_publica_3 TEXT, sub_categoria_publica_4 TEXT, " +
    	"sub_categoria_industrial_1 TEXT, sub_categoria_industrial_2 TEXT, sub_categoria_industrial_3 TEXT, sub_categoria_industrial_4 TEXT," +
    	"tipo_fonte_abastecimento TEXT, imovel_status TEXT, imovel_enviado TEXT, latitude TEXT, longitude TEXT, data TEXT, entrevistado TEXT, tipo_operacao TEXT," +
    	"area_construida TEXT, classe_social TEXT, numero_animais INTEGER, "+
    	"volume_piscina TEXT, volume_cisterna TEXT, volume_caixa_dagua TEXT, "+
    	"tipo_uso TEXT, acesso_hidrometro TEXT, "+
    	"numero_criancas INTEGER, numero_adultos INTEGER, numero_alunos INTEGER, numero_caes INTEGER, numero_idosos INTEGER, numero_empregados INTEGER, numero_outros INTEGER, " +
    	"quantidade_economias_social INTEGER, quantidade_economias_outros INTEGER, observacao TEXT )";

    private static final String DATABASE_RAMO_ATIVIDADE_IMOVEL_QUERY =
    	"CREATE TABLE ramo_atividade_imovel (id INTEGER PRIMARY KEY autoincrement, matricula TEXT not null, id_imovel INTEGER, codigo INTEGER)";

    private static final String DATABASE_SERVICO_QUERY =
    	"CREATE TABLE servico (id INTEGER PRIMARY KEY autoincrement, matricula TEXT not null, tipo_ligacao_agua TEXT, tipo_ligacao_esgoto TEXT, local_instalacao_ramal TEXT, latitude TEXT, longitude TEXT, data TEXT)";

    private static final String DATABASE_MEDIDOR_QUERY =
    	"CREATE TABLE medidor (id INTEGER PRIMARY KEY autoincrement, matricula TEXT not null, possui_medidor TEXT, numero_hidrometro TEXT, marca TEXT, capacidade TEXT, tipo_caixa_protecao TEXT, latitude TEXT, longitude TEXT, data TEXT)";

    private static final String DATABASE_GERAL_QUERY =
    	"CREATE TABLE geral (id INTEGER PRIMARY KEY autoincrement, versao_aplicativo TEXT, id_rota TEXT, localidade TEXT, setor TEXT, rota TEXT, qtd_imoveis_novos INTEGER, nome_arquivo TEXT, tipo_arquivo TEXT)";

    private static final String DATABASE_ANORMALIDADE_QUERY =
    	"CREATE TABLE anormalidade (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT, data DATE)";

    private static final String DATABASE_ANORMALIDADE_IMOVEL_QUERY =
    	"CREATE TABLE anormalidade_imovel (id INTEGER PRIMARY KEY autoincrement, matricula TEXT, latitude TEXT, longitude TEXT, codigo_anormalidade TEXT, comentario TEXT, path_image_1 TEXT, path_image_2 TEXT, data TEXT, login_usuario TEXT)";

    private static final String DATABASE_RAMO_ATIVIDADE_QUERY =
    	"CREATE TABLE ramo_atividade (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";

    private static final String DATABASE_SITUACAO_AGUA_QUERY =
    	"CREATE TABLE ligacao_agua (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";

    private static final String DATABASE_SITUACAO_ESGOTO_QUERY =
    	"CREATE TABLE ligacao_esgoto (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";

    private static final String DATABASE_PROTECAO_HIDROMETRO_QUERY =
    	"CREATE TABLE protecao_hidrometro (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";

    private static final String DATABASE_FONTE_ABASTECIMENTO_QUERY =
    	"CREATE TABLE fonte_abastecimento (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";

    private static final String DATABASE_MARCA_HIDROMETRO_QUERY =
    	"CREATE TABLE marca_hidrometro (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";
    
    private static final String DATABASE_CAPACIDADE_HIDROMETRO_QUERY =
        	"CREATE TABLE capacidade_hidrometro (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";

    private static final String DATABASE_TIPO_LOGRADOURO_QUERY =
        	"CREATE TABLE tipo_logradouro (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";
    
    private static final String DATABASE_LOCAL_INSTALACAO_RAMAL_QUERY =
        "CREATE TABLE local_instalacao_ramal (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";

    private static final String DATABASE_CONFIGURACAO_QUERY =
    	"CREATE TABLE configuracao (id INTEGER PRIMARY KEY autoincrement, rota_carregada INTEGER, posicao_cadastro_selecionado INTEGER)";
    
    private static final String DATABASE_CLASSE_SOCIAL_QUERY =
            "CREATE TABLE "+Constantes.TABLE_CLASSE_SOCIAL+" (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";
    
    private static final String DATABASE_TIPO_USO_QUERY =
            "CREATE TABLE "+Constantes.TABLE_TIPO_USO+" (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";
    
    private static final String DATABASE_ACESSO_HIDROMETRO_QUERY =
            "CREATE TABLE "+Constantes.TABLE_ACESSO_HIDROMETRO+" (id INTEGER PRIMARY KEY autoincrement, codigo INTEGER, descricao TEXT)";
    
    private static final String DATABASE_USUARIO_QUERY =
            "CREATE TABLE "+Constantes.TABLE_USUARIO+" (id INTEGER PRIMARY KEY autoincrement, nome TEXT, login TEXT, senha TEXT)";
    
    private static final String DATABASE_INCONSISTENCIA_IMOVEL_QUERY =
            "CREATE TABLE "+Constantes.TABLE_INCONSISTENCIA_IMOVEL+" (id INTEGER PRIMARY KEY autoincrement, matricula TEXT, inconsistencia TEXT)";
    
    public DbHelper(Context context) {
		super(context, Constantes.DATABASE_NAME, null, DATABASE_VERSION);
	}

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(DATABASE_CLIENTE_QUERY);
    	db.execSQL(DATABASE_IMOVEL_QUERY);
     	db.execSQL(DATABASE_RAMO_ATIVIDADE_IMOVEL_QUERY);
    	db.execSQL(DATABASE_SERVICO_QUERY);
    	db.execSQL(DATABASE_MEDIDOR_QUERY);
     	db.execSQL(DATABASE_GERAL_QUERY);
     	db.execSQL(DATABASE_ANORMALIDADE_QUERY);
     	db.execSQL(DATABASE_ANORMALIDADE_IMOVEL_QUERY);
     	db.execSQL(DATABASE_RAMO_ATIVIDADE_QUERY);
     	db.execSQL(DATABASE_SITUACAO_AGUA_QUERY);
     	db.execSQL(DATABASE_SITUACAO_ESGOTO_QUERY);
     	db.execSQL(DATABASE_PROTECAO_HIDROMETRO_QUERY);
     	db.execSQL(DATABASE_FONTE_ABASTECIMENTO_QUERY);
     	db.execSQL(DATABASE_MARCA_HIDROMETRO_QUERY);
     	db.execSQL(DATABASE_CAPACIDADE_HIDROMETRO_QUERY);
     	db.execSQL(DATABASE_TIPO_LOGRADOURO_QUERY);
     	db.execSQL(DATABASE_LOCAL_INSTALACAO_RAMAL_QUERY);
     	db.execSQL(DATABASE_CONFIGURACAO_QUERY);
     	db.execSQL(DATABASE_CLASSE_SOCIAL_QUERY);
     	db.execSQL(DATABASE_TIPO_USO_QUERY);
     	db.execSQL(DATABASE_ACESSO_HIDROMETRO_QUERY);
     	db.execSQL(DATABASE_USUARIO_QUERY);
     	db.execSQL(DATABASE_INCONSISTENCIA_IMOVEL_QUERY);
     	
     	db.execSQL(ADD_COLUMN_PERCENTUAL_ABASTECIMENTO_TO_IMOVEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
		if(newVersion==2 && oldVersion==1){
			
			for (String string : UPDATE_IMOVEL_DATABASE) {
				db.execSQL(string);
			}
	    	db.execSQL(DATABASE_CLASSE_SOCIAL_QUERY);
	     	db.execSQL(DATABASE_TIPO_USO_QUERY);
	     	db.execSQL(DATABASE_ACESSO_HIDROMETRO_QUERY);
		}
		
		if(newVersion==3 && oldVersion==2){
			db.execSQL(ADD_COLUMN_PERCENTUAL_ABASTECIMENTO_TO_IMOVEL);
		}
    }
}
