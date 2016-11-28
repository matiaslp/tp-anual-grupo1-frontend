package ar.edu.utn.dds.grupouno.autentification.funciones;

import java.util.HashSet;

import javax.persistence.Entity;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import ar.edu.utn.dds.grupouno.autentification.Accion;
import ar.edu.utn.dds.grupouno.autentification.Rol;
import ar.edu.utn.dds.grupouno.autentification.Usuario;
import ar.edu.utn.dds.grupouno.procesos.ActualizacionLocalesComerciales;
import ar.edu.utn.dds.grupouno.procesos.ResultadoProceso;
import ar.edu.utn.dds.grupouno.quartz.ProcesoListener;

@SuppressWarnings("serial")
@Entity
public class FuncActualizacionLocalesComerciales extends Accion {

	public FuncActualizacionLocalesComerciales(Rol rol) {
		Roles = new HashSet<Rol>();
		// Agregar Roles para esta funcionalidad
		Roles.add(rol);
		nombre = "actualizacionLocalesComerciales";
		isProcess = true;
	}

	public FuncActualizacionLocalesComerciales() {

	}

	public void actualizarLocales(Usuario user, String Token, int cantidadReintentos, boolean enviarEmail,
			String filePath) {
		if (validarsesion(user, Token)) {
			try {
				ResultadoProceso resultadoProceso = new ResultadoProceso();
				Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
				scheduler.getContext().put("ResultadoProceso", resultadoProceso);
				scheduler.getContext().put("Usuario", user);
				
				scheduler.start();
				
				JobKey key = new JobKey(ActualizacionLocalesComerciales.class.getSimpleName());
				
				// Crea una instancia del proceso y con la opcion requestRecovery(true) se fuerzan reintentos en caso de fallas
				JobDetail job = JobBuilder.newJob(ActualizacionLocalesComerciales.class).withIdentity(key).requestRecovery(true).build();
				
				// Cargo en el jobDataMap el path del archivo que uso de referencia.
				job.getJobDataMap().put("filePath", filePath);
				job.getJobDataMap().put("enviarMail", enviarEmail);
				job.getJobDataMap().put("reintentosMax", cantidadReintentos);
				job.getJobDataMap().put("reintentosCont", 0);
				
				Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger").startNow().build();
						
				// Creo instancia del jobListener y se lo agrego al scheduler
				ActualizacionLocalesComerciales procesoInicial = new ActualizacionLocalesComerciales();
				ProcesoListener procesoInicialListener = procesoInicial.getProcesoListener();
				scheduler.getListenerManager().addJobListener((JobListener)procesoInicialListener, KeyMatcher.keyEquals(key));
				
				StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
				
				// Para darle tiempo al planificador que se puedea inicializar y ejecutar los procesos
				Thread.sleep(1000);
				
				scheduler.shutdown();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException 
					| SchedulerException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
//
//	// creacion Proceso para agregar a la lista en Proceso Multiple
//	public Proceso prepAgregarAcciones(Usuario user, String Token, int cantidadReintentos, boolean enviarEmail,
//			String filePath) {
//		if (validarsesion(user, Token)) {
//			return new ActualizacionLocalesComerciales(cantidadReintentos, enviarEmail, filePath, user);
//		} else
//			return null;
//	}

}
