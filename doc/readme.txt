metaweb step by step:

1 - copy spring-instrument-tomcat-3.0.0.RELEASE.jar to tomcat lib folder;
2 - modify tomcat server.xml file, adding the following lines:

	<Context docBase="project1" path="/project1" reloadable="true" source="org.eclipse.jst.jee.server:project1">
	    <Loader loaderClass="org.springframework.instrument.classloading.tomcat.TomcatInstrumentableClassLoader"/>
	</Context>
