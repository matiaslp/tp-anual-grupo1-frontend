package ar.edu.utn.dds.grupouno.repositorio;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import ar.edu.utn.dds.grupouno.procesos.ResultadoProceso;

public class ResultadosProcesos {
	protected EntityManager em;

	ResultadosProcesos(EntityManager emanager) {
		this.em = emanager;
	}

	public ResultadoProceso resultadoProcesoPorId(Long id) {
		return em.find(ResultadoProceso.class, id);
	}

	public void persistir(ResultadoProceso resProc) {
		em.getTransaction().begin();
		em.persist(resProc);
		em.getTransaction().commit();
	}
	
	public ArrayList<ResultadoProceso> getListado() {
		ArrayList<ResultadoProceso> listadoResultadoProceso = new ArrayList<ResultadoProceso>();

		listadoResultadoProceso = (ArrayList<ResultadoProceso>) em.createNamedQuery("ResultadoProceso.findAll").getResultList();

		return listadoResultadoProceso;
	}
}
