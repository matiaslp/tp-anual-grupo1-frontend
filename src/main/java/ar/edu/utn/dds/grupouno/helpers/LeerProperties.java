package ar.edu.utn.dds.grupouno.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LeerProperties {

	public Properties prop;
	private static LeerProperties instance = null;

	public LeerProperties() {
		prop = new Properties();

		try (InputStream input = new FileInputStream("config.properties")) {

			// load a properties file
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static LeerProperties getInstance() {
		if (instance == null)
			instance = new LeerProperties();
		return instance;
	}
}