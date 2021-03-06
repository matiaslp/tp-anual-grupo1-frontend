package ar.edu.utn.dds.grupouno.abmc.poi;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import ar.edu.utn.dds.grupouno.abmc.RegistroHistorico;
import ar.edu.utn.dds.grupouno.abmc.consultaExterna.dtos.POI_DTO;
import ar.edu.utn.dds.grupouno.helpers.LevDist;
import ar.edu.utn.dds.grupouno.helpers.MetodosComunes;
import ar.edu.utn.dds.grupouno.repositorio.PersistibleConNombre;

@Entity
@Table(name = "POI")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
		@NamedQuery(name = "getPOIbyNombre", query = "SELECT p FROM POI p WHERE p.nombre LIKE :pnombre AND p.fechaBaja IS NULL"),
		@NamedQuery(name = "getPOIbyNombreConEliminados", query = "SELECT p FROM POI p WHERE p.nombre LIKE :pnombre"),
		@NamedQuery(name = "POI.findAll", query = "SELECT p FROM POI p") })
public class POI extends PersistibleConNombre {

	protected String callePrincipal;
	protected String calleLateral;
	protected long numeracion;
	protected long piso;
	protected String departamento;
	protected String unidad;
	protected long codigoPostal;
	protected String localidad;
	protected String barrio;
	protected String provincia;
	protected String pais;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "georef_id", referencedColumnName = "id")
	protected GeoLocation ubicacion;
	protected long comuna; // define cuando otro punto es cercano.
	protected long cercania = 500;
	protected TiposPOI tipo;
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	// @Fetch(FetchMode.JOIN)
	@JoinTable(name = "POI_SERVICIO", joinColumns = { @JoinColumn(name = "poi_id") }, inverseJoinColumns = {
			@JoinColumn(name = "servicio_id") })
	protected List<NodoServicio> servicios = new ArrayList<NodoServicio>();
	// pueden ser varias y se crean a travez de
	// FlyweightFactoryEtiqueta.listarEtiquetas(String etiquetas[])
	@ManyToMany(cascade = { CascadeType.ALL })
	@OrderColumn
	@JoinTable(name = "POI_ETIQUETA", joinColumns = { @JoinColumn(name = "poi_id") }, inverseJoinColumns = {
			@JoinColumn(name = "etiqueta_id") })
	protected List<Etiqueta> etiquetas = new ArrayList<Etiqueta>();
	@Column
	@Type(type = "org.hibernate.type.ZonedDateTimeType")
	protected ZonedDateTime fechaBaja = null;
	protected boolean esLocal = true;

	@ManyToMany(mappedBy = "pois")
	private Set<RegistroHistorico> registrosHistoricos = new HashSet<RegistroHistorico>();

	public boolean estaXMetrosDePOI(double x, POI unPOI) {
		return (distanciaCoordDosPOIs(this, unPOI) * 1000 < x);
	}

	public static double distanciaEntreDosPuntos(double lat1, double lng1, double lat2, double lng2) {
		// double radioTierra = 3958.75;//en millas
		double radioTierra = 6371;// en kil�metros
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double va1 = Math.pow(sindLat, 2)
				+ Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
		double distancia = radioTierra * va2;

		return distancia;
	}

	public static double distanciaCoordDosPOIs(POI unPOI, POI segundoPOI) {
		double lat1 = segundoPOI.ubicacion.getLatitudeInDegrees();
		double lng1 = segundoPOI.ubicacion.getLongitudeInDegrees();
		double lat2 = unPOI.ubicacion.getLatitudeInDegrees();
		double lng2 = unPOI.ubicacion.getLongitudeInDegrees();
		return distanciaEntreDosPuntos(lat1, lng1, lat2, lng2);
	}

	// Se le pregunta a un POI si es cercano.
	public boolean esCercano(POI poi) {
		double distancia = this.ubicacion.distanceTo(poi.ubicacion);
		long tcercania = this.getCercania();
		int retval = Double.compare(distancia, tcercania);
		if (retval > 0)
			return false;
		else
			return true;
	}

	public String getDireccion() {

		String resultado = "";
		if (this.callePrincipal != null && !this.callePrincipal.isEmpty())
			resultado = resultado + callePrincipal + " ";
		if (this.numeracion > 0)
			resultado = resultado + numeracion + " ";
		if (this.piso > 0)
			resultado = resultado + piso + "°" + " ";
		if (this.departamento != null && !departamento.isEmpty())
			resultado = resultado + departamento + " ";
		if (this.unidad != null && !unidad.isEmpty())
			resultado = resultado + unidad + " ";
		if (this.barrio != null && !barrio.isEmpty())
			resultado = resultado + barrio;
		return resultado;
	}

	public String getCallePrincipal() {
		return callePrincipal;
	}

	public void setCallePrincipal(String callePrincipal) {
		this.callePrincipal = callePrincipal;
	}

	public String getCalleLateral() {
		return calleLateral;
	}

	public void setCalleLateral(String calleLateral) {
		this.calleLateral = calleLateral;
	}

	public long getNumeracion() {
		return numeracion;
	}

	public void setNumeracion(long numeracion) {
		this.numeracion = numeracion;
	}

	public long getPiso() {
		return piso;
	}

	public void setPiso(long piso) {
		this.piso = piso;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public long getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(long codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public double getLatitud() {
		return ubicacion.getLatitudeInDegrees();
	}

	public void setLatitud(double latitud) {
		this.ubicacion.setDegLat(latitud);
	}

	public double getLongitud() {
		return ubicacion.getLongitudeInDegrees();
	}

	public void setLongitud(double longitud) {
		this.ubicacion.setDegLon(longitud);
	}

	public long getComuna() {
		return comuna;
	}

	public void setComuna(long comuna) {
		this.comuna = comuna;
	}

	public long getCercania() {
		return cercania;
	}

	public void setCecania(long valor) {
		this.cercania = valor;
	}

	public POI getPOI() {
		return this;
	}

	public GeoLocation getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(GeoLocation ubic) {
		ubicacion = ubic;
	}

	@Enumerated(EnumType.ORDINAL)
	public TiposPOI getTipo() {
		return tipo;
	}

	public void setTipo(TiposPOI tipo) {
		this.tipo = tipo;
	}

	public void setCercania(int cercania) {
		this.cercania = cercania;
	}

	public void setEtiquetas(String nombres[]) {
		this.etiquetas.clear();
		if (nombres != null)
			for (int i = 0; i < nombres.length; i++) {
				this.etiquetas.add(FlyweightFactoryEtiqueta.getInstance().getEtiqueta(nombres[i]));
			}
	}

	public List<Etiqueta> getEtiquetasList() {
		return this.etiquetas;
	}

	public void setEtiquetasList(ArrayList<Etiqueta> et) {
		this.etiquetas = et;
	}

	public String[] getEtiquetas() {
		String[] nombres = new String[etiquetas.size()];
		for (int i = 0; i < etiquetas.size(); i++) {
			nombres[i] = etiquetas.get(i).getNombre();
		}
		return nombres;
	}

	public String getEtiqueta(int num) {

		return etiquetas.get(num).getNombre();
	}

	public Boolean buscarEtiqueta(String etiquetaNombre) {
		if (etiquetas != null)
			for (Etiqueta e : etiquetas) {
				if (compararAtributo(etiquetaNombre, e.getNombre())) {
					return true;
				}
			}

		return false;
	}

	public void refreshEtiquetas() {
		String[] etViejas = getEtiquetas();
		this.setEtiquetas(etViejas);
	}

	public DateTime getFechaBaja() {
		DateTime tm = MetodosComunes.convertJavatoJoda(fechaBaja);
		return tm;
	}

	public void setFechaBaja(DateTime fb) {
		ZonedDateTime tm = MetodosComunes.convertJodatoJava(fb);
		this.fechaBaja = tm;
	}

	public boolean determinarCercaniaPOI(GeoLocation ubicacion) {
		double lat1 = this.ubicacion.getLatitudeInDegrees();
		double lng1 = this.ubicacion.getLongitudeInDegrees();
		double lat2 = ubicacion.getLatitudeInDegrees();
		double lng2 = ubicacion.getLongitudeInDegrees();
		double distancia = distanciaEntreDosPuntos(lat1, lng1, lat2, lng2);

		return this.cercania > distancia;
	}

	public void setDatos(POI_DTO dto) {
		this.setCallePrincipal(dto.getCallePrincipal());
		this.setCalleLateral(dto.getCalleLateral());
		this.setNumeracion(dto.getNumeracion());
		this.setPiso(dto.getPiso());
		this.setDepartamento(dto.getDepartamento());
		this.setUnidad(dto.getUnidad());
		this.setCodigoPostal(dto.getCodigoPostal());
		this.setLocalidad(dto.getLocalidad());
		this.setBarrio(dto.getBarrio());
		this.setProvincia(dto.getProvincia());
		this.setPais(dto.getPais());
		this.setComuna(dto.getComuna());
	}

	public boolean busquedaEstandar(String filtros[]) {

		for (String filtro : filtros) {
			if (MetodosComunes.isNumeric(filtro)) {
				long valor = Long.parseLong(filtro);
				if (numeracion == valor)
					return true;
				else if (piso == valor)
					return true;
				else if (codigoPostal == valor)
					return true;
				else if (comuna == valor)
					return true;
			}
			if (this.nombre != null && compararAtributo(filtro, this.nombre))
				return true;
			else if (this.callePrincipal != null && compararAtributo(filtro, this.callePrincipal))
				return true;
			else if (this.calleLateral != null && compararAtributo(filtro, this.calleLateral))
				return true;
			else if (this.departamento != null && compararAtributo(filtro, this.departamento))
				return true;
			else if (this.unidad != null && compararAtributo(filtro, this.unidad))
				return true;
			else if (this.localidad != null && compararAtributo(filtro, this.localidad))
				return true;
			else if (this.barrio != null && compararAtributo(filtro, this.barrio))
				return true;
			else if (this.provincia != null && compararAtributo(filtro, this.provincia))
				return true;
			else if (this.pais != null && compararAtributo(filtro, this.pais))
				return true;
			else if (this.tipo != null && compararAtributo(filtro, this.tipo.name()))
				return true;
			else if (buscarEtiqueta(filtro))
				return true;
		}
		return false;
	}

	public boolean compararAtributo(String str, String atributo) {

		String palabrasAtributo[] = atributo.split("\\s+");
		for (String palabra : palabrasAtributo) {
			if (LevDist.calcularDistancia(str, palabra))
				return true;
		}
		return false;
	}

	public boolean buscarServicios(String filtro) {
		if (servicios != null) {
			for (NodoServicio servicio : servicios) {
				if (servicio.getNombre() != null && compararAtributo(filtro, servicio.getNombre())) {
					return true;
				} else if (MetodosComunes.isNumeric(filtro)) {
					long filtroNumerico = Long.parseLong(filtro);
					if (servicio.horaInicio < filtroNumerico && filtroNumerico < servicio.horaFin) {
						return true;
					} else if (servicio.getListaDias().contains(filtroNumerico)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean compararPOI(POI poi) {
		POI other = (POI) poi;
		if (calleLateral == null) {
			if (other.calleLateral != null)
				return false;
		} else if (!calleLateral.equals(other.calleLateral))
			return false;
		if (callePrincipal == null) {
			if (other.callePrincipal != null)
				return false;
		} else if (!callePrincipal.equals(other.callePrincipal))
			return false;
		if (codigoPostal != other.codigoPostal)
			return false;
		if (comuna != other.comuna)
			return false;
		if (departamento == null) {
			if (other.departamento != null)
				return false;
		} else if (!departamento.equals(other.departamento))
			return false;
		if (localidad == null) {
			if (other.localidad != null)
				return false;
		} else if (!localidad.equals(other.localidad))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (numeracion != other.numeracion)
			return false;
		if (pais == null) {
			if (other.pais != null)
				return false;
		} else if (!pais.equals(other.pais))
			return false;
		if (piso != other.piso)
			return false;
		if (provincia == null) {
			if (other.provincia != null)
				return false;
		} else if (!provincia.equals(other.provincia))
			return false;
		if (tipo != other.tipo)
			return false;
		if (barrio == null) {
			if (other.barrio != null)
				return false;
		} else if (!barrio.equals(other.barrio))
			return false;
		if (unidad == null) {
			if (other.unidad != null)
				return false;
		} else if (!unidad.equals(other.unidad))
			return false;
		if (!compararEtiquetas(other)) {
			return false;
		}
		if (!compararServicios(other)) {
			return false;
		}

		return true;
	}

	public boolean compararEtiquetas(POI poi) {
		if (this.etiquetas == null && poi.etiquetas == null) {
			return true;
		} else if (this.etiquetas != null && poi.etiquetas == null) {
			return false;
		} else if (this.etiquetas == null && poi.etiquetas != null) {
			return false;
		} else {
			if (this.etiquetas.size() == poi.getEtiquetas().length) {
				for (Etiqueta etiqueta : this.etiquetas) {
					if (!poi.buscarEtiqueta(etiqueta.getNombre()))
						return false;
				}
				return true;
			}
			return false;
		}

	}

	public boolean compararServicios(POI poi) {
		if (this.servicios == null && poi.servicios == null) {
			return true;
		} else if (this.servicios != null && poi.servicios == null) {
			return false;
		} else if (this.servicios == null && poi.servicios != null) {
			return false;
		} else if (this.servicios.size() != poi.servicios.size()) {
			return false;
		} else {
			for (int i = 0; i < this.servicios.size(); i++) {
				NodoServicio nodoThisPoi = this.servicios.get(i);
				NodoServicio nodoOtherPoi = poi.servicios.get(i);

				if (!nodoThisPoi.getName().equals(nodoOtherPoi.getName())) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean darDeBaja(DateTime fecha) {
		// Si retorna false significa que ya estaba dado de baja
		if (fechaBaja != null)
			return false;
		fechaBaja = MetodosComunes.convertJodatoJava(fecha);
		return true;
	}

	public void darAlta() {
		this.fechaBaja = null;
	}

	public boolean dadoDeBaja() {
		return (this.fechaBaja != null);
	}

	public boolean dadoDeBaja(DateTime fecha) {
		if (this.fechaBaja != null && fecha != null) {
			DateTime fb = MetodosComunes.convertJavatoJoda(fechaBaja);
			return fb.withTimeAtStartOfDay().equals(fecha.withTimeAtStartOfDay());
		} else
			return false;
	}

	public boolean isEsLocal() {
		return esLocal;
	}

	public void setEsLocal(boolean esLocal) {
		this.esLocal = esLocal;
	}

	public List<NodoServicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<NodoServicio> servicios) {
		this.servicios = servicios;
	}

	public POI() {

	}

}
