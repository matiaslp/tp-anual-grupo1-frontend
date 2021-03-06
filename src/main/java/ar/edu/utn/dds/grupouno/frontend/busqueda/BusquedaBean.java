package ar.edu.utn.dds.grupouno.frontend.busqueda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;

import org.json.JSONException;
import org.primefaces.context.RequestContext;

import ar.edu.utn.dds.grupouno.abmc.POI_ABMC;
import ar.edu.utn.dds.grupouno.abmc.consultaExterna.dtos.NodoServicioDTO;
import ar.edu.utn.dds.grupouno.abmc.consultaExterna.dtos.resultadoBusquedaDTO;
import ar.edu.utn.dds.grupouno.abmc.poi.NodoServicio;
import ar.edu.utn.dds.grupouno.abmc.poi.POI;
import ar.edu.utn.dds.grupouno.abmc.poi.ParadaColectivo;
import ar.edu.utn.dds.grupouno.abmc.poi.TiposPOI;
import ar.edu.utn.dds.grupouno.autentification.Usuario;
import ar.edu.utn.dds.grupouno.frontend.abmPOIs.BajaPOI;
import ar.edu.utn.dds.grupouno.helpers.LeerProperties;
import ar.edu.utn.dds.grupouno.repositorio.Repositorio;

@ManagedBean
@SessionScoped
public class BusquedaBean {
	private String textoLibre;
	private String textoBuscar;

	private List<resultadoBusquedaDTO> pois = new ArrayList<resultadoBusquedaDTO>();
	private resultadoBusquedaDTO selectedPoi;
	String ServicioAPI;
	
	private List<Item> items;

	public BusquedaBean() {
		items = new ArrayList<Item>();
		items.add(new Item());
		ServicioAPI = LeerProperties.getInstance().prop.getProperty("Servicio_Externo");
	}

	public String getTextoLibre() {
		return textoLibre;
	}

	public List<resultadoBusquedaDTO> getPois() {
		return pois;

	}

	public void setPois(List<resultadoBusquedaDTO> pois) {
		this.pois = pois;
	}

	public void setTextoLibre(String textoLibre) {
		this.textoLibre = textoLibre;
	}

	public resultadoBusquedaDTO getSelectedPoi() {
		return selectedPoi;
	}

	public void setSelectedPoi(resultadoBusquedaDTO selectedPoi) {
		this.selectedPoi = selectedPoi;
	}

	@SuppressWarnings("null")
	public void buscar() {
		pois.clear();
		String username = ((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("username"));
		String token = ((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("token"));
		Usuario usuario = Repositorio.getInstance().usuarios().getUsuarioByName(username);
		ArrayList<POI> lstPOI = null;
		ArrayList<POI> lstPOIPalSeparada = null;
		try {
			textoBuscar = new String();
			int fallo = 0;
			for (Item unText : items) {
				if (unText.getValue().length() > 0) {
					if (textoBuscar.length() > 0) {
						textoBuscar = textoBuscar + " " + unText.getValue();
					} else {
						textoBuscar = unText.getValue();
					}
				} else {
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('unCaracter').show();");
					fallo = 1;
					break;
				}
			}
			if (fallo == 0) {

				lstPOI = POI_ABMC.getInstance().buscar(ServicioAPI, textoBuscar, usuario.getId());
			}
		} catch (JSONException | IOException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (lstPOI != null && lstPOI.size() > 0) {
			pois = listToDTO(lstPOI);
		}
	}

	private List<resultadoBusquedaDTO> listToDTO(ArrayList<POI> lstPOI) {
		List<resultadoBusquedaDTO> resultados = new ArrayList<resultadoBusquedaDTO>();
		if (lstPOI.size() > 0) {
			for (POI point : lstPOI) {
				resultados.add(toDTO(point));
			}
		}
		return resultados;
	}

	@SuppressWarnings("null")
	private resultadoBusquedaDTO toDTO(POI point) {
		resultadoBusquedaDTO resultado = new resultadoBusquedaDTO();

		resultado.setId(point.getId());
		resultado.setTipo(point.getTipo());
		resultado.setDireccion(point.getDireccion());
		if (point.getNombre() != null) {
			resultado.setNombre(point.getNombre());
		}

		if (point.getTipo().equals(TiposPOI.PARADA_COLECTIVO)) {
			resultado.setLinea(((ParadaColectivo) point).getLinea());
		}

		List<NodoServicio> servicios = point.getServicios();
		if (servicios != null && servicios.size() > 0) {
			for (int i = 0; i < servicios.size(); i++) {
				resultado.addServicio(toDTO(servicios.get(i)));
			}
		}
		return resultado;
	}

	private NodoServicioDTO toDTO(NodoServicio servicio) {
		NodoServicioDTO dto = new NodoServicioDTO();
		dto.setNombre(servicio.getName());
		dto.setBandaHoraria(formatBandaHoraria(servicio.getHoraInicio(), servicio.getHoraFin()));
		return dto;
	}

	private String formatBandaHoraria(int horaInicio, int horaFin) {
		return String.valueOf(horaInicio) + " - " + String.valueOf(horaFin);
	}

	public void add() {
		items.add(new Item());

	}

	public void remove(Item it) {
		if (items.size() > 1)
			items.remove(it);
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> pois) {
		this.items = pois;
	}

	public void reset() {
		textoLibre = "";
		textoBuscar = "";
		pois.clear();
		selectedPoi = null;
		items = new ArrayList<Item>();
		items.add(new Item());

	}
	
	public void home(){
		reset();
		RequestContext.getCurrentInstance().reset("form:panel");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/tp-anual/faces/welcome.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String borrarPoi(){
		String username = ((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("username"));
		String token = ((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("token"));
		Usuario usuario = Repositorio.getInstance().usuarios().getUsuarioByName(username);
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().put("poiSeleccionado"+token+usuario,Long.toString(this.selectedPoi.getId()));
		reset();
		return "poisBajaB";

	}
	
	public String modificarPoi(){
		String username = ((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("username"));
		String token = ((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("token"));
		Usuario usuario = Repositorio.getInstance().usuarios().getUsuarioByName(username);
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().put("poiSeleccionado"+token+usuario,Long.toString(this.selectedPoi.getId()));
		reset();
		return "poisModificacionB";

	}

	
	
}
