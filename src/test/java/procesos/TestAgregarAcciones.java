package procesos;

import java.util.ArrayList;

import javax.mail.MessagingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import autentification.AuthAPI;
import autentification.Rol;
import autentification.Usuario;
import autentification.UsuariosFactory;
import db.AgregarAccionesTransaction;
import db.DB_Usuarios;

public class TestAgregarAcciones {
	DB_Usuarios db_usuario;
	AuthAPI unAuthAPI;
	boolean encontrada;
	boolean encontradaTodas;
	boolean agregado;
	Usuario unUsuarioAdmin;
	Usuario unUsuarioTerminal;
	Usuario unUsuarioAdmin2;
	Usuario unUsuarioTerminal2;
	UsuariosFactory fact = new UsuariosFactory();

	private ArrayList<String> listadoAccionesQueEstanEnAdmin;
	private ArrayList<String> listadoAccionesQueEstanEnTerminal;

	AgregarAccionesTransaction transaction;

	@Before
	public void init() {
		listadoAccionesQueEstanEnAdmin = new ArrayList<String>();
		listadoAccionesQueEstanEnTerminal = new ArrayList<String>();
		db_usuario = db.DB_Usuarios.getInstance();
		unAuthAPI = AuthAPI.getInstance();
		transaction = new AgregarAccionesTransaction(0);

		// PONER LAS SIGUIENTES LISTAS DE ACCIONES

		listadoAccionesQueEstanEnTerminal.add("busquedaPOI");
		listadoAccionesQueEstanEnTerminal.add("obtenerInfoPOI");

		listadoAccionesQueEstanEnAdmin.add("busquedaPOI");
		listadoAccionesQueEstanEnAdmin.add("obtenerInfoPOI");
		listadoAccionesQueEstanEnAdmin.add("reporteBusquedaPorUsuario");
		listadoAccionesQueEstanEnAdmin.add("reporteBusquedasPorFecha");
		listadoAccionesQueEstanEnAdmin.add("reportecantidadResultadosPorTerminal");
		listadoAccionesQueEstanEnAdmin.add("cambiarEstadoMail");
		listadoAccionesQueEstanEnAdmin.add("actualizacionLocalesComerciales");
		listadoAccionesQueEstanEnAdmin.add("agregarAcciones");
		listadoAccionesQueEstanEnAdmin.add("bajaPOIs");
		listadoAccionesQueEstanEnAdmin.add("procesoMultiple");

		fact.crearUsuario("admin", "123", Rol.ADMIN);
		fact.crearUsuario("terminal", "123", Rol.TERMINAL);
		fact.crearUsuario("adminPrueba", "123", Rol.ADMIN);
		fact.crearUsuario("terminal1", "123", Rol.TERMINAL);
	}

	@Test
	public void agregarAccionesAUsuarioAdminlistadoAccionesQueEstanEnAdmin() throws MessagingException {

		agregado = AgregarAcciones.AgregarAccionesAUsuario("admin", listadoAccionesQueEstanEnAdmin, transaction);
		// comprobacion si tiene toda la lista a agregar
		encontradaTodas = true;
		for (String unafuncionabilidad : listadoAccionesQueEstanEnAdmin) {
			encontrada = db_usuario.getUsuarioByName("admin").getFuncionalidad(unafuncionabilidad)!=null;
			if (encontrada == false) {
				encontradaTodas = false;
			}
		}
		Assert.assertTrue(encontradaTodas);

	}

	@Test
	public void agregarAccionesAUsuarioTerminallistadoAccionesQueEstanEnAdmin() throws MessagingException {

		AgregarAcciones.AgregarAccionesAUsuario("terminal", listadoAccionesQueEstanEnAdmin, transaction);

		// comprobacion si tiene toda la lista a agregar
		encontradaTodas = true;
		for (String unafuncionabilidad : listadoAccionesQueEstanEnAdmin) {
			encontrada = db_usuario.getUsuarioByName("terminal").getFuncionalidad(unafuncionabilidad)!=null;
			if (encontrada == false) {
				encontradaTodas = false;
			}

		}
		Assert.assertFalse(encontradaTodas);

	}

	@Test
	public void agregarAccionesAUsuarioTerminallistadoAccionesQueEstanEnTerminal() throws MessagingException {

		agregado = AgregarAcciones.AgregarAccionesAUsuario("terminal", listadoAccionesQueEstanEnTerminal, transaction);
		// comprobacion si tiene toda la lista a agregar
		encontradaTodas = true;
		for (String unafuncionabilidad : listadoAccionesQueEstanEnTerminal) {
			encontrada = db_usuario.getUsuarioByName("terminal").getFuncionalidad(unafuncionabilidad)!=null;
			if (encontrada == false) {
				encontradaTodas = false;
			}

		}
		Assert.assertTrue(encontradaTodas);

	}

	@Test
	public void agregarAccionesAUsuarioAdminlistadoAccionesQueEstanEnTerminal() throws MessagingException {

		agregado = AgregarAcciones.AgregarAccionesAUsuario("admin", listadoAccionesQueEstanEnTerminal, transaction);
		// comprobacion si tiene toda la lista a agregar
		encontradaTodas = true;
		for (String unafuncionabilidad : listadoAccionesQueEstanEnTerminal) {
			encontrada = db_usuario.getUsuarioByName("admin").getFuncionalidad(unafuncionabilidad)!=null;
			if (encontrada == false) {
				encontradaTodas = false;
			}

		}
		Assert.assertTrue(encontradaTodas);

	}	
}
