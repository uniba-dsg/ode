package org.apache.ode.tomcat;

import java.nio.file.Path;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.ode.axis2.hooks.ODEAxisServlet;

public class TomcatAxisServletStarter {

	private final Path tomcatBase;

	/**
	 * Starting Tomcat with AXIS2 Class
	 * 
	 * @param tomcatBase
	 *            location of folder with WEB-INF in it. Normally location of
	 *            the jar / execution directory.
	 */
	public TomcatAxisServletStarter(Path tomcatBase) {
		this.tomcatBase = tomcatBase;
	}

	/**
	 * Starts Tomcat with ODE and blocks
	 * 
	 * @param port
	 *            where to listen on
	 * @throws LifecycleException
	 */
	public void startTomcat(int port) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);

		Context rootCtx = tomcat.addContext("/ode", tomcatBase.toAbsolutePath()
				.toString());

		ODEAxisServlet odeServlet = new ODEAxisServlet();
		Wrapper odeWrapper = Tomcat.addServlet(rootCtx, "AxisServlet",
				odeServlet);

		odeWrapper.setLoadOnStartup(1);

		generateServletMapping(rootCtx);

		tomcat.start();
		tomcat.getServer().await();
	}

	private void generateServletMapping(Context rootCtx) {

		rootCtx.addServletMapping("/servlet/AxisServlet", "AxisServlet");

		rootCtx.addServletMapping("*.jws", "AxisServlet");
		rootCtx.addServletMapping("/services/*", "AxisServlet");
		rootCtx.addServletMapping("/processes/*", "AxisServlet");
		rootCtx.addServletMapping("/deployment/*", "AxisServlet");

	}

}
