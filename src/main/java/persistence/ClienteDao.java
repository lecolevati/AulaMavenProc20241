package persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Cliente;

public class ClienteDao implements ICrud<Cliente> {

	private GenericDao gDao;

	public ClienteDao(GenericDao gDao) {
		this.gDao = gDao;
	}

	@Override
	public String inserir(Cliente cli) throws SQLException, ClassNotFoundException {
		String saida = insupdt(cli, "I");
		return saida;
	}

	@Override
	public String atualizar(Cliente cli) throws SQLException, ClassNotFoundException {
		String saida = insupdt(cli, "U");
		return saida;
	}

	private String insupdt(Cliente cli, String op) throws ClassNotFoundException, SQLException {
		Connection c = gDao.getConnection();
		String sql = "{CALL sp_cliente(?,?,?,?,?)}";
		CallableStatement cs = c.prepareCall(sql);
		
		cs.setString(1, op);
		cs.setInt(2, cli.getId());
		cs.setString(3, cli.getNome());
		cs.setString(4, cli.getNascimento().toString());
		cs.registerOutParameter(5, Types.VARCHAR);
		cs.execute();
		
		String saida = cs.getString(5);
		
		cs.close();
		c.close();
		return saida;
	}

	@Override
	public String excluir(Cliente cli) throws SQLException, ClassNotFoundException {
		Connection c = gDao.getConnection();
		String sql = "{CALL sp_cliente(?,?,?,?,?)}";
		CallableStatement cs = c.prepareCall(sql);
		
		cs.setString(1, "D");
		cs.setInt(2, cli.getId());
		cs.setNull(3, Types.VARCHAR);
		cs.setNull(4, Types.DATE);
		cs.registerOutParameter(5, Types.VARCHAR);
		cs.execute();
		
		String saida = cs.getString(5);
		
		cs.close();
		c.close();
		
		return saida;
	
	}

	@Override
	public Cliente buscar(Cliente cli) throws SQLException, ClassNotFoundException {
		Connection c = gDao.getConnection();
		String sql = "SELECT id, nome, dt_nasc FROM cliente WHERE id = ?";
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, cli.getId());
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			cli.setId(rs.getInt("id"));
			cli.setNome(rs.getString("nome"));
			cli.setNascimento(LocalDate.parse(rs.getString("dt_nasc")));
		}
		rs.close();
		ps.close();
		c.close();
		
		return cli;
	}

	@Override
	public List<Cliente> listar() throws SQLException, ClassNotFoundException {
		List<Cliente> clientes = new ArrayList<>(); 
		Connection c = gDao.getConnection();
		String sql = "SELECT id, nome, CONVERT(CHAR(10), dt_nasc, 103) AS nasc FROM cliente";
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Cliente cli = new Cliente();
			cli.setId(rs.getInt("id"));
			cli.setNome(rs.getString("nome"));
			cli.setNasc(rs.getString("nasc"));
			clientes.add(cli);
		}
		rs.close();
		ps.close();
		c.close();
		
		return clientes;
	}

}
