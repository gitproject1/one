package example;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {

	public static final String DEFAULT_FILENAME = "eclipselink.properties";

	public static void loadProperties(Map<Object, Object> properties) {
		loadProperties(properties, DEFAULT_FILENAME);
	}

	public static void loadProperties(Map<Object, Object> properties, String filename) {

		loadPropertiesStream(properties, filename);

		for (Object key : properties.keySet()) {
			String keyName = (String) key;
			String value = (String) properties.get(keyName);
			properties.put(keyName, value);
		}
	}

	public static void loadPropertiesStream(Map<Object, Object> properties, String filename) {

		try {
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

			Properties props = new Properties();
			props.load(stream);
			properties.putAll(props);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
