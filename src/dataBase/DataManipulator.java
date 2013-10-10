package dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import business.Controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.Constantes;
import util.ParserUtil;
import util.Util;

import model.AnormalidadeImovel;
import model.Registro;
import model.Cliente;
import model.DadosGerais;
import model.Imovel;
import model.Medidor;
import model.Servicos;

public class DataManipulator
{
    private static Context context;
    private DbHelper openHelper;
    static SQLiteDatabase db;

    public DataManipulator(Context context) {
        DataManipulator.context = context;
        openHelper = new DbHelper(DataManipulator.context);
    }

    public DataManipulator open() throws SQLException {
        db = openHelper.getWritableDatabase();
        return this;
    }

    public void close() {
    	openHelper.close();
    }

    public int getNumeroImoveis() {
        return (int)DatabaseUtils.queryNumEntries(db,Constantes.TABLE_IMOVEL);
    }

    public Cliente getClienteSelecionado(){
    	return Controlador.getInstancia().getClienteSelecionado();
    }
    
    public Imovel getImovelSelecionado(){
    	return Controlador.getInstancia().getImovelSelecionado();
    }
    
    public Medidor getMedidorSelecionado(){
    	return Controlador.getInstancia().getMedidorSelecionado();
    }
    
    public Servicos getServicosSelecionado(){
    	return Controlador.getInstancia().getServicosSelecionado();
    }
    
    public AnormalidadeImovel getAnormalidadeImovelSelecionado(){
    	return Controlador.getInstancia().getAnormalidadeImovelSelecionado();
    }
    
    public DadosGerais getDadosGerais(){
    	return Controlador.getInstancia().getDadosGerais();
    }
    
    public Registro getAnormalidades(){
    	return Controlador.getInstancia().getAnormalidades();
    }
    
    public Registro getRamosAtividade(){
    	return Controlador.getInstancia().getRamosAtividade();
    }
    
    public void deleteTable(String tableName) {
        db.delete(tableName, null, null);
    }
    
    public void deleteElementId(int rowId, String tableName) {
        db.delete(tableName, null, null);
   }
    
    public void deleteRamosAtividadeImovel(long idImovel) {
        db.delete(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, "id_imovel=?", new String []{String.valueOf(idImovel)});
   }
    
    public int getPosicaoImovelLista(Imovel imovel) {
    	Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "matricula" }, null, null, null, null,  "inscricao asc");
    	int i = 0;
    	 if (cursor.moveToFirst()) {
    		 do {
    			 i++;
    			 if (imovel.getMatricula() == Integer.parseInt(cursor.getString(0))) {
    				 return i;
    			 }
    		 } while (cursor.moveToNext());
    	 }
    	 
    	 return i;
    }
    
    
