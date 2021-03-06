package ar.edu.utn.dds.grupouno.abmc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.mail.MessagingException;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.dds.grupouno.abmc.poi.Banco;
import ar.edu.utn.dds.grupouno.abmc.poi.CGP;
import ar.edu.utn.dds.grupouno.abmc.poi.LocalComercial;
import ar.edu.utn.dds.grupouno.abmc.poi.POI;
import ar.edu.utn.dds.grupouno.abmc.poi.ParadaColectivo;
import ar.edu.utn.dds.grupouno.autentification.Usuario;
import ar.edu.utn.dds.grupouno.autentification.UsuariosFactory;
import ar.edu.utn.dds.grupouno.helpers.LeerProperties;
import ar.edu.utn.dds.grupouno.repositorio.DB_POI;
import ar.edu.utn.dds.grupouno.repositorio.RepoMongo;
import ar.edu.utn.dds.grupouno.repositorio.Repositorio;

public class TestABMC_Consulta {
	POI_ABMC abmc;
	String ServicioAPI;
	DB_POI instance;
	Banco banco;
	LocalComercial local;
	ParadaColectivo parada;
	CGP cgp;
	Usuario usuario;
	UsuariosFactory ufactory = new UsuariosFactory();

	@Before
	public void inicializar() {
		abmc = new POI_ABMC();
		instance = Repositorio.getInstance().pois();
		// DB_POI.getListado().clear();
		banco = new Banco("Santander", 0, 0);
		banco.setBarrio("Mataderos");
		banco.setPais("Argentina");
		banco.setCallePrincipal("Alberdi");
		banco.setCalleLateral("Escalada");
		ServicioAPI = LeerProperties.getInstance().prop.getProperty("Servicio_Externo");
		local = new LocalComercial("Localcito", 0, 0, null);
		parada = new ParadaColectivo("47", 0, 0);
		cgp = new CGP("Mataderos", 0, 0);
		instance.agregarPOI(cgp);
		instance.agregarPOI(parada);
		instance.agregarPOI(local);
		instance.agregarPOI(banco);

		usuario = ufactory.crearUsuario("admin", "password", "ADMIN");

		usuario.setAuditoriaActivada(true);
		usuario.setCorreo("uncorreo@correoloco.com");
		usuario.setLog(true);
		usuario.setMailHabilitado(true);
		usuario.setNombre("Shaggy");
		usuario.setNotificacionesActivadas(true);

		Repositorio.getInstance().usuarios().persistirUsuario(usuario);

	}

	//Este test queda anulado ya que no se puede buscar por un string vacio
//	@Test
//	public void testConsultaVacia() throws JSONException, MalformedURLException, IOException, MessagingException {
//		ArrayList<POI> lista = null;
//		lista = abmc.buscar(ServicioAPI, "", usuario.getId());
//		Assert.assertTrue(lista.isEmpty());
//	}

	@Test
	public void testConsultaLocal() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar("", "Alberdi", usuario.getId());
		Assert.assertTrue(lista.size() == 1);
	}

	@Test
	public void testConsultaLocal2() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar("", "Mataderos", usuario.getId());
		Assert.assertTrue(lista.size() == 2);
	}

	@Test
	public void testConsultaRemota() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Mataderos", usuario.getId());
		Assert.assertTrue(lista.size() == 17); // 2 POI locales Mataderos, 16
												// externos pero 1 repetido.
	}

	// deberia devolver 1 solo resultado, pero como el servicio remoto
	// ServiciosAPI no filtra bien,
	// devuelve todos los CGPs y el banco encontrado (en total 16)
	// CON MONGO FUNCIONA BIEN CUANDO ESTA EN CACHE.
	@Test
	public void testConsultaRemota2() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia", usuario.getId());
		Assert.assertTrue(lista.size() == 1);
	}

	@Test
	public void testConsultaRemotaVariasPalabras()
			throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia Mataderos", usuario.getId());
		Assert.assertTrue(lista.size() == 3);// 2 POI locales, 15 POI ext de
												// CGP porque funciona mal y 1
												// Banco galicia externo
	}

	@Test
	public void testConsulta() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia", usuario.getId());
		Assert.assertTrue(!(lista.isEmpty()));
	}

	//ESTE SIN CACHE DA 18 PORQUE EL SERVICIO ANDA MAL
	@Test
	public void testConsultavariasPalabras()
			throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia Mataderos", usuario.getId());
		Assert.assertTrue(lista.size() == 3);
	}

	@After
	public void outtro() {

		instance.remove(usuario);
		RepoMongo.getInstance().getDatastore().delete(RepoMongo.getInstance()
				.getDatastore().createQuery(RegistroHistoricoMorphia.class));
//		ArrayList<RegistroHistorico> list = Repositorio.getInstance().resultadosRegistrosHistoricos().getListado();
//		for (RegistroHistorico reg : list)
//			instance.remove(reg);
		instance.remove(cgp);
		instance.remove(parada);
		instance.remove(local);
		instance.remove(banco);
	}
}
