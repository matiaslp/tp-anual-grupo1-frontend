package ar.edu.utn.dds.grupouno.abmc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.mail.MessagingException;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.dds.grupouno.abmc.POI_ABMC;
import ar.edu.utn.dds.grupouno.db.DB_POI;
import ar.edu.utn.dds.grupouno.db.poi.Banco;
import ar.edu.utn.dds.grupouno.db.poi.CGP;
import ar.edu.utn.dds.grupouno.db.poi.LocalComercial;
import ar.edu.utn.dds.grupouno.db.poi.POI;
import ar.edu.utn.dds.grupouno.db.poi.ParadaColectivo;

public class TestABMC_Consulta {
	POI_ABMC abmc;
	String ServicioAPI;
	DB_POI instance;
	Banco banco;
	LocalComercial local;
	ParadaColectivo parada ;
	CGP cgp;

	@Before
	public void inicializar() {
		abmc = new POI_ABMC();
		instance = DB_POI.getInstance();
		DB_POI.getListado().clear();
		banco = new Banco("Santander", 0, 0);
		banco.setBarrio("Mataderos");
		banco.setPais("Argentina");
		banco.setCallePrincipal("Alberdi");
		banco.setCalleLateral("Escalada");
		ServicioAPI = "http://trimatek.org/Consultas/";
		local = new LocalComercial("Localcito", 0, 0, null);
		parada = new ParadaColectivo("47", 0, 0);
		cgp = new CGP("Mataderos", 0, 0);
		instance.agregarPOI(cgp);
		instance.agregarPOI(parada);
		instance.agregarPOI(local);
		instance.agregarPOI(banco);

	}

	@Test
	public void testConsultaVacia() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "", 1);
		Assert.assertTrue(lista.isEmpty());
	}

	@Test
	public void testConsultaLocal() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar("", "Alberdi", 1);
		Assert.assertTrue(lista.size() == 1);
	}

	@Test
	public void testConsultaLocal2() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar("", "Mataderos", 1);
		Assert.assertTrue(lista.size() == 2);
	}

	@Test
	public void testConsultaRemota() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Mataderos", 1);
		Assert.assertTrue(lista.size() == 17); //2 POI locales Mataderos, 16 externos pero 1 repetido.
	}

	// deberia devolver 1 solo resultado, pero como el servicio remoto
	// ServiciosAPI no filtra bien,
	// devuelve todos los CGPs y el banco encontrado (en total 16)
	@Test
	public void testConsultaRemota2() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia", 1);
		Assert.assertTrue(lista.size() == 16);
	}

	@Test
	public void testConsultaRemotaVariasPalabras()
			throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia Mataderos", 1);
		Assert.assertTrue(lista.size() == 18);//2 POI locales, 15 POI ext de CGP porque funciona mal y 1 Banco galicia externo
	}

	@Test
	public void testConsulta() throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia", 1);
		Assert.assertTrue(!(lista.isEmpty()));
	}

	@Test
	public void testConsultavariasPalabras()
			throws JSONException, MalformedURLException, IOException, MessagingException {
		ArrayList<POI> lista = null;
		lista = abmc.buscar(ServicioAPI, "Galicia Mataderos", 1);
		Assert.assertTrue(lista.size() == 18);
	}
}
