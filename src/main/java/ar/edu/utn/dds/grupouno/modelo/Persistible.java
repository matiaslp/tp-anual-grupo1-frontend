package ar.edu.utn.dds.grupouno.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public class Persistible implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;
	protected String nombre;

	protected Persistible() {
	}

	@Column(name = "id")
	public long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "nombre")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String toString() {
		return getId() + "-" + getNombre();
	}
}