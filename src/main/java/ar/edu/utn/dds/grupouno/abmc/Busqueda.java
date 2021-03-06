package ar.edu.utn.dds.grupouno.abmc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.mail.MessagingException;

import org.json.JSONException;

import ar.edu.utn.dds.grupouno.abmc.poi.POI;



public interface Busqueda {

	public ArrayList<POI> buscar(String url, String texto, long userID)
			throws JSONException, MalformedURLException, IOException, MessagingException;

}