public List<String> selectEnderecoImoveis(String condition){
    	
    	ArrayList<String> list = new ArrayList<String>();
    	Cursor cursor;
        
   		cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "id",
   																  "matricula",
   																  "logradouro_imovel",
   																  "numero_imovel",
   																  "complemento_imovel",
   																  "bairro_imovel",
   																  "cep_imovel", 
   																  "municipio_imovel" }, condition, null, null, null,  "inscricao asc");

        int x=0;
        if (cursor.moveToFirst()) {
           do {
                String b1= "(" + (x+1) + ") " + String.valueOf(Integer.parseInt(cursor.getString(1))) + " - " + cursor.getString(2).trim() + ", n°" + cursor.getString(3).trim() + " " + cursor.getString(4).trim() + " " +  cursor.getString(5).trim() + " " + cursor.getString(6).trim() + " " + cursor.getString(7).trim();
//                b1 = Util.capitalizarString(b1);
                list.add(b1);
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
   }

	public List<String> selectSubLotesImovel(String localidadeSetorQuadraLote) {
	
		ArrayList<String> list = new ArrayList<String>();
	
		if (localidadeSetorQuadraLote != Constantes.NULO_STRING || localidadeSetorQuadraLote != null) {
			Cursor cursor;
			
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "inscricao" }, " inscricao LIKE '"+localidadeSetorQuadraLote+"%'",
					null, null, null, "inscricao asc");
			
			if (cursor.moveToFirst()) {
				do {
					list.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
	        if (cursor != null && !cursor.isClosed()) {
	            cursor.close();
	         }
		}

		return list;
	}

	public List<Imovel> selectEnderecoImovel(String condition){
	
		Cursor cursor;
		List<Imovel> imoveis = null;
	    
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "inscricao",
														        		"rota",
														        		"face",
														        		"codigo_municipio",
														        		"logradouro_imovel",
														        		"bairro_imovel",
														        		"cep_imovel",
														        		"municipio_imovel",
														        		"codigo_logradouro_imovel", "numero_imovel", "complemento_imovel" }, 
														        		condition, null, null, null,  "inscricao asc");
		Imovel imovel = null;
	    if (cursor.moveToFirst()) {
	    	imoveis = new ArrayList<Imovel>();
	    	do {
				imovel = new Imovel();
				imovel.setInscricao(cursor.getString(0));
				imovel.setRota(cursor.getString(1));
				imovel.setFace(cursor.getString(2));
				imovel.setCodigoMunicipio(cursor.getString(3));
				imovel.getEnderecoImovel().setLogradouro(cursor.getString(4));
				imovel.getEnderecoImovel().setBairro(cursor.getString(5));
				imovel.getEnderecoImovel().setCep(cursor.getString(6));
				imovel.getEnderecoImovel().setMunicipio(cursor.getString(7));
				imovel.setCodigoLogradouro(cursor.getString(8));
				imovel.getEnderecoImovel().setNumero(cursor.getString(9));
				imovel.getEnderecoImovel().setComplemento(cursor.getString(10));
				
				imoveis.add(imovel);
	    	} while (cursor.moveToNext());
	    }

	    if (cursor != null && !cursor.isClosed()) {
	       cursor.close();
	    }
	    cursor.close();
	    
	    return imoveis;
}
    
    public List<String> selectIdImoveis(String condition){
    	
    	ArrayList<String> list = new ArrayList<String>();
    	Cursor cursor;
    	
    	if (condition == Constantes.NULO_STRING  || condition == null){

            cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "id"}, null, null, null, null,  "inscricao asc");

    	}else{
    		
            cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "id"}, condition, null, null, null,  "inscricao asc");
    	}

        int x=0;
        if (cursor.moveToFirst()) {
           do {
                String b1= cursor.getString(0);
                list.add(b1);
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}
    
    public List<String> selectIdClientes(String condition){
    	
    	ArrayList<String> list = new ArrayList<String>();
    	Cursor cursor;
 
    	if (condition == Constantes.NULO_STRING  || condition == null){

    		cursor = db.query(Constantes.TABLE_CLIENTE, new String[] { "id"}, null, null, null, null,  "id asc");

    	}else{
    		
    		cursor = db.query(Constantes.TABLE_CLIENTE, new String[] { "id"}, condition, null, null, null,  "id asc");
    	}
    	
        if (cursor.moveToFirst()) {
           do {
                String b1= cursor.getString(0);
                list.add(b1);
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}
    
    public List<String> selectStatusImoveis(String condition){
    	
    	ArrayList<String> list = new ArrayList<String>();
    	Cursor cursor;
        
    	if (condition == Constantes.NULO_STRING  || condition == null){

        	cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status"}, null, null, null, null,  "inscricao asc");

    	}else{
    		
        	cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status"}, condition, null, null, null,  "inscricao asc");
    	}

        int x=0;
        if (cursor.moveToFirst()) {
           do {
                list.add(cursor.getString(0));
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

    public List<Integer> selectNumeroTodosStatusImoveis(){
    	
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	int visitados = 0;
    	int naoVisitados = 0;
    	int visitadosAnormalidade = 0;
    	int novos = 0;
    	int transmitidos = 0;
    	int naoTransmitidos = 0;
    	
        Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status", "imovel_enviado", "matricula"}, null, null, null, null,  "inscricao asc");
        if (cursor.moveToFirst()) {
           do {
        	   
        	   // Contabiliza imoveis novos
        	   if( Integer.parseInt(cursor.getString(0)) == Constantes.IMOVEL_NOVO || Integer.parseInt(cursor.getString(0)) == Constantes.IMOVEL_NOVO_COM_ANORMALIDADE ){
        		   novos++;
        	   }
        	   
        	   // Verifica imovel_status
        	   if ( Integer.parseInt(cursor.getString(0)) == Constantes.IMOVEL_A_SALVAR ){
        		   naoVisitados++;
				
        	   } else if ( Integer.parseInt(cursor.getString(0)) == Constantes.IMOVEL_SALVO){
        		   visitados++;
				
        	   } else if ( Integer.parseInt(cursor.getString(0)) == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE ){
        		   visitadosAnormalidade++;
        	   }
        	   
        	   // Verifica imovel_enviado
        	   if ( Integer.parseInt(cursor.getString(1)) == Constantes.SIM ){
        		   transmitidos++;
        	   
        	   } else if ( Integer.parseInt(cursor.getString(1)) == Constantes.NAO ){
        		   naoTransmitidos++;
        	   }
           
           } while (cursor.moveToNext());
           
           list.add(visitados);
           list.add(naoVisitados);
           list.add(visitadosAnormalidade);
           list.add(novos);
           list.add(transmitidos);
           list.add(naoTransmitidos);

        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

    public void selectCliente(long id){
    	
        Cursor cursor = db.query(Constantes.TABLE_CLIENTE, new String[] {"matricula",
														        		"gerencia", 
														        		"tipo_endereco_proprietario", 
														        		"tipo_endereco_responsavel", 
														        		"usuario_proprietario", 
														        		"tipo_responsavel", 

														        		"nome_usuario", 
														        		"tipo_pessoa_usuario", 
														        		"cpf_cnpj_usuario", 
														        		"rg_usuario", 
														        		"uf_usuario", 
														        		"tipo_sexo_usuario", 
														        		"telefone_usuario",
														        		"celular_usuario", 
														        		"email_usuario",
														        		
														        		"nome_proprietario",
														        		"tipo_pessoa_proprietario", 
														        		"cpf_cnpj_proprietario", 
														        		"rg_proprietario", 
														        		"uf_proprietario", 
														        		"tipo_sexo_proprietario", 
														        		"telefone_proprietario",
														        		"celular_proprietario",
														        		"email_proprietario",
														        		"tipo_logradouro_proprietario",
														        		"logradouro_proprietario",
														        		"numero_proprietario",
														        		"complemento_proprietario", 
														        		"bairro_proprietario", 
														        		"cep_proprietario", 
														        		"municipio_proprietario",
														
														        		"nome_responsavel", 
														        		"tipo_pessoa_responsavel", 
														        		"cpf_cnpj_responsavel", 
														        		"rg_responsavel", 
														        		"uf_responsavel", 
														        		"tipo_sexo_responsavel", 
														        		"telefone_responsavel", 
														        		"celular_responsavel", 
														        		"email_responsavel",
														        		"tipo_logradouro_responsavel",
														        		"logradouro_responsavel",
														        		"numero_responsavel",
														        		"complemento_responsavel", 
														        		"bairro_responsavel", 
														        		"cep_responsavel", 
														        		"municipio_responsavel",
														        		"latitude",
														        		"longitude",
														        		"data",
														        		"matricula_usuario",
														        		"matricula_responsavel",
														        		"matricula_proprietario"}, "id = " + id, null, null, null,  "id asc");
        
        if (cursor.moveToFirst()) {
 
        	getClienteSelecionado().setMatricula(cursor.getString(0));
        	getClienteSelecionado().setNomeGerenciaRegional(cursor.getString(1));
        	getClienteSelecionado().setTipoEnderecoProprietario(cursor.getString(2));
        	getClienteSelecionado().setTipoEnderecoResponsavel(cursor.getString(3));
        	getClienteSelecionado().setUsuarioEProprietario(cursor.getString(4));
        	getClienteSelecionado().setTipoResponsavel(cursor.getString(5));
        	
        	getClienteSelecionado().getUsuario().setNome(cursor.getString(6));
        	getClienteSelecionado().getUsuario().setTipoPessoa(cursor.getString(7));
        	getClienteSelecionado().getUsuario().setCpfCnpj(cursor.getString(8));
        	getClienteSelecionado().getUsuario().setRg(cursor.getString(9));
        	getClienteSelecionado().getUsuario().setUf(cursor.getString(10));
        	getClienteSelecionado().getUsuario().setTipoSexo(cursor.getString(11));
        	getClienteSelecionado().getUsuario().setTelefone(cursor.getString(12));
        	getClienteSelecionado().getUsuario().setCelular(cursor.getString(13));
        	getClienteSelecionado().getUsuario().setEmail(cursor.getString(14));
        	
        	getClienteSelecionado().getProprietario().setNome(cursor.getString(15));
        	getClienteSelecionado().getProprietario().setTipoPessoa(cursor.getString(16));
        	getClienteSelecionado().getProprietario().setCpfCnpj(cursor.getString(17));
        	getClienteSelecionado().getProprietario().setRg(cursor.getString(18));
        	getClienteSelecionado().getProprietario().setUf(cursor.getString(19));
        	getClienteSelecionado().getProprietario().setTipoSexo(cursor.getString(20));
        	getClienteSelecionado().getProprietario().setTelefone(cursor.getString(21));
        	getClienteSelecionado().getProprietario().setCelular(cursor.getString(22));
        	getClienteSelecionado().getProprietario().setEmail(cursor.getString(23));
        	getClienteSelecionado().getEnderecoProprietario().setTipoLogradouro(cursor.getString(24));
        	getClienteSelecionado().getEnderecoProprietario().setLogradouro(cursor.getString(25));
        	getClienteSelecionado().getEnderecoProprietario().setNumero(cursor.getString(26));
        	getClienteSelecionado().getEnderecoProprietario().setComplemento(cursor.getString(27));
        	getClienteSelecionado().getEnderecoProprietario().setBairro(cursor.getString(28));
        	getClienteSelecionado().getEnderecoProprietario().setCep(cursor.getString(29));
        	getClienteSelecionado().getEnderecoProprietario().setMunicipio(cursor.getString(30));

        	getClienteSelecionado().getResponsavel().setNome(cursor.getString(31));
        	getClienteSelecionado().getResponsavel().setTipoPessoa(cursor.getString(32));
        	getClienteSelecionado().getResponsavel().setCpfCnpj(cursor.getString(33));
        	getClienteSelecionado().getResponsavel().setRg(cursor.getString(34));
        	getClienteSelecionado().getResponsavel().setUf(cursor.getString(35));
        	getClienteSelecionado().getResponsavel().setTipoSexo(cursor.getString(36));
        	getClienteSelecionado().getResponsavel().setTelefone(cursor.getString(37));
        	getClienteSelecionado().getResponsavel().setCelular(cursor.getString(38));
        	getClienteSelecionado().getResponsavel().setEmail(cursor.getString(39));
        	getClienteSelecionado().getEnderecoResponsavel().setTipoLogradouro(cursor.getString(40));
        	getClienteSelecionado().getEnderecoResponsavel().setLogradouro(cursor.getString(41));
        	getClienteSelecionado().getEnderecoResponsavel().setNumero(cursor.getString(42));
        	getClienteSelecionado().getEnderecoResponsavel().setComplemento(cursor.getString(43));
        	getClienteSelecionado().getEnderecoResponsavel().setBairro(cursor.getString(44));
        	getClienteSelecionado().getEnderecoResponsavel().setCep(cursor.getString(45));
        	getClienteSelecionado().getEnderecoResponsavel().setMunicipio(cursor.getString(46));

        	getClienteSelecionado().setLatitude(cursor.getString(47));
        	getClienteSelecionado().setLongitude(cursor.getString(48));
        	getClienteSelecionado().setData(cursor.getString(49));
        	
        	getClienteSelecionado().getUsuario().setMatricula(cursor.getInt(50));
        	getClienteSelecionado().getResponsavel().setMatricula(cursor.getInt(51));
        	getClienteSelecionado().getProprietario().setMatricula(cursor.getInt(52));
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
   }
    
   public void selectImovel(long id){
    	
        Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] {"codigo_cliente",
														        		"inscricao",
														        		"rota",
														        		"face",
														        		"codigo_municipio",
														        		"numero_iptu",
														        		"numero_celpa",
														        		"numero_pontos_uteis",
														        		"tipo_logradouro_imovel",
														        		"logradouro_imovel",
														        		"numero_imovel",
														        		"complemento_imovel",
														        		"bairro_imovel",
														        		"cep_imovel",
														        		"municipio_imovel",
														        		"codigo_logradouro_imovel",
														        		"sub_categoria_residencial_1",
														        		"sub_categoria_residencial_2",
														        		"sub_categoria_residencial_3",
														        		"sub_categoria_residencial_4",
														        		"sub_categoria_comercial_1",
														        		"sub_categoria_comercial_2",
														        		"sub_categoria_comercial_3",
														        		"sub_categoria_comercial_4",
														        		"sub_categoria_publica_1",
														        		"sub_categoria_publica_2",
														        		"sub_categoria_publica_3",
														        		"sub_categoria_publica_4",
														        		"sub_categoria_industrial_1",
														        		"sub_categoria_industrial_2",
														        		"sub_categoria_industrial_3",
														        		"sub_categoria_industrial_4",
														        		"tipo_fonte_abastecimento",
														        		"matricula",
														        		"imovel_status",
														        		"imovel_enviado",
														        		"latitude",
														        		"longitude",
														        		"data"}, "id = " + id, null, null, null,  "inscricao asc");
 
        if (cursor.moveToFirst()) {
        	getImovelSelecionado().setImovelId(id);
        	getImovelSelecionado().setCodigoCliente(cursor.getString(0));
        	getImovelSelecionado().setInscricao(cursor.getString(1));
        	getImovelSelecionado().setRota(cursor.getString(2));
        	getImovelSelecionado().setFace(cursor.getString(3));
        	getImovelSelecionado().setCodigoMunicipio(cursor.getString(4));
        	getImovelSelecionado().setIptu(cursor.getString(5));
        	getImovelSelecionado().setNumeroCelpa(cursor.getString(6));
        	getImovelSelecionado().setNumeroPontosUteis(cursor.getString(7));
        	
        	getImovelSelecionado().getEnderecoImovel().setTipoLogradouro(cursor.getString(8));
        	getImovelSelecionado().getEnderecoImovel().setLogradouro(cursor.getString(9));
        	getImovelSelecionado().getEnderecoImovel().setNumero(cursor.getString(10));
        	getImovelSelecionado().getEnderecoImovel().setComplemento(cursor.getString(11));
        	getImovelSelecionado().getEnderecoImovel().setBairro(cursor.getString(12));
        	getImovelSelecionado().getEnderecoImovel().setCep(cursor.getString(13));
        	getImovelSelecionado().getEnderecoImovel().setMunicipio(cursor.getString(14));
        	getImovelSelecionado().setCodigoLogradouro(cursor.getString(15));
        	
        	getImovelSelecionado().getCategoriaResidencial().setEconomiasSubCategoria1(cursor.getString(16));
        	getImovelSelecionado().getCategoriaResidencial().setEconomiasSubCategoria2(cursor.getString(17));
        	getImovelSelecionado().getCategoriaResidencial().setEconomiasSubCategoria3(cursor.getString(18));
        	getImovelSelecionado().getCategoriaResidencial().setEconomiasSubCategoria4(cursor.getString(19));
        	getImovelSelecionado().getCategoriaComercial().setEconomiasSubCategoria1(cursor.getString(20));
        	getImovelSelecionado().getCategoriaComercial().setEconomiasSubCategoria2(cursor.getString(21));
        	getImovelSelecionado().getCategoriaComercial().setEconomiasSubCategoria3(cursor.getString(22));
        	getImovelSelecionado().getCategoriaComercial().setEconomiasSubCategoria4(cursor.getString(23));
        	getImovelSelecionado().getCategoriaPublica().setEconomiasSubCategoria1(cursor.getString(24));
        	getImovelSelecionado().getCategoriaPublica().setEconomiasSubCategoria2(cursor.getString(25));
        	getImovelSelecionado().getCategoriaPublica().setEconomiasSubCategoria3(cursor.getString(26));
        	getImovelSelecionado().getCategoriaPublica().setEconomiasSubCategoria4(cursor.getString(27));
        	getImovelSelecionado().getCategoriaIndustrial().setEconomiasSubCategoria1(cursor.getString(28));
        	getImovelSelecionado().getCategoriaIndustrial().setEconomiasSubCategoria2(cursor.getString(29));
        	getImovelSelecionado().getCategoriaIndustrial().setEconomiasSubCategoria3(cursor.getString(30));
        	getImovelSelecionado().getCategoriaIndustrial().setEconomiasSubCategoria4(cursor.getString(31));
        	
        	getImovelSelecionado().setTipoFonteAbastecimento(cursor.getString(32));
        	getImovelSelecionado().setMatricula(cursor.getString(33));
        	getImovelSelecionado().setImovelStatus(cursor.getString(34));
        	getImovelSelecionado().setImovelEnviado(cursor.getString(35));
        	getImovelSelecionado().setLatitude(cursor.getString(36));
        	getImovelSelecionado().setLongitude(cursor.getString(37));
        	getImovelSelecionado().setData(cursor.getString(38));
       	
        	getImovelSelecionado().setListaRamoAtividade(selectRamoAtividadeImovel(id));
        }

        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();

   }
   
	public void selectServico(long id){

		Cursor cursor = db.query(Constantes.TABLE_SERVICO, new String[] {"tipo_ligacao_agua",
    														        	"tipo_ligacao_esgoto",
    														        	"local_instalacao_ramal",
    														        	"latitude",
    														        	"longitude",
    														        	"data"}, "id = " + id, null, null, null,  "id asc");
        if (cursor.moveToFirst()) {
        	getServicosSelecionado().setTipoLigacaoAgua(cursor.getString(0));
        	getServicosSelecionado().setTipoLigacaoEsgoto(cursor.getString(1));
        	getServicosSelecionado().setLocalInstalacaoRamal(cursor.getString(2));
        	getServicosSelecionado().setLatitude(cursor.getString(3));
        	getServicosSelecionado().setLongitude(cursor.getString(4));
        	getServicosSelecionado().setData(cursor.getString(5));
        }

        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();

	}
            
	public void selectMedidor(long id){
            	
		Cursor cursor = db.query(Constantes.TABLE_MEDIDOR, new String[] {"possui_medidor",
        														        "numero_hidrometro",
        														        "marca",
        														        "capacidade",
        														        "tipo_caixa_protecao",
        														        "latitude",
														        		"longitude",
														        		"data"}, "id = " + id, null, null, null,  "id asc");
        if (cursor.moveToFirst()) {
    		getMedidorSelecionado().setPossuiMedidor(cursor.getString(0));
    		getMedidorSelecionado().setNumeroHidrometro(cursor.getString(1));
    		getMedidorSelecionado().setMarca(cursor.getString(2));
    		getMedidorSelecionado().setCapacidade(cursor.getString(3));
    		getMedidorSelecionado().setTipoCaixaProtecao(cursor.getString(4)); 
    		getMedidorSelecionado().setLatitude(cursor.getString(5));
    		getMedidorSelecionado().setLongitude(cursor.getString(6));
    		getMedidorSelecionado().setData(cursor.getString(7));
        }

        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
	}
                
	public void selectAnormalidadeImovel(long id){
    	
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_ANORMALIDADE_IMOVEL, new String[] {"latitude", 
				 																	 "longitude", 
				 																	 "codigo_anormalidade", 
																					 "comentario", 
																					 "path_image_1", 
																					 "path_image_2", 
																					 "data"}, "id = " + id, null, null, null,  "codigo_anormalidade asc");
        
		if (cursor != null){
        	
			if (cursor.moveToFirst()) {
	    		getAnormalidadeImovelSelecionado().setLatitude(cursor.getString(0));
	    		getAnormalidadeImovelSelecionado().setLongitude(cursor.getString(1));
	    		getAnormalidadeImovelSelecionado().setCodigoAnormalidade(Integer.parseInt(cursor.getString(2)));
	    		getAnormalidadeImovelSelecionado().setComentario(cursor.getString(3));
	    		getAnormalidadeImovelSelecionado().setPathFoto1(cursor.getString(4));
	    		getAnormalidadeImovelSelecionado().setPathFoto2(cursor.getString(5));
	    		getAnormalidadeImovelSelecionado().setData(cursor.getString(6));
	        }
	
	        if (cursor != null && !cursor.isClosed()) {
	           cursor.close();
	        }
	        cursor.close();
        
		}else{
    		getAnormalidadeImovelSelecionado().setCodigoAnormalidade(0);
    		getAnormalidadeImovelSelecionado().setComentario("");
        }
	}
                
	public void selectGeral(){
                	
		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] {"codigo_febraban",
            														    "ano_mes_faturamento",
            														    "telefone0800",
            														    "cnpj_empresa",
            														    "inscricao_estadual_empresa",
            														    "login",
            														    "senha",
            														    "indicador_transmissao_offline",
            														    "versao_celular",
            														    "data_inicio",
            														    "data_fim",
            														    "id_rota",
            														    "localidade",
            														    "setor",
            														    "rota",
            														    "grupo_faturamento"}, null, null, null, null,  "id asc");

        if (cursor.moveToFirst()) {
       	 
     	   getDadosGerais().setCodigoEmpresaFebraban(cursor.getString(0));
     	   getDadosGerais().setAnoMesFaturamento(cursor.getString(1));
    	   getDadosGerais().setTelefone0800(cursor.getString(2));
    	   getDadosGerais().setCnpjEmpresa(cursor.getString(3));
    	   getDadosGerais().setInscricaoEstadualEmpresa(cursor.getString(4));
    	   getDadosGerais().setLogin(cursor.getString(5));
    	   getDadosGerais().setSenha(cursor.getString(6));
    	   getDadosGerais().setIndicadorTransmissaoOffline(cursor.getString(7));
    	   getDadosGerais().setVersaoCelular(cursor.getString(8));
    	   getDadosGerais().setDataInicio(cursor.getString(9));
    	   getDadosGerais().setDataFim(cursor.getString(10));
    	   getDadosGerais().setIdRota(cursor.getString(11));
    	   getDadosGerais().setLocalidade(cursor.getString(12));
    	   getDadosGerais().setSetor(cursor.getString(13));
    	   getDadosGerais().setRota(cursor.getString(14));
    	   getDadosGerais().setGrupoFaturamento(cursor.getString(15));

        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
	}
	
	public int selectConfiguracaoElement(String element){
		   
		int elementValue = 0;
		
		Cursor cursor = db.query(Constantes.TABLE_CONFIGURACAO, new String[] {element}, null, null, null, null,  "id asc");

		if (cursor.moveToFirst()) {
			elementValue = Integer.parseInt(cursor.getString(0));
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		cursor.close();

		return elementValue;
	}
   

	public List<String> selectInformacoesRota(){
		
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] {"grupo_faturamento",
																	   "localidade", 
																	   "setor",
																	   "rota",
																	   "ano_mes_faturamento",
																	   "login"}, null, null, null, null,  "grupo_faturamento asc");
		
		if (cursor.moveToFirst()) {
	           list.add(cursor.getString(0));
	           list.add(cursor.getString(1));
	           list.add(cursor.getString(2));
	           list.add(cursor.getString(3));
	           list.add(cursor.getString(4));
	           list.add(cursor.getString(5));
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

	public List<String> selectAnormalidades(){
    	
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_ANORMALIDADE, new String[] {"codigo", "descricao"}, null, null, null, null,  "codigo asc");
		int x=0;
		if (cursor.moveToFirst()) {
			do {
                String b1= cursor.getString(1);
                list.add(b1);
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

	public ArrayList<String> selectRamoAtividadeImovel(long id){
    	
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, new String[] {"codigo"}, "id_imovel = " + id, null, null, null,  "codigo asc");
		int x=0;
		if (cursor.moveToFirst()) {
			do {
                list.add(cursor.getString(0));
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

	public List<String> selectDescricoesFromTable(String table){
    	
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = db.query(table, new String[] {"codigo", "descricao"}, null, null, null, null,  "descricao asc");
		int x=0;
		if (cursor.moveToFirst()) {
			do {
                String b1= cursor.getString(1);
                list.add(b1);
                x=x+1;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return list;
	}

    public String selectCodigoByDescricaoFromTable(String table, String descricao){
    	
    	String codigo = new String();
        Cursor cursor = db.query(table, new String[] {"codigo"}, "descricao = " + "\""  + descricao + "\"" , null, null, null,  "descricao asc");
        if (cursor.moveToFirst()) {
        	codigo = cursor.getString(0);
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return codigo;
   }

    public String selectDescricaoByCodigoFromTable(String table, String codigo){
    	
    	String descricao = new String();
        Cursor cursor = db.query(table, new String[] {"descricao"}, "codigo = " + "\""  + codigo + "\"" , null, null, null,  "codigo asc");
        if (cursor != null ){
	        if (cursor.moveToFirst()) {
	        	descricao = cursor.getString(0);
	        }
	        if (cursor != null && !cursor.isClosed()) {
	           cursor.close();
	        }
	        cursor.close();
	        return descricao;

        }else{
        	return null;
        }
   }

	public long insertDadosGerais(String linhaArquivo){
	   
		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);
		ContentValues initialValues = new ContentValues();
	   
		initialValues.put("codigo_febraban", parser.obterDadoParser(4));
		initialValues.put("ano_mes_faturamento", parser.obterDadoParser(6));
		initialValues.put("telefone0800", parser.obterDadoParser(12));
		initialValues.put("cnpj_empresa", parser.obterDadoParser(14));
		initialValues.put("inscricao_estadual_empresa", parser.obterDadoParser(20));
		initialValues.put("login", parser.obterDadoParser(11));
		initialValues.put("senha", parser.obterDadoParser(40));
		initialValues.put("indicador_transmissao_offline", parser.obterDadoParser(1));
		initialValues.put("versao_celular", parser.obterDadoParser(10));
		initialValues.put("data_inicio", parser.obterDadoParser(8));
		initialValues.put("data_fim", parser.obterDadoParser(8));
		initialValues.put("id_rota", parser.obterDadoParser(4));
		initialValues.put("localidade", parser.obterDadoParser(3));
		initialValues.put("setor", parser.obterDadoParser(3));
		initialValues.put("rota", parser.obterDadoParser(2));
		initialValues.put("grupo_faturamento", parser.obterDadoParser(3));

		return db.insert(Constantes.TABLE_GERAL, null, initialValues);
	}
   
	public void updateConfiguracao(String parametroName, int value){
		   
		ContentValues initialValues = new ContentValues();
		initialValues.put(parametroName, value);

		if (DatabaseUtils.queryNumEntries(db,Constantes.TABLE_CONFIGURACAO) > 0){
			db.update(Constantes.TABLE_CONFIGURACAO, initialValues, "id=?", new String []{String.valueOf(1)});
			
		}else{
			db.insert(Constantes.TABLE_CONFIGURACAO, null, initialValues);
		}
	}
   
	public long insertCliente(String linhaArquivo){
   	
	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();
	   
	   initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
	   initialValues.put("gerencia", parser.obterDadoParser(25));
	   initialValues.put("tipo_endereco_proprietario", parser.obterDadoParser(1));
	   initialValues.put("tipo_endereco_responsavel", parser.obterDadoParser(1));
	   initialValues.put("usuario_proprietario", parser.obterDadoParser(1));
	   initialValues.put("tipo_responsavel", parser.obterDadoParser(1));

	   initialValues.put("matricula_usuario", parser.obterDadoParser(9));
	   initialValues.put("nome_usuario", parser.obterDadoParser(50));
	   initialValues.put("tipo_pessoa_usuario", parser.obterDadoParser(1));
	   initialValues.put("cpf_cnpj_usuario", parser.obterDadoParser(14).trim());
	   initialValues.put("rg_usuario", parser.obterDadoParser(9).trim());
	   initialValues.put("uf_usuario", parser.obterDadoParser(2));
	   initialValues.put("tipo_sexo_usuario", parser.obterDadoParser(1));
	   initialValues.put("telefone_usuario", parser.obterDadoParser(10));
	   initialValues.put("celular_usuario", parser.obterDadoParser(10));
	   initialValues.put("email_usuario", parser.obterDadoParser(30));
		
	   initialValues.put("matricula_proprietario", parser.obterDadoParser(9));
	   initialValues.put("nome_proprietario", parser.obterDadoParser(50));
	   initialValues.put("tipo_pessoa_proprietario", parser.obterDadoParser(1));
	   initialValues.put("cpf_cnpj_proprietario", parser.obterDadoParser(14).trim());
	   initialValues.put("rg_proprietario", parser.obterDadoParser(9).trim());
	   initialValues.put("uf_proprietario", parser.obterDadoParser(2));
	   initialValues.put("tipo_sexo_proprietario", parser.obterDadoParser(1));
	   initialValues.put("telefone_proprietario", parser.obterDadoParser(10));
	   initialValues.put("celular_proprietario", parser.obterDadoParser(10));
	   initialValues.put("email_proprietario", parser.obterDadoParser(30));
	   initialValues.put("tipo_logradouro_proprietario", parser.obterDadoParser(2));
	   initialValues.put("logradouro_proprietario", parser.obterDadoParser(40));
	   initialValues.put("numero_proprietario", parser.obterDadoParser(5));
	   initialValues.put("complemento_proprietario", parser.obterDadoParser(25));
	   initialValues.put("bairro_proprietario", parser.obterDadoParser(20));
	   initialValues.put("cep_proprietario", parser.obterDadoParser(8));
	   initialValues.put("municipio_proprietario", parser.obterDadoParser(15));
	
	   initialValues.put("matricula_responsavel", parser.obterDadoParser(9));
	   initialValues.put("nome_responsavel", parser.obterDadoParser(50));
	   initialValues.put("tipo_pessoa_responsavel", parser.obterDadoParser(1));
	   initialValues.put("cpf_cnpj_responsavel", parser.obterDadoParser(14).trim());
	   initialValues.put("rg_responsavel", parser.obterDadoParser(9).trim());
	   initialValues.put("uf_responsavel", parser.obterDadoParser(2));
	   initialValues.put("tipo_sexo_responsavel", parser.obterDadoParser(1));
	   initialValues.put("telefone_responsavel", parser.obterDadoParser(10));
	   initialValues.put("celular_responsavel", parser.obterDadoParser(10));
	   initialValues.put("email_responsavel", parser.obterDadoParser(30));
	   initialValues.put("tipo_logradouro_responsavel", parser.obterDadoParser(2));
	   initialValues.put("logradouro_responsavel", parser.obterDadoParser(40));
	   initialValues.put("numero_responsavel", parser.obterDadoParser(5));
	   initialValues.put("complemento_responsavel", parser.obterDadoParser(25));
	   initialValues.put("bairro_responsavel", parser.obterDadoParser(20));
	   initialValues.put("cep_responsavel", parser.obterDadoParser(8));
	   initialValues.put("municipio_responsavel", parser.obterDadoParser(15));
	   initialValues.put("latitude", String.valueOf(Constantes.NULO_DOUBLE));
	   initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
   	   initialValues.put("data", "");
	   
	   return db.insert(Constantes.TABLE_CLIENTE, null, initialValues);
	}

	public long insertImovel(String linhaArquivo){
		ParserUtil parser = new ParserUtil(linhaArquivo);
    	parser.obterDadoParser(2);
    	
	   ContentValues initialValues = new ContentValues();
	   initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
	   initialValues.put("codigo_cliente", parser.obterDadoParser(30));
	   initialValues.put("inscricao", parser.obterDadoParser(17));
	   initialValues.put("rota", parser.obterDadoParser(2));
	   initialValues.put("face", parser.obterDadoParser(2));
	   initialValues.put("codigo_municipio", parser.obterDadoParser(8));
	   initialValues.put("numero_iptu", parser.obterDadoParser(31));
	   initialValues.put("numero_celpa", parser.obterDadoParser(20));
	   initialValues.put("numero_pontos_uteis", parser.obterDadoParser(3));
	   	
	   
	   initialValues.put("tipo_logradouro_imovel", parser.obterDadoParser(2));
	   initialValues.put("logradouro_imovel", parser.obterDadoParser(40));
	   initialValues.put("numero_imovel", parser.obterDadoParser(5));
	   initialValues.put("complemento_imovel", parser.obterDadoParser(25));
	   initialValues.put("bairro_imovel", parser.obterDadoParser(20));
	   initialValues.put("cep_imovel", parser.obterDadoParser(8));
	   initialValues.put("municipio_imovel", parser.obterDadoParser(15));
	   initialValues.put("codigo_logradouro_imovel", parser.obterDadoParser(9));

	   initialValues.put("sub_categoria_residencial_1", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_residencial_2", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_residencial_3", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_residencial_4", parser.obterDadoParser(3));
	   
	   initialValues.put("sub_categoria_comercial_1", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_comercial_2", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_comercial_3", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_comercial_4", parser.obterDadoParser(3));
	   
	   initialValues.put("sub_categoria_publica_1", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_publica_2", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_publica_3", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_publica_4", parser.obterDadoParser(3));
	   
	   initialValues.put("sub_categoria_industrial_1", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_industrial_2", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_industrial_3", parser.obterDadoParser(3));
	   initialValues.put("sub_categoria_industrial_4", parser.obterDadoParser(3));
	   initialValues.put("tipo_fonte_abastecimento", parser.obterDadoParser(2));

	   initialValues.put("imovel_status", String.valueOf(Constantes.IMOVEL_A_SALVAR));
	   initialValues.put("imovel_enviado", String.valueOf(Constantes.NAO));
	   initialValues.put("latitude", String.valueOf(Constantes.NULO_DOUBLE));
	   initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
   	   initialValues.put("data", "");
	   
	   return db.insert(Constantes.TABLE_IMOVEL, null, initialValues);
	}

	public void insertAnormalidadeImovel(String linhaArquivo){
		
		ParserUtil parser = new ParserUtil(linhaArquivo);
    	parser.obterDadoParser(2);

    	ContentValues initialValues = new ContentValues();
    	
    		initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
    		initialValues.put("latitude", String.valueOf(Integer.parseInt(parser.obterDadoParser(15))));
    		initialValues.put("longitude", String.valueOf(Integer.parseInt(parser.obterDadoParser(15))));
    		initialValues.put("codigo_anormalidade", "0");
    		initialValues.put("comentario", "");
    		initialValues.put("path_image_1", "");
    		initialValues.put("path_image_2", "");
    		initialValues.put("data", "");

		   //Verifica se deve atualizar ou inserir um novo elemento na tabela
		   if (Controlador.getInstancia().getIdCadastroSelecionado() > 0){
			   db.update(Constantes.TABLE_ANORMALIDADE_IMOVEL, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
	   		   
		   }else{
			   Controlador.getInstancia().setCadastroSelecionado(db.insert(Constantes.TABLE_ANORMALIDADE_IMOVEL, null, initialValues));
		   }
	}

	public long insertRamosAtividadeImovel(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("matricula", parser.obterDadoParser(9));
		   initialValues.put("id_imovel", selectIdImoveis(null).size());
		   initialValues.put("codigo", parser.obterDadoParser(3));

		   return db.insert(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, null, initialValues);
		}

	public long insertRamosAtividadeImovel(String matricula, long idImovel, String codigo){

		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("matricula", matricula);
		   initialValues.put("id_imovel", idImovel);
		   initialValues.put("codigo", codigo);

		   return db.insert(Constantes.TABLE_RAMO_ATIVIDADE_IMOVEL, null, initialValues);
		}

	public long insertServico(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
		   initialValues.put("tipo_ligacao_agua", parser.obterDadoParser(2));
		   initialValues.put("tipo_ligacao_esgoto", parser.obterDadoParser(2));
		   initialValues.put("local_instalacao_ramal", parser.obterDadoParser(2));
		   initialValues.put("latitude",  String.valueOf(Constantes.NULO_DOUBLE));
		   initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
	   	   initialValues.put("data", "");

		   return db.insert(Constantes.TABLE_SERVICO, null, initialValues);
		}
	    
	public long insertAnormalidade(String linhaArquivo){

	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();
	   
	   initialValues.put("codigo", parser.obterDadoParser(3));
	   initialValues.put("descricao", parser.obterDadoParser(25));

	   return db.insert(Constantes.TABLE_ANORMALIDADE, null, initialValues);
	}
    
	public long insertRamoAtividade(String linhaArquivo){

	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();
	   
	   initialValues.put("codigo", parser.obterDadoParser(3));
	   initialValues.put("descricao", parser.obterDadoParser(20));

	   return db.insert(Constantes.TABLE_RAMO_ATIVIDADE, null, initialValues);
	}
	
	public long insertSituacaoLigacaoAgua(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_SITUACAO_LIGACAO_AGUA, null, initialValues);
	}
		
	public long insertSituacaoLigacaoEsgoto(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_SITUACAO_LIGACAO_ESGOTO, null, initialValues);
	}
		
	public long insertProtecaoHidrometro(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_PROTECAO_HIDROMETRO, null, initialValues);
	}
		
	public long insertFonteAbastecimento(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_FONTE_ABASTECIMENTO, null, initialValues);
	}
			
	public long insertMarcaHidrometro(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(30));

		   return db.insert(Constantes.TABLE_MARCA_HIDROMETRO, null, initialValues);
	}
		
	public long insertLocalInstalacaoRamal(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(30));

		   return db.insert(Constantes.TABLE_LOCAL_INSTALACAO_RAMAL, null, initialValues);
	}
		
	public long insertMedidor(String linhaArquivo){

	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();

	   initialValues.put("matricula", String.valueOf(Integer.parseInt(parser.obterDadoParser(9))));
	   initialValues.put("possui_medidor", parser.obterDadoParser(1));
	   initialValues.put("numero_hidrometro", parser.obterDadoParser(10));
	   initialValues.put("marca", parser.obterDadoParser(2));
	   initialValues.put("capacidade", parser.obterDadoParser(2));
	   initialValues.put("tipo_caixa_protecao", parser.obterDadoParser(2));
	   initialValues.put("latitude", String.valueOf(Constantes.NULO_DOUBLE));
	   initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
   	   initialValues.put("data", "");

	   return db.insert(Constantes.TABLE_MEDIDOR, null, initialValues);
   }
	
	public long insertCapacidadeHidrometro(String linhaArquivo){
	   ParserUtil parser = new ParserUtil(linhaArquivo);
	   parser.obterDadoParser(2);
	   ContentValues initialValues = new ContentValues();
	   
	   initialValues.put("codigo", parser.obterDadoParser(2));
	   initialValues.put("descricao", parser.obterDadoParser(20));

	   return db.insert(Constantes.TABLE_CAPACIDADE_HIDROMETRO, null, initialValues);
	}
	
	public long insertLogradouro(String linhaArquivo){
		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);
		ContentValues initialValues = new ContentValues();
		
		initialValues.put("codigo", parser.obterDadoParser(2));
		initialValues.put("descricao", parser.obterDadoParser(20));
		
		return db.insert(Constantes.TABLE_TIPO_LOGRADOURO, null, initialValues);
	}
	
	public void salvarCliente(){

	   ContentValues initialValues = new ContentValues();
	   initialValues.put("matricula", getImovelSelecionado().getMatricula());
	   initialValues.put("gerencia", getClienteSelecionado().getNomeGerenciaRegional());
	   initialValues.put("tipo_endereco_proprietario", String.valueOf(getClienteSelecionado().getTipoEnderecoProprietario())); 
	   initialValues.put("tipo_endereco_responsavel", String.valueOf(getClienteSelecionado().getTipoEnderecoResponsavel())); 
	   initialValues.put("usuario_proprietario", String.valueOf(getClienteSelecionado().isUsuarioProprietario()));
	   initialValues.put("tipo_responsavel", String.valueOf(getClienteSelecionado().getTipoResponsavel()));

	   initialValues.put("nome_usuario", getClienteSelecionado().getUsuario().getNome());
	   initialValues.put("tipo_pessoa_usuario", String.valueOf(getClienteSelecionado().getUsuario().getTipoPessoa()));
	   initialValues.put("cpf_cnpj_usuario", getClienteSelecionado().getUsuario().getCpfCnpj());
	   initialValues.put("rg_usuario", getClienteSelecionado().getUsuario().getRg());
	   initialValues.put("uf_usuario", getClienteSelecionado().getUsuario().getUf());
	   initialValues.put("tipo_sexo_usuario", getClienteSelecionado().getUsuario().getTipoSexo());
	   initialValues.put("telefone_usuario", getClienteSelecionado().getUsuario().getTelefone());
	   initialValues.put("celular_usuario", getClienteSelecionado().getUsuario().getCelular()); 
	   initialValues.put("email_usuario", getClienteSelecionado().getUsuario().getEmail());
	   
	   initialValues.put("nome_proprietario", getClienteSelecionado().getProprietario().getNome());
	   initialValues.put("tipo_pessoa_proprietario", String.valueOf(getClienteSelecionado().getProprietario().getTipoPessoa()));
	   initialValues.put("cpf_cnpj_proprietario", getClienteSelecionado().getProprietario().getCpfCnpj());
	   initialValues.put("rg_proprietario", getClienteSelecionado().getProprietario().getRg());
	   initialValues.put("uf_proprietario", getClienteSelecionado().getProprietario().getUf());
	   initialValues.put("tipo_sexo_proprietario", getClienteSelecionado().getProprietario().getTipoSexo());
	   initialValues.put("telefone_proprietario", getClienteSelecionado().getProprietario().getTelefone());
	   initialValues.put("celular_proprietario", getClienteSelecionado().getProprietario().getCelular());
	   initialValues.put("email_proprietario", getClienteSelecionado().getProprietario().getEmail());
	   initialValues.put("tipo_logradouro_proprietario", getClienteSelecionado().getEnderecoProprietario().getTipoLogradouro());
	   initialValues.put("logradouro_proprietario", getClienteSelecionado().getEnderecoProprietario().getLogradouro());
	   initialValues.put("numero_proprietario", getClienteSelecionado().getEnderecoProprietario().getNumero());
	   initialValues.put("complemento_proprietario", getClienteSelecionado().getEnderecoProprietario().getComplemento());
	   initialValues.put("bairro_proprietario", getClienteSelecionado().getEnderecoProprietario().getBairro());
	   initialValues.put("cep_proprietario", getClienteSelecionado().getEnderecoProprietario().getCep());
	   initialValues.put("municipio_proprietario", getClienteSelecionado().getEnderecoProprietario().getMunicipio());
	   
	   initialValues.put("nome_responsavel", getClienteSelecionado().getResponsavel().getNome());
	   initialValues.put("tipo_pessoa_responsavel", String.valueOf(getClienteSelecionado().getResponsavel().getTipoPessoa()));
	   initialValues.put("cpf_cnpj_responsavel", getClienteSelecionado().getResponsavel().getCpfCnpj());
	   initialValues.put("rg_responsavel", getClienteSelecionado().getResponsavel().getRg());
	   initialValues.put("uf_responsavel", getClienteSelecionado().getResponsavel().getUf());
	   initialValues.put("tipo_sexo_responsavel", getClienteSelecionado().getResponsavel().getTipoSexo());
	   initialValues.put("telefone_responsavel", getClienteSelecionado().getResponsavel().getTelefone());
	   initialValues.put("celular_responsavel", getClienteSelecionado().getResponsavel().getCelular());
	   initialValues.put("email_responsavel", getClienteSelecionado().getResponsavel().getEmail()); 
	   initialValues.put("tipo_logradouro_responsavel", getClienteSelecionado().getEnderecoResponsavel().getTipoLogradouro());
	   initialValues.put("logradouro_responsavel", getClienteSelecionado().getEnderecoResponsavel().getLogradouro());
	   initialValues.put("numero_responsavel", getClienteSelecionado().getEnderecoResponsavel().getNumero());
	   initialValues.put("complemento_responsavel", getClienteSelecionado().getEnderecoResponsavel().getComplemento());
	   initialValues.put("bairro_responsavel", getClienteSelecionado().getEnderecoResponsavel().getBairro());
	   initialValues.put("cep_responsavel", getClienteSelecionado().getEnderecoResponsavel().getCep());
	   initialValues.put("municipio_responsavel", getClienteSelecionado().getEnderecoResponsavel().getMunicipio());

	   initialValues.put("latitude", getClienteSelecionado().getLatitude());
	   initialValues.put("longitude", getClienteSelecionado().getLongitude());
   	   initialValues.put("data", getClienteSelecionado().getData());

	   //Verifica se deve atualizar ou inserir um novo elemento na tabela
	   if (Controlador.getInstancia().getIdCadastroSelecionado() > 0){
		   db.update(Constantes.TABLE_CLIENTE, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
   		   
	   }else{
		   db.insert(Constantes.TABLE_CLIENTE, null, initialValues);
	   }
	}

	public void salvarImovel(){
		
		if (getImovelSelecionado().getMatricula() < 1000) {
			salvarQtdImoveisNovos(getImovelSelecionado().getMatricula());
		}

	   ContentValues initialValues = new ContentValues();
	   initialValues.put("matricula", getImovelSelecionado().getMatricula());
	   initialValues.put("codigo_cliente", getImovelSelecionado().getCodigoCliente());
	   initialValues.put("inscricao", getImovelSelecionado().getInscricao());
	   initialValues.put("rota", getImovelSelecionado().getRota());
	   initialValues.put("face", getImovelSelecionado().getFace());
	   initialValues.put("codigo_municipio", getImovelSelecionado().getCodigoMunicipio());
	   initialValues.put("numero_iptu", getImovelSelecionado().getIptu());
	   initialValues.put("numero_celpa", getImovelSelecionado().getNumeroCelpa());
	   initialValues.put("numero_pontos_uteis", getImovelSelecionado().getNumeroPontosUteis());
	   initialValues.put("tipo_logradouro_imovel", getImovelSelecionado().getEnderecoImovel().getTipoLogradouro());
	   initialValues.put("logradouro_imovel", getImovelSelecionado().getEnderecoImovel().getLogradouro());
	   initialValues.put("numero_imovel", getImovelSelecionado().getEnderecoImovel().getNumero());
	   initialValues.put("complemento_imovel", getImovelSelecionado().getEnderecoImovel().getComplemento());

	   initialValues.put("bairro_imovel", getImovelSelecionado().getEnderecoImovel().getBairro());
	   initialValues.put("cep_imovel", getImovelSelecionado().getEnderecoImovel().getCep());
	   initialValues.put("municipio_imovel", getImovelSelecionado().getEnderecoImovel().getMunicipio());
	   initialValues.put("codigo_logradouro_imovel", getImovelSelecionado().getCodigoLogradouro()); 
	   initialValues.put("sub_categoria_residencial_1", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria1()));
	   initialValues.put("sub_categoria_residencial_2", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria2()));
	   initialValues.put("sub_categoria_residencial_3", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria3()));
	   initialValues.put("sub_categoria_residencial_4", String.valueOf(getImovelSelecionado().getCategoriaResidencial().getEconomiasSubCategoria4()));
	   
	   initialValues.put("sub_categoria_comercial_1", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria1()));
	   initialValues.put("sub_categoria_comercial_2", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria2()));
	   initialValues.put("sub_categoria_comercial_3", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria3()));
	   initialValues.put("sub_categoria_comercial_4", String.valueOf(getImovelSelecionado().getCategoriaComercial().getEconomiasSubCategoria4()));
	   
	   initialValues.put("sub_categoria_publica_1", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria1()));
	   initialValues.put("sub_categoria_publica_2", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria2()));
	   initialValues.put("sub_categoria_publica_3", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria3()));
	   initialValues.put("sub_categoria_publica_4", String.valueOf(getImovelSelecionado().getCategoriaPublica().getEconomiasSubCategoria4()));
	   
	   initialValues.put("sub_categoria_industrial_1", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria1()));
	   initialValues.put("sub_categoria_industrial_2", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria2()));
	   initialValues.put("sub_categoria_industrial_3", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria3()));
	   initialValues.put("sub_categoria_industrial_4", String.valueOf(getImovelSelecionado().getCategoriaIndustrial().getEconomiasSubCategoria4()));
	   initialValues.put("tipo_fonte_abastecimento", String.valueOf(getImovelSelecionado().getTipoFonteAbastecimento()));
	   initialValues.put("imovel_status", String.valueOf(getImovelSelecionado().getImovelStatus()));
	   initialValues.put("imovel_enviado", String.valueOf(getImovelSelecionado().isImovelEnviado()));
	   
	   initialValues.put("latitude", getImovelSelecionado().getLatitude());
	   initialValues.put("longitude", getImovelSelecionado().getLongitude());
   	   initialValues.put("data", getImovelSelecionado().getData());

   	   //Verifica se deve atualizar ou inserir um novo elemento na tabela
	   if (Controlador.getInstancia().getIdCadastroSelecionado() > 0){
		   db.update(Constantes.TABLE_IMOVEL, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
   		   
   		// deve-se agora substituir os ramos de atividade so imovel. 
   		// Removendo
		deleteRamosAtividadeImovel(Controlador.getInstancia().getIdCadastroSelecionado());
   		
   		// Inserindo
   		if (getImovelSelecionado().getListaRamoAtividade() != null && getImovelSelecionado().getListaRamoAtividade().size() > 0 ){
   			for (int i = 0; i < getImovelSelecionado().getListaRamoAtividade().size(); i++){
   				insertRamosAtividadeImovel(String.valueOf(getImovelSelecionado().getMatricula()), getImovelSelecionado().getImovelId(), getImovelSelecionado().getListaRamoAtividade().get(i));
   			}
   		}

	   }else{
		   db.insert(Constantes.TABLE_IMOVEL, null, initialValues);
	   }
	}

	public void salvarServico(){

	   ContentValues initialValues = new ContentValues();
	   initialValues.put("matricula", getImovelSelecionado().getMatricula());
	   initialValues.put("tipo_ligacao_agua", String.valueOf(getServicosSelecionado().getTipoLigacaoAgua()));
	   initialValues.put("tipo_ligacao_esgoto", String.valueOf(getServicosSelecionado().getTipoLigacaoEsgoto())); 
	   initialValues.put("local_instalacao_ramal", String.valueOf(getServicosSelecionado().getLocalInstalacaoRamal())); 
	   initialValues.put("latitude", String.valueOf(getServicosSelecionado().getLatitude())); 
	   initialValues.put("longitude", String.valueOf(getServicosSelecionado().getLongitude())); 
   	   initialValues.put("data", getServicosSelecionado().getData());

	   //Verifica se deve atualizar ou inserir um novo elemento na tabela
	   if (Controlador.getInstancia().getIdCadastroSelecionado() > 0){
		   db.update(Constantes.TABLE_SERVICO, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
   		   
	   }else{
		   db.insert(Constantes.TABLE_SERVICO, null, initialValues);
	   }
	}

	public void salvarMedidor(){

	   ContentValues initialValues = new ContentValues();
	   initialValues.put("matricula", getImovelSelecionado().getMatricula());
	   initialValues.put("possui_medidor", String.valueOf(getMedidorSelecionado().getPossuiMedidor()));
	   initialValues.put("numero_hidrometro", getMedidorSelecionado().getNumeroHidrometro());
	   initialValues.put("marca", getMedidorSelecionado().getMarca());
	   initialValues.put("capacidade", getMedidorSelecionado().getCapacidade());
	   initialValues.put("tipo_caixa_protecao", String.valueOf(getMedidorSelecionado().getTipoCaixaProtecao())); 
	   initialValues.put("latitude", getMedidorSelecionado().getLatitude());
	   initialValues.put("longitude", getMedidorSelecionado().getLongitude());
   	   initialValues.put("data", getMedidorSelecionado().getData());

	   //Verifica se deve atualizar ou inserir um novo elemento na tabela
	   if (Controlador.getInstancia().getIdCadastroSelecionado() > 0){
		   db.update(Constantes.TABLE_MEDIDOR, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
   		   
	   }else{
		   db.insert(Constantes.TABLE_MEDIDOR, null, initialValues);
	   }
	}

	public void salvarAnormalidadeImovel(){

	   ContentValues initialValues = new ContentValues();
	   initialValues.put("matricula", getImovelSelecionado().getMatricula());
	   initialValues.put("codigo_anormalidade", String.valueOf(getAnormalidadeImovelSelecionado().getCodigoAnormalidade()));
	   initialValues.put("comentario", getAnormalidadeImovelSelecionado().getComentario());
	   initialValues.put("path_image_1", getAnormalidadeImovelSelecionado().getPathFoto1());
	   initialValues.put("path_image_2", getAnormalidadeImovelSelecionado().getPathFoto2());
	   initialValues.put("latitude", getAnormalidadeImovelSelecionado().getLatitude());
	   initialValues.put("longitude", getAnormalidadeImovelSelecionado().getLongitude());
   	   initialValues.put("data", getAnormalidadeImovelSelecionado().getData());

	   //Verifica se deve atualizar ou inserir um novo elemento na tabela
	   if (Controlador.getInstancia().getIdCadastroSelecionado() > 0){
		   db.update(Constantes.TABLE_ANORMALIDADE_IMOVEL, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
   		   
	   }else{
		   Controlador.getInstancia().setCadastroSelecionado(db.insert(Constantes.TABLE_ANORMALIDADE_IMOVEL, null, initialValues));
	   }
	}
	
	public void salvarConfiguracaoElement(String parametroName, int value){
		   
		ContentValues initialValues = new ContentValues();
		initialValues.put("rota_carregada", value);

		db.update(Constantes.TABLE_CONFIGURACAO, initialValues, "id=?", new String []{String.valueOf(1)});
	}
	
	public int getQtdImoveisNovo() {
		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] { "qtd_imoveis_novos" }, null, null, null, null, null);
    	int i = 0;
    	 if (cursor.moveToFirst()) {
    		 i = cursor.getInt(0);
    	 }
    	 
    	 return i;
	}
	
	public void salvarQtdImoveisNovos(int qtsImoveisNovos) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("qtd_imoveis_novos", qtsImoveisNovos);

		db.update(Constantes.TABLE_GERAL, initialValues, "id=?", new String []{String.valueOf(1)});
	}

}