package ar.edu.utn.dds.grupouno.acciones;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ar.edu.utn.dds.grupouno.autentification.Accion;

@ManagedBean(name="AccionesDeConsultaBean")
@ViewScoped
public class AccionesDeConsultaBean {
	private List<Accion> accionesParaSeleccionar = new ArrayList<Accion>();
	private List<Accion> accionesSeleccionadas = new ArrayList<Accion>();
	private Accion accion = null;
	
	public List<Accion> getAccionesParaSeleccionar() {
		return accionesParaSeleccionar;
	}
	public void setAccionesParaSeleccionar(List<Accion> accionesParaSeleccionar) {
		this.accionesParaSeleccionar = accionesParaSeleccionar;
	}
	public List<Accion> getAccionesSeleccionadas() {
		return accionesSeleccionadas;
	}
	public void setAccionesSeleccionadas(List<Accion> accionesSeleccionadas) {
		this.accionesSeleccionadas = accionesSeleccionadas;
	}
	
	public Accion getAccion() {
		return accion;
	}
	public void setAccion(Accion accion) {
		this.accion = accion;
	}
	
	public void confirmar(){
		// agrega las acciones al usuario.
	}
	
	public void eliminar(Accion accion){
		this.accionesSeleccionadas.remove(accion);
	}
	
	public void cancelar(){
		
	}
	
	public void agregar(Accion accion){
		this.accionesSeleccionadas.add(accion);
	}
}
