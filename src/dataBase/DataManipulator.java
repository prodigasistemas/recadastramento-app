package dataBase;

import java.util.ArrayList;
import java.util.List;

import model.AnormalidadeImovel;
import model.Cliente;
import model.DadosGerais;
import model.Imovel;
import model.Medidor;
import model.Registro;
import model.Servicos;
import model.Usuario;
import util.Constantes;
import util.ParserUtil;
import util.Util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import business.Controlador;

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
    
    public Usuario getUsuario(){
    	return Controlador.getInstancia().getUsuario();
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
    	String table = Constantes.TABLE_CLIENTE;
    	
    	if(condition.substring(1,7).equals("imovel")){
    		table += " INNER JOIN "+ Constantes.TABLE_IMOVEL +" ON cliente.id=imovel.codigo_cliente ";
		}
    	
    	if (condition == Constantes.NULO_STRING  || condition == null){

    		cursor = db.query(table, new String[] { "cliente.id"}, null, null, null, null, "cliente.id asc");

    	}else{
    		
    		cursor = db.query(table, new String[] { "cliente.id"}, condition, null, null, null, "cliente.id asc");
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
    
	public List<Imovel> selectStatusImoveis(String condicao) {

		List<Imovel> imoveis = new ArrayList<Imovel>();

		Cursor cursor;
		if (condicao == null) {
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status, imovel_enviado" }, null, null, null, null, "inscricao asc");
		} else {
			cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status", "imovel_enviado" }, condicao, null, null, null, "inscricao asc");
		}

		if (cursor.moveToFirst()) {
			do {
				Imovel imovel = new Imovel();
				imovel.setImovelStatus(cursor.getString(0));
				imovel.setImovelEnviado(cursor.getString(1));
				imoveis.add(imovel);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return imoveis;
	}

	public List<Integer> selectNumeroTodosStatusImoveis() {

		ArrayList<Integer> lista = new ArrayList<Integer>();

		int visitados = 0;
		int naoVisitados = 0;
		int visitadosAnormalidade = 0;
		int novos = 0;
		int transmitidos = 0;
		int naoTransmitidos = 0;
		int inconsistencias = 0;

		Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { "imovel_status", "imovel_enviado" }, null, null, null, null, "inscricao asc");
		
		if (cursor.moveToFirst()) {
			do {
				int imovelStatus = Integer.parseInt(cursor.getString(0));

				if (imovelStatus == Constantes.IMOVEL_SALVO) {
					visitados++;
					
				} else if (imovelStatus == Constantes.IMOVEL_A_SALVAR) {
					naoVisitados++;
					
				} else if (imovelStatus == Constantes.IMOVEL_SALVO_COM_ANORMALIDADE) {
					visitadosAnormalidade++;
				}

				if (imovelStatus == Constantes.IMOVEL_NOVO || imovelStatus == Constantes.IMOVEL_NOVO_COM_ANORMALIDADE) {
					novos++;
				}
				
				int imovelEnviado = Integer.parseInt(cursor.getString(1));
				
				if (imovelEnviado == Constantes.SIM) {
					transmitidos++;
				} else if (imovelEnviado == Constantes.NAO) {
					if (imovelStatus == Constantes.IMOVEL_SALVO_COM_INCONSISTENCIA) {
						inconsistencias++;
					} else {
						naoTransmitidos++;
					}
				}

			} while (cursor.moveToNext());

			lista.add(visitados);
			lista.add(naoVisitados);
			lista.add(visitadosAnormalidade);
			lista.add(novos);
			lista.add(transmitidos);
			lista.add(inconsistencias);
			lista.add(naoTransmitidos);
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		cursor.close();
		
		return lista;
	}

    public void selectCliente(long id){
    	
        Cursor cursor = db.query(Constantes.TABLE_CLIENTE, new String[] {"matricula",
														        		"gerencia", 
														        		"tipo_endereco_proprietario", 
														        		"tipo_endereco_responsavel", 
														        		"usuario_proprietario", 
														        		"tipo_responsavel", 

														        		"matricula_usuario", 
														        		"nome_usuario", 
														        		"tipo_pessoa_usuario", 
														        		"cpf_cnpj_usuario", 
														        		"rg_usuario", 
														        		"uf_usuario", 
														        		"tipo_sexo_usuario", 
														        		"telefone_usuario",
														        		"celular_usuario", 
														        		"email_usuario",
														        		
														        		"matricula_proprietario",
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
														
														        		"matricula_responsavel", 
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
        	
        	getClienteSelecionado().getUsuario().setMatricula(cursor.getString(6));
        	getClienteSelecionado().getUsuario().setNome(cursor.getString(7));
        	getClienteSelecionado().getUsuario().setTipoPessoa(cursor.getString(8));
        	getClienteSelecionado().getUsuario().setCpfCnpj(cursor.getString(9));
        	getClienteSelecionado().getUsuario().setRg(cursor.getString(10));
        	getClienteSelecionado().getUsuario().setUf(cursor.getString(11));
        	getClienteSelecionado().getUsuario().setTipoSexo(cursor.getString(12));
        	getClienteSelecionado().getUsuario().setTelefone(cursor.getString(13));
        	getClienteSelecionado().getUsuario().setCelular(cursor.getString(14));
        	getClienteSelecionado().getUsuario().setEmail(cursor.getString(15));
        	
        	getClienteSelecionado().getProprietario().setMatricula(cursor.getString(16));
        	getClienteSelecionado().getProprietario().setNome(cursor.getString(17));
        	getClienteSelecionado().getProprietario().setTipoPessoa(cursor.getString(18));
        	getClienteSelecionado().getProprietario().setCpfCnpj(cursor.getString(19));
        	getClienteSelecionado().getProprietario().setRg(cursor.getString(20));
        	getClienteSelecionado().getProprietario().setUf(cursor.getString(21));
        	getClienteSelecionado().getProprietario().setTipoSexo(cursor.getString(22));
        	getClienteSelecionado().getProprietario().setTelefone(cursor.getString(23));
        	getClienteSelecionado().getProprietario().setCelular(cursor.getString(24));
        	getClienteSelecionado().getProprietario().setEmail(cursor.getString(25));
        	getClienteSelecionado().getEnderecoProprietario().setTipoLogradouro(cursor.getString(26));
        	getClienteSelecionado().getEnderecoProprietario().setLogradouro(cursor.getString(27));
        	getClienteSelecionado().getEnderecoProprietario().setNumero(cursor.getString(28));
        	getClienteSelecionado().getEnderecoProprietario().setComplemento(cursor.getString(29));
        	getClienteSelecionado().getEnderecoProprietario().setBairro(cursor.getString(30));
        	getClienteSelecionado().getEnderecoProprietario().setCep(cursor.getString(31));
        	getClienteSelecionado().getEnderecoProprietario().setMunicipio(cursor.getString(32));

        	getClienteSelecionado().getResponsavel().setMatricula(cursor.getString(33));
        	getClienteSelecionado().getResponsavel().setNome(cursor.getString(34));
        	getClienteSelecionado().getResponsavel().setTipoPessoa(cursor.getString(35));
        	getClienteSelecionado().getResponsavel().setCpfCnpj(cursor.getString(36));
        	getClienteSelecionado().getResponsavel().setRg(cursor.getString(37));
        	getClienteSelecionado().getResponsavel().setUf(cursor.getString(38));
        	getClienteSelecionado().getResponsavel().setTipoSexo(cursor.getString(39));
        	getClienteSelecionado().getResponsavel().setTelefone(cursor.getString(40));
        	getClienteSelecionado().getResponsavel().setCelular(cursor.getString(41));
        	getClienteSelecionado().getResponsavel().setEmail(cursor.getString(42));
        	
        	getClienteSelecionado().getEnderecoResponsavel().setTipoLogradouro(cursor.getString(43));
        	getClienteSelecionado().getEnderecoResponsavel().setLogradouro(cursor.getString(44));
        	getClienteSelecionado().getEnderecoResponsavel().setNumero(cursor.getString(45));
        	getClienteSelecionado().getEnderecoResponsavel().setComplemento(cursor.getString(46));
        	getClienteSelecionado().getEnderecoResponsavel().setBairro(cursor.getString(47));
        	getClienteSelecionado().getEnderecoResponsavel().setCep(cursor.getString(48));
        	getClienteSelecionado().getEnderecoResponsavel().setMunicipio(cursor.getString(49));

        	getClienteSelecionado().setLatitude(cursor.getString(50));
        	getClienteSelecionado().setLongitude(cursor.getString(51));
        	getClienteSelecionado().setData(cursor.getString(52));
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
   }
    
    public Cliente selectClientePorId(long id) {
    	
        Cursor cursor = db.query(Constantes.TABLE_CLIENTE, new String[] {"matricula",
														        		"gerencia", 
														        		"tipo_endereco_proprietario", 
														        		"tipo_endereco_responsavel", 
														        		"usuario_proprietario", 
														        		"tipo_responsavel", 

														        		"matricula_usuario", 
														        		"nome_usuario", 
														        		"tipo_pessoa_usuario", 
														        		"cpf_cnpj_usuario", 
														        		"rg_usuario", 
														        		"uf_usuario", 
														        		"tipo_sexo_usuario", 
														        		"telefone_usuario",
														        		"celular_usuario", 
														        		"email_usuario",
														        		
														        		"matricula_proprietario",
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
														
														        		"matricula_responsavel", 
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
        
        Cliente cliente = null;
        
        if (cursor.moveToFirst()) {
        	cliente = new Cliente();
        	
        	cliente.setMatricula(cursor.getString(0));
        	cliente.setNomeGerenciaRegional(cursor.getString(1));
        	cliente.setTipoEnderecoProprietario(cursor.getString(2));
        	cliente.setTipoEnderecoResponsavel(cursor.getString(3));
        	cliente.setUsuarioEProprietario(cursor.getString(4));
        	cliente.setTipoResponsavel(cursor.getString(5));
        	
        	cliente.getUsuario().setMatricula(cursor.getString(6));
        	cliente.getUsuario().setNome(cursor.getString(7));
        	cliente.getUsuario().setTipoPessoa(cursor.getString(8));
        	cliente.getUsuario().setCpfCnpj(cursor.getString(9));
        	cliente.getUsuario().setRg(cursor.getString(10));
        	cliente.getUsuario().setUf(cursor.getString(11));
        	cliente.getUsuario().setTipoSexo(cursor.getString(12));
        	cliente.getUsuario().setTelefone(cursor.getString(13));
        	cliente.getUsuario().setCelular(cursor.getString(14));
        	cliente.getUsuario().setEmail(cursor.getString(15));
        	
        	cliente.getProprietario().setMatricula(cursor.getString(16));
        	cliente.getProprietario().setNome(cursor.getString(17));
        	cliente.getProprietario().setTipoPessoa(cursor.getString(18));
        	cliente.getProprietario().setCpfCnpj(cursor.getString(19));
        	cliente.getProprietario().setRg(cursor.getString(20));
        	cliente.getProprietario().setUf(cursor.getString(21));
        	cliente.getProprietario().setTipoSexo(cursor.getString(22));
        	cliente.getProprietario().setTelefone(cursor.getString(23));
        	cliente.getProprietario().setCelular(cursor.getString(24));
        	cliente.getProprietario().setEmail(cursor.getString(25));
        	cliente.getEnderecoProprietario().setTipoLogradouro(cursor.getString(26));
        	cliente.getEnderecoProprietario().setLogradouro(cursor.getString(27));
        	cliente.getEnderecoProprietario().setNumero(cursor.getString(28));
        	cliente.getEnderecoProprietario().setComplemento(cursor.getString(29));
        	cliente.getEnderecoProprietario().setBairro(cursor.getString(30));
        	cliente.getEnderecoProprietario().setCep(cursor.getString(31));
        	cliente.getEnderecoProprietario().setMunicipio(cursor.getString(32));

        	cliente.getResponsavel().setMatricula(cursor.getString(33));
        	cliente.getResponsavel().setNome(cursor.getString(34));
        	cliente.getResponsavel().setTipoPessoa(cursor.getString(35));
        	cliente.getResponsavel().setCpfCnpj(cursor.getString(36));
        	cliente.getResponsavel().setRg(cursor.getString(37));
        	cliente.getResponsavel().setUf(cursor.getString(38));
        	cliente.getResponsavel().setTipoSexo(cursor.getString(39));
        	cliente.getResponsavel().setTelefone(cursor.getString(40));
        	cliente.getResponsavel().setCelular(cursor.getString(41));
        	cliente.getResponsavel().setEmail(cursor.getString(42));
        	
        	cliente.getEnderecoResponsavel().setTipoLogradouro(cursor.getString(43));
        	cliente.getEnderecoResponsavel().setLogradouro(cursor.getString(44));
        	cliente.getEnderecoResponsavel().setNumero(cursor.getString(45));
        	cliente.getEnderecoResponsavel().setComplemento(cursor.getString(46));
        	cliente.getEnderecoResponsavel().setBairro(cursor.getString(47));
        	cliente.getEnderecoResponsavel().setCep(cursor.getString(48));
        	cliente.getEnderecoResponsavel().setMunicipio(cursor.getString(49));

        	cliente.setLatitude(cursor.getString(50));
        	cliente.setLongitude(cursor.getString(51));
        	cliente.setData(cursor.getString(52));
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        
        return cliente;
   }
    
	public void selectImovel(long id) {

		Cursor cursor = getCursorTabelaImovel("id = " + id);

		if (cursor.moveToFirst()) {
			montarImovel(cursor, id, getImovelSelecionado());
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
	
	public Servicos selectServicos(long id){
		Cursor cursor = db.query(Constantes.TABLE_SERVICO, new String[] {"tipo_ligacao_agua",
    														        	"tipo_ligacao_esgoto",
    														        	"local_instalacao_ramal",
    														        	"latitude",
    														        	"longitude",
    														        	"data"}, "id = " + id, null, null, null,  "id asc");
		Servicos servico = null;
		
        if (cursor.moveToFirst()) {
        	servico = new Servicos();
        	servico.setTipoLigacaoAgua(cursor.getString(0));
        	servico.setTipoLigacaoEsgoto(cursor.getString(1));
        	servico.setLocalInstalacaoRamal(cursor.getString(2));
        	servico.setLatitude(cursor.getString(3));
        	servico.setLongitude(cursor.getString(4));
        	servico.setData(cursor.getString(5));
        }

        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();

        return servico;
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
	
	public Medidor selectMedidorPorId(long id) {
		Cursor cursor = db.query(Constantes.TABLE_MEDIDOR, new String[] {"possui_medidor",
        														        "numero_hidrometro",
        														        "marca",
        														        "capacidade",
        														        "tipo_caixa_protecao",
        														        "latitude",
														        		"longitude",
														        		"data"}, "id = " + id, null, null, null,  "id asc");
		
		Medidor medidor = null;
        if (cursor.moveToFirst()) {
        	medidor = new Medidor();
    		medidor.setPossuiMedidor(cursor.getString(0));
    		medidor.setNumeroHidrometro(cursor.getString(1));
    		medidor.setMarca(cursor.getString(2));
    		medidor.setCapacidade(cursor.getString(3));
    		medidor.setTipoCaixaProtecao(cursor.getString(4)); 
    		medidor.setLatitude(cursor.getString(5));
    		medidor.setLongitude(cursor.getString(6));
    		medidor.setData(cursor.getString(7));
        }

        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        
        return medidor;
	}
                
	public void selectAnormalidadeImovel(long id){
		Cursor cursor = db.query(Constantes.TABLE_ANORMALIDADE_IMOVEL, new String[] {"latitude", 
				 																	 "longitude", 
				 																	 "codigo_anormalidade", 
																					 "comentario", 
																					 "path_image_1", 
																					 "path_image_2", 
																					 "data"}, "id=?", new String[] {String.valueOf(id)}, null, null,  "codigo_anormalidade asc");
        
		if (cursor != null){
        	
			if (cursor.moveToFirst()) {
	    		getAnormalidadeImovelSelecionado().setLatitude(cursor.getString(0));
	    		getAnormalidadeImovelSelecionado().setLongitude(cursor.getString(1));
	    		getAnormalidadeImovelSelecionado().setCodigoAnormalidade(Integer.parseInt(cursor.getString(2)));
	    		getAnormalidadeImovelSelecionado().setComentario(cursor.getString(3));
	    		getAnormalidadeImovelSelecionado().setFoto1(cursor.getString(4));
	    		getAnormalidadeImovelSelecionado().setFoto2(cursor.getString(5));
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
	
	public AnormalidadeImovel selectAnormalidadeImovel(String matricula) {

		AnormalidadeImovel anormalidadeImovel = new AnormalidadeImovel();

		Cursor cursor = db.query(Constantes.TABLE_ANORMALIDADE_IMOVEL, new String[] { 
				"latitude", 
				"longitude", 
				"codigo_anormalidade", 
				"comentario",
				"path_image_1", 
				"path_image_2", 
				"data", 
				"matricula", 
				"login_usuario"}, 
				"matricula=?", new String[] { matricula }, null, null, "codigo_anormalidade asc");

		if (cursor != null) {

			if (cursor.moveToFirst()) {
				anormalidadeImovel.setLatitude(cursor.getString(0));
				anormalidadeImovel.setLongitude(cursor.getString(1));
				anormalidadeImovel.setCodigoAnormalidade(Integer.parseInt(cursor.getString(2)));
				anormalidadeImovel.setComentario(cursor.getString(3));
				anormalidadeImovel.setFoto1(cursor.getString(4));
				anormalidadeImovel.setFoto2(cursor.getString(5));
				anormalidadeImovel.setData(cursor.getString(6));
				anormalidadeImovel.setMatricula(Integer.parseInt(cursor.getString(7)));
				anormalidadeImovel.setLoginUsuario(cursor.getString(8));

				Controlador.getInstancia().setAnormalidadeImovelSelecionado(anormalidadeImovel);
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			
			cursor.close();
		}

		return anormalidadeImovel;
	}
                
	public void selectGeral() {

		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] { 
				"id_rota",
				"localidade",
				"setor",
				"rota",
				"versao_arquivo",
				"nome_arquivo",
				"tipo_arquivo",
				"id_arquivo"},
				null, null, null, null, "id asc");

		if (cursor.moveToFirst()) {
			getDadosGerais().setIdRota(cursor.getString(0));
			getDadosGerais().setLocalidade(cursor.getString(1));
			getDadosGerais().setSetor(cursor.getString(2));
			getDadosGerais().setRota(cursor.getString(3));
			getDadosGerais().setVersaoArquivo(cursor.getString(4));
			getDadosGerais().setNomeArquivo(cursor.getString(5));
			getDadosGerais().setTipoArquivo(cursor.getString(6));
			getDadosGerais().setIdArquivo(cursor.getString(7));
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
   

	public List<String> selectInformacoesRota() {
		ArrayList<String> lista = new ArrayList<String>();
		Cursor cursor = db.query(Constantes.TABLE_GERAL, new String[] { 
				"localidade", 
				"setor", 
				"rota", 
				"nome_arquivo", 
				"tipo_arquivo" },
				null, null, null, null, "localidade asc");

		if (cursor.moveToFirst()) {
			lista.add(cursor.getString(0));
			lista.add(cursor.getString(1));
			lista.add(cursor.getString(2));
			lista.add(cursor.getString(3));
			lista.add(cursor.getString(4));
		}
		
		cursor.close();

		return lista;
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
		String asc = null;
		if(!table.equals(Constantes.TABLE_TIPO_USO) && 
				!table.equals(Constantes.TABLE_CLASSE_SOCIAL) &&
				!table.equals(Constantes.TABLE_ACESSO_HIDROMETRO)) asc = "descricao asc";
		
		Cursor cursor = db.query(table, new String[] {"codigo", "descricao"}, null, null, null, null, asc);
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
	
	public Usuario selectUsuario(String login) {
		Cursor cursor = db.query(Constantes.TABLE_USUARIO, new String[] { "nome", "login", "senha" }, "login = '" + login + "'", null, null, null, "nome ASC");
		Usuario usuario = null;
			
		if (cursor.moveToFirst()) {
			usuario = new Usuario();
					
			usuario.setNome(cursor.getString(0));
			usuario.setLogin(cursor.getString(1));
			usuario.setSenha(cursor.getString(2));
			
			Controlador.getInstancia().setUsuario(usuario);
		} else {
			Controlador.getInstancia().setUsuario(null);
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		cursor.close();
		return usuario;
	}

	public long insertDadosGerais(String linhaArquivo, String nomeArquivo) {
		ParserUtil parser = new ParserUtil(linhaArquivo);
		ContentValues initialValues = new ContentValues();

		parser.obterDadoParser(2);
		parser.obterDadoParser(4);
		parser.obterDadoParser(6);
		parser.obterDadoParser(12);
		parser.obterDadoParser(14);
		parser.obterDadoParser(20);
		parser.obterDadoParser(11);
		parser.obterDadoParser(40);
		parser.obterDadoParser(1);
		initialValues.put("versao_arquivo", Util.removerZerosAEsquerda(parser.obterDadoParser(10)));
		parser.obterDadoParser(8);
		parser.obterDadoParser(8);
		initialValues.put("id_rota", parser.obterDadoParser(4));
		initialValues.put("localidade", parser.obterDadoParser(3));
		initialValues.put("setor", parser.obterDadoParser(3));
		initialValues.put("rota", parser.obterDadoParser(2));
		parser.obterDadoParser(3);
		initialValues.put("nome_arquivo", nomeArquivo.replace(".txt", "").replace(".zip", ""));
		initialValues.put("tipo_arquivo", parser.obterDadoParser(1));
		initialValues.put("id_arquivo", Util.removerZerosAEsquerda(parser.obterDadoParser(11)));

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

	public long insertCliente(String linhaArquivo) {
		int tamanhoLinha = linhaArquivo.length();

		ParserUtil parser = new ParserUtil(linhaArquivo);
		parser.obterDadoParser(2);
		ContentValues initialValues = new ContentValues();

		initialValues.put("matricula", parser.obterDadoParser(9));
		initialValues.put("gerencia", parser.obterDadoParser(25));
		initialValues.put("tipo_endereco_proprietario", parser.obterDadoParser(1));
		initialValues.put("tipo_endereco_responsavel", parser.obterDadoParser(1));
		initialValues.put("usuario_proprietario", parser.obterDadoParser(1));
		initialValues.put("tipo_responsavel", parser.obterDadoParser(1));

		int tamanhoTelefoneUsuario = getTamanhoTelefone(tamanhoLinha);
		int tamanhoCelularUsuario = getTamanhoCelularUsuario(tamanhoLinha);

		initialValues.put("matricula_usuario", parser.obterDadoParser(9));
		initialValues.put("nome_usuario", parser.obterDadoParser(50));
		initialValues.put("tipo_pessoa_usuario", parser.obterDadoParser(1));
		initialValues.put("cpf_cnpj_usuario", parser.obterDadoParser(14).trim());
		initialValues.put("rg_usuario", parser.obterDadoParser(13).trim());
		initialValues.put("uf_usuario", parser.obterDadoParser(2));
		initialValues.put("tipo_sexo_usuario", parser.obterDadoParser(1));
		initialValues.put("telefone_usuario", parser.obterDadoParser(tamanhoTelefoneUsuario));
		initialValues.put("celular_usuario", parser.obterDadoParser(tamanhoCelularUsuario));
		initialValues.put("email_usuario", parser.obterDadoParser(30));

		String matriculaProprietario = parser.obterDadoParser(9);

		int tamanhoTelefoneProprietario = getTamanhoTelefone(tamanhoLinha);
		int tamanhoCelularProprietario = getTamanhoCelularProprietarioOuResponsavel(tamanhoLinha, matriculaProprietario);

		initialValues.put("matricula_proprietario", matriculaProprietario);
		initialValues.put("nome_proprietario", parser.obterDadoParser(50));
		initialValues.put("tipo_pessoa_proprietario", parser.obterDadoParser(1));
		initialValues.put("cpf_cnpj_proprietario", parser.obterDadoParser(14).trim());
		initialValues.put("rg_proprietario", parser.obterDadoParser(13).trim());
		initialValues.put("uf_proprietario", parser.obterDadoParser(2));
		initialValues.put("tipo_sexo_proprietario", parser.obterDadoParser(1));
		initialValues.put("telefone_proprietario", parser.obterDadoParser(tamanhoTelefoneProprietario));
		initialValues.put("celular_proprietario", parser.obterDadoParser(tamanhoCelularProprietario));
		initialValues.put("email_proprietario", parser.obterDadoParser(30));
		initialValues.put("tipo_logradouro_proprietario", parser.obterDadoParser(2));
		initialValues.put("logradouro_proprietario", parser.obterDadoParser(40));
		initialValues.put("numero_proprietario", parser.obterDadoParser(5));
		initialValues.put("complemento_proprietario", parser.obterDadoParser(25));
		initialValues.put("bairro_proprietario", parser.obterDadoParser(20));
		initialValues.put("cep_proprietario", parser.obterDadoParser(8));
		initialValues.put("municipio_proprietario", parser.obterDadoParser(15));

		String matriculaResponsavel = parser.obterDadoParser(9);

		int tamanhoTelefoneResponsavel = getTamanhoTelefone(tamanhoLinha);
		int tamanhoCelularResponsavel = getTamanhoCelularProprietarioOuResponsavel(tamanhoLinha, matriculaResponsavel);

		initialValues.put("matricula_responsavel", matriculaResponsavel);
		initialValues.put("nome_responsavel", parser.obterDadoParser(50));
		initialValues.put("tipo_pessoa_responsavel", parser.obterDadoParser(1));
		initialValues.put("cpf_cnpj_responsavel", parser.obterDadoParser(14).trim());
		initialValues.put("rg_responsavel", parser.obterDadoParser(13).trim());
		initialValues.put("uf_responsavel", parser.obterDadoParser(2));
		initialValues.put("tipo_sexo_responsavel", parser.obterDadoParser(1));
		initialValues.put("telefone_responsavel", parser.obterDadoParser(tamanhoTelefoneResponsavel));
		initialValues.put("celular_responsavel", parser.obterDadoParser(tamanhoCelularResponsavel));
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

	private int getTamanhoTelefone(int tamanhoLinha) {
		if (tamanhoLinha == 696 || tamanhoLinha == 695) {
			return 11;
		} else {
			return 10;
		}
	}

	private int getTamanhoCelularUsuario(int tamanhoLinha) {
		if (tamanhoLinha == 690) {
			return 10;
		} else {
			return 11;
		}
	}

	private int getTamanhoCelularProprietarioOuResponsavel(int tamanhoLinha, String matricula) {
		int tamanho = 0;

		if (tamanhoLinha == 690) {
			tamanho = 10;
		} else if (tamanhoLinha == 696) {
			return 11;
		} else {
			if (matricula.trim().length() == 0) {
				tamanho = 10;
			} else {
				tamanho = 11;
			}
		}

		return tamanho;
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
	   initialValues.put("numero_pontos_uteis", parser.obterDadoParser(5));
	   initialValues.put("numero_ocupantes", parser.obterDadoParser(5));
	   
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
	   
	   initialValues.put("area_construida", parser.obterDadoParser(10));
	   initialValues.put("classe_social", parser.obterDadoParser(1));
	   initialValues.put("numero_animais", parser.obterDadoParser(4));
	   
	   initialValues.put("volume_cisterna", parser.obterDadoParser(7));
	   initialValues.put("volume_piscina", parser.obterDadoParser(7));
	   initialValues.put("volume_caixa_dagua", parser.obterDadoParser(7));
	   
	   initialValues.put("tipo_uso", parser.obterDadoParser(1));
	   initialValues.put("acesso_hidrometro", parser.obterDadoParser(1));
	   
	   initialValues.put("numero_criancas", parser.obterDadoParser(4));
	   initialValues.put("numero_adultos", parser.obterDadoParser(4));
	   initialValues.put("numero_idosos", parser.obterDadoParser(4));
	   initialValues.put("numero_empregados", parser.obterDadoParser(4));
	   initialValues.put("numero_alunos", parser.obterDadoParser(4));
	   initialValues.put("numero_caes", parser.obterDadoParser(4));
	   initialValues.put("numero_outros", parser.obterDadoParser(4));
	   
	   initialValues.put("quantidade_economias_social", parser.obterDadoParser(3));
	   initialValues.put("quantidade_economias_outros", parser.obterDadoParser(3));
	   
	   initialValues.put("observacao", parser.obterDadoParser(100));

	   initialValues.put("percentual_abastecimento", parser.obterDadoParser(3) != null ? parser.getConteudo() : "000");
	   
	   initialValues.put("imovel_status", String.valueOf(Constantes.IMOVEL_A_SALVAR));
	   initialValues.put("imovel_enviado", String.valueOf(Constantes.NAO));
	   initialValues.put("latitude", String.valueOf(Constantes.NULO_DOUBLE));
	   initialValues.put("longitude", String.valueOf(Constantes.NULO_DOUBLE));
   	   initialValues.put("data", "");
   	   initialValues.put("entrevistado", "");
   	   initialValues.put("tipo_operacao", Constantes.OPERACAO_CADASTRO_ALTERADO);
	   
	   return db.insert(Constantes.TABLE_IMOVEL, null, initialValues);
	}

	public long insertAnormalidadeImovel(String linhaArquivo) {
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
		
		return db.insert(Constantes.TABLE_ANORMALIDADE_IMOVEL, null, initialValues);
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
	
	public long insertClasseSocial(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_CLASSE_SOCIAL, null, initialValues);
	}
	
	public long insertTipoUso(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_TIPO_USO, null, initialValues);
	}
	
	public long insertAcessoHidrometro(String linhaArquivo){

		   ParserUtil parser = new ParserUtil(linhaArquivo);
		   parser.obterDadoParser(2);
		   ContentValues initialValues = new ContentValues();
		   
		   initialValues.put("codigo", parser.obterDadoParser(2));
		   initialValues.put("descricao", parser.obterDadoParser(20));

		   return db.insert(Constantes.TABLE_ACESSO_HIDROMETRO, null, initialValues);
	}
	
	public long insertUsuario(String linhaArquivo) {
		ParserUtil parser = new ParserUtil(linhaArquivo);
		ContentValues initialValues = new ContentValues();

		parser.obterDadoParser(2);
		initialValues.put("nome", parser.obterDadoParser(50).trim());
		initialValues.put("login", parser.obterDadoParser(11).trim());
		initialValues.put("senha", parser.obterDadoParser(40).trim());

		return db.insert(Constantes.TABLE_USUARIO, null, initialValues);
	}
	
	public void salvarCliente(){

	   ContentValues initialValues = new ContentValues();
	   initialValues.put("matricula", getImovelSelecionado().getMatricula());
	   initialValues.put("gerencia", getClienteSelecionado().getNomeGerenciaRegional());
	   initialValues.put("tipo_endereco_proprietario", String.valueOf(getClienteSelecionado().getTipoEnderecoProprietario())); 
	   initialValues.put("tipo_endereco_responsavel", String.valueOf(getClienteSelecionado().getTipoEnderecoResponsavel())); 
	   initialValues.put("usuario_proprietario", String.valueOf(getClienteSelecionado().isUsuarioProprietario()));
	   initialValues.put("tipo_responsavel", String.valueOf(getClienteSelecionado().getTipoResponsavel()));

	   if (getClienteSelecionado().isNovoUsuario())
		   initialValues.put("matricula_usuario", "0");
	   
	   initialValues.put("nome_usuario", getClienteSelecionado().getUsuario().getNome());
	   initialValues.put("tipo_pessoa_usuario", String.valueOf(getClienteSelecionado().getUsuario().getTipoPessoa()));
	   initialValues.put("cpf_cnpj_usuario", getClienteSelecionado().getUsuario().getCpfCnpj());
	   initialValues.put("rg_usuario", getClienteSelecionado().getUsuario().getRg());
	   initialValues.put("uf_usuario", getClienteSelecionado().getUsuario().getUf());
	   initialValues.put("tipo_sexo_usuario", getClienteSelecionado().getUsuario().getTipoSexo());
	   initialValues.put("telefone_usuario", getClienteSelecionado().getUsuario().getTelefone());
	   initialValues.put("celular_usuario", getClienteSelecionado().getUsuario().getCelular()); 
	   initialValues.put("email_usuario", getClienteSelecionado().getUsuario().getEmail());
	   
	   if (getClienteSelecionado().isNovoProprietario())
		   initialValues.put("matricula_proprietario", "0");
	   
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
	   
	   if (getClienteSelecionado().isNovoResponsavel())
		   initialValues.put("matricula_responsavel", "0");
	   
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
	   if (Controlador.getInstancia().getClienteSelecionado().isNovoRegistro()){
		   db.insert(Constantes.TABLE_CLIENTE, null, initialValues);
		   Controlador.getInstancia().getClienteSelecionado().setNovoRegistro(false);
	   }else{
		   db.update(Constantes.TABLE_CLIENTE, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
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
	   initialValues.put("numero_ocupantes", getImovelSelecionado().getNumeroOcupantes());
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
	   initialValues.put("imovel_enviado", String.valueOf(getImovelSelecionado().getImovelEnviado()));
	   
	   initialValues.put("latitude", getImovelSelecionado().getLatitude());
	   initialValues.put("longitude", getImovelSelecionado().getLongitude());
   	   initialValues.put("data", getImovelSelecionado().getData());
   	   initialValues.put("entrevistado", getImovelSelecionado().getEntrevistado());
   	   initialValues.put("tipo_operacao", getImovelSelecionado().getOperacaoTipo());
   	   
   	   //novos campos de cadastramento
   	   initialValues.put("area_construida", getImovelSelecionado().getAreaConstruida());
   	   initialValues.put("classe_social", getImovelSelecionado().getClasseSocial());
   	   initialValues.put("numero_animais", getImovelSelecionado().getNumeroAnimais());
	   initialValues.put("volume_piscina", getImovelSelecionado().getVolumePiscina());
	   initialValues.put("volume_cisterna", getImovelSelecionado().getVolumeCisterna());
	   initialValues.put("volume_caixa_dagua", getImovelSelecionado().getVolumeCaixaDagua());
	   initialValues.put("tipo_uso", getImovelSelecionado().getTipoUso());
	   initialValues.put("acesso_hidrometro", getImovelSelecionado().getAcessoHidrometro());
	   
	   initialValues.put("numero_criancas", getImovelSelecionado().getOcupacaoImovel().getCriancas());
	   initialValues.put("numero_adultos", getImovelSelecionado().getOcupacaoImovel().getAdultos());
	   initialValues.put("numero_alunos", getImovelSelecionado().getOcupacaoImovel().getAlunos());
	   initialValues.put("numero_caes", getImovelSelecionado().getOcupacaoImovel().getCaes());
	   initialValues.put("numero_idosos", getImovelSelecionado().getOcupacaoImovel().getIdosos());
	   initialValues.put("numero_empregados", getImovelSelecionado().getOcupacaoImovel().getEmpregados());
	   initialValues.put("numero_outros", getImovelSelecionado().getOcupacaoImovel().getOutros());
	   
	   initialValues.put("quantidade_economias_social", getImovelSelecionado().getQuantidadeEconomiasSocial());
	   initialValues.put("quantidade_economias_outros", getImovelSelecionado().getQuantidadeEconomiasOutros());
	   
	   initialValues.put("observacao", getImovelSelecionado().getObservacao());
	   
	   initialValues.put("percentual_abastecimento", getImovelSelecionado().getPercentualAbastecimento());

   	   //Verifica se deve atualizar ou inserir um novo elemento na tabela
	   if (Controlador.getInstancia().getImovelSelecionado().isNovoRegistro()){
		   db.insert(Constantes.TABLE_IMOVEL, null, initialValues);
		   Controlador.getInstancia().getImovelSelecionado().setNovoRegistro(false);
	   }else{
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

	   }
	}
	
	public void salvarStatusImovel(Imovel imovel) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("imovel_status", imovel.getImovelStatus());
		initialValues.put("imovel_enviado", imovel.getImovelEnviado());

		db.update(Constantes.TABLE_IMOVEL, initialValues, "id=?", new String[] { String.valueOf(imovel.getImovelId()) });
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
	   if (Controlador.getInstancia().getServicosSelecionado().isNovoRegistro()){
		   db.insert(Constantes.TABLE_SERVICO, null, initialValues);
		   Controlador.getInstancia().getServicosSelecionado().setNovoRegistro(false);
	   }else{
		   db.update(Constantes.TABLE_SERVICO, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
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
	   if (Controlador.getInstancia().getMedidorSelecionado().isNovoRegistro()){
		   db.insert(Constantes.TABLE_MEDIDOR, null, initialValues);
		   Controlador.getInstancia().getMedidorSelecionado().setNovoRegistro(false);
	   }else{
		   db.update(Constantes.TABLE_MEDIDOR, initialValues, "id=?", new String []{String.valueOf(Controlador.getInstancia().getIdCadastroSelecionado())});
	   }
	}

	public void salvarAnormalidadeImovel() {
		ContentValues initialValues = new ContentValues();
		
		initialValues.put("matricula", getImovelSelecionado().getMatricula());
		initialValues.put("codigo_anormalidade", String.valueOf(getAnormalidadeImovelSelecionado().getCodigoAnormalidade()));
		initialValues.put("comentario", getAnormalidadeImovelSelecionado().getComentario());
		initialValues.put("path_image_1", getAnormalidadeImovelSelecionado().getFoto1());
		initialValues.put("path_image_2", getAnormalidadeImovelSelecionado().getFoto2());
		initialValues.put("latitude", getAnormalidadeImovelSelecionado().getLatitude());
		initialValues.put("longitude", getAnormalidadeImovelSelecionado().getLongitude());
		initialValues.put("data", getAnormalidadeImovelSelecionado().getData());
		initialValues.put("login_usuario", getAnormalidadeImovelSelecionado().getLoginUsuario());

		AnormalidadeImovel anormalidadeImovel = selectAnormalidadeImovel(String.valueOf(getImovelSelecionado().getMatricula()));

		if (anormalidadeImovel.getMatricula() == getImovelSelecionado().getMatricula()) {
			db.update(Constantes.TABLE_ANORMALIDADE_IMOVEL, initialValues, "matricula=?", new String[] { String.valueOf(anormalidadeImovel.getMatricula()) });

		} else {
			if (Controlador.getInstancia().getAnormalidadeImovelSelecionado().isNovoRegistro()) {
				Controlador.getInstancia().setCadastroSelecionado(db.insert(Constantes.TABLE_ANORMALIDADE_IMOVEL, null, initialValues));
				Controlador.getInstancia().getAnormalidadeImovelSelecionado().setNovoRegistro(false);
			}
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
	
	public void inserirInconsistenciaImovel(int matricula, String inconsistencia) {
		ContentValues valores = new ContentValues();
		valores.put("matricula", matricula);
		valores.put("inconsistencia", inconsistencia);
		
		db.insert(Constantes.TABLE_INCONSISTENCIA_IMOVEL, null, valores);
	}
	
	public String[] pesquisarInconsistencias(int matricula) {
		Cursor cursor = db.query(Constantes.TABLE_INCONSISTENCIA_IMOVEL, new String[] { "inconsistencia" }, "matricula = " + matricula, null, null, null, "id asc");

		String[] inconsistencias = new String[cursor.getCount()];

		int count = 0;
		if (cursor.moveToFirst()) {
			do {
				inconsistencias[count] = cursor.getString(0);
				count++;
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return inconsistencias;
	}
	
	public List<Imovel> pesquisarImoveisFinalizados() {

		Cursor cursor = getCursorTabelaImovel("imovel_enviado = " + Constantes.NAO + " and imovel_status != " + Constantes.IMOVEL_A_SALVAR);

		List<Imovel> imoveis = new ArrayList<Imovel>();

		if (cursor.moveToFirst()) {
			do {
				long id = Long.valueOf(getValorColuna(cursor, "id"));

				Imovel imovel = new Imovel();
				montarImovel(cursor, id, imovel);

				imoveis.add(imovel);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return imoveis;
	}

	private Cursor getCursorTabelaImovel(String condicao) {
		
		Cursor cursor = db.query(Constantes.TABLE_IMOVEL, new String[] { 
				"id", 
				"codigo_cliente", 
				"inscricao", 
				"rota", 
				"face", 
				"codigo_municipio",
				"numero_iptu", 
				"numero_celpa", 
				"numero_pontos_uteis", 
				"numero_ocupantes", 
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
				"data",
				"entrevistado", 
				"tipo_operacao", 
				"area_construida", 
				"classe_social", 
				"numero_animais", 
				"volume_piscina", 
				"volume_cisterna",
				"volume_caixa_dagua", 
				"tipo_uso", 
				"acesso_hidrometro", 
				"numero_criancas", 
				"numero_adultos", 
				"numero_alunos", 
				"numero_caes", 
				"numero_idosos",
				"numero_empregados", 
				"numero_outros", 
				"quantidade_economias_social", 
				"quantidade_economias_outros", 
				"observacao", 
				"percentual_abastecimento" },
				condicao, null, null, null, "inscricao asc");
		
		return cursor;
	}
	
	private void montarImovel(Cursor cursor, long id, Imovel imovel) {
		imovel.setImovelId(id);
		imovel.setCodigoCliente(getValorColuna(cursor, "codigo_cliente"));
		imovel.setInscricao(getValorColuna(cursor, "inscricao"));
		imovel.setRota(getValorColuna(cursor, "rota"));
		imovel.setFace(getValorColuna(cursor, "face"));
		imovel.setCodigoMunicipio(getValorColuna(cursor, "codigo_municipio"));
		imovel.setIptu(getValorColuna(cursor, "numero_iptu"));
		imovel.setNumeroCelpa(getValorColuna(cursor, "numero_celpa"));
		imovel.setNumeroPontosUteis(getValorColuna(cursor, "numero_pontos_uteis"));
		imovel.setNumeroOcupantes(getValorColuna(cursor, "numero_ocupantes"));

		imovel.getEnderecoImovel().setTipoLogradouro(getValorColuna(cursor, "tipo_logradouro_imovel"));
		imovel.getEnderecoImovel().setLogradouro(getValorColuna(cursor, "logradouro_imovel"));
		imovel.getEnderecoImovel().setNumero(getValorColuna(cursor, "numero_imovel"));
		imovel.getEnderecoImovel().setComplemento(getValorColuna(cursor, "complemento_imovel"));
		imovel.getEnderecoImovel().setBairro(getValorColuna(cursor, "bairro_imovel"));
		imovel.getEnderecoImovel().setCep(getValorColuna(cursor, "cep_imovel"));
		imovel.getEnderecoImovel().setMunicipio(getValorColuna(cursor, "municipio_imovel"));
		imovel.setCodigoLogradouro(getValorColuna(cursor, "codigo_logradouro_imovel"));

		imovel.getCategoriaResidencial().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_residencial_1"));
		imovel.getCategoriaResidencial().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_residencial_2"));
		imovel.getCategoriaResidencial().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_residencial_3"));
		imovel.getCategoriaResidencial().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_residencial_4"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_comercial_1"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_comercial_2"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_comercial_3"));
		imovel.getCategoriaComercial().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_comercial_4"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_publica_1"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_publica_2"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_publica_3"));
		imovel.getCategoriaPublica().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_publica_4"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria1(getValorColuna(cursor, "sub_categoria_industrial_1"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria2(getValorColuna(cursor, "sub_categoria_industrial_2"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria3(getValorColuna(cursor, "sub_categoria_industrial_3"));
		imovel.getCategoriaIndustrial().setEconomiasSubCategoria4(getValorColuna(cursor, "sub_categoria_industrial_4"));

		imovel.setTipoFonteAbastecimento(getValorColuna(cursor, "tipo_fonte_abastecimento"));
		imovel.setMatricula(getValorColuna(cursor, "matricula"));
		imovel.setImovelStatus(getValorColuna(cursor, "imovel_status"));
		imovel.setImovelEnviado(getValorColuna(cursor, "imovel_enviado"));
		imovel.setLatitude(getValorColuna(cursor, "latitude"));
		imovel.setLongitude(getValorColuna(cursor, "longitude"));
		imovel.setData(getValorColuna(cursor, "data"));
		imovel.setEntrevistado(getValorColuna(cursor, "entrevistado"));
		imovel.setOperacoTipo(Integer.valueOf(getValorColuna(cursor, "tipo_operacao")));

		imovel.setListaRamoAtividade(selectRamoAtividadeImovel(id));

		imovel.setAreaConstruida(getValorColuna(cursor, "area_construida"));
		imovel.setClasseSocial(getValorColuna(cursor, "classe_social"));
		imovel.setNumeroAnimais(getValorColuna(cursor, "numero_animais"));
		imovel.setVolumePiscina(getValorColuna(cursor, "volume_piscina"));
		imovel.setVolumeCisterna(getValorColuna(cursor, "volume_cisterna"));
		imovel.setVolumeCaixaDagua(getValorColuna(cursor, "volume_caixa_dagua"));
		imovel.setTipoUso(getValorColuna(cursor, "tipo_uso"));
		imovel.setAcessoHidrometro(getValorColuna(cursor, "acesso_hidrometro"));

		imovel.getOcupacaoImovel().setCriancas(getValorColuna(cursor, "numero_criancas"));
		imovel.getOcupacaoImovel().setAdultos(getValorColuna(cursor, "numero_adultos"));
		imovel.getOcupacaoImovel().setAlunos(getValorColuna(cursor, "numero_alunos"));
		imovel.getOcupacaoImovel().setCaes(getValorColuna(cursor, "numero_caes"));
		imovel.getOcupacaoImovel().setIdosos(getValorColuna(cursor, "numero_idosos"));
		imovel.getOcupacaoImovel().setEmpregados(getValorColuna(cursor, "numero_empregados"));
		imovel.getOcupacaoImovel().setOutros(getValorColuna(cursor, "numero_outros"));

		imovel.setQuantidadeEconomiasSocial(getValorColuna(cursor, "quantidade_economias_social"));
		imovel.setQuantidadeEconomiasOutros(getValorColuna(cursor, "quantidade_economias_outros"));
		imovel.setObservacao(getValorColuna(cursor, "observacao"));
		imovel.setPercentualAbastecimento(getValorColuna(cursor, "percentual_abastecimento"));
	}
	
	private String getValorColuna(Cursor cursor, String coluna) {
		return cursor.getString(cursor.getColumnIndexOrThrow(coluna));		
	}
}