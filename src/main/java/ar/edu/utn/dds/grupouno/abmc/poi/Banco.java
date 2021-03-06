package ar.edu.utn.dds.grupouno.abmc.poi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import ar.edu.utn.dds.grupouno.helpers.LevDist;

@Entity
@Table(name = "BANCO")
@PrimaryKeyJoinColumn(name = "id")
public class Banco extends POI {

	String sucursal;
	String gerente;

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getGerente() {
		return gerente;
	}

	public void setGerente(String gerente) {
		this.gerente = gerente;
	}

	public void agregarServicio(String nombre, ArrayList<Integer> dias, int horaInicio, int horaFin) {
		if(this.servicios == null){
			servicios = new ArrayList<NodoServicio>();
			NodoServicio nuevoNodo = new NodoServicio();
			nuevoNodo.setName(nombre);
			nuevoNodo.listaDias = dias;
			nuevoNodo.horaInicio = horaInicio;
			nuevoNodo.horaFin = horaFin;
			servicios.add(nuevoNodo);
		}else{
			NodoServicio nuevoNodo = new NodoServicio();
			nuevoNodo.setName(nombre);
			nuevoNodo.listaDias = dias;
			nuevoNodo.horaInicio = horaInicio;
			nuevoNodo.horaFin = horaFin;
			servicios.add(nuevoNodo);
		}

	}

	public boolean disponible(String servicio) {
		/*
		 * el cajero automatico siempre esta disponible y si no pongo nada pide
		 * que devuelva si el banco tiene algun servicio disponible. Como el
		 * cajero siempre está disponible entonces es true.
		 */
		if (servicio.equals("cajero") || servicio.equals("")) {
			return true;
		} else {
			// obtengo el nro del dia de la semana
			int dia = obtenerDia();
			if ((Calendar.MONDAY <= dia) && (dia <= Calendar.FRIDAY)) {
				dia = 0;
				// si es de lunes a viernes, comparo la hora
				return compararHora();
			} else {
				// sino, falso.
				return false;
			}
		}
	}

	public int obtenerDia() {
		Calendar calendario = Calendar.getInstance();
		// obtengo el nro de dia de la semana
		// ej: lunes = 1, martes = 2, etc
		int dia = calendario.get(Calendar.DAY_OF_WEEK);
		return dia;
	}

	public boolean compararHora() {
		int horaInicio = 10;
		int horaFin = 15;
		Calendar calendario = Calendar.getInstance();
		// si la hora actual esta entre 10 y 15 es true
		// leer HOUR_OF_DAY antes de tocar los operadores de comparacion (?
		if (horaInicio <= calendario.get(Calendar.HOUR_OF_DAY) && calendario.get(Calendar.HOUR_OF_DAY) < horaFin) {
			return true;
		} else {
			return false; // sino es false
		}
	}

	public Banco() {

	}

	public Banco(String nombre, double latitud, double longitud) {
		this.ubicacion = GeoLocation.fromDegrees(latitud, longitud);
		this.setNombre(nombre);
		this.setTipo(TiposPOI.BANCO);
	}

	public void setServicios(ArrayList<NodoServicio> serv) {
		servicios = serv;
	}

	@Override
	public boolean busquedaEstandar(String filtros[]) {

		if (super.busquedaEstandar(filtros)) {
			return true;
		}

		for (String filtro : filtros) {
			if (this.sucursal != null && compararAtributo(filtro, this.sucursal)) {
				return true;
			} else if (this.gerente != null && compararAtributo(filtro, this.gerente)) {
				return true;
			} else {
				if(this.buscarServicios(filtro))
					return true;
			}
		}

		return false;

	}

	@Override
	public boolean compararPOI(POI poi) {
		if (!super.compararPOI(poi)) {
			return false;
		}
		Banco other = (Banco) poi;
		if (gerente == null) {
			if (other.gerente != null)
				return false;
		} else if (!gerente.equals(other.gerente))
			return false;
		if (sucursal == null) {
			if (other.sucursal != null)
				return false;
		} else if (!sucursal.equals(other.sucursal))
			return false;
		return true;
	}
	
	public List<NodoServicio> getServicios() {
		return servicios;
	}

	public void setServicio(NodoServicio servicio) {
		servicios.add(servicio);
	}

}
