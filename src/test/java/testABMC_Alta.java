

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ABMC.POI_ABMC;
import ABMC.POI_DTO;
import DB.DB_Server;
import POI.Rubro;
import POI.TiposPOI;


public class testABMC_Alta {
	POI_ABMC abmc = new POI_ABMC();
	POI_ABMC poi_abmc;
	POI_DTO poiDTOBanco; 
	POI_DTO poiDTOCGP;
	POI_DTO poiDTOComercial;
	POI_DTO poiDTOColectivo;
	Rubro rubro;
	DB_Server unServer;
	DB_Server instancia;
	
	@Before
	public void init(){
		poi_abmc =new POI_ABMC();
		unServer = new DB_Server();
		instancia = unServer.getInstance();
		
		poiDTOBanco = new POI_DTO();		
		poiDTOBanco.setTipo(TiposPOI.BANCO);
		poiDTOBanco.setNombre("unBancoJorge!");
		poiDTOBanco.setLatitud(-34.5664823);
		poiDTOBanco.setLongitud(-34.5664823);
		
		poiDTOCGP = new POI_DTO();
		poiDTOCGP.setTipo( TiposPOI.CGP);
		poiDTOCGP.setNombre("unCGP");
		poiDTOCGP.setLatitud(-34.5664823);
		poiDTOCGP.setLongitud(-34.5664823);
		poiDTOCGP.setRubro(rubro= new Rubro("unRubro"));
		
		poiDTOComercial = new POI_DTO();
		poiDTOComercial.setTipo( TiposPOI.LOCAL_COMERCIAL);
		poiDTOComercial.setNombre("unLocalComercial");
		poiDTOComercial.setLatitud(-34.5664823);
		poiDTOComercial.setLongitud(-34.5664823);
		
		poiDTOColectivo = new POI_DTO();
		poiDTOColectivo.setTipo( TiposPOI.PARADA_COLECTIVO);
		poiDTOColectivo.setNombre("unaParadaDeColectivo");
		poiDTOColectivo.setLatitud(-34.5664823);
		poiDTOColectivo.setLongitud(-34.5664823);
	}
	
	@Test
	public void altaBanco(){
		//Agregar conversion de DTO_POI a POI
		boolean respuesta = instancia.agregarPOI(poiDTOBanco.converttoPOI());
		Assert.assertTrue(respuesta);
		Assert.assertTrue(poiDTOBanco.getNombre() == instancia.getListado().get(instancia.getListado().size()-1).getNombre());
		
	}
	
	@Test
	public void altaCGP(){
		boolean respuesta=poi_abmc.alta(poiDTOCGP);
		Assert.assertTrue(respuesta);
	}
	
	@Test
	public void altaLocalComercial(){
		boolean respuesta=poi_abmc.alta(poiDTOComercial);
		Assert.assertTrue(respuesta);
	}
	
	@Test
	public void altaParadaColectivo(){
		boolean respuesta=poi_abmc.alta(poiDTOColectivo);
		Assert.assertTrue(respuesta);
	}
}
