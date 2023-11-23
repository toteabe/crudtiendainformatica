package org.iesvdm.crudtiendainformatica.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.*;

import org.iesvdm.crudtiendainformatica.dao.FabricanteDAO;
import org.iesvdm.crudtiendainformatica.dao.FabricanteDAOImpl;
import org.iesvdm.crudtiendainformatica.model.Fabricante;


//DECLARACIÓN DEL SERVLET Y DE LOS PATHS QUE SERÁN TRATADAS POR ÉL
//		nombre del servlet--v         path/s -----------v => con el * se indica cualquier subruta

//@WebServlet(name = "fabricanteServlet", value = "fabricantes*")
@WebServlet(name = "fabricanteServlet", value = {"/fabricantes/*","/fabs/*"})
public class FabricantesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String contextPath;

	@Override
	public void init() {
		this.contextPath = getServletContext().getContextPath();
		System.out.println("Context Path =" + this.contextPath);
	}

	/**
	 * HTTP Method: GET
	 * Paths: 
	 * 		/fabricantes
	 * 		/fabricantes/{id}
	 * 		/fabricantes/edit/{id}
	 * 		/fabricantes/create
	 */		
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		RequestDispatcher dispatcher;
		String contextPath = request.getContextPath();
		String pathInfo = request.getPathInfo(); //
			
		if (pathInfo == null || "/".equals(pathInfo)) {
			FabricanteDAO fabDAO = new FabricanteDAOImpl();
			
			//GET 
			//	/fabricantes
			//	/fabricantes/
			
			request.setAttribute("listaFabricantes", fabDAO.getAll());		
			dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/fabricantes.jsp");
			        		       
		} else {
			// GET
			// 		/fabricantes/{id}
			// 		/fabricantes/{id}/
			// 		/fabricantes/edit/{id}
			// 		/fabricantes/edit/{id}/
			// 		/fabricantes/create
			// 		/fabricantes/create/
			
			pathInfo = pathInfo.replaceAll("/$", "");
			String[] pathParts = pathInfo.split("/");
			
			if (pathParts.length == 2 && "crear".equals(pathParts[1])) {
				
				// GET
				// fabricantescreate
				dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/crear-fabricante.jsp");
        												
			
			} else if (pathParts.length == 2) {
				FabricanteDAO fabDAO = new FabricanteDAOImpl();
				// GET
				// fabricantes{id}
				try {
					request.setAttribute("fabricante",fabDAO.find(Integer.parseInt(pathParts[1])));
					dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/detalle-fabricante.jsp");
					        								
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/fabricantes.jsp");
				}
				
			} else if (pathParts.length == 3 && "editar".equals(pathParts[1]) ) {
				FabricanteDAO fabDAO = new FabricanteDAOImpl();
				
				// GET
				// /fabricantes/edit/{id}
				try {
					request.setAttribute("fabricante",fabDAO.find(Integer.parseInt(pathParts[2])));
					dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/editar-fabricante.jsp");
					        								
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/fabricantes.jsp");
				}
				
				
			} else {
				
				System.out.println("Opción POST no soportada.");
				dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/fabricantes.jsp");
			
			}
			
		}
		
		dispatcher.forward(request, response);
			 
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		RequestDispatcher dispatcher;
		String __method__ = request.getParameter("__method__");
		
		if (__method__ == null) {
			// Crear uno nuevo
			FabricanteDAO fabDAO = new FabricanteDAOImpl();
			
			String nombre = request.getParameter("nombre");
			Fabricante nuevoFab = new Fabricante();
			nuevoFab.setNombre(nombre);
			fabDAO.create(nuevoFab);			
			
		} else if (__method__ != null && "put".equalsIgnoreCase(__method__)) {			
			// Actualizar uno existente
			//Dado que los forms de html sólo soportan method GET y POST utilizo parámetro oculto para indicar la operación de actulización PUT.
			doPut(request, response);
			
		
		} else if (__method__ != null && "delete".equalsIgnoreCase(__method__)) {			
			// Actualizar uno existente
			//Dado que los forms de html sólo soportan method GET y POST utilizo parámetro oculto para indicar la operación de actulización DELETE.
			doDelete(request, response);

		} else {
			
			System.out.println("Opción POST no soportada.");
			
		}
		
		response.sendRedirect(this.contextPath + "/fabricantes");
		//response.sendRedirect("/fabricantes");
		
		
	}
	
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		FabricanteDAO fabDAO = new FabricanteDAOImpl();
		String codigo = request.getParameter("codigo");
		String nombre = request.getParameter("nombre");
		Fabricante fab = new Fabricante();
		
		try {
			
			int id = Integer.parseInt(codigo);
			fab.setCodigo(id);
			fab.setNombre(nombre);
			fabDAO.update(fab);
			
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		RequestDispatcher dispatcher;
		FabricanteDAO fabDAO = new FabricanteDAOImpl();
		String codigo = request.getParameter("codigo");
		
		try {
			
			int id = Integer.parseInt(codigo);
		
		fabDAO.delete(id);
			
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		
	}
	
}
