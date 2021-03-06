package ar.edu.utn.dds.grupouno.procesos;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.KeyMatcher;

import ar.edu.utn.dds.grupouno.autentification.Usuario;
import ar.edu.utn.dds.grupouno.email.EnviarEmail;
import ar.edu.utn.dds.grupouno.helpers.MetodosComunes;
import ar.edu.utn.dds.grupouno.repositorio.Repositorio;

@SuppressWarnings("rawtypes")
public abstract class ProcesoListener implements JobListener {
	
	public String getName() {
		return getClass().getName();
	}
	
	// Las subclases concretas que hereden de esta clase abstracta deben implementar este m�todo
	protected abstract void rollback(Usuario usuario);
	
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// Cargo la fecha de inicio del Job.
		try {
			ResultadoProceso resultado = ((ResultadoProceso) context.getScheduler().getContext().get("ResultadoProceso"));
			resultado.setInicioEjecucion(MetodosComunes.convertJodatoJava(new DateTime()));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void jobExecutionVetoed(JobExecutionContext arg0) {
		//	TODO Auto-generated method stub
	}
	
	// M�todo invocado por Quartz luego de ejecutar el Job
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		Scheduler scheduler = null;
		SchedulerContext contextoScheduler = null;
		ResultadoProceso resultado = null;
		
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		
		try {
			scheduler = context.getScheduler();
			contextoScheduler = scheduler.getContext();
			resultado = ((ResultadoProceso) contextoScheduler.get("ResultadoProceso"));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		// Cargo la fecha de fin de ejecuci�n del job
		resultado.setFinEjecucion(MetodosComunes.convertJodatoJava(new DateTime()));
		resultado.setMensajeError(resultado.getMensajeError()+ ". Reintentado " +
				((Integer)contextoScheduler.getInt("reintentosCont")).toString() + " veces");
				
		//Persiste resultado en la BD
		Repositorio.getInstance().resultadosProcesos().persistir(resultado);
		
		// Se valida si hubo una excepci�n
		if (jobException != null || resultado.getResultado() == Resultado.ERROR) {
			Usuario usuario = (Usuario) contextoScheduler.get("Usuario");
			
			if(dataMap.getBoolean("enviarMail")){
				List<ResultadoProceso> resultados = new ArrayList<ResultadoProceso>();
				resultados.add(resultado);
				EnviarEmail.mandarCorreoProcesoError(usuario,resultados);
			}
			
			// Mientras que la cant de reintentos no alcance la cantidad seteada por el usuario
			// el job se seguira re disparando.
			int reintento = contextoScheduler.getInt("reintentosCont");
			if(contextoScheduler.getInt("reintentosMax") > reintento){
				reintento++;
				contextoScheduler.put("reintentosCont", reintento);
				try {
					reEjecutarProceso(context);
				} catch (SchedulerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				rollback(usuario);
				contextoScheduler.replace("ejecutado", true);
			}
		} else {
			try {
				Usuario usuario = (Usuario) context.getScheduler().getContext().get("Usuario");
				List<ResultadoProceso> resultados = new ArrayList<ResultadoProceso>();
				resultados.add(resultado);
				EnviarEmail.mandarCorreoProcesoError(usuario,resultados);

				
				ejecutarProcesoAnidado(context);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void reEjecutarProceso(JobExecutionContext context) throws SchedulerException {

		// Este método realiza acciones similares al
		// ejecutaEjemploProcesosAnidadosConRollback() de la clase Consola
		Scheduler scheduler = context.getScheduler();
		
		String nombreProcesoActual = getClass().getName().replace("Listener", "");

		try {
		// Obtiene la clase del proceso actual
		Class actualProceso = getClass().getClassLoader().loadClass(nombreProcesoActual);
		// Obtiene el listener del pr�ximo proceso
		ProcesoListener siguienteListener = ((Proceso) actualProceso.newInstance()).getProcesoListener();

		// Define un identificador
		JobKey jobKey = new JobKey(actualProceso.getSimpleName() + ((Integer)scheduler.getContext().getInt("reintentosCont")).toString());

		// Se crea una instancia del próximo proceso a ejecutar
		JobDetail nextJob = JobBuilder.newJob(actualProceso).withIdentity(jobKey).requestRecovery(true).build();

		// Se crea un nuevo trigger que ejecutará el nuevo proceso
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobKey + "trigger").startNow().build();

		// Se agrega el listener del nuevo proceso al planificador
		scheduler.getListenerManager().addJobListener((JobListener) siguienteListener, KeyMatcher.keyEquals(jobKey));

		// Se suma al planificador el nuevo proceso junto con el trigger
		scheduler.scheduleJob(nextJob, trigger);


		} catch (ClassNotFoundException cnf) {
		System.out.println(cnf.getMessage());
		} catch (InstantiationException ie) {
		System.out.println(ie.getMessage());
		} catch (IllegalAccessException iae) {
		System.out.println(iae.getMessage());
		}

		}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	public void ejecutarProcesoAnidado(JobExecutionContext context) throws SchedulerException {

		// Este m�todo realiza acciones similares al
		// ejecutaEjemploProcesosAnidadosConRollback() de la clase Consola
		Scheduler scheduler = context.getScheduler();

		String nombreProcesoActual = getClass().getName().replace("Listener", "");
		
		try {
			
			List<NodoProceso> lstProc = (List<NodoProceso>)scheduler.getContext().get("lstProc");
			if (lstProc != null && lstProc.size() > 0){
				NodoProceso nodo = lstProc.remove(0);
			
			//Obtiene la clase del siguiente proceso encadenado
			Class siguienteProceso = nodo.getSiguienteProceso();

			// Chequea si hay definido un siguiente proceso
				ResultadoProceso resultadoProceso = new ResultadoProceso();
				scheduler.getContext().put("ResultadoProceso", resultadoProceso);
				scheduler.getContext().put("ejecutado", false);
				scheduler.getContext().put("reintentosMax",  nodo.getSiguienteProcesoReintentos());
				scheduler.getContext().put("reintentosCont", 0);
				
				// Obtiene el listener del pr�ximo proceso
				ProcesoListener siguienteListener = ((Proceso) siguienteProceso.newInstance()).getProcesoListener();

				// Define un identificador
				JobKey jobKey = new JobKey(siguienteProceso.getSimpleName());

				// Se crea una instancia del pr�ximo proceso a ejecutar
				JobDetail nextJob = JobBuilder.newJob(siguienteProceso).withIdentity(jobKey).requestRecovery(true).build();
				
				// Cargo en el jobDataMap el path del archivo que uso de referencia.
				nextJob.getJobDataMap().put("filePath", nodo.getSiguienteProcesoFilepath());
				nextJob.getJobDataMap().put("enviarMail",  nodo.isSiguienteProcesoEnviarEmail());

				// Se crea un nuevo trigger que ejecutar� el nuevo proceso
				Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobKey + "trigger").startNow().build();

				// Se agrega el listener del nuevo proceso al planificador
				scheduler.getListenerManager().addJobListener((JobListener) siguienteListener, KeyMatcher.keyEquals(jobKey));

				// Se suma al planificador el nuevo proceso junto con el trigger
				scheduler.scheduleJob(nextJob, trigger);
			} else {
				scheduler.getContext().replace("ejecutado", true);
				

			}

		} catch (ClassNotFoundException cnf) {
			System.out.println(cnf.getMessage());
		} catch (InstantiationException ie) {
			System.out.println(ie.getMessage());
		} catch (IllegalAccessException iae) {
			System.out.println(iae.getMessage());
		}

	}	
}
