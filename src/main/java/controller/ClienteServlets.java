package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cliente;
import persistence.ClienteDao;
import persistence.GenericDao;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cliente")
public class ClienteServlets extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ClienteServlets() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("id");
		String cmd = request.getParameter("acao");

		Cliente cliente = new Cliente();
		String erro = "";
		String saida = "";

		GenericDao gDao = new GenericDao();
		ClienteDao cDao = new ClienteDao(gDao);

		try {
			if (id != null & cmd != null) {
				cliente.setId(Integer.parseInt(id));
				if (cmd.equalsIgnoreCase("EDITAR")) {
					cliente = cDao.buscar(cliente);
				}
				if (cmd.equalsIgnoreCase("EXCLUIR")) {
					saida = cDao.excluir(cliente);
					cliente = new Cliente();
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			erro = e.getMessage();
		} finally {
			request.setAttribute("erro", erro);
			request.setAttribute("saida", saida);
			request.setAttribute("cliente", cliente);

			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("id");
		String nome = request.getParameter("nome");
		String nascimento = request.getParameter("nascimento");
		String cmd = request.getParameter("botao");

		Cliente cliente = new Cliente();
		if (!cmd.equalsIgnoreCase("LISTAR")) {
			cliente.setId(Integer.parseInt(id));
		}
		if (cmd.equalsIgnoreCase("INSERT") || cmd.equalsIgnoreCase("UPDATE")) {
			cliente.setNome(nome);
			cliente.setNascimento(LocalDate.parse(nascimento));
		}

		List<Cliente> clientes = new ArrayList<>();
		String erro = "";
		String saida = "";

		GenericDao gDao = new GenericDao();
		ClienteDao cDao = new ClienteDao(gDao);

		try {
			if (cmd.equalsIgnoreCase("INSERT")) {
				saida = cDao.inserir(cliente);
				cliente = new Cliente();
			}
			if (cmd.equalsIgnoreCase("UPDATE")) {
				saida = cDao.atualizar(cliente);
				cliente = new Cliente();
			}
			if (cmd.equalsIgnoreCase("DELETE")) {
				saida = cDao.excluir(cliente);
				cliente = new Cliente();
			}
			if (cmd.equalsIgnoreCase("BUSCAR")) {
				cliente = cDao.buscar(cliente);
			}
			if (cmd.equalsIgnoreCase("LISTAR")) {
				clientes = cDao.listar();
				cliente = new Cliente();
			}
		} catch (SQLException | ClassNotFoundException e) {
			erro = e.getMessage();
		} finally { // RETORNO
			request.setAttribute("erro", erro);
			request.setAttribute("saida", saida);
			request.setAttribute("cliente", cliente);
			request.setAttribute("clientes", clientes);

			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}
	}

}
