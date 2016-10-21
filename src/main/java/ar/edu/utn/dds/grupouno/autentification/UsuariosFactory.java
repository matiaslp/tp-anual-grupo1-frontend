package ar.edu.utn.dds.grupouno.autentification;

import java.util.HashMap;
import java.util.Map;

import ar.edu.utn.dds.grupouno.db.DB_Usuarios;

public class UsuariosFactory {
	
	public UsuariosFactory(){
		
	}
	
	public Usuario crearUsuario(String username, String password, Rol rol){
		Usuario nuevoUsuario = null;
		if(DB_Usuarios.getInstance().getUsuarioByName(username) != null){
			return null;
		}else{
			nuevoUsuario = new Usuario();
			int size = DB_Usuarios.getInstance().getListaUsuarios().size();
			if (size > 0)
				nuevoUsuario.setID(DB_Usuarios.getInstance().getListaUsuarios().get(size-1).getID()+1);
			else
				nuevoUsuario.setID(1);
			nuevoUsuario.setPassword(password);
			nuevoUsuario.setUsername(username);
			nuevoUsuario.setRol(rol);
			nuevoUsuario.setFuncionalidades(new HashMap<String,Accion>());
			nuevoUsuario.setMailHabilitado(true);
			nuevoUsuario.setNotificacionesActivadas(true);
			nuevoUsuario.setAuditoriaActivada(true);
			
			Map<String, Accion> funcionalidades = new HashMap <String, Accion>();
			AuthAPI.getInstance();
			if(rol.equals(Rol.ADMIN)){
				funcionalidades .put("bajaPOIs", AuthAPI.getInstance().getAcciones().get("bajaPOIs"));
				funcionalidades.put("actualizacionLocalesComerciales", AuthAPI.getInstance().getAcciones().get("actualizacionLocalesComerciales"));
				funcionalidades.put("procesoMultiple", AuthAPI.getInstance().getAcciones().get("procesoMultiple"));
				funcionalidades.put("busquedaPOI", AuthAPI.getInstance().getAcciones().get("busquedaPOI"));
				funcionalidades.put("obtenerInfoPOI", AuthAPI.getInstance().getAcciones().get("obtenerInfoPOI"));
				funcionalidades.put("cambiarEstadoMail", AuthAPI.getInstance().getAcciones().get("cambiarEstadoMail"));
			}else{
				funcionalidades.put("busquedaPOI", AuthAPI.getInstance().getAcciones().get("busquedaPOI"));
				funcionalidades.put("obtenerInfoPOI", AuthAPI.getInstance().getAcciones().get("obtenerInfoPOI"));
				funcionalidades.put("notificacionBusquedaLarga", AuthAPI.getInstance().getAcciones().get("notificarBusquedaLarga"));
				funcionalidades.put("auditoria", AuthAPI.getInstance().getAcciones().get("auditoria"));
			}
			
			DB_Usuarios.getInstance().agregarUsuarioALista(nuevoUsuario);
			return nuevoUsuario;
		}
	}

}
