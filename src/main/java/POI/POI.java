package POI;

public class POI {

	String callePrincipal;
	String calleLateral ;
	int numeracion;
	int piso;
	String departamento;
	String unidad;
	int codigoPostal;
	String localidad;
	String barrio;
	String provincia;
	String pais;
	Float latitud;
	Float longitud;
	int cercania; //define cuando otro punto es cercano.
	String tipo; //este atributo hay que ver si nos sirve porque
				//las subclases tienen el nombre del tipo, de por si.
	String comuna;
	

	

	
	public boolean estaXMetrosDePOI(double x, POI unPOI){
		double lat1 = this.latitud;
		double lng1 = this.longitud;
		double lat2 = unPOI.latitud;
		double lng2 = unPOI.longitud;
		
		return (distanciaCoord(lat1,lng1,lat2,lng2)*1000 < x);
	}
	
	public static double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {  
        //double radioTierra = 3958.75;//en millas  
        double radioTierra = 6371;//en kil�metros  
        double dLat = Math.toRadians(lat2 - lat1);  
        double dLng = Math.toRadians(lng2 - lng1);  
        double sindLat = Math.sin(dLat / 2);  
        double sindLng = Math.sin(dLng / 2);  
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)  
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));  
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));  
        double distancia = radioTierra * va2;  
   
        return distancia;  
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

	public int getNumeracion() {
		return numeracion;
	}

	public void setNumeracion(int numeracion) {
		this.numeracion = numeracion;
	}

	public int getPiso() {
		return piso;
	}

	public void setPiso(int piso) {
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

	public int getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(int codigoPostal) {
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
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public TipoPOI getTipo() {
		return tipo;
	}

	public void setTipo(TipoPOI tipo) {
		this.tipo = tipo;
	}

	public String getComuna() {
		return comuna;
	}

	public void setComuna(String comuna) {
		this.comuna = comuna;
	}
	
<<<<<<< HEAD
	public int getCercania(){
		return cercania;
	}
	
	public void setCecania(int valor){
		this.cercania=valor;
=======
	public POI getPOI() {
		return this;
>>>>>>> 1fec5651c6f9b2159abb58dd961515d85bb0b822
	}
}
