package com.github.rreinert.project1.util;

import java.util.HashMap;
import java.util.Map;

public class PropertiesLoader {

	public static Map<Object, Object> loadProperties() {
		
		Map<Object, Object> properties = new HashMap<Object, Object>();
		
		for (Object key : System.getProperties().keySet()) {
			String keyName = (String) key;

			if (keyName.startsWith("javax.persistence") || keyName.startsWith("eclipselink")) {
				String value = System.getProperty(keyName);
				properties.put(keyName, value);
			}
		}
		
		return properties;
	}

}
